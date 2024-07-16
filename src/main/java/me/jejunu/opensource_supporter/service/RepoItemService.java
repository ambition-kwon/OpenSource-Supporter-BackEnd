package me.jejunu.opensource_supporter.service;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import me.jejunu.opensource_supporter.config.GithubApiFeignClient;
import me.jejunu.opensource_supporter.config.GithubStatsFeignClient;
import me.jejunu.opensource_supporter.config.OpenAIFeignClient;
import me.jejunu.opensource_supporter.domain.RepoItem;
import me.jejunu.opensource_supporter.domain.User;
import me.jejunu.opensource_supporter.dto.*;
import me.jejunu.opensource_supporter.repository.RepoItemRepository;
import me.jejunu.opensource_supporter.repository.SupportedPointRepository;
import me.jejunu.opensource_supporter.repository.UserRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.nio.charset.StandardCharsets;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class RepoItemService {
    private final GithubApiFeignClient githubApiFeignClient;
    private final GithubApiService githubApiService;
    private final RepoItemRepository repoItemRepository;
    private final UserRepository userRepository;
    private final SupportedPointRepository supportedPointRepository;
    private final GithubStatsFeignClient githubStatsFeignClient;
    private final OpenAIFeignClient openAIFeignClient;
    private final ScheduledExecutorService scheduledExecutorService;

    private final ConcurrentHashMap<Long, String> readmeCache = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, List<Integer>> weeklyCommitCache = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, String> chatGptCache = new ConcurrentHashMap<>();


    @Transactional(readOnly = true)
    public List<RepoItemModalResponseDto> readMultipleRepoItems(String authHeader){
        String userToken = authHeader.replace("Bearer ", "");
        JSONObject userDataResponse = githubApiService.getUserFromGithub(userToken);
        String userName = userDataResponse.getString("login");
        Object response = githubApiFeignClient.getUserRepoItem(authHeader, userName, "updated", 100);
        List<RepoItemModalResponseDto> result = new ArrayList<>();
        JSONArray jsonArray = new JSONArray(response.toString());

        for(int i = 0; i < jsonArray.length(); i++){
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String repoName = jsonObject.getString("name");
            int forkCount = jsonObject.getInt("forks_count");
            int starCount = jsonObject.getInt("stargazers_count");
            LocalDateTime lastCommitAt = LocalDateTime.parse(jsonObject.getString("pushed_at"),DateTimeFormatter.ISO_DATE_TIME);

            Optional<RepoItem> repoItem = repoItemRepository.findByRepoNameAndUserName(repoName, userName);
            Long repoId = repoItem.map(RepoItem::getId).orElse(null);
            boolean posted = repoItem.isPresent();
            if(!jsonObject.getBoolean("fork")) {
                result.add(RepoItemModalResponseDto.builder()
                        .repoId(repoId)
                        .repoName(repoName)
                        .forkCount(forkCount)
                        .starCount(starCount)
                        .lastCommitAt(lastCommitAt)
                        .posted(posted)
                        .build());
            }
        }
        return result;
    }

    @Transactional(readOnly = true)
    public PagedRepoItemResponseDto getMyPartners(String authHeader, Pageable pageable) {
        String userToken = authHeader.replace("Bearer ", "");
        JSONObject userDataResponse = githubApiService.getUserFromGithub(userToken);
        String userName = userDataResponse.getString("login");
        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new IllegalArgumentException("not found user"));
        Page<RepoItem> repoItemsPage = supportedPointRepository.findDistinctRepoItemsByUser(user, pageable);
        List<RecommendedRepoCardDto> repoCardDtoList = repoItemsPage.stream()
                .map(this::convertToCardDto)
                .sorted(Comparator.comparing(RecommendedRepoCardDto::getLastCommitAt).reversed())
                .toList();
        return PagedRepoItemResponseDto.builder()
                .hasNextPage(repoItemsPage.hasNext())
                .data(repoCardDtoList)
                .build();
    }

    @Transactional(readOnly = true)
    public RepoItem readSingleRepoItem(Long id){
        return repoItemRepository.findById(id)
                .orElseThrow(()->new IllegalArgumentException("not found RepoItem"));
    }

    @Transactional
    public void deleteRepoItem(String authHeader, RepoItemIdRequestDto request){
        String userToken = authHeader.replace("Bearer ", "");
        JSONObject userDataResponse = githubApiService.getUserFromGithub(userToken);
        String userName = userDataResponse.getString("login");
        RepoItem deleteRepoItem = repoItemRepository.findById(request.getRepoId())
                .orElseThrow(()->new IllegalArgumentException("not found repoItem"));
        if(Objects.equals(userName, deleteRepoItem.getUser().getUserName()) && deleteRepoItem.getSupportedPointList() != null){
            repoItemRepository.deleteById(request.getRepoId());
        }
        else if(!Objects.equals(userName, deleteRepoItem.getUser().getUserName())){
            throw new RuntimeException("The token owner and the repository owner are different");
        }
        else{
            throw new RuntimeException("This Repository has already received sponsorship");
        }
    }

    @Transactional
    public RepoItem updateRepoItem(String authHeader, RepoItemUpdateRequestDto request) {
        String userToken = authHeader.replace("Bearer ", "");
        Long repoId = request.getRepoId();
        RepoItem updateRepoItem = repoItemRepository.findById(repoId)
                .orElseThrow(()->new IllegalArgumentException("not found repoItem"));
        String userName = updateRepoItem.getUser().getUserName();
        String repoName = updateRepoItem.getRepoName();
        //MostLanguage
        JSONObject mostLanguageResponse = new JSONObject(githubApiFeignClient.getMostLanguage(userName, repoName, userToken));
        String mostLanguage = findMostUsedLanguage(mostLanguageResponse);
        //License
        String license = findLicense(userName, repoName, userToken);
        //LastCommitAt
        JSONObject commitResponse = new JSONObject(githubApiFeignClient.getCommitSha(userName, repoName, userToken));
        LocalDateTime lastCommitAt = findLastCommitAt(commitResponse);

        updateRepoItem.update(request.getDescription(), request.getTags(), mostLanguage, license, lastCommitAt);

        return updateRepoItem;
    }

    @Transactional
    public RepoItem createRepoItem(String authHeader, RepoItemCreateRequestDto request) {
        String userToken = authHeader.replace("Bearer ", "");
        String userName = request.getUserName();
        String repoName = request.getRepoName();
        String description = request.getDescription();
        List<String> tags = request.getTags();
        String repositoryLink = "https://github.com/" + userName + "/" + repoName;
        //MostLanguage
        JSONObject mostLanguageResponse = new JSONObject(githubApiFeignClient.getMostLanguage(userName, repoName, userToken));
        String mostLanguage = findMostUsedLanguage(mostLanguageResponse);
        //License
        String license = findLicense(userName, repoName, userToken);
        //LastCommitAt
        JSONObject commitResponse = new JSONObject(githubApiFeignClient.getCommitSha(userName, repoName, userToken));
        LocalDateTime lastCommitAt = findLastCommitAt(commitResponse);
        //userID
        User user = userRepository.findByUserName(userName)
                .orElseThrow(()->new IllegalArgumentException("not found user"));

        RepoItem newRepoItem = RepoItem.builder()
                .user(user)
                .repoName(repoName)
                .description(description)
                .tags(tags)
                .repositoryLink(repositoryLink)
                .mostLanguage(mostLanguage)
                .license(license)
                .lastCommitAt(lastCommitAt)
                .build();

        if(isRepoItemExists(repoName, user)){
            throw new RuntimeException("동일 사용자 중복 레포지토리 등록 에러");
        }

        return repoItemRepository.save(newRepoItem);
    }

    public String findMostUsedLanguage(JSONObject mostLanguageJSON) {
        String mostUsedLanguage = null;
        long maxLines = 0;

        // Iterate through each language and its line count
        Iterator<String> keys = mostLanguageJSON.keys();
        while (keys.hasNext()) {
            String language = keys.next();
            long lines = mostLanguageJSON.getLong(language);

            // Check if this language has more lines than the current max
            if (lines > maxLines) {
                maxLines = lines;
                mostUsedLanguage = language;
            }
        }

        return mostUsedLanguage;
    }
    public String findLicense(String userName, String repoName, String access_token) {
        try {
            JSONObject licenseResponse = new JSONObject(githubApiFeignClient.getRepoLicense(userName, repoName, access_token));

            // "license" 키의 값을 추출합니다.
            JSONObject licenseObject = licenseResponse.getJSONObject("license");
            // "name" 키의 값을 추출하여 license 변수에 저장하고 리턴합니다.
            return licenseObject.getString("name");
        } catch (Exception e) {
            e.printStackTrace();
            return "Unlicensed";
        }
    }

    public LocalDateTime findLastCommitAt(JSONObject commitResponse) {
        LocalDateTime lastCommitAt = null;

        if (commitResponse != null && commitResponse.has("pushed_at")) {
            String pushedAt = commitResponse.getString("pushed_at");

            // ISO_OFFSET_DATE_TIME 포맷의 문자열을 LocalDateTime으로 변환
            ZonedDateTime zonedDateTime = ZonedDateTime.parse(pushedAt, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
            lastCommitAt = zonedDateTime.toLocalDateTime();
        }
        return lastCommitAt;
    }

    private boolean isRepoItemExists(String repoName, User user) {
        Optional<RepoItem> existingRepoItem = repoItemRepository.findByRepoNameAndUser(repoName, user);
        return existingRepoItem.isPresent();
    }

    @Cacheable(cacheNames = "recentlyCommitRepoCache")
    public PagedRepoItemResponseDto updateRecentlyCommitRepo(Pageable pageable) {
        Page<RepoItem> repoItemsPage = repoItemRepository.findAllByOrderByLastCommitAtDesc(pageable);
        List<RecommendedRepoCardDto> repoCardDtoList = repoItemsPage.stream()
                .map(this::convertToCardDto)
                .toList();
        return PagedRepoItemResponseDto.builder()
                .hasNextPage(repoItemsPage.hasNext())
                .data(repoCardDtoList)
                .build();
    }

    @Cacheable(cacheNames = "mostViewedRepoCache")
    public PagedRepoItemResponseDto updateMostViewed(Pageable pageable) {
        Page<RepoItem> repoItemsPage = repoItemRepository.findAllByOrderByViewCountDescLastCommitAtDesc(pageable);
        List<RecommendedRepoCardDto> repoCardDtoList = repoItemsPage.stream()
                .map(this::convertToCardDto)
                .toList();
        return PagedRepoItemResponseDto.builder()
                .hasNextPage(repoItemsPage.hasNext())
                .data(repoCardDtoList)
                .build();
    }

    @Transactional(readOnly = true)
    public List<RecommendedRepoCardDto> readSupportedRepoItems(String userName){
        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new IllegalArgumentException("not found user"));
        return user.getRepoItemList().stream()
                .map(this::convertToCardDto)
                .sorted(Comparator.comparing(RecommendedRepoCardDto::getLastCommitAt).reversed())
                .toList();
    }

    @Transactional(readOnly = true)
    public List<RecommendedRepoCardDto> readSupportingRepoItems(String userName){
        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new IllegalArgumentException("not found user"));
        List<RepoItem> repoItems = supportedPointRepository.findDistinctRepoItemsByUser(user);
        return repoItems.stream()
                .map(this::convertToCardDto)
                .sorted(Comparator.comparing(RecommendedRepoCardDto::getLastCommitAt).reversed())
                .toList();
    }

    @Transactional(readOnly = true)
    public List<RecommendedRepoCardDto> searchRepoItems(String keyword){
        List<RepoItem> repoItems = repoItemRepository.searchByKeyword(keyword);
        return repoItems.stream()
                .map(this::convertToCardDto)
                .sorted(Comparator.comparing(RecommendedRepoCardDto::getLastCommitAt).reversed())
                .toList();
    }

    @Transactional
    public RepoItemDetailResponseDto getDetailRepoItem(String clientId, String clientSecret, Long repoId, String openApiKey) {
        String adminAuth = "Basic " + Base64.getEncoder().encodeToString((clientId + ":" + clientSecret).getBytes());
        RepoItem repoItem = repoItemRepository.findById(repoId)
                .orElseThrow(() -> new IllegalArgumentException("not found repoItem"));

        //readme.md file
        String readmeContent = readmeCache.get(repoItem.getId());
        if (readmeContent == null) {
            try {
                String responseReadme = githubApiFeignClient.getReadme(repoItem.getUser().getUserName(), repoItem.getRepoName(), adminAuth);
                JSONObject jsonObjectReadme = new JSONObject(responseReadme);
                String base64EncodedString = jsonObjectReadme.getString("content");
                base64EncodedString = base64EncodedString.replaceAll("\\s", ""); //공백 제거 정규표현식
                byte[] decodedBytes = Base64.getDecoder().decode(base64EncodedString);
                readmeContent = new String(decodedBytes, StandardCharsets.UTF_8);
                readmeCache.put(repoItem.getId(), readmeContent);
                scheduledExecutorService.schedule(() -> readmeCache.remove(repoItem.getId()), 10, TimeUnit.MINUTES);
            } catch (FeignException e) {
                if (e.status() == 404) {
                    readmeContent = "This repository does not have a README.md file";
                }
            }
        }

        //Weekly commit List
        List<Integer> weeklyCommitList = weeklyCommitCache.get(repoItem.getId());
        if (weeklyCommitList == null) {
            JSONObject responseWeeklyCommit = new JSONObject(githubApiFeignClient.getWeeklyCommitList(repoItem.getUser().getUserName(), repoItem.getRepoName(), adminAuth));
            JSONArray weeklyCommit = responseWeeklyCommit.getJSONArray("all");
            weeklyCommitList = new ArrayList<>();
            for (int i = 0; i < weeklyCommit.length(); i++) {
                weeklyCommitList.add(weeklyCommit.getInt(i));
            }
            weeklyCommitCache.put(repoItem.getId(), weeklyCommitList);
            scheduledExecutorService.schedule(() -> weeklyCommitCache.remove(repoItem.getId()), 10, TimeUnit.MINUTES);
        }
        //GitHub stats
        String userAnalysis = githubStatsFeignClient.getUserAnalysis(repoItem.getUser().getUserName());
        // Rank
        Pattern rankPattern = Pattern.compile("Rank: ([A-Za-z0-9+-]+)");
        Matcher rankMatcher = rankPattern.matcher(userAnalysis);
        String rank = rankMatcher.find() ? rankMatcher.group(1) : "Not Found";

        // Total Stars
        Pattern starsPattern = Pattern.compile("Total Stars Earned: (\\d+),");
        Matcher starsMatcher = starsPattern.matcher(userAnalysis);
        int totalStars = starsMatcher.find() ? Integer.parseInt(starsMatcher.group(1)) : 0;

        // Total Commits
        Pattern commitsPattern = Pattern.compile("Total Commits in \\d{4} : (\\d+),");
        Matcher commitsMatcher = commitsPattern.matcher(userAnalysis);
        int totalCommits = commitsMatcher.find() ? Integer.parseInt(commitsMatcher.group(1)) : 0;

        // Total PullRequests
        Pattern prPattern = Pattern.compile("Total PRs: (\\d+),");
        Matcher prMatcher = prPattern.matcher(userAnalysis);
        int totalPullRequests = prMatcher.find() ? Integer.parseInt(prMatcher.group(1)) : 0;

        // Total Issues
        Pattern issuesPattern = Pattern.compile("Total Issues: (\\d+),");
        Matcher issuesMatcher = issuesPattern.matcher(userAnalysis);
        int totalIssues = issuesMatcher.find() ? Integer.parseInt(issuesMatcher.group(1)) : 0;

        // Contributed to
        Pattern contributionsPattern = Pattern.compile("Contributed to \\(last year\\): (\\d+)</desc>");
        Matcher contributionsMatcher = contributionsPattern.matcher(userAnalysis);
        int totalContributions = contributionsMatcher.find() ? Integer.parseInt(contributionsMatcher.group(1)) : 0;
        //chatgpt analysis
        String chatgptAnalysis = chatGptCache.get(repoItem.getId());
        if (chatgptAnalysis == null) {
            List<AIRequestDto.ChatMessageDto> requestMessages = new ArrayList<>();
            String prompt = "내가 Github Repository에 관한 각종 정보를 주면 너는 미사어구 및 필요없는 말을 하지 말고, 이 레포지토리 및 레포지토리 소유자에 대해 투자할 가치가 있는지에 대한 분석 정보만을 명확하게 내게 제공해줘. 최대한 내가 준 내용을 재언급하지 않으면서 너의 생각 및 분석을 위주로 해줘. 답변 언어는 영어로 부탁해. 먼저 소유자 관련 정보인데 total Starts 는" + totalStars + "이고, total Commits는 " + totalCommits + ", total Pull Requests는 " + totalPullRequests + ", Total Issues는 " + totalIssues + ", Total Contributed to는 " + totalContributions + "이야. 내가 지금 준 total에 관련된 정보들은 현 유저가 가진 모든 레포지토리에서 일어난 활동들을 종합한 값이라는 것을 인지해줘. 다음은 현 레포지토리 관련 정보야. 전체가 아니라 현재. 리드미는 다음 소괄호 안의 내용과 같고 (" + readmeContent + ") 디스크립션은 \"" + repoItem.getDescription() + "\" 이 문자열과 같아. 그리고 현 레포지토리에 대한 주차별 commit 갯수 인데, 다음 소괄호 안의 배열과 같고 총 1년치의 정보야. (" + weeklyCommitList +") 첫 번째 값이 52주 전의 주당 커밋 갯수고, 마지막 값이 가장 최근 주의 커밋 갯수야. 이 모든 것을 종합해서 투자할 가치가 있는지 근거를 명확히 해서 분석해줘. 최종 의견은 '결론 : ' 양식에 맞춰서 작성해줘";
            requestMessages.add(new AIRequestDto.ChatMessageDto("user", prompt));
            JSONObject chatGpt = new JSONObject(openAIFeignClient.getChatGpt(new AIRequestDto("gpt-3.5-turbo-1106", requestMessages), openApiKey));
            chatgptAnalysis = chatGpt.getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content");
            chatGptCache.put(repoItem.getId(), chatgptAnalysis);
            scheduledExecutorService.schedule(() -> chatGptCache.remove(repoItem.getId()), 10, TimeUnit.MINUTES);
        }
        repoItem.setViewCount(repoItem.getViewCount() + 1); //조회수 증가 로직

        return RepoItemDetailResponseDto.builder()
                .avatarUrl(repoItem.getUser().getAvatarUrl())
                .userName(repoItem.getUser().getUserName())
                .repoName(repoItem.getRepoName())
                .tags(repoItem.getTags())
                .mostLanguage(repoItem.getMostLanguage())
                .license(repoItem.getLicense())
                .lastCommitAt(repoItem.getLastCommitAt())
                .viewCount(repoItem.getViewCount())
                .description(repoItem.getDescription())
                .readmeContent(readmeContent)
                .weeklyCommitList(weeklyCommitList)
                .totalCommits(totalCommits)
                .totalStars(totalStars)
                .totalContributions(totalContributions)
                .totalIssues(totalIssues)
                .totalPullRequests(totalPullRequests)
                .rank(rank)
                .chatgptAnalysis(chatgptAnalysis)
                .repositoryLink(repoItem.getRepositoryLink())
                .build();
    }


    private RecommendedRepoCardDto convertToCardDto(RepoItem repoItem) {
        return RecommendedRepoCardDto.builder()
                .id(repoItem.getId())
                .userName(repoItem.getUser().getUserName())
                .avatarUrl(repoItem.getUser().getAvatarUrl())
                .repoName(repoItem.getRepoName())
                .description(repoItem.getDescription())
                .tags(repoItem.getTags())
                .mostLanguage(repoItem.getMostLanguage())
                .license(repoItem.getLicense())
                .repositoryLink(repoItem.getRepositoryLink())
                .viewCount(repoItem.getViewCount())
                .totalPoint(repoItem.getTotalPoint())
                .lastCommitAt(repoItem.getLastCommitAt())
                .build();
    }
}

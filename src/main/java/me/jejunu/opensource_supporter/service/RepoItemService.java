package me.jejunu.opensource_supporter.service;

import lombok.RequiredArgsConstructor;
import me.jejunu.opensource_supporter.config.GithubApiFeignClient;
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

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RepoItemService {
    private final GithubApiFeignClient githubApiFeignClient;
    private final GithubApiService githubApiService;
    private final RepoItemRepository repoItemRepository;
    private final UserRepository userRepository;
    private final SupportedPointRepository supportedPointRepository;

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

            result.add(RepoItemModalResponseDto.builder()
                    .repoId(repoId)
                    .repoName(repoName)
                    .forkCount(forkCount)
                    .starCount(starCount)
                    .lastCommitAt(lastCommitAt)
                    .posted(posted)
                    .build());
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

    @Transactional
    public RepoItem increaseViewCount(Long id){
        RepoItem responseRepoItem = repoItemRepository.findById(id)
                .orElseThrow(()->new IllegalArgumentException("not found RepoItem"));
        responseRepoItem.setViewCount(responseRepoItem.getViewCount() + 1);

        return responseRepoItem;
    }

    @Transactional(readOnly = true)
    public RepoItem readSingleRepoItem(Long id){
        return repoItemRepository.findById(id)
                .orElseThrow(()->new IllegalArgumentException("not found RepoItem"));
    }

    @Transactional
    public void deleteRepoItem(String authHeader, RepoItemDeleteRequestDto request){
        String userToken = authHeader.replace("Bearer ", "");
        JSONObject userDataResponse = githubApiService.getUserFromGithub(userToken);
        String userName = userDataResponse.getString("login");
        RepoItem deleteRepoItem = repoItemRepository.findById(request.getRepoId())
                .orElseThrow(()->new IllegalArgumentException("not found repoItem"));
        if(Objects.equals(userName, deleteRepoItem.getUser().getUserName())){
            repoItemRepository.deleteById(request.getRepoId());
        }
        else{
            throw new RuntimeException("token 소유자, Repo 소유자 불일치");
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

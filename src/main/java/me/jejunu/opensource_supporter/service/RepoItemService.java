package me.jejunu.opensource_supporter.service;

import lombok.RequiredArgsConstructor;
import me.jejunu.opensource_supporter.config.GithubApiFeignClient;
import me.jejunu.opensource_supporter.domain.RepoItem;
import me.jejunu.opensource_supporter.domain.User;
import me.jejunu.opensource_supporter.dto.RepoItemCreateRequestDto;
import me.jejunu.opensource_supporter.dto.RepoItemDeleteRequestDto;
import me.jejunu.opensource_supporter.dto.RepoItemUpdateRequestDto;
import me.jejunu.opensource_supporter.repository.RepoItemRepository;
import me.jejunu.opensource_supporter.repository.UserRepository;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RepoItemService {
    private final GithubApiFeignClient githubApiFeignClient;
    private final RepoItemRepository repoItemRepository;
    private final UserRepository userRepository;

    @Transactional
    public RepoItem increaseViewCount(Long id){
        RepoItem responseRepoItem = repoItemRepository.findById(id)
                .orElseThrow(()->new IllegalArgumentException("not found RepoItem"));
        responseRepoItem.setViewCount(responseRepoItem.getViewCount() + 1);

        return responseRepoItem;
    }

    @Transactional
    public void deleteRepoItem(RepoItemDeleteRequestDto request){
        //구현하자
    }

    @Transactional
    public RepoItem updateRepoItem(RepoItemUpdateRequestDto request) {
        String access_token = request.getAccess_token();
        Long repoId = request.getRepoId();
        RepoItem updateRepoItem = repoItemRepository.findById(repoId)
                .orElseThrow(()->new IllegalArgumentException("not found repoItem"));
        String userName = updateRepoItem.getUser().getUserName();
        String repoName = updateRepoItem.getRepoName();
        //MostLanguage
        JSONObject mostLanguageResponse = new JSONObject(githubApiFeignClient.getMostLanguage(userName, repoName, access_token));
        String mostLanguage = findMostUsedLanguage(mostLanguageResponse);
        //License
        String license = findLicense(userName, repoName, access_token);
        //LastCommitAt
        JSONObject commitResponse = new JSONObject(githubApiFeignClient.getCommitSha(userName, repoName, access_token));
        LocalDateTime lastCommitAt = findLastCommitAt(commitResponse);

        updateRepoItem.update(request.getDescription(), request.getTags(), mostLanguage, license, lastCommitAt);

        return updateRepoItem;
    }

    @Transactional
    public RepoItem createRepoItem(RepoItemCreateRequestDto request) {
        String access_token = request.getAccess_token();
        String userName = request.getUserName();
        String repoName = request.getRepoName();
        String description = request.getDescription();
        List<String> tags = request.getTags();
        String repositoryLink = "https://github.com/" + userName + "/" + repoName;
        //MostLanguage
        JSONObject mostLanguageResponse = new JSONObject(githubApiFeignClient.getMostLanguage(userName, repoName, access_token));
        String mostLanguage = findMostUsedLanguage(mostLanguageResponse);
        //License
        String license = findLicense(userName, repoName, access_token);
        //LastCommitAt
        JSONObject commitResponse = new JSONObject(githubApiFeignClient.getCommitSha(userName, repoName, access_token));
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
    private String findLicense(String userName, String repoName, String access_token) {
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
}

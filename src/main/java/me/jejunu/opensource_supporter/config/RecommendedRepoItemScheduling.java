package me.jejunu.opensource_supporter.config;

import lombok.RequiredArgsConstructor;
import me.jejunu.opensource_supporter.domain.RepoItem;
import me.jejunu.opensource_supporter.domain.User;
import me.jejunu.opensource_supporter.repository.RepoItemRepository;
import me.jejunu.opensource_supporter.repository.UserRepository;
import me.jejunu.opensource_supporter.service.RepoItemService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component

@RequiredArgsConstructor
public class RecommendedRepoItemScheduling {

    @Value("${spring.security.oauth2.client.registration.github.client-id}")
    private String clientId;
    @Value("${spring.security.oauth2.client.registration.github.client-secret}")
    private String clientSecret;

    private final GithubApiFeignClient githubApiFeignClient;
    private final RepoItemRepository repoItemRepository;
    private final UserRepository userRepository;
    private final RepoItemService repoItemService;
    private final CacheManager cacheManager;


    @Scheduled(cron = "0 0 0 * * *") // 매일 자정에 수행
    public void recommendedRepoItemListUp() {
        CompletableFuture<Void> updateRepoItemFromDBFuture = CompletableFuture.runAsync(this::updateRepoItemFromGithub);
        //updateRepoItemFromDBFuture.thenRun(this::updateRecentlyCommitRepo);
        //updateRepoItemFromDBFuture.thenRun(this::updateMostViewed);
    }

    @Transactional
    public void updateRepoItemFromGithub() {

        String adminAuth = "Basic " + Base64.getEncoder().encodeToString((clientId + ":" + clientSecret).getBytes());
        List<User> userList = userRepository.findAll();

        for (User user : userList) {
            List<RepoItem> repoList = user.getRepoItemList();

            for (RepoItem repoItem : repoList) {

                Object response = githubApiFeignClient.getUserSingleRepoItem(adminAuth, user.getUserName(), repoItem.getRepoName());
                JSONObject jsonObject = new JSONObject(response.toString());

                LocalDateTime lastCommitAtFromGithub = LocalDateTime.parse(jsonObject.getString("pushed_at"), DateTimeFormatter.ISO_DATE_TIME);
                if (!lastCommitAtFromGithub.equals(repoItem.getLastCommitAt())) { // 변경된 부분
                    repoItem.setLastCommitAt(lastCommitAtFromGithub);
                    repoItem.setMostLanguage(jsonObject.optString("language", null));
                    repoItem.setLicense(repoItemService.findLicense(user.getUserName(), repoItem.getRepoName(), adminAuth));
                    System.out.println("lastCommitAtFromGithub = " + lastCommitAtFromGithub);
                    repoItemRepository.save(repoItem);
                    System.out.println("db 업데이트 했어요~");
                }
            }
        }
        System.out.println("db 업데이트 한 부분이 없어요~");
    }

    @Cacheable(cacheNames = "recentlyCommitRepoCache")
    public List<RepoItem> updateRecentlyCommitRepo() {
        List<RepoItem> repoItems = repoItemRepository.findAll();
        repoItems.sort(Comparator.comparing(RepoItem::getLastCommitAt).reversed());
        System.out.println("recently Commit Repo 캐싱 데이터가 없어요. 새롭게 캐싱 데이터를 작성합니다.");
        return repoItems;
    }
// pagenation recently commit repo
//    @Cacheable(cacheNames = "recentlyCommitRepoCache")
//    public Page<RepoItem> updateRecentlyCommitRepo(Pageable pageable) {
//        Page<RepoItem> repoItems = repoItemRepository.findAll(pageable);
//        return repoItems;
//    }

    @Cacheable(cacheNames = "mostViewedRepoCache") // 캐시 이름 지정
    public List<RepoItem> updateMostViewed(){
        List<RepoItem> repoItems = repoItemRepository.findAll();
        repoItems.sort(Comparator.comparingInt(RepoItem::getViewCount).reversed());
        System.out.println("캐싱 데이터가 없어요. 새롭게 캐싱 데이터를 작성합니다.");
        return repoItems;
    }

    // pagenation mostview repo
//    @Cacheable(cacheNames = "mostViewedRepoCache") // 캐시 이름 지정
//    public Page<RepoItem> updateMostViewed(Pageable pageable){
//        Page<RepoItem> repoItems = repoItemRepository.findAll(pageable);
//        return repoItems;
//    }

    //public List<RepoItem> updateMySupporter(){
//
//        return null;
//    }
}



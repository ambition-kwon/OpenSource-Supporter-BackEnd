package me.jejunu.opensource_supporter.controller;

import lombok.RequiredArgsConstructor;
import me.jejunu.opensource_supporter.config.RecommendedRepoItemScheduling;
import me.jejunu.opensource_supporter.domain.RepoItem;
import me.jejunu.opensource_supporter.dto.*;
import me.jejunu.opensource_supporter.service.RepoItemService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin
public class RepoItemController {
    private final RepoItemService repoItemService;
    private final RecommendedRepoItemScheduling recommendedRepoItemScheduling;

    @GetMapping("/api/repos/modal")
    public ResponseEntity<List<RepoItemModalResponseDto>> readMultipleRepoItems(@RequestHeader("Authorization") String authHeader){
        List<RepoItemModalResponseDto> repoItems = repoItemService.readMultipleRepoItems(authHeader);
        return ResponseEntity.ok().body(repoItems);
    }

    @GetMapping("/api/repos/main/partners")
    public ResponseEntity<List<RepoItem>> readPartnerRepoItems(@RequestHeader("Authorization") String authHeader){
        List<RepoItem> repoItems = repoItemService.readPartnerRepoItems(authHeader);
        return ResponseEntity.ok().body(repoItems);
    }

    @PostMapping("/api/repo")
    public ResponseEntity<RepoItem> createRepoItem(@RequestHeader("Authorization") String authHeader, @RequestBody RepoItemCreateRequestDto request){
        RepoItem repoItem = repoItemService.createRepoItem(authHeader, request);
        return ResponseEntity.ok().body(repoItem);
    }

    @PutMapping("/api/repo")
    public ResponseEntity<RepoItem> updateRepoItem(@RequestHeader("Authorization") String authHeader, @RequestBody RepoItemUpdateRequestDto request){
        RepoItem repoItem = repoItemService.updateRepoItem(authHeader, request);
        return ResponseEntity.ok().body(repoItem);
    }

    @DeleteMapping("/api/repo")
    public ResponseEntity<Void> deleteRepoItem(@RequestHeader("Authorization") String authHeader, @RequestBody RepoItemDeleteRequestDto request){
        repoItemService.deleteRepoItem(authHeader, request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/api/repo")
    public ResponseEntity<RepoItem> readSingleRepoItem(@RequestParam("id") Long id){
        RepoItem repoItem = repoItemService.readSingleRepoItem(id);
        return ResponseEntity.ok().body(repoItem);
    }

    @PutMapping("/api/repo/view-count")
    public ResponseEntity<RepoItem> increaseViewCount(@RequestParam("id") Long id){
        RepoItem responseRepoItem = repoItemService.increaseViewCount(id);
        return ResponseEntity.ok().body(responseRepoItem);
    }

    @GetMapping("/api/repo/recommended/recentlyCommit")
    public ResponseEntity<RecommendedRepoCardDto> getRecentlyCommit(){
        List<RepoItem> repoResult = recommendedRepoItemScheduling.updateRecentlyCommitRepo();
        return ResponseEntity.ok().body(RecommendedRepoCardDto.builder().recentlyCommitRepoList(repoResult).build());
    }

//    @GetMapping("/api/repo/recommended/recentlyCommit")
//    public ResponseEntity<RecommendedRepoCardDto> getRecentlyCommit(Pageable pageable){
//        Page<RepoItem> repoResult = recommendedRepoItemScheduling.updateRecentlyCommitRepo(pageable);
//        return ResponseEntity.ok().body(RecommendedRepoCardDto.builder().recentlyCommitRepoList(repoResult.getContent()).build());
//    }

    @GetMapping("/api/repo/recommended/myPartners")
    public ResponseEntity<RecommendedRepoCardDto> getMyPartners(){
        //RepoItem myPartnersResponseEntity = 캐싱에서 가져오기
        //return ResponseEntity.ok().body(myPartnersResponseEntity);
        return null;
    }

    @GetMapping("/api/repo/recommended/mostViewed")
    public ResponseEntity<RecommendedRepoCardDto> getMostViewed(){
        List<RepoItem> repoResult = recommendedRepoItemScheduling.updateMostViewed();
        return ResponseEntity.ok().body(RecommendedRepoCardDto.builder().recentlyCommitRepoList(repoResult).build());
    }

//    @GetMapping("/api/repo/recommended/mostViewed")
//    public ResponseEntity<RecommendedRepoCardDto> getMostViewed(Pageable pageable){
//        Page<RepoItem> repoResult = recommendedRepoItemScheduling.updateMostViewed(pageable);
//        return ResponseEntity.ok().body(RecommendedRepoCardDto.builder().recentlyCommitRepoList(repoResult.getContent()).build());
//    }

}

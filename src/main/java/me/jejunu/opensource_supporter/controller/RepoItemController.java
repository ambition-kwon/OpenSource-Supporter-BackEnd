package me.jejunu.opensource_supporter.controller;

import lombok.RequiredArgsConstructor;
import me.jejunu.opensource_supporter.domain.RepoItem;
import me.jejunu.opensource_supporter.dto.*;
import me.jejunu.opensource_supporter.service.RepoItemService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin
public class RepoItemController {
    @Value("${openai.api-key}")
    private String openApiKey;
    @Value("${spring.security.oauth2.client.registration.github.client-id}")
    private String clientId;
    @Value("${spring.security.oauth2.client.registration.github.client-secret}")
    private String clientSecret;
    private final RepoItemService repoItemService;

    @GetMapping("/api/repos/modal")
    public ResponseEntity<List<RepoItemModalResponseDto>> readMultipleRepoItems(@RequestHeader("Authorization") String authHeader){
        List<RepoItemModalResponseDto> repoItems = repoItemService.readMultipleRepoItems(authHeader);
        return ResponseEntity.ok().body(repoItems);
    }

    @GetMapping("/api/repos/supported")
    public ResponseEntity<List<RecommendedRepoCardDto>> readSupportedRepoItems(@RequestParam("userName") String userName){
       List<RecommendedRepoCardDto> repoItems = repoItemService.readSupportedRepoItems(userName);
       return ResponseEntity.ok().body(repoItems);
    }

    @GetMapping("/api/repos/supporting")
    public ResponseEntity<List<RecommendedRepoCardDto>> readSupportingRepoItems(@RequestParam("userName") String userName){
        List<RecommendedRepoCardDto> repoItems = repoItemService.readSupportingRepoItems(userName);
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
    public ResponseEntity<String> deleteRepoItem(@RequestHeader("Authorization") String authHeader, @RequestBody RepoItemIdRequestDto request){
        try {
            repoItemService.deleteRepoItem(authHeader, request);
            return ResponseEntity.ok().build();
        }catch (RuntimeException e){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    @GetMapping("/api/repo")
    public ResponseEntity<RepoItem> readSingleRepoItem(@RequestParam("id") Long id){
        RepoItem repoItem = repoItemService.readSingleRepoItem(id);
        return ResponseEntity.ok().body(repoItem);
    }

    @GetMapping("/api/repo/recommended/myPartners")
    public ResponseEntity<PagedRepoItemResponseDto> getMyPartners(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "3") int size) {
        Pageable pageable = PageRequest.of(page, size);
        PagedRepoItemResponseDto repoItems = repoItemService.getMyPartners(authHeader, pageable);
        return ResponseEntity.ok().body(repoItems);
    }

    @GetMapping("/api/repo/recommended/recentlyCommit")
    public ResponseEntity<PagedRepoItemResponseDto> getRecentlyCommit(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "3") int size) {
        Pageable pageable = PageRequest.of(page, size);
        PagedRepoItemResponseDto recentlyCommitRepo = repoItemService.updateRecentlyCommitRepo(pageable);
        return ResponseEntity.ok().body(recentlyCommitRepo);
    }

    @GetMapping("/api/repo/recommended/mostViewed")
    public ResponseEntity<PagedRepoItemResponseDto> getMostViewed(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "3") int size) {
        Pageable pageable = PageRequest.of(page, size);
        PagedRepoItemResponseDto mostViewedRepo = repoItemService.updateMostViewed(pageable);
        return ResponseEntity.ok().body(mostViewedRepo);
    }

    @GetMapping("/api/repo/detail")
    public ResponseEntity<RepoItemDetailResponseDto> getDetailRepoItem(@RequestParam("repoId") Long repoId){
        RepoItemDetailResponseDto response = repoItemService.getDetailRepoItem(clientId, clientSecret, repoId, "Bearer " + openApiKey);
        return ResponseEntity.ok().body(response);
    }
}

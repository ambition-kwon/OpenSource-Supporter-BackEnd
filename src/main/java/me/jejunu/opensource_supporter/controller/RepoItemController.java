package me.jejunu.opensource_supporter.controller;

import lombok.RequiredArgsConstructor;
import me.jejunu.opensource_supporter.domain.RepoItem;
import me.jejunu.opensource_supporter.dto.RepoItemCreateRequestDto;
import me.jejunu.opensource_supporter.dto.RepoItemDeleteRequestDto;
import me.jejunu.opensource_supporter.dto.RepoItemUpdateRequestDto;
import me.jejunu.opensource_supporter.service.RepoItemService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@CrossOrigin
public class RepoItemController {
    private final RepoItemService repoItemService;

    @PostMapping("/api/repo")
    public ResponseEntity<RepoItem> createRepoItem(@RequestBody RepoItemCreateRequestDto request){
        RepoItem repoItem = repoItemService.createRepoItem(request);
        return ResponseEntity.ok().body(repoItem);
    }

    @PutMapping("/api/repo")
    public ResponseEntity<RepoItem> updateRepoItem(@RequestBody RepoItemUpdateRequestDto request){
        RepoItem repoItem = repoItemService.updateRepoItem(request);
        return ResponseEntity.ok().body(repoItem);
    }

    @DeleteMapping("/api/repo")
    public ResponseEntity<Void> deleteRepoItem(@RequestBody RepoItemDeleteRequestDto request){
        repoItemService.deleteRepoItem(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/api/repo")
    public ResponseEntity<RepoItem> readRepoItem(){
        //구현하자.
        return ResponseEntity.ok().build();
    }

    @PutMapping("/api/repo/view-count")
    public ResponseEntity<RepoItem> increaseViewCount(@RequestParam("id") Long id){
        RepoItem responseRepoItem = repoItemService.increaseViewCount(id);
        return ResponseEntity.ok().body(responseRepoItem);
    }
}

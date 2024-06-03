package me.jejunu.opensource_supporter.controller;


import lombok.RequiredArgsConstructor;
import me.jejunu.opensource_supporter.domain.User;
import me.jejunu.opensource_supporter.dto.RecommendedRepoCardDto;
import me.jejunu.opensource_supporter.dto.SearchResponseDto;
import me.jejunu.opensource_supporter.service.RepoItemService;
import me.jejunu.opensource_supporter.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin
public class SearchController {
    private final UserService userService;
    private final RepoItemService repoItemService;

    @GetMapping("/api/search")
    public ResponseEntity<SearchResponseDto> searchUsers(@RequestParam("keyword") String keyword){
        List<User> users = userService.searchUsers(keyword);
        List<RecommendedRepoCardDto> repoItems = repoItemService.searchRepoItems(keyword);
        return ResponseEntity.ok().body(SearchResponseDto.builder().users(users).repositories(repoItems).build());
    }
}

package me.jejunu.opensource_supporter.controller;

import lombok.RequiredArgsConstructor;
import me.jejunu.opensource_supporter.dto.PagedRankResponseDto;
import me.jejunu.opensource_supporter.dto.RankingMyInfoRequestDto;
import me.jejunu.opensource_supporter.service.RankingService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin
public class RankController {

    private final RankingService rankingService;

    @GetMapping("/api/rank/userRank")
    public ResponseEntity<PagedRankResponseDto> getUserRanking(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size){
        Pageable pageable = PageRequest.of(page,size);
        PagedRankResponseDto userRankList = rankingService.getUserRankingList(pageable);
        return ResponseEntity.ok().body(userRankList);
    }

    @Cacheable(cacheNames = "myRank")
    @GetMapping("/api/rank/myRank")
    public ResponseEntity<RankingMyInfoRequestDto> getMyRanking(@RequestHeader("Authorization") String authHeader){
        RankingMyInfoRequestDto myRank = rankingService.getMyRanking(authHeader);
        return ResponseEntity.ok().body(myRank);
    }
}

package me.jejunu.opensource_supporter.controller;

import lombok.RequiredArgsConstructor;
import me.jejunu.opensource_supporter.dto.RankingMyInfoRequestDto;
import me.jejunu.opensource_supporter.dto.RankingUserInfoRequestDto;
import me.jejunu.opensource_supporter.service.RankingService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
@CrossOrigin
public class RankController {

    private final RankingService rankingService;

    @GetMapping("/api/rank/userRank")
    public ResponseEntity<List<RankingUserInfoRequestDto>> getUserRanking(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size){
        Pageable pageable = PageRequest.of(page,size);
        List<RankingUserInfoRequestDto> userRankList = rankingService.getUserRankingList(pageable);
        return ResponseEntity.ok(userRankList);
    }

    @Cacheable(cacheNames = "myRank")
    @GetMapping("/api/rank/myRank")
    public ResponseEntity<RankingMyInfoRequestDto> getMyRanking(@RequestHeader("Authorization") String authHeader){
        RankingMyInfoRequestDto myRank = rankingService.getMyRanking(authHeader);
        return ResponseEntity.ok(myRank);
    }
}

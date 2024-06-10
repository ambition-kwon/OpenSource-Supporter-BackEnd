package me.jejunu.opensource_supporter.controller;

import lombok.AllArgsConstructor;
import me.jejunu.opensource_supporter.domain.RepoItem;
import me.jejunu.opensource_supporter.domain.User;
import me.jejunu.opensource_supporter.dto.GainedPointCreateRequestDto;
import me.jejunu.opensource_supporter.dto.PagedPointResponseDto;
import me.jejunu.opensource_supporter.dto.PointSummaryResponseDto;
import me.jejunu.opensource_supporter.dto.SupportedPointCreateRequestDto;
import me.jejunu.opensource_supporter.service.PointService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@CrossOrigin
public class PointController {
    private final PointService pointService;

    // PayPal 활용한 사용자 포인트 충전
    @PostMapping("/api/point/charge")
    public ResponseEntity<User> chargePointByPayment(@RequestHeader("Authorization") String authHeader, @RequestBody GainedPointCreateRequestDto request) {
        if (request.getPrice() > 0) {
            User user = pointService.chargePointByPaypal(authHeader, request);
            return ResponseEntity.ok().body(user);
        }
        else return ResponseEntity.badRequest().build();
    }

    // 특정 RepoItem 포인트 후원
    @PostMapping("/api/repo/point")
    public ResponseEntity<RepoItem> supportToRepoItem(@RequestHeader("Authorization") String authHeader, @RequestBody SupportedPointCreateRequestDto request){
        if(request.getPrice() > 0){
            RepoItem repoItem = pointService.supportToRepoItem(authHeader, request);
            return ResponseEntity.ok().body(repoItem);
        }
        else return ResponseEntity.badRequest().build();
    }

    @GetMapping("/api/point/spent")
    public ResponseEntity<PagedPointResponseDto> getSpentPointList(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "6") int size){
        Pageable pageable = PageRequest.of(page, size);
        PagedPointResponseDto responseDto = pointService.getSpentPointList(authHeader, pageable);
        return ResponseEntity.ok().body(responseDto);
    }

    @GetMapping("/api/point/earned")
    public ResponseEntity<PagedPointResponseDto> getEarnedPointList(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "6") int size){
        Pageable pageable = PageRequest.of(page, size);
        PagedPointResponseDto responseDto = pointService.getEarnedPointList(authHeader, pageable);
        return ResponseEntity.ok().body(responseDto);
    }

    @GetMapping("/api/point/summary")
    public ResponseEntity<PointSummaryResponseDto> getPointSummary(@RequestHeader("Authorization") String authHeader){
        PointSummaryResponseDto responseDto = pointService.getPointSummary(authHeader);
        return ResponseEntity.ok().body(responseDto);
    }
}

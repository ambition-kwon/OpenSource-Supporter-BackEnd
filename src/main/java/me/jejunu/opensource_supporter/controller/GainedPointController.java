package me.jejunu.opensource_supporter.controller;

import lombok.AllArgsConstructor;
import me.jejunu.opensource_supporter.domain.User;
import me.jejunu.opensource_supporter.dto.GainedPointCreateRequestDto;
import me.jejunu.opensource_supporter.service.GainedPointService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@CrossOrigin
public class GainedPointController {
    private final GainedPointService gainedPointService;

    @PostMapping("/api/point/charge")
    public ResponseEntity<User> chargePointByPayment(@RequestHeader("Authorization") String authHeader, @RequestBody GainedPointCreateRequestDto request) {
        if (request.getPrice() > 0) {
            User user = gainedPointService.chargePointByPaypal(authHeader, request);
            return ResponseEntity.ok().body(user);
        }
        else return ResponseEntity.badRequest().build();
    }
}

package me.jejunu.opensource_supporter.controller;

import lombok.RequiredArgsConstructor;
import me.jejunu.opensource_supporter.domain.User;
import me.jejunu.opensource_supporter.dto.CardInfoResponseDto;
import me.jejunu.opensource_supporter.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@CrossOrigin
public class UserController {
    private final UserService userService;

    @GetMapping("/api/user/card")
    public ResponseEntity<CardInfoResponseDto> getCardInfo(@RequestParam("userName") String userName){
        CardInfoResponseDto cardInfoResponseDto = userService.getCardInfo(userName);
        return ResponseEntity.ok().body(cardInfoResponseDto);
    }

    @GetMapping("/api/user")
    public ResponseEntity<User> getUser(@RequestParam("userName") String userName){
        User user = userService.getUser(userName);
        return ResponseEntity.ok().body(user);
    }
}

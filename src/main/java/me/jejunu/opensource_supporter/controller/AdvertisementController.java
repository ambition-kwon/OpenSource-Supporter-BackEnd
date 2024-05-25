package me.jejunu.opensource_supporter.controller;

import lombok.RequiredArgsConstructor;
import me.jejunu.opensource_supporter.domain.Advertisement;
import me.jejunu.opensource_supporter.domain.User;
import me.jejunu.opensource_supporter.dto.AdvertisementCallbackRequestDto;
import me.jejunu.opensource_supporter.dto.AdvertisementCreateRequestDto;
import me.jejunu.opensource_supporter.dto.AdvertisementDeleteRequestDto;
import me.jejunu.opensource_supporter.service.AdvertisementService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@CrossOrigin
public class AdvertisementController {
    private final AdvertisementService advertisementService;

    @PostMapping("/api/advertisement")
    public ResponseEntity<Advertisement> createAdvertisement(@RequestBody AdvertisementCreateRequestDto request){
        Advertisement advertisement = advertisementService.createAdvertisement(request);
        return ResponseEntity.ok().body(advertisement);
    }

    @DeleteMapping("/api/advertisement")
    public ResponseEntity<Void> deleteAdvertisement(@RequestBody AdvertisementDeleteRequestDto request){
        advertisementService.deleteAdvertisement(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/api/advertisement/random")
    public ResponseEntity<Advertisement> readRandomAdvertisement(){
        Advertisement advertisement = advertisementService.readRandomAdvertisement();
        return ResponseEntity.ok().body(advertisement);
    }

    @PostMapping("api/advertisement/viewed")
    public ResponseEntity<User> successfulViews(@RequestBody AdvertisementCallbackRequestDto request){
        User user = advertisementService.successfulViews(request);
        return ResponseEntity.ok().body(user);
    }
}

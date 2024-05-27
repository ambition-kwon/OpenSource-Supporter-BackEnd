package me.jejunu.opensource_supporter.service;

import lombok.RequiredArgsConstructor;
import me.jejunu.opensource_supporter.domain.Advertisement;
import me.jejunu.opensource_supporter.domain.GainedPoint;
import me.jejunu.opensource_supporter.domain.User;
import me.jejunu.opensource_supporter.dto.AdvertisementCallbackRequestDto;
import me.jejunu.opensource_supporter.dto.AdvertisementCreateRequestDto;
import me.jejunu.opensource_supporter.dto.AdvertisementDeleteRequestDto;
import me.jejunu.opensource_supporter.repository.AdvertisementRepository;
import me.jejunu.opensource_supporter.repository.GainedPointRepository;
import me.jejunu.opensource_supporter.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdvertisementService {
    private final AdvertisementRepository advertisementRepository;
    private final UserRepository userRepository;
    private final GainedPointRepository gainedPointRepository;

    @Transactional
    public Advertisement createAdvertisement(AdvertisementCreateRequestDto request){
        return advertisementRepository.save(Advertisement.builder()
                .adName(request.getAdName())
                .adLength(request.getAdLength())
                .adContent(request.getAdContent())
                .price(request.getPrice())
                .build());
    }

    @Transactional
    public void deleteAdvertisement(AdvertisementDeleteRequestDto request){
        advertisementRepository.deleteById(request.getAdvertisementId());
    }

    @Transactional
    public Advertisement readRandomAdvertisement(){
        Advertisement advertisement = advertisementRepository.findByRandom()
                .orElseThrow(()->new IllegalArgumentException("no advertisement"));
        advertisement.incrementNumberOfCalls();
        return advertisement;
    }

    @Transactional
    public User successfulViews(AdvertisementCallbackRequestDto request){
        Advertisement advertisement = advertisementRepository.findById(request.getAdvertisementId())
                .orElseThrow(()->new IllegalArgumentException("not found advertisement"));
        User user = userRepository.findByUserName(request.getUserName())
                .orElseThrow(()->new IllegalArgumentException("not found user"));
        gainedPointRepository.save(GainedPoint.builder()
                .user(user)
                .method("advertisement")
                .price(advertisement.getPrice())
                .advertisement(advertisement)
                .build());
        advertisement.setNumberOfSuccessfulCalls(advertisement.getNumberOfSuccessfulCalls() + 1); //@PreUpdate 동작시키기위한 강제 업데이트(실제 값은 메서드로 변함)
        user.setTotalPoint(user.getTotalPoint() + advertisement.getPrice()); //@PreUpdate 동작시키기위한 강제 업데이트(실제 값은 메서드로 변함)
        return user;
    }
}

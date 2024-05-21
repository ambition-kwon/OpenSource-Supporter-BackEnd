package me.jejunu.opensource_supporter.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AdvertisementCallbackRequestDto {
    private String userName;
    private Long advertisementId;
}

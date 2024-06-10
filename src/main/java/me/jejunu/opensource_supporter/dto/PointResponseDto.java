package me.jejunu.opensource_supporter.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class PointResponseDto {
    private LocalDateTime date;
    private int point;
    private String description;
    private String status;

    @Builder
    public PointResponseDto(LocalDateTime date, int point, String description, String status) {
        this.date = date;
        this.point = point;
        this.description = description;
        this.status = status;
    }
}

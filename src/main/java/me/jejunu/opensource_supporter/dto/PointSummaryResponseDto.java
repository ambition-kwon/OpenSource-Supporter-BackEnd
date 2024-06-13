package me.jejunu.opensource_supporter.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PointSummaryResponseDto {
    private int spentPoints;
    private int paypalPoints;
    private int advertisementPoints;
    private int sponsoredPoints;
    private double monthlySpentPointsPercentage;
    private double monthlyPaypalPointsPercentage;
    private double monthlyAdvertisementPointsPercentage;
    private double monthlySponsoredPointsPercentage;

    @Builder
    public PointSummaryResponseDto(int spentPoints, int paypalPoints, int advertisementPoints, int sponsoredPoints, double monthlySpentPointsPercentage, double monthlyPaypalPointsPercentage, double monthlyAdvertisementPointsPercentage, double monthlySponsoredPointsPercentage) {
        this.spentPoints = spentPoints;
        this.paypalPoints = paypalPoints;
        this.advertisementPoints = advertisementPoints;
        this.sponsoredPoints = sponsoredPoints;
        this.monthlySpentPointsPercentage = monthlySpentPointsPercentage;
        this.monthlyPaypalPointsPercentage = monthlyPaypalPointsPercentage;
        this.monthlyAdvertisementPointsPercentage = monthlyAdvertisementPointsPercentage;
        this.monthlySponsoredPointsPercentage = monthlySponsoredPointsPercentage;
    }
}

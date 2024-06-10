package me.jejunu.opensource_supporter.service;

import lombok.AllArgsConstructor;
import me.jejunu.opensource_supporter.domain.GainedPoint;
import me.jejunu.opensource_supporter.domain.RepoItem;
import me.jejunu.opensource_supporter.domain.SupportedPoint;
import me.jejunu.opensource_supporter.domain.User;
import me.jejunu.opensource_supporter.dto.*;
import me.jejunu.opensource_supporter.repository.GainedPointRepository;
import me.jejunu.opensource_supporter.repository.RepoItemRepository;
import me.jejunu.opensource_supporter.repository.SupportedPointRepository;
import me.jejunu.opensource_supporter.repository.UserRepository;
import org.json.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
@AllArgsConstructor
public class PointService {
    private final UserRepository userRepository;
    private final GainedPointRepository gainedPointRepository;
    private final GithubApiService githubApiService;
    private final RepoItemRepository repoItemRepository;
    private final SupportedPointRepository supportedPointRepository;

    @Transactional
    public User chargePointByPaypal(String authHeader, GainedPointCreateRequestDto request){
        String userToken = authHeader.replace("Bearer ", "");
        JSONObject userDataResponse = githubApiService.getUserFromGithub(userToken);
        String userName = userDataResponse.getString("login");
        User user = userRepository.findByUserName(userName)
                .orElseThrow(()->new IllegalArgumentException("not found user"));
        gainedPointRepository.save(GainedPoint.builder()
                .price(request.getPrice())
                .method("Paypal")
                .user(user)
                .advertisement(null)
                .build());
        user.setTotalPoint(user.getTotalPoint() + request.getPrice()); //@PreUpdate 동작시키기위한 강제 업데이트(실제 값은 메서드로 변함)
        return user;
    }

    @Transactional
    public RepoItem supportToRepoItem(String authHeader, SupportedPointCreateRequestDto request){
        String userToken = authHeader.replace("Bearer ", "");
        JSONObject userDataResponse = githubApiService.getUserFromGithub(userToken);
        String userName = userDataResponse.getString("login");
        User giveUser = userRepository.findByUserName(userName)
                .orElseThrow(()->new IllegalArgumentException("not found user"));
        if(giveUser.getRemainingPoint() < request.getPrice()) throw new RuntimeException("need more points"); //예외1(남은 포인트 체크)
        RepoItem repoItem = repoItemRepository.findById(request.getRepoId())
                .orElseThrow(()->new IllegalArgumentException("not found repoItem"));
        User takeUser = repoItem.getUser();
        if(giveUser == takeUser) throw new RuntimeException("Cannot support your own repository"); //예외2(본인레포 후원 금지)
        supportedPointRepository.save(SupportedPoint.builder()
                .user(giveUser)
                .repoItem(repoItem)
                .price(request.getPrice())
                .build());
        // 관리자 승인 로직 없어서 그냥 바로 승인되었다고 가정
        gainedPointRepository.save(GainedPoint.builder()
                .user(takeUser)
                .price(request.getPrice())
                .method(giveUser.getUserName() + " has sponsored the " +  repoItem.getRepoName() + " repository")
                .advertisement(null)
                .build());
        giveUser.setUsedPoint(giveUser.getUsedPoint() + request.getPrice()); //@PreUpdate 동작시키기위한 강제 업데이트(실제 값은 메서드로 변함)
        takeUser.setTotalPoint(takeUser.getTotalPoint() + request.getPrice()); //@PreUpdate 동작시키기위한 강제 업데이트(실제 값은 메서드로 변함)
        repoItem.setTotalPoint(repoItem.getTotalPoint() + request.getPrice()); //@PreUpdate 동작시키기위한 강제 업데이트(실제 값은 메서드로 변함)
        return repoItem;
    }

    public PagedPointResponseDto getSpentPointList(String authHeader, Pageable pageable){
        String userToken = authHeader.replace("Bearer ", "");
        JSONObject userDataResponse = githubApiService.getUserFromGithub(userToken);
        String userName = userDataResponse.getString("login");
        User user = userRepository.findByUserName(userName)
                .orElseThrow(()->new IllegalArgumentException("not found user"));
        Page<SupportedPoint> supportedPoints = supportedPointRepository.findByUserOrderByCreatedAtDesc(user, pageable);
        List<PointResponseDto> pointResponseDtoList = supportedPoints.stream()
                .map(supportedPoint -> PointResponseDto.builder()
                        .date(supportedPoint.getCreatedAt())
                        .point(-supportedPoint.getPrice())
                        .description(supportedPoint.getRepoItem().getUser().getUserName() + " / " + supportedPoint.getRepoItem().getRepoName())
                        .status(supportedPoint.isSent() ? "COMPLETED" : "IN PROGRESS")
                        .build())
                .toList();
        return PagedPointResponseDto.builder()
                .hasNextPage(supportedPoints.hasNext())
                .data(pointResponseDtoList)
                .build();
    }

    public PagedPointResponseDto getEarnedPointList(String authHeader, Pageable pageable){
        String userToken = authHeader.replace("Bearer ", "");
        JSONObject userDataResponse = githubApiService.getUserFromGithub(userToken);
        String userName = userDataResponse.getString("login");
        User user = userRepository.findByUserName(userName)
                .orElseThrow(()->new IllegalArgumentException("not found user"));
        Page<GainedPoint> gainedPoints = gainedPointRepository.findByUserOrderByCreatedAtDesc(user, pageable);
        List<PointResponseDto> pointResponseDtoList = gainedPoints.stream()
                .map(gainedPoint -> PointResponseDto.builder()
                        .date(gainedPoint.getCreatedAt())
                        .point(gainedPoint.getPrice())
                        .description(gainedPoint.getMethod())
                        .status("COMPLETED")
                        .build())
                .toList();
        return PagedPointResponseDto.builder()
                .hasNextPage(gainedPoints.hasNext())
                .data(pointResponseDtoList)
                .build();
    }

    public PointSummaryResponseDto getPointSummary(String authHeader){
        int totalSpentPoints = 0;
        int totalPaypalPoints = 0;
        int totalAdvertisementPoints = 0;
        int totalSponsoredPoints = 0;
        int currentMonthSpentPoints = 0;
        int previousMonthSpentPoints = 0;
        int currentMonthPaypalPoints = 0;
        int previousMonthPaypalPoints = 0;
        int currentMonthAdvertisementPoints = 0;
        int previousMonthAdvertisementPoints = 0;
        int currentMonthSponsoredPoints = 0;
        int previousMonthSponsoredPoints = 0;
        double monthlySpentPointsPercentage = 0.0;
        double monthlyPaypalPointsPercentage = 0.0;
        double monthlyAdvertisementPointsPercentage = 0.0;
        double monthlySponsoredPointsPercentage = 0.0;
        LocalDate now = LocalDate.now();
        YearMonth currentMonth = YearMonth.from(now);
        YearMonth previousMonth = currentMonth.minusMonths(1);

        String userToken = authHeader.replace("Bearer ", "");
        JSONObject userDataResponse = githubApiService.getUserFromGithub(userToken);
        String userName = userDataResponse.getString("login");
        User user = userRepository.findByUserName(userName)
                .orElseThrow(()->new IllegalArgumentException("not found user"));
        List<SupportedPoint> supportedPointList = supportedPointRepository.findByUser(user);
        List<GainedPoint> gainedPointList = gainedPointRepository.findByUser(user);

        if (supportedPointList != null) {
            currentMonthSpentPoints = supportedPointList.stream()
                    .filter(supportedPoint -> YearMonth.from(supportedPoint.getCreatedAt().toLocalDate()).equals(currentMonth))
                    .mapToInt(SupportedPoint::getPrice)
                    .sum();

            previousMonthSpentPoints = supportedPointList.stream()
                    .filter(supportedPoint -> YearMonth.from(supportedPoint.getCreatedAt().toLocalDate()).equals(previousMonth))
                    .mapToInt(SupportedPoint::getPrice)
                    .sum();

            totalSpentPoints = supportedPointList.stream()
                    .mapToInt(SupportedPoint::getPrice)
                    .sum();
        }

        if (gainedPointList != null) {
            currentMonthPaypalPoints = gainedPointList.stream()
                    .filter(gainedPoint -> YearMonth.from(gainedPoint.getCreatedAt().toLocalDate()).equals(currentMonth)
                            && "Paypal".equals(gainedPoint.getMethod()))
                    .mapToInt(GainedPoint::getPrice)
                    .sum();

            previousMonthPaypalPoints = gainedPointList.stream()
                    .filter(gainedPoint -> YearMonth.from(gainedPoint.getCreatedAt().toLocalDate()).equals(previousMonth)
                            && "Paypal".equals(gainedPoint.getMethod()))
                    .mapToInt(GainedPoint::getPrice)
                    .sum();

            currentMonthAdvertisementPoints = gainedPointList.stream()
                    .filter(gainedPoint -> YearMonth.from(gainedPoint.getCreatedAt().toLocalDate()).equals(currentMonth)
                            && gainedPoint.getMethod().startsWith("Advertisement /"))
                    .mapToInt(GainedPoint::getPrice)
                    .sum();

            previousMonthAdvertisementPoints = gainedPointList.stream()
                    .filter(gainedPoint -> YearMonth.from(gainedPoint.getCreatedAt().toLocalDate()).equals(previousMonth)
                            && gainedPoint.getMethod().startsWith("Advertisement /"))
                    .mapToInt(GainedPoint::getPrice)
                    .sum();

            currentMonthSponsoredPoints = gainedPointList.stream()
                    .filter(gainedPoint -> YearMonth.from(gainedPoint.getCreatedAt().toLocalDate()).equals(currentMonth)
                            && !("Paypal".equals(gainedPoint.getMethod()) || gainedPoint.getMethod().startsWith("Advertisement /")))
                    .mapToInt(GainedPoint::getPrice)
                    .sum();

            previousMonthSponsoredPoints = gainedPointList.stream()
                    .filter(gainedPoint -> YearMonth.from(gainedPoint.getCreatedAt().toLocalDate()).equals(previousMonth)
                            && !("Paypal".equals(gainedPoint.getMethod()) || gainedPoint.getMethod().startsWith("Advertisement /")))
                    .mapToInt(GainedPoint::getPrice)
                    .sum();

            totalPaypalPoints = gainedPointList.stream()
                    .filter(gainedPoint -> "Paypal".equals(gainedPoint.getMethod()))
                    .mapToInt(GainedPoint::getPrice)
                    .sum();

            totalAdvertisementPoints = gainedPointList.stream()
                    .filter(gainedPoint -> gainedPoint.getMethod().startsWith("Advertisement /"))
                    .mapToInt(GainedPoint::getPrice)
                    .sum();

            totalSponsoredPoints = gainedPointList.stream()
                    .filter(gainedPoint -> !("Paypal".equals(gainedPoint.getMethod()) || gainedPoint.getMethod().startsWith("Advertisement /")))
                    .mapToInt(GainedPoint::getPrice)
                    .sum();
        }

        //직전 달이 0포인트면 무한대로 발산하기 때문에 0.0%로 예외처리
        if (previousMonthSpentPoints != 0) {
            monthlySpentPointsPercentage = ((double) (currentMonthSpentPoints - previousMonthSpentPoints) / previousMonthSpentPoints) * 100;
        }

        if (previousMonthPaypalPoints != 0) {
            monthlyPaypalPointsPercentage = ((double) (currentMonthPaypalPoints - previousMonthPaypalPoints) / previousMonthPaypalPoints) * 100;
        }

        if (previousMonthAdvertisementPoints != 0) {
            monthlyAdvertisementPointsPercentage = ((double) (currentMonthAdvertisementPoints - previousMonthAdvertisementPoints) / previousMonthAdvertisementPoints) * 100;
        }

        if (previousMonthSponsoredPoints != 0) {
            monthlySponsoredPointsPercentage = ((double) (currentMonthSponsoredPoints - previousMonthSponsoredPoints) / previousMonthSponsoredPoints) * 100;
        }

        return PointSummaryResponseDto.builder()
                .spentPoints(totalSpentPoints)
                .paypalPoints(totalPaypalPoints)
                .advertisementPoints(totalAdvertisementPoints)
                .sponsoredPoints(totalSponsoredPoints)
                .monthlySpentPointsPercentage(monthlySpentPointsPercentage)
                .monthlyPaypalPointsPercentage(monthlyPaypalPointsPercentage)
                .monthlyAdvertisementPointsPercentage(monthlyAdvertisementPointsPercentage)
                .monthlySponsoredPointsPercentage(monthlySponsoredPointsPercentage)
                .build();
    }
}

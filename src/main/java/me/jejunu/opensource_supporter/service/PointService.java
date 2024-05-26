package me.jejunu.opensource_supporter.service;

import lombok.AllArgsConstructor;
import me.jejunu.opensource_supporter.domain.GainedPoint;
import me.jejunu.opensource_supporter.domain.RepoItem;
import me.jejunu.opensource_supporter.domain.SupportedPoint;
import me.jejunu.opensource_supporter.domain.User;
import me.jejunu.opensource_supporter.dto.GainedPointCreateRequestDto;
import me.jejunu.opensource_supporter.dto.SupportedPointCreateRequestDto;
import me.jejunu.opensource_supporter.repository.GainedPointRepository;
import me.jejunu.opensource_supporter.repository.RepoItemRepository;
import me.jejunu.opensource_supporter.repository.SupportedPointRepository;
import me.jejunu.opensource_supporter.repository.UserRepository;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
                .method("paypal")
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
                .method("Calculate(" + giveUser.getUserName() + "->" + takeUser.getUserName() + "/" + repoItem.getRepoName() + ")")
                .advertisement(null)
                .build());
        giveUser.setUsedPoint(giveUser.getUsedPoint() + request.getPrice()); //@PreUpdate 동작시키기위한 강제 업데이트(실제 값은 메서드로 변함)
        takeUser.setTotalPoint(takeUser.getTotalPoint() + request.getPrice()); //@PreUpdate 동작시키기위한 강제 업데이트(실제 값은 메서드로 변함)
        repoItem.setTotalPoint(repoItem.getTotalPoint() + request.getPrice()); //@PreUpdate 동작시키기위한 강제 업데이트(실제 값은 메서드로 변함)
        return repoItem;
    }
}

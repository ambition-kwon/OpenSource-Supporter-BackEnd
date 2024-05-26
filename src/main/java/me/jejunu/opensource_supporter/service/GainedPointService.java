package me.jejunu.opensource_supporter.service;

import lombok.AllArgsConstructor;
import me.jejunu.opensource_supporter.domain.GainedPoint;
import me.jejunu.opensource_supporter.domain.User;
import me.jejunu.opensource_supporter.dto.GainedPointCreateRequestDto;
import me.jejunu.opensource_supporter.repository.GainedPointRepository;
import me.jejunu.opensource_supporter.repository.UserRepository;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class GainedPointService {
    private final UserRepository userRepository;
    private final GainedPointRepository gainedPointRepository;
    private final GithubApiService githubApiService;

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
}

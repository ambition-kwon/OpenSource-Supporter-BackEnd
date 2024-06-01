package me.jejunu.opensource_supporter.service;

import lombok.RequiredArgsConstructor;
import me.jejunu.opensource_supporter.domain.User;
import me.jejunu.opensource_supporter.dto.RankingMyInfoRequestDto;
import me.jejunu.opensource_supporter.dto.RankingUserInfoRequestDto;
import me.jejunu.opensource_supporter.repository.UserRepository;
import org.json.JSONObject;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RankingService {

    private final UserRepository userRepository;
    private final GithubApiService githubApiService;

    @Cacheable(cacheNames = "rank")
    public List<User> getRankedUsers() {
        return userRepository.findAllByOrderByUsedPointDesc();
    }


    public List<RankingUserInfoRequestDto> getUserRankingList(Pageable pageable) {
        List<User> rankedUsers = getRankedUsers();
        AtomicInteger startRank = new AtomicInteger((int) pageable.getOffset() + 1);

        return rankedUsers.stream()
                .map(user -> RankingUserInfoRequestDto.builder()
                        .rank(startRank.getAndIncrement())
                        .userName(user.getUserName())
                        .customName(user.getCustomName())
                        .usedPoint(user.getUsedPoint())
                        .build())
                .collect(Collectors.toList());
    }

    public RankingMyInfoRequestDto getMyRanking(String authHeader) {
        String userToken = authHeader.replace("Bearer ", "");
        JSONObject userDataResponse = githubApiService.getUserFromGithub(userToken);
        String userName = userDataResponse.getString("login");
        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new IllegalArgumentException("not found user"));

        List<User> rankedUsers = getRankedUsers();
        int totalUsers = rankedUsers.size();

        // Find the rank of the user and calculate the top percent
        int userRank = rankedUsers.stream()
                .map(User::getUserName)
                .collect(Collectors.toList())
                .indexOf(userName) + 1;

        if (userRank == 0) {
            throw new IllegalArgumentException("User not found");
        }

        double topPercent = ((double) userRank / totalUsers) * 100;

        return RankingMyInfoRequestDto.builder()
                .rank(userRank)
                .userName(user.getUserName())
                .customName(user.getCustomName())
                .usedPoint(user.getUsedPoint())
                .topPercent(topPercent)
                .build();
    }
}

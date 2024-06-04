package me.jejunu.opensource_supporter.service;

import lombok.RequiredArgsConstructor;
import me.jejunu.opensource_supporter.domain.User;
import me.jejunu.opensource_supporter.dto.PagedRankResponseDto;
import me.jejunu.opensource_supporter.dto.RankingMyInfoRequestDto;
import me.jejunu.opensource_supporter.dto.RankingUserInfoRequestDto;
import me.jejunu.opensource_supporter.repository.UserRepository;
import org.json.JSONObject;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
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

    @Cacheable(cacheNames = "pageRank")
    public Page<User> getRankedUsers(Pageable pageable) {
        return userRepository.findAllByOrderByUsedPointDesc(pageable);
    }
    @Cacheable(cacheNames = "listRank")
    public List<User> getRankedUsers() {
        return userRepository.findAllByOrderByUsedPointDesc();
    }

    public PagedRankResponseDto getUserRankingList(Pageable pageable) {
        Page<User> rankedUsers = getRankedUsers(pageable);
        AtomicInteger startRank = new AtomicInteger((int) pageable.getOffset() + 1);
        List<RankingUserInfoRequestDto> rankingUserInfoRequestDtoList = rankedUsers.stream()
                .map(user -> RankingUserInfoRequestDto.builder()
                        .rank(startRank.getAndIncrement())
                        .userName(user.getUserName())
                        .customName(user.getCustomName())
                        .avatarUrl(user.getAvatarUrl())
                        .usedPoint(user.getUsedPoint())
                        .build()).toList();
        return PagedRankResponseDto.builder()
                .hasNextPage(rankedUsers.hasNext())
                .data(rankingUserInfoRequestDtoList)
                .build();
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
                .toList()
                .indexOf(userName) + 1;
        if (userRank == 0) {
            throw new IllegalArgumentException("not found user rank");}
        double topPercent = ((double) userRank / totalUsers) * 100;
        return RankingMyInfoRequestDto.builder()
                .rank(userRank)
                .userName(user.getUserName())
                .customName(user.getCustomName())
                .avatarUrl(user.getAvatarUrl())
                .usedPoint(user.getUsedPoint())
                .topPercent(topPercent)
                .build();
    }
}

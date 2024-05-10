package me.jejunu.opensource_supporter.service;

import lombok.RequiredArgsConstructor;
import me.jejunu.opensource_supporter.domain.RepoItem;
import me.jejunu.opensource_supporter.domain.SupportedPoint;
import me.jejunu.opensource_supporter.domain.User;
import me.jejunu.opensource_supporter.dto.CardInfoResponseDto;
import me.jejunu.opensource_supporter.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public CardInfoResponseDto getCardInfo(String userName){
        User user = userRepository.findByUserName(userName)
                .orElseThrow(()->new IllegalArgumentException("not found user"));

        Set<String> donatedRepoSet = new HashSet<>(); //중복 제거 처리(Set)
        for (SupportedPoint supportedPoint : user.getSupportedPointList()) {
            donatedRepoSet.add(supportedPoint.getRepoItem().getRepoName());
        }
        List<String> donatedRepoList = new ArrayList<>(donatedRepoSet);

        return CardInfoResponseDto.builder()
                .userName(user.getUserName())
                .totalDonated(user.getUsedPoint())
                .donatedRepoList(donatedRepoList)
                .build();
    }
}

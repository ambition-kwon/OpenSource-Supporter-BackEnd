package me.jejunu.opensource_supporter.service;

import lombok.RequiredArgsConstructor;
import me.jejunu.opensource_supporter.domain.RepoItem;
import me.jejunu.opensource_supporter.dto.RepoItemCreateRequestDto;
import me.jejunu.opensource_supporter.repository.RepoItemRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RepoItemService {
    private final RepoItemRepository repoItemRepository;
    public RepoItem createRepoItem(RepoItemCreateRequestDto request){
        String userName = request.getUserName();
        String repoName = request.getRepoName();
        String description = request.getDescription();
        List<String> tags = request.getTags();
        String repositoryLink = "https://github.com/" + userName + "/" + repoName;
        String mostLanguage = null; //Github측에서 받아오도록 구성
        String license = null; //Github측에서 받아오도록 구성
        LocalDateTime lastCommitAt = null; //Github측에서 받아오도록 구성

        return repoItemRepository.save(RepoItem.builder().build());
    }
}

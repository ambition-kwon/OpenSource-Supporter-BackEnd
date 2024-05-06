package me.jejunu.opensource_supporter.repository;

import me.jejunu.opensource_supporter.domain.RepoItem;
import me.jejunu.opensource_supporter.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RepoItemRepository extends JpaRepository<RepoItem, Long> {

    Optional<RepoItem> findByRepoNameAndUser(String repoName, User user);

}

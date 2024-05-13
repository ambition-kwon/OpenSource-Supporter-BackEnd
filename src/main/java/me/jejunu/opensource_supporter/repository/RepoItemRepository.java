package me.jejunu.opensource_supporter.repository;

import me.jejunu.opensource_supporter.domain.RepoItem;
import me.jejunu.opensource_supporter.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RepoItemRepository extends JpaRepository<RepoItem, Long> {
    Optional<RepoItem> findByRepoNameAndUser(String repoName, User user);
    Optional<RepoItem> findByRepoName(String repoName);

    @Query("SELECT DISTINCT r.user.userName FROM RepoItem r")
    List<String> findAllUserNames();

    @Query("SELECT DISTINCT r.repoName FROM RepoItem r")
    List<String> findAllByRepoName();

    List<RepoItem> findAll();
}

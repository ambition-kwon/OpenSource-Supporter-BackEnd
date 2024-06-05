package me.jejunu.opensource_supporter.repository;

import me.jejunu.opensource_supporter.domain.RepoItem;
import me.jejunu.opensource_supporter.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RepoItemRepository extends JpaRepository<RepoItem, Long> {
    Optional<RepoItem> findByRepoNameAndUser(String repoName, User user);
    Optional<RepoItem> findByRepoName(String repoName);

    List<RepoItem> findAll();

    Page<RepoItem> findAllByOrderByLastCommitAtDesc(Pageable pageable);
    Page<RepoItem> findAllByOrderByViewCountDescLastCommitAtDesc(Pageable pageable);

    @Query("SELECT r FROM RepoItem r JOIN r.user u WHERE r.repoName LIKE %:keyword% OR r.description LIKE %:keyword% OR :keyword MEMBER OF r.tags OR u.userName LIKE %:keyword%")
    List<RepoItem> searchByKeyword(@Param("keyword") String keyword);

}

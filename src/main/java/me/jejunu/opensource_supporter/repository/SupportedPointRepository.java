package me.jejunu.opensource_supporter.repository;

import me.jejunu.opensource_supporter.domain.RepoItem;
import me.jejunu.opensource_supporter.domain.SupportedPoint;
import me.jejunu.opensource_supporter.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SupportedPointRepository extends JpaRepository<SupportedPoint, Long> {
    @Query("SELECT DISTINCT sp.repoItem FROM SupportedPoint sp WHERE sp.user = :user")
    List<RepoItem> findDistinctRepoItemsByUser(@Param("user") User user);

    Page<RepoItem> findDistinctRepoItemsByUser(User user, Pageable pageable);
}

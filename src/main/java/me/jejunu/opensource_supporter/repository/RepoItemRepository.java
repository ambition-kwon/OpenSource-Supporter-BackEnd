package me.jejunu.opensource_supporter.repository;

import me.jejunu.opensource_supporter.domain.RepoItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepoItemRepository extends JpaRepository<RepoItem, Long> {

}

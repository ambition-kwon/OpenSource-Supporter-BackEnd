package me.jejunu.opensource_supporter.repository;

import me.jejunu.opensource_supporter.domain.GainedPoint;
import me.jejunu.opensource_supporter.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GainedPointRepository extends JpaRepository<GainedPoint, Long> {
    Page<GainedPoint> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);
    List<GainedPoint> findByUser(User user);
}

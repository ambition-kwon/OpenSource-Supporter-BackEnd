package me.jejunu.opensource_supporter.repository;

import me.jejunu.opensource_supporter.domain.GainedPoint;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GainedPointRepository extends JpaRepository<GainedPoint, Long> {
}

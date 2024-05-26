package me.jejunu.opensource_supporter.repository;

import me.jejunu.opensource_supporter.domain.SupportedPoint;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupportedPointRepository extends JpaRepository<SupportedPoint, Long> {
}

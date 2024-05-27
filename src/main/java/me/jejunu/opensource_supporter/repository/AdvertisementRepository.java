package me.jejunu.opensource_supporter.repository;

import me.jejunu.opensource_supporter.domain.Advertisement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface AdvertisementRepository extends JpaRepository<Advertisement, Long> {
    //isActive가 true인 광고 중 랜덤으로 하나만을 return
    @Query(value = "SELECT * FROM advertisements WHERE is_active = true ORDER BY RAND() LIMIT 1", nativeQuery = true)
    Optional<Advertisement> findByRandom();
}

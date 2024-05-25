package me.jejunu.opensource_supporter.repository;

import me.jejunu.opensource_supporter.domain.Advertisement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface AdvertisementRepository extends JpaRepository<Advertisement, Long> {
    @Query(value = "SELECT * FROM advertisements ORDER BY RAND() LIMIT 1", nativeQuery = true)
    Optional<Advertisement> findByRandom();
}

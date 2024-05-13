package me.jejunu.opensource_supporter.controller;

import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.RequiredArgsConstructor;
//import me.jejunu.opensource_supporter.config.RCMDRepoItemCache;
import me.jejunu.opensource_supporter.domain.RepoItem;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@CrossOrigin
public class RecommnendedRepoController {
    //private final RCMDRepoItemCache cache = new RCMDRepoItemCache();

    //@Cacheable



}

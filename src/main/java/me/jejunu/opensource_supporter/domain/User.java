package me.jejunu.opensource_supporter.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {
    @Id
    private Long id;

    @Column(unique = true)
    private String userName;

    @Column(updatable = false)
    private String adLink;

    @Column(updatable = false)
    private String cardLink;

    private double totalPoint;

    private double usedPoint;

    private double remainingPoint;

    private boolean isAdmin;

    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<RepoItem> repositories;

    @OneToMany(mappedBy = "user")
    private List<SupportPoint> supportPointList;

    @OneToMany(mappedBy = "user")
    private List<GainedPoint> gainedPointList;

    @Builder
    public User(String userName) {
        this.userName = userName;
        this.adLink = "test_link";
        this.cardLink = "test_link";
        this.totalPoint = 0.0;
        this.usedPoint = 0.0;
        this.remainingPoint = 0.0;
        this.isAdmin = false;
    }
}

package me.jejunu.opensource_supporter.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String userName;

    @Column(updatable = false)
    private String adLink;

    @Column(updatable = false)
    private String cardLink;

    private int totalPoint;

    private int usedPoint;

    private int remainingPoint;

    private boolean isAdmin;

    @CreatedDate
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<RepoItem> repoItemList;

    @OneToMany(mappedBy = "user")
    private List<SupportedPoint> supportedPointList;

    @OneToMany(mappedBy = "user")
    private List<GainedPoint> gainedPointList;

    @Builder

    public User(String userName) {
        this.userName = userName;
        this.adLink = "https://www.test.com/adLink";
        this.cardLink = "https://www.test.com/cardLink";
    }
}

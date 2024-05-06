package me.jejunu.opensource_supporter.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "supported_points")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class SupportedPoint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int price;

    private boolean isSent;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime modifiedAt;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "rid")
    private RepoItem repoItem;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "uid")
    private User user;

    @Builder
    public SupportedPoint(User user, RepoItem repoItem, int price) {
        this.user = user;
        this.repoItem = repoItem;
        this.price = price;
        this.isSent = false;
    }
}

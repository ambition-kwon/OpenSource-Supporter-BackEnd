package me.jejunu.opensource_supporter.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "support_points")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SupportPoint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double price;

    private boolean isSent;

    @ManyToOne
    @JoinColumn(name = "repo_item_id")
    private RepoItem repoItem;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    public SupportPoint(User user, RepoItem repoItem, double price) {
        this.user = user;
        this.repoItem = repoItem;
        this.price = price;
        this.isSent = false;
    }
}

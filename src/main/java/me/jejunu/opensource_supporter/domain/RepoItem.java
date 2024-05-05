package me.jejunu.opensource_supporter.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "repo_items")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class RepoItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String repoName;

    private String description;

    private int viewCount;

    @ElementCollection
    private List<String> tags;

    private int totalPoint;

    private String repositoryLink;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime modifiedAt;

    @ManyToOne
    @JoinColumn(name = "uid")
    private User user;

    @OneToMany(mappedBy = "repoItem")
    private List<SupportedPoint> supportedPointList;

    @Builder
    public RepoItem(String repoName, String description, User user) {
        this.repoName = repoName;
        this.description = description;
        this.user = user;
    }
}

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

    @ElementCollection
    private List<String> tags;

    private String mostLanguage;

    private String license;

    private String repositoryLink;

    private int viewCount;

    private int totalPoint;

    private LocalDateTime lastCommitAt;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime modifiedAt;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "uid")
    private User user;

    @OneToMany(mappedBy = "repoItem")
    private List<SupportedPoint> supportedPointList;

    @Builder
    public RepoItem(User user, String repoName, String description, List<String> tags, String mostLanguage, String license, LocalDateTime lastCommitAt, String repositoryLink) {
        this.user = user;
        this.repoName = repoName;
        this.description = description;
        this.tags = tags;
        this.repositoryLink = repositoryLink;
        this.mostLanguage = mostLanguage;
        this.license = license;
        this.lastCommitAt = lastCommitAt;
    }
}

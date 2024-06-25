package me.jejunu.opensource_supporter.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
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

    @Setter
    private String customName;

    @Column(updatable = false)
    private String adLink;

    @Column(updatable = false)
    private String cardLink;

    @Setter
    private String avatarUrl;

    @Setter
    private int totalPoint;

    @Setter
    private int usedPoint;

    private int remainingPoint;

    private boolean isAdmin;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime modifiedAt;

    @JsonIgnore
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "user", cascade = CascadeType.REMOVE)
    private List<RepoItem> repoItemList;

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<SupportedPoint> supportedPointList;

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<GainedPoint> gainedPointList;

    @Builder
    public User(String userName, String customName, String avatarUrl) {
        this.userName = userName;
        this.customName = customName;
        this.avatarUrl = avatarUrl;
        this.adLink = "http://opensource-supporter.site/advertisement/" + userName;
        this.cardLink = "http://opensource-supporter.site/supporter-card/" + userName;
        this.isAdmin = false;
    }

    //엔티티가 save 혹은 update 되기 일보직전 동작합니다(단, 변경사항 없는 save는 미동작함)
    //gainedPoint 혹은 supportedPoint가 null일 경우 에러 발생하여 null check 추가하였습니다.
    //totalPoint는 Paypal 충전 혹은 광고 시청을 통한 보상만 합산합니다.
    @PrePersist
    @PreUpdate
    public void updatePoints(){
        if (this.gainedPointList != null) {
            this.totalPoint = this.gainedPointList.stream()
                    .filter(gainedPoint -> "Paypal".equals(gainedPoint.getMethod()) || gainedPoint.getMethod().startsWith("Advertisement /"))
                    .mapToInt(GainedPoint::getPrice)
                    .sum();
        }
        if (this.supportedPointList != null) {
            this.usedPoint = this.supportedPointList.stream().mapToInt(SupportedPoint::getPrice).sum();
        }
        this.remainingPoint = this.totalPoint - this.usedPoint;
    }
}

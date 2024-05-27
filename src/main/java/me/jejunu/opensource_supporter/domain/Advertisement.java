package me.jejunu.opensource_supporter.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "advertisements")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@EntityListeners(AuditingEntityListener.class)
public class Advertisement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(updatable = false, nullable = false, unique = true)
    private String adName;

    @Column(updatable = false, nullable = false)
    private String adContent;

    @Column(nullable = false)
    private int price;

    private int adLength;

    private int numberOfCalls;

    @Setter
    private int numberOfSuccessfulCalls;

    private boolean isActive;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime modifiedAt;

    @OneToMany(mappedBy = "advertisement", cascade = CascadeType.REMOVE)
    private List<GainedPoint> gainedPointList;

    @Builder
    public Advertisement(String adName, String adContent, int adLength, int price) {
        this.adName = adName;
        this.adContent = adContent;
        this.adLength = adLength;
        this.price = price;
        this.numberOfCalls = 0;
        this.numberOfSuccessfulCalls = 0;
        this.isActive = true;
    }

    public void incrementNumberOfCalls(){
        this.numberOfCalls += 1;
    }

    @PrePersist
    @PreUpdate
    public void countSuccessfulCalls(){
        if(this.gainedPointList != null){
            this.numberOfSuccessfulCalls = this.gainedPointList.size();
        }
    }
}

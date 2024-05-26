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
@Table(name = "gained_points")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@EntityListeners(AuditingEntityListener.class)
public class GainedPoint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int price;

    private String method;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime modifiedAt;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "uid")
    private User user;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "aid")
    private Advertisement advertisement; //충전 방법이 advertise일 경우 primary key값이 들어가며, paypal일 경우 null이 들어갑니다.

    @Builder
    public GainedPoint(User user, String method, int price, Advertisement advertisement) {
        this.user = user;
        this.method = method;
        this.price = price;
        this.advertisement = advertisement;
    }
}

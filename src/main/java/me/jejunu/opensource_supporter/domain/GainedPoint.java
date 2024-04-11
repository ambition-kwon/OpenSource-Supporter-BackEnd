package me.jejunu.opensource_supporter.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Table(name = "gained_points")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class GainedPoint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long index;

    private double price;

    private String method;

    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    public GainedPoint(User user, String method, double price) {
        this.user = user;
        this.method = method;
        this.price = price;
    }
}

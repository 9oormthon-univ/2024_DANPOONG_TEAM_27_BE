package com.luckit.goal.domain;

import com.luckit.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Goal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "GOAL_ID")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    private User user;

    private String name;

    private boolean isCompleted;

    private LocalDate startDate;
    private LocalDate endDate;

    public void toggleCompleted() {
        this.isCompleted = !this.isCompleted;
    }

}

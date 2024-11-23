package com.luckit.goal.domain;

import com.luckit.global.exception.CustomException;
import com.luckit.global.exception.code.ErrorCode;
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

    // Name 수정 메서드
    public void updateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new CustomException(ErrorCode.EMPTY_NEW_GOAL_ERROR, ErrorCode.EMPTY_NEW_GOAL_ERROR.getMessage());
        }
        this.name = name.trim();
    }
}

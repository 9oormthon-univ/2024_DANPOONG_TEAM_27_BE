package com.luckit.todo.domain;

import com.luckit.goal.domain.Goal;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;

@Entity
@Getter
@EntityListeners(AuditingEntityListener.class) // Auditing 활성화
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor
@Builder
public class Todo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TODO_ID")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GOAL_ID")
    private Goal goal;

    private String name;

    @CreatedDate
    private LocalDate date;

    private boolean isCompleted;

    private boolean isMadeByGpt;

    private String fortuneType;

    private int score;

    private int animal;

    public void toggleCompleted() {
        this.isCompleted = !this.isCompleted;
    }

}

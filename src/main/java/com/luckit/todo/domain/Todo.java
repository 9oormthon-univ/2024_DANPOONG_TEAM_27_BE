package com.luckit.todo.domain;

import com.luckit.goal.domain.Goal;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
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

    private LocalDate date;

    private boolean idCompleted;

    private int animal;

    public void toggleCompleted() {
        this.idCompleted = !this.idCompleted;
    }

}

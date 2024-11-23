package com.luckit.goal.domain;

import com.luckit.todo.domain.Todo;
import com.luckit.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GoalRepository extends JpaRepository<Goal, Integer> {

    List<Goal> findAllByUser(User user);

    @Query("SELECT t " +
            "FROM Goal t " +
            "WHERE t.user.userId = :userId " +
            "AND t.isCompleted = true")
    List<Goal> findCompletedGoalsByUserId(@Param("userId") Integer userId);
}

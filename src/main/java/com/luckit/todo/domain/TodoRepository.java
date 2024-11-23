package com.luckit.todo.domain;

import com.luckit.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface TodoRepository extends JpaRepository<Todo, Integer> {

    List<Todo> findAllByGoalId(Integer goalId);

    @Query("SELECT COUNT(t) " +
            "FROM Todo t " +
            "WHERE t.goal.user.userId = :userId " +
            "AND t.date = :date " +
            "AND t.isCompleted = true")
    int countCompletedTodosByUserIdAndDate(@Param("userId") Integer userId, @Param("date") LocalDate date);

    @Query("SELECT COUNT(t) " +
            "FROM Todo t " +
            "WHERE t.goal.id = :goalId " +
            "AND t.isCompleted = true")
    int countCompletedTodosByGoalId(@Param("goalId") Integer goalId);

    @Query("SELECT t " +
            "FROM Todo t " +
            "WHERE t.goal.id = :goalId " +
            "AND t.isCompleted = true")
    List<Todo> findCompletedTodosByGoalId(@Param("goalId") Integer goalId);


    boolean existsByGoal_IdAndIsMadeByGptTrueAndDate(Integer goalId, LocalDate date);

}
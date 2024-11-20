package com.luckit.todo.service;

import com.luckit.global.exception.CustomException;
import com.luckit.global.exception.code.ErrorCode;
import com.luckit.goal.controller.dto.AddGoalDto;
import com.luckit.goal.domain.Goal;
import com.luckit.goal.domain.GoalRepository;
import com.luckit.todo.controller.dto.AddTodoDto;
import com.luckit.todo.domain.Todo;
import com.luckit.todo.domain.TodoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TodoService {

    private final GoalRepository goalRepository;
    private final TodoRepository todoRepository;

    public String addTodo(AddTodoDto addTodoDto) {

        Goal goal = goalRepository.findById(addTodoDto.goalId())
                .orElseThrow(() -> new CustomException(ErrorCode.NO_GOAL_ERROR, ErrorCode.NO_GOAL_ERROR.getMessage()));

        Todo todo = Todo.builder()
                .goal(goal)
                .name(addTodoDto.name())
                .idCompleted(false)
                .build();

        todoRepository.save(todo);

        return "Todo successfully saved.";
    }
}
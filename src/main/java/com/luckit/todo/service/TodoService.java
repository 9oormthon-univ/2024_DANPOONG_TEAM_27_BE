package com.luckit.todo.service;

import com.luckit.global.exception.CustomException;
import com.luckit.global.exception.code.ErrorCode;
import com.luckit.goal.domain.Goal;
import com.luckit.goal.domain.GoalRepository;
import com.luckit.todo.controller.dto.AddTodoDto;
import com.luckit.todo.controller.dto.GetTodoDto;
import com.luckit.todo.domain.Todo;
import com.luckit.todo.domain.TodoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TodoService {

    private final GoalRepository goalRepository;
    private final TodoRepository todoRepository;

    public String addTodo(AddTodoDto addTodoDto) {

        Goal goal = goalRepository.findById(addTodoDto.goalId())
                .orElseThrow(() -> new CustomException(ErrorCode.NO_GOAL_ERROR, ErrorCode.NO_GOAL_ERROR.getMessage()));

        LocalDate date = LocalDate.of(
                addTodoDto.year(),
                addTodoDto.month(),
                addTodoDto.day()
        );

        Todo todo = Todo.builder()
                .goal(goal)
                .name(addTodoDto.name())
                .date(date)
                .idCompleted(false)
                .build();

        todoRepository.save(todo);

        return "Todo successfully saved.";
    }

    public List<GetTodoDto> getTodo(Integer goalId) {

        List<Todo> todoList = todoRepository.findAllByGoalId(goalId);

        List<GetTodoDto> getTodoDtos = new ArrayList<>();

        for (Todo todo : todoList) {

            LocalDate date = todo.getDate();

            GetTodoDto dto = GetTodoDto.builder()
                    .todoId(todo.getId())
                    .year(date.getYear())
                    .month(date.getMonthValue())
                    .day(date.getDayOfMonth())
                    .name(todo.getName())
                    .isCompleted(todo.isIdCompleted())
                    .build();

            getTodoDtos.add(dto);
        }

        return getTodoDtos;
    }

    public String completeTodo(Integer todoId) {

        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new CustomException(ErrorCode.NO_TODO_ERROR, ErrorCode.NO_TODO_ERROR.getMessage()));

        todo.toggleCompleted();
        todoRepository.save(todo);

        return "Todo successfully completed.";
    }
}

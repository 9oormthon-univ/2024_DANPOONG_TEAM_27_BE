package com.luckit.todo.controller;


import com.luckit.global.exception.code.SuccessCode;
import com.luckit.global.template.ApiResponseTemplate;
import com.luckit.todo.controller.dto.AddTodoDto;
import com.luckit.todo.controller.dto.GetTodoDto;
import com.luckit.todo.service.TodoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/todo")
public class TodoController implements TodoControllerDocs {

    private final TodoService todoService;

    @PostMapping
    public ApiResponseTemplate<String> addTodo(
            @RequestBody AddTodoDto addTodoDto
            ) {
        return ApiResponseTemplate.success(SuccessCode.ADD_TODO_SUCCESS, todoService.addTodo(addTodoDto));
    }

    @GetMapping("/{goal_id}")
    public ApiResponseTemplate<List<GetTodoDto>> getTodo(
            @PathVariable("goal_id") Integer goalId
    ) {
        return ApiResponseTemplate.success(SuccessCode.GET_TODO_SUCCESS, todoService.getTodo(goalId));
    }

    @PostMapping("/{todo_id}")
    public ApiResponseTemplate<String> completeTodo(
            @PathVariable("todo_id") Integer todoId
    ) {
        return ApiResponseTemplate.success(SuccessCode.COMPLETE_TODO_SUCCESS, todoService.completeTodo(todoId));
    }

}

package com.luckit.todo.controller;


import com.luckit.global.exception.code.SuccessCode;
import com.luckit.global.template.ApiResponseTemplate;
import com.luckit.todo.controller.dto.AddTodoDto;
import com.luckit.todo.controller.dto.GetTodoDto;
import com.luckit.todo.controller.dto.UpdateTodoDto;
import com.luckit.todo.service.TodoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
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
            Principal principal,
            @PathVariable("goal_id") Integer goal_id
    ) {
        return ApiResponseTemplate.success(SuccessCode.GET_TODO_SUCCESS, todoService.getTodo(principal, goal_id));
    }

    @PostMapping("/{todo_id}")
    public ApiResponseTemplate<String> completeTodo(
            @PathVariable("todo_id") Integer todoId
    ) {
        return ApiResponseTemplate.success(SuccessCode.COMPLETE_TODO_SUCCESS, todoService.completeTodo(todoId));
    }

    @DeleteMapping("/{todo_id}")
    public ApiResponseTemplate<String> deleteTodo(
            @PathVariable("todo_id") Integer todoId
    ) {
        return ApiResponseTemplate.success(SuccessCode.DELETE_TODO_SUCCESS, todoService.deleteTodo(todoId));
    }


    @GetMapping("/graph")
    public ApiResponseTemplate<List<Integer>> getTodoGraph(
            Principal principal,
            @RequestParam int year,
            @RequestParam int month
    ) {
        return ApiResponseTemplate.success(SuccessCode.GET_TODO_GRAPH_SUCCESS, todoService.getTodoGraph(Integer.parseInt(principal.getName()), year, month));
    }

    @PostMapping("/update")
    public ApiResponseTemplate<String> updateTodo(
            @RequestBody UpdateTodoDto updateTodoDto
    ) {
        return ApiResponseTemplate.success(SuccessCode.UPDATE_TODO_SUCCESS, todoService.updateTodo(updateTodoDto));
    }
}

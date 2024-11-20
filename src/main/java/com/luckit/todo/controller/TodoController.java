package com.luckit.todo.controller;


import com.luckit.global.exception.code.SuccessCode;
import com.luckit.global.template.ApiResponseTemplate;
import com.luckit.todo.controller.dto.AddTodoDto;
import com.luckit.todo.service.TodoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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

}

package com.luckit.todo.controller;

import com.luckit.global.template.ApiResponseTemplate;
import com.luckit.todo.controller.dto.AddTodoDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Todo", description = "Todo API")
public interface TodoControllerDocs {

    @Operation(
            summary = "미션 생성",
            description = "미션 생성 API",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "mission successfully created",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = String.class)
                            )
                    ),
                    @ApiResponse(responseCode = "401", description = "Unauthorized access")
            }
    )
    ApiResponseTemplate<String> addTodo(
            @RequestBody AddTodoDto addTodoDto
    );

}

package com.luckit.todo.controller;

import com.luckit.global.template.ApiResponseTemplate;
import com.luckit.todo.controller.dto.AddTodoDto;
import com.luckit.todo.controller.dto.GetTodoDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;

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

    @Operation(
            summary = "미션 조회",
            description = "미션 조회 API",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "mission successfully retrieved",
                            content = @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(
                                            schema = @Schema(implementation = GetTodoDto.class)
                                    )
                            )
                    ),
                    @ApiResponse(responseCode = "401", description = "Unauthorized access")
            }
    )
    ApiResponseTemplate<List<GetTodoDto>> getTodo(
            @PathVariable("goal_id") Integer goal_id
    );

    @Operation(
            summary = "미션 완료 상태 변경",
            description = "미션 상태 변경 API",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully change mission status.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = String.class)
                            )
                    ),
                    @ApiResponse(responseCode = "401", description = "Unauthorized access")
            }
    )
    ApiResponseTemplate<String> completeTodo(
            @PathVariable("todo_id") Integer todoId
    );

    @Operation(
            summary = "미션 삭제",
            description = "미션 삭제 API",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully delete mission",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = String.class)
                            )
                    ),
                    @ApiResponse(responseCode = "401", description = "Unauthorized access")
            }
    )
    ApiResponseTemplate<String> deleteTodo(
            @PathVariable("todo_id") Integer todoId
    );

    @Operation(
            summary = "미션 달성 그래프",
            description = "미션 달성 그래프 API",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully get mission graph.",
                            content = @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(
                                            schema = @Schema(implementation = Integer.class)
                                    )
                            )
                    ),
                    @ApiResponse(responseCode = "401", description = "Unauthorized access")
            }
    )
    ApiResponseTemplate<List<Integer>> getTodoGraph(
            Principal principal,
            @RequestParam int year,
            @RequestParam int month
    );

}

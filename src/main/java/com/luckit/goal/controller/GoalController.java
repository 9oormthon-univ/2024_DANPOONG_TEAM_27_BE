package com.luckit.goal.controller;

import com.luckit.global.exception.code.SuccessCode;
import com.luckit.global.template.ApiResponseTemplate;
import com.luckit.goal.controller.dto.*;
import com.luckit.goal.service.GoalService;
import com.luckit.todo.controller.dto.UpdateTodoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/goal")
public class GoalController implements GoalControllerDocs {

    private final GoalService goalService;

    @PostMapping
    public ApiResponseTemplate<String> addGoal(
            Principal principal,
            @RequestBody AddGoalDto addGoalDto
            ) {
        return ApiResponseTemplate.success(SuccessCode.ADD_GOAL_SUCCESS, goalService.addGoal(Integer.parseInt(principal.getName()), addGoalDto));
    }

    @GetMapping
    public ApiResponseTemplate<List<GetGoalDto>> getGoal(
            Principal principal
    ) {
        return ApiResponseTemplate.success(SuccessCode.GET_GOAL_SUCCESS, goalService.getGoal(Integer.parseInt(principal.getName())));
    }

    @GetMapping("/mypage")
    public ApiResponseTemplate<List<GetGoalMypageDto>> getGoalMypage(
            Principal principal
    ) {
        return ApiResponseTemplate.success(SuccessCode.GET_GOAL_MYPAGE_SUCCESS, goalService.getGoalMypage(Integer.parseInt(principal.getName())));
    }

    @GetMapping("/mypage/{goal_id}")
    public ApiResponseTemplate<GetEachGoalMypageDto> getEachGoalMypage(
            @PathVariable("goal_id") Integer goalId
    ) {
        return ApiResponseTemplate.success(SuccessCode.GET_EACH_GOAL_MYPAGE_SUCCESS, goalService.getEachGoalMypage(goalId));
    }

    @PostMapping("/{goal_id}")
    public ApiResponseTemplate<CompleteGoalDto> completeGoal(
            @PathVariable("goal_id") Integer goalId
    ) {
        return ApiResponseTemplate.success(SuccessCode.COMPLETE_GOAL_SUCCESS, goalService.completeGoal(goalId));
    }

    @DeleteMapping("/{goal_id}")
    public ApiResponseTemplate<String> deleteGoal(
            @PathVariable("goal_id") Integer goalId
    ) {
        return ApiResponseTemplate.success(SuccessCode.DELETE_GOAL_SUCCESS, goalService.deleteGoal(goalId));
    }

    @PostMapping("/update")
    public ApiResponseTemplate<String> updateGoal(
            @RequestBody UpdateTodoDto updateTodoDto
    ) {
        return ApiResponseTemplate.success(SuccessCode.UPDATE_GOAL_SUCCESS, goalService.updateGoal(updateTodoDto));
    }

}

package com.luckit.goal.controller;

import com.luckit.global.exception.code.SuccessCode;
import com.luckit.global.template.ApiResponseTemplate;
import com.luckit.goal.controller.dto.AddGoalDto;
import com.luckit.goal.controller.dto.GetGoalDto;
import com.luckit.goal.service.GoalService;
import lombok.RequiredArgsConstructor;
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

    @PostMapping("/{goal_id}")
    public ApiResponseTemplate<String> completeGoal(
            @PathVariable("goal_id") Integer goalId
    ) {
        return ApiResponseTemplate.success(SuccessCode.COMPLETE_GOAL_SUCCESS, goalService.completeGoal(goalId));
    }

}

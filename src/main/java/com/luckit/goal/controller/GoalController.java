package com.luckit.goal.controller;

import com.luckit.global.exception.code.SuccessCode;
import com.luckit.global.template.ApiResponseTemplate;
import com.luckit.goal.controller.dto.AddGoalDto;
import com.luckit.goal.domain.Goal;
import com.luckit.goal.service.GoalService;
import com.luckit.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

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

}

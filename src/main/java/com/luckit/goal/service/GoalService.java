package com.luckit.goal.service;

import com.luckit.global.exception.CustomException;
import com.luckit.global.exception.code.ErrorCode;
import com.luckit.goal.controller.dto.AddGoalDto;
import com.luckit.goal.domain.Goal;
import com.luckit.goal.domain.GoalRepository;
import com.luckit.user.domain.User;
import com.luckit.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class GoalService {

    private final GoalRepository goalRepository;
    private final UserRepository userRepository;

    public String addGoal(Integer id, AddGoalDto addGoalDto) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.NO_USER_INFO, ErrorCode.NO_USER_INFO.getMessage()));

        LocalDate start_date = LocalDate.of(
                addGoalDto.start_date_year(),
                addGoalDto.start_date_month(),
                addGoalDto.start_date_day()
        );

        LocalDate end_date = LocalDate.of(
                addGoalDto.end_date_year(),
                addGoalDto.end_date_month(),
                addGoalDto.end_date_day()
        );

        Goal goal = Goal.builder()
                .user(user)
                .name(addGoalDto.name())
                .isCompleted(addGoalDto.isCompleted())
                .startDate(start_date)
                .endDate(end_date)
                .build();

        goalRepository.save(goal);

        return "Goal successfully saved.";
    }
}

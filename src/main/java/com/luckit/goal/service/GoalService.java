package com.luckit.goal.service;

import com.luckit.global.exception.CustomException;
import com.luckit.global.exception.code.ErrorCode;
import com.luckit.goal.controller.dto.AddGoalDto;
import com.luckit.goal.controller.dto.GetGoalDto;
import com.luckit.goal.domain.Goal;
import com.luckit.goal.domain.GoalRepository;
import com.luckit.user.domain.User;
import com.luckit.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
                .isCompleted(false)
                .startDate(start_date)
                .endDate(end_date)
                .build();

        goalRepository.save(goal);

        return "Goal successfully saved.";
    }

    public List<GetGoalDto> getGoal(Integer id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.NO_USER_INFO, ErrorCode.NO_USER_INFO.getMessage()));

        List<Goal> goals = goalRepository.findAllByUser(user);

        List<GetGoalDto> getGoalDtos = new ArrayList<>();
        for (Goal goal : goals) {
            LocalDate start_date = goal.getStartDate();
            LocalDate end_date = goal.getEndDate();

            GetGoalDto dto = GetGoalDto.builder()
                    .goalId(goal.getId())
                    .name(goal.getName())
                    .isCompleted(goal.isCompleted())
                    .start_date_year(start_date.getYear())
                    .start_date_month(start_date.getMonthValue())
                    .start_date_day(start_date.getDayOfMonth())
                    .end_date_year(end_date.getYear())
                    .end_date_month(end_date.getMonthValue())
                    .end_date_day(end_date.getDayOfMonth())
                    .build();

            getGoalDtos.add(dto);
        }

        return getGoalDtos;
    }

    public String completeGoal(Integer goalId) {

        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new CustomException(ErrorCode.NO_GOAL_ERROR, ErrorCode.NO_GOAL_ERROR.getMessage()));

        goal.toggleCompleted();
        goalRepository.save(goal);

        return "Goal successfully completed.";
    }
}

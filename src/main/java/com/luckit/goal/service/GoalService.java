package com.luckit.goal.service;

import com.luckit.global.exception.CustomException;
import com.luckit.global.exception.code.ErrorCode;
import com.luckit.goal.controller.dto.*;
import com.luckit.goal.domain.Goal;
import com.luckit.goal.domain.GoalRepository;
import com.luckit.todo.domain.Todo;
import com.luckit.todo.domain.TodoRepository;
import com.luckit.user.domain.User;
import com.luckit.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class GoalService {

    private final TodoRepository todoRepository;
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


    public List<GetGoalMypageDto> getGoalMypage(Integer userId) {

        List<Goal> goalList = goalRepository.findAllByUser_UserId(userId);

        List<GetGoalMypageDto> getGoalMypageDtos = new ArrayList<>();

        for (Goal goal : goalList) {
            int countSuccessTodo = todoRepository.countCompletedTodosByGoalId(goal.getId());

            GetGoalMypageDto dto = GetGoalMypageDto.builder()
                    .goalId(goal.getId())
                    .name(goal.getName())
                    .countSuccessTodo(countSuccessTodo)
                    .build();

            getGoalMypageDtos.add(dto);
        }

        return getGoalMypageDtos;


    }


    public GetEachGoalMypageDto getEachGoalMypage(Integer goalId) {

        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new CustomException(ErrorCode.NO_GOAL_ERROR, ErrorCode.NO_GOAL_ERROR.getMessage()));

        List<Todo> completeTodoList = todoRepository.findCompletedTodosByGoalId(goalId);

        List<Integer> countAnimals = new ArrayList<>();

        for (int i = 0; i < 12; i++) {
            countAnimals.add(0);
        }

        for (Todo todo : completeTodoList) {
            int index = todo.getAnimal() - 1;
            countAnimals.set(index, countAnimals.get(index) + 1);
        }

        LocalDate start_date = goal.getStartDate();
        LocalDate end_date = goal.getEndDate();

        return GetEachGoalMypageDto.builder()
                .start_date_year(start_date.getYear())
                .start_date_month(start_date.getMonthValue())
                .start_date_day(start_date.getDayOfMonth())
                .end_date_year(end_date.getYear())
                .end_date_month(end_date.getMonthValue())
                .end_date_day(end_date.getDayOfMonth())
                .countAnimals(countAnimals)
                .build();
    }

    public CompleteGoalDto completeGoal(Integer goalId) {

        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new CustomException(ErrorCode.NO_GOAL_ERROR, ErrorCode.NO_GOAL_ERROR.getMessage()));

        List<Todo> completeTodoList = todoRepository.findCompletedTodosByGoalId(goal.getId());

        goal.toggleCompleted();
        goalRepository.save(goal);

        LocalDate start_date = goal.getStartDate();
        LocalDate end_date = goal.getEndDate();

        List<Integer> countAnimals = new ArrayList<>();

        for (int i = 0; i < 12; i++) {
            countAnimals.add(0);
        }

        for (Todo todo : completeTodoList) {
            int index = todo.getAnimal() - 1;
            countAnimals.set(index, countAnimals.get(index) + 1);
        }

        int maxValue = Collections.max(countAnimals); // 최대값 찾기

        List<Integer> maxIndexes = new ArrayList<>();
        for (int i = 0; i < countAnimals.size(); i++) {
            if (countAnimals.get(i) == maxValue) {
                maxIndexes.add(i);
            }
        }

        Random random = new Random();
        int randomIndex = random.nextInt(maxIndexes.size());
        int randomElement = maxIndexes.get(randomIndex);

        return CompleteGoalDto.builder()
                .evolvedAnimals(randomElement+1)
                .start_date_year(start_date.getYear())
                .start_date_month(start_date.getMonthValue())
                .start_date_day(start_date.getDayOfMonth())
                .end_date_year(end_date.getYear())
                .end_date_month(end_date.getMonthValue())
                .end_date_day(end_date.getDayOfMonth())
                .countSuccessTodo(completeTodoList.size())
                .countAnimals(countAnimals)
                .build();
    }

    public String deleteGoal(Integer goalId) {

        List<Todo> todos = todoRepository.findAllByGoalId(goalId);
        todoRepository.deleteAll(todos);
        goalRepository.deleteById(goalId);

        return "Goal successfully deleted.";
    }

}

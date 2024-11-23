package com.luckit.todo.service;

import com.luckit.fortune.api.dto.response.UserMissionResDto;
import com.luckit.fortune.application.FortuneService;
import com.luckit.global.exception.CustomException;
import com.luckit.global.exception.code.ErrorCode;
import com.luckit.goal.domain.Goal;
import com.luckit.goal.domain.GoalRepository;
import com.luckit.todo.controller.dto.AddTodoDto;
import com.luckit.todo.controller.dto.GetTodoDto;
import com.luckit.todo.controller.dto.UpdateTodoDto;
import com.luckit.todo.domain.Todo;
import com.luckit.todo.domain.TodoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class TodoService {

    private final GoalRepository goalRepository;
    private final TodoRepository todoRepository;
    private final FortuneService fortuneService;

    public String addTodo(AddTodoDto addTodoDto) {

        Goal goal = goalRepository.findById(addTodoDto.goalId())
                .orElseThrow(() -> new CustomException(ErrorCode.NO_GOAL_ERROR, ErrorCode.NO_GOAL_ERROR.getMessage()));


        Random random = new Random();
        int randomAnimal = random.nextInt(12) + 1;
        int score = random.nextInt(5) + 1;

        UserMissionResDto.FortuneType[] values = UserMissionResDto.FortuneType.values();
        int randomIndex = random.nextInt(values.length);
        UserMissionResDto.FortuneType fortuneType = values[randomIndex];


        Todo todo = Todo.builder()
                .goal(goal)
                .name(addTodoDto.name())
                .isCompleted(false)
                .isMadeByGpt(false)
                .animal(randomAnimal)
                .fortuneType(String.valueOf(fortuneType))
                .score(score)
                .build();

        todoRepository.save(todo);

        return "Todo successfully saved.";
    }

    // 무작위로 FortuneType을 선택하는 메서드
    private UserMissionResDto.FortuneType getRandomFortuneType(Random random) {
        UserMissionResDto.FortuneType[] types = UserMissionResDto.FortuneType.values(); // 모든 FortuneType 가져오기
        return types[random.nextInt(types.length)]; // 무작위로 하나 선택
    }

    public List<GetTodoDto> getTodo(Principal principal, Integer goalId) {

        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new CustomException(ErrorCode.NO_GOAL_ERROR, ErrorCode.NO_GOAL_ERROR.getMessage()));

        if (!todoRepository.existsByGoal_IdAndIsMadeByGptTrueAndDate(goalId, LocalDate.now())) {
            Random random = new Random(); // Random 객체 생성

            List<UserMissionResDto> missionResDtos = (List<UserMissionResDto>) fortuneService.createDailyMission(principal);
            for (UserMissionResDto missionResDto : missionResDtos) {

                int randomAnimal = random.nextInt(12) + 1;
                int score = random.nextInt(5) + 1;

                UserMissionResDto.FortuneType[] values = UserMissionResDto.FortuneType.values();
                int randomIndex = random.nextInt(values.length);
                UserMissionResDto.FortuneType fortuneType = values[randomIndex];

                Todo todo = Todo.builder()
                        .goal(goal)
                        .name(missionResDto.missionName())
                        .isCompleted(false)
                        .isMadeByGpt(true)
                        .fortuneType(fortuneType.name())
                        .animal(randomAnimal)
                        .score(score)
                        .date(LocalDate.now())
                        .build();

                todoRepository.save(todo);
            }
        }

        List<Todo> todoList = todoRepository.findAllByGoalId(goalId);

        List<GetTodoDto> getTodoDtos = new ArrayList<>();

        for (Todo todo : todoList) {

            LocalDate date = todo.getDate();

            GetTodoDto dto = GetTodoDto.builder()
                    .todoId(todo.getId())
                    .year(date.getYear())
                    .month(date.getMonthValue())
                    .day(date.getDayOfMonth())
                    .name(todo.getName())
                    .isCompleted(todo.isCompleted())
                    .isMadeByGpt(todo.isMadeByGpt())
                    .score(todo.getScore())
                    .fortuneType(UserMissionResDto.FortuneType.valueOf(todo.getFortuneType()))
                    .animal(todo.getAnimal())
                    .build();

            getTodoDtos.add(dto);
        }

        return getTodoDtos;
    }

    public String completeTodo(Integer todoId) {

        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new CustomException(ErrorCode.NO_TODO_ERROR, ErrorCode.NO_TODO_ERROR.getMessage()));

        todo.toggleCompleted();
        todoRepository.save(todo);

        return "Todo successfully completed.";
    }

    public String deleteTodo(Integer todoId) {

        todoRepository.deleteById(todoId);

        return "Todo successfully deleted.";
    }

    public List<Integer> getTodoGraph(Integer userId, int year, int month) {

        List<Integer> days = List.of(1, 5, 10, 15, 20, 25, 30);
        List<Integer> result = new ArrayList<>();

        for (int day : days) {
            try {
                LocalDate date = LocalDate.of(year, month, day);
                int count = todoRepository.countCompletedTodosByUserIdAndDate(userId, date);
                result.add(count);
            } catch (Exception e) {
                // 날짜가 안맞으면 (예를 들어, 2월은 30일 없으니깐)
                result.add(0);
            }
        }

        return result;
    }

    public String updateTodo(UpdateTodoDto updateTodoDto) {

        Todo todo = todoRepository.findById(updateTodoDto.todoId())
                .orElseThrow(() -> new CustomException(ErrorCode.NO_TODO_ERROR, ErrorCode.NO_TODO_ERROR.getMessage()));

        todo.updateName(updateTodoDto.name());

        todoRepository.save(todo);

        return "Todo successfully updated.";
    }
}

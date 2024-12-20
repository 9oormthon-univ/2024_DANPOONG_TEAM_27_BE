package com.luckit.user.service;

import com.luckit.global.dto.UserInfo;
import com.luckit.global.exception.CustomException;
import com.luckit.global.exception.code.ErrorCode;
import com.luckit.user.controller.dto.CreateUserDto;
import com.luckit.user.domain.User;
import com.luckit.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserInfo getUserInfo(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.NO_USER_INFO, ErrorCode.NO_USER_INFO.getMessage()));

        return UserInfo.builder()
                .name(user.getName())
                .email(user.getEmail())
                .profileImageUrl(user.getProfileImage())
                .build();
    }

    public String createUserFortune(Integer id, CreateUserDto requestDto) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.NO_USER_INFO, ErrorCode.NO_USER_INFO.getMessage()));

        // 2. CreateUserRequestDto에서 사용자 정보를 추출
        String gender = requestDto.gender();
        String solarOrLunar = requestDto.solarOrLunar();
        LocalDateTime dateOfBirth = LocalDateTime.of(
                requestDto.year(),
                requestDto.month(),
                requestDto.day(),
                requestDto.hour(),
                requestDto.minute()
        );

        // 3. 사용자 정보 업데이트
        user = user.toBuilder()
                .gender(gender)
                .solarOrLunar(solarOrLunar)
                .date_of_birth(dateOfBirth)
                .unknownTime(requestDto.unknownTime())
                .build();

        // 4. 사용자 정보 저장
        userRepository.save(user);

        return "User information successfully saved.";
    }

    public CreateUserDto getUserFortune(Integer id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.NO_USER_INFO, ErrorCode.NO_USER_INFO.getMessage()));


        LocalDateTime dateOfBirth = user.getDate_of_birth();

        return CreateUserDto.builder()
                .gender(user.getGender())
                .solarOrLunar(user.getSolarOrLunar())
                .year(dateOfBirth.getYear())
                .month(dateOfBirth.getMonthValue())
                .day(dateOfBirth.getDayOfMonth())
                .hour(dateOfBirth.getHour())
                .minute(dateOfBirth.getMinute())
                .unknownTime(user.isUnknownTime())
                .build();
    }
}

package com.luckit.user.service;

import com.luckit.user.controller.dto.CreateUserRequestDto;
import com.luckit.user.controller.dto.UserInfoDto;
import com.luckit.user.domain.User;
import com.luckit.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;


    public UserInfoDto createUser(String email, CreateUserRequestDto requestDto) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> User)

    }
}

package com.floodguard.backend.service.impl;

import com.floodguard.backend.dto.UserDTO;
import com.floodguard.backend.exception.CustomException;
import com.floodguard.backend.model.User;
import com.floodguard.backend.repository.UserRepository;
import com.floodguard.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDTO.Response getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));

        return UserDTO.Response.builder()
                .userId(user.getUserId())
                .fullName(user.getFullName())
                .email(user.getUsername())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole().name())
                .priorityLevel(0)
                .addressGroup(user.getManagementAreaCode())
                .reportsCount(0)
                .build();
    }
}

package com.floodguard.backend.controller;

import com.floodguard.backend.dto.UserDTO;
import com.floodguard.backend.response.ApiResponse;
import com.floodguard.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserDTO.Response>> getCurrentUser() {
        return ResponseEntity
                .ok(ApiResponse.success("Get current user info successfully", userService.getCurrentUser()));
    }
}

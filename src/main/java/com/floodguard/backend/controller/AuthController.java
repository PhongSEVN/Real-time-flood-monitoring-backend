package com.floodguard.backend.controller;

import com.floodguard.backend.dto.AuthDTO;
import com.floodguard.backend.response.ApiResponse;
import com.floodguard.backend.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;

    /**
     * Đăng nhập bằng Google ID Token
     * POST /api/auth/google
     */
    @PostMapping("/google")
    public ResponseEntity<ApiResponse<AuthDTO.AuthResponse>> loginWithGoogle(
            @Valid @RequestBody AuthDTO.GoogleLoginRequest request) {
        AuthDTO.AuthResponse response = authService.loginWithGoogle(request);
        String message = response.getUser().isNewUser()
                ? "Account created and logged in successfully"
                : "Logged in successfully";
        return ResponseEntity.ok(ApiResponse.success(message, response));
    }

    /**
     * Đăng nhập bằng email/password
     * POST /api/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthDTO.AuthResponse>> login(
            @Valid @RequestBody AuthDTO.LoginRequest request) {
        AuthDTO.AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Logged in successfully", response));
    }

    /**
     * Đăng ký tài khoản mới
     * POST /api/auth/register
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthDTO.AuthResponse>> register(
            @Valid @RequestBody AuthDTO.RegisterRequest request) {
        AuthDTO.AuthResponse response = authService.register(request);
        return ResponseEntity.ok(ApiResponse.success("Account registered successfully", response));
    }
}

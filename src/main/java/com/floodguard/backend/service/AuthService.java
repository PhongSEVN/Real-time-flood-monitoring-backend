package com.floodguard.backend.service;

import com.floodguard.backend.dto.AuthDTO;

public interface AuthService {

    /**
     * Đăng nhập bằng Google ID Token
     * Nếu user chưa tồn tại sẽ tự động tạo mới
     */
    AuthDTO.AuthResponse loginWithGoogle(AuthDTO.GoogleLoginRequest request);

    /**
     * Đăng nhập bằng email/password
     */
    AuthDTO.AuthResponse login(AuthDTO.LoginRequest request);

    /**
     * Đăng ký tài khoản mới
     */
    AuthDTO.AuthResponse register(AuthDTO.RegisterRequest request);
}

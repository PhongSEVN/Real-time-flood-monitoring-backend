package com.floodguard.backend.service.impl;

import com.floodguard.backend.dto.AuthDTO;
import com.floodguard.backend.exception.CustomException;
import com.floodguard.backend.model.User;
import com.floodguard.backend.model.UserRole;
import com.floodguard.backend.repository.UserRepository;
import com.floodguard.backend.config.JwtUtil;
import com.floodguard.backend.service.AuthService;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @Value("${google.client-id}")
    private String googleClientId;

    @Override
    public AuthDTO.AuthResponse loginWithGoogle(AuthDTO.GoogleLoginRequest request) {
        try {
            // Verify Google ID Token
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(), GsonFactory.getDefaultInstance())
                    .setAudience(Collections.singletonList(googleClientId))
                    .build();

            GoogleIdToken idToken = verifier.verify(request.getIdToken());

            if (idToken == null) {
                throw new CustomException("Invalid Google ID token", HttpStatus.UNAUTHORIZED);
            }

            GoogleIdToken.Payload payload = idToken.getPayload();
            String email = payload.getEmail();
            String fullName = (String) payload.get("name");

            // Kiểm tra user đã tồn tại chưa
            Optional<User> existingUser = userRepository.findByUsername(email);
            boolean isNewUser = existingUser.isEmpty();

            User user;
            if (isNewUser) {
                // Tạo user mới
                user = User.builder()
                        .username(email)
                        .fullName(fullName != null ? fullName : email.split("@")[0])
                        .role(UserRole.RESIDENT)
                        .build();
                user = userRepository.save(user);
                log.info("Created new user from Google login: {}", email);
            } else {
                user = existingUser.get();
                log.info("Existing user logged in with Google: {}", email);
            }

            // Tạo JWT token
            Map<String, Object> claims = new HashMap<>();
            claims.put("userId", user.getUserId());
            claims.put("role", user.getRole().name());
            String token = jwtUtil.generateToken(email, claims);

            return AuthDTO.AuthResponse.builder()
                    .accessToken(token)
                    .tokenType("Bearer")
                    .expiresIn(jwtUtil.getExpiration())
                    .user(mapToUserInfo(user, isNewUser))
                    .build();

        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            log.error("Google login error", e);
            throw new CustomException("Failed to verify Google token: " + e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }

    @Override
    public AuthDTO.AuthResponse login(AuthDTO.LoginRequest request) {
        // Tìm user theo số điện thoại
        User user = userRepository.findByPhoneNumber(request.getPhoneNumber())
                .orElseThrow(() -> new CustomException("Invalid phone number or password", HttpStatus.UNAUTHORIZED));

        if (user.getPasswordHash() == null ||
                !passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new CustomException("Invalid phone number or password", HttpStatus.UNAUTHORIZED);
        }

        // Tạo JWT token - dùng phoneNumber làm subject
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getUserId());
        claims.put("role", user.getRole().name());
        String token = jwtUtil.generateToken(user.getPhoneNumber(), claims);

        return AuthDTO.AuthResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .expiresIn(jwtUtil.getExpiration())
                .user(mapToUserInfo(user, false))
                .build();
    }

    @Override
    public AuthDTO.AuthResponse register(AuthDTO.RegisterRequest request) {
        // Kiểm tra số điện thoại đã tồn tại chưa
        if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new CustomException("Phone number already registered", HttpStatus.BAD_REQUEST);
        }

        // Kiểm tra email đã tồn tại chưa
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new CustomException("Email already registered", HttpStatus.BAD_REQUEST);
        }

        // Parse role from request or default to RESIDENT
        UserRole userRole = UserRole.RESIDENT;
        if (request.getRole() != null) {
            try {
                userRole = UserRole.valueOf(request.getRole().toUpperCase());
            } catch (IllegalArgumentException e) {
                userRole = UserRole.RESIDENT;
            }
        }

        // Tạo user mới - dùng phoneNumber làm username
        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .username(request.getPhoneNumber()) // Dùng SĐT làm username để đảm bảo unique
                .phoneNumber(request.getPhoneNumber())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(userRole)
                .managementAreaCode(request.getAddressGroup())
                .build();

        user = userRepository.save(user);
        log.info("New user registered: {}", request.getPhoneNumber());

        // Tạo JWT token
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getUserId());
        claims.put("role", user.getRole().name());
        String token = jwtUtil.generateToken(user.getPhoneNumber(), claims);

        return AuthDTO.AuthResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .expiresIn(jwtUtil.getExpiration())
                .user(mapToUserInfo(user, true))
                .build();
    }

    private AuthDTO.UserInfo mapToUserInfo(User user, boolean isNewUser) {
        return AuthDTO.UserInfo.builder()
                .userId(user.getUserId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole().name())
                .priorityLevel(0)
                .addressGroup(user.getManagementAreaCode())
                .isNewUser(isNewUser)
                .build();
    }
}

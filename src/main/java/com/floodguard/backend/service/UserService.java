package com.floodguard.backend.service;

import com.floodguard.backend.dto.UserDTO;

public interface UserService {
    UserDTO.Response getCurrentUser();
}

package com.event.ems.service;

import com.event.ems.dto.AuthResponse;
import com.event.ems.dto.LoginRequest;
import com.event.ems.dto.RegisterRequest;
import com.event.ems.entity.User;

public interface UserService {

    User registerUser(RegisterRequest request);

    AuthResponse login(LoginRequest request);
}

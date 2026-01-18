package com.event.ems.service;

import com.event.ems.dto.UserResponse;
import java.util.List;

public interface AdminUserService {

    List<UserResponse> getAllUsers();

    void changeUserStatus(Long userId, boolean enabled);

    void makeOrganizer(Long userId);

    void removeOrganizer(Long userId);

    void deleteUser(Long userId);
}

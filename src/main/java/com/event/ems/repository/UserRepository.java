package com.event.ems.repository;

import com.event.ems.entity.RoleName;
import com.event.ems.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    // ✅ NEW — count users by role name
    long countByRoles_Name(RoleName roleName);
}
package com.event.ems.dto;

import jakarta.validation.constraints.NotBlank;

public class LoginRequest {

    @NotBlank
    private String email;

    @NotBlank
    private String password;

    public LoginRequest() {}

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

/*VIVA- for JWT- login token
We implemented JWT-based authentication where user credentials are verified, a token is generated on login, and role-based authorization is handled using Spring Security filters.
* */

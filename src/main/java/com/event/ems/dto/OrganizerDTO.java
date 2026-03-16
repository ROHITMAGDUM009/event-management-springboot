package com.event.ems.dto;

public class OrganizerDTO {

    private Long id;
    private String fullName;
    private String email;
    private boolean enabled;

    public OrganizerDTO(Long id, String fullName, String email, boolean enabled) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.enabled = enabled;
    }

    public Long getId() { return id; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public boolean isEnabled() { return enabled; }
}
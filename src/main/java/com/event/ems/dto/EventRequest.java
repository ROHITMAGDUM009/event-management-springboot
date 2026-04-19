package com.event.ems.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import java.time.LocalDate;

public class EventRequest {

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    @NotBlank(message = "Location is required")
    private String location;

    @NotNull(message = "Event date is required")
    private LocalDate eventDate;

    @NotBlank(message = "Event type is required")
    private String eventType;

    private Double price;
    private String approvalType;
    private String imageUrl;

    // ✅ SEAT MANAGEMENT
    private Boolean hasSeatLimit = false;
    private Integer totalSeats;

    // Getters & Setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public LocalDate getEventDate() { return eventDate; }
    public void setEventDate(LocalDate eventDate) { this.eventDate = eventDate; }
    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    public String getApprovalType() { return approvalType; }
    public void setApprovalType(String approvalType) { this.approvalType = approvalType; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public Boolean getHasSeatLimit() { return hasSeatLimit; }
    public void setHasSeatLimit(Boolean hasSeatLimit) { this.hasSeatLimit = hasSeatLimit; }
    public Integer getTotalSeats() { return totalSeats; }
    public void setTotalSeats(Integer totalSeats) { this.totalSeats = totalSeats; }
}
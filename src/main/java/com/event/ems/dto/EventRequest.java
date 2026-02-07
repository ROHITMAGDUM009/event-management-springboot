package com.event.ems.dto;

import java.time.LocalDate;

public class EventRequest {

    private String title;
    private String description;
    private String location;
    private LocalDate eventDate;

    private String eventType;       // FREE / PAID
    private Double price;

    private String approvalType;    // AUTO / MANUAL

    public String getTitle() {
        return this.title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public String getLocation() {
        return this.location;
    }

    public void setLocation(final String location) {
        this.location = location;
    }

    public LocalDate getEventDate() {
        return this.eventDate;
    }

    public void setEventDate(final LocalDate eventDate) {
        this.eventDate = eventDate;
    }

    public String getEventType() {
        return this.eventType;
    }

    public void setEventType(final String eventType) {
        this.eventType = eventType;
    }

    public Double getPrice() {
        return this.price;
    }

    public void setPrice(final Double price) {
        this.price = price;
    }

    public String getApprovalType() {
        return this.approvalType;
    }

    public void setApprovalType(final String approvalType) {
        this.approvalType = approvalType;
    }
}

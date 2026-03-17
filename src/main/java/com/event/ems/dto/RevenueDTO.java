package com.event.ems.dto;

public class RevenueDTO {

    private double totalRevenue;

    public RevenueDTO(double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public double getTotalRevenue() {
        return totalRevenue;
    }
}
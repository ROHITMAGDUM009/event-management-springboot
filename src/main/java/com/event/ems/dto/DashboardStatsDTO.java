package com.event.ems.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsDTO {

    private long totalUsers;
    private long totalOrganizers;
    private long totalEvents;
    private long pendingEvents;
    private long totalBookings;
    private double totalRevenue;
}
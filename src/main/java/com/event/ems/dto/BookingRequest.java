package com.event.ems.dto;

public class BookingRequest {
    private Long eventId;
    private Integer ticketQuantity = 1;

    public Long getEventId() { return eventId; }
    public void setEventId(Long eventId) { this.eventId = eventId; }
    public Integer getTicketQuantity() { return ticketQuantity; }
    public void setTicketQuantity(Integer ticketQuantity) { this.ticketQuantity = ticketQuantity; }
}
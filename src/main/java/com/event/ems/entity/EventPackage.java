package com.event.ems.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "event_packages")
public class EventPackage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // Silver, Gold, VIP

    private Double price;

    private String benefits;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    // getters & setters
    public Long getId() {
        return this.id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public Double getPrice() {
        return this.price;
    }

    public void setPrice(final Double price) {
        this.price = price;
    }

    public String getBenefits() {
        return this.benefits;
    }

    public void setBenefits(final String benefits) {
        this.benefits = benefits;
    }

    public Event getEvent() {
        return this.event;
    }

    public void setEvent(final Event event) {
        this.event = event;
    }
}

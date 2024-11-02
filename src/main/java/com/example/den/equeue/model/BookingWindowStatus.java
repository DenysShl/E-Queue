package com.example.den.equeue.model;

public record BookingWindowStatus(
        boolean bookingOpen,
        long secondLeftCurrentPhase,
        long serverTime
) {
}

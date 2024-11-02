package com.example.den.equeue.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.time.LocalTime;

@RedisHash("booking_slot")
public record BookingSlot(
        @Id String id,
        LocalTime startTime,
        LocalTime endTime
) {
}

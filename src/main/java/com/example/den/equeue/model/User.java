package com.example.den.equeue.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;


@RedisHash("USERS")
public record User(
        @Id String id,
        String username) {
}

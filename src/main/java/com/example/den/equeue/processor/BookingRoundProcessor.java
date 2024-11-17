package com.example.den.equeue.processor;

import com.example.den.equeue.model.BookingSlot;
import com.example.den.equeue.repository.BookingSlotRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class BookingRoundProcessor {

    private final BookingSlotRepository bookingSlotRepository;
    private final StringRedisTemplate stringRedisTemplate;
    private final RedisTemplate redisTemplate;

    @Scheduled(cron = "${app.booking-round-cron}")
    public void processBookingRound() {
        log.info("Processing booking round...");
        final Boolean lockAcquired = redisTemplate.opsForValue()
                .setIfAbsent("bookingRoundLock", "locked", Duration.ofSeconds(10));

        if (Boolean.TRUE.equals(lockAcquired)) {
            log.info("Lock acquired, processing booking round...");
            try {
                Iterable<BookingSlot> all = bookingSlotRepository.findAll();
                for (BookingSlot bookingSlot : all) {
                    log.info("Slot: {}", bookingSlot);
                    final String slotKey = "slot:" + bookingSlot.id() + ":users";
                    final Set<String> members = redisTemplate.opsForSet().members(slotKey);

                    if (CollectionUtils.isEmpty(members)) {
                        log.info("No users in slot {}", bookingSlot.id());
                        continue;
                    } else {
                        log.info("Users in slot {} are: {}", bookingSlot.id(), members.size());
                        final List<String> users = new ArrayList<>(members);
                        Collections.shuffle(users, new java.util.Random());
                        final String winnerUser = users.getFirst();

                        log.info("Slot {} winner: {}", bookingSlot.id(), winnerUser);
                        redisTemplate.opsForValue().set("slot:" + bookingSlot.id() + ":winner", winnerUser, Duration.ofSeconds(30));
                        bookingSlotRepository.delete(bookingSlot);
                        redisTemplate.delete(slotKey);
                    }
                }
                redisTemplate.delete("booked_users");

            } finally {
                redisTemplate.delete("bookingRoundLock");
            }
        } else {
            log.info("Lock not acquired, skipping processing booking round...");
        }
    }
}

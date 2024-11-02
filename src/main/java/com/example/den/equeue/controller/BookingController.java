package com.example.den.equeue.controller;

import com.example.den.equeue.model.BookingSlot;
import com.example.den.equeue.model.BookingWindowStatus;
import com.example.den.equeue.repository.BookingSlotRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.StreamSupport;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/booking")
public class BookingController {

    private final BookingSlotRepository bookingSlotRepository;
    private final StringRedisTemplate stringRedisTemplate;
    private final RedisTemplate redisTemplate;

    @GetMapping("/slots")
    public List<BookingSlot> getBookings() {
        log.info("Getting all bookings");
        Iterable<BookingSlot> all = bookingSlotRepository.findAll();
        return StreamSupport.stream(all.spliterator(), false)
                .sorted(Comparator.comparing(BookingSlot::startTime))
                .toList();
    }

    @DeleteMapping("/slots/{id}")
    public void bookSlot(final String id) {
        log.info("Booking slot {}", id);
        BookingSlot bookingSlot = bookingSlotRepository.findById(id).orElseThrow();
        bookingSlotRepository.delete(bookingSlot);
    }

    @PostMapping("/book")
    public ResponseEntity<?> bookSlot(@RequestParam String slotId,
                                      @AuthenticationPrincipal OAuth2User principal) {
        final String userId = principal.getAttribute("id").toString();
        log.info("User {} is booking slot {}", userId, slotId);

        final Boolean hasBooked = stringRedisTemplate.opsForSet()
                .isMember("booked_users", userId);

        if (Boolean.TRUE.equals(hasBooked)) {
            log.info("User {} has already booked slot {}", userId, slotId);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Already booked");
        }

        stringRedisTemplate.opsForSet().add("booked_users", userId);

        final String slotKey = "slot:" + slotId + ":users";
        stringRedisTemplate.opsForSet().add(slotKey, userId);
        log.info("User {} booked slot {}", userId, slotId);

        return ResponseEntity.ok(slotId);
    }

    @GetMapping("/hasBooked")
    public ResponseEntity<?> hasBooked(@AuthenticationPrincipal OAuth2User principal) {
        final String userId = principal.getAttribute("id").toString();
        final Boolean hasBooked = stringRedisTemplate.opsForSet()
                .isMember("booked_users", userId);
        final Map<String, Object> response = Map.of("hasBooked", Boolean.TRUE.equals(hasBooked));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status")
    public BookingWindowStatus getBookingStatus(@AuthenticationPrincipal OAuth2User principal) {

        return null;
    }

}
package com.example.den.equeue.initializer;

import com.example.den.equeue.model.BookingSlot;
import com.example.den.equeue.repository.BookingSlotRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer {

    private final StringRedisTemplate stringRedisTemplate;
    private final BookingSlotRepository bookingSlotRepository;

    @EventListener(ApplicationReadyEvent.class)
    public void startUp() {
        log.info("Initializing data...");
        initializeBookingSlots();
    }

    private void initializeBookingSlots() {
        if (isInitialized()) {
            log.info("Booking slots already initialized");
        } else {
            log.info("Initializing booking slots...");
            final LocalTime startTime = LocalTime.of(9, 0);
            final LocalTime endTime = LocalTime.of(18, 0);

            for (LocalTime slot = startTime; slot.isBefore(endTime); slot = slot.plusMinutes(30)) {
                final BookingSlot bookingSlot = new BookingSlot(slot.toString(), slot, slot.plusMinutes(30));
                log.info("Slot: {} - bookingSlot: {}", slot);

                bookingSlotRepository.save(bookingSlot);
            }

            stringRedisTemplate.opsForValue()
                    .set("bookingSlots.initialized", "true");
            log.info("Booking slots initialized");
        }
    }

    private boolean isInitialized() {
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey("bookingSlots.initialized"));
    }
}

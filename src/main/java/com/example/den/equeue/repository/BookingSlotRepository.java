package com.example.den.equeue.repository;

import com.example.den.equeue.model.BookingSlot;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingSlotRepository extends CrudRepository<BookingSlot, String> {
}

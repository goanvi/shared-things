package se.itmo.ru.bookings.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import se.itmo.ru.bookings.entity.Booking

@Repository
interface BookingRepository: JpaRepository<Booking, Int> {
}
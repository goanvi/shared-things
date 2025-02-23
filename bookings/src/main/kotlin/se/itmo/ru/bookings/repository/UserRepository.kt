package se.itmo.ru.bookings.repository

import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import se.itmo.ru.bookings.entity.User
import java.util.*

@Repository
interface UserRepository : ReactiveCrudRepository<User, UUID> {

    fun findByUsername(username: String): Mono<User>

}
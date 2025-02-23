package se.itmo.ru.bookings.service

import org.springframework.http.HttpStatus
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import org.springframework.web.server.ResponseStatusException
import reactor.core.publisher.Mono
import se.itmo.ru.bookings.repository.UserRepository


@Component
class CustomUserDetailsService(
        private val repository: UserRepository
) : ReactiveUserDetailsService {

    override fun findByUsername(username: String): Mono<UserDetails> {
        return repository.findByUsername(username)
                .map { user -> user as UserDetails }
                .switchIfEmpty(Mono.error(
                        ResponseStatusException(HttpStatus.UNAUTHORIZED, "user not found with name :$username")
                ))
    }
}
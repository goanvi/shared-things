package se.itmo.ru.authservice.services

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono
import se.itmo.ru.authservice.models.User
import se.itmo.ru.authservice.repositories.UserRepository
import java.util.UUID


@Service
class UserService(
        private final val userRepository: UserRepository
) {


    @Transactional(readOnly = true)
    fun findById(id: UUID): Mono<User> {
        return userRepository.findById(id)
    }
}
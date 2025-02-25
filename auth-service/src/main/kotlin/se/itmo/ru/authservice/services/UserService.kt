package se.itmo.ru.authservice.services

import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono
import se.itmo.ru.authservice.models.User
import se.itmo.ru.authservice.models.UserRole
import se.itmo.ru.authservice.repositories.UserRepository
import se.itmo.ru.common.dto.request.users.CreateUserRequestDto
import se.itmo.ru.common.dto.response.users.CreateUserResponseDto
import java.time.LocalDateTime
import java.util.UUID


@Service
class UserService(
        private val userRepository: UserRepository,
        private val passwordEncoder: PasswordEncoder,
        private val template: R2dbcEntityTemplate
) {


    @Transactional(readOnly = true)
    fun findById(id: UUID): Mono<User> {
        println("XIXUIXUIXUIXUXIUXIUIXUX")
        return userRepository.findById(id)
    }

    fun create(dto: CreateUserRequestDto): Mono<CreateUserResponseDto> {
        val user = this.template.insert(User::class.java).using(dto.toEntity())
        return user.map { it.toDto() }
    }

    private fun CreateUserRequestDto.toEntity(): User =
            User(
                    id = UUID.randomUUID(),
                    name = username,
                    pass = passwordEncoder.encode(password),
                    role = UserRole.valueOf(role.uppercase()),
                    createdAt = LocalDateTime.now(),
                    updatedAt = LocalDateTime.now(),
            )

    private fun User.toDto(): CreateUserResponseDto =
            CreateUserResponseDto(
                    id = id,
                    username = username,
                    role = role.toString(),
            )


}
package se.itmo.ru.authservice.controllers

import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import se.itmo.ru.authservice.models.User
import se.itmo.ru.authservice.services.AuthService
import se.itmo.ru.common.dto.request.auth.AuthTokenRequestDto
import se.itmo.ru.common.dto.request.auth.ValidateTokenRequestDto
import se.itmo.ru.common.dto.response.auth.AuthTokenResponseDto
import se.itmo.ru.common.dto.response.auth.ValidateTokenResponseDto


@RestController
@RequestMapping("auth")
class AuthController(
        private val authService: AuthService,
        private val authManager: ReactiveAuthenticationManager,
) {
    @PostMapping("/validate")
    fun validate(@Valid @RequestBody dto: ValidateTokenRequestDto): Mono<ResponseEntity<ValidateTokenResponseDto>> {
        println("XUIXUXIUXIUXIXIUXIUXIUXIUXIUXIUXIU")
        return this.authService.validateAndExtractUser(dto.token).map { it ->
            ResponseEntity.ok().body(
                    ValidateTokenResponseDto(
                            it.id,
                            it.role.name,
                    )
            )
        }
    }

    @PostMapping("/login")
    fun login(
            @Valid @RequestBody authTokenRequestDto: AuthTokenRequestDto
    ): Mono<ResponseEntity<AuthTokenResponseDto>> {
        return this.authManager.authenticate(
                UsernamePasswordAuthenticationToken(
                        authTokenRequestDto.username,
                        authTokenRequestDto.password
                )
        ).flatMap { authentication ->
            val user = authentication.principal as User
            val token = authService.generateToken(user)

            Mono.just(
                    ResponseEntity
                            .ok()
                            .body(AuthTokenResponseDto(token))
            )
        }
    }
}
package se.itmo.ru.authservice.controllers

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
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
@Tag(
    name = "Аутентификация"
)
class AuthController(
    private val authService: AuthService,
    private val authManager: ReactiveAuthenticationManager,
) {
    @Operation(
        summary = "Проверка валидности токена",
        description = "Позволяет проверить токен и получить ID пользователя и его роль."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200", description = "Токен валиден",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = ValidateTokenResponseDto::class)
                )]
            ),
            ApiResponse(responseCode = "401", description = "Токен недействителен или истек"),
            ApiResponse(responseCode = "400", description = "Некорректный запрос")
        ]
    )
    @PostMapping("/validate")
    fun validate(@Valid @RequestBody dto: ValidateTokenRequestDto): Mono<ResponseEntity<ValidateTokenResponseDto>> {
        return this.authService.validateAndExtractUser(dto.token).map { it ->
            ResponseEntity.ok().body(
                ValidateTokenResponseDto(
                    it.id,
                    it.role.name,
                )
            )
        }
    }

    @Operation(summary = "Авторизация пользователя", description = "Аутентифицирует пользователя и выдает JWT-токен.")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200", description = "Успешный вход",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = AuthTokenResponseDto::class)
                )]
            ),
            ApiResponse(responseCode = "401", description = "Неверные учетные данные"),
            ApiResponse(responseCode = "400", description = "Некорректный запрос")
        ]
    )
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
package se.itmo.ru.authservice.controllers

import io.swagger.v3.oas.annotations.Hidden
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import se.itmo.ru.authservice.services.UserService
import se.itmo.ru.common.dto.request.users.CreateUserRequestDto
import se.itmo.ru.common.dto.response.auth.ValidateTokenResponseDto
import se.itmo.ru.common.dto.response.users.CreateUserResponseDto


@RestController
@RequestMapping("/users")
@PreAuthorize("hasAuthority('SUPER_VAISER')")
@SecurityRequirement(name = "bearer-key")
@Tag(
    name = "Пользователи"
)
class UserController(
    @Autowired
    private val userService: UserService
) {


    @Hidden
    @GetMapping
    fun findAll(pageable: Pageable) {
    }

    @Hidden
    @GetMapping("/{id}")
    fun findOne(@PathVariable id: String) {
    }

    @PostMapping
    @Operation(
        summary = "Создание пользователя",
        description = "Только Супер пользователь может создать пользователя"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200", description = "Пользователь успешно создан",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = CreateUserResponseDto::class)
                )]
            ),
            ApiResponse(responseCode = "403", description = "Доступ запрещен (не супер пользователь)")
        ]
    )
    fun create(
        @RequestBody dto: CreateUserRequestDto
    ): Mono<ResponseEntity<CreateUserResponseDto>> {
        val res = this.userService.create(dto)
        return res.map { ResponseEntity.ok().body(it) }
    }

    @Hidden
    @PatchMapping("/{id}")
    fun update() {

    }

    @Hidden
    @DeleteMapping("/{id}")
    fun delete() {
    }
}

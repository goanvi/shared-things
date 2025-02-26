package se.itmo.ru.accounts.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.data.domain.Pageable
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import se.itmo.ru.common.dto.AccountDto
import se.itmo.ru.accounts.service.AccountService
import java.util.*

@RestController
@RequestMapping("account")
@Tag(name = "Аккаунты")
class AccountController(
    private val service: AccountService
) {

    @Operation(
        summary = "Создание аккаунта",
        description = "Позволяет создать аккаунт"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200", description = "Аккаунт создан",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = AccountDto::class)
                )]
            ),
            ApiResponse(responseCode = "400", description = "Некорректный запрос"),
            ApiResponse(responseCode = "422", description = "Аккаунт уже создан")
        ]
    )
    @PostMapping("/create")
    fun createAccount(@Valid @RequestBody accountDto: AccountDto): AccountDto =
        service.createAccount(accountDto)

    @Operation(
        summary = "Получение аккаунт по id",
        description = "Возвращает аккаунт с указанным id"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200", description = "Аккаунт успешно получен",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = AccountDto::class)
                )]
            ),
            ApiResponse(responseCode = "400", description = "Некорректный запрос"),
            ApiResponse(responseCode = "404", description = "Аккаунт c таким id не найден")
        ]
    )
    @GetMapping("/{id}")
    fun getAccountById(@PathVariable("id") accountId: UUID): AccountDto =
        service.getAccountById(accountId)

    @Operation(
        summary = "Обновление аккаунта по id",
        description = "Возвращает аккаунт с указанным id"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200", description = "Аккаунт успешно обновлен",
            ),
            ApiResponse(responseCode = "400", description = "Некорректный запрос"),
            ApiResponse(responseCode = "404", description = "Аккаунт c таким id не найден"),
            ApiResponse(responseCode = "422", description = "Некорректные данные")
        ]
    )
    @PutMapping("/{id}")
    fun updateAccount(
        @PathVariable("id") accountId: UUID,
        @Valid @RequestBody accountDto: AccountDto
    ): Unit =
        service.updateAccount(accountId, accountDto)

    //Admin
    @Operation(
        summary = "Получение не проверенные аккаунты (только для админов)",
        description = "Возвращает не проверенные аккаунты (только для админов)"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200", description = "Аккаунты успешно получены",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = AccountDto::class)
                )]
            ),
            ApiResponse(responseCode = "400", description = "Некорректный запрос"),
            ApiResponse(responseCode = "403", description = "Нет доступа"),
            ApiResponse(responseCode = "422", description = "Некорректные данные")
        ]
    )
    @GetMapping("/unmoderated")
    @PreAuthorize("hasAuthority('ADMIN')")
    fun getUnmoderatedAccount(pageable: Pageable): List<AccountDto> =
        service.getAllUnmoderatedAccount(pageable)

    @Operation(
        summary = "Помечает аккаунты как проверенные (только для админов)",
        description = "Возвращает количество проверенных аккаунты (только для админов)"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200", description = "Аккаунты успешно проверены",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = Int::class)
                )]
            ),
            ApiResponse(responseCode = "400", description = "Некорректный запрос"),
            ApiResponse(responseCode = "403", description = "Нет доступа"),
            ApiResponse(responseCode = "422", description = "Некорректные данные")
        ]
    )
    @PostMapping("/moderate")
    @PreAuthorize("hasAuthority('ADMIN')")
    fun setAccountAsModerated(@RequestBody accountIds: Set<UUID>): Int =
        service.setAccountsAsModerated(accountIds)
}
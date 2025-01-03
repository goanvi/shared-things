package se.itmo.ru.accounts.controller

import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*
import se.itmo.ru.accounts.dto.AccountDto
import se.itmo.ru.accounts.service.AccountService

@RestController
@RequestMapping("api/account")
class AccountController(
    private val service: AccountService
) {

    @PostMapping("/create")
    fun createAccount(@Valid @RequestBody accountDto: AccountDto): AccountDto =
        service.createAccount(accountDto)

    @GetMapping("/{id}")
    fun getAccountById(@PathVariable("id") accountId: Int): AccountDto =
        service.getAccountById(accountId)

    @PutMapping("/{id}")
    fun updateAccount(
        @PathVariable("id") accountId: Int,
        @Valid @RequestBody accountDto: AccountDto
    ): Unit =
        service.updateAccount(accountId, accountDto)
}
package se.itmo.ru.accounts.controller

import jakarta.validation.Valid
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.*
import se.itmo.ru.common.dto.AccountDto
import se.itmo.ru.accounts.service.AccountService
import java.util.*

@RestController
@RequestMapping("api/account")
class AccountController(
    private val service: AccountService
) {

    @PostMapping("/create")
    fun createAccount(@Valid @RequestBody accountDto: AccountDto): AccountDto =
        service.createAccount(accountDto)

    @GetMapping("/{id}")
    fun getAccountById(@PathVariable("id") accountId: UUID): AccountDto =
        service.getAccountById(accountId)

    @PutMapping("/{id}")
    fun updateAccount(
        @PathVariable("id") accountId: UUID,
        @Valid @RequestBody accountDto: AccountDto
    ): Unit =
        service.updateAccount(accountId, accountDto)

    //Admin
    @GetMapping("/unmoderated")
    fun getUnmoderatedAccount(pageable: Pageable): List<AccountDto> =
        service.getAllUnmoderatedAccount(pageable)

    @PostMapping("/moderate")
    fun setAccountAsModerated(@RequestBody accountIds: Set<UUID>): Int =
        service.setAccountsAsModerated(accountIds)
}
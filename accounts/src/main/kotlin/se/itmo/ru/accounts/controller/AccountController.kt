package se.itmo.ru.accounts.controller

import jakarta.validation.Valid
import org.springframework.data.domain.Pageable
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import se.itmo.ru.common.dto.AccountDto
import se.itmo.ru.accounts.service.AccountService
import java.util.*

@RestController
@RequestMapping("account")
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
    @PreAuthorize("hasAuthority('ADMIN')")
    fun getUnmoderatedAccount(pageable: Pageable): List<AccountDto> =
        service.getAllUnmoderatedAccount(pageable)

    @PostMapping("/moderate")
    @PreAuthorize("hasAuthority('ADMIN')")
    fun setAccountAsModerated(@RequestBody accountIds: Set<UUID>): Int =
        service.setAccountsAsModerated(accountIds)
}
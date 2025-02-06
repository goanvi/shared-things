package se.itmo.ru.accounts.service

import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import se.itmo.ru.accounts.dto.AccountDto
import se.itmo.ru.accounts.entity.Account
import se.itmo.ru.accounts.provider.AccountRepositoryProvider
import java.util.*

@Service
class AccountService(
    private val accountProvider: AccountRepositoryProvider
) {

    fun createAccount(accountDto: AccountDto): AccountDto {
        accountDto.accountId = UUID.randomUUID()
        accountDto.moderated = false
        return accountDto.toEntity().let {
            accountProvider.saveAccount(it)
        }.toDto()
    }

    fun getAccountById(accountId: UUID): AccountDto =
        accountProvider.getAccountById(accountId).toDto()


    fun getAllUnmoderatedAccount(pageable: Pageable): List<AccountDto> =
        accountProvider.getAllUnmoderatedAccount(pageable).map { it.toDto() }

    fun setAccountsAsModerated(accountIds: Set<UUID>): Int =
        accountProvider.setAccountsAsModerated(accountIds)

    fun updateAccount(accountId: UUID, accountDto: AccountDto): Unit {
        accountDto.moderated = false
        accountDto.toEntity().let {
            accountProvider.updateAccount(accountId, it)
        }
    }


    private fun AccountDto.toEntity(): Account =
        Account(
            accountId = accountId,
            username = username,
            name = name,
            surname = surname,
            email = email,
            moderated = moderated,
        )

    private fun Account.toDto(): AccountDto =
        AccountDto(
            accountId = accountId,
            username = username,
            name = name,
            surname = surname,
            email = email,
            moderated = moderated,
        )
}
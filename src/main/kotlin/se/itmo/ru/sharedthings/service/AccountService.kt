package se.itmo.ru.sharedthings.service

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import se.itmo.ru.sharedthings.dto.AccountDto
import se.itmo.ru.sharedthings.entity.Account
import se.itmo.ru.sharedthings.provider.AccountRepositoryProvider

@Service
class AccountService(
    private val accountProvider: AccountRepositoryProvider
) {

    fun createAccount(accountDto: AccountDto): AccountDto {
        accountDto.accountId = 0
        accountDto.moderated = false
        return accountDto.toEntity().let {
            accountProvider.saveAccount(it)
        }.toDto()
    }

    fun getAccountById(accountId: Int): AccountDto =
        accountProvider.getAccountById(accountId).toDto()


    fun getAllUnmoderatedAccount(pageable: Pageable): List<AccountDto> =
        accountProvider.getAllUnmoderatedAccount(pageable).map { it.toDto() }

    fun setAccountsAsModerated(accountIds: Set<Int>): Int =
        accountProvider.setAccountsAsModerated(accountIds)

    fun updateAccount(accountId: Int, accountDto: AccountDto): Unit {
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
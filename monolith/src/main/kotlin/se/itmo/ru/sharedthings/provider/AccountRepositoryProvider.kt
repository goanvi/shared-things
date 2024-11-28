package se.itmo.ru.sharedthings.provider

import jakarta.persistence.EntityExistsException
import jakarta.persistence.EntityNotFoundException
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import se.itmo.ru.sharedthings.entity.Account
import se.itmo.ru.sharedthings.repository.AccountRepository


@Component
class AccountRepositoryProvider(
    private val accountRepository: AccountRepository
) {
    @Transactional
    fun saveAccount(account: Account): Account {
        return when {
            account.accountId != 0
                    && accountRepository.existsById(account.accountId) ->
                throw EntityExistsException("Account with id ${account.accountId} already exists")

            accountRepository.existsByUsername(account.username) ->
                throw EntityExistsException("Account with username ${account.username} already exists")

            account.email != null
                    && accountRepository.existsByEmail(account.email) ->
                throw EntityExistsException("Account with email ${account.email} already exists")

            else -> accountRepository.save(account)
        }
    }

    @Transactional
    fun updateAccount(accountId: Int, account: Account): Account =
        accountRepository.findById(accountId)
            .orElseThrow { throw EntityNotFoundException() }
            .let {
                when {
                    it.username != account.username
                            && accountRepository.existsByUsername(account.username) ->
                        throw EntityExistsException("Account with username ${account.username} already exists")

                    account.email != null
                            && it.email != account.email
                            && accountRepository.existsByEmail(account.email) ->
                        throw EntityExistsException("Account with email ${account.email} already exists")

                    else -> {
                        account.accountId = accountId
                        accountRepository.save(account)
                    }
                }
            }

    fun getAccountById(accountId: Int): Account =
        accountRepository.findById(accountId)
            .orElseThrow { throw EntityNotFoundException("Account with id $accountId not found") }

    fun getAllUnmoderatedAccount(pageable: Pageable): List<Account> =
        accountRepository.findAllByModerated(false, pageable)

    fun setAccountsAsModerated(accountIds: Set<Int>) =
        accountRepository.setAccountsAsModerated(accountIds)
}

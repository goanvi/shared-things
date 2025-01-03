package se.itmo.ru.accounts.repository

import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import se.itmo.ru.accounts.entity.Account

@Repository
interface AccountRepository : JpaRepository<Account, Int> {

    fun existsByUsername(username: String): Boolean

    fun existsByEmail(email: String): Boolean

    fun findAllByModerated(moderated: Boolean, pageable: Pageable): List<Account>

    @Modifying
    @Query("update Account a set a.moderated = true where a.accountId in (:accountIds)")
    fun setAccountsAsModerated(@Param("accountIds") accountIds: Set<Int>): Int
}
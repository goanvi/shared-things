package se.itmo.ru.sharedthings.repository

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import se.itmo.ru.sharedthings.entity.Account

@Repository
interface AccountRepository: JpaRepository<Account, Int> {

    fun existsByUsername(username: String): Boolean

    fun existsByEmail(email: String): Boolean

    fun findByUsername(username: String): Account?

    fun findAllByModerated(moderated: Boolean, pageable: Pageable): Page<Account>
}
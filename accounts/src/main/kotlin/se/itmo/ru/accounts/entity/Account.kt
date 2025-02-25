package se.itmo.ru.accounts.entity

import jakarta.persistence.*
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.util.*

@Entity
@Table(name = "account")
data class Account(

    @Id
    @Column(name = "account_id")
    @NotNull
    var accountId: UUID,

    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "username", nullable = false, unique = true, length = 100)
    val username: String,

    @Size(min = 1, max = 100)
    @Column(name = "name", length = 100)
    val name: String? = null,

    @Size(min = 1, max = 100)
    @Column(name = "surname", length = 100)
    val surname: String? = null,

    @Size(min = 1, max = 100)
    @Email
    @Column(name = "email", unique = true, length = 100)
    val email: String? = null,

    @NotNull
    @Column(name = "moderated", nullable = false)
    val moderated: Boolean = false
)

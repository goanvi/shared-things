package se.itmo.ru.sharedthings.entity

import jakarta.persistence.*
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

@Entity
@Table(name = "account")
data class Account(

    @Id
    @Column(name = "account_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "account_account_id_seq")
    @SequenceGenerator(name = "account_account_id_seq", allocationSize = 1)
    val accountId: Int? = null,

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

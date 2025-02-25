package se.itmo.ru.sharedthings.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.io.Serializable

data class AccountDto(

    @field:NotNull(message = "account id can't be null")
    var accountId: Int = 0,

    @field:NotBlank(message = "username can't be blank")
    @field:Size(max = 100, message = "username must be between 1 and 100 characters")
    val username: String,

    @field:Size(min = 1, max = 100, message = "name must be between 1 and 100 characters")
    val name: String? = null,

    @field:Size(min = 1, max = 100, message = "surname must be between 1 and 100 characters")
    val surname: String? = null,

    @field:Size(min = 1, max = 100, message = "email must be between 1 and 100 characters")
    @field:Email(message = "email is not valid")
    val email: String? = null,

    @field:NotNull(message = "moderated can't be null")
    var moderated: Boolean = false
): Serializable
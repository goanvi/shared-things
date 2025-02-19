package se.itmo.ru.authservice.models

import org.jetbrains.annotations.NotNull
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.time.LocalDateTime
import java.util.UUID

@Table(name = "users")
data class User(
        @field:Id
        @field:Column("id")
        var id: UUID,

        @field:Column("username")
        @field:NotNull
        var name: String,

        @field:Column("password")
        @field:NotNull
        var pass: String,

        @field:Column("role")
        var role: UserRole = UserRole.USER,

        @field:Column("created_at")
        @field:NotNull
        var createdAt: LocalDateTime = LocalDateTime.now(),

        @field:Column("updated_at")
        @field:NotNull
        var updatedAt: LocalDateTime = LocalDateTime.now(),
) : UserDetails {
    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return mutableListOf(SimpleGrantedAuthority(this.role.name))
    }

    override fun getPassword(): String {
        return this.pass
    }

    override fun getUsername(): String {
        return this.name
    }
}
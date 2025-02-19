package se.itmo.ru.authservice.models

enum class UserRole(name: String) {
    ADMIN("ADMIN"),
    SUPER_VAISER("SUPER_VAISER"),
    USER("USER")
}
package se.itmo.ru.sharedthings.validator

import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import jakarta.validation.constraints.FutureOrPresent
import java.time.LocalDateTime

class FutureDateValidator : ConstraintValidator<FutureOrPresent, LocalDateTime> {
    override fun isValid(value: LocalDateTime?, p1: ConstraintValidatorContext?): Boolean {
        return value?.isAfter(LocalDateTime.now()) ?: false
    }
}
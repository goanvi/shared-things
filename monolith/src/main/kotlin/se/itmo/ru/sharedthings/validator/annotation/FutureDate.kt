package se.itmo.ru.sharedthings.validator.annotation

import jakarta.validation.Constraint
import jakarta.validation.Payload
import se.itmo.ru.sharedthings.validator.FutureDateValidator
import kotlin.reflect.KClass

@Target(AnnotationTarget.FIELD, AnnotationTarget.FUNCTION, AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [FutureDateValidator::class])
annotation class FutureDate(
    val message: String = "Дата и время должны быть в будущем или настоящем",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)
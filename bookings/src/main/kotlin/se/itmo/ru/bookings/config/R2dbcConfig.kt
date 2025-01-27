package se.itmo.ru.bookings.config

import io.r2dbc.spi.ConnectionFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.r2dbc.convert.R2dbcCustomConversions
import org.springframework.data.r2dbc.dialect.DialectResolver
import se.itmo.ru.bookings.utils.BookingStatusToStringConverter
import se.itmo.ru.bookings.utils.ItemStatusToStringConverter
import se.itmo.ru.bookings.utils.StringToBookingStatusConverter
import se.itmo.ru.bookings.utils.StringToItemStatusConverter

@Configuration
class R2dbcConfig {

    @Bean
    fun r2dbcCustomConversions(connectionFactory: ConnectionFactory): R2dbcCustomConversions {
        return R2dbcCustomConversions.of(
            DialectResolver.getDialect(connectionFactory),
            listOf(
                ItemStatusToStringConverter(),
                StringToItemStatusConverter(),
                BookingStatusToStringConverter(),
                StringToBookingStatusConverter()
            )
        )
    }

}
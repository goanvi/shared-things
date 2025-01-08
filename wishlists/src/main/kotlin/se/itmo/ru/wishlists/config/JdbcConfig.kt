package se.itmo.ru.wishlists.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.jdbc.core.convert.JdbcCustomConversions
import org.springframework.data.jdbc.repository.config.AbstractJdbcConfiguration
import se.itmo.ru.wishlists.utils.StringToWishlistStatusConverter
import se.itmo.ru.wishlists.utils.WishlistStatusToStringConverter

@Configuration
class JdbcConfig : AbstractJdbcConfiguration() {

    @Bean
    override fun jdbcCustomConversions(): JdbcCustomConversions {
        return JdbcCustomConversions(
            listOf(
                WishlistStatusToStringConverter(),
                StringToWishlistStatusConverter()
            )
        )
    }
}
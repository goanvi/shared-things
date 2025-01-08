package se.itmo.ru.wishlists.utils

import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import org.springframework.data.convert.WritingConverter
import se.itmo.ru.wishlists.enum.WishlistStatus

@WritingConverter
class WishlistStatusToStringConverter : Converter<WishlistStatus, String> {
    override fun convert(source: WishlistStatus): String {
        return source.name
    }
}

@ReadingConverter
class StringToWishlistStatusConverter : Converter<String, WishlistStatus> {
    override fun convert(source: String): WishlistStatus {
        return WishlistStatus.valueOf(source.uppercase())
    }
}
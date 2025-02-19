package se.itmo.ru.bookings.utils

import org.springframework.core.convert.converter.Converter
import org.springframework.data.convert.ReadingConverter
import org.springframework.data.convert.WritingConverter
import se.itmo.ru.common.BookingStatus
import se.itmo.ru.common.ItemStatus

@WritingConverter
class ItemStatusToStringConverter : Converter<ItemStatus, String> {
    override fun convert(source: ItemStatus): String {
        return source.name
    }
}

@ReadingConverter
class StringToItemStatusConverter : Converter<String, ItemStatus> {
    override fun convert(source: String): ItemStatus {
        return ItemStatus.valueOf(source.uppercase())
    }
}

@WritingConverter
class BookingStatusToStringConverter : Converter<BookingStatus, String> {
    override fun convert(source: BookingStatus): String {
        return source.name
    }
}

@ReadingConverter
class StringToBookingStatusConverter : Converter<String, BookingStatus> {
    override fun convert(source: String): BookingStatus {
        return BookingStatus.valueOf(source.uppercase())
    }
}

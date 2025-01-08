package se.itmo.ru.bookings.entity

import jakarta.persistence.*
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.hibernate.annotations.ColumnDefault
import java.time.LocalDateTime

@Entity
@Table(name = "feedback")
@IdClass(FeedbackKey::class)
data class Feedback(

    @Id
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "item_id", nullable = false)
    val item: Item,

    @Id
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "booking_id", nullable = false)
    val booking: Booking,

    @Size(min = 1 ,max = 300)
    @NotNull
    @Column(name = "title", nullable = false, length = 300)
    val title: String,

    @Column(name = "description")
    val description: String? = null,

    @NotNull
    @Column(name = "date", nullable = false)
    val date: LocalDateTime = LocalDateTime.now(),

    @NotNull
    @Max(10)
    @Min(1)
    @Column(name = "rate", nullable = false)
    val rate: Int,

    @NotNull
    @ColumnDefault("false")
    @Column(name = "moderated", nullable = false)
    val moderated: Boolean = false
)

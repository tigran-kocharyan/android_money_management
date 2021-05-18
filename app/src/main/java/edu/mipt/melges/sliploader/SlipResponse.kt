package edu.mipt.melges.sliploader

import java.time.LocalDateTime

data class SlipResponse(
    val code: SlipState,
    val data: SlipResponseData
)

data class SlipResponseData(
    val json: SlipData
)

data class SlipData(
    val user: String,
    val totalSum: Long,
    val retailPlace: String,
    val dateTime: LocalDateTime,
    val items: List<SlipItem>
)

data class SlipItem(
    val sum: Long,
    val name: String,
    val price: Long,
    val ndsSum: Long,
    val quantity: Long
)

enum class SlipState(val code: Int) {
    INCORRECT_SLIP(0),
    CORRECT_SLIP(1),
    WAITING_DATA(2),
    TOO_MANY_REQUESTS(3),
    WAITING_RETRY(4),
    UNKNOWN_ERROR(5)
}
package ru.totowka.accountant.data.model

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import ru.totowka.accountant.data.type.SlipResponse
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class SlipDocumentData(
    val fiscalStorageNumber: Long, // fn
    val fiscalDocumentNumber: Long, // fd
    val fiscalFeatureNumber: Long, //fp
    val fiscalDateTime: LocalDateTime, // t
    val operationType: Int, // n
    val slipAmount: Long // s * 100
)

class SlipLoaderClient(private val client: HttpClient, private val apiToken: String) {

    companion object {
        const val slipRequestUri = "https://proverkacheka.com/api/v1/check/get"
        val slipDateTimeFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmm")
    }

    suspend fun resolveSlipInfo(slipDocumentData: SlipDocumentData): SlipResponse {
        return client.post(slipRequestUri) {
            body = FormDataContent(Parameters.build {
                append("fn", slipDocumentData.fiscalStorageNumber.toString())
                append("fd", slipDocumentData.fiscalDocumentNumber.toString())
                append("fp", slipDocumentData.fiscalFeatureNumber.toString())
                append("t", slipDocumentData.fiscalDateTime.format(slipDateTimeFormat))
                append("n", slipDocumentData.operationType.toString())
                append(
                    "s",
                    "${(slipDocumentData.slipAmount / 100)}.${slipDocumentData.slipAmount % 100}"
                )
                append("qr", "true")
                append("token", apiToken)
            })
        }
    }
}
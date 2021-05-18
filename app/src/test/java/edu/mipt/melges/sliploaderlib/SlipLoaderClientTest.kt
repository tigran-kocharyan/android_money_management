package edu.mipt.melges.sliploaderlib

import edu.mipt.melges.sliploaderlib.SlipState.CORRECT_SLIP
import edu.mipt.melges.sliploaderlib.TestUtils.Companion.mockHttpClient
import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.client.features.json.*
import io.ktor.client.features.logging.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

import java.time.LocalDateTime

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class SlipLoaderClientTest {
    @Test
    fun shouldParseResponseWhenRequestAndResponseAreCorrect() {
        // given
        val client = mockHttpClient {
            respond(
                TestUtils.resourceAsString(
                    "SlipLoaderClientTest/shouldParseResponseWhenRequestAndResponseAreValid.Response.json",
                ),
                HttpStatusCode.OK,
                headersOf("Content-Type", ContentType.Application.Json.toString())
            )
        }
        val sut = SlipLoaderClient(client, "2224.sMMTJTqgbpkWZihad")

        val result = runBlocking {
            sut.resolveSlipInfo(
                SlipDocumentData(
                    9280440300977714,
                    38232,
                    4083315663,
                    LocalDateTime.of(2021, 5,4, 19,10),
                    1,
                    297000
                )
            )
        }

        assertThat(result)
            .isEqualTo(
                SlipResponse(
                    CORRECT_SLIP,
                    SlipResponseData(
                        SlipData(
                            "ОБЩЕСТВО С ОГРАНИЧЕННОЙ ОТВЕТСТВЕННОСТЬЮ \"ГАЗПРОМНЕФТЬ - ЦЕНТР\"",
                            297000,
                            "КААЗС № 702",
                            LocalDateTime.of(2021, 5, 4, 19, 10,0),
                            listOf(
                                SlipItem(
                                    297000,
                                    "Бензин АИ-95(АИ-95-К5)",
                                    4950,
                                    49500,
                                    60
                                )
                            )
                        )
                    )
                )
            )
    }
}

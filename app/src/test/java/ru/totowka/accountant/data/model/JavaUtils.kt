package ru.totowka.accountant.data.model

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.client.features.json.*
import io.ktor.client.features.logging.*
import java.lang.ClassLoader.getSystemResourceAsStream

class TestUtils {
    companion object {
        fun resourceAsString(resourcePath: String): String {
            return getSystemResourceAsStream(resourcePath)!!.reader().readText()
        }

        fun mockHttpClient(handler: MockRequestHandler): HttpClient {
            return HttpClient(MockEngine) {
                install(JsonFeature) {
                    serializer = JacksonSerializer {
                        registerModule(JavaTimeModule())
                        configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                    }
                }
                install(Logging) {
                    logger = Logger.SIMPLE
                    level = LogLevel.ALL
                }
                engine {
                    addHandler(handler)
                }
            }
        }
    }
}
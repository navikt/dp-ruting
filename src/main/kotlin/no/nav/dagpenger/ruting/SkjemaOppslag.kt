package no.nav.dagpenger.ruting

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.DeserializationFeature
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.jackson.jackson
import kotlinx.coroutines.runBlocking

object SkjemaOppslag {
    private val httpClient =
        HttpClient {
            install(ContentNegotiation) {
                jackson {
                    configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                }
            }
        }

    private data class Skjema(
        @JsonProperty("Skjemanummer")
        val kode: String,
        @JsonProperty("Tittel")
        val tittel: String,
    )

    private data class Response(
        @JsonProperty("Skjemaer")
        val skjemaer: List<Skjema>,
    )

    private val skjemaMap by lazy {
        runBlocking {
            httpClient.get("https://www.nav.no/soknader/api/sanity/skjemautlisting") {
            }.body<Response>().skjemaer
        }.associateBy { it.kode }
    }

    fun finnTittel(skjemaKode: String): String {
        return skjemaMap[skjemaKode]?.tittel ?: throw RuntimeException("Fant ikke skjema med kode $skjemaKode")
    }
}

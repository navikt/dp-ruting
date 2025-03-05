package no.nav.dagpenger.ruting

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText

interface SafClient {
    suspend fun hentJournalpost(journalpostId: String): Journalpost
}

class SafGraphClient(
    url: String,
    tokenProvider: () -> String,
    debug: Boolean = false,
    private val httpClient: HttpClient = httpClient(url, tokenProvider, debug),
) : SafClient {
    companion object {
        fun httpClient(
            url: String,
            tokenProvider: () -> String,
            debug: Boolean,
            engine: HttpClientEngine = CIO.create(),
        ): HttpClient {
            return HttpClient(engine) {
                expectSuccess = true
                defaultRequest {
                    url(url)
                    header("Content-Type", "application/json")
                    header("Authorization", "Bearer ${tokenProvider()}")
                }
                if (debug) {
                    install(Logging) {
                        level = LogLevel.BODY
                        logger =
                            object : Logger {
                                override fun log(message: String) {
                                    println(message)
                                }
                            }
                    }
                }
            }
        }
    }

    override suspend fun hentJournalpost(journalpostId: String): Journalpost {
        return httpClient.post {
            setBody(GraphqlQuery.SafGraphqlQuery(journalpostId).toJson())
        }.let {
            Journalpost(it.bodyAsText())
        }
    }
}

sealed class GraphqlQuery() {
    abstract val query: String
    abstract val variables: Any?

    private val objectMapper =
        jacksonObjectMapper()
            .registerModule(JavaTimeModule())
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)

    fun toJson(): String = objectMapper.writeValueAsString(this)

    class SafGraphqlQuery(
        @JsonIgnore
        val journalpostId: String,
    ) : GraphqlQuery() {
        override val query: String
            get() =
                """
                query(${'$'}journalpostId: String!) {
                    journalpost(journalpostId: ${'$'}journalpostId) {
                        journalstatus
                        journalpostId
                        journalfoerendeEnhet
                        datoOpprettet
                        behandlingstema
                        bruker {
                          type
                          id
                        }
                        relevanteDatoer {
                          dato
                          datotype
                        }
                        dokumenter {
                          tittel
                          dokumentInfoId
                          brevkode
                        }
                    }
                }
                """.trimIndent()
        override val variables: Any
            get() =
                mapOf(
                    "journalpostId" to journalpostId,
                )
    }
}

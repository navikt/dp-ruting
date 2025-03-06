package no.nav.dagpenger.ruting

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.navikt.tbd_libs.rapids_and_rivers.isMissingOrNull
import mu.KotlinLogging
import no.nav.dagpenger.ruting.SkjemaType.Companion.tilSkjemaType

private val logger = KotlinLogging.logger { }

class JournalpostMapper(json: String) {
    companion object {
        private val objectMapper =
            jacksonObjectMapper()
                .registerModule(JavaTimeModule())
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
    }

    private val jsonNode = objectMapper.readTree(json)

    val skjemaType: SkjemaType by lazy { jsonNode.getSkjematype() }
    val id: String by lazy { jsonNode.getJournalpostId() }

    private fun JsonNode.getSkjematype(): SkjemaType {
        return this.at("/data/journalpost/dokumenter").let { dokumenter ->
            when (dokumenter.size()) {
                0 -> throw IllegalArgumentException("Journalpost har ingen dokumenter")
                else -> dokumenter.first()["brevkode"].asText().tilSkjemaType()
            }
        }
    }

    private fun JsonNode.getJournalpostId(): String {
        return this.at("/data/journalpost/journalpostId").let {
            when (it.isMissingOrNull()) {
                true -> throw IllegalArgumentException("Journalpost mangler journalpostId")
                else ->
                    when (it.isTextual) {
                        true -> it.asText()
                        else -> throw IllegalArgumentException("JournalpostId er ikke en tekststreng")
                    }
            }
        }
    }
}

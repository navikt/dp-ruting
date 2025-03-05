package no.nav.dagpenger.ruting

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import no.nav.dagpenger.ruting.SkjemaType.Companion.tilSkjemaType

data class Journalpost(
    val skjemaType: SkjemaType,
) {
    constructor(json: String) : this(
        JournalPostMapper(json),
    )

    private constructor(mapper: JournalPostMapper) : this(
        skjemaType = mapper.skjemaType,
    )
}

class JournalPostMapper(json: String) {
    companion object {
        private val objectMapper =
            jacksonObjectMapper()
                .registerModule(JavaTimeModule())
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
    }

    private val jsonNode = objectMapper.readTree(json)

    val skjemaType: SkjemaType = jsonNode.getSkjematype()

    private fun JsonNode.getSkjematype(): SkjemaType {
        return this.at("/data/journalpost/dokumenter").let { dokumenter ->
            when (dokumenter.size()) {
                0 -> throw IllegalArgumentException("Journalpost har ingen dokumenter")
                else -> dokumenter.first()["brevkode"].asText().tilSkjemaType()
            }
        }
    }
}

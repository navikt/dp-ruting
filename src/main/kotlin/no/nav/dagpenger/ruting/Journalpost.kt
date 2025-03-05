package no.nav.dagpenger.ruting

import com.github.navikt.tbd_libs.rapids_and_rivers.JsonMessage

data class Journalpost(
    val json: String,
) {
    // TODO
    fun brevkoder(): List<String> {
        val dokumenter = JsonMessage.newMessage(json)["data"]["journalpost"]["dokumenter"]
        return dokumenter.map { it["brevkode"].asText() }
    }
}

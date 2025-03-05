package no.nav.dagpenger.ruting

data class Journalpost(
    val json: String,
) {
    //TODO
//    val dokumenter = JsonMessage.newMessage(json)["data"]["journalpost"]["dokumenter"]
//    val brevkoder: List<String> = dokumenter.map { it["brevkode"].asText() }
}

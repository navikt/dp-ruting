package no.nav.dagpenger.ruting

import com.github.navikt.tbd_libs.rapids_and_rivers.JsonMessage
import com.github.navikt.tbd_libs.rapids_and_rivers.River
import com.github.navikt.tbd_libs.rapids_and_rivers.River.PacketListener
import com.github.navikt.tbd_libs.rapids_and_rivers_api.MessageContext
import com.github.navikt.tbd_libs.rapids_and_rivers_api.MessageMetadata
import com.github.navikt.tbd_libs.rapids_and_rivers_api.RapidsConnection
import io.micrometer.core.instrument.MeterRegistry
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

internal class JoarkMottak(
    rapidsConnection: RapidsConnection,
    private val mediator: Mediator,
) : PacketListener {
    companion object {
        val rapidFilter: River.() -> Unit = {
            precondition {
                it.requireKey("journalpostId", "journalpostStatus")
                it.requireValue("temaNytt", "DAG")
            }
            validate { it.interestedIn("hendelsesType", "mottaksKanal", "behandlingstema") }
        }
    }

    init {
        River(rapidsConnection).apply(rapidFilter).register(this)
    }

    override fun onPacket(
        packet: JsonMessage,
        context: MessageContext,
        metadata: MessageMetadata,
        meterRegistry: MeterRegistry,
    ) {
        val joarkHendelse = packet.joarkHendelse()
        logger.info { "Mottat joarkhendelse $joarkHendelse" }
        mediator.håndter(joarkHendelse)
    }
}

private fun JsonMessage.joarkHendelse(): JoarkHendelse {
    return JoarkHendelse(
        journalpostId = this["journalpostId"].asText(),
        journalpostStatus = this["journalpostStatus"].asText(),
        temaNytt = this["temaNytt"].asText(),
        hendelsesType = this["hendelsesType"].asText(),
        mottaksKanal = this["mottaksKanal"].asText(),
    )
}

data class JoarkHendelse(
    val journalpostId: String,
    val journalpostStatus: String,
    val temaNytt: String,
    val hendelsesType: String,
    val mottaksKanal: String,
)

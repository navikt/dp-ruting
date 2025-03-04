package no.nav.dagpenger.ruting

import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

interface Mediator {
    fun håndter(joarkHendelse: JoarkHendelse)
}

internal class MediatorImpl : Mediator {
    override fun håndter(joarkHendelse: JoarkHendelse) {
        logger.info { "Håndterer joarkHendelse: $joarkHendelse" }
    }
}

package no.nav.dagpenger.ruting

import io.micrometer.core.instrument.MeterRegistry

fun MeterRegistry.journalpostTotal(skjemaType: SkjemaType) {
    this.counter("dp.ruting.journalpost.total", "skjematype", skjemaType.name).increment()
}

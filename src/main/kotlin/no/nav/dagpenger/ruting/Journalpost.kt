package no.nav.dagpenger.ruting

data class Journalpost(
    val id: String,
    val skjemaType: SkjemaType,
) {
    constructor(json: String) : this(
        JournalpostMapper(json),
    )

    private constructor(mapper: JournalpostMapper) : this(
        id = mapper.id,
        skjemaType = mapper.skjemaType,
    )
}

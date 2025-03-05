package no.nav.dagpenger.ruting

data class Journalpost(
    val id: String,
    val skjemaType: SkjemaType,
) {
    constructor(json: String) : this(
        JournalPostMapper(json),
    )

    private constructor(mapper: JournalPostMapper) : this(
        id = mapper.id,
        skjemaType = mapper.skjemaType,
    )
}

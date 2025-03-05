package no.nav.dagpenger.ruting

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class JournalPostMapperTest {
    @Test
    fun `kan hente ut riktig skjemaType`() {
        JournalPostMapper(
            //language=json
            """
            {
              "data": {
                "journalpost": {
                  "dokumenter": [
                    {
                      "tittel": "Søknad om dagpenger (ikke permittert)",
                      "dokumentInfoId": "723669209",
                      "brevkode": "NAV 04-01.03"
                    },
                    {
                      "tittel": "Noe AnneT",
                      "dokumentInfoId": "823669209",
                      "brevkode": "Hubba Bubba"
                    }
                  ]
                }
              }
            }
            """.trimIndent(),
        ).skjemaType shouldBe SkjemaType.DAGPENGESØKNAD_ORDINÆR
    }
}

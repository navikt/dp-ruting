package no.nav.dagpenger.ruting

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class JournalpostMapperTest {
    @Test
    fun `kan hente ut riktig skjemaType`() {
        JournalpostMapper(
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
        ).skjemaType shouldBe SkjemaTypeEnum.DAGPENGESØKNAD_ORDINÆR
    }

    @Test
    fun `forventer minst et dokument i dokumenter lista`() {
        setOf(
            //language=JSON
            """ { "data": { "journalpost": { "dokumenter": [] } } } """,
            //language=JSON
            """ { "data": { "journalpost": {  } } } """,
        ).forEach { json ->
            shouldThrow<IllegalArgumentException> {
                JournalpostMapper(json).skjemaType
            }.message shouldBe "Journalpost har ingen dokumenter"
        }
    }

    @Test
    fun `Kan hente journalpostId`() {
        JournalpostMapper(
            //language=json
            """
            {
              "data": {
                "journalpost": {
                  "journalpostId": "123456789"
                }
              }
            }
            """.trimIndent(),
        ).id shouldBe "123456789"
    }

    @Test
    fun `forventer journalpostId som en tekst streng`() {
        setOf(
            //language=JSON
            """ { "data": { "journalpost": { } } } """,
            //language=JSON
            """ { "data": { } } """,
            //language=JSON
            """ { } """,
        ).forEach { json ->
            val exception =
                shouldThrow<IllegalArgumentException> {
                    JournalpostMapper(json).id
                }.message shouldBe "Journalpost mangler journalpostId"
        }

        shouldThrow<IllegalArgumentException> {
            JournalpostMapper(
                //language=JSON
                """ { "data": { "journalpost": { "journalpostId": 123456789 } } } """,
            ).id
        }.message shouldBe "JournalpostId er ikke en tekststreng"
    }
}

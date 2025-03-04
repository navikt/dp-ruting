package no.nav.dagpenger.ruting

import com.github.navikt.tbd_libs.rapids_and_rivers.test_support.TestRapid
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import org.junit.jupiter.api.Test


class JoarkMottakTest {
    private val rapid = TestRapid()

    @Test
    fun `rapid filter test`() {
        val slot = slot<JoarkHendelse>()
        val mediator = mockk<Mediator>().also {
            every { it.håndter(capture(slot)) } just Runs
        }
        val joarkMottak = JoarkMottak(rapid, mediator)
        rapid.sendTestMessage(joarkMelding(
            journalpostId = "123456789",
            journalpostStatus = "Mottatt",
            hendelseType = "MidlertidigJournalført",
            mottaksKanal = "NAV_NO",
            tema = "DAG"
        ))


    }

    //language=JSON
    private fun joarkMelding(
        journalpostId: String = "123456789",
        journalpostStatus: String = "Mottatt",
        hendelseType: String = "MidlertidigJournalført",
        mottaksKanal: String = "NAV_NO",
        tema: String = "DAG",
    ): String =
        """
        {
          "hendelsesId": "",
          "versjon": "",
          "hendelsesType": "$hendelseType",
          "journalpostId": "$journalpostId",
          "journalpostStatus": "$journalpostStatus",
          "temaGammelt": "$tema",
          "temaNytt": "$tema",
          "mottaksKanal": "$mottaksKanal",
          "kanalReferanseId": "vetikke"
        }
        """.trimIndent()


}

package no.nav.dagpenger.ruting

import com.github.navikt.tbd_libs.rapids_and_rivers.test_support.TestRapid
import io.kotest.matchers.shouldBe
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Test

class JoarkMottakTest {
    private val rapid = TestRapid()

    @Test
    fun `skal håndtere joark hendelse`() {
        val slot = slot<JoarkHendelse>()
        val mediator =
            mockk<Mediator>().also {
                every { it.håndter(capture(slot)) } just Runs
            }
        JoarkMottak(rapidsConnection = rapid, safClient = mockk(), mediator = mediator)
        rapid.sendTestMessage(
            joarkMelding(
                journalpostId = "123456789",
                journalpostStatus = "Mottatt",
                hendelseType = "MidlertidigJournalført",
                mottaksKanal = "NAV_NO",
                tema = "DAG",
            ),
        )

        verify(exactly = 1) { mediator.håndter(any()) }

        require(slot.isCaptured)
        slot.captured.let { actualJoarkHendelse ->
            actualJoarkHendelse.journalpostId shouldBe "123456789"
            actualJoarkHendelse.journalpostStatus shouldBe "Mottatt"
            actualJoarkHendelse.hendelsesType shouldBe "MidlertidigJournalført"
            actualJoarkHendelse.mottaksKanal shouldBe "NAV_NO"
            actualJoarkHendelse.temaNytt shouldBe "DAG"
        }
    }

    private fun joarkMelding(
        journalpostId: String = "123456789",
        journalpostStatus: String = "Mottatt",
        hendelseType: String = "MidlertidigJournalført",
        mottaksKanal: String = "NAV_NO",
        tema: String = "DAG",
    ): String =
        //language=JSON
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

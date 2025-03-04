package no.nav.dagpenger.ruting

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.navikt.tbd_libs.naisful.naisApp
import com.github.navikt.tbd_libs.rapids_and_rivers.KafkaRapid
import com.github.navikt.tbd_libs.rapids_and_rivers_api.RapidsConnection
import io.ktor.server.cio.CIOApplicationEngine
import io.ktor.server.engine.EmbeddedServer
import mu.KotlinLogging
import no.nav.helse.rapids_rivers.RapidApplication

private val logger = KotlinLogging.logger { }

internal class ApplicationBuilder(
    config: Map<String, String>,
) : RapidsConnection.StatusListener {
    private val rapidsConnection: RapidsConnection =
        RapidApplication.create(
            env = config,
            builder = {
                withKtor { preStopHook, rapid ->
                    naisApp(
                        meterRegistry = Configuration.prometheusRegistry,
                        objectMapper = jacksonObjectMapper(),
                        applicationLogger = KotlinLogging.logger("ApplicationLogger"),
                        callLogger = KotlinLogging.logger("CallLogger"),
                        aliveCheck = rapid::isReady,
                        readyCheck = rapid::isReady,
                        preStopHook = preStopHook::handlePreStopRequest,
                    ) {}
                }
            },
        ) { _: EmbeddedServer<CIOApplicationEngine, CIOApplicationEngine.Configuration>, rapid: KafkaRapid ->
            val mediator: Mediator = MediatorImpl()
            val safClient =
                SafGraphClient(
                    url = Configuration.safGraphqlUrl,
                    tokenProvider = Configuration.safGraphqlTokenProvider,
                )
            JoarkMottak(
                rapidsConnection = rapid,
                safClient = safClient,
                mediator = mediator,
            )
        }

    init {
        rapidsConnection.register(this)
    }

    fun start() = rapidsConnection.start()

    fun stop() = rapidsConnection.stop()

    override fun onStartup(rapidsConnection: RapidsConnection) {
        logger.info { "Starter app ${Configuration.APP_NAME}" }
    }

    override fun onShutdown(rapidsConnection: RapidsConnection) {
        logger.info { "Shutdown app ${Configuration.APP_NAME}" }
    }
}

package no.nav.dagpenger.ruting

import com.natpryce.konfig.ConfigurationMap
import com.natpryce.konfig.ConfigurationProperties
import com.natpryce.konfig.EnvironmentVariables
import com.natpryce.konfig.Key
import com.natpryce.konfig.getValue
import com.natpryce.konfig.overriding
import com.natpryce.konfig.stringType
import io.micrometer.core.instrument.Clock
import io.micrometer.prometheusmetrics.PrometheusConfig
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry
import io.prometheus.metrics.model.registry.PrometheusRegistry
import no.nav.dagpenger.oauth2.CachedOauth2Client
import no.nav.dagpenger.oauth2.OAuth2Config

object Configuration {
    const val APP_NAME = "dp-ruting"

    private val defaultProperties =
        ConfigurationMap(
            mapOf(
                "RAPID_APP_NAME" to APP_NAME,
                "KAFKA_CONSUMER_GROUP_ID" to "$APP_NAME-v1",
                "KAFKA_EXTRA_TOPIC" to "teamdagpenger.mottak.v1",
                "KAFKA_RAPID_TOPIC" to "teamdagpenger.rapid.v1",
                "KAFKA_RESET_POLICY" to "LATEST",
                "SAF_GRAPHQL_URL" to "http://saf.default.svc.nais.local/graphql",
            ),
        )
    val properties =
        ConfigurationProperties.systemProperties() overriding EnvironmentVariables() overriding defaultProperties

    val config: Map<String, String> =
        properties.list().reversed().fold(emptyMap()) { map, pair ->
            map + pair.second
        }

    private val cachedTokenProvider by lazy {
        val azureAd = OAuth2Config.AzureAd(properties)
        CachedOauth2Client(
            tokenEndpointUrl = azureAd.tokenEndpointUrl,
            authType = azureAd.clientSecret(),
        )
    }
    val safGraphqlUrl: String by lazy {
        properties[Key("SAF_GRAPHQL_URL", stringType)]
    }
    val safGraphqlTokenProvider: () -> String by lazy {
        {
            cachedTokenProvider.clientCredentials(properties[Key("SAF_SCOPE", stringType)]).access_token
                ?: throw RuntimeException("Kunne ikke finne token for SAF")
        }
    }

    val prometheusRegistry by lazy {
        PrometheusMeterRegistry(
            PrometheusConfig.DEFAULT,
            PrometheusRegistry.defaultRegistry,
            Clock.SYSTEM,
        )
    }
}

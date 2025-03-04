package no.nav.dagpenger.ruting

import io.kubernetes.client.openapi.ApiClient
import io.kubernetes.client.openapi.apis.CoreV1Api
import io.kubernetes.client.openapi.models.V1Secret
import io.kubernetes.client.util.ClientBuilder
import io.kubernetes.client.util.KubeConfig
import kotlinx.coroutines.runBlocking
import no.nav.dagpenger.oauth2.CachedOauth2Client
import no.nav.dagpenger.oauth2.OAuth2Config
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.io.FileReader

class SafGraphClientTest {
    @Disabled
    @Test
    fun e2eTest() {
        SafGraphClient(
            url = "https://saf-q1.dev.intern.nav.no/graphql",
            tokenProvider = {
                getAuthEnv("dp-ruting", "azurerator.nais.io").let {
                    getAzureAdToken("dp-ruting", "api://dev-fss.teamdokumenthandtering.saf-q1/.default")
                }
            },
        ).let {
            runBlocking {
                it.hentJournalpost("690881908").let {
                    println(it.json)
                }
            }
        }
    }

    fun getAuthEnv(
        app: String,
        type: String = "jwker.nais.io",
    ): Map<String, String> {
        // file path to your KubeConfig
        val kubeConfigPath = System.getenv("KUBECONFIG")

        // IF this fails do kubectl get pod to aquire credentials
        val client: ApiClient = ClientBuilder.kubeconfig(KubeConfig.loadKubeConfig(FileReader(kubeConfigPath))).build()

        return CoreV1Api(client)
            .listNamespacedSecret(
                "teamdagpenger",
            ).execute().items.filter {
                it.metadata.labels?.get("app") == app && it.metadata.labels?.get("type") == type
            }.first<V1Secret?>()
            ?.data!!
            .mapValues { e -> String(e.value) }
    }

    fun getAzureAdToken(
        app: String,
        scope: String,
    ): String {
        val azureadConfig =
            OAuth2Config.AzureAd(
                getAuthEnv(app, "azurerator.nais.io"),
            )
        val tokenAzureAdClient: CachedOauth2Client by lazy {
            CachedOauth2Client(
                tokenEndpointUrl = azureadConfig.tokenEndpointUrl,
                authType = azureadConfig.clientSecret(),
            )
        }

//    val scope = "api://dev-gcp.teamdagpenger.dp-mellomlagring/.default"
        return tokenAzureAdClient.clientCredentials(scope).access_token!!
    }
}

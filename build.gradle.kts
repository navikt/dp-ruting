import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
plugins {
    id("common")
    application
    alias(libs.plugins.shadow.jar)
}

dependencies {
    implementation(libs.kotlin.logging)
    implementation(libs.rapids.and.rivers)
    implementation(libs.konfig)
    implementation("com.github.navikt.tbd-libs:naisful-app:2025.03.10-19.50-d556269c")
    implementation(libs.bundles.jackson)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.client.logging.jvm)
    implementation("no.nav.dagpenger:oauth2-klient:2025.02.13-18.02.052b7c34baab")
    implementation("io.prometheus:prometheus-metrics-core:1.3.6")

    testImplementation(libs.bundles.naisful.rapid.and.rivers.test)
    testImplementation(libs.mockk)
    testImplementation(libs.bundles.kotest.assertions)
    testImplementation("io.kubernetes:client-java:23.0.0")
}

application {
    mainClass.set("no.nav.dagpenger.ruting.AppKt")
}

tasks.withType<ShadowJar> {
    mergeServiceFiles()
}

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
    implementation("com.github.navikt.tbd-libs:naisful-app:2025.04.02-15.56-d34332c4")
    implementation(libs.bundles.jackson)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.client.logging.jvm)
    implementation("no.nav.dagpenger:oauth2-klient:2025.03.31-22.36.fc954bf09c91")
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

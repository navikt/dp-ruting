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
    implementation("com.github.navikt.tbd-libs:naisful-app:2025.02.25-08.21-6205bbfb")
    implementation(libs.bundles.jackson)
}

application {
    mainClass.set("no.nav.dagpenger.ruting.AppKt")
}

tasks.withType<ShadowJar> {
    mergeServiceFiles()
}

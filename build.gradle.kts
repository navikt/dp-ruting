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
}

application {
    mainClass.set("no.nav.dagpenger.ruting.AppKt")
}

tasks.withType<ShadowJar> {
    mergeServiceFiles()
}

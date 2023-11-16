plugins {
    id("kotlin-library-convention")
}

repositories {
    mavenCentral()
}

dependencies {

    /* Kotlin */
    implementation(kotlin("stdlib"))

    /* Test */
    testImplementation(project(":airflux-functional-test"))
    testImplementation(testLibs.bundles.kotest)
    testImplementation(testLibs.pitest.junit5)
}

tasks {
    compileTestKotlin.configure {
        kotlinOptions {
            freeCompilerArgs += listOf("-Xcontext-receivers")
        }
    }
}

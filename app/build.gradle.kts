plugins {
    application
    kotlin("jvm") version "1.7.10"
    kotlin("plugin.serialization") version "1.7.10"
}

repositories {
    mavenCentral()
    google()
}

dependencies {
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    implementation("com.charleskorn.kaml:kaml:0.48.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.0")
}

testing {
    suites {
        val test by getting(JvmTestSuite::class) {
            useKotlinTest()
        }
    }
}

application {
    mainClass.set("com.eddsteel.literalquotes.AppKt")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.72" apply false
    id("org.jetbrains.dokka") version "0.10.1" apply false
}

allprojects {

    group = "com.github.hexindai.s3harding"
    version = "0.0.8"

    repositories {
        mavenCentral()
        jcenter()
    }

    tasks.withType<KotlinCompile>().configureEach {
        kotlinOptions.jvmTarget = "1.8"
    }
}

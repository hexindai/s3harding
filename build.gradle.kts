import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.71" apply false
}

allprojects {

    group = "com.github.hexindai.s3harding"
    version = "0.0.1-SNAPSHOT"

    repositories {
        maven {
            url = uri("https://maven.aliyun.com/nexus/content/repositories/central")
        }
        jcenter {
            url = uri("https://maven.aliyun.com/repository/jcenter")
        }
    }

    tasks.withType<KotlinCompile>().configureEach {
        kotlinOptions.jvmTarget = "1.8"
    }
}

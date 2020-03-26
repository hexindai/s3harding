import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.70" apply false
}

allprojects {

    group = "com.github.hexindai.s3harding"
    version = "0.0.1"

    repositories {
        jcenter {
            url = uri("https://maven.aliyun.com/repository/jcenter")
        }
    }

    tasks.withType<KotlinCompile>().configureEach {
        kotlinOptions.jvmTarget = "1.8"
    }
}

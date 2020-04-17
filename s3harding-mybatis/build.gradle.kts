import org.gradle.api.tasks.testing.logging.TestExceptionFormat

plugins {
    kotlin("jvm")
    id("org.jetbrains.dokka")
    jacoco
    `maven-publish`
    signing
}

dependencies {

    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))

    implementation(project(":s3harding-core"))
    implementation(platform("org.testcontainers:testcontainers-bom:1.14.0"))
    implementation("org.mybatis:mybatis:3.5.4")
    implementation("com.github.jsqlparser:jsqlparser:3.1")

    testImplementation("com.github.jsqlparser:jsqlparser:3.1")

    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-junit"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.6.0")
    testImplementation("org.testcontainers:mysql")
    testImplementation("mysql:mysql-connector-java:8.0.19")
    testImplementation("log4j:log4j:1.2.17")

}

tasks.test {
    finalizedBy(tasks.jacocoTestReport)
    useJUnitPlatform {
        includeEngines("junit-jupiter")
    }
    testLogging {
        exceptionFormat = TestExceptionFormat.FULL
        events("passed", "skipped", "failed")
    }
}

tasks.dokka {
    outputFormat = "html"
    outputDirectory = "$buildDir/javadoc"
}

jacoco {
    toolVersion = "0.8.5"
}

tasks.jacocoTestReport {
    reports {
        xml.isEnabled = true
        csv.isEnabled = true
        html.isEnabled = true
    }
}

val sourcesJar by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}

val dokkaJar by tasks.registering(Jar::class) {
    description = "Assembles Kotlin docs with Dokka"
    archiveClassifier.set("javadoc")
    from(tasks.dokka)
}

publishing {

    repositories {
        when (project.findProperty("release")) {
            "SNAPSHOT" -> maven {
                name = "OSSRH-SNAPSHOT"
                url = uri("https://oss.sonatype.org/content/repositories/snapshots")
                credentials {
                    username = project.findProperty("ossrh.user") as String? ?: System.getenv("OSSRH_USERNAME")
                    password = project.findProperty("ossrh.key") as String? ?: System.getenv("OSSRH_TOKEN")
                }
            }
            "GA" -> {
                maven {
                    name = "OSSRH-GA"
                    url = uri("https://oss.sonatype.org/service/local/staging/deploy/maven2")
                    credentials {
                        username = project.findProperty("ossrh.user") as String? ?: System.getenv("OSSRH_USERNAME")
                        password = project.findProperty("ossrh.key") as String? ?: System.getenv("OSSRH_TOKEN")
                    }
                }
                maven {
                    name = "GitHubPackages"
                    url = uri("https://maven.pkg.github.com/hexindai/s3harding")
                    credentials {
                        username = project.findProperty("gpr.user") as String? ?: System.getenv("USERNAME")
                        password = project.findProperty("gpr.key") as String? ?: System.getenv("TOKEN")
                    }
                }
            }
        }
    }

    publications {
        create<MavenPublication>("gpr") {
            from(components["java"])
            artifact(sourcesJar.get())
            artifact(dokkaJar.get())

            pom {
                name.set(project.name)
                description.set("S3harding: A sharding library for Mybatis")
                url.set("https://github.com/hexindai/s3harding")

                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }

                developers {
                    developer {
                        id.set("Runrioter")
                        name.set("Runrioter Wung")
                        email.set("runrioter@gmail.com")
                    }
                }

                scm {
                    connection.set("scm:git:git://github.com/hexindai/s3harding.git")
                    developerConnection.set("scm:git:ssh://git@github.com:hexindai/s3harding.git")
                    url.set("https://github.com/hexindai/s3harding/tree/master/s3harding-mybatis")
                }
            }
        }
    }
}

signing {
    val signingKey = System.getenv("PGP_PRIVATE_KEY")
    val signingPassword = System.getenv("PGP_PRIVATE_KEY_PASSWORD")
    useInMemoryPgpKeys(signingKey, signingPassword)
    sign(publishing.publications["gpr"])
}

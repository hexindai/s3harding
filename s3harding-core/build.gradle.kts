
plugins {
    kotlin("jvm")
}

dependencies {

    implementation(kotlin("stdlib-jdk8"))
    implementation("commons-codec:commons-codec:1.14")

    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-junit"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.6.0")

}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}

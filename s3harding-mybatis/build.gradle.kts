
plugins {
    kotlin("jvm")
}

dependencies {

    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))

    implementation(project(":s3harding-core"))
    implementation("org.mybatis:mybatis:3.5.4")
    implementation("com.github.jsqlparser:jsqlparser:3.1")

    testImplementation("org.hsqldb:hsqldb:2.5.0")
    testImplementation("com.github.jsqlparser:jsqlparser:3.1")

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

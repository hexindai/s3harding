rootProject.name = "s3harding"

include("s3harding-core")
include("s3harding-mybatis")

pluginManagement {
    repositories {
        maven {
            url = uri("https://maven.aliyun.com/repository/gradle-plugin")
        }
    }
}
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile


plugins {
    id("org.springframework.boot") version "2.5.5"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    kotlin("jvm") version "1.5.31"
    kotlin("plugin.spring") version "1.5.31"
}

group = "cn.itxia"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_1_8

repositories {
    //下载依赖慢的话用下面的国内源，但不一定有mirai最新的包，也许会build fail
    maven {
        url = uri("https://mirrors.cloud.tencent.com/nexus/repository/maven-public/")
    }
    maven {
        url = uri("https://maven.aliyun.com/repository/spring/")
    }
    jcenter {
        url = uri("https://maven.aliyun.com/repository/public")
    }
    mavenCentral()
    jcenter()
}

kotlin.target.attributes.attribute(KotlinPlatformType.attribute, KotlinPlatformType.jvm)

dependencies {
    //coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2")

    //mirai qq bot library
    val miraiVersion = "2.7.1"
    implementation("net.mamoe:mirai-core-jvm:$miraiVersion") {
        exclude("net.mamoe", "mirai-core-api")
        exclude("net.mamoe", "mirai-core-utils")
    }
    implementation("net.mamoe:mirai-core-api-jvm:$miraiVersion") {
        exclude("net.mamoe", "mirai-core-utils")
    }
    implementation("net.mamoe:mirai-core-utils-jvm:$miraiVersion")

    //http request lib
    implementation("com.squareup.okhttp3:okhttp:4.9.2")

    //apache log4j
    implementation("org.apache.logging.log4j:log4j-api:2.14.1")
    implementation("org.apache.logging.log4j:log4j-core:2.14.1")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.0")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.springframework.boot:spring-boot-starter-web")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "1.8"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

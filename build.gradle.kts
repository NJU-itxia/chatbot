import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.4.1"
    id("io.spring.dependency-management") version "1.0.10.RELEASE"
    kotlin("jvm") version "1.4.21"
    kotlin("plugin.spring") version "1.4.21"
}

group = "cn.itxia"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_1_8

repositories {
    //下载依赖慢的话用下面的国内源，但不一定有mirai最新的包，也许会build fail
//    maven {
//        url = uri("https://maven.aliyun.com/nexus/content/repositories/jcenter")
//    }
//    maven {
//        url = uri("https://maven.aliyun.com/repository/public/")
//    }
//    maven {
//        url = uri("https://maven.aliyun.com/repository/spring/")
//    }
    mavenCentral()
    jcenter()
}

dependencies {
    //mirai qq bot library
//    implementation("net.mamoe:mirai-core:1.3.3")
//    implementation("net.mamoe:mirai-core-qqandroid:1.3.3")
    //升级到2.0测试版
    implementation("net.mamoe:mirai-core:2.0-M1-1")
//    implementation("net.mamoe:mirai-core-api")

    //http request lib
    implementation("com.squareup.okhttp3:okhttp:4.9.0")

    //apache log4j
    implementation("org.apache.logging.log4j:log4j-api:2.14.0")
//    implementation("org.apache.logging.log4j:log4j-api-kotlin-parent:1.0.0")
    implementation("org.apache.logging.log4j:log4j-core:2.14.0")

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
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

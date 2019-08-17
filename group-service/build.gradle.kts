import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val coroutinesVersion = "1.3.0-RC2"
val spekVersion = "2.0.2"
val junitVersion = "5.2.0"

buildscript {
    repositories {
        maven("http://repo.spring.io/milestone")
    }
    dependencies {
        classpath("org.springframework.boot:spring-boot-gradle-plugin:2.2.0.M5")
    }
}

apply(plugin = "org.springframework.boot")

plugins {
    id("io.spring.dependency-management") version "1.0.8.RELEASE"
    kotlin("jvm") version "1.3.41"
    kotlin("plugin.spring") version "1.3.41"
    id("com.google.cloud.tools.jib") version "1.3.0"
}

group = "zhi.yest.community-intersection"
version = "0.0.1-SNAPSHOT"

repositories {
    maven(url = "https://repo.spring.io/milestone")
    maven(url = "http://dl.bintray.com/spekframework/spek")
    mavenCentral()
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:Hoxton.M1")
    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactive:$coroutinesVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:$coroutinesVersion")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.cloud:spring-cloud-starter-config")
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")
    implementation("org.springframework.security:spring-security-oauth2-client")
    implementation("org.springframework.security:spring-security-oauth2-resource-server")
    implementation("org.springframework.security:spring-security-oauth2-jose")

    compileOnly("javax.servlet:javax.servlet-api:4.0.1")

    testImplementation("com.nhaarman.mockitokotlin2:mockito-kotlin:2.+")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testImplementation("org.spekframework.spek2:spek-dsl-jvm:$spekVersion")
    testRuntimeOnly("org.spekframework.spek2:spek-runner-junit5:$spekVersion")
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "1.8"
    }
}

tasks.withType<Test> {
    useJUnitPlatform {
        includeEngines = mutableSetOf("spek2")
    }
}

jib {
    to {
        image = "dazzbourgh/friend-finder/group-service"
        tags = setOf("latest")
    }
    container {
        jvmFlags = listOf("-XX:+UnlockExperimentalVMOptions",
                "-XX:+UseCGroupMemoryLimitForHeap",
                "-XX:MaxRAMFraction=1")
    }
}

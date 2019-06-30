import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val coroutinesVersion = "1.2.0"
val spekVersion = "2.0.2"
val junitVersion = "5.2.0"

plugins {
    id("org.springframework.boot") version "2.1.5.RELEASE"
    id("io.spring.dependency-management") version "1.0.7.RELEASE"
    kotlin("jvm") version "1.3.30"
    kotlin("plugin.spring") version "1.3.30"
    id("com.google.cloud.tools.jib") version "1.0.1"
}

group = "zhi.yest.community-intersection"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_1_8

repositories {
    mavenCentral()
    maven(url = "http://dl.bintray.com/spekframework/spek")
}

extra["springCloudVersion"] = "Greenwich.SR1"

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.springframework.cloud:spring-cloud-starter-config")
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-hystrix")
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-ribbon")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.security:spring-security-oauth2-client")
    compileOnly("javax.servlet:javax.servlet-api:4.0.1")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactive:$coroutinesVersion")
    runtimeOnly("org.springframework.boot:spring-boot-devtools")

    testImplementation("com.nhaarman.mockitokotlin2:mockito-kotlin:2.+")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testImplementation("org.spekframework.spek2:spek-dsl-jvm:$spekVersion")
    testRuntimeOnly("org.spekframework.spek2:spek-runner-junit5:$spekVersion")
    testImplementation("org.springframework.boot:spring-boot-starter-test")

    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
    }
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
//
//jib {
//    to {
//        image = 'dazzbourgh/friend-finder'
//        tags = ['latest']
//    }
//    container {
//        jvmFlags = ['-XX:+UnlockExperimentalVMOptions',
//            '-XX:+UseCGroupMemoryLimitForHeap',
//            '-XX:MaxRAMFraction=1']
//    }
//}
//

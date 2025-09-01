import io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension

plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("kapt") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25" apply false
    kotlin("plugin.jpa") version "1.9.25" apply false
    id("org.springframework.boot") version "3.5.0" apply false
    id("io.spring.dependency-management") version "1.1.7" apply false
}

allprojects {
    group = "com.buoyancy"
    version = "0.0.1-SNAPSHOT"

    repositories {
        mavenCentral()
        maven { url = uri("https://repo.spring.io/milestone") }
    }
}

subprojects {

    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.jetbrains.kotlin.kapt")
    apply(plugin = "org.jetbrains.kotlin.plugin.spring")
    apply(plugin = "org.jetbrains.kotlin.plugin.jpa")
    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management")

    configure<JavaPluginExtension> {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(21))
        }
    }

    extra["springCloudVersion"] = "2025.0.0"

    configure<DependencyManagementExtension> {
        imports {
            mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
            applyMavenExclusions(false) // turns off maven exclusions processing for spring dependency management plugin
            // which dramatically speeds up the build process (and gets rid of slow detachedConfiguration tasks)
        }
    }

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
        }
    }

//    tasks.matching { it.name.contains("kapt", ignoreCase = true) }.configureEach {
//        enabled = false
//    }

    val implementation by configurations

    dependencies {
        implementation("org.jetbrains.kotlin:kotlin-reflect")
        implementation("org.mapstruct:mapstruct:1.6.3")
        implementation("io.github.oshai:kotlin-logging-jvm:7.0.7")
        implementation("ch.qos.logback:logback-classic:1.5.18")
        implementation("org.springframework.boot:spring-boot-starter-cache")
        kapt("org.mapstruct:mapstruct-processor:1.6.3")
    }

    tasks.withType<Test> {
        useJUnitPlatform()
        testLogging {
            events("passed", "skipped", "failed")
            showStandardStreams = true
        }
    }
}
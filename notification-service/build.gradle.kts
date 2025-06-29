group = "${rootProject.group}.notification"

dependencies {
    implementation(project(":common-module")) {
        exclude(group = "org.springframework.boot", module = "spring-boot-starter-data-jpa")
        exclude(group = "org.springframework.boot", module = "spring-boot-starter-web")
        exclude(group = "org.flywaydb", module = "flyway-core")
        exclude(group = "org.flywaydb", module = "flyway-database-postgresql")
        exclude(group = "org.postgresql", module = "postgresql")
    }
    implementation("org.springframework.boot:spring-boot-starter-mail:3.5.3")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.springframework.kafka:spring-kafka")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.springframework.kafka:spring-kafka-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.4.0")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.mockito", module = "mockito-core")
    }
    testImplementation(kotlin("test"))
}

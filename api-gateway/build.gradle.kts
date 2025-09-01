group = "${rootProject.group}.gateway"

dependencies {
    implementation(project(":common-module")) {
        exclude(group = "org.flywaydb")
        exclude(group = "org.springframework.boot", "spring-boot-starter-data-jpa")
        exclude(group = "org.springframework.boot", "spring-boot-starter-data-redis")
    }
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.cloud:spring-cloud-starter-gateway-server-webmvc")
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")
//    implementation("org.springframework.boot:spring-boot-starter-security")
//    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    implementation("org.springframework.cloud:spring-cloud-starter-loadbalancer")
//    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.4.0")
//    testImplementation("org.springframework.boot:spring-boot-starter-test") {
//        exclude(group = "org.mockito", module = "mockito-core")
//    }
    testImplementation(kotlin("test"))
}

group = "${rootProject.group}.discovery"

dependencies {
    implementation(project(":common-module")) {
        exclude(group = "org.flywaydb")
        exclude(group = "org.springframework.boot", "spring-boot-starter-data-jpa")
        exclude(group = "org.springframework.boot", "spring-boot-starter-data-redis")
        exclude(group = "org.springframework.boot", module = "spring-boot-starter-security")
        exclude(group = "org.springframework.boot", module = "spring-boot-starter-oauth2-resource-server")
        exclude(group = "org.springframework.security", module = "spring-security-oauth2-jose")
    }
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-server")
    implementation("org.springframework.cloud:spring-cloud-starter-loadbalancer")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.4.0")
    testImplementation(kotlin("test"))
}

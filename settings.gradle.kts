plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}
rootProject.name = "foodtech"
include("order-service")
include("payment-service")
include("restaurant-service")
include("notification-service")
include("api-gateway")
include("common-module")

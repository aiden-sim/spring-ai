plugins {
    kotlin("jvm")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.ai:spring-ai-starter-mcp-server-webmvc")
}

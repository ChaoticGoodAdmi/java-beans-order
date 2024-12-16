plugins {
	kotlin("jvm") version "1.9.25"
	kotlin("plugin.spring") version "1.9.25"
	id("org.springframework.boot") version "3.4.0"
	id("io.spring.dependency-management") version "1.1.6"
}

group = "ru.ushakov"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("jakarta.validation:jakarta.validation-api:3.1.0")
	implementation("org.hibernate.validator:hibernate-validator:8.0.0.Final")
	implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")

	implementation("org.jetbrains.kotlin:kotlin-reflect")

	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

	implementation("org.springframework.kafka:spring-kafka")

	implementation("org.springframework.boot:spring-boot-starter-data-jpa:3.4.0")

	implementation("org.springframework.boot:spring-boot-starter-actuator:3.4.0")
	implementation("io.micrometer:micrometer-registry-prometheus")
	implementation("io.micrometer:micrometer-core")

	implementation("org.springframework.boot:spring-boot-starter-cache")
	implementation("org.springframework.boot:spring-boot-starter-data-redis")

	runtimeOnly("org.postgresql:postgresql")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testImplementation("org.springframework.kafka:spring-kafka-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

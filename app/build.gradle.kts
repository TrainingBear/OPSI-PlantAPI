plugins {
	id("java-library")
	id("org.springframework.boot") version "3.5.3"
	id("io.spring.dependency-management") version "1.1.7"
    kotlin("jvm")
}

tasks.register("prepareKotlinBuildScriptModel"){}

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

dependencies {
	implementation(project(":api"))
	implementation("com.github.alexdlaird:java-ngrok:2.3.16")
	implementation("org.apache.commons:commons-lang3:3.18.0")
	implementation ("com.fasterxml.jackson.core:jackson-databind")
	implementation("org.apache.commons:commons-csv:1.10.0")
	implementation("org.springframework.boot:spring-boot-starter-jdbc")
	implementation("com.mysql:mysql-connector-j:8.3.0")
	implementation("org.springframework.boot:spring-boot-starter-web")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    implementation(kotlin("stdlib-jdk8"))
}

tasks.withType<Test> {
	useJUnitPlatform()
}
repositories {
    mavenCentral()
}
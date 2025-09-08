plugins {
	id("java-library")
	id("org.springframework.boot") version "3.5.3"
	id("io.spring.dependency-management") version "1.1.7"
}

tasks.register("prepareKotlinBuildScriptModel"){}

dependencies {
	implementation(project(":api"))
//	https://mvnrepository.com/artifact/com.google.guava/guava
	implementation("com.google.guava:guava:33.4.8-jre")
	implementation("org.apache.commons:commons-lang3:3.18.0")
//	implementation("org.tensorflow:tensorflow-core-platform:1.1.0")
	implementation ("com.fasterxml.jackson.core:jackson-databind")
	implementation("org.apache.commons:commons-csv:1.10.0")
	implementation("org.springframework.boot:spring-boot-starter-jdbc")
	implementation("com.mysql:mysql-connector-j:8.3.0")
	implementation("org.springframework.boot:spring-boot-starter-web")
//	compileOnly("org.tensorflow", "tensorflow-core-platform", "1.1.0")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
plugins {
	java
	id("org.jetbrains.kotlin.jvm") version "2.0.0"
	id("org.springframework.boot") version "3.5.3"
	id("io.spring.dependency-management") version "1.1.7"
}
allprojects {
	group = "com.trbear9.plants"
	version = "1.0.0"

	repositories {
		google()
		gradlePluginPortal()
		mavenCentral()
	}
}
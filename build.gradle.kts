plugins {
	id("java")
}

java {
	sourceCompatibility = JavaVersion.VERSION_21
}

allprojects {
	group = "com.github.TrainingBear"
	version = "1.0.0"

	repositories {
		google()
		gradlePluginPortal()
		mavenCentral()
	}
}


subprojects {
	plugins.withType<JavaPlugin>{
		project.dependencies {
		    "compileOnly"("org.projectlombok:lombok:1.18.32")
		    "annotationProcessor"("org.projectlombok:lombok:1.18.32")
		}
	}
}
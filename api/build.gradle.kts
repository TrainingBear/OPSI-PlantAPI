plugins {
    id("java-library")
}

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
    /* https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-annotations */
    implementation("com.fasterxml.jackson.core:jackson-annotations:3.0-rc5")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}
plugins {
    id("java-library")
}

repositories {
    mavenCentral()
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
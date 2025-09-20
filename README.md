# Open Plant API 
[![](https://jitpack.io/v/TrainingBear/OPSI-PlantAPI.svg)](https://jitpack.io/#TrainingBear/OPSI-PlantAPI)
![](logoopsi.png)
***
RESTAPI ini di di bangun oleh salah satu tim OPSI dari SMA Negeri 1 Ambarawa.
RESTAPI ini menggunakan springboot dan fastapi (untuk memproses model CNN).
POST Request bertipe application/json, lalu proses oleh 
model CNN yang sudah di latih. Response yang dihasilkan bertipe Application/json.

untuk menggunakan RESTAPI ini kami menyediakan SDK Client untuk bahasa pemograman JVM (Java, kotlin, scala, groovy,
Clonjure, Jython, JRuby).

untuk yang mau berkontribusi dalam pembuatan SDK Client, atau penambahan fitur lainya.
Kalian bisa membuat pull request, atau hubungi kami lewat email kukuhsudrajad354@gmail.com kontribusimu akan sangat membantu
*** 
## SDK Client Installation
Maven:
```html
<repositories>
	<repository>
	    <id>jitpack.io</id>
	    <url>https://jitpack.io</url>
	</repository>
</repositories>

<dependency>
    <groupId>com.github.TrainingBear.OPSI-PlantAPI</groupId>
    <artifactId>api</artifactId>
    <version>{{VERSION}}</version>
</dependency>
```
***
Gradle:
```groovy
dependencyResolutionManagement {
	repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
	repositories {
		mavenCentral()
		maven { url 'https://jitpack.io' }
	}
}

dependencies {
        implementation 'com.github.TrainingBear.OPSI-PlantAPI:api:{{VERSION}}'
}
```
***
Gradle kotlin-dsl:
```kotlin
dependencyResolutionManagement {
	repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
	repositories {
		mavenCentral()
		maven { url = uri("https://jitpack.io") }
	}
}

dependencies {
        implementation("com.github.TrainingBear.OPSI-PlantAPI:api:{{VERSION}}")
}
```
***


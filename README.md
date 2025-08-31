# PlantAPI
[![](https://jitpack.io/v/TrainingBear/OPSI-PlantAPI.svg)](https://jitpack.io/#TrainingBear/OPSI-PlantAPI)

RESTAPI ini di di bangun oleh salah satu tim OPSI dari SMA Negeri 1 Ambarawa. RestApi ini menggunakan springboot 
dan fastapi (untuk memproses model CNN). POST Request yang di kirimkan ke REST API bertipe application/json, lalu proses oleh 
model CNN yang sudah di latih. Response juga berbentuk Application/json.

maka dari itu, guna membuat request ke restapi ini. anda perlu mengirim data blob/json, untuk mendapatkan response. agar lebih mudah kami juga menyediakan 
api untuk mengirimkan request ke restapi ini. sehingga post & response sudah terintegrasi.

# Maven 

	<repositories>
		<repository>
		    <id>jitpack.io</id>
		    <url>https://jitpack.io</url>
		</repository>
	</repositories>

	<dependency>
	    <groupId>com.github.TrainingBear</groupId>
	    <artifactId>api</artifactId>
	    <version>1.1.0</version>
	</dependency>

# Gradle

	dependencyResolutionManagement {
		repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
		repositories {
			mavenCentral()
			maven { url 'https://jitpack.io' }
		}
	}

	dependencies {
	        implementation 'com.github.TrainingBear:api:1.1.0'
	}

# Gradle kotlin-dsl

	dependencyResolutionManagement {
		repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
		repositories {
			mavenCentral()
			maven { url = uri("https://jitpack.io") }
		}
	}

	dependencies {
	        implementation("com.github.TrainingBear:api:1.1.0")
	}


# Open Plant API [<img src="SMANEGA.png" alt="SMANEGA" width="30" title="SMA Negeri 1 Ambarawa">](https://sman1ambarawa.sch.id/)
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

# Server start guide
kami menggunakan [ngrok](https://ngrok.com) untuk proxy https protocol provider. 
ngrok gratis tanpa biaya. namun setiap memulai server, url https yang diberikan akan acak seperti: https://c27e3f9aacc8.ngrok-free.app

untuk mengakalinya kami menggunakan [GitHub Gist](https://gist.github.com/) yang berisi url https yang selalu di update setiap server start.
berikut tahap-tahap menjalankan server:

compile:
-
```cmd
git clone https://github.com/TrainingBear/OPSI-PlantAPI.git
cd OPSI-PlantAPI
./gradlew clean build -x test
```
Buat gist dengan nama url.json di https://gist.github.com/

Jalankan dengan argumen:
- 
```text 
java -jar target/app-$version.jar --GIST-TOKEN=$GIST_TOKEN --GIST-PROVIDER=$PROVIDER --OPEN-AI-KEY=<open ai key> --MODEL=<model path>
```
Keterangan:
- --GIST-PROVIDER=PROVIDER, PROVIDER mengacu pada link Gist. contoh --GIST=PROVIDER=TrainingBear/84d0e105aaabce26c8dfbaff74b2280e
- --GITHUB-TOKEN=GIST_TOKEN, GIST_TOKEN adalah token akun Github mu, baca [disini](https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/creating-a-personal-access-token) untuk membuat token.
- --OPEN-AI-KEY=AI_KEY, open ai key bisa di dapatkan di [disini](https://platform.openai.com/account/api-keys).
- --MODEL=PATH, pastikan model terletak di folder server berada. contoh --MODEL=model.keras

model CNN yang kami buat: https://github.com/TrainingBear/OPSI-Project-AndroidApplication/tree/master/tensorflow_examples/lite/model_maker/demo

---
# SDK Client Installation
Versi terbaru tersedia di [release](https://github.com/TrainingBear/OPSI-PlantAPI/releases)
atau di [jitpack](https://jitpack.io/#TrainingBear/OPSI-PlantAPI)
- Maven:
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
    <version>$VERSION</version>
</dependency>
```
***
- Gradle:
```groovy
dependencyResolutionManagement {
	repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
	repositories {
		mavenCentral()
		maven { url 'https://jitpack.io' }
	}
}

dependencies {
        implementation 'com.github.TrainingBear.OPSI-PlantAPI:api:$VERSION'
}
```
***
- Gradle kotlin-dsl:
```kotlin
dependencyResolutionManagement {
	repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
	repositories {
		mavenCentral()
		maven { url = uri("https://jitpack.io") }
	}
}

dependencies {
        implementation("com.github.TrainingBear.OPSI-PlantAPI:api:$VERSION")
}
```
***


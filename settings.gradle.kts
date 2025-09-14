pluginManagement {
    plugins {
        kotlin("jvm") version "2.2.0"
    }
}
rootProject.name = "plants-api"
//gradle.startParameter.excludedTaskNames += "prepareKotlinBuildScriptModel"
include(":app", ":api")

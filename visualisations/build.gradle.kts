plugins {
    kotlin("js") version "1.7.20"
}

group = "me.karim"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.3")
    implementation("org.jetbrains.kotlin-wrappers:kotlin-react:18.2.0-pre.346")
    implementation("org.jetbrains.kotlin-wrappers:kotlin-react-dom:18.2.0-pre.346")
    implementation("org.jetbrains.kotlin-wrappers:kotlin-emotion:11.9.3-pre.346")
}

kotlin {
    js {
        binaries.executable()
        browser {
            commonWebpackConfig {
                cssSupport.enabled = true
            }
        }
    }
}
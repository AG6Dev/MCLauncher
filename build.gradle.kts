plugins {
    id("java")
    id("application")
    id("org.openjfx.javafxplugin") version "0.0.13"
    id("org.jetbrains.kotlin.jvm")
}

group = "dev.ag6"
version = "1.0.0"

repositories {
    mavenCentral()

    maven {
        url = uri("https://jitpack.io")
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}

tasks.withType<Jar>().configureEach {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.jar {
    manifest {
        attributes(
            "Main-Class" to "dev.ag6.mclauncher.MCLauncherKt"
        )
    }

    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
}

application {
    mainModule.set("dev.ag6.mclauncher")
    mainClass.set("dev.ag6.mclauncher.MCLauncherKt")
}

javafx {
    version = property("javafxVersion") as String
    modules("javafx.controls", "javafx.fxml", "javafx.graphics", "javafx.web", "javafx.media", "javafx.swing")
}

dependencies {
    implementation("fr.brouillard.oss:cssfx:${property("cssfxVersion")}")

    implementation("com.google.code.gson:gson:${property("gsonVersion")}")

    implementation("org.kordamp.ikonli:ikonli-javafx:${property("ikonliVersion")}")

    implementation("io.github.oshai:kotlin-logging-jvm:${property("kotlinLoggingVersion")}")
    implementation("ch.qos.logback:logback-classic:${property("logbackVersion")}")
    implementation("io.github.palexdev:materialfx:${property("materialfxVersion")}")
    implementation("com.squareup.okhttp3:okhttp:${property("okhttpVersion")}")

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${property("coroutinesVersion")}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-javafx:${property("coroutinesVersion")}")
}

kotlin {
    jvmToolchain(21)
}
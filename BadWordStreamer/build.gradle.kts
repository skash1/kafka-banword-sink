import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.22"
    id("com.github.davidmc24.gradle.plugin.avro") version "1.9.1"
    application
}

val koinVersion = "4.0.0"
val jacksonVersion = "2.9.8"
val slf4jVersion = "2.0.13"
val confluentKafkaVersion = "7.4.0"
val kafkaVersion = "3.4.0"
val avroVersion = "1.11.4"
val gsonVersion = "2.10"
val kloggingVersion = "0.7.0"

group = "io.slurm"
version = "1.0.0"

repositories {
    maven {
        url = uri("https://packages.confluent.io/maven/")
    }
    mavenCentral()
}

dependencies {
    implementation(project.dependencies.platform("io.insert-koin:koin-bom:$koinVersion"))
    implementation("io.insert-koin:koin-core")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")
    implementation("org.slf4j:slf4j-api:$slf4jVersion")
    implementation("org.slf4j:slf4j-simple:$slf4jVersion")
    implementation("io.klogging:klogging-jvm:$kloggingVersion")

    /* Add the Kafka dependencies */
    implementation("io.confluent:kafka-avro-serializer:$confluentKafkaVersion")
    implementation("com.google.code.gson:gson:$gsonVersion")
    implementation("org.apache.kafka:kafka-streams:$kafkaVersion")
    implementation("org.apache.kafka:kafka-clients:$kafkaVersion")
    implementation("io.confluent:kafka-streams-avro-serde:$confluentKafkaVersion")
    implementation("org.apache.avro:avro:$avroVersion")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
}

application {
    mainClass.set("BadWordStreamerKt")
}
plugins {
    java
    id("org.springframework.boot") version "3.3.1"
    id("io.spring.dependency-management") version "1.1.5"
    id("jacoco")
}

group = "com.paymybuddy"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-validation:3.3.2")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("nz.net.ultraq.thymeleaf:thymeleaf-layout-dialect:3.3.0")

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    runtimeOnly("org.postgresql:postgresql")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}



//tasks.register<Exec>("npmInstall") {
//    workingDir = file("${projectDir}")
//    commandLine = listOf("npm", "install")
//}
//
//tasks.register<Exec>("npmRunDev") {
//    workingDir = file("${projectDir}")
//    commandLine = listOf("npm", "run", "dev")
//}
//
//tasks.register<Exec>("npmRunBuild") {
//    workingDir = file("${projectDir}")
//    commandLine = listOf("npm", "run", "build")
//}
//
//tasks.named("processResources") {
//    dependsOn("npmRunBuild")
//}
//
//tasks.named("bootRun") {
//    dependsOn("npmRunDev")
//}





tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.test {
    finalizedBy("jacocoTestReport")
}

tasks.named<JacocoReport>("jacocoTestReport") {
    dependsOn("test")
    reports {
        xml.required = false
        csv.required = false
        html.required = true
    }
}
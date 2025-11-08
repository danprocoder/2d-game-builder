plugins {
    application
}

repositories {
    mavenCentral()
}

dependencies {
    // OpenGL dependencies
    implementation("org.jogamp.gluegen:gluegen-rt-main:2.6.0")
    implementation("org.jogamp.jogl:jogl-all-main:2.6.0")

    // Sound dependencies
    implementation("javazoom:jlayer:1.0.1")
    // implementation("com.googlecode.soundlibs:mp3spi:1.9.5.4")
    // implementation("com.googlecode.soundlibs:tritonus-share:0.3.7.4")
    // implementation("com.googlecode.soundlibs:jlayer:1.0.1.4")
    
    // Test dependencies
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

application {
    mainClass.set("com.gamebuilder.GameBuilder")

    applicationDefaultJvmArgs = listOf(
        "--add-exports=java.desktop/sun.awt=ALL-UNNAMED",
        "--add-opens=java.desktop/sun.awt=ALL-UNNAMED"
    )
}

tasks.test {
    useJUnitPlatform()
}

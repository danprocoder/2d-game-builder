plugins {
    application
}

repositories {
    mavenCentral()
}

dependencies {
  implementation("org.jogamp.gluegen:gluegen-rt-main:2.6.0")
  implementation("org.jogamp.jogl:jogl-all-main:2.6.0")
}

application {
    mainClass.set("com.gamebuilder.GameBuilder")
}
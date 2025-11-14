// build.gradle.kts (Project level)
buildscript {
    repositories {
        google()
        mavenCentral()
        maven("https://repository.map.naver.com/archive/maven")
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.2.0") // Android Studio 기본 버전
    }
}

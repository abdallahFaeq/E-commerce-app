buildscript {
    dependencies {
        classpath("com.google.gms:google-services:4.4.1")
    }
}
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.2.0" apply false
    id ("com.android.library") version "7.3.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.10" apply false
    id("com.google.dagger.hilt.android") version "2.51" apply false
    id("androidx.navigation.safeargs.kotlin") version "2.7.6" apply false
    id("com.google.gms.google-services") version "4.4.1" apply false
}
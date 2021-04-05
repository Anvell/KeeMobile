package constants

@Suppress("unused")
object Dependencies {

    object Core {
        const val desugarJdkLibs = "com.android.tools:desugar_jdk_libs:1.1.1"
        const val androidGradlePlugin = "com.android.tools.build:gradle:4.2.0-beta03"
    }

    object Kotlin {
        private const val version = "1.4.31"
        const val gradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:$version"
        const val serialization = "org.jetbrains.kotlin:kotlin-serialization:$version"
        const val serializationJson = "org.jetbrains.kotlinx:kotlinx-serialization-json:1.0.1"
    }

    object Coroutines {
        private const val version = "1.4.2"
        const val core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$version"
        const val android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:$version"
        const val test = "org.jetbrains.kotlinx:kotlinx-coroutines-test:$version"
    }

    object Testing {
        const val junit = "junit:junit:4.13.2"
        const val mockk = "io.mockk:mockk:1.10.6"
        const val coreTesting = "androidx.arch.core:core-testing:2.1.0"
        const val rules = "androidx.test:rules:1.2.0"
        const val strikt = "io.strikt:strikt-core:0.28.2"
        const val kotestAssertions = "io.kotest:kotest-assertions-core-jvm:4.0.7"
    }

    object Compose {
        private const val version = "1.0.0-beta03"
        const val runtime = "androidx.compose.runtime:runtime:$version"
        const val foundation = "androidx.compose.foundation:foundation:$version"
        const val layout = "androidx.compose.foundation:foundation-layout:$version"
        const val ui = "androidx.compose.ui:ui:$version"
        const val icons = "androidx.compose.material:material-icons-extended:$version"
        const val animation = "androidx.compose.animation:animation:$version"
        const val material = "androidx.compose.material:material:$version"
        const val livedata = "androidx.compose.runtime:runtime-livedata:$version"
        const val tooling = "androidx.compose.ui:ui-tooling:$version"
        const val test = "androidx.compose.ui:ui-test:$version"
        const val uiTest = "androidx.compose.ui:ui-test-junit4:$version"

        object Extra {
            const val activity = "androidx.activity:activity-compose:1.3.0-alpha05"
        }
    }

    object Accompanist {
        private const val version = "0.7.0"
        const val flowlayout = "com.google.accompanist:accompanist-flowlayout:$version"
    }

    object AndroidCore {
        const val appcompat = "androidx.appcompat:appcompat:1.3.0-beta01"
        const val coreKtx = "androidx.core:core-ktx:1.3.1"
        const val documentfile = "androidx.documentfile:documentfile:1.0.1"
        const val dynamicAnimation = "androidx.dynamicanimation:dynamicanimation:1.0.0"

        object Widgets {
            const val material = "com.google.android.material:material:1.3.0"
            const val recyclerview = "androidx.recyclerview:recyclerview:1.1.0"
            const val viewpager = "androidx.viewpager2:viewpager2:1.0.0"
            const val constraintlayout = "androidx.constraintlayout:constraintlayout:2.0.4"
        }

        object Navigation {
            private const val version = "2.3.4"
            const val fragment = "androidx.navigation:navigation-fragment-ktx:$version"
            const val ui = "androidx.navigation:navigation-ui-ktx:$version"
        }

        object Fragment {
            private const val version = "1.3.1"
            const val fragment = "androidx.fragment:fragment:$version"
            const val fragmentKtx = "androidx.fragment:fragment-ktx:$version"
        }

        object Lifecycle {
            private const val version = "2.3.0"
            const val runtime = "androidx.lifecycle:lifecycle-runtime-ktx:$version"
            const val extensions = "androidx.lifecycle:lifecycle-extensions:$version"
            const val livedata = "androidx.lifecycle:lifecycle-livedata-ktx:$version"
            const val viewmodel = "androidx.lifecycle:lifecycle-viewmodel-ktx:$version"
        }

        object Hilt {
            private const val version = "1.0.0-alpha03"
            const val work = "androidx.hilt:hilt-work:$version"
            const val viewmodel = "androidx.hilt:hilt-lifecycle-viewmodel:$version"
            const val compiler = "androidx.hilt:hilt-compiler:$version"
        }
    }

    object Dagger {
        private const val version = "2.31.2"
        const val dagger = "com.google.dagger:dagger:$version"
        const val compiler = "com.google.dagger:dagger-compiler:$version"
    }

    object Hilt {
        private const val version = "2.31.2-alpha"
        const val library = "com.google.dagger:hilt-android:$version"
        const val compiler = "com.google.dagger:hilt-android-compiler:$version"
        const val testing = "com.google.dagger:hilt-android-testing:$version"
        const val gradlePlugin = "com.google.dagger:hilt-android-gradle-plugin:$version"
    }

    object RxJava {
        const val rxJava = "io.reactivex.rxjava2:rxjava:2.2.21"
        const val rxAndroid = "io.reactivex.rxjava2:rxandroid:2.1.1"
        const val rxBindings = "com.jakewharton.rxbinding3:rxbinding:3.1.0"
    }

    object Epoxy {
        private const val version = "3.11.0"
        const val epoxy = "com.airbnb.android:epoxy:$version"
        const val dataBinding = "com.airbnb.android:epoxy-databinding:$version"
        const val processor = "com.airbnb.android:epoxy-processor:$version"
    }

    object Keepass {
        const val openkeepass = "com.github.anvell:openkeepass:v0.8.3-SNAPSHOT"
    }

    object Security {
        const val biometric = "androidx.biometric:biometric:1.1.0"
        const val tink = "com.google.crypto.tink:tink-android:1.5.0"
        const val jetpackSecurity = "androidx.security:security-crypto:1.0.0-rc04"
    }

    object Codecs {
        const val commonsCodec = "commons-codec:commons-codec:1.13"
    }

    object Logging {
        const val timber = "com.jakewharton.timber:timber:4.7.1"
    }
}

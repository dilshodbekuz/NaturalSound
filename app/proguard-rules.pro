# ── Umumiy ───────────────────────────────────────────────────────────────────
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# ── Firebase ──────────────────────────────────────────────────────────────────
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.firebase.**
-dontwarn com.google.android.gms.**

# ── Firebase Realtime Database (SoundDto model) ───────────────────────────────
-keep class com.naturalsound.data.model.** { *; }
-keep class com.naturalsound.domain.model.** { *; }

# ── Hilt ──────────────────────────────────────────────────────────────────────
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep @dagger.hilt.android.lifecycle.HiltViewModel class * { *; }
-keepclasseswithmembers class * {
    @javax.inject.Inject <init>(...);
}

# ── Kotlin Coroutines ─────────────────────────────────────────────────────────
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-dontwarn kotlinx.coroutines.**

# ── Kotlin Serialization ──────────────────────────────────────────────────────
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt

# ── Jetpack Compose ───────────────────────────────────────────────────────────
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**

# ── Coil ──────────────────────────────────────────────────────────────────────
-dontwarn coil.**

# ── AndroidX Navigation ───────────────────────────────────────────────────────
-keep class androidx.navigation.** { *; }

# ── Enum lar ──────────────────────────────────────────────────────────────────
-keepclassmembers enum * { *; }

# ── Crash stack trace ─────────────────────────────────────────────────────────
-keep public class * extends java.lang.Exception

# ── Google AdMob / Native Ads ─────────────────────────────────────────────────
# play-services-ads classes (com.google.android.gms.** already kept above,
# but an explicit AdMob block documents intent and guards against future changes)
-keep class com.google.android.gms.ads.** { *; }
-keep class com.google.ads.** { *; }
-dontwarn com.google.android.gms.ads.**
# Keep the NativeAdManager and its ad layout view-binding helpers
-keep class com.naturalsound.ads.** { *; }

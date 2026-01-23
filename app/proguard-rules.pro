# Mantém tudo do seu app
-keep class com.example.comandabar.** { *; }

# Mantém Compose
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**

# Kotlin Metadata
-keep class kotlin.Metadata { *; }

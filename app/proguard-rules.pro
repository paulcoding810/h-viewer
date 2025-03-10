# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-obfuscationdictionary dict.txt
-classobfuscationdictionary dict.txt
-packageobfuscationdictionary dict.txt

### Rhino
-keepattributes Signature
-dontwarn org.mozilla.javascript.**
-keep class org.mozilla.javascript.** { *; }
-keep class org.jsoup.** { *; }
-dontwarn org.jspecify.annotations.NullMarked

### Ktor and OkHttp
-keep class okhttp3.** { *; }
-keep class com.squareup.okhttp3.** { *; }

-keep class io.ktor.** { *; }
-dontwarn java.lang.management.ManagementFactory
-dontwarn java.lang.management.RuntimeMXBean

### Gson
-keep class com.google.gson.** { *; }
-keep class com.paulcoding.hviewer.model.** { *; }
-keep class com.paulcoding.hviewer.js.** { *; }

### Sora editor
-keep class io.github.rosemoe.sora.** { *; }
-keep class org.eclipse.tm4e.** { *; }
-keep class org.joni.ast.** { *; }

### R8
-dontwarn kotlin.Cloneable$DefaultImpls
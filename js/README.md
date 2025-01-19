

## Proguard

```
-keepattributes Signature
-dontwarn org.mozilla.javascript.**
-keep class org.mozilla.javascript.** { *; }
-keep class org.jsoup.** { *; }
-dontwarn org.jspecify.annotations.NullMarked
```
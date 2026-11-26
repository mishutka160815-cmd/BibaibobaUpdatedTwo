-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}
-keep,includedescriptorclasses class com.bibaiboba.app.**$$serializer { *; }
-keepclassmembers class com.bibaiboba.app.** {
    *** Companion;
}
-keepclasseswithmembers class com.bibaiboba.app.** {
    kotlinx.serialization.KSerializer serializer(...);
}
-dontwarn org.slf4j.impl.StaticLoggerBinder

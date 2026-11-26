-keepattributes *Annotation*, InnerClasses, EnclosingMethod, Signature, Exceptions
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations, AnnotationDefault

# Kotlinx Serialization (including R8 full mode)
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}
-if @kotlinx.serialization.Serializable class **
-keepclassmembers class <1> {
    static <1>$Companion Companion;
}
-keep,includedescriptorclasses class com.bibaiboba.app.**$$serializer { *; }
-keep @kotlinx.serialization.Serializable class com.bibaiboba.app.data.model.** { *; }
-keepclassmembers class com.bibaiboba.app.** {
    *** Companion;
}
-keepclasseswithmembers class com.bibaiboba.app.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Retrofit 2 & OkHttp
-keep interface com.bibaiboba.app.data.api.ChatApi { *; }
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-dontwarn javax.annotation.**
-dontwarn kotlin.Unit
-dontwarn retrofit2.-KotlinExtensions
-dontwarn retrofit2.-KotlinExtensions$*
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn org.slf4j.impl.StaticLoggerBinder

# AndroidViewModel reflection instantiation
-keep class com.bibaiboba.app.ui.ChatViewModel {
    public <init>(android.app.Application);
}

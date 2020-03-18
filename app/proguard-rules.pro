-keepattributes Signature
-keepattributes *Annotation*
-keepattributes InnerClasses
-keepattributes Exceptions
-keepattributes Deprecated
-keepattributes SourceFile
-keepattributes LineNumberTable
-keepattributes EnclosingMethod

-renamesourcefileattribute SourceFile

# JSR 305
-dontwarn javax.annotation.**

# Kotlin
-keep class kotlin.reflect.** {
    *;
}

-keep class kotlin.Metadata {
    *;
}

# Activities
-keepnames class * extends androidx.fragment.app.FragmentActivity

# Fragments
-keepnames class * extends androidx.fragment.app.Fragment

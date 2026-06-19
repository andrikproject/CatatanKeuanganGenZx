# Proguard rules
-keep class com.genzx.keuangan.** { *; }
-keepclassmembers class * {
    @androidx.room.* <methods>;
}

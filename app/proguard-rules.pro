## 混淆时所采用的算法
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
-dontwarn android.support.v4.**
-dontwarn android.os.**
## 这里第三方JAR不警告
-dontwarn org.apache.**
-dontwarn com.jauker.**
-dontwarn android.support.**
-dontwarn com.google.zxing.**
-dontwarn com.ldroid.**
-dontwarn com.sun.crypto.**
-dontwarn uk.co.senab.**
-dontwarn android.net.**
-dontwarn com.android.internal.**
-dontwarn com.amap.**
-dontwarn com.autonavi.aps.amapapi.model.**
-dontwarn com.loc.**
-dontwarn com.mob.**
-dontwarn cn.sharesdk.**
-dontwarn com.android.volley.**
## 保持哪些类不被混淆
-keep class android.support.v4.** { *; }
-keep class android.os.** { *; }
-keep class org.xutils.** { *; }
-keep class butterknife.** { *; }
-keep class com.szbc.tool.GetJsonDataUtil
-keep class com.amap.** {*;}
-keep class com.autonavi.aps.amapapi.model.** {*;}
-keep class com.loc.** {*;}
-keep class com.mob.** {*;}
-keep class cn.sharesdk.** {*;}
-keep class com.android.volley.** {*;}
-keep interface org.xutils.** { *; }
-keep interface butterknife.** { *; }
-keep interface com.amap.** { *; }
-keep interface com.autonavi.aps.amapapi.model.** {*;}
-keep interface com.loc.** {*;}
-keep interface com.mob.** {*;}
-keep interface cn.sharesdk.** {*;}
-keep interface com.android.volley.** {*;}
-keep interface de.mindpipe.android.logging.log4j.**
-keep public class * extends android.support.v4.**
# 保持 native 方法不被混淆
-keepclasseswithmembernames class * {
	native <methods>;
}
# 保持自定义控件类不被混淆
-keepclasseswithmembers class * {
	public <init>(android.content.Context,android.util.AttributeSet);
}
# 保持自定义控件类不被混淆
-keepclasseswithmembers class * {
	public <init>(android.content.Context, android.util.AttributeSet, int);
}
#保持类成员
-keepclassmembers class * extends android.app.Activity {
	public void *(android.view.View);
}
# 保持枚举 enum 类不被混淆
-keepclassmembers enum * {
	public static **[] values();
	public static ** valueOf(java.lang.String);
}
# 保持 Parcelable 不被混淆
-keep class * implements android.os.Parcelable {
	public static final android.os.Parcelable$Creator *;
}
## 保护类中的所有方法名
-keepclassmembers class * {
    public <methods>;
}
#
-keep public interface org.xutils.* { *; }
-keepclassmembers class * extends org.xutils.* { *; }
-keepclassmembers class * extends org.xutils.http.RequestParams { *; }
#
## 这里第三方JAR不混淆
-keep class org.apache.** {*;}
-keep class com.jauker.** {*;}
-keep class android.support.** {*;}
-keep class com.google.zxing.** {*;}
-keep class com.ldroid.** {*;}
-keep class com.sun.crypto.** {*;}
-keep class com.nostra13.** {*;}
-keep class uk.co.senab.** {*;}
-keep class android.net.** {*;}
-keep class com.android.internal.** {*;}
#
-keep class android.webkit.WebView
-keep class android.net.http.SslError
-keep class android.webkit.WebViewClient
#
-keep interface org.apache.** {*;}
-keep interface com.jauker.** {*;}
-keep interface android.support.** {*;}
-keep interface com.google.zxing.** {*;}
-keep interface com.ldroid.** {*;}
-keep interface com.sun.crypto.** {*;}
-keep interface uk.co.senab.** {*;}
-keep interface android.net.** {*;}
-keep interface com.android.internal.** {*;}
#
-keepattributes Exceptions,InnerClasses,Signature,Deprecated,Annotation,Synthetic,EnclosingMethod,InnerClasses
#
#
-dontwarn android.webkit.WebView
-dontwarn android.net.http.SslError
-dontwarn android.webkit.WebViewClient
-dontpreverify
-repackageclasses ''
-allowaccessmodification
#-optimizations !code/simplification/arithmetic
-keepattributes *Annotation*
-dontwarn
-keepattributes EnclosingMethod
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider

-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public void set*(...);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keepclassmembers class * implements android.os.Parcelable {
    static android.os.Parcelable$Creator CREATOR;
}
-keepclassmembers class **.R$* {
    public static <fields>;
}
# Keep fragments
-keep public class * extends android.support.v4.app.Fragment
-keep public class * extends android.app.Fragment
# Serializables
-keepnames class * implements java.io.Serializable

-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    !private <fields>;
    !private <methods>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}
# Native Methods
-keepclasseswithmembernames class * {
    native <methods>;
}
# Android Support Library
-keep class android.** {*;}
# Button methods
-keepclassmembers class * {
    public void *ButtonClicked(android.view.View);
}
# Reflection
-keepclassmembers class com.andymcsherry.proguarddemo.SensorDescriptionFragment {
    public void updateFields(com.andymcsherry.proguarddemo.SensorData);
}
# Remove Logging
-assumenosideeffects class android.util.Log {
    public static *** e(...);
    public static *** w(...);
    public static *** wtf(...);
    public static *** d(...);
    public static *** v(...);
}
# bottom navigation
-keep public class android.support.design.widget.BottomNavigationView { *; }
-keep public class android.support.design.internal.BottomNavigationMenuView { *; }
-keep public class android.support.design.internal.BottomNavigationPresenter { *; }
-keep public class android.support.design.internal.BottomNavigationItemView { *; }
-keep public class com.jzxiang.pickerview { *; }
-keep public class com.bigkoo.pickerview { *; }
-keep public class com.google.gson.Gson { *; }

# For using GSON @Expose annotation
-keepattributes *Annotation*
-keepattributes Signature
# Gson specific classes
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.stream.** { *; }
# Application classes that will be serialized/deserialized over Gson
-keep class com.google.gson.examples.android.model.** { *; }

# Prevent proguard from stripping interface information from TypeAdapterFactory,
# JsonSerializer, JsonDeserializer instances (so they can be used in @JsonAdapter)
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer
#alipay
#-libraryjars libs/alipaySDK-20150610.jar
-keep class com.alipay.*{*;}
-keep class com.ta.utdid2.*{*;}
-keep class com.ut.device.*{*;}
-keep interface com.alipay.*{*;}
-keep interface com.ta.utdid2.*{*;}
-keep interface com.ut.device.*{*;}

# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\mywork\adt-bundle-windows-x86_64-20140624\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
#API里面的类，最好都不混淆
-keep public class * extends android.app.Fragment
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.support.v4.**
-keep public class * extends android.support.v7.**
# 保护注解
-keepattributes *Annotation*
-keep class * extends java.lang.annotation.Annotation {*;}
# 泛型与反射
-keepattributes Signature
-keepattributes EnclosingMethod
# 不混淆内部类
-keepattributes InnerClasses
#不混淆bean
-keep class cc.wulian.smarthomev6.entity.** { *; }
-keep class cc.wulian.smarthomev6.support.core.apiunit.bean.** { *; }
-keep class cc.wulian.smarthomev6.support.core.device.** { *; }
-keep class cc.wulian.smarthomev6.support.core.mqtt.bean.** { *; }
-keep class cc.wulian.smarthomev6.support.event.** { *; }
-keep class cc.wulian.smarthomev6.main.device.eques.bean.** { *; }
-keep class cc.wulian.smarthomev6.main.device.device_23.bean.** { *; }
-keep class cc.wulian.smarthomev6.main.device.device_if02.bean.** { *; }
#友盟统计
-keepclassmembers class * {
   public <init> (org.json.JSONObject);
}
-keep public class cc.wulian.smarthomev6.R$*{
public static final int *;
}
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
#image-loader
-keep class com.nostra13.universalimageloader.** {*;}
#okhttp
-dontwarn okhttp3.**
-keep class okhttp3.**{*;}
#okio
-dontwarn okio.**
-keep class okio.**{*;}
#okgo
-dontwarn com.lzy.okgo.**
-keep class com.lzy.okgo.**{*;}
#eventbus
-keepattributes *Annotation*
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }
## Only required if you use AsyncExecutor
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(Java.lang.Throwable);
}
#greenDao
-dontwarn org.greenrobot.greendao.**
-keep class org.greenrobot.greendao.**{*;}
-keepclassmembers class * extends org.greenrobot.greendao.AbstractDao {
public static java.lang.String TABLENAME;
}
-keep class **$Properties
#fastjson
#-libraryjars libs/fastjson-1.2.7.jar
-dontwarn com.alibaba.fastjson.**
-dontskipnonpubliclibraryclassmembers
-dontskipnonpubliclibraryclasses
#pinyin4j
#-libraryjars libs/pinyin4j-2.5.0.jar
-dontwarn demo.**
-keep class demo.**{*;}
-dontwarn net.sourceforge.pinyin4j.**
-keep class net.sourceforge.pinyin4j.**{*;}
-keep class net.sourceforge.pinyin4j.format.**{*;}
-keep class net.sourceforge.pinyin4j.format.exception.**{*;}
#zxing
#-libraryjars libs/zxing-3.2.1.jar
-dontwarn com.google.zxing.**
#友盟share
-dontusemixedcaseclassnames
-dontshrink
-dontoptimize
-dontwarn com.google.android.maps.**
-dontwarn android.webkit.WebView
-dontwarn com.umeng.**
-dontwarn com.tencent.weibo.sdk.**
-dontwarn com.facebook.**
-keep public class javax.**
-keep public class android.webkit.**
-dontwarn android.support.v4.**
-keep enum com.facebook.**
-keepattributes Exceptions,InnerClasses,Signature
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable

-keep public interface com.facebook.**
-keep public interface com.tencent.**
-keep public interface com.umeng.socialize.**
-keep public interface com.umeng.socialize.sensor.**
-keep public interface com.umeng.scrshot.**
-keep class com.android.dingtalk.share.ddsharemodule.** { *; }
-keep public class com.umeng.socialize.* {*;}


-keep class com.facebook.**
-keep class com.facebook.** { *; }
-keep class com.umeng.scrshot.**
-keep public class com.tencent.** {*;}
-keep class com.umeng.socialize.sensor.**
-keep class com.umeng.socialize.handler.**
-keep class com.umeng.socialize.handler.*
-keep class com.umeng.weixin.handler.**
-keep class com.umeng.weixin.handler.*
-keep class com.umeng.qq.handler.**
-keep class com.umeng.qq.handler.*
-keep class UMMoreHandler{*;}
-keep class com.tencent.mm.sdk.modelmsg.WXMediaMessage {*;}
-keep class com.tencent.mm.sdk.modelmsg.** implements   com.tencent.mm.sdk.modelmsg.WXMediaMessage$IMediaObject {*;}
-keep class im.yixin.sdk.api.YXMessage {*;}
-keep class im.yixin.sdk.api.** implements im.yixin.sdk.api.YXMessage$YXMessageData{*;}
-keep class com.tencent.mm.sdk.** {
     *;
    }
-keep class com.tencent.mm.opensdk.** {
   *;
    }
-dontwarn twitter4j.**
-keep class twitter4j.** { *; }

-keep class com.tencent.** {*;}
-dontwarn com.tencent.**
-keep public class com.umeng.com.umeng.soexample.R$*{
    public static final int *;
    }
-keep public class com.linkedin.android.mobilesdk.R$*{
    public static final int *;
        }
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
    }

-keep class com.tencent.open.TDialog$*
-keep class com.tencent.open.TDialog$* {*;}
-keep class com.tencent.open.PKDialog
-keep class com.tencent.open.PKDialog {*;}
-keep class com.tencent.open.PKDialog$*
-keep class com.tencent.open.PKDialog$* {*;}

-keep class com.sina.** {*;}
-dontwarn com.sina.**
-keep class  com.alipay.share.sdk.** {
       *;
    }
-keepnames class * implements android.os.Parcelable {
    public static final ** CREATOR;
    }

-keep class com.linkedin.** { *; }
-keepattributes Signature

#不混淆爱看视频sdk
-keep class com.wulian.sdk.android.ipc.rtcv2.** { *; }
-keep class com.wulian.webrtc.** { *; }

##不混淆移康sdk
-libraryjars libs/armeabi-v7a/libicvss.so
-keep class com.eques.icvss.** { *; }

#Talkingdata SDK
-dontwarn com.tendcloud.tenddata.**
-keep class com.tendcloud.** {*;}
-keep public class com.tendcloud.tenddata.** { public protected *;}
-keepclassmembers class com.tendcloud.tenddata.**{
public void *(***);
}
-keep class com.talkingdata.sdk.TalkingDataSDK {public *;}
-keep class com.apptalkingdata.** {*;}
-keep class dice.** {*; }
-dontwarn dice.**

#科大讯飞语音合成sdk
-keep class com.iflytek.** { *; }

#小物摄像机IOT
-keep class com.yuantuo.netsdk.**{*;}
-keep class com.decoder.util.**{*;}
-keep class com.tutk.** {*;}
-keep class cc.wulian.smarthomev6.main.device.cylincam.bean.** {*;}
-keep class cc.wulian.smarthomev6.main.device.cylincam.server.** {*;}
-keep class cc.wulian.smarthomev6.main.device.cylincam.utils.** {*;}

-keep class com.wulian.lanlibrary.** {*;}

#ijk播放器
-keep class com.classic.ijkplayer.** {*;}
-keep class tv.danmaku.ijk.media.** {*;}

#户外摄像机直连sdk
-keep class com.realtek.simpleconfiglib.** {*;}

#乐橙摄像机sdk
-keep class com.lechange.**{*;}
-keep class com.company.**{*;}

#罗格朗门禁sdk
-dontwarn com.cloudrtc.**
-keep class com.cloudrtc.** {*;}
-keep class org.pjsip.pjsua2.** {*;}
-keep class org.webrtc.** {*;}


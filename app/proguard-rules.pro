-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers

-optimizationpasses 7
-dontpreverify
-forceprocessing

-allowaccessmodification
-optimizations
-dontoptimize
#-keeppackagenames
-ignorewarnings
-optimizations !code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/*
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*,!code/allocation/var
-optimizations !code/simplification/cast,!code/simplification/advanced,!field/*,!class/merging/*,!method/removal/parameter,!method/propagation/parameter
-optimizations !code/allocation/variable
-optimizations !method/inlining/*
-optimizations !code/simplification/arithmetic
-keepattributes *Annotation*
-repackageclasses 'o'
-obfuscationdictionary 'C:\Users\Administrator\Documents\work\NICVPN_V2_SHELL\dicdex.txt'
-classobfuscationdictionary 'C:\Users\Administrator\Documents\work\NICVPN_V2_SHELL\dicdex.txt'
-packageobfuscationdictionary 'C:\Users\Administrator\Documents\work\NICVPN_V2_SHELL\dicdex.txt'
-mergeinterfacesaggressively
-overloadaggressively
-verbose
-keepattributes Signature


-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute ''
# prevent Crashlytics obfuscation
-keep class com.crashlytics.** { *; }
-dontwarn com.crashlytics.**

-keep class com.nicadevelop.nicavpn.System { *; }
-keep class com.nicadevelop.nicavpn.service.** { *; }
-keep class com.nicadevelop.nicavpn.AppoDealAdManager
-keep class com.nicadevelop.nicavpn.tools.Utils
-keep class com.nicadevelop.nicavpn.tools.sharedPreferences.ConfigCert
-keep class com.nicadevelop.nicavpn.Constants
-keep class com.marcoscg.** { *; }

# Constante
-keep class com.nicadevelop.nicavpn.Constants.** {*;}
-keep interface com.nicadevelop.nicavpn.Constants.** { *; }
-keep class com.nicadevelop.nicavpn.Constants.** {*;}
-dontwarn com.nicadevelop.nicavpn.Constants.**
-dontwarn com.nicadevelop.nicavpn.Constants.**

# Constante
-keep class com.nicadevelop.nicavpn.Api_Fetch_Service.** {*;}
-keep interface com.nicadevelop.nicavpn.Api_Fetch_Service.** { *; }
-keep class com.nicadevelop.nicavpn.Api_Fetch_Service.** {*;}
-dontwarn com.nicadevelop.nicavpn.Api_Fetch_Service.**
-dontwarn com.nicadevelop.nicavpn.Api_Fetch_Service.**

# Constante
-keep class com.nicadevelop.nicavpn.StartFragment {*;}
-keep interface com.nicadevelop.nicavpn.StartFragment { *; }
-keep class com.nicadevelop.nicavpn.StartFragment {*;}
-dontwarn com.nicadevelop.nicavpn.StartFragment
-dontwarn com.nicadevelop.nicavpn.StartFragment

-keep class com.nicadevelop.nicavpn.StartFragment.** {*;}
-keep interface com.nicadevelop.nicavpn.StartFragment.** { *; }
-keep class com.nicadevelop.nicavpn.StartFragment.** {*;}
-dontwarn com.nicadevelop.nicavpn.StartFragment.**
-dontwarn com.nicadevelop.nicavpn.StartFragment.**

# NicVPN Tools
-keep class com.nicadevelop.nicavpn.tools.** {*;}
-keep interface com.nicadevelop.nicavpn.tools.** { *; }
-keep class com.nicadevelop.nicavpn.tools.** {*;}
-dontwarn com.nicadevelop.nicavpn.tools.**
-dontwarn com.nicadevelop.nicavpn.tools.**

# Open VPN
-keep class de.blinkt.openvpn.** {*;}
-keep interface de.blinkt.openvpn.** { *; }
-keep class org.spongycastle.** {*;}
-dontwarn org.spongycastle.**
-dontwarn de.blinkt.openvpn.**

# Appodeal
-keep class com.appodeal.ads.** {*;}
-keep interface com.appodeal.ads.** { *; }
-keep class com.appodeal.ads.** {*;}
-dontwarn com.appodeal.ads.**
-dontwarn com.appodeal.ads.**

# V2Ray
-keep class com.v2ray.ang.** {*;}
-keep interface com.v2ray.ang.** { *; }
-keep class com.v2ray.ang.** {*;}
-dontwarn com.v2ray.ang.**
-dontwarn com.v2ray.ang.**
-keep class go.** { *; }
-keep class libv2ray.** { *; }

# ServiceVPN
-keep class com.nicadevelop.nicavpn.service.** {*;}
-keep interface com.nicadevelop.nicavpn.service.** { *; }
-keep class com.nicadevelop.nicavpn.service.** {*;}
-dontwarn com.nicadevelop.nicavpn.service.**
-dontwarn com.nicadevelop.nicavpn.service.**

-keep class libv2ray.** { *;}



# 保留R下面的资源
-keep class **.R$* {*;}
# 保留四大组件，自定义的Application等这些类不被混淆
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.preference.Preference
-keepnames public class * extends androidx.fragment.app.Fragment

# 保证AndroidViewModel构造函数不被混淆
-keepclassmembers class * extends androidx.lifecycle.AndroidViewModel {
    public <init>(android.app.Application);
}
# 保证LifecycleViewModel构造函数不被混淆
-keepclassmembers class * extends cn.cqray.android.lifecycle.LifecycleViewModel {
    public <init>(androidx.lifecycle.LifecycleOwner);
}
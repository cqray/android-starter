# 保留R下面的资源
-keep class **.R$* {*;}
# 保留四大组件，自定义的Application等这些类不被混淆
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.preference.Preference
-keep public class * extends android.view.View
-keep public class * extends androidx.fragment.app.Fragment
-keepnames public class * extends androidx.fragment.app.Fragment

# 保证AndroidViewModel构造函数不被混淆
-keepclassmembers class * extends androidx.lifecycle.AndroidViewModel {
    public <init>(android.app.Application);
}
# 保证LifecycleViewModel构造函数不被混淆
-keepclassmembers class * extends cn.cqray.android.lifecycle.LifecycleViewModel {
    public <init>(androidx.lifecycle.LifecycleOwner);
}

#保持全部实现 Serializable 接口的类成员
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

-keepclassmembers class com.scwang.smart.refresh.layout.SmartRefreshLayout {
    protected boolean mEnableLoadMore;
    protected boolean mEnableRefresh;
    protected boolean mEnableOverScrollDrag;
    protected boolean mManualLoadMore;
}

# ButterKnife混淆代码
-keep class butterknife.* { *; }
-keep class butterknife.internal.* { *; }
-keep class **$$ViewBinder { *; }
-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}
-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}
-dontwarn butterknife.internal.**
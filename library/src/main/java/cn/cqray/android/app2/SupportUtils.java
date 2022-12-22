package cn.cqray.android.app2;

import android.app.Activity;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import org.xmlpull.v1.XmlPullParser;

import cn.cqray.android.app.SupportProvider;
import cn.cqray.android.util.ContextUtils;

class SupportUtils {

    static void checkProvider(SupportProvider provider) {
        if ((provider instanceof AppCompatActivity) || (provider instanceof Fragment)) {
            return;
        }
        throw new RuntimeException(String.format(
                "%s must extends %s or %s.",
                provider.getClass().getName(),
                AppCompatActivity.class.getName(),
                Fragment.class.getName()));
    }

    /**
     * 获取Activity进入动画资源ID
     * @param act Activity实例
     * @return 资源ID
     */
    static int getActivityOpenEnterAnimationResId(@NonNull Activity act) {
        int[] attr = new int[] { android.R.attr.windowAnimationStyle };
        TypedArray array = act.getTheme().obtainStyledAttributes(attr);
        int animationStyleResId = array.getResourceId(0, -1);
        if (animationStyleResId != -1) {
            attr = new int[] { android.R.attr.activityOpenEnterAnimation };
            array = act.getTheme().obtainStyledAttributes(animationStyleResId, attr);
            return array.getResourceId(0, -1);
        }
        return -1;
    }

    /**
     * 获取动画资源的动画时长
     * @param resId 动画资源ID
     * @return 时长
     */
    static int getAnimDurationFromResource(int resId) {
        if (resId == -1) {
            return 0;
        }
        XmlResourceParser parser = ContextUtils.getResources().getAnimation(resId);
        int duration = 0;
        try {
            // 循环直到文档结束
            while (parser.next() != XmlPullParser.END_DOCUMENT) {
                String val = parser.getAttributeValue("http://schemas.android.com/apk/res/android", "duration");
                if (!TextUtils.isEmpty(val)) {
                    try {
                        duration = Math.max(duration, Integer.parseInt(val));
                    } catch (Exception ignore) {}
                }
            }
        } catch (Exception ignored) {}
        parser.close();
        return duration;
    }
}

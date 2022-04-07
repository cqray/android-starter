package cn.cqray.android.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Build;
import android.view.Display;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.blankj.utilcode.util.ActivityUtils;

import cn.cqray.android.Starter;

/**
 * 屏幕管理工具
 * @author Cqray
 */
public class ScreenUtils {

    public static void setFull(Fragment fragment) {
        if (fragment != null) {
            setFull(fragment.getActivity());
        }
    }

    public static void setFull(Fragment fragment, boolean hideNavigation) {
        if (fragment != null) {
            setFull(fragment.getActivity(), hideNavigation);
        }
    }

    public static void setFull(Activity act) {
        if (act != null) {
            setFull(act.getWindow());
        }
    }

    public static void setFull(Activity act, boolean hideNavigation) {
        if (act != null) {
            setFull(act.getWindow(), hideNavigation);
        }
    }

    public static void setFull(Window window) {
        setFull(window, true);
    }

    /**
     * 设置全屏
     * @param window Window窗体
     * @param hideNavigation 是否隐藏虚拟导航栏
     */
    public static void setFull(Window window, final boolean hideNavigation) {
        if (window == null) {
            return;
        }
        final View decorView = window.getDecorView();
        window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (hideNavigation) {
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                int flags = hideNavigation ? View.SYSTEM_UI_FLAG_HIDE_NAVIGATION : 0;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    flags |= View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                            //布局位于状态栏下方
                            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                            //全屏
                            View.SYSTEM_UI_FLAG_FULLSCREEN |
                            //隐藏导航栏
                            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    flags |= View.SYSTEM_UI_FLAG_IMMERSIVE;
                } else {
                    flags |= View.SYSTEM_UI_FLAG_LOW_PROFILE;
                }
                decorView.setSystemUiVisibility(flags);
            }
        });
    }

    public static boolean isFull(@NonNull Window window) {
        return (window.getAttributes().flags & WindowManager.LayoutParams.FLAG_FULLSCREEN)
                == WindowManager.LayoutParams.FLAG_FULLSCREEN;
    }

    public static boolean isFull(Activity act) {
        if (act == null || act.getWindow() == null) {
            return false;
        }
        Window window = act.getWindow();
        return (window.getAttributes().flags & WindowManager.LayoutParams.FLAG_FULLSCREEN)
                == WindowManager.LayoutParams.FLAG_FULLSCREEN;
    }

    public static boolean isFull(Fragment fragment) {
        Activity act = fragment.getActivity();
        return isFull(act);
    }

    public static int getStatusBarHeight() {
        int statusBarHeight = 0;
        int resourceId = Resources.getSystem().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = Resources.getSystem().getDimensionPixelSize(resourceId);
        }
        if (OsUtils.isFlymeOs4x()) {
            return 2 * statusBarHeight;
        }
        return statusBarHeight;
    }

    public static int getWidth() {
        return getContext().getResources().getDisplayMetrics().widthPixels;
    }

    public static int getHeight() {
        return getContext().getResources().getDisplayMetrics().heightPixels;
    }

    public static boolean isNavigationBarShow(Activity activity){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Display display = activity.getWindowManager().getDefaultDisplay();
            Point size = new Point();
            Point realSize = new Point();
            display.getSize(size);
            display.getRealSize(realSize);
            return realSize.y!=size.y;
        }else {
            boolean menu = ViewConfiguration.get(activity).hasPermanentMenuKey();
            boolean back = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
            if(menu || back) {
                return false;
            }else {
                return true;
            }
        }
    }

    public static int getNavigationBarHeight(Activity activity) {
        if (!isNavigationBarShow(activity)){
            return 0;
        }
        Resources resources = activity.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height",
                "dimen", "android");
        //获取NavigationBar的高度
        int height = resources.getDimensionPixelSize(resourceId);
        return height;
    }


    private static Context getContext() {
        Activity act = ActivityUtils.getTopActivity();
        if (act == null) {
            return Starter.getInstance().getContext();
        }
        return act;
    }
}

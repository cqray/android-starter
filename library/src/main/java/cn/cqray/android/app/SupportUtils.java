package cn.cqray.android.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

class SupportUtils {

    public static void checkActivity(AppCompatActivity activity) {
        if (!(activity instanceof SupportProvider)) {
            throw new RuntimeException(String.format(
                    "%s must implements %s.",
                    activity.getClass().getName(),
                    SupportProvider.class.getSimpleName()));
        }
    }

    public static void checkFragment(Fragment fragment) {
        if (!(fragment instanceof SupportProvider)) {
            throw new RuntimeException(String.format(
                    "%s must implements %s.",
                    Fragment.class.getName(),
                    SupportProvider.class.getName()));
        }
    }

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
}

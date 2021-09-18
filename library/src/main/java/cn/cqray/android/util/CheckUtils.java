package cn.cqray.android.util;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import cn.cqray.android.app.SupportDelegateProvider;

/**
 * 检查工具
 * @author Cqray
 */
public class CheckUtils {

    public static void checkDelegateProvider(@NonNull Object provider) {
        String name = provider.getClass().getSimpleName();
        if (!(provider instanceof AppCompatActivity) && !(provider instanceof Fragment)) {
            throw new IllegalArgumentException(name + " must extend AppCompatActivity or Fragment.");
        }
        if (!(provider instanceof SupportDelegateProvider)) {
            throw new IllegalArgumentException(name + " must implements SupportDelegateProvider.");
        }
    }
}

package cn.cqray.android.util;

import android.text.TextUtils;

/**
 * 系统工具
 * @author Cqray
 */
public class OsUtils {

    /**  **/
    public static final String FLYME_OS_4 = "Flyme_OS_4";
    /**  **/
    public static final String VERSION_4_4_4 = "4.4.4";

    public static boolean isFlymeOs4x() {
        String sysVersion = android.os.Build.VERSION.RELEASE;
        if (VERSION_4_4_4.equals(sysVersion)) {
            String sysIncrement = android.os.Build.VERSION.INCREMENTAL;
            String displayId = android.os.Build.DISPLAY;
            if (!TextUtils.isEmpty(sysIncrement)) {
                return sysIncrement.contains(FLYME_OS_4);
            } else {
                return displayId.contains(FLYME_OS_4.replace("_", " "));
            }
        }
        return false;
    }
}

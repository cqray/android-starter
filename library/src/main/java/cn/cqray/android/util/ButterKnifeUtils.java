package cn.cqray.android.util;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;

import androidx.annotation.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * ButterKnife反射调用工具，主要是为了方便
 * 全局绑定控件，需要加混淆规则。
 * @author Cqray
 */
public final class ButterKnifeUtils {

    /** 是否支持ButterKnife **/
    private static boolean sSupport = true;
    private static Class<?> sButterKnifeClass;
    private static Class<?> sUnbindClass;
    private static Method sBindActivityMethod;
    private static Method sBindDialogMethod;
    private static Method sBindViewMethod;
    private static Method sBindObjectViewMethod;
    private static Method sBindObjectDialogMethod;
    private static Method sBindObjectActivityMethod;
    private static Method sUnbindMethod;

    private ButterKnifeUtils() {}

    public static void bind(Activity act) {
        Class<?> btClass = getButterKnifeClass();
        if (btClass != null) {
            try {
                if (sBindActivityMethod == null) {
                    sBindActivityMethod = sButterKnifeClass.getMethod("bind", Activity.class);
                }
                sBindActivityMethod.invoke(null, act);
            } catch (NoSuchMethodException e) {
                sSupport = false;
            } catch (IllegalAccessException e) {
                sSupport = false;
            } catch (InvocationTargetException e) {
                sSupport = false;
            }
        }
    }

    public static void bind(View view) {
        Class<?> btClass = getButterKnifeClass();
        if (btClass != null) {
            try {
                if (sBindViewMethod == null) {
                    sBindViewMethod = sButterKnifeClass.getMethod("bind", View.class);
                }
                sBindViewMethod.invoke(null, view);
            } catch (NoSuchMethodException e) {
                sSupport = false;
            } catch (IllegalAccessException e) {
                sSupport = false;
            } catch (InvocationTargetException e) {
                sSupport = false;
            }
        }
    }

    public static void bind(Dialog dlg) {
        Class<?> btClass = getButterKnifeClass();
        if (btClass != null) {
            try {
                if (sBindDialogMethod == null) {
                    sBindDialogMethod = sButterKnifeClass.getMethod("bind", Dialog.class);
                }
                sBindDialogMethod.invoke(null, dlg);
            } catch (NoSuchMethodException e) {
                sSupport = false;
            } catch (IllegalAccessException e) {
                sSupport = false;
            } catch (InvocationTargetException e) {
                sSupport = false;
            }
        }
    }

    @Nullable
    public static Object bind(Object obj, View source) {
        Class<?> btClass = getButterKnifeClass();
        if (btClass != null) {
            try {
                if (sBindObjectViewMethod == null) {
                    sBindObjectViewMethod = sButterKnifeClass.getMethod("bind", Object.class, View.class);
                }
                return sBindObjectViewMethod.invoke(null, obj, source);
            } catch (NoSuchMethodException e) {
                sSupport = false;
            } catch (IllegalAccessException e) {
                sSupport = false;
            } catch (InvocationTargetException e) {
                sSupport = false;
            }
        }
        return null;
    }

    @Nullable
    public static Object bind(Object obj, Dialog source) {
        Class<?> btClass = getButterKnifeClass();
        if (btClass != null) {
            try {
                if (sBindObjectDialogMethod == null) {
                    sBindObjectDialogMethod = sButterKnifeClass.getMethod("bind", Object.class, Dialog.class);
                }
                return sBindObjectDialogMethod.invoke(null, obj, source);
            } catch (NoSuchMethodException e) {
                sSupport = false;
            } catch (IllegalAccessException e) {
                sSupport = false;
            } catch (InvocationTargetException e) {
                sSupport = false;
            }
        }
        return null;
    }

    @Nullable
    public static Object bind(Object obj, Activity source) {
        Class<?> btClass = getButterKnifeClass();
        if (btClass != null) {
            try {
                if (sBindObjectActivityMethod == null) {
                    sBindObjectActivityMethod = sButterKnifeClass.getMethod("bind", Object.class, Activity.class);
                }
                return sBindObjectActivityMethod.invoke(null, obj, source);
            } catch (NoSuchMethodException e) {
                sSupport = false;
            } catch (IllegalAccessException e) {
                sSupport = false;
            } catch (InvocationTargetException e) {
                sSupport = false;
            }
        }
        return null;
    }

    public static void unbind(Object unBinder) {
        if (sSupport && unBinder != null) {
            try {
                if (sUnbindClass == null) {
                    sUnbindClass = Class.forName("butterknife.Unbinder");
                }
                if (sUnbindMethod == null) {
                    sUnbindMethod = sUnbindClass.getMethod("unbind");
                }
                sUnbindMethod.invoke(unBinder);
            } catch (ClassNotFoundException e) {
                sSupport = false;
            } catch (NoSuchMethodException e) {
                sSupport = false;
            } catch (IllegalAccessException e) {
                sSupport = false;
            } catch (InvocationTargetException e) {
                sSupport = false;
            }
        }
    }

    private static Class<?> getButterKnifeClass() {
        if (sSupport) {
            if (sButterKnifeClass == null) {
                try {
                    sButterKnifeClass = Class.forName("butterknife.ButterKnife");
                } catch (ClassNotFoundException e) {
                    sSupport = false;
                }
            }
        }
        return sButterKnifeClass;
    }
}

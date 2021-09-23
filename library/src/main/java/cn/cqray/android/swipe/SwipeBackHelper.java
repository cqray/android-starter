package cn.cqray.android.swipe;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import java.util.Stack;

import cn.cqray.android.Starter;
import cn.cqray.android.app.StarterProvider;
import cn.cqray.android.util.CheckUtils;

/**
 * 侧滑返回辅助类
 * @author Cqray
 */
public class SwipeBackHelper {

    /** Activity相关堆 **/
    private static final Stack<SwipeBackDelegate> ACTIVITY_DELEGATES = new Stack<>();
    /** Fragment相关堆 **/
    private static final Stack<SwipeBackDelegate> FRAGMENT_DELEGATES = new Stack<>();

    public static void addSwipeDelegate(@NonNull SwipeBackDelegate delegate) {
        StarterProvider provider = delegate.getSupportDelegateProvider();
//        SupportHandler<SwipeBackDelegate> handler = Starter.getInstance().getSwipeBackHandler();
//        if (provider instanceof Activity) {
//            if (!ACTIVITY_DELEGATES.contains(delegate)) {
//                ACTIVITY_DELEGATES.push(delegate);
//                if (handler != null) {
//                    handler.onHandle(provider, delegate);
//                }
//            }
//        } else {
//            if (!FRAGMENT_DELEGATES.contains(delegate)) {
//                FRAGMENT_DELEGATES.push(delegate);
//                if (handler != null) {
//                    handler.onHandle(provider, delegate);
//                }
//            }
//        }
    }

    public static void removeSwipeDelegate(@NonNull SwipeBackDelegate delegate) {
        ACTIVITY_DELEGATES.remove(delegate);
        FRAGMENT_DELEGATES.remove(delegate);
    }

    @Nullable
    private static SwipeBackDelegate getSwipeDelegate(@NonNull StarterProvider provider) {
        for (SwipeBackDelegate delegate : ACTIVITY_DELEGATES) {
            if (delegate.getSupportDelegateProvider() == provider) {
                return delegate;
            }
        }
        return null;
    }

    @Nullable
    static SwipeBackDelegate getPreSwipeDelegate(@NonNull StarterProvider provider) {
        SwipeBackDelegate delegate;
        if ((delegate = getSwipeDelegate(provider)) != null) {
            int preIndex = ACTIVITY_DELEGATES.indexOf(delegate) - 1;
            return preIndex >= 0 ? ACTIVITY_DELEGATES.get(preIndex) : null;
        }
        return null;
    }

    @NonNull
    public static SwipeBackDelegate with(AppCompatActivity activity) {
        CheckUtils.checkDelegateProvider(activity);
        StarterProvider provider = (StarterProvider) activity;
        SwipeBackDelegate delegate = getSwipeDelegate(provider);
        if (delegate == null) {
            delegate = new SwipeBackDelegate(provider);
        }
        return delegate;
    }

    @NonNull
    public static SwipeBackDelegate with(Fragment fragment) {
        CheckUtils.checkDelegateProvider(fragment);
        StarterProvider provider = (StarterProvider) fragment;
        SwipeBackDelegate delegate = getSwipeDelegate(provider);
        if (delegate == null) {
            delegate = new SwipeBackDelegate(provider);
        }
        return delegate;
    }
}

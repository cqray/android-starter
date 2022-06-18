package cn.cqray.android.launch;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;

import java.io.Serializable;
import java.util.ArrayList;

public class Launcher {

    private Class<? extends Activity> activity;
    private Bundle mArguments = new Bundle();

    public static Launcher with(FragmentActivity activity) {

        return new Launcher(activity);
    }

    public static Launcher with(Fragment fragment) {

        return new Launcher(fragment);
    }

    private LifecycleOwner mLifecyclerOwner;

    private Launcher(LifecycleOwner lifecycleOwner) {
        mLifecyclerOwner = lifecycleOwner;
    }

    public Launcher put(Intent intent) {

        return this;
    }

    public Launcher put(String key, byte value) {
        return put(key, (Object) value);
    }

    public Launcher put(String key, char value) {
        return put(key, (Object) value);
    }

    public Launcher put(String key, short value) {
        return put(key, (Object) value);
    }

    public Launcher put(String key, boolean value) {
        return put(key, (Object) value);
    }

    public Launcher put(String key, int value) {
        return put(key, (Object) value);
    }

    public Launcher put(String key, float value) {
        return put(key, (Object) value);
    }

    public Launcher put(String key, double value) {
        return put(key, (Object) value);
    }

    public Launcher put(String key, CharSequence value) {
        return put(key, (Object) value);
    }

    public Launcher put(String key, String value) {
        return put(key, (Object) value);
    }

    public Launcher put(String key, byte [] value) {
        return put(key, (Object) value);
    }

    public Launcher put(String key, char []value) {
        return put(key, (Object) value);
    }

    public Launcher put(String key, short [] value) {
        return put(key, (Object) value);
    }

    public Launcher put(String key, boolean [] value) {
        return put(key, (Object) value);
    }

    public Launcher put(String key, int [] value) {
        return put(key, (Object) value);
    }

    public Launcher put(String key, float [] value) {
        return put(key, (Object) value);
    }

    public Launcher put(String key, double [] value) {
        return put(key, (Object) value);
    }

    public Launcher put(String key, CharSequence [] value) {
        return put(key, (Object) value);
    }

    public Launcher put(String key, String [] value) {
        return put(key, (Object) value);
    }

    public Launcher put(String key, Serializable value) {
        return put(key, (Object) value);
    }

    public Launcher put(String key, Parcelable value) {
        return put(key, (Object) value);
    }

    public Launcher put(String key, Parcelable [] value) {
        mArguments.putParcelableArray(key, value);
        return this;
    }

    public Launcher put(String key, ArrayList<?> value) {
        return put(key, (Object) value);
    }

    private Launcher put(String key, Object value) {
        if (value instanceof Serializable) {
            mArguments.putSerializable(key, (Serializable) value);
        } else if (value instanceof Parcelable) {
            mArguments.putParcelable(key, (Parcelable) value);
        }
        return this;
    }

    public void launch(Class<?> target) {
        FragmentActivity activity = null;
        if (mLifecyclerOwner instanceof FragmentActivity) {
            activity = (FragmentActivity) mLifecyclerOwner;
        } else if (mLifecyclerOwner instanceof Fragment) {
            activity = ((Fragment) mLifecyclerOwner).requireActivity();
        }
        if (activity != null) {
            Intent intent = new Intent(activity, target);
            intent.putExtras(mArguments);
            try {
                new LaunchFragment2(intent).showNow(activity.getSupportFragmentManager(), target.getName());
            } catch (Exception ignore) {}
        }
    }

    public void forward() {

    }
}

package cn.cqray.android.app;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import java.util.Stack;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(prefix = "m")
public class StarterCache2 {

    /** id关键字 **/
    private static final String FRAGMENT_ID_KEY = "starter:fragment_id";

    /** 容器Id **/
    @Getter(AccessLevel.PROTECTED)
    @Setter(AccessLevel.PROTECTED)
    private int mContainerId;
    /** 回退栈 **/
    private final Stack<String> mBackStack = new Stack<>();

    private StarterCache2(FragmentActivity activity) {

    }

    private StarterCache2(Fragment fragment) {

    }

}

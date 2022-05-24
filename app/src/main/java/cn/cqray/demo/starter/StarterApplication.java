package cn.cqray.demo.starter;

import android.app.Application;

import com.github.ybq.android.spinkit.Style;
import com.hjq.toast.ToastUtils;
import com.scwang.smart.refresh.footer.ClassicsFooter;
import com.scwang.smart.refresh.header.ClassicsHeader;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;

import cn.cqray.android.Starter;
import cn.cqray.android.StarterStrategy;
import cn.cqray.android.anim.DefaultHorizontalAnimator;
import cn.cqray.android.anim.DefaultVerticalAnimator;
import cn.cqray.android.state.EmptyAdapter;
import cn.cqray.android.state.ErrorAdapter;

/**
 * @author Admin
 * @date 2021/9/23 10:51
 */
public class StarterApplication extends Application {

    static {
        SmartRefreshLayout.setDefaultRefreshHeaderCreator((context, layout) -> new ClassicsHeader(context));
        SmartRefreshLayout.setDefaultRefreshFooterCreator((context, layout) -> new ClassicsFooter(context));
    }

    @Override
    public void onCreate() {
        super.onCreate();

        StarterStrategy strategy = StarterStrategy.builder()
                .fragmentAnimator(new DefaultHorizontalAnimator())
                //.fragmentBackground(new ColorDrawable(Color.RED))
                //.toolbarTitleTextColor(Color.WHITE)
                //.toolbarTitleTextSize(14)
                .toolbarBackText("返回")
                .toolbarDividerVisible(true)
                .toolbarDividerHeight(2)
                .toolbarBackIconRes(R.drawable.def_back_common_light)
                .toolbarTitleCenter(true)
                .fragmentBackgroundRes(R.color.background)
                .busyAdapter(new MyBusyAdapter().setSpinStyle(Style.DOUBLE_BOUNCE))
                .emptyAdapter(new EmptyAdapter().setImageResource(R.drawable.ic_no_data_search))
                .errorAdapter(new ErrorAdapter())
                .build();

        Starter.getInstance().initialize(this, strategy);

        ToastUtils.init(this);
    }
}

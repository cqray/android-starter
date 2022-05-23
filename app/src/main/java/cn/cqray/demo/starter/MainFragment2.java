package cn.cqray.demo.starter;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import cn.cqray.android.ui.page.PaginationFragment;

/**
 * @author Admin
 * @date 2021/9/23 17:31
 */
public class MainFragment2 extends PaginationFragment<String> {

    @Override
    public void onCreating(@Nullable Bundle savedInstanceState) {
        super.onCreating(savedInstanceState);
        mToolbar.setTitle("测试列表");
        mPaginationDelegate.setPaginationEnable(false);
//        setBusy("123", "456");
    }

    @Override
    protected BaseQuickAdapter<String, ? extends BaseViewHolder> onCreateAdapter() {
        return new BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_string) {
            @Override
            protected void convert(@NotNull BaseViewHolder holder, String s) {
                holder.setText(R.id.text, s);
            }
        };
    }


    @Override
    protected void onRefresh(int pageNum, int pageSize) {
//        int size = mAdapter.getItemCount();
//        int count = size + 20;
//        List<String> list = new ArrayList<>();
//        for (int i = size; i < count; i++) {
//            list.add(String.valueOf(i));
//        }
//        timer(aLong -> {
//
//            if (size < 60) {
//                finish(list);
//            } else {
//                finish(null);
//            }
//        }, 2000);

//        finish(null);
//
//        Log.e("数据", "我设置7777");
//        setEmpty();
//        timer(aLong -> {
//
//            setEmpty();
//        }, 100);
    }

    @Override
    public void onEnterAnimEnd() {
        finish(null);
    }
}

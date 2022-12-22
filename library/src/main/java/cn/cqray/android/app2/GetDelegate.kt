package cn.cqray.android.app2

import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import cn.cqray.android.app.SupportViewModel
import cn.cqray.android.lifecycle.LifecycleViewModelProvider
import java.util.*

class GetDelegate(val provider: GetProvider) {


    lateinit var viewModel: GetNavigationViewModel

    fun onCreated() {

        if (provider is Fragment) {
            val fragment = provider as Fragment
            val activity = fragment.requireActivity()
//            mMainViewModel = GetViewModelProvider(activity).get(SupportViewModel::class.java)
        } else {
            val activity = provider as FragmentActivity
            activity.onBackPressedDispatcher.addCallback(
                activity,
                object : OnBackPressedCallback(true) {
                    override fun handleOnBackPressed() {
//                        mMainViewModel.onBackPressed()
                    }
                })
//            mMainViewModel = GetViewModelProvider((mProvider as AppCompatActivity?)!!).get(
//                SupportViewModel::class.java
//            )
        }

    }

    fun onViewCreated() {}

    fun onDestroyed() {}

    /** 委托缓存  */


    companion object {

        // GetDelegate缓存
        private var delegates: MutableMap<GetProvider, GetDelegate> = Collections.synchronizedMap(HashMap())

        @JvmStatic
        fun get(provider: GetProvider): GetDelegate {
            var delegate = delegates[provider]
            if (delegate == null) {
                delegate = GetDelegate(provider)
                delegates[provider] = delegate
            }
            return delegate
        }

        private fun release(provider: GetProvider) = delegates.remove(provider)
    }

//    private object Holder {
//
//    }
}
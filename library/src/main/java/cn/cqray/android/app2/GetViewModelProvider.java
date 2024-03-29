package cn.cqray.android.app2;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;


import cn.cqray.android.lifecycle.LifecycleViewModel;

/**
 * {@link LifecycleViewModel}提供器，与其配套使用
 * @author Cqray
 */
public class GetViewModelProvider extends ViewModelProvider {

    public GetViewModelProvider(@NonNull ViewModelStoreOwner owner) {
        super(owner, new GetViewModelFactory(owner));
    }

    public GetViewModelProvider(@NonNull Fragment fragment) {
        this((ViewModelStoreOwner) fragment);
    }

    public GetViewModelProvider(@NonNull FragmentActivity activity) {
        this((ViewModelStoreOwner) activity);
    }

//    /** 工厂实例缓存Map **/
//    static final HashMap<ViewModelStoreOwner, GetViewModelFactory> FACTORY_MAP = new HashMap<>();
//
//    /**
//     * 获取对应的工厂实例缓存
//     * @param owner 对应的{@link ViewModelStoreOwner}
//     */
//    @NonNull
//    static GetViewModelFactory getInstance(@NonNull ViewModelStoreOwner owner) {
//        GetViewModelFactory factory = FACTORY_MAP.get(owner);
//        if (factory == null) {
//            factory = new GetViewModelFactory(owner);
//            FACTORY_MAP.put(owner, factory);
//        }
//        return factory;
//    }
//
//    /**
//     * 移除对应的工厂实例缓存
//     * @param key 对应键
//     */
//    static void removeFactory(Object key) {
//        if (key instanceof ViewModelStoreOwner) {
//            FACTORY_MAP.remove(key);
//        }
//    }

//    /**
//     * {@link LifecycleViewModel}工厂
//     */
//    static class GetViewModelFactory extends NewInstanceFactory {
//
//        private final LifecycleOwner mLifecycleOwner;
//
//        public GetViewModelFactory(@NonNull ViewModelStoreOwner owner) {
//            if (owner instanceof LifecycleOwner) {
//                mLifecycleOwner = (LifecycleOwner) owner;
//            } else {
//                throw new IllegalArgumentException("owner must implements LifecycleOwner.");
//            }
//        }
//
//        @NonNull
//        @Override
//        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
//            if (LifecycleViewModel.class.isAssignableFrom(modelClass)) {
//                try {
//                    return modelClass.getConstructor(LifecycleOwner.class).newInstance(mLifecycleOwner);
//                } catch (NoSuchMethodException e) {
//                    throw new RuntimeException("Cannot create an instance of " + modelClass, e);
//                } catch (IllegalAccessException e) {
//                    throw new RuntimeException("Cannot create an instance of " + modelClass, e);
//                } catch (InstantiationException e) {
//                    throw new RuntimeException("Cannot create an instance of " + modelClass, e);
//                } catch (InvocationTargetException e) {
//                    throw new RuntimeException("Cannot create an instance of " + modelClass, e);
//                }
//            }
//            return super.create(modelClass);
//        }
//    }
}

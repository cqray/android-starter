package cn.cqray.android.lifecycle;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

/**
 * {@link LifecycleViewModel}提供器，与其配套使用
 * @author Cqray
 */
public class LifecycleViewModelProvider extends ViewModelProvider {

    public LifecycleViewModelProvider(@NonNull ViewModelStoreOwner owner) {
        super(owner, getInstance(owner));
    }

    public LifecycleViewModelProvider(@NonNull LifecycleOwner owner) {
        this((ViewModelStoreOwner) owner);
    }

    /** 工厂实例缓存Map **/
    static final HashMap<ViewModelStoreOwner, LifecycleViewModelFactory> FACTORY_MAP = new HashMap<>();

    /**
     * 获取对应的工厂实例缓存
     * @param owner 对应的{@link ViewModelStoreOwner}
     */
    @NonNull
    static LifecycleViewModelFactory getInstance(@NonNull ViewModelStoreOwner owner) {
        LifecycleViewModelFactory factory = FACTORY_MAP.get(owner);
        if (factory == null) {
            factory = new LifecycleViewModelFactory(owner);
            FACTORY_MAP.put(owner, factory);
        }
        return factory;
    }

    /**
     * 移除对应的工厂实例缓存
     * @param key 对应键
     */
    static void removeFactory(Object key) {
        if (key instanceof ViewModelStoreOwner) {
            FACTORY_MAP.remove(key);
        }
    }

    /**
     * {@link LifecycleViewModel}工厂
     */
    static class LifecycleViewModelFactory extends ViewModelProvider.NewInstanceFactory {

        private LifecycleOwner mLifecycleOwner;

        public LifecycleViewModelFactory(@NonNull ViewModelStoreOwner owner) {
            if (owner instanceof LifecycleOwner) {
                mLifecycleOwner = (LifecycleOwner) owner;
            } else {
                throw new IllegalArgumentException("owner must implements LifecycleOwner.");
            }
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (LifecycleViewModel.class.isAssignableFrom(modelClass)) {
                try {
                    return modelClass.getConstructor(LifecycleOwner.class).newInstance(mLifecycleOwner);
                } catch (NoSuchMethodException e) {
                    throw new RuntimeException("Cannot create an instance of " + modelClass, e);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Cannot create an instance of " + modelClass, e);
                } catch (InstantiationException e) {
                    throw new RuntimeException("Cannot create an instance of " + modelClass, e);
                } catch (InvocationTargetException e) {
                    throw new RuntimeException("Cannot create an instance of " + modelClass, e);
                }
            }
            return super.create(modelClass);
        }
    }
}

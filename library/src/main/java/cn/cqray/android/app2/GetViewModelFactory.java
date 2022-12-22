package cn.cqray.android.app2;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import java.lang.reflect.InvocationTargetException;

public class GetViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final LifecycleOwner mLifecycleOwner;

    public GetViewModelFactory(@NonNull ViewModelStoreOwner owner) {
        if (owner instanceof LifecycleOwner) {
            mLifecycleOwner = (LifecycleOwner) owner;
        } else {
            throw new IllegalArgumentException("The owner must implements LifecycleOwner.");
        }
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (GetViewModel.class.isAssignableFrom(modelClass)) {
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

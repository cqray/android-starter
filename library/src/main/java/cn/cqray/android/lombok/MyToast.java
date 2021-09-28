package cn.cqray.android.lombok;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Admin
 * @date 2021/9/28 10:58
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.SOURCE)
public @interface MyToast {
}

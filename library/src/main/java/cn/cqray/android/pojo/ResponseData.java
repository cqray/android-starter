package cn.cqray.android.pojo;

import androidx.annotation.NonNull;
import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.List;

import cn.cqray.android.Starter;
import lombok.Getter;

/**
 * 网络请求响应体
 * @author Cqray
 */
@Getter
public class ResponseData<T> implements Serializable {

    @SerializedName(value = "code",alternate = {"ret", "error_code"})
    public String code;
    @SerializedName(value = "message",alternate = {"msg", "info", "reason"})
    public String message;
    @SerializedName(value = "data",alternate = {"result", "value"})
    public T data;

    public int getCodeAsInt() {
        try {
            return Integer.parseInt(code);
        } catch (Exception ignore) {
            return 0;
        }
    }

    public boolean isSucceed() {
        return Starter.getInstance()
                .getStarterStrategy()
                .getResponseDataSucceedCode()
                .contains(code);
    }

    @NonNull
    public static <T> ResponseData<T> success(T data, String message) {
        List<String> codes = Starter.getInstance().getStarterStrategy().getResponseDataSucceedCode();
        ResponseData<T> responseData = new ResponseData<>();
        responseData.data = data;
        responseData.message = message;
        responseData.code = codes.size() > 0 ? codes.get(0) : "200";
        return responseData;
    }

    @NonNull
    public static <T> ResponseData<T> fail(String message) {
        return fail(null, message);
    }

    @NonNull
    public static <T> ResponseData<T> fail(int code, String message) {
        return fail(String.valueOf(code), message);
    }

    @NonNull
    public static <T> ResponseData<T> fail(String code, String message) {
        String failCode = Starter.getInstance().getStarterStrategy().getResponseDataFailCode();
        ResponseData<T> responseData = new ResponseData<>();
        responseData.message = message;
        responseData.code = code == null ? failCode : code;
        return responseData;
    }
}

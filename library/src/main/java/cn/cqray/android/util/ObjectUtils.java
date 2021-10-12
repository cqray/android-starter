package cn.cqray.android.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

/**
 * @author Admin
 * @date 2021/10/12 9:27
 */
@SuppressWarnings("unchecked")
public class ObjectUtils {

    private volatile static Gson sGson;

    public static void main(String[] args) {

        List<Integer> list = Arrays.asList(1, 2, 3, 4);
        System.out.println(list.getClass());
        List<Integer> newList = deepClone(list, new TypeToken<List<Integer>>(){}.getType());
        for (Integer i : newList) {
            System.out.println(i);
        }

        Point p = new Point(10, 100);
        Point pp = deepClone(p, Point.class);
        System.out.println(pp.x + "|" + pp.y);

        System.out.println(ObjectUtils.isEqual("www", "www"));
    }

    static class Point {
        public int x;
        public int y;

        public Point() {
        }

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    /**
     * 序列表深拷贝
     * @param serializable 序列化对象
     * @param <T> 泛型
     */
    public static <T> T deepClone(Serializable serializable) {
        if (serializable == null) {
            return null;
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos;
        try {
            oos = new ObjectOutputStream(bos);
            oos.writeObject(serializable);
            //将当前这个对象写到一个输出流当中，，因为这个对象的类实现了Serializable这个接口，所以在这个类中
            //有一个引用，这个引用如果实现了序列化，那么这个也会写到这个输出流当中
            ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bis);
            return  (T) ois.readObject();
        } catch (IOException | ClassNotFoundException ignored) {
            return null;
        }
    }

    /**
     * 通过Gson深拷贝
     * @param data 数据
     * @param type 类型
     * @param <T> 泛型
     */
    public static <T> T deepClone(T data, Type type) {
        return (T) getGson().fromJson(getGson().toJsonTree(data), type);
    }

    /**
     * 两个对象是否相等
     * @param o1 对象1
     * @param o2 对象2
     */
    public static boolean isEqual(Object o1, Object o2) {
        if (o1 == o2) {
            return true;
        }
        if (o1 != null) {
            return o1.equals(o2);
        }
        return false;
    }

    private static Gson getGson() {
        if (sGson == null) {
            synchronized (ObjectUtils.class) {
                if (sGson == null) {
                    sGson = new Gson();
                }
            }
        }
        return sGson;
    }
}

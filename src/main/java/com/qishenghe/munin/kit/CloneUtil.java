package com.qishenghe.munin.kit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * 拷贝工具
 *
 * @author qishenghe
 * @date 2021/6/7 21:20
 * @change 2021/6/7 21:20 by qishenghe for init
 */
public class CloneUtil {

    /**
     * 深拷贝
     *
     * @param src 拷贝源
     * @return 拷贝副本
     * @since 1.0.0
     * @author qishenghe
     * @date 2021/6/8 9:38
     * @change 2021/6/8 9:38 by qishenghe for init
     */
    public static <T> T deepCopy(T src) {
        try {
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(byteOut);
            out.writeObject(src);

            ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
            ObjectInputStream in = new ObjectInputStream(byteIn);
            @SuppressWarnings("unchecked")
            T dest = (T) in.readObject();
            return dest;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}

package com.hupper.algorithm.util;

import java.io.*;

/**
 * @author lhp@meitu.com
 * @date 2018/7/6 下午1:42
 */
public class SerializeUtil {

    public static byte[] serialize(Object object) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(object);
        return baos.toByteArray();

    }

    public static Object unserialize(byte[] bytes) throws IOException,
            ClassNotFoundException {
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = new ObjectInputStream(bais);
        return ois.readObject();
    }


}

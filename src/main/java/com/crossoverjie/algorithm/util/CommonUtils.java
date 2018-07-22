/**
 * Copyright 2016 benjobs
 * <p>
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.crossoverjie.algorithm.util;

import java.io.File;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;


/**
 * @name:CommonUtil
 * @version: 1.0.0
 * @description: 常用工具类
 */

@SuppressWarnings({"rawtypes", "unchecked"})
public abstract class CommonUtils implements Serializable {

    private static final long serialVersionUID = 6458428317155311192L;

    /**
     * 非空判断
     *
     * @param objs 要判断,处理的对象
     * @return Boolean
     * @see <b>对象为Null返回true,集合的大小为0也返回true,迭代器没有下一个也返回true..</b>
     * @since 1.0
     */
    public static Boolean isEmpty(Object... objs) {

        if (objs == null) {
            return true;
        }

        if (objs.length == 0) return true;

        for (Object obj : objs) {
            if (obj == null) {
                return true;
            }

            // 字符序列集
            if ((obj instanceof CharSequence) && "".equals(obj.toString().trim())) {
                return true;
            }
            // 单列集合
            if (obj instanceof Collection) {
                if (((Collection<?>) obj).isEmpty()) {
                    return true;
                }
            }
            // 双列集合
            if (obj instanceof Map) {
                if (((Map<?, ?>) obj).isEmpty()) {
                    return true;
                }
            }

            if (obj instanceof Iterable) {
                if (((Iterable<?>) obj).iterator() == null || !((Iterable<?>) obj).iterator().hasNext()) {
                    return true;
                }
            }

            // 迭代器
            if (obj instanceof Iterator) {
                if (!((Iterator<?>) obj).hasNext()) {
                    return true;
                }
            }

            // 文件类型
            if (obj instanceof File) {
                if (!((File) obj).exists()) {
                    return true;
                }
            }

            if ((obj instanceof Object[]) && ((Object[]) obj).length == 0) {
                return true;
            }
        }

        return false;
    }

    /**
     * 空判断
     *
     * @param obj 要判断,处理的对象
     * @return Boolean
     * @see <b>与非空相反</b>
     * @since 1.0
     */
    public static Boolean notEmpty(Object... obj) {
        return !isEmpty(obj);
    }



    public static Long toLong(Object val, Long defVal) {
        if (isEmpty(val)) {
            return defVal;
        }
        try {
            return Long.parseLong(val.toString());
        } catch (NumberFormatException e) {
            return defVal;
        }
    }

    public static Long toLong(Object val) {
        return toLong(val, null);
    }

    public static Integer toInt(Object val, Integer defVal) {
        if (isEmpty(val)) {
            return defVal;
        }
        try {
            return Integer.parseInt(val.toString());
        } catch (NumberFormatException e) {
            return defVal;
        }
    }


    public static float toFloat(Object val, float defVal) {
        if (isEmpty(val)) {
            return defVal;
        }
        try {
            return Float.parseFloat(val.toString());
        } catch (NumberFormatException e) {
            return defVal;
        }
    }

    public static Boolean toBoolean(String text, Boolean defVal) {
        if (isEmpty(text)) {
            return false;
        }
        try {
            return Boolean.parseBoolean(text);
        } catch (NumberFormatException e) {
            return defVal;
        }
    }

    public static Boolean toBoolean(String text) {
        return toBoolean(text, false);
    }

    public static Integer toInt(Object val) {
        return toInt(val, null);
    }

    public static Float toFloat(Object val) {
        return toFloat(val, 0f);
    }

    /**
     * 对Null作预处理
     *
     * @param obj   待处理的对象
     * @param clazz 该对象的类型
     * @return T 返回处理后的不为Null的该对象
     * @see <b>对Null作预处理,有效避免NullPointerException</b>
     * @since 1.0
     */
    public static <T> T preparedNull(T obj, Class<?> clazz) {

        if (notEmpty(obj)) {
            return obj;
        }

        AssertUtils.notNull(clazz, "this class must be not null!");

        Object val = null;

        // 单列集合
        if (List.class.isAssignableFrom(clazz)) {
            val = new ArrayList<Object>(0);
        } else if (Set.class.isAssignableFrom(clazz)) {
            val = new HashSet<Object>(0);
        } else if (Map.class.isAssignableFrom(clazz)) {
            val = new HashMap<Object, Object>(0);
        } else {
            try {
                val = clazz.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return (T) val;
    }


    public static List arrayToList(Object source) {
        return Arrays.asList(ObjectUtils.toObjectArray(source));
    }

    public static boolean contains(Iterator iterator, Object element) {
        if (iterator != null) {
            while (iterator.hasNext()) {
                Object candidate = iterator.next();
                if (ObjectUtils.safeEquals(candidate, element)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Check whether the given Enumeration contains the given element.
     *
     * @param enumeration the Enumeration to check
     * @param element     the element to look for
     * @return <code>true</code> if found, <code>false</code> else
     */
    public static boolean contains(Enumeration enumeration, Object element) {
        if (enumeration != null) {
            while (enumeration.hasMoreElements()) {
                Object candidate = enumeration.nextElement();
                if (ObjectUtils.safeEquals(candidate, element)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Check whether the given Collection contains the given element instance.
     * <p>Enforces the given instance to be present, rather than returning
     * <code>true</code> for an equal element as well.
     *
     * @param collection the Collection to check
     * @param element    the element to look for
     * @return <code>true</code> if found, <code>false</code> else
     */
    public static boolean containsInstance(Collection collection, Object element) {
        if (collection != null) {
            for (Object candidate : collection) {
                if (candidate == element) {
                    return true;
                }
            }
        }
        return false;
    }


    public static <A, E extends A> A[] toArray(Enumeration<E> enumeration, A[] array) {
        ArrayList<A> elements = new ArrayList<A>();
        while (enumeration.hasMoreElements()) {
            elements.add(enumeration.nextElement());
        }
        return elements.toArray(array);
    }


    //获取系统名字
    public static String getOsName() {
        return System.getProperties().getProperty("os.name");
    }

    public static boolean isLinuxOs() {
        return getOsName().toUpperCase().startsWith("LIN");
    }

    //是否为Window系统
    public static boolean isWindowOs() {
        return getOsName().toUpperCase().startsWith("WIN");
    }

    //判断类型是否为jdk里自带的原始类型
    public static boolean isPrototype(Class clazz) {
        return clazz.getClassLoader() == null;
    }


    //获取泛型上的具体类型（第一个）
    public static Class<?> getGenericType(Class<?> clazz) {
        return getGenericType(clazz, 0);
    }

    //获取泛型上的具体类型（指定哪个类型）
    public static Class<?> getGenericType(Class<?> clazz, int i) {
        Type type = clazz.getGenericSuperclass();
        if (type instanceof ParameterizedType) {
            Type[] types = ((ParameterizedType) type).getActualTypeArguments();
            return (Class<?>) types[i];
        }
        return null;
    }




    public static String uuid() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /**
     * 生成指定长度的uuid
     *
     * @param len
     * @return
     */
    public static String uuid(int len) {
        StringBuffer sb = new StringBuffer();
        while (sb.length() < len) {
            sb.append(uuid());
        }
        return sb.toString().substring(0, len);
    }


}



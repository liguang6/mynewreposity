package com.byd.wms.business.modules.in.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/** 
 * @author 作者 E-mail: peng.tao1
 * @version 创建时间：2019年1月2日 下午3:02:04 
 * 类说明 
 */
public class CompareObject <T> implements Comparator<T> {

	
	/**
     * 设定任意属性 进行比较
     */
    private final List<String> BEAN_PROPERTIES = new ArrayList<String>();
    private final static String _DESC = "DESC";
//    private final static String BLANK = "";

    public int compare(T o1, T o2) {

        if (o1 == null || o2 == null) {
            return 0;
        }

        // 属性 列表为空 将不处理 返回为对象一致
        if (BEAN_PROPERTIES.isEmpty()) {
            return 0;
        }
        int rel = 0;
        try {

            for (String item : BEAN_PROPERTIES) {
                rel = compare(item, o1, o2);
                if (rel != 0) {
                    break;
                }
            }
        } catch (Throwable ex) {
           
        }
        return rel;
    }

    /**
     * 排序执行
     *
     * @param item
     * @param o1
     * @param o2
     * @return
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws NoSuchFieldException
     */
    protected int compare(String item, T o1, T o2) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException {
        Comparable obj1;
        Comparable obj2;
        String[] properties;
        String field;
        String sortString;
        sortString = null;
        int rel = 0;
        properties = item.split("[ ]+");
        if (properties.length > 1) {
            sortString = properties[1].replaceAll("[ ]*", "");
        }
        field = properties[0];
        obj1 = getComparableProperty(o1, field);
        obj2 = getComparableProperty(o2, field);
        int cmtint = 1;
        if (sortString != null && _DESC.equalsIgnoreCase(sortString)) {
            cmtint *= -1;
        }
        if (obj1 == null && obj2 != null) {
            rel = -1 * cmtint;
        } else if (obj1 != null && obj2 == null) {
            rel = 1 * cmtint;
        } else if (obj1 != null && obj2 != null) {
            rel = obj1.compareTo(obj2) * cmtint;
        }
        return rel;
    }

    /**
     * 设定属性
     *
     * @param property
     * @return
     */
    public CompareObject<T> addProperty(String property) {

        BEAN_PROPERTIES.add(property);
        return this;
    }

    /**
     * 设定属性列表
     *
     * @param properties
     * @return
     */
    public CompareObject<T> addAllProperty(List<String> properties) {

        BEAN_PROPERTIES.addAll(properties);
        return this;
    }

    /**
     * 获取可比较属性
     *
     * @param bean
     * @param property
     * @return
     * @throws NoSuchFieldException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    protected Comparable getComparableProperty(T bean, String property) throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Field field = bean.getClass().getDeclaredField(property);
        field.setAccessible(true);
        return (Comparable) field.get(bean);
    }

}

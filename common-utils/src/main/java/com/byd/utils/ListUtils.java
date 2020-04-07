package com.byd.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.ComparatorUtils;
import org.apache.commons.collections.comparators.ComparableComparator;

/**
 * List工具类
 * @author ren.wei3
 *
 */
public class ListUtils {

	/**
	 * List通用排序方法
	 * @param list 待排序
	 * @param fieldName 排序字段
	 * @param asc 如果为true，是正序；为false，为倒序
	 */
	@SuppressWarnings("unchecked")
    public static <T> void sort(List<T> list, String fieldName, boolean asc) {
        Comparator<?> mycmp = ComparableComparator.getInstance();
        mycmp = ComparatorUtils.nullLowComparator(mycmp); // 允许null
        if (!asc) {
            mycmp = ComparatorUtils.reversedComparator(mycmp); // 逆序
        }
        Collections.sort(list, new BeanComparator(fieldName, mycmp));
    }
	
	/**
	 * List按指定字段分组拆分成多个List
	 * @param list
	 * @param fieldName
	 * @return
	 */
	public static List<List<Map<String, Object>>> getListByGroup(List<Map<String, Object>> list, String fieldName) {
        List<List<Map<String, Object>>> result = new ArrayList<List<Map<String, Object>>>();
        Map<String, List<Map<String, Object>>> map = new TreeMap<String, List<Map<String, Object>>>();
 
        for (Map<String, Object> bean : list) {
            if (map.containsKey(bean.get(fieldName))) {
                List<Map<String, Object>> t = map.get(bean.get(fieldName));
                t.add(bean);
                map.put(bean.get(fieldName).toString(), t);
            } else {
                List<Map<String, Object>> t = new ArrayList<Map<String, Object>>();
                t.add(bean);
                map.put(bean.get(fieldName).toString(), t);
            }
        }
        for (Entry<String, List<Map<String, Object>>> entry : map.entrySet()) {
            result.add(entry.getValue());
        }
        return result;
    }
	
}

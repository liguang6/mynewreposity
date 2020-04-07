package com.byd.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class UsefulMethods {
	public static String toNumbers(List<Integer> repeatColumes){
		String res = "";
		for(Integer number:repeatColumes){
			res +=(String.valueOf(number+1)+",");
		}
		return res.substring(0, res.length()-1);
	}
	
	/**
	 * 检查重复的字段  返回与那几行重复了 字段类型为字符串
	 * @param partsNo
	 * @param currentIndex
	 * @param list
	 * @return
	 * @throws SecurityException 
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 * @throws IllegalAccessException 
	 */
	public static List<Integer> checkCloumRepeat(String colume,String columeValue,int currentIndex,List<?> list,Class<?> cls) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		List<Integer> result = new ArrayList<Integer>();
		for(int i=0;i<list.size();i++){
			if(i == currentIndex) continue;
			String mthName = "get" + colume.substring(0, 1).toUpperCase()+colume.substring(1);
			Method mth = cls.getMethod(mthName);
			String value =  (String) mth.invoke(list.get(i));
			if(value.equals(columeValue)){
				result.add(i);
			}
		}
		return result;
	}
}

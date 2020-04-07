package com.byd.wms.business.modules.query.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ParamsFilterUtils {
	// 需要解析的参数列名
	public static String [] COL_NAME= {"MATNR","LGORT","BATCH","LIFNR",
			"MOVE_TYPE","WMS_NO","REF_WMS_NO","MAT_DOC","WMS_MOVE_TYPE","COST_CENTER",
			"INBOUND_NO","RECEIPT_NO","PO_NO","SAP_OUT_NO","ASN_NO","MO_NO"};
	
	public static Map<String,Object> paramFilter(Map<String,Object> map) {
		// 遍历需要解析的参数列名
		for(String key : COL_NAME) {
			if(map.get(key)!=null) {
				String value=map.get(key).toString();
				// 录入多个条件值，默认用','分隔；转化成数组
				String [] valueArr=value.split(",");
			    
				if(valueArr.length>1) {
					// 单一值（不包含'~'符号）
					if(value.indexOf("~")<0) {
						//选择单一值（不包含'！'符号）
						if(value.indexOf("!")<0) {
							List<Object> list=Arrays.asList(valueArr);
							map.put(key+"_IN", list);
						}else {
							// 排除单一值
							List<Object> list=new ArrayList<Object>();
							for(String obj : valueArr) {
								obj=obj.substring(1, obj.length());
								list.add(obj);
							}
							map.put(key+"_NOT_IN", list);
						}
					}else {// 范围值（用'~'表示）
						// 选择范围
						if(value.indexOf("!")<0) {
							List<Object> list=Arrays.asList(valueArr);
							map.put(key+"_BETWEEN", list);
						}else {
							// 排除范围
							List<Object> list=new ArrayList<Object>();
							for(String obj : valueArr) {
								obj=obj.replace("!","");
								list.add(obj);
							}
							map.put(key+"_NOT_BETWEEN", list);
						}
					}
					map.remove(key);					
				}else {
					//【 demo: !123】
					if(value.indexOf("!")>=0 && value.indexOf("~")<0) {
					    List<Object> list=new ArrayList<Object>();
						list.add(value.substring(1, value.length()));
						map.put(key+"_NOT_IN", list);
						map.remove(key);
					}
					// 【 demo: 123~456】
					if(value.indexOf("!")<0 &&value.indexOf("~")>=0) {
					    List<Object> list=new ArrayList<Object>();
						list.add(value);
						map.put(key+"_BETWEEN", list);
						map.remove(key);
					}
					// 【 demo: !123~!456】
					if(value.indexOf("!")>=0 &&value.indexOf("~")>=0) {
					    List<Object> list=new ArrayList<Object>();
						list.add(value.replace("!",""));
						map.put(key+"_NOT_BETWEEN", list);
						map.remove(key);
					}
				}
			}
		}
		return map;
	}
	
	public static Map<String,Object> test(Map<String,Object> map) {
		if(map.get("MATNR")!=null) {
			String matnr=map.get("MATNR").toString();
			
			if(matnr.indexOf(",")>0) {
				String [] matnrArr=matnr.split(",");
				// 单一值（不包含'~'符号）
				if(matnr.indexOf("~")<0) {
					//选择单一值（不包含'！'符号）
					if(matnr.indexOf("!")<0) {
						List<Object> list=Arrays.asList(matnrArr);
						map.put("MATNR_IN", list);
					}else {
						// 排除单一值
						for(String obj : matnrArr) {
							obj=obj.substring(1, obj.length());
						}
						List<Object> list=Arrays.asList(matnrArr);
						map.put("MATNR_NOT_IN", list);
					}
				}else {// 范围值（用'~'表示）
					// 选择范围
					if(matnr.indexOf("!")<0) {
						List<Object> list=Arrays.asList(matnrArr);
						map.put("MATNR_BETWEEN", list);
					}else {
						// 排除范围
						List<Object> list=new ArrayList<Object>();
						for(String obj : matnrArr) {
							obj=obj.replace("!","");
							list.add(obj);
						}
						map.put("MATNR_NOT_BETWEEN", list);
					}
				}
				map.remove("MATNR");
			}
		}
		return map;
	}
	
/*    public static void main(String [] args) {
    	Map<String,Object> map=new HashMap<String,Object>();
    	String matnr="!123~!456";
    	map.put("MATNR", matnr);
    	Map newMap=paramFilter(map);
    	System.out.println(newMap);
    }*/
}

package com.byd.wms.webservice.common.util;

import java.util.HashMap;
import java.util.Map;

public class WebServiceAccessLock {
	public static String COUNT_TYPE_ADD = "COUNT_ADD";//访问数加一
    public static String COUNT_TYPE_SUB = "COUNT_SUB";//访问数减一
    public static int MAX_COUNT = 200;//最大允许访问数
    public static int currentCount=0;
    private static Map<String, Integer> cateCount = new HashMap<String, Integer>();
    
    /**
     * 线程处理
     * @param cateAcc
     *            访问名
     * @param countType
     *            OPT_ADD/OPT_SU
     */
    public static synchronized void operatelock(String cateAcc, String countType){
		currentCount = 0;
		if (cateCount.get(cateAcc) != null) {
			currentCount = Integer.parseInt(cateCount.get(cateAcc).toString());
		}
		if (COUNT_TYPE_ADD.equals(countType)) {
			currentCount++;
			System.out.println("执行add操作:当前访问数:" + currentCount);
			cateCount.put(cateAcc, Integer.valueOf(currentCount));
		} else if (COUNT_TYPE_SUB.equals(countType)) {
			currentCount--;
			System.out.println("执行sub操作:当前访问数:" + currentCount);
			cateCount.put(cateAcc, Integer.valueOf(currentCount));
		}
    }
}

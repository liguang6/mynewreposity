package com.byd.sap.service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

import com.sap.conn.jco.JCoException;

/**
 * 异步任务
 * @author develop01
 *
 */
public interface IfutureTaskService {
	
	/**
	 * SAP通用过账异步执行方法
	 * @param destination SAP连接对象
	 * @param function SAP函数对象
	 * @return 函数是否执行成功
	 * @throws JCoException
	 * @throws InterruptedException
	 */
	Future<HashMap<String,Object>> sapGoodsmvtCreateExecute(Map<String, Object> paramMap) throws JCoException,InterruptedException;
	
	/**
	 * SAP交货单修改、过账异步执行方法
	 * @param destination SAP连接对象
	 * @param function SAP函数对象
	 * @return 函数是否执行成功
	 * @throws JCoException
	 * @throws InterruptedException
	 */
	Future<HashMap<String,Object>> sapDeliveryChangeExecute(Map<String, Object> paramMap) throws JCoException,InterruptedException;
	
}

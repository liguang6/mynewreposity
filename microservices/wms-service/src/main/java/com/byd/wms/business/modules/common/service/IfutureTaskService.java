package com.byd.wms.business.modules.common.service;

import java.util.Map;
import java.util.concurrent.Future;

/**
 * 异步任务
 * @author develop01
 *
 */
public interface IfutureTaskService {
	
	/**
	 * SAP通用过账异步执行方法
	 * @param paramMap 过账参数
	 * @throws JCoException
	 * @throws InterruptedException
	 */
	public Future<String> sapPost(Map<String, Object> params) throws InterruptedException;
}

package com.byd.wms.business.modules.common.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import org.apache.commons.lang.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.byd.wms.business.modules.common.service.CommonService;
import com.byd.wms.business.modules.common.service.IfutureTaskService;

@Service("futureTaskService")
public class FutureTaskServiceImpl implements IfutureTaskService {
	private static final Logger log = LoggerFactory.getLogger("FutureTaskServiceImpl");
	  @Autowired
	    private CommonService commonService;
	
	/**
	 * SAP过账：检查配置 是否及时过账到SAP，如果配置及时过账，抛数据给SAP过账，如果过账失败，显示报错信息，操作终止
	 * 非及时过账，延迟过账到SAP，先产生和更新WMS相关的数据后，再抛数据给SAP过账
	 * SAP过账失败回滚
	 * @param params
	 * 过账内容键值对： WERKS：过账工厂代码  JZ_DATE：过账日期  PZ_DATE：凭证日期 HEADER_TXT：抬头文本 ,
	 * BUSINESS_NAME：业务类型描述,BUSINESS_TYPE:WMS业务类型,BUSINESS_CLASS:业务分类
	 * 过账行项目List:matList
	 * @return String SAP_NO
	 */
	@Async
	@Override
	@Transactional(rollbackFor=RuntimeException.class)
	public Future<String> sapPost(Map<String, Object> params) throws InterruptedException{
		long start = System.currentTimeMillis();
    	log.info("开启SAP过账线程："+ DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss") );
    	String SAP_NO = "";
    	try {
    		SAP_NO =  commonService.doSapPostBusiness(params);
		} catch (Exception e) {
			SAP_NO = "SAP过账失败："+e.getMessage();
		}
		log.info("SAP过账线程执行完成，SAP凭证号："+SAP_NO+"----"+DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss") );
		log.info("SAP过账共耗时：" + (System.currentTimeMillis() - start)/1000+"秒");
		return new AsyncResult<String>(SAP_NO);
	}
	
    
}

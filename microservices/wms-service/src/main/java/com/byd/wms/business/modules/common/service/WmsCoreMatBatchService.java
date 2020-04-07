package com.byd.wms.business.modules.common.service;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.service.IService;
import com.byd.wms.business.modules.common.entity.WmsCoreMatBatchEntity;

/** 
 * @author 作者 E-mail: peng.tao1@byd.com
 * @version 创建时间：2018年8月21日 上午10:16:11 
 * 类说明 
 */
public interface WmsCoreMatBatchService extends IService<WmsCoreMatBatchEntity> {
	
	void deleteBatch(List<Map<String, Object>> params);
	
	/**
	 * 获取物料批次
	 * @param params
	 * @return
	 */
	List<Map<String, Object>> getBatch(List<Map<String, Object>> params);
}

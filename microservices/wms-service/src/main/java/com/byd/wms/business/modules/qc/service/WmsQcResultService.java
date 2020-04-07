package com.byd.wms.business.modules.qc.service;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.service.IService;
import com.byd.utils.PageUtils;
import com.byd.wms.business.modules.qc.entity.WmsQcResultEntity;

/**
 * 检验结果
 *
 * @author (changsha) byd_infomation_center
 * @email 
 * @date 2018-08-13 15:12:12
 */
public interface WmsQcResultService extends IService<WmsQcResultEntity> {
	List<WmsQcResultEntity> queryRejudgeItems(Map<String,Object> params);
	
	PageUtils queryResultList(Map<String,Object> params);
	
	PageUtils queryDestroyQtyList(Map<String,Object> params);
}


package com.byd.wms.business.modules.common.service;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.service.IService;
import com.byd.utils.PageUtils;
import com.byd.wms.business.modules.common.entity.WmsCBatchPlanRulesEntity;

/** 
 * @author 作者 E-mail: 
 * @version 创建时间：2018年8月21日 下午1:26:27 
 * 类说明 
 */
public interface WmsCBatchPlanRulesService extends IService<WmsCBatchPlanRulesEntity>{
	PageUtils queryPage(Map<String,Object> params);
	
	List<Map<String,Object>> selectBatchCodeList(Map<String, Object> map);
	
	public int merge(List<WmsCBatchPlanRulesEntity> list);
}

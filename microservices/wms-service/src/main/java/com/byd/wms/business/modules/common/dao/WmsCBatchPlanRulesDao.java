package com.byd.wms.business.modules.common.dao;

import java.util.List;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.byd.wms.business.modules.common.entity.WmsCBatchPlanRulesEntity;

/** 
 * @author 作者 E-mail: peng.tao1@byd.com
 * @version 创建时间：2018年8月21日 下午1:18:43 
 * 类说明 
 */
public interface WmsCBatchPlanRulesDao extends BaseMapper<WmsCBatchPlanRulesEntity> {
	public int merge(List<WmsCBatchPlanRulesEntity> list);
}

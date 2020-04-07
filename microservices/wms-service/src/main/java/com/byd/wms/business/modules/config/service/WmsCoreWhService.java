package com.byd.wms.business.modules.config.service;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.service.IService;
import com.byd.utils.PageUtils;
import com.byd.wms.business.modules.config.entity.WmsCoreWhEntity;
/**
 * 仓库信息维护
 * 
 * @author tangj
 * @email 
 * @date 2018年7月31日 上午9:43:39
 */
public interface WmsCoreWhService extends IService<WmsCoreWhEntity>{
    // 分页查询
	PageUtils queryPage(Map<String, Object> params);
	// 批量软删
	public int deleteBatch(List ids);
	// 单条记录软删
	public int delById(Long id);
	
	PageUtils queryPagenew(Map<String, Object> params);
}

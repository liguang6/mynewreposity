package com.byd.wms.business.modules.config.service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.service.IService;
import com.byd.utils.PageUtils;
import com.byd.wms.business.modules.config.entity.WmsCMatDangerEntity;
/**
 * 危化品物料配置表
 * @author tangj
 * @email 
 * @date 2018年08月01日 
 */
public interface WmsCMatDangerService extends IService<WmsCMatDangerEntity>{
 
	PageUtils queryPage(Map<String, Object> params);
	/**
	 * 导入危化品物料验证，区分insert与update数据
	 **/
	public List<Map<String,Object>> validateMatDager(List<String> list);
	
}

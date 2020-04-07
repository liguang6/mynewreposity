package com.byd.wms.business.modules.config.service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.service.IService;
import com.byd.utils.PageUtils;
import com.byd.wms.business.modules.config.entity.WmsCoreWhAreaEntity;
/**
 * 仓库存储类型配置 各工厂仓库存储类型设置
 * @author tangj
 * @email 
 * @date 2018年08月06日 
 */
public interface WmsCoreWhAreaService extends IService<WmsCoreWhAreaEntity>{
 
	PageUtils queryPage(Map<String, Object> params);
	/**
	 * 导入危化品物料验证，区分insert与update数据
	 **/
	public List<Map<String,Object>> validate(List<String> list);
	
}

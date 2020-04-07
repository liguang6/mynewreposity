package com.byd.wms.business.modules.config.service.impl;

import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.byd.utils.PageUtils;
import com.byd.utils.Query;
import com.byd.wms.business.modules.config.dao.WmsCControlSearchDao;
import com.byd.wms.business.modules.config.entity.WmsCControlSearchEntity;
import com.byd.wms.business.modules.config.service.WmsCControlSearchService;
@Service("wmsCControlSearchService")
public class WmsCControlSearchServiceImpl extends ServiceImpl<WmsCControlSearchDao, WmsCControlSearchEntity> implements WmsCControlSearchService{

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		String warehouseCode = params.get("warehouseCode")==null?null:String.valueOf(params.get("warehouseCode"));
		if(StringUtils.isBlank(warehouseCode)) {
			warehouseCode = params.get("WAREHOUSE_CODE")==null?null:String.valueOf(params.get("WAREHOUSE_CODE"));
		}
		
		Page<WmsCControlSearchEntity> page = this.selectPage(new Query<WmsCControlSearchEntity>(params).getPage(),
				new EntityWrapper<WmsCControlSearchEntity>().like("WH_NUMBER", warehouseCode).eq("DEL","0")
				);
		return new PageUtils(page);
	}

}

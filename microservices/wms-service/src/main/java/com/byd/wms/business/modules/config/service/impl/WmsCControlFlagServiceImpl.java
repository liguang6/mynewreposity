package com.byd.wms.business.modules.config.service.impl;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.byd.utils.PageUtils;
import com.byd.utils.Query;
import com.byd.wms.business.modules.config.dao.WmsCControlFlagDao;
import com.byd.wms.business.modules.config.entity.WmsCControlFlagEntity;
import com.byd.wms.business.modules.config.service.WmsCControlFlagService;
@Service("wmsCControlFlagService")
public class WmsCControlFlagServiceImpl extends ServiceImpl<WmsCControlFlagDao, WmsCControlFlagEntity> implements WmsCControlFlagService{

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		String warehouseCode = params.get("warehouseCode")==null?null:String.valueOf(params.get("warehouseCode"));
		if(StringUtils.isBlank(warehouseCode)) {
			warehouseCode = params.get("WAREHOUSE_CODE")==null?null:String.valueOf(params.get("WAREHOUSE_CODE"));
		}
		Page<WmsCControlFlagEntity> page = this.selectPage(new Query<WmsCControlFlagEntity>(params).getPage(),
				new EntityWrapper<WmsCControlFlagEntity>().like("WH_NUMBER", warehouseCode).eq("DEL","0")
				);
		return new PageUtils(page);
	}

}

package com.byd.wms.business.modules.config.service.impl;

import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.byd.utils.PageUtils;
import com.byd.utils.Query;
import com.byd.wms.business.modules.config.dao.WmsCStepLinkageDao;
import com.byd.wms.business.modules.config.entity.WmsCStepLinkageEntity;
import com.byd.wms.business.modules.config.service.WmsCStepLinkageService;
@Service("wmsCStepLinkageService")
public class WmsCStepLinkageServiceImpl extends ServiceImpl<WmsCStepLinkageDao, WmsCStepLinkageEntity> implements WmsCStepLinkageService{

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		String warehouseCode = params.get("warehouseCode")==null?null:String.valueOf(params.get("warehouseCode"));
		if(StringUtils.isBlank(warehouseCode)) {
			warehouseCode = params.get("WAREHOUSE_CODE")==null?null:String.valueOf(params.get("WAREHOUSE_CODE"));
		}
		
		String werksFrom = params.get("werksFrom")==null?null:String.valueOf(params.get("werksFrom"));
		if(StringUtils.isBlank(warehouseCode)) {
			warehouseCode = params.get("WERKS_FROM")==null?null:String.valueOf(params.get("WERKS_FROM"));
		}
		
		String werksTo = params.get("werksTo")==null?null:String.valueOf(params.get("werksTo"));
		if(StringUtils.isBlank(warehouseCode)) {
			warehouseCode = params.get("WERKS_TO")==null?null:String.valueOf(params.get("WERKS_TO"));
		}
		
		
		Page<WmsCStepLinkageEntity> page = this.selectPage(new Query<WmsCStepLinkageEntity>(params).getPage(),
				new EntityWrapper<WmsCStepLinkageEntity>().like("WH_NUMBER", warehouseCode)
				.like("WERKS_FROM", werksFrom)
				.like("WERKS_TO", werksTo)
				.eq("DEL","0")
				);
		
		
		return new PageUtils(page);
	}

}

package com.byd.wms.business.modules.config.service.impl;

import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.byd.utils.PageUtils;
import com.byd.utils.Query;
import com.byd.wms.business.modules.config.dao.WmsCOutRuleDao;
import com.byd.wms.business.modules.config.entity.WmsCOutRuleEntity;
import com.byd.wms.business.modules.config.service.WmsCOutRuleService;
@Service("wmsCOutRuleService")
public class WmsCOutRuleServiceImpl extends ServiceImpl<WmsCOutRuleDao, WmsCOutRuleEntity> implements WmsCOutRuleService{

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		String warehouseCode = params.get("warehouseCode")==null?null:String.valueOf(params.get("warehouseCode"));
		if(StringUtils.isBlank(warehouseCode)) {
			warehouseCode = params.get("WAREHOUSE_CODE")==null?null:String.valueOf(params.get("WAREHOUSE_CODE"));
		}
		
		String outRule = params.get("outRule") == null?null:String.valueOf(params.get("outRule"));
		if(StringUtils.isBlank(outRule)){
			outRule = params.get("OUT_RULE") == null?null:String.valueOf(params.get("OUT_RULE"));
		}
		if(params.get("flag") != null && params.get("flag") .equals("x")){
			params.put("pageSize",String.valueOf(Integer.MAX_VALUE));
		}
		Page<WmsCOutRuleEntity> page = this.selectPage(new Query<WmsCOutRuleEntity>(params).getPage(),
				new EntityWrapper<WmsCOutRuleEntity>().like("WH_NUMBER", warehouseCode).like("OUT_RULE", outRule).eq("DEL","0")
				);
		return new PageUtils(page);
	}

}

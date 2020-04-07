package com.byd.wms.business.modules.config.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.byd.utils.PageUtils;
import com.byd.utils.Query;
import com.byd.wms.business.modules.config.dao.WmsCOutRuleDao;
import com.byd.wms.business.modules.config.dao.WmsCOutRuleDetailDao;
import com.byd.wms.business.modules.config.entity.WmsCOutRuleDetailEntity;
import com.byd.wms.business.modules.config.entity.WmsCOutRuleEntity;
import com.byd.wms.business.modules.config.service.WmsCOutRuleDetailService;
import com.byd.wms.business.modules.config.service.WmsCOutRuleService;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service()
public class WmsCOutRuleDetailServiceImpl extends ServiceImpl<WmsCOutRuleDetailDao, WmsCOutRuleDetailEntity> implements WmsCOutRuleDetailService {

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
		String seqNo = params.get("seqNo") == null?null:String.valueOf(params.get("seqNo"));
		if(StringUtils.isBlank(seqNo)){
			seqNo = params.get("SEQ_NO") == null?null:String.valueOf(params.get("SEQ_NO"));
		}
		Page<WmsCOutRuleDetailEntity> page = this.selectPage(new Query<WmsCOutRuleDetailEntity>(params).getPage(),
				new EntityWrapper<WmsCOutRuleDetailEntity>().like(!StringUtils.isBlank(warehouseCode),"WH_NUMBER", warehouseCode)
						.like(!StringUtils.isBlank(outRule),"OUT_RULE", outRule)
						.eq(!StringUtils.isBlank(seqNo),"seq_no",seqNo)
						.eq("DEL","0")
				);
		if(page.getRecords() == null || page.getRecords().size()==0){
			params.put("pageNo", "1");
			page = this.selectPage(new Query<WmsCOutRuleDetailEntity>(params).getPage(),
					new EntityWrapper<WmsCOutRuleDetailEntity>().like(!StringUtils.isBlank(warehouseCode),"WH_NUMBER", warehouseCode)
							.like(!StringUtils.isBlank(outRule),"OUT_RULE", outRule)
							.eq(!StringUtils.isBlank(seqNo),"seq_no",seqNo)
							.eq("DEL","0")
			);
		}
		return new PageUtils(page);
	}

}

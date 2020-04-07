package com.byd.wms.business.modules.config.service.impl;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.byd.utils.PageUtils;
import com.byd.utils.Query;
import com.byd.wms.business.modules.config.dao.WmsSBestRuleDao;
import com.byd.wms.business.modules.config.entity.WmsSBestRuleEntity;
import com.byd.wms.business.modules.config.service.WmsSBestRuleService;

/**
 * @author ren.wei3
 *
 */
@Service("wmsSBestRuleService")
public class WmsSBestRuleServiceImpl extends ServiceImpl<WmsSBestRuleDao,WmsSBestRuleEntity> implements WmsSBestRuleService{

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		String whNumber = "";
		String werks = "";
		if (params.get("whNumber")!=null && !params.get("whNumber").equals("")) {
			whNumber = params.get("whNumber").toString().trim().toUpperCase();
		}
		if (params.get("werks")!=null && !params.get("werks").equals("")) {
			werks = params.get("werks").toString().trim().toUpperCase();
		}
        Page<WmsSBestRuleEntity> page = this.selectPage(new Query<WmsSBestRuleEntity>(params).getPage(),
				new EntityWrapper<WmsSBestRuleEntity>()
				.like(StringUtils.isNotBlank(werks),"WERKS", werks)
				.like(StringUtils.isNotBlank(whNumber),"WH_NUMBER", whNumber)
				.orderBy("ID")
		);
		return new PageUtils(page);
	}
}

package com.byd.wms.business.modules.pda.service.impl;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.plugins.Page;
import com.byd.utils.PageUtils;
import com.byd.utils.Query;
import com.byd.wms.business.modules.out.dao.WmsOutRequirementHeadDao;
import com.byd.wms.business.modules.pda.service.PdaWmsOutHandoverService;

@Service("pdaWmsOutHandoverService")
public class PdaWmsOutHandoverServiceImpl implements PdaWmsOutHandoverService{

	@Autowired
	private WmsOutRequirementHeadDao headDao;
	
	@Override
	public PageUtils list(Map<String, Object> params) {
		List<Map<String, Object>> list = headDao.selectRequirement(params);
		Page page=new Query<Map<String,Object>>(params).getPage();
		page.setRecords(list);
		page.setTotal(list.size());
		page.setSize(list.size());
        return new PageUtils(page);
	}
	
}

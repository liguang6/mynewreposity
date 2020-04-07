package com.byd.wms.business.modules.config.service.impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.byd.utils.PageUtils;
import com.byd.utils.Query;
import com.byd.wms.business.modules.config.dao.WmsCQcMatDao;
import com.byd.wms.business.modules.config.dao.WmsCQcPlantDao;
import com.byd.wms.business.modules.config.entity.WmsCQcPlantEntity;
import com.byd.wms.business.modules.config.service.WmsCQcPlantService;

@Service("wmsCQcPlantService")
public class WmsCQcPlantServiceImpl extends ServiceImpl<WmsCQcPlantDao, WmsCQcPlantEntity> implements WmsCQcPlantService {
	@Autowired
	private WmsCQcPlantDao wmsCQcPlantDao;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
//    	String werks = String.valueOf(params.get("werks"));
//    	String businessName = String.valueOf(params.get("businessName"));
//    	if(StringUtils.isBlank(werks)){
//			werks = params.get("WERKS") == null ? null : String.valueOf(params.get("WERKS"));
//		}
//    	if(StringUtils.isBlank(businessName)){
//    		businessName = params.get("BUSINESS_NAME") == null ? null : String.valueOf(params.get("BUSINESS_NAME"));
//		}
    	String pageNo=(params.get("pageNo")!=null && !params.get("pageNo").equals(""))?params.get("pageNo").toString():"1";
		String pageSize=(params.get("pageSize")!=null && !params.get("pageSize").equals(""))?params.get("pageSize").toString():"15";
		int start = 0;int end = wmsCQcPlantDao.getListCount(params);
		if(params.get("pageSize")!=null && !params.get("pageSize").equals("")) {
			start = (Integer.valueOf(pageNo)-1) * Integer.valueOf(pageSize);
			end = Integer.valueOf(pageNo)*Integer.valueOf(pageSize);
		}
		params.put("start", start);params.put("end", end);
		List<Map<String,Object>> list=wmsCQcPlantDao.getListByPage(params);
		Page page=new Query<Map<String,Object>>(params).getPage();
		page.setRecords(list);
		page.setTotal(end);
		page.setSize(Integer.valueOf(pageSize));
		page.setCurrent(Integer.valueOf(pageNo));
        return new PageUtils(page);
        
    }

}

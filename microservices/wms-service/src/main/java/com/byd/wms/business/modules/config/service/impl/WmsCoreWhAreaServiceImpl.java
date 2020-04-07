package com.byd.wms.business.modules.config.service.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.byd.utils.PageUtils;
import com.byd.utils.Query;
import com.byd.wms.business.modules.config.dao.WmsCoreWhAreaDao;
import com.byd.wms.business.modules.config.entity.WmsCoreWhAreaEntity;
import com.byd.wms.business.modules.config.service.WmsCoreWhAreaService;

@Service("wmsCoreWhAreaService")
public class WmsCoreWhAreaServiceImpl extends ServiceImpl<WmsCoreWhAreaDao, WmsCoreWhAreaEntity> implements WmsCoreWhAreaService {
	@Autowired
	private WmsCoreWhAreaDao wmsCoreWhAreaDao;
	@Override
	public PageUtils queryPage(Map<String, Object> params) {
        String whNumber = params.get("whNumber") == null?null:String.valueOf(params.get("whNumber"));
        String storageAreaCode = params.get("storageAreaCode") == null?null:String.valueOf(params.get("storageAreaCode"));
        String areaName = params.get("areaName") == null?null:String.valueOf(params.get("areaName"));
        
        if(StringUtils.isBlank(whNumber)){
        	whNumber = params.get("WH_NUMBER") == null?null:String.valueOf(params.get("WH_NUMBER"));
		}
        if(StringUtils.isBlank(storageAreaCode)){
        	storageAreaCode = params.get("storageAreaCode") == null?null:String.valueOf(params.get("storageAreaCode"));
		}
        if(StringUtils.isBlank(areaName)){
        	areaName = params.get("areaName") == null?null:String.valueOf(params.get("areaName"));
		}
        
        Page<WmsCoreWhAreaEntity> page = this.selectPage(new Query<WmsCoreWhAreaEntity>(params).getPage(),
				new EntityWrapper<WmsCoreWhAreaEntity>().like("WH_NUMBER", whNumber).like("AREA_NAME", areaName)
				.like("STORAGE_AREA_CODE", storageAreaCode).eq("DEL","0")
		);
		return new PageUtils(page);
	}

	@Override
	public List<Map<String, Object>> validate(List<String> list) {
		return wmsCoreWhAreaDao.validate(list);
	}

}

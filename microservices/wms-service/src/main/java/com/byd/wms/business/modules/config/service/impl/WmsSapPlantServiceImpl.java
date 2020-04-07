package com.byd.wms.business.modules.config.service.impl;

import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.byd.utils.PageUtils;
import com.byd.utils.Query;
import com.byd.wms.business.modules.config.dao.WmsSapPlantDao;
import com.byd.wms.business.modules.config.entity.WmsSapPlant;
import com.byd.wms.business.modules.config.service.WmsSapPlantService;

@Service
public class WmsSapPlantServiceImpl extends ServiceImpl<WmsSapPlantDao, WmsSapPlant> implements WmsSapPlantService{

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		String werks = params.get("werks") == null?null:String.valueOf(params.get("werks"));
		if(StringUtils.isBlank(werks)){
			werks = params.get("WERKS") == null?null:String.valueOf(params.get("WERKS"));
		}
		String werksName = params.get("werksName") == null?null:String.valueOf(params.get("werksName"));
		if(StringUtils.isBlank(werksName)){
			werksName = params.get("WERKS_NAME") == null?null:String.valueOf(params.get("WERKS_NAME"));
		}
		Page<WmsSapPlant> page = this.selectPage(new Query<WmsSapPlant>(params).getPage(),
				new EntityWrapper<WmsSapPlant>().eq(StringUtils.isNotEmpty(werksName),"werks_name", werksName)
				.eq(StringUtils.isNotEmpty(werks),"werks", werks).eq("del", "0"));
		return new PageUtils(page);
	}

	@Override
	public List<WmsSapPlant> queryPlantListByMap(Map<String, Object> queryParams) {
		if(queryParams.isEmpty()){
			return null;
		}
		return this.selectByMap(queryParams);
	}

}

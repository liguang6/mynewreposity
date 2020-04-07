package com.byd.wms.business.modules.config.service.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.byd.utils.PageUtils;
import com.byd.utils.Query;
import com.byd.wms.business.modules.config.dao.WmsSapPlantLgortDao;
import com.byd.wms.business.modules.config.entity.WmsSapPlantLgortEntity;
import com.byd.wms.business.modules.config.service.WmsSapPlantLgortService;

@Service
public class WmsSapPlantLgortServiceImpl extends ServiceImpl<WmsSapPlantLgortDao, WmsSapPlantLgortEntity> implements WmsSapPlantLgortService{
	@Autowired
	private WmsSapPlantLgortDao wmsSapPlantLgortDao;
	@Override
	public PageUtils queryPage(Map<String, Object> params) {
	   String werks = params.get("werks") == null?null:String.valueOf(params.get("werks"));
	   String lgortName = params.get("lgortName") == null?null:String.valueOf(params.get("lgortName"));
	   Page<WmsSapPlantLgortEntity> page =  this.selectPage(new Query<WmsSapPlantLgortEntity>(params).getPage(),
			   new EntityWrapper<WmsSapPlantLgortEntity>().eq("DEL", "0").eq(StringUtils.isNotEmpty(werks),"WERKS", werks).like(StringUtils.isNotEmpty(lgortName),"LGORT_NAME", lgortName));//标识为删除的不查出来
	   return new PageUtils(page);
	}

	@Override
	public List<Map<String, Object>> validate(List<String> list) {
		return wmsSapPlantLgortDao.validate(list);
	}

	@Override
	public List<Map<String, Object>> selectLgortList(Map<String, Object> param) {
		return wmsSapPlantLgortDao.selectLgortList(param);
	}

}

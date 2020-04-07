package com.byd.wms.business.modules.config.service.impl;


import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.byd.wms.business.modules.config.dao.WmsCAssemblySortTypeDao;
import com.byd.utils.PageUtils;
import com.byd.utils.Query;
import com.byd.wms.business.modules.config.entity.WmsCAssemblySortTypeEntity;
import com.byd.wms.business.modules.config.service.WmsCAssemblySortTypeService;

/** 
 * @author 作者 E-mail: 
 * @version 创建时间：2019年8月2日 下午3:41:45 
 * 类说明 
 */
@Service("wmsCAssemblySortTypeService")
public class WmsCAssemblySortTypeServiceImpl extends ServiceImpl<WmsCAssemblySortTypeDao,WmsCAssemblySortTypeEntity> implements WmsCAssemblySortTypeService{
	@Autowired
	private WmsCAssemblySortTypeDao wmsCAssemblySortTypeDao;
	@Override
	public PageUtils queryPage(Map<String, Object> params) {

		String fWerks = params.get("f_werks") == null?null:String.valueOf(params.get("f_werks"));
		String jisSortType = params.get("jis_sort_type") == null?null:String.valueOf(params.get("jis_sort_type"));
		Page<WmsCAssemblySortTypeEntity> page = this.selectPage(new Query<WmsCAssemblySortTypeEntity>(params).getPage(),
				new EntityWrapper<WmsCAssemblySortTypeEntity>().eq("DEL","0")
				.eq(StringUtils.isNotEmpty(fWerks),"F_WERKS", fWerks)
				.eq(StringUtils.isNotEmpty(jisSortType),"JIS_SORT_TYPE", jisSortType)
		);
		return new PageUtils(page);
	
	}

	

}

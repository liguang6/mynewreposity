package com.byd.wms.business.modules.config.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.byd.utils.PageUtils;
import com.byd.utils.Query;
import com.byd.wms.business.modules.config.dao.WmsCMatLtSampleDao;
import com.byd.wms.business.modules.config.entity.WmsCMatLtSampleEntity;
import com.byd.wms.business.modules.config.service.WmsCMatLtSampleService;


@Service("wmsCMatLtSampleService")
public class WmsCMatLtSampleServiceImpl extends ServiceImpl<WmsCMatLtSampleDao, WmsCMatLtSampleEntity> implements WmsCMatLtSampleService {
	@Autowired
	private WmsCMatLtSampleDao wmsCMatLtSampleDao;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
    	String werks = params.get("werks") == null?null:String.valueOf(params.get("werks"));
    	String matnr = params.get("matnr") == null?null:String.valueOf(params.get("matnr"));
       
    	if(StringUtils.isBlank(werks)){
			werks = params.get("WERKS") == null?null:String.valueOf(params.get("WERKS"));
		}
        if(StringUtils.isBlank(matnr)){
        	matnr = params.get("MATNR") == null?null:String.valueOf(params.get("MATNR"));
        }
    	Page<WmsCMatLtSampleEntity> page = this.selectPage(
                new Query<WmsCMatLtSampleEntity>(params).getPage(),
                new EntityWrapper<WmsCMatLtSampleEntity>().eq(StringUtils.isNotEmpty(werks), "WERKS", werks).
                eq(StringUtils.isNotEmpty(matnr),"MATNR",matnr).eq("DEL","0")
        );

        return new PageUtils(page);
    }

	@Override
	public List<Map<String, Object>> validate(List<String> list) {
		return wmsCMatLtSampleDao.validate(list);
	}

	@Override
	public int merge(List<Map<String, Object>> list) {
		return wmsCMatLtSampleDao.merge(list);
	}

}

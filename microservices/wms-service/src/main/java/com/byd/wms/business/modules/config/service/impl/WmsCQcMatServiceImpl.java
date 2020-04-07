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
import com.byd.wms.business.modules.config.entity.WmsCQcMatEntity;
import com.byd.wms.business.modules.config.service.WmsCQcMatService;


@Service("wmsCQcMatService")
public class WmsCQcMatServiceImpl extends ServiceImpl<WmsCQcMatDao, WmsCQcMatEntity> implements WmsCQcMatService {
	@Autowired
	private WmsCQcMatDao wmsCQcMatDao;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
    	
    	String werks = String.valueOf(params.get("werks"));
    	String matnr = String.valueOf(params.get("matnr"));
    	if(StringUtils.isBlank(werks)){
			werks = params.get("WERKS") == null ? null : String.valueOf(params.get("WERKS"));
		}
    	if(StringUtils.isBlank(matnr)){
    		matnr = params.get("MATNR") == null ? null : String.valueOf(params.get("MATNR"));
		}
        Page<WmsCQcMatEntity> page = this.selectPage(
                new Query<WmsCQcMatEntity>(params).getPage(),
                new EntityWrapper<WmsCQcMatEntity>()
                .like(!"".equals(werks)&&!"null".equals(werks),"werks", werks)
                .like(!"".equals(matnr)&&!"null".equals(matnr),"matnr", matnr)
        );

        return new PageUtils(page);
    }

	@Override
	public List<Map<String, Object>> validate(List<String> list) {
		return wmsCQcMatDao.validate(list);
	}

}

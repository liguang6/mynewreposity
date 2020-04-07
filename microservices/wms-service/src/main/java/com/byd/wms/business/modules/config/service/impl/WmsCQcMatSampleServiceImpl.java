package com.byd.wms.business.modules.config.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.byd.utils.PageUtils;
import com.byd.utils.Query;
import com.byd.utils.StringUtils;
import com.byd.wms.business.modules.config.dao.WmsCQcMatSampleDao;
import com.byd.wms.business.modules.config.entity.WmsCQcMatSampleEntity;
import com.byd.wms.business.modules.config.service.WmsCQcMatSampleService;

@Service("wmsCQcMatSampleService")
public class WmsCQcMatSampleServiceImpl extends ServiceImpl<WmsCQcMatSampleDao, WmsCQcMatSampleEntity> implements WmsCQcMatSampleService {
	@Autowired
	private WmsCQcMatSampleDao wmsCQcMatSampleDao;
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
        Page<WmsCQcMatSampleEntity> page = this.selectPage(
                new Query<WmsCQcMatSampleEntity>(params).getPage(),
                new EntityWrapper<WmsCQcMatSampleEntity>()
                .like(StringUtils.isNotBlank(werks),"werks", werks)
                .like(StringUtils.isNotBlank(matnr),"matnr", matnr)
        );

        return new PageUtils(page);
    }
    @Override
	public List<Map<String, Object>> validate(List<String> list) {
		return wmsCQcMatSampleDao.validate(list);
	}
	@Override
	public void merge(List<WmsCQcMatSampleEntity> list) {
		wmsCQcMatSampleDao.merge(list);
	}
}

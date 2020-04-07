package com.byd.wms.business.modules.config.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.byd.utils.PageUtils;
import com.byd.utils.Query;
import com.byd.utils.R;
import com.byd.wms.business.modules.config.dao.WmsCMatStorageDao;
import com.byd.wms.business.modules.config.entity.WmsCMatStorageEntity;
import com.byd.wms.business.modules.config.entity.WmsSapMaterialEntity;
import com.byd.wms.business.modules.config.formbean.WmsCMatStorageFormbean;
import com.byd.wms.business.modules.config.service.WmsCMatStorageService;
import com.byd.wms.business.modules.config.service.WmsSapMaterialService;

@Service("wmsCMatStorageService")
public class WmsCMatStorageServiceImpl extends ServiceImpl<WmsCMatStorageDao, WmsCMatStorageEntity> implements WmsCMatStorageService {
	@Autowired
	private WmsCMatStorageDao wmsCMatStorageDao;
	@Autowired
    private WmsSapMaterialService wmsSapMaterialService;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
    	String werks = params.get("werks") == null?null:String.valueOf(params.get("werks"));
    	String matnr = params.get("matnr") == null?null:String.valueOf(params.get("matnr"));
        String whNumber = params.get("whNumber") == null?null:String.valueOf(params.get("whNumber"));
        if(StringUtils.isBlank(werks)){
			werks = params.get("WERKS") == null?null:String.valueOf(params.get("WERKS"));
		}
        if(StringUtils.isBlank(matnr)){
        	matnr = params.get("MATNR") == null?null:String.valueOf(params.get("MATNR"));
        }
        
        if(StringUtils.isBlank(whNumber)){
        	whNumber = params.get("WH_NUMBER") == null?null:String.valueOf(params.get("WH_NUMBER"));
        }
        
        Page<WmsCMatStorageEntity> page = this.selectPage(
                new Query<WmsCMatStorageEntity>(params).getPage(),
                new EntityWrapper<WmsCMatStorageEntity>().like("WERKS", werks)
                .like("MATNR", matnr).like("WH_NUMBER", whNumber).eq("DEL", "0")
        );
        
        return new PageUtils(page);
    }

	@Override
	public List<Map<String, Object>> validate(List<String> list) {
		return wmsCMatStorageDao.validate(list);
	}

	@Override
	public PageUtils queryPagenew(Map<String, Object> params) {
		String pageNo=(params.get("pageNo")!=null && !params.get("pageNo").equals(""))?params.get("pageNo").toString():"1";
		String pageSize=(params.get("pageSize")!=null && !params.get("pageSize").equals(""))?params.get("pageSize").toString():"15";
		int start = 0;int end = 6000;
		if(params.get("pageSize")!=null && !params.get("pageSize").equals("")) {
			start = (Integer.valueOf(pageNo)-1) * Integer.valueOf(pageSize);
			end = Integer.valueOf(pageNo)*Integer.valueOf(pageSize);
		}
		params.put("start", start);params.put("end", end);
		List<WmsCMatStorageFormbean> list=wmsCMatStorageDao.getCMatStorageList(params);
		Page page=new Query<Map<String,Object>>(params).getPage();
		page.setRecords(list);
		page.setTotal(wmsCMatStorageDao.getCMatStorageCount(params));
		page.setSize(Integer.valueOf(pageSize));
		page.setCurrent(Integer.valueOf(pageNo));
        return new PageUtils(page);
	}

}

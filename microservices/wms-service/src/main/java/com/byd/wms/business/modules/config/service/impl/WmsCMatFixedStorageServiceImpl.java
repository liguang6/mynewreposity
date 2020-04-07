package com.byd.wms.business.modules.config.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.byd.utils.PageUtils;
import com.byd.utils.Query;
import com.byd.wms.business.modules.config.dao.WmsCMatFixedStorageDao;
import com.byd.wms.business.modules.config.entity.WmsCMatFixedStorageEntity;
import com.byd.wms.business.modules.config.service.WmsCMatFixedStorageService;

/**
 * @author ren.wei3
 *
 */
@Service("wmsCMatFixedStorageService")
public class WmsCMatFixedStorageServiceImpl extends ServiceImpl<WmsCMatFixedStorageDao,WmsCMatFixedStorageEntity> implements WmsCMatFixedStorageService{
	@Autowired
    private WmsCMatFixedStorageDao wmsCMatFixedStorageDao;
    @Override
	public PageUtils queryPage(Map<String, Object> params) {
    	String pageNo=(params.get("pageNo")!=null && !params.get("pageNo").equals(""))?params.get("pageNo").toString():"1";
		String pageSize=(params.get("pageSize")!=null && !params.get("pageSize").equals(""))?params.get("pageSize").toString():"15";
		if (params.get("whNumber")!=null && !params.get("whNumber").equals("")) {
			params.put("whNumber", params.get("whNumber").toString().trim().toUpperCase());
		}
		if (params.get("werks")!=null && !params.get("werks").equals("")) {
			params.put("werks", params.get("werks").toString().trim().toUpperCase());
		}
		if (params.get("matnr")!=null && !params.get("matnr").equals("")) {
			params.put("matnr", params.get("matnr").toString().trim().toUpperCase());
		}
			
		int start = 0;int end = 0;
		int count=wmsCMatFixedStorageDao.getListCount(params);
		if(params.get("pageSize")!=null && !params.get("pageSize").equals("")) {
			start = (Integer.valueOf(pageNo)-1) * Integer.valueOf(pageSize);
			end = Integer.valueOf(pageNo)*Integer.valueOf(pageSize);
		}else {
			end=count;
		}
		params.put("START", start);
		params.put("END", end);
		params.put("orderBy", "id");
//		List<WmsCMatFixedStorageFormbean> list=wmsCMatFixedStorageDao.queryFixedStorage(params);
		List<Map<String, Object>> list=wmsCMatFixedStorageDao.queryFixedStorage(params);
		Page page=new Query<Map<String,Object>>(params).getPage();
		page.setRecords(list);
		page.setTotal(count);
		page.setSize(Integer.valueOf(pageSize));
		page.setCurrent(Integer.valueOf(pageNo));
        return new PageUtils(page);
	}
    
    @Override
	public List<Map<String, Object>> validate(List<String> list) {
		return wmsCMatFixedStorageDao.validate(list);
	}
    
    /**
     * 固定存储，查找空仓位
     * @param params
     * @return
     */
    @Override
    public List<WmsCMatFixedStorageEntity> findEmptyBin(Map<String, Object> params) {
    	
    	return wmsCMatFixedStorageDao.findEmptyBin(params);
    }
    
    /**
     * 固定存储，添加至现有仓位
     * @param params
     * @return
     */
    @Override
    public List<Map<String, Object>> findAlreadyBin(Map<String, Object> params) {
    	
    	return wmsCMatFixedStorageDao.findAlreadyBin(params);
    }
}

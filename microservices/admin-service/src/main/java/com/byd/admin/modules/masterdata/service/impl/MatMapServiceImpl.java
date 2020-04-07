package com.byd.admin.modules.masterdata.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.plugins.Page;
import com.byd.admin.modules.masterdata.dao.MatMapDao;
import com.byd.admin.modules.masterdata.service.MatMapService;
import com.byd.utils.PageUtils;
import com.byd.utils.Query;
import com.byd.utils.datasources.DataSourceNames;
import com.byd.utils.datasources.annotation.DataSource;

/***
 * 品质检验标准
 * @author thw
 * @date 2019-09-03 16:12:06
 */
@Service("matMapService")
public class MatMapServiceImpl implements MatMapService{
	@Autowired
	private MatMapDao matMapDao;

	/**
	 * 获取物料图纸存储地址URL
	 * @param condMap material_no/MATERIAL_NO：图号
	 * @return
	 */
	@Override
	@DataSource(name = DataSourceNames.THIRD)
	public String getMatMapUrl(Map<String, Object> condMap) {
		return matMapDao.getMatMapUrl(condMap);
	}
	
	@Override
	@DataSource(name = DataSourceNames.THIRD)
    public PageUtils queryPmdMapPage(Map<String, Object> params) {
    	String pageNo=(params.get("pageNo")!=null && !params.get("pageNo").equals(""))?params.get("pageNo").toString():"1";
		String pageSize=(params.get("pageSize")!=null && !params.get("pageSize").equals(""))?params.get("pageSize").toString():"15";
		int start = 0;int end = 0;
		int count=matMapDao.getPmdMapCount(params);

		if(params.get("pageSize")!=null && !params.get("pageSize").equals("")) {
			start = (Integer.valueOf(pageNo)-1) * Integer.valueOf(pageSize);
			end = Integer.valueOf(pageNo)*Integer.valueOf(pageSize);
		}else {
			end = Integer.valueOf(pageSize);
		}

		params.put("START", start);
		params.put("END", end);
		List<Map<String,Object>> list=matMapDao.getPmdMapList(params);
		Page page=new Query<Map<String,Object>>(params).getPage();
		page.setRecords(list);
		page.setTotal(count);
		page.setSize(Integer.valueOf(pageSize));
		page.setCurrent(Integer.valueOf(pageNo));
        return new PageUtils(page);
    }
    
	@Override
	@DataSource(name = DataSourceNames.THIRD)
    public List<Map<String,Object>> getPmdMapList(Map<String, Object> params){
    	return matMapDao.getPmdMapList(params);
    }
	
}

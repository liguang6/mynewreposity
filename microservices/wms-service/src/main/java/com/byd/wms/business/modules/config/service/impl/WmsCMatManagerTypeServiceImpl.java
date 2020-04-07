package com.byd.wms.business.modules.config.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.byd.utils.PageUtils;
import com.byd.utils.Query;
import com.byd.wms.business.modules.config.entity.WmsCMatManagerTypeEntity;
import com.byd.wms.business.modules.config.service.WmsCMatManagerTypeService;
import com.byd.wms.business.modules.config.dao.WmsCMatManagerTypeDao;

@Service("wmsCMatManagerTypeService")
public class WmsCMatManagerTypeServiceImpl extends ServiceImpl<WmsCMatManagerTypeDao, WmsCMatManagerTypeEntity> implements WmsCMatManagerTypeService {
	@Autowired
    private WmsCMatManagerTypeDao wmsCMatManagerTypeDao;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
    	String pageNo=(params.get("pageNo")!=null && !params.get("pageNo").equals(""))?params.get("pageNo").toString():"1";
		String pageSize=(params.get("pageSize")!=null && !params.get("pageSize").equals(""))?params.get("pageSize").toString():"15";
		int start = 0;int end = 0;
		int count=wmsCMatManagerTypeDao.getListCount(params);
		if(params.get("pageSize")!=null && !params.get("pageSize").equals("")) {
			start = (Integer.valueOf(pageNo)-1) * Integer.valueOf(pageSize);
			end = Integer.valueOf(pageNo)*Integer.valueOf(pageSize);
		}else {
			end=count;
		}
		params.put("START", start);params.put("END", end);
		List<Map<String,Object>> list=wmsCMatManagerTypeDao.getListByPage(params);
		Page page=new Query<Map<String,Object>>(params).getPage();
		page.setRecords(list);
		page.setTotal(count);
		page.setSize(Integer.valueOf(pageSize));
		page.setCurrent(Integer.valueOf(pageNo));
        return new PageUtils(page);
    }
    public List<Map<String,Object>>getLgortSelect(Map<String,Object> param){
    	return wmsCMatManagerTypeDao.getLgortSelect(param);
    }
	@Override
	public int batchSave(List<WmsCMatManagerTypeEntity> list) {
		// TODO Auto-generated method stub
		return wmsCMatManagerTypeDao.merge(list);
	}
}

package com.byd.wms.business.modules.config.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.byd.utils.PageUtils;
import com.byd.utils.Query;
import com.byd.wms.business.modules.config.dao.WmsCMatManagerDao;
import com.byd.wms.business.modules.config.entity.WmsCMatManagerEntity;
import com.byd.wms.business.modules.config.service.WmsCMatManagerService;

@Service("wmsCMatManagerService")
public class WmsCMatManagerServiceImpl extends ServiceImpl<WmsCMatManagerDao, WmsCMatManagerEntity> implements WmsCMatManagerService {
	@Autowired
    private WmsCMatManagerDao wmsCMatManagerDao;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        
    	String pageNo=(params.get("pageNo")!=null && !params.get("pageNo").equals(""))?params.get("pageNo").toString():"1";
		String pageSize=(params.get("pageSize")!=null && !params.get("pageSize").equals(""))?params.get("pageSize").toString():"15";
		int start = 0;int end = 0;
		int count=wmsCMatManagerDao.getListCount(params);
		if(params.get("pageSize")!=null && !params.get("pageSize").equals("")) {
			start = (Integer.valueOf(pageNo)-1) * Integer.valueOf(pageSize);
			end = Integer.valueOf(pageNo)*Integer.valueOf(pageSize);
		}else {
			end=count;
		}
		params.put("START", start);params.put("END", end);
		List<Map<String,Object>> list=wmsCMatManagerDao.getListByPage(params);
		Page page=new Query<Map<String,Object>>(params).getPage();
		page.setRecords(list);
		page.setTotal(count);
		page.setSize(Integer.valueOf(pageSize));
		page.setCurrent(Integer.valueOf(pageNo));
        return new PageUtils(page);
    }
	@Override
	public List<Map<String, Object>> validateMat(Map<String, Object> param) {
		// TODO Auto-generated method stub
		return wmsCMatManagerDao.validateMat(param);
	}
	@Override
	public List<Map<String, Object>> validateLifnr(Map<String,Object> param) {
		// TODO Auto-generated method stub
		return wmsCMatManagerDao.validateLifnr(param);
	}
	@Override
	public int batchSave(List<WmsCMatManagerEntity> list) {
		// TODO Auto-generated method stub
		return wmsCMatManagerDao.merge(list);
	}
	@Override
	public List<String> validateAuthorizeCode(Map<String,Object> param) {
		// TODO Auto-generated method stub
		List<String> retult=new ArrayList<String>();
		List<Map<String,Object>> list =wmsCMatManagerDao.validateAuthorizeCode(param);
		for(Map<String,Object> entity : list) {
			retult.add(entity.get("AUTHORIZE_CODE").toString());
		}
		return retult;
	}

}

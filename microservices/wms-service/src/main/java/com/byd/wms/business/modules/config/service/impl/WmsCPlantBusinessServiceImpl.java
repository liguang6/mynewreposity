package com.byd.wms.business.modules.config.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.byd.utils.PageUtils;
import com.byd.utils.Query;
import com.byd.wms.business.modules.config.dao.WmsCPlantBusinessDao;
import com.byd.wms.business.modules.config.entity.WmsCPlantBusinessEntity;
import com.byd.wms.business.modules.config.entity.WmsCQcResultEntity;
import com.byd.wms.business.modules.config.service.WmsCPlantBusinessService;


@Service("wmsCPlantBusinessService")
public class WmsCPlantBusinessServiceImpl extends ServiceImpl<WmsCPlantBusinessDao, WmsCPlantBusinessEntity> implements WmsCPlantBusinessService {
	@Autowired
	private WmsCPlantBusinessDao wmsCPlantBusinessDao;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
    	String pageNo=(params.get("pageNo")!=null && !params.get("pageNo").equals(""))?params.get("pageNo").toString():"1";
		String pageSize=(params.get("pageSize")!=null && !params.get("pageSize").equals(""))?params.get("pageSize").toString():"15";
		int count=wmsCPlantBusinessDao.getListCount(params);
		int start = 0;int end = 0;
		if(params.get("pageSize")!=null && !params.get("pageSize").equals("")) {
			start = (Integer.valueOf(pageNo)-1) * Integer.valueOf(pageSize);
			end = Integer.valueOf(pageNo)*Integer.valueOf(pageSize);
		}else {
			end=count;
		}
		params.put("start", start);params.put("end", end);
		List<Map<String,Object>> list=wmsCPlantBusinessDao.getListByPage(params);
		Page page=new Query<Map<String,Object>>(params).getPage();
		page.setRecords(list);
		page.setTotal(count);
		page.setSize(Integer.valueOf(pageSize));
		page.setCurrent(Integer.valueOf(pageNo));
		if(page.getRecords().size()==0&&page.getCurrent()!=1) {
        	pageNo="1";
        	if(params.get("pageSize")!=null && !params.get("pageSize").equals("")) {
    			start = (Integer.valueOf(pageNo)-1) * Integer.valueOf(pageSize);
    			end = Integer.valueOf(pageNo)*Integer.valueOf(pageSize);
    		}else {
    			end=count;
    		}
        	params.put("start", start);params.put("end", end);
    		list=wmsCPlantBusinessDao.getListByPage(params);
    		page=new Query<Map<String,Object>>(params).getPage();
    		page.setRecords(list);
    		page.setTotal(count);
    		page.setSize(Integer.valueOf(pageSize));
    		page.setCurrent(Integer.valueOf(pageNo));
		}
        return new PageUtils(page);
    }

	@Override
	public long getMaxSortNo(String werks) {
		return wmsCPlantBusinessDao.getMaxSortNo(werks);
	}

	@Override
	public List<Map<String, Object>> getWmsBusinessCode(String businessCode) {
		return wmsCPlantBusinessDao.getWmsBusinessCode(businessCode);
	}

	@Override
	public List<Map<String, Object>> validate(List<String> list) {
		return wmsCPlantBusinessDao.validate(list);
	}

	@Override
	public Map<String, Object> getById(long id) {
		return wmsCPlantBusinessDao.getById(id);
	}

	@Override
	public List<Map<String, Object>> getWmsBusinessCodeList(Map<String, Object> params) {
		return wmsCPlantBusinessDao.getWmsBusinessCodeList(params);
	}

	@Override
	public void saveCopyData(List<Map<String, Object>> list) {
		String werks=list.get(0).get("WERKS").toString();
		List<Map<String,Object>> addList=new ArrayList<Map<String,Object>>();
		List<Map<String,Object>> updateList=new ArrayList<Map<String,Object>>();
		List<WmsCPlantBusinessEntity> resultList=wmsCPlantBusinessDao.selectList(new EntityWrapper<WmsCPlantBusinessEntity>()
							.eq("WERKS", werks));
		boolean isExsist=false;
        for(Map<String,Object> object : list) {
        	String newBusinessCode=object.get("BUSINESSCODE").toString();
        	for(WmsCPlantBusinessEntity entity : resultList) {
        		String oldBusinessCode=entity.getBusinessCode();
        		if(newBusinessCode.equals(oldBusinessCode)) {
        			// 系统已存在，执行更新
        			object.put("ID", entity.getId());
        			updateList.add(object);
        			isExsist=true;
        			break;
        		}
        	}
        	// 不存在，就插入
        	if(!isExsist) {
        		addList.add(object);
        	}
        }
        if(addList.size()>0) {
        	wmsCPlantBusinessDao.saveCopyData(addList);
        }
        if(updateList.size()>0) {
        	wmsCPlantBusinessDao.updateCopyData(updateList);
        }
	}

}

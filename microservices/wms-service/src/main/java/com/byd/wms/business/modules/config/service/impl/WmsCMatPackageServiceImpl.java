package com.byd.wms.business.modules.config.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.plugins.Page;
import com.byd.utils.PageUtils;
import com.byd.utils.Query;
import com.byd.wms.business.modules.config.dao.WmsCMatPackageDao;
import com.byd.wms.business.modules.config.service.WmsCMatPackageService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

@Service("wmsCMatPackageService")
public class WmsCMatPackageServiceImpl implements WmsCMatPackageService {
	@Autowired
	private WmsCMatPackageDao wmsCMatPackageDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        
    	String pageNo=(params.get("pageNo")!=null && !params.get("pageNo").equals(""))?params.get("pageNo").toString():"1";
		String pageSize=(params.get("pageSize")!=null && !params.get("pageSize").equals(""))?params.get("pageSize").toString():"15";
		int start = 0;int end = 0;
		int count=wmsCMatPackageDao.getListCount(params);
		if(params.get("pageSize")!=null && !params.get("pageSize").equals("")) {
			start = (Integer.valueOf(pageNo)-1) * Integer.valueOf(pageSize);
			end = Integer.valueOf(pageNo)*Integer.valueOf(pageSize);
		}else {
			end=count;
		}
		params.put("START", start);params.put("END", end);
		List<Map<String,Object>> list=wmsCMatPackageDao.getListByPage(params);
		Page page=new Query<Map<String,Object>>(params).getPage();
		page.setRecords(list);
		page.setTotal(count);
		page.setSize(Integer.valueOf(pageSize));
		page.setCurrent(Integer.valueOf(pageNo));
        return new PageUtils(page);
    }

	@Override
	public List<Map<String, Object>> getItemByHeadId(String head_id) {
		return wmsCMatPackageDao.getItemList(head_id);
	}

	@Override
	public boolean merge(Map<String, Object> params) {
		String headId=params.get("headId")!=null ? params.get("headId").toString() : "";
		Gson gson=new Gson();
		List<Map<String,Object>> list =gson.fromJson((String) params.get("items"),new TypeToken<List<Map<String,Object>>>() {}.getType());
		List<Map<String,Object>> addList=new ArrayList<Map<String,Object>>();
		List<Map<String,Object>> updateList=new ArrayList<Map<String,Object>>();
		
		for(Map<String,Object> map : list) {
			String itemId=map.get("ID")!=null ? map.get("ID").toString() : "";
		    if(itemId.equals("")) {
		    	addList.add(map);
		    }else {
		    	updateList.add(map);
		    }
		}
		params.put("addList", addList);
		params.put("updateList", updateList);
		// 新增
		if(headId.equals("0")) {
			int headCount=wmsCMatPackageDao.insertHead(params);
			Integer MAT_PACKAGE_HEAD_ID=Integer.parseInt(params.get("id").toString());
			params.put("headId", MAT_PACKAGE_HEAD_ID);
			if(addList.size()>0) {
			    int itemCount=wmsCMatPackageDao.insertItems(params);
			}
		}else {
			int headCount=wmsCMatPackageDao.updateHead(params);
			if(addList.size()>0) {
				wmsCMatPackageDao.insertItems(params);
			}
			if(updateList.size()>0) {
			    int itemCount=wmsCMatPackageDao.updateItems(params);
			}
		}
		return false;
	}

	@Override
	public boolean delete(Map<String, Object> params) {
        String headId=params.get("headId")!=null ? params.get("headId").toString() : "";
        String id=params.get("id")!=null ? params.get("id").toString() : "";
        if(!headId.equals("")) {
        	wmsCMatPackageDao.deleteHead(headId);
        	wmsCMatPackageDao.deleteItems(headId);
        }
        if(!id.equals("")) {
        	wmsCMatPackageDao.deleteItem(id);
        }
        return true;
	}

	@Override
	public Map<String, Object> getHeadById(String id) {
		return wmsCMatPackageDao.getHeadById(id);
	}
}

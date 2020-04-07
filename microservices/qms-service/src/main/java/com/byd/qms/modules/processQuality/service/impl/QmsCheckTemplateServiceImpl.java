package com.byd.qms.modules.processQuality.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.plugins.Page;
import com.byd.qms.modules.processQuality.dao.QmsCheckTemplateDao;
import com.byd.qms.modules.processQuality.service.QmsCheckTemplateService;
import com.byd.utils.DateUtils;
import com.byd.utils.PageUtils;
import com.byd.utils.Query;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

@Service("qmsCheckTemplateService")
public class QmsCheckTemplateServiceImpl implements QmsCheckTemplateService {
	@Autowired
	private QmsCheckTemplateDao qmsCheckTemplateDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
    	String pageNo=(params.get("pageNo")!=null && !params.get("pageNo").equals(""))?params.get("pageNo").toString():"1";
		String pageSize=(params.get("pageSize")!=null && !params.get("pageSize").equals(""))?params.get("pageSize").toString():"-1";
		int start = 0;
		int limit=0;
		int count = qmsCheckTemplateDao.getListCount(params);
		if(params.get("pageSize")!=null && !params.get("pageSize").equals("")) {
			start = (Integer.valueOf(pageNo)-1) * Integer.valueOf(pageSize);
			limit = Integer.valueOf(pageSize);
		}else {
			limit=count;
		}
		params.put("start", start);params.put("limit", limit);
		List<Map<String,Object>> list=qmsCheckTemplateDao.getListByPage(params);
		Page page=new Query<Map<String,Object>>(params).getPage();
		page.setRecords(list);
		page.setTotal(count);
		page.setSize(Integer.valueOf(pageSize));
		page.setCurrent(Integer.valueOf(pageNo));
		if(page.getRecords().size()==0&&page.getCurrent()!=1) {
        	pageNo="1";
        	if(params.get("pageSize")!=null && !params.get("pageSize").equals("")) {
    			start = (Integer.valueOf(pageNo)-1) * Integer.valueOf(pageSize);
    			limit = Integer.valueOf(pageNo)*Integer.valueOf(pageSize);
    		}else {
    			limit=count;
    		}
    		params.put("start", start);params.put("limit", limit);
        	list=qmsCheckTemplateDao.getListByPage(params);
    		page=new Query<Map<String,Object>>(params).getPage();
    		page.setRecords(list);
    		page.setTotal(count);
    		page.setSize(Integer.valueOf(pageSize));
    		page.setCurrent(Integer.valueOf(pageNo));
		}
        return new PageUtils(page);
    }
	
	@Override
	@Transactional(rollbackFor=Exception.class)
	public String save(Map<String, Object> params){
		// 模板代码格式: yyyyMMddHHmmssSSS
		String tempNo=DateUtils.format(new Date(),DateUtils.DATE_TIME_STAMP);
		
		params.put("tempNo", tempNo);
		
		qmsCheckTemplateDao.saveHead(params);
		
		qmsCheckTemplateDao.saveItem(params);
		
		return tempNo;
	}

	@Override
	public Map<String, Object> getById(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int update(Map<String, Object> params) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	@Transactional(rollbackFor=Exception.class)
	public int delete(String tempNo) {
		
		int delHeadCount=qmsCheckTemplateDao.deleteHead(tempNo);
		
		int delItemCount=qmsCheckTemplateDao.deleteItem(tempNo);
		
		return (delHeadCount>0 && delItemCount>0) ? 1 : 0;
	}
	@Override
	public int deleteItemById(Long id) {
		// TODO Auto-generated method stub
		return qmsCheckTemplateDao.deleteItemById(id);
	}
	@Override
	public List<Map<String, Object>> getItemList(Map<String, Object> params) {
		return qmsCheckTemplateDao.getItemList(params);
	}

	@Override
	@Transactional(rollbackFor=Exception.class)
	public String saveOrUpdate(Map<String, Object> params) {
		List<Map<String,Object>> saveList=new ArrayList<Map<String,Object>>();
		List<Map<String,Object>> updateList=new ArrayList<Map<String,Object>>();
		Integer curMaxItemNo=qmsCheckTemplateDao.getMaxItemNo(params.get("tempNo").toString());
		
		Gson gson=new Gson();
    	List<Map<String, Object>> list =
    			gson.fromJson((String) params.get("saveData"),new TypeToken<List<Map<String, Object>>>() {}.getType());
		for(Map<String, Object> map : list) {
			// 将“是”、“否”转换成“X”、“0”
			map.put("numberFlag", replaceFlag(map.get("numberFlagDesc")));
			map.put("onePassedFlag", replaceFlag(map.get("onePassedFlagDesc")));
			map.put("photoFlag", replaceFlag(map.get("photoFlagDesc")));
			map.put("patrolFlag", replaceFlag(map.get("patrolFlagDesc")));
            if(map.get("id").toString().equals("0")) {
            	map.put("tempItemNo", curMaxItemNo+1);
            	saveList.add(map);
            	curMaxItemNo++;
			}else {
				updateList.add(map);
			}
		}
		params.put("list", saveList);
		params.put("updateList", updateList);
		if(saveList.size()>0) {
			int saveCount=qmsCheckTemplateDao.saveItem(params);
		}
		if(updateList.size()>0) {
			int updateCount=qmsCheckTemplateDao.updateItem(params);
		}
		return null;
	}

    public String replaceFlag(Object str) {
    	String result="0";
    	if(str==null) {
    		return "0";
    	}
    	if(str.toString().trim().equals("")) {
    		return "0";
    	}
    	if(str.toString().trim().equals("是")) {
    		return "X";
    	}
    	if(str.toString().trim().equals("否")) {
    		return "0";
    	}
    	return result;
    }

	@Override
	public int checkTempIsUsed(String tempNo) {
		// TODO Auto-generated method stub
		return qmsCheckTemplateDao.checkTempIsUsed(tempNo);
	}

}

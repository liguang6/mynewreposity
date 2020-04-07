package com.byd.qms.modules.processQuality.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.plugins.Page;
import com.byd.qms.modules.processQuality.dao.QmsPatrolRecordDao;
import com.byd.qms.modules.processQuality.service.QmsPatrolRecordService;
import com.byd.utils.DateUtils;
import com.byd.utils.PageUtils;
import com.byd.utils.Query;

@Service("qmsPatrolRecordService")
public class QmsPatrolRecordServiceImpl implements QmsPatrolRecordService {
	@Autowired
	private QmsPatrolRecordDao qmsPatrolRecordDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
    	String pageNo=(params.get("pageNo")!=null && !params.get("pageNo").equals(""))?params.get("pageNo").toString():"1";
		String pageSize=(params.get("pageSize")!=null && !params.get("pageSize").equals(""))?params.get("pageSize").toString():"-1";
		int start = 0;
		int limit=0;
		int count = qmsPatrolRecordDao.getListCount(params);
		if(params.get("pageSize")!=null && !params.get("pageSize").equals("")) {
			start = (Integer.valueOf(pageNo)-1) * Integer.valueOf(pageSize);
			limit = Integer.valueOf(pageSize);
		}else {
			limit=count;
		}
		params.put("start", start);params.put("limit", limit);
		List<Map<String,Object>> list=qmsPatrolRecordDao.getListByPage(params);
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
        	list=qmsPatrolRecordDao.getListByPage(params);
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
		// 巡检编号格式: yyyyMMddHHmmssSSS
		String patrolRecordNo=DateUtils.format(new Date(),DateUtils.DATE_TIME_STAMP);
		
		params.put("patrolRecordNo", patrolRecordNo);
		
		qmsPatrolRecordDao.batchSave(params);
		
		return patrolRecordNo;
	}


	@Override
	public int update(Map<String, Object> params) {
		return qmsPatrolRecordDao.batchUpdate(params);
	}

	@Override
	@Transactional(rollbackFor=Exception.class)
	public int delete(String patorlRecordNo) {
		
		return qmsPatrolRecordDao.delete(patorlRecordNo);
	}
	

	@Override
	public Map<String, Object> getById(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Map<String, Object>> getTemplateList(Map<String, Object> params) {
		String tempNo=qmsPatrolRecordDao.getMaxTempNo(params);
		if(tempNo==null) {
			throw new RuntimeException("模板不存在请联系品质中心维护！");
		}
		List<Map<String,Object>> list=qmsPatrolRecordDao.getTemplateList(tempNo);
		if(list.size()==0) {
			throw new RuntimeException("模板不存在请联系品质中心维护！");
		}
		return list;
	}

	@Override
	public List<Map<String, Object>> getList(Map<String, Object> params) {
		return qmsPatrolRecordDao.getList(params);
	}

}

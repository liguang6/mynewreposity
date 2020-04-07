package com.byd.wms.business.modules.config.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.byd.utils.PageUtils;
import com.byd.utils.Query;
import com.byd.wms.business.modules.config.dao.WmsCQcResultDao;
import com.byd.wms.business.modules.config.entity.WmsCQcResultEntity;
import com.byd.wms.business.modules.config.service.WmsCQcResultService;

@Service("wmsCQcResultService")
public class WmsCQcResultServiceImpl extends
		ServiceImpl<WmsCQcResultDao, WmsCQcResultEntity> implements
		WmsCQcResultService {
	@Autowired
	private WmsCQcResultDao wmsCQcResultDao;
	
	
	
	/**
	 * 1.有对应工厂的质检配置，返回对应的质检配置 2.没有，返回通用的质检配置（werks字段为空白的）
	 */
	@Override
	public PageUtils queryPage(Map<String, Object> params) {
//		String werks = String.valueOf(params.get("werks"));
//		String qcResultCode = String
//				.valueOf(params.get("qcResultCode") == null ? "" : params
//						.get("qcResultCode"));
//
//		Page<WmsCQcResultEntity> page = this.selectPage(
//				new Query<WmsCQcResultEntity>(params).getPage(),
//				new EntityWrapper<WmsCQcResultEntity>().eq(
//						StringUtils.isNotBlank(qcResultCode), "qc_result_code",
//						qcResultCode).like(StringUtils.isNotBlank(werks),
//						"werks", werks));
//		if (page.getTotal() == 0) {
//			page = this.selectPage(
//					new Query<WmsCQcResultEntity>(params).getPage(),
//					new EntityWrapper<WmsCQcResultEntity>()
//							.eq(StringUtils.isNotBlank(qcResultCode),
//									"qc_result_code", qcResultCode)
//							.isNull("WERKS").or().eq("WERKS", ""));
//		}
//
//		return new PageUtils(page);
		String pageNo=(params.get("pageNo")!=null && !params.get("pageNo").equals(""))?params.get("pageNo").toString():"1";
		String pageSize=(params.get("pageSize")!=null && !params.get("pageSize").equals(""))?params.get("pageSize").toString():"15";
		int start = 0;int end = 6000;
		if(params.get("pageSize")!=null && !params.get("pageSize").equals("")) {
			start = (Integer.valueOf(pageNo)-1) * Integer.valueOf(pageSize);
			end = Integer.valueOf(pageNo)*Integer.valueOf(pageSize);
		}
		params.put("start", start);params.put("end", end);
		List<Map<String,Object>> list=wmsCQcResultDao.getListByPage(params);
		Page page=new Query<Map<String,Object>>(params).getPage();
		page.setRecords(list);
		page.setTotal(wmsCQcResultDao.getListCount(params));
		page.setSize(Integer.valueOf(pageSize));
		page.setCurrent(Integer.valueOf(pageNo));
        return new PageUtils(page);
	}

	@Override
	public WmsCQcResultEntity queryQcResult(String werks, String qcResultCode) {
		Map<String,Object> queryMap = new HashMap<String,Object>();
		queryMap.put("WERKS", werks);
		queryMap.put("QC_RESULT_CODE", qcResultCode);
		List<WmsCQcResultEntity> results =  this.selectByMap(queryMap);
		if(results.size()>0){
			return results.get(0);
		}
		//返回通用配置
		return this.selectOne(new EntityWrapper<WmsCQcResultEntity>().eq("QC_RESULT_CODE", qcResultCode).isNull("WERKS"));
	}

	@Override
	@Transactional
	public void saveCopyData(List<Map<String, Object>> list) {
//		String werks=list.get(0).get("WERKS").toString();
//		List<Map<String,Object>> addList=new ArrayList<Map<String,Object>>();
//		List<Map<String,Object>> updateList=new ArrayList<Map<String,Object>>();
//		List<WmsCQcResultEntity> resultList=wmsCQcResultDao.selectList(new EntityWrapper<WmsCQcResultEntity>()
//							.eq("WERKS", werks).eq("DEL","0"));
//		boolean isExsist=false;
//        for(Map<String,Object> object : list) {
//        	String newQcResultCode=object.get("QC_RESULT_CODE").toString();
//        	for(WmsCQcResultEntity entity : resultList) {
//        		String oldQcResultEntity=entity.getQcResultCode();
//        		if(newQcResultCode.equals(oldQcResultEntity)) {
//        			// 系统已存在，执行更新
//        			object.put("ID", entity.getId());
//        			updateList.add(object);
//        			isExsist=true;
//        			break;
//        		}
//        	}
//        	// 不存在，就插入
//        	if(!isExsist) {
//        		addList.add(object);
//        	}
//        }
//        if(addList.size()>0) {
//        	wmsCQcResultDao.saveCopyData(addList);
//        }
//        if(updateList.size()>0) {
//        	wmsCQcResultDao.updateCopyData(updateList);
//        }
		wmsCQcResultDao.merge(list);
	}
}

package com.byd.zzjmes.modules.common.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.byd.utils.DateUtils;
import com.byd.utils.UserUtils;
import com.byd.zzjmes.modules.common.dao.ZzjCommonDao;
import com.byd.zzjmes.modules.common.service.ZzjCommonService;


@Service("zzjCommonService")
public class ZzjCommonServiceImpl implements ZzjCommonService {
	private Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private ZzjCommonDao zzjCommonDao;
    @Autowired
    private UserUtils userUtils;

	@Override
	public List<Map<String, Object>> getPlanBatchList(Map<String, Object> params) {
		return zzjCommonDao.getPlanBatchList(params);
	}

	@Override
	public List<Map<String, Object>> getJTProcess(Map<String, Object> params) {		
		return zzjCommonDao.getJTProcess(params);
	}

	@Override
	public List<Map<String, Object>> getMachineList(Map<String, Object> params) {
		return zzjCommonDao.getMachineList(params);
	}

	@Override
	public List<Map<String, Object>> getAssemblyPositionList(Map<String, Object> params) {
		return zzjCommonDao.getAssemblyPositionList(params);
	}

	@Override
	public int productionExceptionManage(Map<String, Object> params) {
		logger.info("-->productionExceptionManage plan_item_id = " + params.get("plan_item_id").toString() + "|exc_str = " + params.get("exc_str").toString());
		// 下料明细问题,图纸标记错误,aaa;图纸问题,缺图,bbb;
		Map<String,Object> currentUser = userUtils.getUser();
		String plan_item_id = params.get("plan_item_id").toString();
		String pmd_item_id = params.get("pmd_item_id").toString();
		String exc_str = params.get("exc_str").toString();
		String del_ids = params.get("del_ids").toString();
		String[] exc = exc_str.split(";");
		if(!exc_str.equals("")) {
			for(int i=0; i<exc.length;i++) {
				logger.info(exc[i]);
				String[] e = exc[i].split(",");
				String exception_type_code = e[0];
				String exception_type_name = e[0];
				String reason_type_code = e[1];
				String reason_type_name = e[1];
				String detailed_exception = e[2];
				String exc_id = e[3];
				String editor = currentUser.get("STAFF_NUMBER").toString() +":"+ currentUser.get("FULL_NAME").toString();
				String edit_date = DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN);
				Map<String, Object> excMap = new HashMap <String, Object>();
				excMap.put("plan_item_id", plan_item_id);
				excMap.put("pmd_item_id", pmd_item_id);
				excMap.put("exception_type_code", exception_type_code);
				excMap.put("exception_type_name", exception_type_name);
				excMap.put("reason_type_code", reason_type_code);
				excMap.put("reason_type_name", reason_type_name);
				excMap.put("detailed_exception", detailed_exception);
				excMap.put("exc_id", exc_id);
				excMap.put("editor", editor);
				excMap.put("edit_date", edit_date);
				if(exc_id.equals("0")) {
					zzjCommonDao.insertProductionException(excMap);
				}else {
					zzjCommonDao.updateProductionException(excMap);
				}
			}
		}
		if(!del_ids.equals("")) {
			String[] del_id = del_ids.split(",");
			for(int i=0; i<del_id.length;i++) {
				Map<String, Object> excMap = new HashMap <String, Object>();
				excMap.put("exc_id", del_id[i]);
				zzjCommonDao.deleteProductionException(excMap);
			}
		}
		return 0;
	}
	@Override
	public List<Map<String, Object>> getProductionExceptionList(Map<String, Object> params){
		return zzjCommonDao.getProductionExceptionList(params);
	}
}

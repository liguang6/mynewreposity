package com.byd.zzjmes.modules.produce.controller;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.byd.utils.DateUtils;
import com.byd.utils.R;
import com.byd.utils.UserUtils;
import com.byd.zzjmes.common.config.OracleJdbcTest;
import com.byd.zzjmes.modules.produce.service.PmdManagerService;

/**
 * 下料明细编辑
 * @author Yangke
 * @date 2019-09-05
 */
@RestController
@RequestMapping("pmdManager")
public class PmdManagerController {
	private Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private PmdManagerService pmdManagerService;
    @Autowired
    private UserUtils userUtils;
	
	@RequestMapping("/getPmdList")
    public R getPmdList(@RequestParam Map<String, Object> params){
		logger.info("-->PmdManagerController :: getPmdList" , params);
		String nopic = "";
		if(params.get("NO_PIC") != null) {
			nopic = params.get("NO_PIC").toString();
		}
		String ZZJ_NO = params.get("ZZJ_NO").toString();
		if(!"".equals(ZZJ_NO)) {
			if(ZZJ_NO.indexOf(",") > 0) {
				ZZJ_NO = "('" + ZZJ_NO.replaceAll(",", "','") + "')";
			}
			params.put("ZZJ_NO", ZZJ_NO);
		}
		List<Map<String, Object>> mapNoList = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> MasterDatamapNoList = new ArrayList<Map<String, Object>>();
		if("1".equals(nopic)) {
			mapNoList = pmdManagerService.getPmdMapNoList(params);

			int pointsDataLimit = 1000;//限制条数
			Integer size = mapNoList.size();
			OracleJdbcTest test = new OracleJdbcTest();
			if(pointsDataLimit<size){
				int part = size/pointsDataLimit;//分批数
				for (int i = 0; i < part; i++) {
					//1000条
					List<Map<String,Object>> listPage = mapNoList.subList(0, pointsDataLimit);
					String str = "";
					for(int j=0;j<listPage.size();j++){
						str += "'" + listPage.get(j).get("material_no").toString() + "'" + ((j==listPage.size()-1)?"":",");
					}
					try {
						//System.out.println("SELECT DISTINCT MAP_NO FROM WMSTEST.MASTERDATA_PDM_MAP m WHERE MAP_NO IN ("+str+")");
						if(!"".equals(str))MasterDatamapNoList.addAll(test.queryMasterDataMapNo("SELECT DISTINCT MAP_NO FROM WMSTEST.MASTERDATA_PDM_MAP m WHERE MAP_NO IN ("+str+")", true));
					} catch (SQLException e) {
						e.printStackTrace();
					}
					mapNoList.subList(0, pointsDataLimit).clear();
				}
				if(!mapNoList.isEmpty()){//表示最后剩下的数据
					String str = "";
					for(int j=0;j<mapNoList.size();j++){
						str += "'" + mapNoList.get(j).get("material_no").toString() + "'" + ((j==mapNoList.size()-1)?"":",");
					}
					try {
						if(!"".equals(str))MasterDatamapNoList.addAll(test.queryMasterDataMapNo("SELECT DISTINCT MAP_NO FROM WMSTEST.MASTERDATA_PDM_MAP m WHERE MAP_NO IN ("+str+")", true));
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}else {
				String str = "";
				for(int j=0;j<mapNoList.size();j++){
					str += "'" + mapNoList.get(j).get("material_no").toString() + "'" + ((j==mapNoList.size()-1)?"":",");
				}
				try {
					if(!"".equals(str))MasterDatamapNoList.addAll(test.queryMasterDataMapNo("SELECT DISTINCT MAP_NO FROM WMSTEST.MASTERDATA_PDM_MAP m WHERE MAP_NO IN ("+str+")", true));
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			List<String> MasterDatamapNoListParm = new ArrayList<String>();
			for(int i=0;i<MasterDatamapNoList.size();i++){
				logger.info("-->MAP_NO : " + MasterDatamapNoList.get(i).get("MAP_NO").toString());
				MasterDatamapNoListParm.add(MasterDatamapNoList.get(i).get("MAP_NO").toString());
			}
			params.put("MAP_NO", MasterDatamapNoListParm);
		}
		return R.ok().put("result", pmdManagerService.getPmdList(params));
	}
	
	@RequestMapping("/getPmdListPage")
    public R getPmdListPage(@RequestParam Map<String, Object> params){
		String nopic = "";
		if(params.get("NO_PIC") != null) {
			nopic = params.get("NO_PIC").toString();
		}
		logger.info("-->PmdManagerController :: getPmdListPage nopic = " + nopic);
		//TODO 优化为通过admin服务获取数据 不使用JDBC方式
		String ZZJ_NO = params.get("ZZJ_NO").toString();
		if(!"".equals(ZZJ_NO)) {
			if(ZZJ_NO.indexOf(",") > 0) {
				ZZJ_NO = "('" + ZZJ_NO.replaceAll(",", "','") + "')";
			}
			params.put("ZZJ_NO", ZZJ_NO);
		}
		List<Map<String, Object>> mapNoList = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> MasterDatamapNoList = new ArrayList<Map<String, Object>>();
		if("1".equals(nopic)) {
			mapNoList = pmdManagerService.getPmdMapNoList(params);

			int pointsDataLimit = 1000;//限制条数
			Integer size = mapNoList.size();
			OracleJdbcTest test = new OracleJdbcTest();
			if(pointsDataLimit<size){
				int part = size/pointsDataLimit;//分批数
				for (int i = 0; i < part; i++) {
					//1000条
					List<Map<String,Object>> listPage = mapNoList.subList(0, pointsDataLimit);
					String str = "";
					for(int j=0;j<listPage.size();j++){
						str += "'" + listPage.get(j).get("material_no").toString() + "'" + ((j==listPage.size()-1)?"":",");
					}
					try {
						//System.out.println("SELECT DISTINCT MAP_NO FROM WMSTEST.MASTERDATA_PDM_MAP m WHERE MAP_NO IN ("+str+")");
						if(!"".equals(str))MasterDatamapNoList.addAll(test.queryMasterDataMapNo("SELECT DISTINCT MAP_NO FROM WMSTEST.MASTERDATA_PDM_MAP m WHERE MAP_NO IN ("+str+")", true));
					} catch (SQLException e) {
						e.printStackTrace();
					}
					mapNoList.subList(0, pointsDataLimit).clear();
				}
				if(!mapNoList.isEmpty()){//表示最后剩下的数据
					String str = "";
					for(int j=0;j<mapNoList.size();j++){
						str += "'" + mapNoList.get(j).get("material_no").toString() + "'" + ((j==mapNoList.size()-1)?"":",");
					}
					try {
						if(!"".equals(str))MasterDatamapNoList.addAll(test.queryMasterDataMapNo("SELECT DISTINCT MAP_NO FROM WMSTEST.MASTERDATA_PDM_MAP m WHERE MAP_NO IN ("+str+")", true));
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}else {
				String str = "";
				for(int j=0;j<mapNoList.size();j++){
					str += "'" + mapNoList.get(j).get("material_no").toString() + "'" + ((j==mapNoList.size()-1)?"":",");
				}
				try {
					if(!"".equals(str))MasterDatamapNoList.addAll(test.queryMasterDataMapNo("SELECT DISTINCT MAP_NO FROM WMSTEST.MASTERDATA_PDM_MAP m WHERE MAP_NO IN ("+str+")", true));
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			List<String> MasterDatamapNoListParm = new ArrayList<String>();
			for(int i=0;i<MasterDatamapNoList.size();i++){
				logger.info("-->MAP_NO : " + MasterDatamapNoList.get(i).get("MAP_NO").toString());
				MasterDatamapNoListParm.add(MasterDatamapNoList.get(i).get("MAP_NO").toString());
			}
			params.put("MAP_NO", MasterDatamapNoListParm);
		}
		
		return R.ok().put("page", pmdManagerService.getPmdListPage(params));
	}
	
	@RequestMapping("/editPmdList")
	public R editPmdList(@RequestParam Map<String, Object> params){
		return R.ok().put("result", pmdManagerService.editPmdList(params));
	}

	@RequestMapping("/deletePmdList")
	public R deletePmdList(@RequestParam Map<String, Object> params){
		return R.ok().put("result", pmdManagerService.deletePmdList(params));
	}
	
	@RequestMapping("/getProductionExceptionPage")
	public R getProductionExceptionPage(@RequestParam Map<String, Object> params){
		return R.ok().put("page", pmdManagerService.getProductionExceptionPage(params));
	}
	@RequestMapping("/exceptionConfirm")
	public R exceptionConfirm(@RequestParam Map<String, Object> params){
    Map<String,Object> currentUser = userUtils.getUser();
		params.put("processor", currentUser.get("STAFF_NUMBER").toString() +":"+ currentUser.get("FULL_NAME").toString());
		params.put("process_date", DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
		return R.ok().put("result", pmdManagerService.exceptionConfirm(params));
	}
	@RequestMapping("/getProductionExceptionList")
	public R getProductionExceptionList(@RequestParam Map<String, Object> params){
		return R.ok().put("result", pmdManagerService.getProductionExceptionList(params));
	}

	@RequestMapping("/getPmdProcessPlanCount")
	public R getPmdProcessPlanCount(@RequestParam Map<String, Object> params){
		return R.ok().put("result", pmdManagerService.getPmdProcessPlanCount(params));
	}
	
	@RequestMapping("/getSubcontractingHeadPage")
	public R getSubcontractingHeadPage(@RequestParam Map<String, Object> params){
		String ZZJ_NO = params.get("ZZJ_NO").toString();
		if(!"".equals(ZZJ_NO)) {
			if(ZZJ_NO.indexOf(",") > 0) {
				ZZJ_NO = "('" + ZZJ_NO.replaceAll(",", "','") + "')";
			}
			params.put("ZZJ_NO", ZZJ_NO);
		}
		return R.ok().put("page", pmdManagerService.getSubcontractingHeadPage(params));
	}

	@RequestMapping("/getSubcontractingItemList")
	public R getSubcontractingItemList(@RequestParam Map<String, Object> params){
		return R.ok().put("result", pmdManagerService.getSubcontractingItemList(params));
	}

	@RequestMapping("/editSubcontractingItem")
	public R editSubcontractingItem(@RequestParam Map<String, Object> params){
		Map<String,Object> currentUser = userUtils.getUser();
		params.put("editor", currentUser.get("STAFF_NUMBER").toString() +":"+ currentUser.get("FULL_NAME").toString());
		params.put("edit_date", DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
		return R.ok().put("result", pmdManagerService.editSubcontractingItem(params));
	}

	@RequestMapping("/getSubcontractingPage")
	public R getSubcontractingPage(@RequestParam Map<String, Object> params){
		String ZZJ_NO = params.get("ZZJ_NO").toString();
		if(!"".equals(ZZJ_NO)) {
			if(ZZJ_NO.indexOf(",") > 0) {
				ZZJ_NO = "('" + ZZJ_NO.replaceAll(",", "','") + "')";
			}
			params.put("ZZJ_NO", ZZJ_NO);
		}
		return R.ok().put("page", pmdManagerService.getSubcontractingPage(params));
	}
	
	@RequestMapping("/getSubcontractingList")
    public R getSubcontractingList(@RequestParam Map<String, Object> params){
		String ZZJ_NO = params.get("ZZJ_NO").toString();
		if(!"".equals(ZZJ_NO)) {
			if(ZZJ_NO.indexOf(",") > 0) {
				ZZJ_NO = "('" + ZZJ_NO.replaceAll(",", "','") + "')";
			}
			params.put("ZZJ_NO", ZZJ_NO);
		}
		return R.ok().put("result", pmdManagerService.getSubcontractingList(params));
	}
	
	@RequestMapping("/editSubcontractingList")
	public R editSubcontractingList(@RequestParam Map<String, Object> params){
		return R.ok().put("result", pmdManagerService.editSubcontractingList(params));
	}
}

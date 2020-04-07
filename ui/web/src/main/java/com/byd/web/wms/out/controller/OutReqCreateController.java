package com.byd.web.wms.out.controller;

import java.io.IOException;
import java.util.*;

import com.byd.utils.DateUtils;
import com.byd.utils.ExcelReader;
import com.byd.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.byd.utils.R;
import com.byd.web.common.utils.TagUtils;
import com.byd.web.wms.out.service.WmsOutReqServiceRemote;
import org.springframework.web.multipart.MultipartFile;

/**
 * 出库模块控制器
 * @author develop07
 *
 */
@RestController
@RequestMapping("/out/outreq")
public class OutReqCreateController {
	

	@Autowired
	TagUtils tagUtils;

	@Autowired
	UserUtils userUtils;
	@Autowired
	WmsOutReqServiceRemote outReqServiceRemote;
	

	@RequestMapping("/queryOutItems6")
	public R queryOutItems6(@RequestBody List<Map<String, Object>> list) {
		// 查询委外订单出库项目
		return outReqServiceRemote.queryOutItems6(list);
	}

	@RequestMapping("/queryOutItems7")
	public R queryOutItems7(@RequestBody List<Map<String, Object>> list) {
		// 查询SAP交货单出库行项目
		return outReqServiceRemote.queryOutItems7(list);
	}

	@RequestMapping("/outReqCreate6")
	public R outReqCreate6(@RequestBody List<Map<String, Object>> list) {
		// 委外订单出库需求创建
		return outReqServiceRemote.outReqCreate6(list);
	}

	@RequestMapping("/outReqCreate7")
	public R outReqCreate7(@RequestBody List<Map<String, Object>> list) {
		// 创建SAP交货单出库需求
		return outReqServiceRemote.outReqCreate7(list);
	}
	
	

	@RequestMapping("/validateOutItems6")
	public R validateOutItems6(@RequestBody List<Map<String, Object>> list) {
		// 校验输入的委外订单是否合格
		return outReqServiceRemote.validateOutItems6(list);
	}

	// SAP销售订单发货
	@RequestMapping("/validateOutItems7")
	public R validateOutItems7(@RequestBody List<Map<String, Object>> list) {	
		Map<String,Object> data = new HashMap<String, Object>();
		data.put("list", list);
		putDepts(data);
		return outReqServiceRemote.validateOutItems7(data);
	}
	
	/**
	 * map中传入用户权限工厂代码
	 * @param data
	 */
	private void putDepts(Map<String,Object> data){
	/*	List<SysDeptEntity> userDepts = tagUtils.deptListWithUser("3",null);
		List<String> deptCodes = userDepts.stream().map(SysDeptEntity::getCode).collect(Collectors.toList());
		data.put("depts", deptCodes);*/
	}
	
	@RequestMapping("/validateOutItem10")
	public R validate10(@RequestBody List<Map<String, Object>> list){
		Map<String,Object> data = new HashMap<>();
		data.put("list", list);
		putDepts(data);
		return outReqServiceRemote.validate10(data);
	}
	
	
	@RequestMapping("/queryOutItem10")
	public R queryOutItem10(@RequestBody List<Map<String, Object>> list){
		return outReqServiceRemote.queryOutItem10(list);
	}
	
	@RequestMapping("/outReqCreate10")
	public R createOutReq10(@RequestBody List<Map<String, Object>> list){
		String staffNumber = "";
		//工号
		list.forEach(m ->{m.put("staffNumber", staffNumber);});
		return outReqServiceRemote.createOutReq10(list);
	}

	@RequestMapping("/validateOutItem8")
	public R validateOutItem8(@RequestBody List<Map<String, Object>> list) {
		return outReqServiceRemote.validateOutItem8(list);
	}
	
	@RequestMapping("/queryOutItem8")
	public R queryOutItem8(@RequestBody List<Map<String, Object>> list){
		return outReqServiceRemote.queryOutItem8(list);
	}
	
	@RequestMapping("/outReqCreate8")
	public R outReqCreate8(@RequestBody List<Map<String, Object>> list){
		//TODO:创建UB转储出库需求
		System.err.println(list.toString());
		return outReqServiceRemote.outReqCreate8(list);
	}
	
	
	@RequestMapping("/queryTotalStockQty")
	public R queryTotalStockQty(@RequestBody Map<String,Object> params){
		return outReqServiceRemote.queryTotalStockQty(params);
	}
	
	@RequestMapping("/createOutReq11")
	public R createOutReq11(@RequestBody List<Map<String, Object>> list){
		return outReqServiceRemote.createOutReq11(list);
	}
	
	@RequestMapping("/mathx")
	public R validateMatrialsHX(@RequestBody List<Map<String, Object>> list){
		System.err.println(list.toString());
		return outReqServiceRemote.validateMatrialsHX(list);
	}
	
	/**
	 * 库存地点调拨311—类型不转移
	 * @param list
	 * @return
	 */
	@RequestMapping("/createOutReq13")
	public R createOutReq13(@RequestBody List<Map<String, Object>> list){
		String s = "";
		list.forEach(m->m.put("staffNumber", s));
		return outReqServiceRemote.createOutReq13(list);
	}
	
	
	//校验 工厂间调拨发货（A303）
	@RequestMapping("/validate16")
	public R validate16(@RequestBody List<Map<String, Object>> list){
		return outReqServiceRemote.validate16(list);
	}
	
	//查询 工厂间调拨发货（A303）
	@RequestMapping("/query16")
	public R query16(@RequestBody List<Map<String, Object>> list){
		return outReqServiceRemote.query16(list);
	}
	//创建 工厂间调拨发货（A303）
	@RequestMapping("/create16")
	public R create16(@RequestBody List<Map<String, Object>> list){
		System.err.println("create16create16create16");
		String s = "";
		list.forEach(m->m.put("staffNumber", s));
		return outReqServiceRemote.create16(list);
	}
	
	//校验 - SAP交货单销售发货（A311T）
	@RequestMapping("/validate17")
	public R validate17(@RequestBody List<Map<String, Object>> list){
		return outReqServiceRemote.validate17(list);
	}

	//查询- SAP交货单销售发货（A311T）
	@RequestMapping("/query17")
	public R query17(@RequestBody List<Map<String, Object>> list){
		return outReqServiceRemote.query17(list);
	}
	

	//创建 - SAP交货单销售发货（A311T）
	@RequestMapping("/create17")
	public R create17(@RequestBody List<Map<String, Object>> list){
		String s = "";
		list.forEach(m->m.put("staffNumber", s));
		return outReqServiceRemote.create17(list);
	}

	/**
	 * 报废（551）
	 * @param list
	 * @return
	 */
	@RequestMapping("/createOutReq18")
	public R createOutReq18(@RequestBody List<Map<String, Object>> list){
		String s = "";
		list.forEach(m->m.put("staffNumber", s));
		return outReqServiceRemote.createOutReq18(list);
	}
	
	@RequestMapping("/queryOutReqPda311")
	public R queryOutReqPda311(@RequestBody Map<String, Object> map){
		return outReqServiceRemote.queryOutReqPda311(map);
	}
	
	@RequestMapping("/queryUNIT")
	public R queryUNIT(@RequestBody Map<String, Object> map){
		return outReqServiceRemote.queryUNIT(map);
	}
	
	@RequestMapping("/createOutReqPda311")
	public R createOutReqPda311(@RequestBody List<Map<String, Object>> list){
		return outReqServiceRemote.createOutReqPda311(list);
	}


	/**
	 * STO一步联动311
	 * @param list
	 * @return
	 */
	@RequestMapping("/createOutReq20")
	public R createOutReq20(@RequestBody List<Map<String, Object>> list){
		String s = "";
		list.forEach(m->m.put("staffNumber", s));
		return outReqServiceRemote.createOutReq20(list);
	}
	
	//校验 工厂间调拨301（总装）
	@RequestMapping("/validate21")
	public R validate21(@RequestBody List<Map<String, Object>> list){
		return outReqServiceRemote.validate21(list);
	}
	
	//查询 工厂间调拨301（总装）
	@RequestMapping("/query21")
	public R query21(@RequestBody List<Map<String, Object>> list){
		return outReqServiceRemote.query21(list);
	}
	
	/**
	 * 工厂间调拨301（总装）
	 * @param list
	 * @return
	 */
	@RequestMapping("/createOutReq21")
	public R createOutReq21(@RequestBody List<Map<String, Object>> list){
		return outReqServiceRemote.createOutReq21(list);
	}
	
	@RequestMapping("/validateCostomer")
	public R validateCostomer(@RequestParam Map<String, Object> params){
		return outReqServiceRemote.validateCostomer(params);
	}


	@RequestMapping("/preview")
	public R previewExcel(MultipartFile excel, String WEARKS, String requireTypes ) throws IOException {
		//System.err.println("=============previewpreviewpreview=============");
		//System.err.println("WERKSWERKSWERKS "+WEARKS);
		Map<String,Object> user = userUtils.getUser();
		List<String[]> sheet =  ExcelReader.readExcel(excel);
		List<Map<String,Object>> entityList = new ArrayList<Map<String,Object>>();
		List queryList = new ArrayList();
		for(int j = 0;j<sheet.size();j++){
			Map map  = new HashMap();
			map.put("MATNR",sheet.get(j)[2]);
			map.put("WEARKS",WEARKS);
			queryList.add(map);
		}

		int index=0;
		if(sheet != null && sheet.size() > 0){
			for(String[] row:sheet){
				Map<String,Object> map=new HashMap<String,Object>();
				map.put("rowNo", ++index);
				map.put("MATNR", row[0]);
				//查询物料描述，单位
				Map<String, Object>  resultLMap = (Map<String, Object>) outReqServiceRemote.getMat(WEARKS, row[0]).get("data");
				map.put("MAKTX", resultLMap.get("MAKTX"));
				map.put("MEINS", resultLMap.get("MEINH"));
				map.put("REQ_QTY", row[1]);
				map.put("LGORT", row[2] );
				map.put("VENDOR", row[3]);
				map.put("LINE", row[4]);
				map.put("BOX_COUNT", row[5]);
				map.put("editor", user.get("USERNAME")+"："+user.get("FULL_NAME"));
				map.put("editDate", DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
				map.put("requireTypes", requireTypes);
				entityList.add(map);
			}
		}

		return outReqServiceRemote.previewBy311(entityList);
	}



}

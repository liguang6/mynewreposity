package com.byd.web.wms.out.controller;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import com.byd.utils.DateUtils;
import com.byd.utils.ExcelReader;
import com.byd.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.byd.utils.R;
import com.byd.web.wms.out.entity.CreateProduceOrderAO;
import com.byd.web.wms.out.entity.ProduceOrderVO;
import com.byd.web.wms.out.service.WmsOutReqServiceRemote;
import org.springframework.web.multipart.MultipartFile;

/**
 * 线边仓领料
 * @author develop07
 */
@RestController
@RequestMapping("/out/producelinewarehouse")
public class ProduceLineWarehouseController {

	@Autowired
	WmsOutReqServiceRemote outReqServiceRemote;
	@Autowired
	UserUtils userUtils;
	//校验订单
	@RequestMapping("/processProduceOrder")
	public R processProduceOrder(@RequestBody List<ProduceOrderVO> produceOrders){
		Map<String,Object> data = new HashMap<String, Object>();
		data.put("params", produceOrders);
/*		Map<String,Object> params= new HashMap<>();
		params.put("deptType", "3");
		List<String> depts = deptService.queryDeptListWithPremision(params).stream().map(SysDeptEntity::getCode).collect(Collectors.toList());
		data.put("depts", depts);*/
		return outReqServiceRemote.processProduceOrder(data);
	}

	@RequestMapping("/producerOrders")
	public R producerOrders(@RequestBody Map<String,Object> params){
		return outReqServiceRemote.producerOrders(params);
	}

	@RequestMapping("/create")
	public R create(@RequestBody List<CreateProduceOrderAO> cList){
		Map<String,Object> data = new HashMap<>();
		data.put("params", cList);
		data.put("staffNumber", "");
		return outReqServiceRemote.create(data);
	}


	@RequestMapping("/import")
	public R upload(@RequestBody List<CreateProduceOrderAO> entityList) throws IOException {
		//System.err.println("uploaduploaduploaduploadupload");
		//System.err.println(entityList.toString());
		return outReqServiceRemote.uploadBy311(entityList);
	}

	@RequestMapping("/queryMaktxAndUnit")
	public R queryMaktxAndUnit(@RequestParam Map<String,Object> params) throws IOException{
		System.err.println(params.toString());
		return R.ok();
	}
	@RequestMapping("/preview")
	public R previewExcel(MultipartFile excel, String WEARKS, String requireTypes ) throws IOException{
		//System.err.println("=============previewpreviewpreview=============");
		//System.err.println("WERKSWERKSWERKS "+WEARKS);


		/**
	            * 判断文件的大小
		*/
		long size = excel.getSize();
		if (size > 1024 * 1024 * 10) {
		     return R.error( "当前文件大小为"+Math.ceil(size * 100 / 1024/ 1024 / 10 / 100) +
		 		    "M,超过10M");
		}

		Map<String,Object> user = userUtils.getUser();
		List<String[]> sheet =  ExcelReader.readExcel(excel);
		if (sheet.size() > 1000) {
			return R.error( "批导行数不能超过1000");
		}
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
				map.put("AUFNR", row[0]);
				map.put("POSNR", row[1]);
				map.put("MATNR", row[2]);
//				System.err.println("row[2]   "+row[2]);
				//查询物料描述，单位
				Map<String, Object>  resultLMap = (Map<String, Object>) outReqServiceRemote.getMat(WEARKS, row[2]).get("data");
				map.put("MAKTX", resultLMap.get("MAKTX"));
				map.put("MEINS", resultLMap.get("MEINH"));
				map.put("REQ_QTY", row[3]);
				map.put("LGORT", row[4] );
				map.put("VENDOR", row[5]);
				map.put("LINE", row[6]);
				map.put("STATION", row[7]);
				map.put("aceptLgortList", row[8]);//fixbug1512
				map.put("editor", user.get("USERNAME")+"："+user.get("FULL_NAME"));
				map.put("editDate", DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
				map.put("requireTypes", requireTypes);
				entityList.add(map);
			}
		}

		return outReqServiceRemote.previewBy311(entityList);
	}
}

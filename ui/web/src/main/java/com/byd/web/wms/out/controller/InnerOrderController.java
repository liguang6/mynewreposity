package com.byd.web.wms.out.controller;

import java.io.IOException;
import java.util.*;

import com.byd.utils.DateUtils;
import com.byd.utils.ExcelReader;
import com.byd.utils.UserUtils;
import com.byd.web.wms.out.entity.CreateProduceOrderAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.byd.utils.R;
import com.byd.web.wms.common.service.WmsSapRemote;
import com.byd.web.wms.out.entity.InternalOrderAO;
import com.byd.web.wms.out.service.WmsOutReqServiceRemote;
import org.springframework.web.multipart.MultipartFile;


/**
 * 内部订单领料需求控制器
 * @author develop07
 *
 */
@RestController
@RequestMapping("/out/innerorder")
public class InnerOrderController {
	
	static Logger log = LoggerFactory.getLogger(InnerOrderController.class);
	
	@Autowired
	WmsSapRemote wmsSapRemote;
	
	@Autowired
	WmsOutReqServiceRemote outReqServiceRemote;

	@Autowired
	UserUtils userUtils;
	
	/**
	 * 从SAP查询内部订单
	 * @param innerOrderNo
	 * @return
	 */
	@RequestMapping("/SAPInnerOrder/{innerOrderNo}")
	public R getInnerOrderFromSAP(@PathVariable("innerOrderNo") String innerOrderNo){
		log.debug("--- > get internal order from SAP");
		Map<String,Object> result =  wmsSapRemote.getSapBapiKaufOrder(innerOrderNo);
		return R.ok().put("data", result);
	}
	
	/**
	 * 创建内部订单领料需求
	 * @param internalOrderList
	 * @return
	 */
	@RequestMapping("/createInternalOrder")
	public R createIternalOrderRequirement(@RequestBody List<InternalOrderAO> internalOrderList){
		return outReqServiceRemote.createIternalOrderRequirement(internalOrderList);
	}

	@RequestMapping("/import")
	public R upload(@RequestBody List<InternalOrderAO> entityList) throws IOException{
		//System.err.println("uploaduploaduploaduploadupload");
		System.err.println(entityList.toString());
		return outReqServiceRemote.createIternalOrderRequirement(entityList);
	}

	@RequestMapping("/queryMaktxAndUnit")
	public R queryMaktxAndUnit(@RequestParam Map<String,Object> params) throws IOException{
		System.err.println(params.toString());
		return R.ok();
	}

	@RequestMapping("/preview")
	public R previewExcel(MultipartFile excel, String WEARKS, String requireTypes ) throws IOException {
		//System.err.println("=============previewpreviewpreview=============");
		//System.err.println("WERKSWERKSWERKS "+WEARKS);
		Map<String,Object> user = userUtils.getUser();
		List<String[]> sheet =  ExcelReader.readExcel(excel);
		List<Map<String,Object>> entityList = new ArrayList<Map<String,Object>>();
		/*List queryList = new ArrayList();
		for(int j = 0;j<sheet.size();j++){
			Map map  = new HashMap();
			map.put("MATNR",sheet.get(j)[2]);
			map.put("WEARKS",WEARKS);
			queryList.add(map);
		}*/

		int index=0;
		if(sheet != null && sheet.size() > 0){
			for(String[] row:sheet){
				System.err.println("row[0]row[0]row[0] "+row[0]);
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
				map.put("STATION", row[4]);
				map.put("MENO", row[5]);
				map.put("editor", user.get("USERNAME")+"："+user.get("FULL_NAME"));
				map.put("editDate", DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
				map.put("requireTypes", requireTypes);
				entityList.add(map);
			}
		}

		return outReqServiceRemote.previewNbdd(entityList);
	}
	
}

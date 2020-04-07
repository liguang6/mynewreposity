package com.byd.web.wms.out.controller;

import java.io.IOException;
import java.util.*;

import com.byd.utils.DateUtils;
import com.byd.utils.ExcelReader;
import com.byd.utils.UserUtils;
import com.byd.web.wms.config.entity.WmsCoreWhBinEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.byd.utils.R;
import com.byd.web.wms.common.service.WmsSapRemote;
import com.byd.web.wms.out.entity.CostCenterAO;
import com.byd.web.wms.out.entity.CreateProduceOrderAO;
import com.byd.web.wms.out.entity.ProduceOrderVO;
import com.byd.web.wms.out.service.WmsOutReqServiceRemote;

/**
 * 生产订单领料需求创建
 * @author develop07
 *
 */
@RestController
@RequestMapping("/out/createRequirement")
public class ProduceOrderController {

	@Autowired
	WmsOutReqServiceRemote outReqServiceRemote;

	@Autowired
    UserUtils userUtils;

	@RequestMapping("/getPlantBusinessTypes")
	public R getPlantBusinessTypes(@RequestParam Map<String, Object> params) {
		return outReqServiceRemote.getPlantBusinessTypes(params);
	}

	@RequestMapping("/processProduceOrder")
	public R processProduceOrder(@RequestBody List<ProduceOrderVO> produceOrders) {
		return outReqServiceRemote.processProduceOrderO(produceOrders);
	}
	
	/**
	 * 查询生产订单信息 - 生产订单领料
	 * @param params
	 * @return
	 */
	@RequestMapping("/producerOrders")
	public R queryProducerOrderInfo(@RequestBody Map<String,Object> params){
		return outReqServiceRemote.queryProducerOrderInfoO(params);
	}
	
	@Autowired
	WmsSapRemote sapRemote;
	
	@RequestMapping("/sapCostcenter/{Costcenter}")
	public Map<String,Object> getSapCostcenterDetail(@PathVariable("Costcenter")String Costcenter){
		return sapRemote.getSapBapiCostcenterDetail(Costcenter);
	}
	
	@RequestMapping("/mat")
	public R getMat(String werks,String mat){return outReqServiceRemote.getMat(werks, mat);}
	
	@RequestMapping("/costcenterReq")
	public R createCostcenterRequirement(@RequestBody List<CostCenterAO> items){
		System.err.println(items.toString());
		return outReqServiceRemote.createCostcenterRequirement(items);
	}
	
	@RequestMapping("/lgortAndStock")
	public R getLgortStockByMaterial(String werks,String whNumber,String matnr){
		return outReqServiceRemote.getLgortStockByMaterial(werks, whNumber, matnr);
	}
	
	@RequestMapping("/costcenterBatchLoadPreview")
	public R batchLoadPreview(MultipartFile excel) throws IOException{
		return outReqServiceRemote.batchLoadPreview(excel);
	}
	
	@RequestMapping("/create")
	public R createProduceOrderReq(@RequestBody List<CreateProduceOrderAO> cList){
		return outReqServiceRemote.createProduceOrderReq(cList);
	}
	
	
	/**
	 * 查询非上线物料
	 * @param werks
	 * @return
	 */
	@RequestMapping("matuseing/{werks}")
	public R matUsing(@PathVariable("werks")String werks){
		return outReqServiceRemote.matUsing(werks);
	}
	
	
	@RequestMapping("sapPoItemHead/{werks}/{poNo}/")
	public R sapPoItemHead(@PathVariable("werks")String werks,@PathVariable("poNo")String poNo){
		return outReqServiceRemote.sapPoItemHead(werks, poNo);
	}


	@RequestMapping("/import")
	public R upload(@RequestBody List<CreateProduceOrderAO> entityList) throws IOException{
		//System.err.println("uploaduploaduploaduploadupload");
		//System.err.println(entityList.toString());
		return outReqServiceRemote.upload(entityList);
	}

    @RequestMapping("/queryMaktxAndUnit")
    public R queryMaktxAndUnit(@RequestParam Map<String,Object> params) throws IOException{
        System.err.println(params.toString());
        return R.ok();
    }
	@RequestMapping("/preview")
	public R previewExcel(MultipartFile excel,String WEARKS,String requireTypes,String innerOrder ) throws IOException{
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
				map.put("AUFNR", row[0]);
				map.put("POSNR", row[1]);
				map.put("MATNR", row[2]);
				System.err.println("row[2]   "+row[2]);
				//查询物料描述，单位
				Map<String, Object>  resultLMap = (Map<String, Object>) outReqServiceRemote.getMat(WEARKS, row[2]).get("data");
				map.put("MAKTX", resultLMap.get("MAKTX"));
				map.put("MEINS", resultLMap.get("MEINH"));
                map.put("REQ_QTY", row[3]);
                map.put("LGORT", row[4] );
                map.put("VENDOR", row[5]);
                map.put("LINE", row[6]);
                map.put("STATION", row[7]);
                map.put("innerOrder", row[8]);
				map.put("editor", user.get("USERNAME")+"："+user.get("FULL_NAME"));
				map.put("editDate", DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
				map.put("requireTypes", requireTypes);
				entityList.add(map);
			}
		}

		return outReqServiceRemote.previewBy311(entityList);
	}

}

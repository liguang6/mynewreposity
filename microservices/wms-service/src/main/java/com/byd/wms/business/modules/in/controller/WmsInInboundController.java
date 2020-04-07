package com.byd.wms.business.modules.in.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.byd.utils.R;
import com.byd.wms.business.modules.config.entity.WmsCWhEntity;
import com.byd.wms.business.modules.config.entity.WmsSapMaterialEntity;
import com.byd.wms.business.modules.config.entity.WmsSapPlantLgortEntity;
import com.byd.wms.business.modules.config.service.WmsCWhService;
import com.byd.wms.business.modules.config.service.WmsSapMaterialService;
import com.byd.wms.business.modules.config.service.WmsSapPlantLgortService;
import com.byd.wms.business.modules.in.service.WmsInInboundService;


/**
 * 创建进仓单Controller
 *
 * @author pengtao
 * @email 
 * @date 2018-08-27 10:54:57
 * 类说明 收货进仓单
 */
@RestController
@RequestMapping("in/wmsinbound")
public class WmsInInboundController {
    @Autowired
    private WmsInInboundService wmsInInboundService;
    @Autowired
	WmsSapPlantLgortService lgortService;
    @Autowired
    WmsCWhService wmsCWhService;
    @Autowired
    private WmsSapMaterialService wmsSapMaterialService;
    
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
    	List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
    	result=wmsInInboundService.getReceiptList(params);
    	
    	Map<String, Object> tempMap=new HashMap<String, Object>();
    	tempMap.put("WERKS", params.get("WERKS"));
    	tempMap.put("WH_NUMBER", params.get("WH_NUMBER"));
    	List<WmsCWhEntity> retCWh=wmsCWhService.selectByMap(tempMap);
    	String barcode_flag="";
    	if(retCWh!=null&&retCWh.size()>0){
    		barcode_flag=retCWh.get(0).getBarcodeFlag();
    	}
        return R.ok().put("result", result).put("BARCODE_FLAG", barcode_flag);
    }
    
    /**
     * 根据工厂、仓库号、仓管员获取待进仓的来料任务清单
     * @param params
     * @return
     */
    @RequestMapping("/getInboundTasks")
    public R getInboundTasks(@RequestParam Map<String, Object> params){
    	List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
    	result=wmsInInboundService.getInboundTasks(params);
        return R.ok().put("data", result);
    }
    
    /**
     * 获取仓管员
     * @param params
     * @return
     */
    @RequestMapping("/relatedAreaNamelist")
    public R RelatedAreaNamelist(@RequestParam Map<String, Object> params){
    	List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
    	result=wmsInInboundService.getRelatedAreaName(params);
    	
        return R.ok().put("result", result);
    }
    
    @RequestMapping("/getDeptNameByWerk")
    public List<Map<String,Object>> getDeptNameByWerk(@RequestParam Map<String, Object> deptMap){
    	return wmsInInboundService.getDeptNameByWerk(deptMap);
    }
    
    /**
     * 获取库位
     */
    @RequestMapping("/lgortlist")
    public R lgortlist(@RequestParam Map<String, Object> params){
    	
    	Map<String, Object> tempParam=new HashMap<String, Object>();
    	if(params.get("DEL")!=null){
    		tempParam.put("DEL", params.get("DEL"));
    	}
    	if(params.get("WERKS")!=null){
    		tempParam.put("WERKS", params.get("WERKS"));
    	}
    	if(params.get("SOBKZ")!=null){
    		tempParam.put("SOBKZ", params.get("SOBKZ"));
    	}
    	if(params.get("BAD_FLAG")!=null){
    		tempParam.put("BAD_FLAG", params.get("BAD_FLAG"));
    	}
    	//List<WmsSapPlantLgortEntity> result=lgortService.selectByMap(tempParam);
    	List<Map<String,Object>> result=lgortService.selectLgortList(tempParam);
    	return R.ok().put("result", result);
    }
    
    /**
     * 保存
     * @throws Exception 
     * @throws JSONException 
     */
    @RequestMapping("/save")
    public R saveInbound(@RequestParam Map<String, Object> params) throws Exception {
    	String inbountNo="";
    	try{
    		//通过params中的条件查询进仓单的数据，RECEIPT_NO，RECEIPT_ITEM_NO一致的比较进仓数量IN_QTY，试装数量TRY_QTY是否一致，不一致则报错
    		boolean isexit=false;
    		
    		Map<String, Object> cond=new HashMap<String, Object>();
    		cond.put("WERKS", params.get("WERKS"));
    		cond.put("WH_NUMBER", params.get("WH_NUMBER"));
    		cond.put("LGORT", params.get("LGORT"));
    		cond.put("BATCH", params.get("BATCH"));
    		cond.put("RELATED_AREA_NAME", params.get("RELATED_AREA_NAME"));
    		cond.put("LIFNR", params.get("LIFNR"));
    		cond.put("MATNR", params.get("MATNR"));
    		cond.put("RECEIPT_DATE_START", params.get("RECEIPT_DATE_START"));
    		cond.put("RECEIPT_DATE_END", params.get("RECEIPT_DATE_END"));
    		cond.put("ASNNO", params.get("ASNNO"));
    		cond.put("QC_RESULT", params.get("QC_RESULT"));
    		List<Map<String, Object>> result=wmsInInboundService.getReceiptList(cond);
    		JSONArray jarr = JSON.parseArray(params.get("ARRLIST").toString());
    		for(int i=0;i<jarr.size();i++){
    			String INABLE_QTY=jarr.getJSONObject(i).getString("INABLE_QTY");//页面的可进仓数量
    			BigDecimal INABLE_QTY_D=BigDecimal.ZERO;//页面的可进仓数量
    			if(!"".equals(INABLE_QTY)&&INABLE_QTY!=null){
    				INABLE_QTY_D=new BigDecimal(INABLE_QTY);
    			}
    			
    			String TRY_QTY=jarr.getJSONObject(i).getString("TRY_QTY");//页面的试装数量
    			BigDecimal TRY_QTY_D=BigDecimal.ZERO;//页面的试装数量
    			if(!"".equals(TRY_QTY)&&TRY_QTY!=null){
    				TRY_QTY_D=new BigDecimal(TRY_QTY);
    			}
    			
    			String RECEIPT_NO=jarr.getJSONObject(i).getString("RECEIPT_NO");//页面的收货单号
    			String RECEIPT_ITEM_NO=jarr.getJSONObject(i).getString("RECEIPT_ITEM_NO");//页面的收货单行号
    			if(result!=null&&result.size()>0){
    				for(int j=0;j<result.size();j++){//进仓单的数量
    					String RECEIPT_NO_in=result.get(j).get("RECEIPT_NO").toString();//进仓单的收货单号
    	    			String RECEIPT_ITEM_NO_in=result.get(j).get("RECEIPT_ITEM_NO").toString();//进仓单的收货单行号
    	    			
    	    			if((RECEIPT_NO.equals(RECEIPT_NO_in))&&(RECEIPT_ITEM_NO.equals(RECEIPT_ITEM_NO_in))){
    	    				//收货单号匹配
    	    				String INABLE_QTY_in=result.get(j).get("INABLE_QTY")==null?"0":result.get(j).get("INABLE_QTY").toString();//进仓单的可进仓数量
    	        			BigDecimal INABLE_QTY_D_in=BigDecimal.ZERO;//进仓单的可进仓数量
    	        			if(!"".equals(INABLE_QTY_in)&&INABLE_QTY_in!=null){
    	        				INABLE_QTY_D_in=new BigDecimal(INABLE_QTY_in);
    	        			}
    	        			
    	        			String TRY_QTY_in=result.get(j).get("TRY_QTY")==null?"0":result.get(j).get("TRY_QTY").toString();//进仓单的试装数量
    	        			BigDecimal TRY_QTY_D_in=BigDecimal.ZERO;//进仓单的试装数量
    	        			if(!"".equals(TRY_QTY_in)&&TRY_QTY_in!=null){
    	        				TRY_QTY_D_in=new BigDecimal(TRY_QTY_in);
    	        			}
    	        			
    	        			if(INABLE_QTY_D_in.subtract(INABLE_QTY_D).compareTo(BigDecimal.ZERO)!=0){
    	        				return R.error("创建失败，进仓单数据已经更新，请重新查询！");
    	        			}
    	        			if(TRY_QTY_D_in.subtract(TRY_QTY_D).compareTo(BigDecimal.ZERO)!=0){
    	        				return R.error("创建失败，进仓单数据已经更新，请重新查询！");
    	        			}
    	        			
    	        			isexit=true;
    	    			}
    					
    				}
    			}
    		}
    		if(!isexit){
    			return R.error("创建失败，进仓单数据已经更新，请重新查询！");
    		}
    		//
		 inbountNo=wmsInInboundService.saveInbound(params);
			if(inbountNo.equals("")) {
				return R.error("收货工厂未配置进仓单编号生成规则！系统异常，请联系管理员！");
			}
    	} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return R.error(e.getMessage()+"系统异常，请联系管理员！");
		}
    	return R.ok().put("inbountNo", inbountNo);
    }
    
    /**
     * 根据收货单获取收货单列表
     * @param params
     * @return
     */
    @RequestMapping("/Receiptlist")
    public R Receiptlist(@RequestParam Map<String, Object> params){
    	List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
    	result=wmsInInboundService.getReceiptInfoByReceiveNo(params);
    	Map<String, Object> retResult=new HashMap<String, Object>();
    	if(result.size()>0){
    		retResult=result.get(0);
    	}
        return R.ok().put("result", retResult);
    }
    
    /**
     * 根据采购订单号获取wms_sap_po_component信息
     * @param params
     * @return
     */
    @RequestMapping("/sapComponentlist")
    public R sapComponentlist(@RequestParam Map<String, Object> params){
    	List<Map<String, Object>> resultlist = new ArrayList<Map<String, Object>>();
    	
    	List<Map<String, Object>> resultReceipt=wmsInInboundService.getReceiptInfoByReceiveNo(params);
    	if(resultReceipt!=null&&resultReceipt.size()>0){
    		
    		resultlist=wmsInInboundService.getComponentInfoByPoNo(resultReceipt.get(0));
    		
        	if(resultlist.size()>0){
        	for(Map<String, Object> resMap:resultlist){
        		BigDecimal meng2_d=resMap.get("MENG2")==null?BigDecimal.ZERO:new BigDecimal(resMap.get("MENG2").toString());
        		BigDecimal meng1_d=resMap.get("MENG1")==null?BigDecimal.ONE:new BigDecimal(resMap.get("MENG1").toString());
        		BigDecimal in_qty_d=params.get("IN_QTY")==null?BigDecimal.ZERO:new BigDecimal(params.get("IN_QTY").toString());
        		BigDecimal new_in_qty=meng2_d.divide(meng1_d).setScale(3).multiply(in_qty_d);
        		resMap.put("MENG2", new_in_qty);//组件数量 3位小数
        		
        		Map<String, Object> sapMaterialMap = new HashMap <String, Object>();
        		sapMaterialMap.put("WERKS", resultReceipt.get(0).get("WERKS"));
        		sapMaterialMap.put("MATNR", resMap.get("MATN2"));
        		List<WmsSapMaterialEntity> sapMateriallist=wmsSapMaterialService.selectByMap(sapMaterialMap);
        		String MAKTX2="";
        		if(sapMateriallist!=null&&sapMateriallist.size()>0){
        			MAKTX2=sapMateriallist.get(0).getMaktx();
        		}
        		
        		resMap.put("MAKTX2", MAKTX2);
        		resMap.put("WERKS", resultReceipt.get(0).get("WERKS"));
        		resMap.put("WH_NUMBER", resultReceipt.get(0).get("WH_NUMBER"));
        		resMap.put("BATCH", resultReceipt.get(0).get("BATCH"));
        	}
        	}
    	}
    	
    	
        return R.ok().put("result", resultlist);
    }
    
}

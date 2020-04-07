package com.byd.wms.business.modules.account.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.byd.utils.DateUtils;
import com.byd.utils.R;
import com.byd.wms.business.modules.account.service.WmsAccountReceiptHxMoService;
import com.byd.wms.business.modules.common.service.WmsCTxtService;
import com.byd.wms.business.modules.common.service.WmsSapRemote;
import com.byd.wms.business.modules.config.entity.WmsCPlant;
import com.byd.wms.business.modules.config.service.WmsCPlantService;

/**
 * 账务处理-发货V（核销业务虚发）
 * 包含：SAP采购订单收料(V)、工厂间调拨收料（V）、SAP交货单收料（V)、成品入库（V）
 * @author (changsha) thw
 * @date 2018-11-01
 */
@RestController
@RequestMapping("account/wmsAccountOutV")
public class WmsAccountOutVController {
	private Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	WmsSapRemote wmsSapRemote;
    @Autowired
    WmsCTxtService wmsCTxtService;
    @Autowired
    WmsAccountReceiptHxMoService wmsAccountReceiptHxMoService;
    @Autowired
    WmsCPlantService wmsCPlantService;

	@RequestMapping("/listMOVMat")
    public R listMOVMat(@RequestBody Map<String, Object> params) {
    	String BUSINESS_NAME = params.get("BUSINESS_NAME").toString();//发货类型
    	String OUT_NO= params.get("OUT_NO").toString();//生产订单号
    	String WERKS = params.get("WERKS").toString();//工厂
    	
    	/**
    	 * 根据生产订单号、工厂，查询生产订单组件信息（及虚发信息）（检验抬头生产工厂是否启用核销业务）
    	 */
    	Map<String,Object> moInfo = wmsAccountReceiptHxMoService.getMoInfoByMoNo(OUT_NO,WERKS);
    	
    	List<Map<String,Object>> moHeadInfoList = moInfo.get("moHeadInfoList")==null?null:(List<Map<String,Object>>)moInfo.get("moHeadInfoList");
    	List<Map<String,Object>> moComponentInfoList = moInfo.get("moComponentInfoList")==null?null:(List<Map<String,Object>>)moInfo.get("moComponentInfoList");
    	if(moHeadInfoList ==null || moHeadInfoList.size()<=0 ||  moComponentInfoList==null || moComponentInfoList.size()<=0) {
    		return R.error("生产订单"+OUT_NO+"在生产工厂"+WERKS+"下不存在 ，请核对是否输入有误，如果无误，请使用生产订单同步功能，同步SAP数据！");
    	}
    	Map<String, Object> headMap = moHeadInfoList.get(0);//生产订单抬头信息
		/**
		 * 校验工厂是否已启用核销管理功能
		 */
		String HX_FLAG = headMap.get("HX_FLAG") == null?"":headMap.get("HX_FLAG").toString();
		if(!"X".equals(HX_FLAG)) {
			return R.error("发料工厂"+moHeadInfoList.get(0).get("WERKS")+"未启用核销业务，请检查工厂配置!");
		}
    	
    	/**
    	 * 校验操作账号是否有每一个生产订单关联的生产工厂的权限，移除没有操作权限的生产订单数据
    	 */
    	params.put("WERKS", WERKS);
    	
    	String grArror = "";
		if(!WERKS.equals(headMap.get("WERKS").toString())) {
			return R.error("不允许同时操作多个工厂的生产订单("+moHeadInfoList.get(0).get("AUFNR")+"-"+WERKS+"，"+headMap.get("AUFNR")+"-"+headMap.get("WERKS")+")！");
		}
		/**
		 * 校验生产订单状态是否可收货
		 */
		String ISTAT_TXT = headMap.get("ISTAT_TXT") == null?"":headMap.get("ISTAT_TXT").toString();//生产订单状态 
		String ELIKZ =  headMap.get("ELIKZ") == null?null:headMap.get("ELIKZ").toString(); // 交货已完成标识
		//生产订单状态必须包含REL（释放）字符串，且交货已完成标识为空的订单才允许收货
		if(!ISTAT_TXT.contains("REL")|| ISTAT_TXT.contains("DLFL") || ISTAT_TXT.contains("LKD") || "X".equals(ELIKZ)) {
			grArror = ","+headMap.get("ELIKZ");
		}
    	if(!"".equals(grArror)) {
    		return R.error("生产订单"+grArror+"未释放或已收货完成或已结算，不允许再收货！");
    	}
    	/**
    	 * 判断账号是否有采购订单对应工厂的操作权限，如果没有提示无权限
    	 */
    	List<Map<String,Object>> deptList= (List<Map<String,Object>>)params.get("deptList");
    	boolean auth=false;
    	for(Map dept:deptList) {
    		if(dept.get("CODE").equals(WERKS)) {
    			auth=true;
    			break;
    		}
    	}
    	if(!auth) {
    		return R.error("您无权操作"+WERKS+"工厂的生产订单！");
    	}
    	/**
    	 * 根据生产订单组件查询组件物料在WMS系统的虚拟库存、非限制库存、是否可超虚拟库存发料
    	 */
    	List<Map<String,Object>> virtualStockList = wmsAccountReceiptHxMoService.getMoComponentVirtualStock(moComponentInfoList);
    	
    	/**
    	 * 根据工厂、业务类型名称、WMS业务类型、仓库业务分类 查询 是否可超虚拟库存发料
    	 */
    	params.put("BUSINESS_TYPE", "11");
    	params.put("BUSINESS_CLASS", "09");
    	List<Map<String, Object>> plantBusinessList = wmsAccountReceiptHxMoService.getPlantBusinessInfo(params);
    	/**
    	 * 是否可超虚拟库存，主要用于核销虚发业务是否可扣减非限制库存 0 否，X 是
    	 */
    	String OVERSTEP_HX_FLAG = plantBusinessList.size()==0?"0":plantBusinessList.get(0)==null?"0":plantBusinessList.get(0).get("OVERSTEP_HX_FLAG")==null?"0":plantBusinessList.get(0).get("OVERSTEP_HX_FLAG").toString();
    	/**
    	 * 根据工厂代码、业务类型获取SAP生产订单收货（V）("60")抬头文本与行文本
    	 */
    	Map<String,String>txtMap=new HashMap<String,String>();
    	String JZ_DATE=params.get("JZ_DATE").toString().replaceAll("-", "");
    	txtMap.put("WERKS", WERKS);
    	txtMap.put("BUSINESS_NAME",BUSINESS_NAME);
    	txtMap.put("JZ_DATE", JZ_DATE);
    	txtMap.put("FULL_NAME", params.get("FULL_NAME").toString());
    	txtMap.put("MO_NO", OUT_NO);
    	Map<String, Object> txt = wmsCTxtService.getRuleTxt(txtMap);
    	
    	Iterator it = moComponentInfoList.iterator();
    
    	while(it.hasNext()) {
    		Map<String,Object> moComponentInfoMap = (Map<String, Object>) it.next();
    		String RSPOS = moComponentInfoMap.get("RSPOS").toString();		
    		//moComponentInfoMap.put("ID", i+1);
    		moComponentInfoMap.put("UNIT", moComponentInfoMap.get("MEINS"));
    		moComponentInfoMap.put("OVERSTEP_HX_FLAG", OVERSTEP_HX_FLAG);
    		moComponentInfoMap.put("HEADER_TXT", txt.get("txtrule"));
    		moComponentInfoMap.put("ITEM_TEXT", txt.get("txtruleitem"));
    		Double BDMNG = Double.valueOf(moComponentInfoMap.get("BDMNG").toString());//需求数量
    		
    		Double XF261 = Double.valueOf(moComponentInfoMap.get("XF261") == null?"0":moComponentInfoMap.get("XF261").toString());
    		Double XF262 = Double.valueOf(moComponentInfoMap.get("XF262") == null?"0":moComponentInfoMap.get("XF262").toString());
    		Double TL_QTY =   Double.valueOf(moComponentInfoMap.get("TL_QTY") == null?"0":moComponentInfoMap.get("TL_QTY").toString());
    		Double STOCK_QTY = 0.00;
    		Double VIRTUAL_QTY = 0.00;
    		for (Map<String, Object> map : virtualStockList) {
				if(RSPOS.equals(map.get("RSPOS"))) {
					STOCK_QTY = map.get("STOCK_QTY")==null?0:Double.valueOf(map.get("STOCK_QTY").toString());
					VIRTUAL_QTY = map.get("VIRTUAL_QTY")==null?0:Double.valueOf(map.get("VIRTUAL_QTY").toString());
					break;
				}
			}
    		
    		Double QTY = 0.000;
    		
    		if("X".equals(OVERSTEP_HX_FLAG)) {
    			//可超虚拟库存发料
    			if(VIRTUAL_QTY+STOCK_QTY -(BDMNG-TL_QTY)<0){
    				QTY = VIRTUAL_QTY+STOCK_QTY;
    			}else {
    				QTY = BDMNG-TL_QTY;
    			}
    		}else {
    			//不可超虚拟库存发料
    			if(VIRTUAL_QTY-(BDMNG-TL_QTY)<0){
    				QTY = VIRTUAL_QTY;
    			}else {
    				QTY = BDMNG-TL_QTY;
    			}
    		}
    		moComponentInfoMap.put("STOCK_QTY", STOCK_QTY);
    		moComponentInfoMap.put("VIRTUAL_QTY", VIRTUAL_QTY);
    		moComponentInfoMap.put("QTY", QTY);
    		moComponentInfoMap.put("QTY_ABLE", QTY);
    		moComponentInfoMap.put("QTY_XF", XF261-XF262);
    		if(QTY<=0) {
    			it.remove();
    		}
    	}
		
		return R.ok().put("result", moComponentInfoList);
	}
	
	
    /**
     * 发货（V）过账-核销业务
     * @param params
     * @return
     */
    @RequestMapping("/postGI")
    public R postGI(@RequestBody Map<String, Object> params) {
    	String BUSINESS_NAME=params.get("BUSINESS_NAME").toString();
    	try {
    		if("55".equals(BUSINESS_NAME)) {//SAP生产订单发料过账(V)
    	    	String matListStr=params.get("matList").toString();
    			String curDate=DateUtils.format(new Date(),"yyyy-MM-dd");
    			
    	    	List<Map<String,Object>> matList=new ArrayList<Map<String,Object>>();
    	    	List<String> MATNR_List = new ArrayList<String>();
    	    	List<Map<String,Object>> stockMatList = new ArrayList<Map<String,Object>>();
    	    	JSONObject.parseArray(matListStr, Map.class).forEach(m->{
    	    		m=(Map<String,Object>)m;
    	    		m.put("CREATOR", params.get("USERNAME").toString());
    	    		m.put("CREATE_DATE", curDate);
    	    		m.put("EDITOR", params.get("USERNAME").toString());
    	    		m.put("EDIT_DATE", curDate);
    	    		m.put("WERKS", params.get("WERKS"));
    	    		m.put("WH_NUMBER", params.get("WH_NUMBER"));
    	    		
    	    		if(!MATNR_List.contains(m.get("MATNR").toString()+"-"+m.get("LGORT").toString())) {
    	    			MATNR_List.add(m.get("MATNR").toString()+"-"+m.get("LGORT").toString());
    	    			stockMatList.add(m);
    	    		}
    	    		matList.add(m);
    	    	});
    	    	params.put("matList", matList);
    	    	params.put("stockMatList", stockMatList);
    			
    			return wmsAccountReceiptHxMoService.postGI_MOV(params);
    		}else if("56".equals(BUSINESS_NAME)) {//STO销售发货（V311T）
    			String matListStr=params.get("matList").toString(); 	    	
    			String curDate=DateUtils.format(new Date(),"yyyy-MM-dd");
    			List<Map<String,Object>> matList=new ArrayList<Map<String,Object>>();
    	    	List<String> MATNR_List = new ArrayList<String>();
    	    	List<Map<String,Object>> stockMatList = new ArrayList<Map<String,Object>>();
    	    	JSONObject.parseArray(matListStr, Map.class).forEach(m->{
    	    		m=(Map<String,Object>)m;
    	    		m.put("CREATOR", params.get("USERNAME").toString());
    	    		m.put("CREATE_DATE", curDate);
    	    		m.put("EDITOR", params.get("USERNAME").toString());
    	    		m.put("EDIT_DATE", curDate);
    	    		m.put("WERKS", params.get("WERKS"));
    	    		m.put("WH_NUMBER", params.get("WH_NUMBER"));
    	    		
    	    		if(!MATNR_List.contains(m.get("MATNR").toString()+"-"+m.get("LGORT").toString())) {
    	    			MATNR_List.add(m.get("MATNR").toString()+"-"+m.get("LGORT").toString());
    	    			stockMatList.add(m);
    	    		}
    	    		matList.add(m);
    	    	});
    	    	params.put("matList", matList);
    	    	params.put("stockMatList", stockMatList);
    			return null;
    		}else if("57".equals(BUSINESS_NAME)) {//工厂间调拨（V303）
    			String matListStr=params.get("matList").toString(); 	    	
    			String curDate=DateUtils.format(new Date(),"yyyy-MM-dd");
    			List<Map<String,Object>> matList=new ArrayList<Map<String,Object>>();
    	    	List<String> MATNR_List = new ArrayList<String>();
    	    	List<Map<String,Object>> stockMatList = new ArrayList<Map<String,Object>>();
    	    	
    	    	JSONObject.parseArray(matListStr, Map.class).forEach(m->{
    	    		m=(Map<String,Object>)m;
    	    		m.put("CREATOR", params.get("USERNAME").toString());
    	    		m.put("CREATE_DATE", curDate);
    	    		m.put("EDITOR", params.get("USERNAME").toString());
    	    		m.put("EDIT_DATE", curDate);
    	    		matList.add(m);
    	    		
    	    		Map<String,Object> smap=new HashMap<String,Object>();
    	    		smap.put("MATNR", m.get("MATNR"));
    	    		smap.put("WERKS", m.get("F_WERKS"));
    	    		smap.put("WH_NUMBER", m.get("F_WH_NUMBER"));
    	    		smap.put("LGORT", m.get("F_LGORT"));
    	    		
    	    		if(!MATNR_List.contains(m.get("MATNR").toString()+"-"+m.get("F_LGORT").toString())) {
    	    			MATNR_List.add(m.get("MATNR").toString()+"-"+m.get("F_LGORT").toString());
    	    			stockMatList.add(smap);
    	    		}
    	    		
    	    	});
    	    	params.put("matList", matList);
    	    	params.put("stockMatList", stockMatList);
    			
    	    	return wmsAccountReceiptHxMoService.postGI_303V(params);
    		}else {
    			return R.error("未知业务，系统无法处理，请联系系统管理员！");
    		}
		} catch(Exception e) {
			e.printStackTrace();
			System.out.println(e.getCause());
			return R.error("系统异常，请联系管理员！"+e.getMessage());
		}
    }
    
    /**
     * 根据料号、工厂、仓库号获取物料信息、库存信息、行文本、头文本
     * @param params
     * @return
     */
    @RequestMapping("/getMatInfo")
    public R getMatInfo(@RequestParam Map<String, Object> params) {
    	String BUSINESS_NAME = params.get("BUSINESS_NAME").toString();//发货类型
    	String WERKS = params.get("WERKS").toString();
       	/**
    	 * 根据工厂、业务类型名称、WMS业务类型、仓库业务分类 查询 是否可超虚拟库存发料
    	 */
    	params.put("BUSINESS_TYPE", "00");
    	params.put("BUSINESS_CLASS", "09");
    	List<Map<String, Object>> plantBusinessList = wmsAccountReceiptHxMoService.getPlantBusinessInfo(params);
    	/**
    	 * 是否可超虚拟库存，主要用于核销虚发业务是否可扣减非限制库存 0 否，X 是
    	 */
    	String OVERSTEP_HX_FLAG = plantBusinessList.size()==0?"0":
    		(plantBusinessList.get(0)==null?"0":(plantBusinessList.get(0).get("OVERSTEP_HX_FLAG")==null?"0":
    			(plantBusinessList.get(0).get("OVERSTEP_HX_FLAG").toString())));
    	/**
    	 * 获取物料信息，物料库存信息
    	 */
    	 List<Map<String,Object>> matList=new ArrayList<Map<String,Object>>();
    	 String matnr_str=params.get("matnr_list").toString();
         List<String> matnr_list=Arrays.asList(matnr_str.split(","));
         params.put("matnr_list", matnr_list);
         matList=wmsAccountReceiptHxMoService.getMatListByMATNR(params);
        /**
         * 获取头文本和行文本 
         */
        Map<String,String>txtMap=new HashMap<String,String>();
     	String JZ_DATE=params.get("JZ_DATE").toString().replaceAll("-", "");
     	txtMap.put("WERKS", WERKS);
     	txtMap.put("BUSINESS_NAME",BUSINESS_NAME);
     	txtMap.put("JZ_DATE", JZ_DATE);
     	txtMap.put("FULL_NAME", params.get("FULL_NAME").toString());
     	Map<String, Object> txt = wmsCTxtService.getRuleTxt(txtMap);
     	
         List<String> compare_list=new ArrayList<String>();
         StringBuilder error_msg=new StringBuilder();
         Iterator it=matList.iterator();
         int i=1;
         while(it.hasNext()) {
        	 Map<String,Object> m=(Map<String,Object>)it.next();
        	 compare_list.add(m.get("MATNR").toString());
        	 if(!params.get("WERKS").equals(m.get("F_WERKS"))) {
        		 error_msg.append("物料"+m.get("MATNR")+"不属于"+params.get("WERKS")+"工厂<br/>");
        		 it.remove();
        		 continue;
        	 }        	 
        	 m.put("HEADER_TXT", txt.get("txtrule"));
             m.put("ITEM_TEXT", txt.get("txtruleitem"));
        	 m.put("OVERSTEP_HX_FLAG", OVERSTEP_HX_FLAG);
        	 Double STOCK_QTY = m.get("STOCK_QTY")==null?0:Double.valueOf(m.get("STOCK_QTY").toString());
        	 Double VIRTUAL_QTY = m.get("VIRTUAL_QTY")==null?0:Double.valueOf(m.get("VIRTUAL_QTY").toString());
        	 Double QTY_ABLE=0d;
         	if("X".equals(OVERSTEP_HX_FLAG)) {
         		QTY_ABLE=STOCK_QTY+VIRTUAL_QTY;
        	}else {
        		QTY_ABLE= VIRTUAL_QTY;
        	}
    		m.put("QTY_ABLE", QTY_ABLE);
    		
            if(QTY_ABLE<=0) {
            	error_msg.append("物料"+m.get("MATNR")+"可过账数量为0");
            	it.remove();
            	continue;
            } 
         	
     		i++;
         }
         
         for(String s:matnr_list) {
        	if(!compare_list.contains(s)) {
        		error_msg.append("物料"+s+"未同步，请先手动执行同步功能<br/>");
        	}
         }

    	return R.ok().put("matList", matList).put("error_msg", error_msg.toString());
    }
    
    /**
     * STO虚发物料数据获取
     * @param params
     * @return
     */
    @RequestMapping("/getSTOVMatList")
    public R getSTOVMatList(@RequestBody Map<String, Object> params) {
    	String OUT_NO= params.get("OUT_NO").toString();//交货单号
    	String WERKS= params.get("WERKS").toString();//发货工厂
    	String BUSINESS_NAME = params.get("BUSINESS_NAME").toString();
    	/**
    	 * 通过调用SAP服务接口，读取SAP交货单数据
    	 */
    	Map<String,Object> dnMap = wmsSapRemote.getSapBapiDeliveryGetlist(OUT_NO);
    	if(null != dnMap.get("CODE") && "-1".equals(dnMap.get("CODE").toString())) {
    		//获取SAP交货单数据失败
    		return R.error("获取SAP交货单数据失败："+dnMap.get("MESSAGE").toString());
    	}
    	//交货单抬头信息
    	Map<String,Object> dnHeaderMap = (Map<String,Object>)dnMap.get("header");
    	
    	//交货单行项目信息
    	List<Map<String,Object>> dnItemList = (ArrayList<Map<String,Object>>)dnMap.get("item");

    	if(dnHeaderMap==null) {
    		return R.error("SAP交货单:"+OUT_NO+"不存在，请检查是否输入有误！");
    	}
    	if(!WERKS.equals(dnHeaderMap.get("VKORG"))) {
    		return R.error("SAP交货单:"+OUT_NO+"不属于工厂"+WERKS);
    	}
    	/*
    	 * 判断交货单收货工厂是否启用核销业务，否则提示错误
    	 */
    	params.put("werks", dnHeaderMap.get("VKORG"));
    	List<WmsCPlant> cPlantList = (List<WmsCPlant>)wmsCPlantService.queryPage(params).getList();
    	if(cPlantList==null || cPlantList.size()!=1 || !"X".equals(cPlantList.get(0).getHxFlag())) {
    		return R.error("收货工厂"+dnHeaderMap.get("VKORG")+"未启用核销业务，请检查工厂配置!");
    	}
    	/*
    	 * 判断账号是否有对应交货单工厂的操作权限，如果没有提示无权限 WERKS
    	 */
    	List<Map<String,Object>> deptList= (List<Map<String,Object>>)params.get("deptList");
    	boolean auth=false;
    	for(Map dept:deptList) {
    		if(dept.get("CODE").equals(dnHeaderMap.get("VKORG"))) {
    			auth=true;
    			break;
    		}
    	}
    	if(!auth) {
    		return R.error("您无权操作"+dnHeaderMap.get("VKORG")+"工厂（收货）的单据!");
    	}   	
    	if(dnItemList==null||dnItemList.size()==0) {
    		return R.error("SAP交货单:"+OUT_NO+"行项目不存在，请检查交货单是否输入有误！");
    	}
    	/*
    	 * 交货单是否已删除
    	 */
    	if(null != dnHeaderMap.get("SPE_LOEKZ") && "X".equals(dnHeaderMap.get("SPE_LOEKZ"))) {
    		//删除的交货单
    		return R.error("SAP交货单："+OUT_NO+"已删除！");
    	}
     	/*
    	 * 交货单类型是否正确  VBTYP == J
    	 */
    	if(null == dnHeaderMap.get("VBTYP") || (!"J".equals(dnHeaderMap.get("VBTYP")) && !"T".equals(dnHeaderMap.get("VBTYP")) ) ) {
    		//凭证类别 VBTYP不正确
    		return R.error("SAP交货单："+OUT_NO+"不是正确类型的交货单！");
    	}
    	if(null != dnHeaderMap.get("VBTYP") && "T".equals(dnHeaderMap.get("VBTYP"))) {
    		//凭证类别 VBTYP不正确 退货交货单
    		return R.error("SAP交货单："+OUT_NO+"为退货交货单，不允许发货！");
    	}
    	/**
    	 * 判断SAP交货单状态是否可收货，状态必须为A
    	 */
    	if("B".equals(dnHeaderMap.get("WBSTK"))) {
    		//交货单状态必须为B WBSTK
    		return R.error("SAP交货单："+OUT_NO+"已部分交货，不允许发货！");
    	}
    	if("C".equals(dnHeaderMap.get("WBSTK"))) {
    		//交货单状态必须为C WBSTK
    		return R.error("SAP交货单："+OUT_NO+"已交货，不允许发货！");
    	}
    	/**
    	 * 查询交货单是否存在未删除的需求号【WMS_OUT_REQUIREMENT_ITEM】，
    	 * 如果已存在，不允许用户再使用，并报错‘已存在**交货单的出库需求”。
    	 */
    	int requirement_count=wmsAccountReceiptHxMoService.getRequirementBySapOutNo(OUT_NO);
    	if(requirement_count>0) {
    		return R.error("SAP交货单："+OUT_NO+"已存在出库需求！");
    	}
    	/**
    	 * 根据SAP交货单组件查询组件物料在WMS系统的虚拟库存、非限制库存、是否可超虚拟库存发料
    	 */
    	List<Map<String,Object>> virtualStockList = wmsAccountReceiptHxMoService.getMoComponentVirtualStock(dnItemList);
    	
    	/**
    	 * 根据工厂、业务类型名称、WMS业务类型、仓库业务分类 查询 是否可超虚拟库存发料
    	 */
    	params.put("BUSINESS_TYPE", "07");
    	params.put("BUSINESS_CLASS", "09");
    	List<Map<String, Object>> plantBusinessList = wmsAccountReceiptHxMoService.getPlantBusinessInfo(params);
    	  /**
         * 获取头文本和行文本 
         */
        Map<String,String>txtMap=new HashMap<String,String>();
     	String JZ_DATE=params.get("JZ_DATE").toString().replaceAll("-", "");
     	txtMap.put("WERKS", WERKS);
     	txtMap.put("BUSINESS_NAME",BUSINESS_NAME);
     	txtMap.put("JZ_DATE", JZ_DATE);
     	txtMap.put("FULL_NAME", params.get("FULL_NAME").toString());
     	Map<String, Object> txt = wmsCTxtService.getRuleTxt(txtMap);
    	/**
    	 * 是否可超虚拟库存，主要用于核销虚发业务是否可扣减非限制库存 0 否，X 是
    	 */
    	String OVERSTEP_HX_FLAG = plantBusinessList.size()==0?"0":
    		(plantBusinessList.get(0)==null?"0":(plantBusinessList.get(0).get("OVERSTEP_HX_FLAG")==null?"0":
    			(plantBusinessList.get(0).get("OVERSTEP_HX_FLAG").toString())));
    	
    	Iterator it = dnItemList.iterator();
        
    	while(it.hasNext()) {
    		Map<String,Object> itemMap = (Map<String, Object>) it.next();
    		String POSNR = itemMap.get("POSNR").toString();	//SAP交货单行项目号
    		itemMap.put("UNIT", itemMap.get("MEINS"));
    		itemMap.put("OVERSTEP_HX_FLAG", OVERSTEP_HX_FLAG);
    		itemMap.put("HEADER_TXT", txt.get("txtrule"));
    		itemMap.put("ITEM_TEXT", txt.get("txtruleitem"));	
    		
    		Double JH_QTY =   Double.valueOf(itemMap.get("LFIMG") == null?"0":itemMap.get("LFIMG").toString());//SAP交货单交货数量
    		Double STOCK_QTY = 0.00;
    		Double VIRTUAL_QTY = 0.00;
    		for (Map<String, Object> map : virtualStockList) {
				if(POSNR.equals(map.get("POSNR"))) {
					STOCK_QTY = map.get("STOCK_QTY")==null?0:Double.valueOf(map.get("STOCK_QTY").toString());
					VIRTUAL_QTY = map.get("VIRTUAL_QTY")==null?0:Double.valueOf(map.get("VIRTUAL_QTY").toString());
					break;
				}
			}
    		
    		Double QTY = 0.000;
    		
    		if("X".equals(OVERSTEP_HX_FLAG)) {
    			//可超虚拟库存发料
    			if(VIRTUAL_QTY+STOCK_QTY -(JH_QTY)<0){
    				QTY = VIRTUAL_QTY+STOCK_QTY;
    			}else {
    				QTY = JH_QTY;
    			}
    		}else {
    			//不可超虚拟库存发料
    			if(VIRTUAL_QTY-(JH_QTY)<0){
    				QTY = VIRTUAL_QTY;
    			}else {
    				QTY = JH_QTY;
    			}
    		}
    		itemMap.put("JH_QTY", JH_QTY);
    		itemMap.put("STOCK_QTY", STOCK_QTY);
    		itemMap.put("VIRTUAL_QTY", VIRTUAL_QTY);
    		itemMap.put("QTY", QTY);
    		itemMap.put("QTY_ABLE", QTY);
    		itemMap.put("MAKTX", itemMap.get("ARKTX"));
    		itemMap.put("PO_NO", itemMap.get("VGBEL"));
    		itemMap.put("PO_ITEM_NO", itemMap.get("VGPOS"));

    		if(QTY<=0) {
    			it.remove();
    		}
    	}
    	return R.ok().put("result", dnItemList);
    }
}

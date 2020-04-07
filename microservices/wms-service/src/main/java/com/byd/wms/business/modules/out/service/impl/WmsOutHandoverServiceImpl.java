package com.byd.wms.business.modules.out.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.byd.utils.DateUtils;
import com.byd.utils.ListUtils;
import com.byd.utils.PageUtils;
import com.byd.utils.Query;
import com.byd.utils.R;
import com.byd.utils.RedisUtils;
import com.byd.wms.business.modules.account.dao.WmsAccountReceiptHxMoDao;
import com.byd.wms.business.modules.common.service.CommonService;
import com.byd.wms.business.modules.common.service.WarehouseTasksService;
import com.byd.wms.business.modules.common.service.WmsCTxtService;
import com.byd.wms.business.modules.config.dao.WmsCPlantToDao;
import com.byd.wms.business.modules.config.dao.WmsSapPlantLgortDao;
import com.byd.wms.business.modules.config.entity.WmsSapPlantLgortEntity;
import com.byd.wms.business.modules.in.dao.WmsInInboundDao;
import com.byd.wms.business.modules.out.dao.ScannerOutDAO;
import com.byd.wms.business.modules.out.dao.WmsOutHandoverDao;
import com.byd.wms.business.modules.out.dao.WmsOutRequirementHeadDao;
import com.byd.wms.business.modules.out.dao.WmsOutRequirementItemDao;
import com.byd.wms.business.modules.out.entity.WmsOutRequirementHeadEntity;
import com.byd.wms.business.modules.out.entity.WmsOutRequirementItemEntity;
import com.byd.wms.business.modules.out.service.WmsOutHandoverService;

/**
 * 需求交接服务类
 * @author 作者 : ren.wei3@byd.com 
 * @version 创建时间：2019年5月31日 上午9:39:25 
 *
 */
@Service("wmsOutHandoverService")
public class WmsOutHandoverServiceImpl implements WmsOutHandoverService{

	@Autowired
	private WmsOutRequirementHeadDao headDao;
	
	@Autowired
	private WmsOutRequirementItemDao itemDao;
	
	@Autowired
	private CommonService commonService;
	
	@Autowired
	private WarehouseTasksService warehouseTasksService;
	
	@Autowired
	private WmsOutHandoverDao wmsOutHandoverDao;
	
	@Autowired
	private WmsCTxtService wmsCtxService;
	
	@Autowired
	private WmsCPlantToDao wmsCPlantToDao;
	
	@Autowired
	private WmsAccountReceiptHxMoDao wmsAccountReceiptHxMoDao;
	
	@Autowired
	private WmsSapPlantLgortDao wmsSapPlantLgortDao;
	
	@Autowired
	private WmsInInboundDao wmsInInboundDao;
	
	@Autowired
	private ScannerOutDAO scannerOutDAO;
	
	@Autowired
    private RedisUtils redisUtils;
	
	@Override
	public PageUtils list(Map<String, Object> params) {
		//人料关系绑定，筛选数据
    	if (params.get("WH_MANAGER")!= null && !params.get("WH_MANAGER").equals("")) {
	    	params.put("AUTHORIZE_CODE", params.get("WH_MANAGER"));
	    	List<Map<String,Object>> relatedAreaName=wmsInInboundDao.getRelatedAreaName(params);//根据工厂仓库号查询
	    	if(relatedAreaName!=null&&relatedAreaName.size()>0){
	    		if("00".equals(relatedAreaName.get(0).get("MAT_MANAGER_TYPE"))){
	    			params.put("CONDMG", "1"); //MAT_MANAGER_TYPE为00， 筛选物料
				}else if("20".equals(relatedAreaName.get(0).get("MAT_MANAGER_TYPE"))){
					params.put("CONDMG", "2"); //MAT_MANAGER_TYPE为20, 筛选库位
				}
	    	}
    	}
    	
		List<Map<String, Object>> list = headDao.selectRequirement(params);
		
		if (list.size() > 0) {
			Map<String, String> txtparams = new HashMap<String, String>(); 
			txtparams.put("BUSINESS_NAME", list.get(0).get("BUSINESS_NAME")==null?"":list.get(0).get("BUSINESS_NAME").toString());
			txtparams.put("JZ_DATE", params.get("JZ_DATE")==null?"":params.get("JZ_DATE").toString());
			txtparams.put("WERKS", params.get("WERKS") ==null?"":params.get("WERKS").toString());
			txtparams.put("FULL_NAME", params.get("FULL_NAME") ==null?"":params.get("FULL_NAME").toString());
			txtparams.put("PURPOSE", list.get(0).get("PURPOSE") ==null?"":list.get(0).get("PURPOSE").toString());
			txtparams.put("requirement_no", params.get("REQUIREMENT_NO") ==null?"":params.get("REQUIREMENT_NO").toString());
			Map<String, Object> retrule=wmsCtxService.getRuleTxt(txtparams);
			
			for(Map<String, Object> map : list) {
				if(!retrule.isEmpty()){
					if("success".equals(retrule.get("msg"))){
						String txtrule=retrule.get("txtrule").toString();//头文本
						String txtruleitem=retrule.get("txtruleitem").toString();//行文本
						map.put("ITEM_TEXT", txtruleitem);
						map.put("TXTRULE", txtrule);
					}
				}
			}
		}
		Page page=new Query<Map<String,Object>>(params).getPage();
		page.setRecords(list);
		page.setTotal(list.size());
		page.setSize(list.size());
        return new PageUtils(page);
	}
	
	/**
	 * 需求（交接）过账
	 * 1、更新需求
	 * 2、更新条码
	 * 3、更新仓库任务
	 * 4、保存拣配下架记录表
	 * 5、更新核销信息
	 * 6、生成WMS凭证信息  
	 * 7、过账sap
	 * 8、更新SAP生产订单组件表投料数量
	 * 9、更新库存，减少下架数量
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public Map<String, Object> handover(Map<String, Object> params)  {
		
		List<List<Map<String,Object>>> sapList=new ArrayList<List<Map<String,Object>>>();//sap过账准备的
		List<List<Map<String,Object>>> wmsList=new ArrayList<List<Map<String,Object>>>();//wms保存事务记录准备的
		
		List<Map<String,Object>> matList = new ArrayList<Map<String,Object>>();//库存修改准备的
		List<Map<String,Object>> wtList = new ArrayList<Map<String,Object>>();//仓库任务修改准备的
		List<Map<String,Object>> reqList = new ArrayList<Map<String,Object>>();//需求修改准备的
		List<Map<String,Object>> labList = new ArrayList<Map<String,Object>>();//更新标签状态
		List<Map<String,Object>> hxToList = new ArrayList<Map<String,Object>>();//核销A303修改准备的
		List<Map<String,Object>> hxMoList = new ArrayList<Map<String,Object>>();//核销A261修改准备的
		List<Map<String,Object>> hxDnList = new ArrayList<Map<String,Object>>();//核销A311修改准备的
		List<Map<String,Object>> moTLInfo = new ArrayList<Map<String,Object>>();//更新生产订单投料数量准备的
		
		boolean uReceiveStock = false; //调拨业务，是否更新接收库位库存
		
		
		String SAP_MOVE_TYPE="";
		String WMS_MOVE_TYPE="";
		//为sap过账准备集合
		Map<String,Object> moveSyn=new HashMap<String,Object>();
		Map<String, Object> sapgzMap=new HashMap<String,Object>();
		
		String requirementNo = params.get("REQUIREMENT_NO").toString();
		JSONArray jarr = JSON.parseArray(params.get("ARRLIST").toString());
		
		if(jarr.size() < 1) {
			redisUtils.unlock(requirementNo, params.get("UID").toString());
    		throw new IllegalArgumentException("数据为空");
    	}
		
		
		List<Map<String, Object>> list = headDao.selectRequirement(params);
		if (list.size() < 1) {
			redisUtils.unlock(requirementNo, params.get("UID").toString());
			throw new IllegalArgumentException("单号无可交接数据！");
		} else {
			for(int i=0;i<jarr.size();i++){
				boolean handoverflag = false; 
				JSONObject itemData=  jarr.getJSONObject(i);
				String requirementItem = itemData.getString("REFERENCE_DELIVERY_ITEM").toString();
				for (Map<String, Object> reqitem : list) {
					if (requirementItem.equals(reqitem.get("REFERENCE_DELIVERY_ITEM"))) {
						handoverflag = true;
						break;
					}
				}
				
				if (!handoverflag) {
					redisUtils.unlock(requirementNo, params.get("UID").toString());
					throw new IllegalArgumentException("行："+requirementItem+" 已交接！");
				}
			}
		}
		
		List<Map<String, Object>> queryStockParams = new ArrayList<Map<String, Object>>(); //库存查询条件
		for(int i=0;i<jarr.size();i++){
			JSONObject itemData=  jarr.getJSONObject(i);
			Map<String, Object> matMap = new HashMap <String, Object>();
			matMap.put("WERKS", itemData.getString("WERKS"));
			matMap.put("WH_NUMBER", itemData.getString("WH_NUMBER"));
			matMap.put("MATNR", itemData.getString("MATNR"));
			matMap.put("LGORT", itemData.getString("LGORT"));
			matMap.put("BATCH", itemData.getString("BATCH"));
			matMap.put("BIN_CODE", itemData.getString("FROM_BIN_CODE"));
			matMap.put("SOBKZ", itemData.getString("SOBKZ"));
			matMap.put("LIFNR", itemData.getString("LIFNR"));
    		queryStockParams.add(matMap);
    	}
    	
    	List<Map<String, Object>> stockMatList = commonService.getWmsStock(queryStockParams);
    	
    	
    	
		for(int i=0;i<jarr.size();i++){
			JSONObject itemData=  jarr.getJSONObject(i);
			
			requirementNo = itemData.getString("REFERENCE_DELIVERY_NO").toString();
			BigDecimal gzQty = new BigDecimal(itemData.get("GZ_QTY").toString());
			
			//准备更新标签状态数据
			List labarr = new ArrayList<>();
			String LABEL_NO  = itemData.getString("LABEL_NO")==null?"":itemData.getString("LABEL_NO").toString();
			if(LABEL_NO.length()>0) {
				if(LABEL_NO.indexOf(",")>-1)
					labarr = Arrays.asList(LABEL_NO.split(","));
				else 
					labarr.add(LABEL_NO);				
			}						
			//JSONArray labarr = JSON.parseArray(itemData.getString("LABEL_NO").toString());
			for(int j=0;j<labarr.size();j++){
	    		Map<String,Object> labMap=new HashMap<String,Object>();
	    		labMap.put("LABEL_NO", labarr.get(j).toString());  //LABEL_NO	    		
	    		labMap.put("LABEL_STATUS", "10"); 
	    		labList.add(labMap);
	    	}
			
			//准备更新需求行数据
			Map<String, Object> outbounditem = new HashMap <String, Object>();
			outbounditem.put("REQUIREMENT_NO", itemData.getString("REFERENCE_DELIVERY_NO"));
			outbounditem.put("REQUIREMENT_ITEM_NO", itemData.getString("REFERENCE_DELIVERY_ITEM"));
			outbounditem.put("QTY_REAL", gzQty);
			reqList.add(outbounditem);
			
			//准备更新仓库任务数据
			Map<String, Object> wtMap = new HashMap <String, Object>();
			wtMap.put("ID", itemData.getString("ID"));
			wtMap.put("WH_NUMBER", itemData.getString("WH_NUMBER"));
			wtMap.put("TASK_NUM", itemData.getString("TASK_NUM"));
			wtMap.put("REAL_QUANTITY", gzQty);
			wtMap.put("EDITOR", params.get("USERNAME").toString());
			wtMap.put("EDIT_DATE", DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
			wtList.add(wtMap);
			
			
			//准备更新库存数据(减少下架数量)
			boolean stockflag = false;
    		for (Map<String, Object> bstockmat:stockMatList) {
        		if (itemData.get("MATNR").equals(bstockmat.get("MATNR")) && itemData.get("LGORT").equals(bstockmat.get("LGORT")) 
        			&& itemData.get("FROM_BIN_CODE").equals(bstockmat.get("BIN_CODE")) 
        			&& (null == itemData.get("BATCH") || "".equals(itemData.get("BATCH")) ? null == bstockmat.get("BATCH") || "".equals(bstockmat.get("BATCH")):itemData.get("BATCH").equals(bstockmat.get("BATCH")))
        			&& (null == itemData.get("SOBKZ") || "".equals(itemData.get("SOBKZ")) ? null == bstockmat.get("SOBKZ") || "".equals(bstockmat.get("SOBKZ")) :itemData.get("SOBKZ").equals(bstockmat.get("SOBKZ")))
        			&& (null == itemData.get("LIFNR") || "".equals(itemData.get("LIFNR")) ? null == bstockmat.get("LIFNR") || "".equals(bstockmat.get("LIFNR")) : itemData.get("LIFNR").equals(bstockmat.get("LIFNR")))) {

					Map<String, Object> matMap1 = new HashMap <String, Object>();
					matMap1.putAll(bstockmat);
					matMap1.put("XJ_QTY", gzQty.negate());
					matMap1.put("STOCK_QTY", "");
					matMap1.put("VIRTUAL_QTY", "");
					matMap1.put("VIRTUAL_LOCK_QTY", "");
					matMap1.put("LOCK_QTY", "");
					matMap1.put("FREEZE_QTY", "");
					matMap1.put("RSB_QTY", "");
					matMap1.put("EDITOR", params.get("USERNAME").toString());
					matMap1.put("EDIT_DATE", DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
					matList.add(matMap1);
					stockflag = true;
					break;
        		} 
    		}
        	if (!stockflag) {
        		redisUtils.unlock(requirementNo, params.get("UID").toString());
        		throw new RuntimeException("工厂 "+itemData.getString("WERKS")+" 料号"+itemData.getString("MATNR")+" 未找到待更新库存！");
	    	}
			
			
			//工厂间调拨(A303)
			if("53".equals(itemData.getString("BUSINESS_NAME"))&&"09".equals(itemData.getString("BUSINESS_TYPE"))){
				Map<String,Object> hxmap=new HashMap<String,Object>();
				hxmap.put("SF303", gzQty);
				hxmap.put("HX_QTY_XF", gzQty.negate());
				hxmap.put("F_WERKS", itemData.getString("WERKS")); //发货工厂代码
				hxmap.put("WERKS", itemData.getString("RECEIVE_WERKS")); //接收工厂代码
				hxmap.put("F_WH_NUMBER", itemData.getString("WH_NUMBER")); //发货仓库号
				hxmap.put("MATNR", itemData.getString("MATNR"));
				hxmap.put("SAP_MATDOC_NO", itemData.getString("SAP_MATDOC_NO"));
				hxmap.put("SAP_MATDOC_ITEM_NO", itemData.getString("SAP_MATDOC_ITEM_NO"));
				hxToList.add(hxmap);
			}
			
			//生产订单领料核销(A261)
			if("41".equals(itemData.getString("BUSINESS_NAME"))&&"11".equals(itemData.getString("BUSINESS_TYPE"))){
				Map<String,Object> hxmap=new HashMap<String,Object>();
				hxmap.put("SF261", gzQty);
				hxmap.put("HX_QTY", gzQty.negate());
				hxmap.put("AUFNR", itemData.getString("MO_NO"));
				hxmap.put("WERKS", itemData.getString("WERKS"));
				hxmap.put("MATNR", itemData.getString("MATNR"));
				hxMoList.add(hxmap);
			}
			
			//STO销售发货(A311T)
			if("54".equals(itemData.getString("BUSINESS_NAME"))&&"07".equals(itemData.getString("BUSINESS_TYPE"))){
				Map<String,Object> hxmap=new HashMap<String,Object>();
				hxmap.put("SF311T", gzQty);
				hxmap.put("HX_QTY_XF", gzQty.negate());
				hxmap.put("F_WERKS", itemData.getString("WERKS"));
				hxmap.put("F_WH_NUMBER", itemData.getString("WH_NUMBER")); //发货仓库号
				hxmap.put("MATNR", itemData.getString("MATNR"));
				hxmap.put("VBELN", itemData.getString("SAP_OUT_NO"));
				hxmap.put("POSNR", itemData.getString("SAP_OUT_ITEM_NO"));
				hxDnList.add(hxmap);
			}
			
			/**
			 * 更新SAP生产订单组件表投料数量
			 */
			if("41".equals(itemData.getString("BUSINESS_NAME"))){
				boolean addflag = true; 
				for (Map<String,Object> tlInfo:moTLInfo) {
					if (tlInfo.get("AUFNR").equals(itemData.getString("MO_NO")) && tlInfo.get("POSNR").equals(itemData.getString("MO_ITEM_NO"))) {
						BigDecimal tlqty = tlInfo.get("QTY") == null ? BigDecimal.ZERO:new BigDecimal(tlInfo.get("QTY").toString());
						BigDecimal gzqty = itemData.getString("GZ_QTY") == null ? BigDecimal.ZERO:new BigDecimal(itemData.getString("GZ_QTY"));
						tlInfo.put("QTY", tlqty.add(gzqty));
						addflag = false;
					}
				}
				
				if (addflag) {
					Map<String,Object> tlmap=new HashMap<String,Object>();
					tlmap.put("WERKS", itemData.getString("WERKS"));
					tlmap.put("AUFNR", itemData.getString("MO_NO"));
					tlmap.put("POSNR", itemData.getString("MO_ITEM_NO"));
					tlmap.put("MATNR", itemData.getString("MATNR"));
					tlmap.put("QTY", itemData.getString("GZ_QTY"));
					moTLInfo.add(tlmap);
				}
			}
			
			
			Map<String,Object> cdmap=new HashMap<String,Object>();
			cdmap.put("BUSINESS_NAME", itemData.getString("BUSINESS_NAME"));//
			cdmap.put("BUSINESS_TYPE", itemData.getString("BUSINESS_TYPE"));//
			
			//查询BUSINESS_CLASS
//			Map<String,Object> bussinessmap=new HashMap<String,Object>();
//			bussinessmap.put("BUSINESS_NAME", itemData.getString("BUSINESS_NAME"));
//			bussinessmap.put("BUSINESS_TYPE", itemData.getString("BUSINESS_TYPE"));
//			bussinessmap.put("SOBKZ", itemData.getString("SOBKZ"));
//			List<Map<String,Object>> retBusinessList=commonService.getWmsBusinessClass(bussinessmap);
//			if(retBusinessList!=null&&retBusinessList.size()>0){
//				cdmap.put("BUSINESS_CLASS", retBusinessList.get(0).get("BUSINESS_CLASS"));
//			}
			cdmap.put("BUSINESS_CLASS", "07");
			
			
			cdmap.put("WERKS", itemData.getString("WERKS"));
			cdmap.put("SOBKZ", itemData.getString("SOBKZ"));
			cdmap.put("LIFNR", itemData.getString("LIFNR")==null?"":itemData.getString("LIFNR"));
			
			JSONObject o = jarr.getJSONObject(0);
			if("46".equals(o.getString("BUSINESS_NAME"))) {
				Map<String, Object> map = new HashMap<>();
				map.put("WERKS_F", o.getString("WERKS"));
				map.put("WERKS_T", o.getString("RECEIVE_WERKS"));
				/**#BUG1429# BUSINESS_NAME=46:工厂间调拨
				 * 303 两步过账的账务，SOBKZ =K,不用把接收库位传给SAP过账接口
				 * 301 一步过账的账务，SOBKZ =Z,需要把接收库位传给SAP过账接口
				*/
				String sobkz = o.getString("SOBKZ");
				if("Z".equals(sobkz)){
					map.put("LGORT_T", o.getString("RECEIVE_LGORT"));
				}
				map.put("SOBKZ", sobkz);
				moveSyn=scannerOutDAO.getMoveAndSyn(map);
				if(moveSyn==null||"".equals(moveSyn.get("WMS_MOVE_TYPE").toString()))
					moveSyn=commonService.getMoveAndSyn(cdmap);
			}else {
				moveSyn=commonService.getMoveAndSyn(cdmap);
			}
			if(moveSyn==null) {
				redisUtils.unlock(requirementNo, params.get("UID").toString());
				throw new RuntimeException(itemData.getString("WERKS")+"工厂未配置业务类型！");
			}
			SAP_MOVE_TYPE = moveSyn.get("SAP_MOVE_TYPE")==null?"":moveSyn.get("SAP_MOVE_TYPE").toString();
			WMS_MOVE_TYPE=moveSyn.get("WMS_MOVE_TYPE")==null?"":moveSyn.get("WMS_MOVE_TYPE").toString();
			
			if (WMS_MOVE_TYPE.equals("") && itemData.getString("BUSINESS_NAME").equals("46")) {
				cdmap.put("LGORT", itemData.getString("LGORT"));
				cdmap.put("RECEIVE_WERKS", itemData.getString("RECEIVE_WERKS"));
				cdmap.put("RECEIVE_LGORT", itemData.getString("RECEIVE_LGORT"));
				cdmap.put("SOBKZ", itemData.getString("SOBKZ"));
				moveSyn = wmsCPlantToDao.getCPlantTo(cdmap);
				
				if(moveSyn==null) {
					if (itemData.getString("SOBKZ").equals("K")) {
						SAP_MOVE_TYPE = "303_K";
						WMS_MOVE_TYPE = "303_K";
					} else {
						SAP_MOVE_TYPE = "303";
						WMS_MOVE_TYPE = "303";
					}
				} else {
					SAP_MOVE_TYPE = moveSyn.get("SAP_MOVE_TYPE")==null?"":moveSyn.get("SAP_MOVE_TYPE").toString();
					WMS_MOVE_TYPE=moveSyn.get("WMS_MOVE_TYPE")==null?"":moveSyn.get("WMS_MOVE_TYPE").toString();
				}
			}
			
			// 为添加凭证记录行项目 准备数据
			Map<String, Object> docitemMap = new HashMap <String, Object>();
			docitemMap.put("LABEL_NO", itemData.getString("LABEL_NO"));
			docitemMap.put("BUSINESS_NAME", itemData.getString("BUSINESS_NAME"));
			docitemMap.put("BUSINESS_TYPE", itemData.getString("BUSINESS_TYPE"));
			docitemMap.put("WMS_MOVE_TYPE", WMS_MOVE_TYPE);
			docitemMap.put("SAP_MOVE_TYPE", SAP_MOVE_TYPE);
			docitemMap.put("SOBKZ", itemData.getString("SOBKZ"));
			docitemMap.put("MATNR", itemData.getString("MATNR"));
			docitemMap.put("MAKTX", itemData.getString("MAKTX"));
			docitemMap.put("WERKS", itemData.getString("WERKS"));
			docitemMap.put("WH_NUMBER", itemData.getString("WH_NUMBER"));
			docitemMap.put("LGORT", itemData.getString("LGORT"));
			docitemMap.put("BIN_CODE", itemData.getString("FROM_BIN_CODE"));
			docitemMap.put("UNIT", itemData.getString("UNIT"));
			docitemMap.put("QTY_WMS", itemData.getString("GZ_QTY")==null?"":itemData.getString("GZ_QTY"));
			docitemMap.put("QTY_SAP", itemData.getString("GZ_QTY")==null?"":itemData.getString("GZ_QTY"));
			docitemMap.put("BATCH", itemData.getString("BATCH"));
			docitemMap.put("BATCH_SAP", itemData.getString("BATCH"));
			docitemMap.put("HANDOVER", params.get("HANDOVER"));
			docitemMap.put("RECEIPT_NO", itemData.getString("RECEIPT_NO")==null?"":itemData.getString("RECEIPT_NO"));
			docitemMap.put("RECEIPT_ITEM_NO", itemData.getString("RECEIPT_ITEM_NO")==null?"":itemData.getString("RECEIPT_ITEM_NO"));
			docitemMap.put("LIFNR", itemData.getString("LIFNR")==null?null:itemData.getString("LIFNR"));
			docitemMap.put("LIKTX", itemData.getString("LIKTX")==null?null:itemData.getString("LIKTX"));
			docitemMap.put("MO_NO", itemData.getString("MO_NO")==null?null:itemData.getString("MO_NO"));
			docitemMap.put("MO_ITEM_NO", itemData.getString("MO_ITEM_NO")==null?null:itemData.getString("MO_ITEM_NO"));
			docitemMap.put("RSNUM", itemData.getString("RSNUM")==null?null:itemData.getString("RSNUM"));
			docitemMap.put("RSPOS", itemData.getString("RSPOS")==null?null:itemData.getString("RSPOS"));
			docitemMap.put("SO_NO", itemData.getString("SO_NO")==null?null:itemData.getString("SO_NO"));
			docitemMap.put("SO_ITEM_NO", itemData.getString("SO_ITEM_NO")==null?null:itemData.getString("SO_ITEM_NO"));
			docitemMap.put("PO_NO", itemData.getString("PO_NO")==null?null:itemData.getString("PO_NO"));
			docitemMap.put("PO_ITEM_NO", itemData.getString("PO_ITEM_NO")==null?null:itemData.getString("PO_ITEM_NO"));
			docitemMap.put("SAP_OUT_NO", itemData.getString("SAP_OUT_NO")==null?null:itemData.getString("SAP_OUT_NO"));
			docitemMap.put("SAP_OUT_ITEM_NO", itemData.getString("SAP_OUT_ITEM_NO")==null?null:itemData.getString("SAP_OUT_ITEM_NO"));
			docitemMap.put("IO_NO", itemData.getString("IO_NO")==null?null:itemData.getString("IO_NO"));
			docitemMap.put("AUTYP", itemData.getString("AUTYP")==null?null:itemData.getString("AUTYP"));
			docitemMap.put("COST_CENTER", itemData.getString("COST_CENTER")==null?null:itemData.getString("COST_CENTER"));
			docitemMap.put("WBS", itemData.getString("WBS")==null?null:itemData.getString("WBS"));
			docitemMap.put("CUSTOMER", itemData.getString("CUSTOMER")==null?null:itemData.getString("CUSTOMER"));
			docitemMap.put("SAKTO", itemData.getString("SAKTO")==null?null:itemData.getString("SAKTO"));
			docitemMap.put("ANLN1", itemData.getString("ANLN1")==null?null:itemData.getString("ANLN1"));
			docitemMap.put("REQUIREMENT_NO", itemData.getString("REFERENCE_DELIVERY_NO"));
			docitemMap.put("REQUIREMENT_ITEM_NO", itemData.getString("REFERENCE_DELIVERY_ITEM"));
			docitemMap.put("SAP_MATDOC_NO", itemData.getString("SAP_MATDOC_NO")==null?"":itemData.getString("SAP_MATDOC_NO"));
			docitemMap.put("SAP_MATDOC_ITEM_NO", itemData.getString("SAP_MATDOC_ITEM_NO")==null?"":itemData.getString("SAP_MATDOC_ITEM_NO"));
			docitemMap.put("CREATOR", params.get("USERNAME")+"："+params.get("FULL_NAME"));
			docitemMap.put("CREATE_DATE", DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));//
			docitemMap.put("ITEM_TEXT", itemData.getString("ITEM_TEXT")==null?"":itemData.getString("ITEM_TEXT"));
			docitemMap.put("HEADER_TXT", itemData.getString("TXTRULE")==null?"":itemData.getString("TXTRULE"));
						
			docitemMap.put("REF_SAP_MATDOC_YEAR", itemData.getString("REF_SAP_MATDOC_YEAR")==null?"":itemData.getString("REF_SAP_MATDOC_YEAR"));
			docitemMap.put("REF_SAP_MATDOC_NO", itemData.getString("REF_SAP_MATDOC_NO")==null?"":itemData.getString("REF_SAP_MATDOC_NO"));
			docitemMap.put("REF_SAP_MATDOC_ITEM_NO", itemData.getString("REF_SAP_MATDOC_ITEM_NO")==null?"":itemData.getString("REF_SAP_MATDOC_ITEM_NO"));
						
			docitemMap.put("BUSINESS_CLASS", cdmap.get("BUSINESS_CLASS"));
			if (!SAP_MOVE_TYPE.contains("311")) 
				docitemMap.put("MOVE_PLANT", itemData.getString("RECEIVE_WERKS")==null?null:itemData.getString("RECEIVE_WERKS"));
			
			docitemMap.put("MOVE_STLOC", itemData.getString("RECEIVE_LGORT")==null?null:itemData.getString("RECEIVE_LGORT"));
			
			if (SAP_MOVE_TYPE.contains("601")) {
				docitemMap.put("REVERSAL_FLAG", "X");
				docitemMap.put("CANCEL_FLAG", "X");
			}
			
						
			if(StringUtils.isNoneBlank(SAP_MOVE_TYPE)) {
				String[] strs=SAP_MOVE_TYPE.split("#");
				SAP_MOVE_TYPE="[";
				for(String s:strs) {
					SAP_MOVE_TYPE+="{\"moveType\":\""+s+"\",\"sapDoc\":\""+"\"}"+",";
				}
				SAP_MOVE_TYPE=SAP_MOVE_TYPE.substring(0, SAP_MOVE_TYPE.length()-1);
				SAP_MOVE_TYPE+="]";
				
				if(sapList!=null&&sapList.size()>0){//sap过账结果集中有数据
					boolean flag = true;
					for(int z=0;z<sapList.size();z++){
						List<Map<String, Object>> tempsaplist=sapList.get(z);//从已经添加的list中取出来
									
						//判断当前cdmap.get("BUSINESS_CLASS")的BUSINESS_CLASS是否和sapList结果集中的一样，是的话就从添加到已有的sapList中
						if(((cdmap.get("BUSINESS_CLASS").toString()).equals(tempsaplist.get(0).get("BUSINESS_CLASS")))
								&&((cdmap.get("BUSINESS_NAME").toString()).equals(tempsaplist.get(0).get("BUSINESS_NAME")))
								&&((cdmap.get("BUSINESS_TYPE").toString()).equals(tempsaplist.get(0).get("BUSINESS_TYPE")))
//								&&((cdmap.get("SOBKZ").toString()).equals(tempsaplist.get(0).get("SOBKZ")))
//								&&((cdmap.get("LIFNR").toString()).equals(tempsaplist.get(0).get("LIFNR")))
								){
											
							List<Map<String, Object>> matlistmap=(List<Map<String, Object>>) sapList.get(z).get(0).get("matList");
							matlistmap.add(docitemMap);
							sapList.get(z).get(0).put("matList", matlistmap);
							flag = false;				
							break;
						}	
					}
					//不是的就新增一个list保存
					if (flag) {
						List<Map<String, Object>> listsaptempMap1 = new ArrayList<Map<String, Object>>();
										
						List<Map<String, Object>> matlisttemp=new ArrayList<Map<String, Object>>();
						Map<String, Object> saptempmap=new HashMap<String, Object>();
						saptempmap.put("BUSINESS_CLASS", cdmap.get("BUSINESS_CLASS"));
						saptempmap.put("BUSINESS_NAME", cdmap.get("BUSINESS_NAME"));
						saptempmap.put("BUSINESS_TYPE", cdmap.get("BUSINESS_TYPE"));
						saptempmap.put("WERKS", cdmap.get("WERKS"));
						saptempmap.put("SOBKZ", itemData.getString("SOBKZ"));
						saptempmap.put("LIFNR", itemData.getString("LIFNR"));
						saptempmap.put("AUTYP", itemData.getString("AUTYP"));
										
						saptempmap.put("PZ_DATE", params.get("PZDDT"));
						saptempmap.put("JZ_DATE", params.get("JZDDT"));
						matlisttemp.add(docitemMap);
						saptempmap.put("matList", matlisttemp);
										
						if(saptempmap!=null&&saptempmap.size()>0){
							listsaptempMap1.add(saptempmap);
							sapList.add(listsaptempMap1);
						}
					}
				}else{//sap过账结果集中没有数据
					List<Map<String, Object>> listsaptempMap1 = new ArrayList<Map<String, Object>>();
									
					List<Map<String, Object>> matlisttemp=new ArrayList<Map<String, Object>>();
					Map<String, Object> saptempmap=new HashMap<String, Object>();
					saptempmap.put("BUSINESS_CLASS", cdmap.get("BUSINESS_CLASS"));
					saptempmap.put("BUSINESS_NAME", cdmap.get("BUSINESS_NAME"));
					saptempmap.put("BUSINESS_TYPE", cdmap.get("BUSINESS_TYPE"));
					saptempmap.put("WERKS", cdmap.get("WERKS"));
					saptempmap.put("SOBKZ", itemData.getString("SOBKZ"));
					saptempmap.put("LIFNR", itemData.getString("LIFNR"));
					
					saptempmap.put("AUTYP", itemData.getString("AUTYP"));
									
					saptempmap.put("PZ_DATE", params.get("PZDDT"));
					saptempmap.put("JZ_DATE", params.get("JZDDT"));
					matlisttemp.add(docitemMap);
					saptempmap.put("matList", matlisttemp);
					listsaptempMap1.add(saptempmap);
					sapList.add(listsaptempMap1);
									
				}
							
			}else {//不需要sap 过账
								
			}
						
			//准备wms事务记录插入数据
			if(wmsList!=null&&wmsList.size()>0){
				boolean flag = true;
				//wmsList结果集中有数据
				for(int z=0;z<wmsList.size();z++){
					List<Map<String, Object>> tempwmslist=wmsList.get(z);//从已经添加的list中取出来
									
					//判断当前cdmap.get("BUSINESS_CLASS")的BUSINESS_CLASS等参数是否和sapList结果集中的一样，是的话就从添加到已有的sapList中
					if(((cdmap.get("BUSINESS_CLASS").toString()).equals(tempwmslist.get(0).get("BUSINESS_CLASS")))
							&&((cdmap.get("BUSINESS_NAME").toString()).equals(tempwmslist.get(0).get("BUSINESS_NAME")))
							&&((cdmap.get("BUSINESS_TYPE").toString()).equals(tempwmslist.get(0).get("BUSINESS_TYPE")))
//							&&((cdmap.get("SOBKZ").toString()).equals(tempwmslist.get(0).get("SOBKZ")))
//							&&((cdmap.get("LIFNR").toString()).equals(tempwmslist.get(0).get("LIFNR")))
							){
						
						//把当前的取出来，放到已存在的结果集中去
						tempwmslist.add(docitemMap);
						flag = false;
						break;
					}			
				}
				if (flag) {
					List<Map<String, Object>> matlisttemp=new ArrayList<Map<String, Object>>();
					
					matlisttemp.add(docitemMap);
					wmsList.add(matlisttemp);//wms事务记录准备
				}
							
			}else{//
				List<Map<String, Object>> matlisttemp=new ArrayList<Map<String, Object>>();
								
				matlisttemp.add(docitemMap);
				wmsList.add(matlisttemp);//wms事务记录准备
			}
				
		}
		
		/**
		 * 库存地点调拨、工厂间调拨（301）业务，如果接受库位已上WMS，需要增加接受库位库存
		 */
    	String rWerks = "";
    	String rLgort = "";
    	String businessname = jarr.getJSONObject(0).getString("BUSINESS_NAME");
    	if ((businessname.equals("46") && SAP_MOVE_TYPE.contains("301")) || businessname.equals("47") || businessname.equals("48") || businessname.equals("77")) {
    		if (businessname.equals("47") || businessname.equals("48")) {
    			rWerks = jarr.getJSONObject(0).getString("WERKS");
    		} else {
    			rWerks = jarr.getJSONObject(0).getString("RECEIVE_WERKS");
    		}
    		
    		rLgort = jarr.getJSONObject(0).getString("RECEIVE_LGORT");
			/**
			 *  bug:1506
			 *  author:rain
			 *  date:2019年11月15日16:06:41
			 *  description:当接收仓库不为wms_sap_plant_lgort表中del=X的库位，才需要新增或者修改库位的信息。
			 */
    		List<WmsSapPlantLgortEntity> plantlgortlist = wmsSapPlantLgortDao.selectList(new EntityWrapper<WmsSapPlantLgortEntity>()
		             .eq(StringUtils.isNotBlank(rWerks),"WERKS", rWerks)
		             .eq(StringUtils.isNotBlank(rLgort),"LGORT", rLgort).eq("DEL","0")
		             );
    		
    		if (!plantlgortlist.isEmpty()) {
    			uReceiveStock = true;
    		}
    	}
		
		//1、更新需求
		if (reqList.size() > 0) {
			ListUtils.sort(reqList, "REQUIREMENT_ITEM_NO", true);
			itemDao.updateOutboundItemQtyANDStatus(reqList); //更新需求行交接数量和状态
		}
		
		WmsOutRequirementHeadEntity headEntity= new WmsOutRequirementHeadEntity();
		List<WmsOutRequirementItemEntity> ilist = itemDao.selectList(new EntityWrapper<WmsOutRequirementItemEntity>()
		             .eq(StringUtils.isNotBlank(requirementNo),"REQUIREMENT_NO", requirementNo)
		             .notIn("REQ_ITEM_STATUS", new String[]{"05","06"}));
				
		if (ilist.size() > 0) {
			headEntity.setRequirementStatus("05"); //部分交接
		} else {
			headEntity.setRequirementStatus("06"); //已交接
		}
		headEntity.setEditor(params.get("USERNAME").toString());
		headEntity.setEditDate(DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
		headDao.update(headEntity, new EntityWrapper<WmsOutRequirementHeadEntity>()
						.eq("REQUIREMENT_NO", requirementNo));
		
		//2、更新条码
		if(labList.size()>0) {
			ListUtils.sort(labList, "LABEL_NO", true);
			commonService.updateLabel(labList);
		}
		
		List<Map<String,Object>> querywtList = warehouseTasksService.selectCoreWHTaskList(wtList);
		for (Map<String,Object> wt : wtList) {
			BigDecimal realqtyadd = wt.get("REAL_QUANTITY") == null ? BigDecimal.ZERO:new BigDecimal(wt.get("REAL_QUANTITY").toString());
			for (Map<String,Object> querywt : querywtList) {
				if (wt.get("TASK_NUM").equals(querywt.get("TASK_NUM"))) {
					BigDecimal taskqty = querywt.get("QUANTITY") == null ? BigDecimal.ZERO:new BigDecimal(querywt.get("QUANTITY").toString());
					BigDecimal realqty = querywt.get("REAL_QUANTITY") == null ? BigDecimal.ZERO:new BigDecimal(querywt.get("REAL_QUANTITY").toString());
					if ((realqty.add(realqtyadd)).compareTo(taskqty) < 0) {
						wt.put("WT_STATUS", "04"); //部分交接
					} else {
						wt.put("WT_STATUS", "05"); //已交接
					}
					break;
				}
			}
		}
		
		//3、更新仓库任务
		ListUtils.sort(wtList, "ID", true);
		warehouseTasksService.updateCoreWHTask(wtList);
		
		//4、保存拣配下架记录表
		warehouseTasksService.mergeWmsOutPicking(wtList);
		
		//5、 更新核销信息
		if (hxToList.size() > 0) {
			wmsOutHandoverDao.updateHXTO(hxToList);
		}
		if (hxMoList.size() > 0) {
			wmsOutHandoverDao.updateHXMO(hxMoList);
		}
		if (hxDnList.size() > 0) {
			wmsOutHandoverDao.updateHXDN(hxDnList);
		}
		
		
		
		//6、 生成WMS凭证信息  
		Map<String,Object> head=new HashMap<String,Object>();
		head.put("WERKS", jarr.getJSONObject(0).getString("WERKS"));
		head.put("PZ_DATE", params.get("PZDDT"));
		head.put("JZ_DATE", params.get("JZDDT"));
		head.put("PZ_YEAR", params.get("PZDDT").toString().substring(0,4));
		head.put("HEADER_TXT", jarr.getJSONObject(0).getString("TXTRULE"));
		head.put("TYPE",  "00");//标准凭证
		head.put("CREATOR", params.get("USERNAME")+"："+params.get("FULL_NAME"));
		head.put("CREATE_DATE", DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));//
		head.put("SAP_MOVE_TYPE",  SAP_MOVE_TYPE);
		String WMS_NO="";
		if(wmsList!=null&&wmsList.size()>0){
			for(int p=0;p<wmsList.size();p++){
				String WMS_NO_temp=commonService.saveWMSDoc(head, wmsList.get(p));
				WMS_NO=WMS_NO+" "+WMS_NO_temp;
			}
		}
		
		//8、更新SAP生产订单组件表投料数量
		if (moTLInfo.size() > 0)
		wmsAccountReceiptHxMoDao.updateSapMoComponentTLInfo(moTLInfo);
		
		//9、更新库存，减少下架数量
		ListUtils.sort(matList, "ID", true); //先排序防死锁
		commonService.modifyWmsStock(matList);
		
		//调拨业务，是否更新接收库位库存
		if (uReceiveStock) {
			for(Map<String,Object> mat:matList) {
				mat.put("WERKS", rWerks);
				mat.put("WH_NUMBER", rWerks);
				mat.put("LGORT", rLgort);
				mat.put("BIN_CODE", "AAAA");
				BigDecimal stockqty = new BigDecimal(mat.get("XJ_QTY").toString());
				mat.put("STOCK_QTY", stockqty.negate());
				mat.put("XJ_QTY", "");
				if (!businessname.equals("47"))
					mat.put("SOBKZ", "Z");
			}
			Map<String, Object> receiveStockparams = new HashMap<String, Object>();
			receiveStockparams.put("STOCK_TYPE", "STOCK_QTY");
			receiveStockparams.put("matList", matList);
			commonService.saveWmsStock(receiveStockparams);
		}
				
		//7、过账sap
		sapgzMap.put("WERKS", jarr.getJSONObject(0).getString("WERKS"));
		sapgzMap.put("JZ_DATE", params.get("JZDDT"));
		sapgzMap.put("PZ_DATE", params.get("PZDDT"));
		sapgzMap.put("HEADER_TXT", jarr.getJSONObject(0).getString("TXTRULE"));
				
		String SAP_NO="";
		for(int d=0;d<sapList.size();d++){
			List<Map<String,Object>> sapmaplist=sapList.get(d);
			for(int e=0;e<sapmaplist.size();e++){
				String SAP_NO_temp=commonService.doSapPost(sapmaplist.get(e));
				SAP_NO=SAP_NO+" "+SAP_NO_temp;
			}
		}
		
		StringBuilder msg=new StringBuilder();
		
		msg.append(" WMS凭证:");
		msg.append(WMS_NO);
		if(StringUtils.isNotBlank(SAP_NO)) {
			msg.append("<br/> SAP凭证:");
			msg.append(SAP_NO);
		}
		
		return R.ok(msg.toString())
				.put("wmsNo", WMS_NO)
				.put("sapNo", SAP_NO);
	}

}

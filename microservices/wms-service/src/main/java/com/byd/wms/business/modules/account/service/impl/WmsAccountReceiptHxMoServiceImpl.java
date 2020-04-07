package com.byd.wms.business.modules.account.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.byd.utils.DateUtils;
import com.byd.utils.R;
import com.byd.wms.business.modules.account.dao.WmsAccountReceiptHxMoDao;
import com.byd.wms.business.modules.account.service.WmsAccountReceiptHxMoService;
import com.byd.wms.business.modules.common.dao.CommonDao;
import com.byd.wms.business.modules.common.service.CommonService;
import com.byd.wms.business.modules.common.service.WmsCoreMatBatchService;

/**
 * SAP生产订单核销业务（成品、联产品、副产品虚收）Service
 *
 * @author (changsha) byd_infomation_center
 * @email 
 * @date 2018-10-24 16:06:38
 */
@Service("wmsAccountReceiptHxMoService")
public class WmsAccountReceiptHxMoServiceImpl implements WmsAccountReceiptHxMoService {
	@Autowired
	private WmsCoreMatBatchService wmsMatBatchService;
	@Autowired
	private CommonService commonService;
	@Autowired
	private CommonDao commonDao;
	@Autowired
	private WmsAccountReceiptHxMoDao wmsAccountReceiptHxMoDao;
	
	 /**
     * 根据生产订单号更新生产订单抬头（成品虚收）核销信息
     * @param map 必须包含值：AUFNR：生产订单号
     * @return boolean
     */
	@Override
    public boolean updateMoHeadByMoNo(Map<String,Object> map) {
		return false;
	}
    
    /**
     * 根据生产订单号更新生产订单组件（联产品、副产品、投料）核销信息
     * @param map 必须包含值：AUFNR：生产订单号、RSNUM：预留/相关需求的编号、RSPOS：预留/相关需求的项目编号
     * @return boolean
     */
	@Override
	public boolean updateMoComponentByMoNo(Map<String,Object> map) {
		return false;
	}
    
    /**
     * 根据生产订单号，查询生产订单及关联的核销信息（包含抬头、行项目和副产品及核销信息）
     * @param  moList 必须包含值：AUFNR 生产订单号数组 WERKS 生产工厂
     * @return map 包含： moHeadInfoList-List<Map<String,Object>>：生产订单抬头及核销信息（成品收货（V）） 
     * 			   moComponentInfoList-List<Map<String,Object>>：生产订单副产品及核销信息（副产品收货（V）核销信息）
     */
	@Override
    public Map<String, Object> getMoInfoByMoNo(List<String> moList,String WERKS){
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		List<Map<String,Object>> moHeadInfoList = wmsAccountReceiptHxMoDao.getMoHeadInfoByMoNo(moList,WERKS);
		List<Map<String,Object>> moItemInfoList = wmsAccountReceiptHxMoDao.getMoItemInfoByMoNo(moList,WERKS);
		List<Map<String,Object>> moComponentInfoList = wmsAccountReceiptHxMoDao.getMoByComponentInfoByMoNo(moList,WERKS);
		
		rtnMap.put("moHeadInfoList", moHeadInfoList);
		rtnMap.put("moItemInfoList", moItemInfoList);
		rtnMap.put("moComponentInfoList", moComponentInfoList);
		
		return rtnMap;
	}
	
    /**
     * 虚发
     * 根据生产订单号，查询生产订单抬头及组件的核销信息（包含抬头、组件、组件核销信息）
     * @param  MO_NO ：生产订单号  WERKS 生产工厂,BWART:移动类型 
     * @return map 包含： moHeadInfoList-List<Map<String,Object>>：生产订单抬头及核销信息 
     * 			   moComponentInfoList-List<Map<String,Object>>：生产订单组件及核销信息
     */
	@Override
    public Map<String, Object> getMoInfoByMoNo(String MO_NO,String WERKS){
		Map<String, Object> rtnMap = new HashMap<String, Object>();
		List<String> moList = new ArrayList<String>();
		moList.add(MO_NO);
		String BWART="261";
		List<Map<String,Object>> moHeadInfoList = wmsAccountReceiptHxMoDao.getMoHeadInfoByMoNo(moList,WERKS);
		List<Map<String,Object>> moComponentInfoList = wmsAccountReceiptHxMoDao.getMoComponentInfoByMoNo(moList,WERKS,BWART);
		rtnMap.put("moHeadInfoList", moHeadInfoList);
		rtnMap.put("moComponentInfoList", moComponentInfoList);
		
		return rtnMap;
	}
	
	 /**
     * 根据生产订单组件查询组件物料在WMS系统的虚拟库存、非限制库存、是否可超虚拟库存发料
     * @param moComponentList 生产订单组装机
     * @return moComponentList 包含了库存信息
     */
	@Override
	public List<Map<String, Object>> getMoComponentVirtualStock(List<Map<String, Object>> moComponentList){
		return wmsAccountReceiptHxMoDao.getMoComponentVirtualStock(moComponentList);
	}
	
	  /**
     * 根据工厂、业务类型名称、WMS业务类型、仓库业务分类 查询 是否可超虚拟库存发料
     * @param map WERKS：工厂 BUSINESS_NAME：业务类型名称 BUSINESS_TYPE：WMS业务类型  BUSINESS_CLASS：业务分类
     * @return
     */
	@Override
	public List<Map<String, Object>> getPlantBusinessInfo(Map<String,Object> map){
		return wmsAccountReceiptHxMoDao.getPlantBusinessInfo(map);
	}
	
	/**
	 * SAP生产订单核销业务（成品、联产品、副产品虚收）收货（V）-核销业务
	 */
	@Override
	@Transactional(rollbackFor=Exception.class)
	public R boundIn_MOV(Map<String, Object> params) {
		List<Map<String,Object>> matList=(List<Map<String,Object>>) params.get("matList");
		String WERKS = params.get("WERKS").toString();//收货工厂
		String BUSINESS_NAME = params.get("BUSINESS_NAME").toString();
		String SAP_MOVE_TYPE="";//SAP移动类型，为空不进行SAP过账
		
		String curDate=DateUtils.format(new Date(),"yyyy-MM-dd");
		params.put("SEARCH_DATE", curDate);
		params.put("BUSINESS_NAME", BUSINESS_NAME);//SAP采购订单收料(V)
		params.put("BUSINESS_TYPE", "10");//生产订单
		params.put("BUSINESS_CLASS", "08");
		
		/**
		 * 获取WMS移动类型、移动原因、SAP移动类型、SAP过账标识
		 */
		Map<String,Object> cdmap=new HashMap<String,Object>();
		cdmap.put("BUSINESS_NAME", BUSINESS_NAME);//SAP采购订单收料(V)
		cdmap.put("BUSINESS_TYPE", "10");//生产订单
		cdmap.put("BUSINESS_CLASS", "08");
		cdmap.put("WERKS", WERKS);
		cdmap.put("SOBKZ", matList.get(0).get("SOBKZ"));
		Map<String,Object> moveSyn = commonDao.getMoveAndSyn(cdmap);
		if(moveSyn == null || moveSyn.get("WMS_MOVE_TYPE")==null) {
			/**
			 * 系统未配置SAP移动类型
			 */
			return R.error("系统未配置生产订单收货（V）业务类型！");
		}
		SAP_MOVE_TYPE = moveSyn.get("SAP_MOVE_TYPE")==null?"":moveSyn.get("SAP_MOVE_TYPE").toString();
		//SAP移动类型JSON字符串拼装
		if(StringUtils.isBlank(SAP_MOVE_TYPE)) {
			return R.error("收货工厂"+WERKS+"未配置生产订单收货(V)业务类型的SAP过账移动类型！");
		}
		params.put("SAP_MOVE_TYPE", SAP_MOVE_TYPE);
		params.put("WMS_MOVE_TYPE", moveSyn.get("WMS_MOVE_TYPE"));
		params.put("SYN_FLAG", moveSyn.get("SYN_FLAG"));
		
		/**
		 * 保存WMS库存
		 */
		params.put("STOCK_TYPE","VIRTUAL_QTY");
		commonService.saveWmsStock(params);
		
		List<Map<String,Object>> hxMoList = new ArrayList<Map<String,Object>>();
		if("60".equals(BUSINESS_NAME)) {
			for (Map<String, Object> mat : matList) {
				Map<String,Object> hxMoItem = new HashMap<String,Object>();
				double xs101m = Double.valueOf(mat.get("RECEIPT_QTY").toString());
				String HX_QTY = mat.get("HX_QTY")==null?"0":mat.get("HX_QTY").toString();
				if(HX_QTY.equals("")) {
					HX_QTY ="0";
				}
				double hxQty = Double.valueOf(HX_QTY);
				String HX_MO_ITEM_ID = mat.get("HX_MO_ITEM_ID")==null?null:mat.get("HX_MO_ITEM_ID").toString();
				if(null!=HX_MO_ITEM_ID&&!"".equals(HX_MO_ITEM_ID)) {
					//已存在核销信息
					hxMoItem = wmsAccountReceiptHxMoDao.getHxMoItemById(HX_MO_ITEM_ID);
					hxQty = Double.valueOf(hxMoItem.get("HX_QTY")==null?"0":hxMoItem.get("HX_QTY").toString())+xs101m;
					
					hxMoItem.put("XS102M",0);
					hxMoItem.put("SS101M",0);
					hxMoItem.put("SS102M",0);
					hxMoItem.put("EDITOR",mat.get("CREATOR").toString());
					hxMoItem.put("EDIT_DATE",mat.get("CREATE_DATE").toString());
				}else {
					hxMoItem.put("WERKS",mat.get("WERKS").toString());
					hxMoItem.put("WH_NUMBER",mat.get("WH_NUMBER").toString());
					hxMoItem.put("AUFNR",mat.get("AUFNR").toString());
					hxMoItem.put("POSNR",mat.get("POSNR").toString());
					hxMoItem.put("MATNR",mat.get("MATNR").toString());
					hxMoItem.put("MAKTX",mat.get("MAKTX").toString());
					hxMoItem.put("PSMNG",mat.get("PSMNG")==null?mat.get("BDMNG"):mat.get("PSMNG"));
					hxMoItem.put("MEINS",mat.get("MEINS").toString());
					hxQty = xs101m;
					hxMoItem.put("XS102M",0);
					hxMoItem.put("SS101M",0);
					hxMoItem.put("SS102M",0);
					
					hxMoItem.put("CREATOR",mat.get("CREATOR").toString());
					hxMoItem.put("CREATE_DATE",mat.get("CREATE_DATE").toString());
				}
				
				
				hxMoItem.put("LGORT",mat.get("LGORT").toString());
				hxMoItem.put("XS101M",xs101m);
				hxMoItem.put("HX_QTY",hxQty);
				
				hxMoList.add(hxMoItem);
			}
			/**
			 * 保存SAP生产订单收货过账(V)核销信息
			 */
			wmsAccountReceiptHxMoDao.insertOrUpdateMoItemHxInfo(hxMoList);
		}
		if("62".equals(BUSINESS_NAME)) {
			for (Map<String, Object> mat : matList) {
				Map<String,Object> hxMoItem = new HashMap<String,Object>();
				double xs531 = Double.valueOf(mat.get("RECEIPT_QTY").toString());
				String HX_QTY_BY = mat.get("HX_QTY_BY")==null?"0":mat.get("HX_QTY_BY").toString();
				if(HX_QTY_BY.equals("")) {
					HX_QTY_BY = "0";
				}
				double hxQtyBy = Double.valueOf(HX_QTY_BY);
				String HX_MO_ITEM_ID = mat.get("HX_MO_COMPONENT_ID")==null?null:mat.get("HX_MO_COMPONENT_ID").toString();
				if(null!=HX_MO_ITEM_ID&&!"".equals(HX_MO_ITEM_ID)) {
					//已存在核销信息
					hxMoItem = wmsAccountReceiptHxMoDao.getHxMoComponentById(HX_MO_ITEM_ID);
					hxQtyBy = Double.valueOf(hxMoItem.get("HX_QTY_BY")==null?"0":hxMoItem.get("HX_QTY_BY").toString())+xs531;
					
					hxMoItem.put("XF261",0);
					hxMoItem.put("XF262",0);
					hxMoItem.put("SF261",0);
					hxMoItem.put("SF262",0);
					hxMoItem.put("HX_QTY",0);
					
					hxMoItem.put("XS532",0);
					hxMoItem.put("SS531",0);
					hxMoItem.put("SS532",0);
					
					hxMoItem.put("EDITOR",mat.get("CREATOR").toString());
					hxMoItem.put("EDIT_DATE",mat.get("CREATE_DATE").toString());
				}else {
					hxMoItem.put("WERKS",mat.get("WERKS").toString());
					hxMoItem.put("AUFNR",mat.get("AUFNR").toString());
					hxMoItem.put("RSNUM",mat.get("RSNUM").toString());
					hxMoItem.put("RSPOS",mat.get("RSPOS").toString());
					
					hxMoItem.put("POSNR",mat.get("POSNR").toString());
					hxMoItem.put("MATNR",mat.get("MATNR").toString());
					hxMoItem.put("MAKTX",mat.get("MAKTX").toString());
					hxMoItem.put("BDMNG",mat.get("BDMNG"));
					hxMoItem.put("MEINS",mat.get("MEINS").toString());
					hxMoItem.put("RGEKZ",mat.get("RGEKZ"));
					hxMoItem.put("SOBKZ",mat.get("SOBKZ"));
					
					hxQtyBy = xs531;
					
					hxMoItem.put("XF261",0);
					hxMoItem.put("XF262",0);
					hxMoItem.put("SF261",0);
					hxMoItem.put("SF262",0);
					hxMoItem.put("HX_QTY",0);
					hxMoItem.put("XS532",0);
					hxMoItem.put("SS531",0);
					hxMoItem.put("SS532",0);
					hxMoItem.put("CANCEL_FLAG",0);
					hxMoItem.put("DEL",0);
					hxMoItem.put("CREATOR",mat.get("CREATOR").toString());
					hxMoItem.put("CREATE_DATE",mat.get("CREATE_DATE").toString());
				}
				
				
				hxMoItem.put("LGORT",mat.get("LGORT").toString());
				hxMoItem.put("XS531",xs531);
				hxMoItem.put("HX_QTY_BY",hxQtyBy);
				
				hxMoList.add(hxMoItem);
			}
			
			/**
			 * 保存副产品收货过账(V)核销信息
			 */
			wmsAccountReceiptHxMoDao.insertOrUpdateMoComponentHxInfo(hxMoList);
		}

		//List<WmsHxToEntity> hxToEntityList = new ArrayList<WmsHxToEntity>();
		
		//this.insertOrUpdateBatch(hxToEntityList);
		params.put("matList", matList);
		/**
		 * 产生WMS凭证记录 
		 * 
		 */
		String WMS_NO = commonService.saveWMSDoc(params,(List<Map<String,Object>>)params.get("matList"));
		
		/**
		 * SAP过账：检查配置 是否及时过账到SAP，如果配置及时过账，抛数据给SAP过账，如果过账失败，显示报错信息，操作终止
		 * 非及时过账，延迟过账到SAP，先产生和更新WMS相关的数据后，再抛数据给SAP过账
		 * SAP过账失败回滚
		 */
		
		String SAP_NO = commonService.doSapPost(params);
		
		StringBuilder msg=new StringBuilder();
		msg.append(" WMS凭证:");
		msg.append(WMS_NO);
		if(StringUtils.isNotBlank(SAP_NO)) {
			msg.append(" SAP凭证:");
			msg.append(SAP_NO);
		}
		return R.ok(msg.toString());
	}
	
	/**
	 * SAP生产订单核销业务（组件）发料（V）-核销业务
	 */
	@Override
	@Transactional(rollbackFor=Exception.class)
	public R postGI_MOV(Map<String, Object> params) {
		List<Map<String,Object>> postMatList = new ArrayList<Map<String,Object>>();//过账物料清单
		
		List<Map<String,Object>> matList=(List<Map<String,Object>>) params.get("matList"); //前台勾选的发料行项目
		List<Map<String,Object>> stockMatList = (List<Map<String,Object>>) params.get("stockMatList");
		//String OVERSTEP_HX_FLAG = params.get("OVERSTEP_HX_FLAG").toString();//是否可超虚拟库存
		String WERKS = params.get("WERKS").toString();//收货工厂
		String BUSINESS_NAME = params.get("BUSINESS_NAME").toString();
		String SAP_MOVE_TYPE="";//SAP移动类型，为空不进行SAP过账
		
		params.put("BUSINESS_TYPE", "11");//生产订单
		params.put("BUSINESS_CLASS", "09");	
		
		/**
		 * 获取WMS移动类型、移动原因、SAP移动类型、SAP过账标识
		 */
		Map<String,Object> cdmap=new HashMap<String,Object>();
		cdmap.put("BUSINESS_NAME", BUSINESS_NAME);//SAP生产订单发料(V)
		cdmap.put("BUSINESS_TYPE", "11");//生产订单
		cdmap.put("BUSINESS_CLASS", "09");
		cdmap.put("WERKS", WERKS);
		cdmap.put("SOBKZ", matList.get(0).get("SOBKZ")==null?"Z":matList.get(0).get("SOBKZ"));
		Map<String,Object> moveSyn = commonDao.getMoveAndSyn(cdmap);
		if(moveSyn == null|| moveSyn.get("WMS_MOVE_TYPE")==null) {
			/**
			 * 系统未配置SAP移动类型
			 */
			return R.error("系统未配置生产订单发料（V）的业务类型！");
		}
		SAP_MOVE_TYPE = moveSyn.get("SAP_MOVE_TYPE")==null?"":moveSyn.get("SAP_MOVE_TYPE").toString();
		//SAP移动类型JSON字符串拼装
		if(StringUtils.isBlank(SAP_MOVE_TYPE)) {
			return R.error("发货工厂"+WERKS+"未配置生产订单发料(V)业务类型的SAP过账移动类型！");
		}	
		params.put("SAP_MOVE_TYPE", SAP_MOVE_TYPE);
		params.put("WMS_MOVE_TYPE", moveSyn.get("WMS_MOVE_TYPE"));
		params.put("SYN_FLAG", moveSyn.get("SYN_FLAG"));
		/**
		 * 查询发料物料库存批次信息
		 */
		List<Map<String, Object>> matStockList = commonService.getMaterialStock(stockMatList);
		for (Map<String, Object> matMap : matList) {
			String OVERSTEP_HX_FLAG = matMap.get("OVERSTEP_HX_FLAG").toString();//是否可超虚拟库存
			String MATNR = matMap.get("MATNR").toString(); //需要发料的料号
			Double QTY = Double.valueOf(matMap.get("QTY")==null?"0":matMap.get("QTY").toString()); //发料数量
			String LGORT = matMap.get("LGORT").toString(); //发出库位
			String EDITOR = matMap.get("EDITOR").toString();
			String EDIT_DATE = matMap.get("EDIT_DATE").toString();
			matMap.put("XF261", QTY);
			matMap.put("SAP_MOVE_TYPE", SAP_MOVE_TYPE);
			matMap.put("BUSINESS_TYPE", "11");
			matMap.put("WMS_MOVE_TYPE", moveSyn.get("WMS_MOVE_TYPE"));			
			
			for (Map<String, Object> stockMap : matStockList) {				
				String MATNR_STOCK = stockMap.get("MATNR").toString(); //库存料号
				Double VIRTUAL_QTY = Double.valueOf(stockMap.get("VIRTUAL_QTY")==null?"0":stockMap.get("VIRTUAL_QTY").toString()); //库存数量
				String LGORT_STOCK = stockMap.get("LGORT").toString(); //库存料号存储库位
				matMap.put("LGORT", LGORT_STOCK);
				stockMap.putAll(matMap);
				/**
				 * 根据料号+发料数量，查询推荐的库存批次信息（先进先出规则）
				 */
				if(MATNR.equals(MATNR_STOCK) && (LGORT.indexOf(LGORT_STOCK)>=0||StringUtils.isBlank(LGORT))) {
					if(VIRTUAL_QTY>=QTY) {
						matMap.put("BATCH", stockMap.get("BATCH"));
						stockMap.put("QTY_WMS", QTY);
						stockMap.put("SAP_MOVE_TYPE", SAP_MOVE_TYPE);
						stockMap.put("QTY_SAP", QTY);
						stockMap.put("UNIT", stockMap.get("MEINS"));
						stockMap.put("MEINS", stockMap.get("MEINS"));
						stockMap.put("BIN_CODE", stockMap.get("BIN_CODE"));
						stockMap.put("LIFNR", stockMap.get("LIFNR"));
						stockMap.put("SOBKZ", stockMap.get("SOBKZ"));
						stockMap.put("EDITOR", EDITOR);
						stockMap.put("EDIT_DATE", EDIT_DATE);					
						stockMap.put("LGORT", LGORT_STOCK);
						stockMap.put("VIRTUAL_QTY", VIRTUAL_QTY-QTY);
						stockMap.put("VIRTUAL_QTY_CUT", VIRTUAL_QTY-QTY);
						QTY = QTY -VIRTUAL_QTY;
						postMatList.add(stockMap);
						break;
					}else {
						if(VIRTUAL_QTY<=0) {
							continue;
						}
						stockMap.put("BATCH", stockMap.get("BATCH"));
						stockMap.put("QTY_WMS", VIRTUAL_QTY);
						stockMap.put("QTY_SAP", VIRTUAL_QTY);
						stockMap.put("UNIT", stockMap.get("MEINS"));
						stockMap.put("MEINS", stockMap.get("MEINS"));
						stockMap.put("BIN_CODE", stockMap.get("BIN_CODE"));
						stockMap.put("LIFNR", stockMap.get("LIFNR"));
						stockMap.put("SOBKZ", stockMap.get("SOBKZ"));
						stockMap.put("EDITOR", EDITOR);
						stockMap.put("EDIT_DATE", EDIT_DATE);
						stockMap.put("VIRTUAL_QTY", "0");
						stockMap.put("VIRTUAL_QTY_CUT", VIRTUAL_QTY-QTY);
						stockMap.put("LGORT", LGORT_STOCK);
						postMatList.add(stockMap);
						
						QTY = QTY -VIRTUAL_QTY;
					}
					
				}
				
			}
			
			if(QTY>0) {
				if(OVERSTEP_HX_FLAG.equals("X")) {
					/**
					 * 虚拟库存不够，可超虚拟库存，使用非限制库存
					 */
					for (Map<String, Object> stockMap : matStockList) {
						String MATNR_STOCK = stockMap.get("MATNR").toString(); //库存料号
						Double STOCK_QTY = Double.valueOf(stockMap.get("STOCK_QTY")==null?"0":stockMap.get("STOCK_QTY").toString()); //库存数量
						String LGORT_STOCK = stockMap.get("LGORT").toString(); //库存料号存储库位
						matMap.put("LGORT", LGORT_STOCK);
						stockMap.putAll(matMap);
						/**
						 * 根据料号+发料数量，查询推荐的库存批次信息（先进先出规则）
						 */
						if(MATNR.equals(MATNR_STOCK) && (LGORT.indexOf(LGORT_STOCK)>=0||StringUtils.isBlank(LGORT))) {
							if(STOCK_QTY>=QTY) {
								stockMap.put("BATCH", stockMap.get("BATCH"));
								stockMap.put("QTY_WMS", QTY);
								stockMap.put("QTY_SAP", QTY);
								stockMap.put("UNIT", stockMap.get("MEINS"));
								stockMap.put("MEINS", stockMap.get("MEINS"));
								stockMap.put("BIN_CODE", stockMap.get("BIN_CODE"));
								stockMap.put("LIFNR", stockMap.get("LIFNR"));
								stockMap.put("SOBKZ", stockMap.get("SOBKZ"));
								stockMap.put("EDITOR", EDITOR);
								stockMap.put("EDIT_DATE", EDIT_DATE);
								stockMap.put("STOCK_QTY_CUT", STOCK_QTY-QTY);								
								stockMap.put("STOCK_QTY", STOCK_QTY-QTY);
								stockMap.put("LGORT", LGORT_STOCK);
								postMatList.add(stockMap);
								QTY = QTY -STOCK_QTY;
								break;
							}else {
								if(STOCK_QTY<=0) {
									continue;
								}
								stockMap.put("BATCH", stockMap.get("BATCH"));
								stockMap.put("QTY_WMS", STOCK_QTY);
								stockMap.put("QTY_SAP", STOCK_QTY);
								stockMap.put("UNIT", stockMap.get("MEINS"));
								stockMap.put("MEINS", stockMap.get("MEINS"));
								stockMap.put("BIN_CODE", stockMap.get("BIN_CODE"));
								stockMap.put("LIFNR", stockMap.get("LIFNR"));
								stockMap.put("SOBKZ", stockMap.get("SOBKZ"));
								stockMap.put("EDITOR", EDITOR);
								stockMap.put("EDIT_DATE", EDIT_DATE);
								stockMap.put("STOCK_QTY_CUT", STOCK_QTY-QTY);
								stockMap.put("STOCK_QTY", "0");
								stockMap.put("LGORT", LGORT_STOCK);
								QTY = QTY -STOCK_QTY;
								postMatList.add(stockMap);
							}
							
						}
					}
					if(QTY>0) {
						return R.error("物料："+MATNR+"库存（含非限制和V类）不足！");
					}
				}else {
					return R.error("物料："+MATNR+"库存（V）不足！");
				}
			}
		}
				
		/**
		 * 扣减WMS库存
		 */
		commonService.modifyWmsStock(postMatList);
		
		/**
		 * 保存生产订单发料账(V)核销信息
		 */
		wmsAccountReceiptHxMoDao.insertOrUpdateMoComponentHxInfo(matList);
		
		/**
		 * 更新SAP采购订单组件表投料数量
		 */
		wmsAccountReceiptHxMoDao.updateSapMoComponentTLInfo(matList);
		
		/**
		 * 产生WMS凭证记录 
		 * 
		 */
		String WMS_NO = commonService.saveWMSDoc(params,postMatList);
		
		/**
		 * SAP过账：检查配置 是否及时过账到SAP，如果配置及时过账，抛数据给SAP过账，如果过账失败，显示报错信息，操作终止
		 * 非及时过账，延迟过账到SAP，先产生和更新WMS相关的数据后，再抛数据给SAP过账
		 * SAP过账失败回滚
		 */
		params.put("matList", postMatList);
		String SAP_NO = commonService.doSapPost(params);
		
		StringBuilder msg=new StringBuilder();
		msg.append(" WMS凭证:");
		msg.append(WMS_NO);
		if(StringUtils.isNotBlank(SAP_NO)) {
			msg.append(" SAP凭证:");
			msg.append(SAP_NO);
		}
		return R.ok(msg.toString());
	}

	@Override
	public List<Map<String, Object>> getMatListByMATNR(Map<String, Object> params) {
		List<Map<String, Object>> matList=null;
		matList=wmsAccountReceiptHxMoDao.getMatListByMATNR(params);
		return matList;
	}

	@Override
	public R postGI_303V(Map<String, Object> params) {
		List<Map<String,Object>> postMatList = new ArrayList<Map<String,Object>>();//过账物料清单		
		List<Map<String,Object>> matList=(List<Map<String,Object>>) params.get("matList"); //前台勾选的发料行项目
		List<Map<String,Object>> stockMatList = (List<Map<String,Object>>) params.get("stockMatList");
		//String OVERSTEP_HX_FLAG = params.get("OVERSTEP_HX_FLAG").toString();//是否可超虚拟库存
		String WERKS = params.get("WERKS").toString();//发货工厂
		String BUSINESS_NAME = params.get("BUSINESS_NAME").toString();
		String SAP_MOVE_TYPE="";//SAP移动类型，为空不进行SAP过账
		
		params.put("BUSINESS_TYPE", "00");//303调拨虚发
		params.put("BUSINESS_CLASS", "09");
		
		
		/**
		 * 获取WMS移动类型、移动原因、SAP移动类型、SAP过账标识
		 */
		Map<String,Object> cdmap=new HashMap<String,Object>();
		cdmap.put("BUSINESS_NAME", BUSINESS_NAME);//SAP生产订单发料(V)
		cdmap.put("BUSINESS_TYPE", "00");//303调拨虚发
		cdmap.put("BUSINESS_CLASS", "09");
		cdmap.put("WERKS", WERKS);
		cdmap.put("SOBKZ", matList.get(0).get("SOBKZ")==null?"Z":matList.get(0).get("SOBKZ"));
		Map<String,Object> moveSyn = commonDao.getMoveAndSyn(cdmap);
		if(moveSyn == null || moveSyn.get("WMS_MOVE_TYPE")==null) {
			/**
			 * 系统未配置SAP移动类型
			 */
			return R.error("系统未配置生产订单发料（V）的业务类型！");
		}
		SAP_MOVE_TYPE = moveSyn.get("SAP_MOVE_TYPE")==null?"":moveSyn.get("SAP_MOVE_TYPE").toString();
		//SAP移动类型JSON字符串拼装
		if(StringUtils.isBlank(SAP_MOVE_TYPE)) {
			return R.error("发货工厂"+WERKS+"未配置工厂间调拨发料(V303)业务类型的SAP过账移动类型！");
		}	
		params.put("SAP_MOVE_TYPE", SAP_MOVE_TYPE);
		params.put("WMS_MOVE_TYPE", moveSyn.get("WMS_MOVE_TYPE"));
		params.put("SYN_FLAG", moveSyn.get("SYN_FLAG"));
		/**
		 * 查询发料物料库存批次信息
		 */
		List<Map<String, Object>> matStockList = commonService.getMaterialStock(stockMatList);
		for (Map<String, Object> matMap : matList) {
			String OVERSTEP_HX_FLAG = matMap.get("OVERSTEP_HX_FLAG").toString();//是否可超虚拟库存
			String MATNR = matMap.get("MATNR").toString(); //需要发料的料号
			Double QTY = Double.valueOf(matMap.get("QTY")==null?"0":matMap.get("QTY").toString()); //发料数量
			String F_LGORT = matMap.get("F_LGORT").toString(); //发出库位
			String EDITOR = matMap.get("EDITOR").toString();
			String EDIT_DATE = matMap.get("EDIT_DATE").toString();

			matMap.put("SAP_MOVE_TYPE", SAP_MOVE_TYPE);
			matMap.put("BUSINESS_TYPE", "00");//303调拨虚发
			matMap.put("WMS_MOVE_TYPE", moveSyn.get("WMS_MOVE_TYPE"));			
			
			for (Map<String, Object> stockMap : matStockList) {				
				String MATNR_STOCK = stockMap.get("MATNR").toString(); //库存料号
				Double VIRTUAL_QTY = Double.valueOf(stockMap.get("VIRTUAL_QTY")==null?"0":stockMap.get("VIRTUAL_QTY").toString()); //库存数量
				String LGORT_STOCK = stockMap.get("LGORT").toString(); //库存料号存储库位
				matMap.put("F_LGORT", LGORT_STOCK);
				stockMap.putAll(matMap);
				/**
				 * 根据料号+发料数量，查询推荐的库存批次信息（先进先出规则）
				 */
				if(MATNR.equals(MATNR_STOCK) && (F_LGORT.indexOf(LGORT_STOCK)>=0||StringUtils.isBlank(F_LGORT))) {
					if(VIRTUAL_QTY>=QTY) {
						matMap.put("BATCH", stockMap.get("BATCH"));
						stockMap.put("F_BATCH", stockMap.get("BATCH"));
						stockMap.put("BIN_CODE", stockMap.get("BIN_CODE"));
						stockMap.put("LIFNR", stockMap.get("LIFNR"));
						stockMap.put("QTY_WMS", QTY);
						stockMap.put("SAP_MOVE_TYPE", SAP_MOVE_TYPE);
						stockMap.put("QTY_SAP", QTY);
						stockMap.put("UNIT", stockMap.get("MEINS"));
						stockMap.put("MEINS", stockMap.get("MEINS"));
						stockMap.put("SOBKZ", stockMap.get("SOBKZ"));
						stockMap.put("EDITOR", EDITOR);
						stockMap.put("EDIT_DATE", EDIT_DATE);					
						stockMap.put("F_LGORT", LGORT_STOCK);
						stockMap.put("VIRTUAL_QTY", VIRTUAL_QTY-QTY);
						stockMap.put("VIRTUAL_QTY_CUT", VIRTUAL_QTY-QTY);
						QTY = QTY -VIRTUAL_QTY;
						postMatList.add(stockMap);
						break;
					}else {
						if(VIRTUAL_QTY<=0) {
							continue;
						}
						stockMap.put("BATCH", stockMap.get("BATCH"));
						stockMap.put("F_BATCH", stockMap.get("BATCH"));
						stockMap.put("QTY_WMS", VIRTUAL_QTY);
						stockMap.put("QTY_SAP", VIRTUAL_QTY);
						stockMap.put("UNIT", stockMap.get("MEINS"));
						stockMap.put("MEINS", stockMap.get("MEINS"));
						stockMap.put("SOBKZ", stockMap.get("SOBKZ"));
						stockMap.put("EDITOR", EDITOR);
						stockMap.put("EDIT_DATE", EDIT_DATE);
						stockMap.put("VIRTUAL_QTY", "0");
						stockMap.put("VIRTUAL_QTY_CUT", VIRTUAL_QTY-QTY);
						stockMap.put("F_LGORT", LGORT_STOCK);
						postMatList.add(stockMap);
						
						QTY = QTY -VIRTUAL_QTY;
					}					
				}				
			}
			
			if(QTY>0) {
				if(OVERSTEP_HX_FLAG.equals("X")) {
					/**
					 * 虚拟库存不够，可超虚拟库存，使用非限制库存
					 */
					for (Map<String, Object> stockMap : matStockList) {
						String MATNR_STOCK = stockMap.get("MATNR").toString(); //库存料号
						Double STOCK_QTY = Double.valueOf(stockMap.get("STOCK_QTY")==null?"0":stockMap.get("STOCK_QTY").toString()); //库存数量
						String LGORT_STOCK = stockMap.get("LGORT").toString(); //库存料号存储库位
						matMap.put("F_LGORT", LGORT_STOCK);
						stockMap.putAll(matMap);
						/**
						 * 根据料号+发料数量，查询推荐的库存批次信息（先进先出规则）
						 */
						if(MATNR.equals(MATNR_STOCK) && (F_LGORT.indexOf(LGORT_STOCK)>=0||StringUtils.isBlank(F_LGORT))) {
							if(STOCK_QTY>=QTY) {
								stockMap.put("BATCH", stockMap.get("BATCH"));
								stockMap.put("F_BATCH", stockMap.get("BATCH"));
								stockMap.put("QTY_WMS", QTY);
								stockMap.put("QTY_SAP", QTY);
								stockMap.put("UNIT", stockMap.get("MEINS"));
								stockMap.put("MEINS", stockMap.get("MEINS"));
								stockMap.put("SOBKZ", stockMap.get("SOBKZ"));
								stockMap.put("EDITOR", EDITOR);
								stockMap.put("EDIT_DATE", EDIT_DATE);
								stockMap.put("STOCK_QTY_CUT", STOCK_QTY-QTY);								
								stockMap.put("STOCK_QTY", STOCK_QTY-QTY);
								stockMap.put("F_LGORT", LGORT_STOCK);
								postMatList.add(stockMap);
								QTY = QTY -STOCK_QTY;
								break;
							}else {
								if(STOCK_QTY<=0) {
									continue;
								}
								stockMap.put("BATCH", stockMap.get("BATCH"));
								stockMap.put("F_BATCH", stockMap.get("BATCH"));
								stockMap.put("QTY_WMS", STOCK_QTY);
								stockMap.put("QTY_SAP", STOCK_QTY);
								stockMap.put("UNIT", stockMap.get("MEINS"));
								stockMap.put("MEINS", stockMap.get("MEINS"));
								stockMap.put("SOBKZ", stockMap.get("SOBKZ"));
								stockMap.put("EDITOR", EDITOR);
								stockMap.put("EDIT_DATE", EDIT_DATE);
								stockMap.put("STOCK_QTY_CUT", STOCK_QTY-QTY);
								stockMap.put("STOCK_QTY", "0");
								stockMap.put("F_LGORT", LGORT_STOCK);
								QTY = QTY -STOCK_QTY;
								postMatList.add(stockMap);
							}							
						}
					}
					if(QTY>0) {
						return R.error("物料："+MATNR+"库存（含非限制和V类）不足！");
					}
				}else {
					return R.error("物料："+MATNR+"库存（V）不足！");
				}
			}
		}
				
		/**
		 * 产生WMS凭证记录 
		 * 
		 */
		String WMS_NO = commonService.saveWMSDoc(params,postMatList);
		
		/**
		 * 更新调拨核销表- WMS_HX_TO，移动类型对应的数量XF303及剩余核销数量HX_QTY_XF
		 */
		wmsAccountReceiptHxMoDao.saveHXTO(postMatList);
		
		/**
		 *WMS_CORE_STOCK表中WERKS即为数据中的F_WERKS发货工厂，WH_NUMBER即为数据中的F_WH_NUMBER 发货库位
		 *LGORT 为数据库中的F_LGORT
		 */
		postMatList.forEach(m->{
			m.put("WERKS", m.get("F_WERKS"));
			m.put("WH_NUMBER", m.get("F_WH_NUMBER"));
			m.put("LGORT", m.get("F_LGORT"));
		});
		/**
		 * 扣减WMS库存
		 */
		commonService.modifyWmsStock(postMatList);
		
		/**
		 * SAP过账：检查配置 是否及时过账到SAP，如果配置及时过账，抛数据给SAP过账，如果过账失败，显示报错信息，操作终止
		 * 非及时过账，延迟过账到SAP，先产生和更新WMS相关的数据后，再抛数据给SAP过账
		 * SAP过账失败回滚
		 */
		params.put("matList", postMatList);
		String SAP_NO = commonService.doSapPost(params);
	
		StringBuilder msg=new StringBuilder();
		msg.append(" WMS凭证:");
		msg.append(WMS_NO);
		if(StringUtils.isNotBlank(SAP_NO)) {
			msg.append(" SAP凭证:");
			msg.append(SAP_NO);
		}
		return R.ok(msg.toString());
		
		
	}

	@Override
	public int getRequirementBySapOutNo(String OUT_NO) {
		return wmsAccountReceiptHxMoDao.getRequirementBySapOutNo(OUT_NO);
	}

	
}
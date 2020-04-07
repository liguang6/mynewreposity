package com.byd.wms.business.modules.in.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
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
import com.byd.utils.DateUtils;
import com.byd.utils.R;
import com.byd.utils.RedisUtils;
import com.byd.wms.business.modules.common.dao.CommonDao;
import com.byd.wms.business.modules.common.service.CommonService;
import com.byd.wms.business.modules.common.service.WarehouseTasksService;
import com.byd.wms.business.modules.common.service.WmsCDocNoService;
import com.byd.wms.business.modules.common.service.WmsCTxtService;
import com.byd.wms.business.modules.config.dao.WmsCoreWhBinDao;
import com.byd.wms.business.modules.config.entity.WmsCMatStorageEntity;
import com.byd.wms.business.modules.config.entity.WmsCWhEntity;
import com.byd.wms.business.modules.config.entity.WmsCoreWhBinEntity;
import com.byd.wms.business.modules.config.entity.WmsSapMaterialEntity;
import com.byd.wms.business.modules.config.entity.WmsSapPlantLgortEntity;
import com.byd.wms.business.modules.config.service.WmsCMatStorageService;
import com.byd.wms.business.modules.config.service.WmsCWhService;
import com.byd.wms.business.modules.config.service.WmsCoreWhBinService;
import com.byd.wms.business.modules.config.service.WmsSapMaterialService;
import com.byd.wms.business.modules.config.service.WmsSapPlantLgortService;
import com.byd.wms.business.modules.in.dao.WmsInInboundDao;
import com.byd.wms.business.modules.in.dao.WmsInReceiptDao;
import com.byd.wms.business.modules.in.service.WmsInHandoverService;
import com.byd.wms.business.modules.in.service.WmsInInboundService;
import com.byd.wms.business.modules.in.service.WmsInReceiptService;

/** 
 * @author 作者 E-mail: 
 * @version 创建时间：2018年9月29日 上午9:35:14 
 * 类说明 
 */
@Service
public class WmsInHandoverServiceImpl implements WmsInHandoverService {
	@Autowired
	private WmsInInboundDao wmsInInboundDao;
	@Autowired
	private WmsCTxtService wmsCtxService;
	@Autowired
	private WmsSapPlantLgortService wmssapplantlgortService;
	@Autowired
    private WmsSapMaterialService wmsSapMaterialService;
	@Autowired
	private CommonDao commonDao;
	@Autowired
	private CommonService commonService;
	@Autowired
	private WmsCMatStorageService matstorageservice;
	@Autowired
	private WmsCoreWhBinService coreWhbinService;
	@Autowired
	private WarehouseTasksService warehouseTasksService;
	@Autowired
	private WmsCoreWhBinDao wmsCoreWhBinDao;
	@Autowired
	WmsCWhService wmsCWhService;
	@Autowired
    WmsInInboundService wmsInInboundService;
	@Autowired
    WmsCDocNoService wmsCDocNoService;
	@Autowired
	private WmsInReceiptDao wmsInReceiptDao;
	@Autowired
    private RedisUtils redisUtils;
	
	@Override
	public List<Map<String, Object>> getInBoundHead(Map<String, Object> map) {
		List<Map<String, Object>> inboundheadlist=wmsInInboundDao.getInBoundHead(map);
		return inboundheadlist;
	}

	@Override
	public List<Map<String, Object>> getInBoundItem(Map<String, Object> map) throws Exception{
		List<Map<String, Object>> inboundNOList = new ArrayList<Map<String, Object>>();
		
		List<Map<String, Object>> poNOList = new ArrayList<Map<String, Object>>();
		
		List<Map<String, Object>> inbounditemlist=wmsInInboundDao.getInBoundItem(map);
		if(inbounditemlist!=null&&inbounditemlist.size()>0){
			for(int i=0;i<inbounditemlist.size();i++){
				if(inbounditemlist.get(i).get("BUSINESS_NAME")!=null){
					String business_name=inbounditemlist.get(i).get("BUSINESS_NAME").toString();
					if("10".equals(business_name)){//收货进仓单
						
						//{INBOUND_NO}   {JZ_DATE}收{F_WERKS}{F_WH_NUMBER}发出物料/{WERKS}{FULL_NAME}
						Map<String, String> params=new HashMap<String, String>();
						params.put("BUSINESS_NAME", "10");
						
						if(inbounditemlist.get(i).get("INBOUND_NO")!=null){
							params.put("INBOUND_NO", inbounditemlist.get(i).get("INBOUND_NO").toString());
						}
							params.put("JZ_DATE", DateUtils.format(new Date(),DateUtils.DATE_PATTERN));
							if(inbounditemlist.get(i).get("WERKS")!=null&&inbounditemlist.get(i).get("LGORT")!=null){
								Map<String, Object> wmssapplantlgorttemp=new HashMap<String, Object>();
								wmssapplantlgorttemp.put("werks", inbounditemlist.get(i).get("WERKS"));
								wmssapplantlgorttemp.put("lgort", inbounditemlist.get(i).get("LGORT"));
								List<WmsSapPlantLgortEntity> wmsSapPlantLgortList=wmssapplantlgortService.selectByMap(wmssapplantlgorttemp);
								if(wmsSapPlantLgortList.size()>0){
									if(wmsSapPlantLgortList.get(0).getLgortName()!=null){
									params.put("LGORT_NAME", wmsSapPlantLgortList.get(0).getLgortName());
									}
								}
								
							}
							
							if(inbounditemlist.get(i).get("WERKS")!=null){
								params.put("WERKS", inbounditemlist.get(i).get("WERKS").toString());
							}
							params.put("FULL_NAME", map.get("USERNAME").toString());
							
							
							Map<String, Object> retrule=wmsCtxService.getRuleTxt(params);
							if(!retrule.isEmpty()){
								if("success".equals(retrule.get("msg"))){
									String txtrule=retrule.get("txtrule").toString();//头文本
									String txtruleitem=retrule.get("txtruleitem").toString();//行文本
									
									inbounditemlist.get(i).put("ITEM_TEXT", txtruleitem);
									
									inbounditemlist.get(i).put("TXTRULE", txtrule);
								}else{
									throw new RuntimeException((String) retrule.get("msg"));
								}
							}
					
					}
					
					if("21".equals(business_name)){//工厂间调拨进仓305
						//{INBOUND_NO}   {JZ_DATE}收{F_WERKS}{F_WH_NUMBER}发出物料/{WERKS}{FULL_NAME}
						Map<String, String> params=new HashMap<String, String>();
						params.put("BUSINESS_NAME", "21");
						
						if(inbounditemlist.get(i).get("INBOUND_NO")!=null){
							params.put("INBOUND_NO", inbounditemlist.get(i).get("INBOUND_NO").toString());
						}
							params.put("JZ_DATE", DateUtils.format(new Date(),DateUtils.DATE_PATTERN));
							if(inbounditemlist.get(i).get("F_WERKS")!=null){
								params.put("F_WERKS", inbounditemlist.get(i).get("F_WERKS").toString());
							}
							if(inbounditemlist.get(i).get("F_WH_NUMBER")!=null){
								params.put("F_WH_NUMBER", inbounditemlist.get(i).get("F_WH_NUMBER").toString());
							}
							if(inbounditemlist.get(i).get("WERKS")!=null){
								params.put("WERKS", inbounditemlist.get(i).get("WERKS").toString());
							}
							params.put("FULL_NAME", map.get("FULL_NAME").toString());
							
							Map<String, Object> retrule=wmsCtxService.getRuleTxt(params);
							if(!retrule.isEmpty()){
								if("success".equals(retrule.get("msg"))){
									String txtrule=retrule.get("txtrule").toString();//头文本
									String txtruleitem=retrule.get("txtruleitem").toString();//行文本
									
									inbounditemlist.get(i).put("ITEM_TEXT", txtruleitem);
									
									inbounditemlist.get(i).put("TXTRULE", txtrule);
								}else{
									throw new RuntimeException((String) retrule.get("msg"));
								}
							}
					}else{
						Map<String, String> params=new HashMap<String, String>();
						params.put("BUSINESS_NAME", inbounditemlist.get(i).get("BUSINESS_NAME").toString());
						
						if(inbounditemlist.get(i).get("INBOUND_NO")!=null){
							params.put("INBOUND_NO", inbounditemlist.get(i).get("INBOUND_NO").toString());
						}
							params.put("JZ_DATE", DateUtils.format(new Date(),DateUtils.DATE_PATTERN));
							if(inbounditemlist.get(i).get("WERKS")!=null){
								params.put("WERKS", inbounditemlist.get(i).get("WERKS").toString());
							}
							if(inbounditemlist.get(i).get("WH_NUMBER")!=null){
								params.put("WH_NUMBER", inbounditemlist.get(i).get("WH_NUMBER").toString());
							}
							
							params.put("FULL_NAME", map.get("FULL_NAME").toString());
							
							Map<String, Object> retrule=wmsCtxService.getRuleTxt(params);
							if(!retrule.isEmpty()){
								if("success".equals(retrule.get("msg"))){
									String txtrule=retrule.get("txtrule").toString();//头文本
									String txtruleitem=retrule.get("txtruleitem").toString();//行文本
									
									inbounditemlist.get(i).put("ITEM_TEXT", txtruleitem);
									
									inbounditemlist.get(i).put("TXTRULE", txtrule);
								}else{
									throw new RuntimeException((String) retrule.get("msg"));
								}
							}
					}
					//设置  从收货单表查询出来的  破坏数量  破坏过账数量   IQC成本中心
					if("10".equals(business_name)){//收货进仓单
						Map<String, Object> receiptMap=new HashMap<String, Object>();
						receiptMap.put("RECEIPT_NO", inbounditemlist.get(i).get("RECEIPT_NO"));
						receiptMap.put("RECEIPT_ITEM_NO", inbounditemlist.get(i).get("RECEIPT_ITEM_NO"));
						List<Map<String, Object>> retReceiptList=wmsInInboundService.getReceiptInfoByReceiveNo(receiptMap);
						if(retReceiptList!=null&&retReceiptList.size()>0){
							inbounditemlist.get(i).put("DESTROY_QTY", retReceiptList.get(0).get("DESTROY_QTY"));
							inbounditemlist.get(i).put("DESTROY_GZ_QTY", retReceiptList.get(0).get("DESTROY_GZ_QTY"));
							inbounditemlist.get(i).put("IQC_COST_CENTER", retReceiptList.get(0).get("IQC_COST_CENTER"));
						}
					}else{
						inbounditemlist.get(i).put("DESTROY_QTY", "");
						inbounditemlist.get(i).put("DESTROY_GZ_QTY", "");
						inbounditemlist.get(i).put("IQC_COST_CENTER", "");
					}
					//getPOITEM
					String business_type=inbounditemlist.get(i).get("BUSINESS_TYPE").toString();
					
					if("67".equals(business_name)&&"02".equals(business_type)){
						String po_no_str=inbounditemlist.get(i).get("PO_NO").toString();
						String po_item_no_str=inbounditemlist.get(i).get("PO_ITEM_NO").toString();
						//查询出 WMS_SAP_PO_ITEM中的UMREN UMREZ
						Map<String, Object> poMap=new HashMap<String, Object>();
						poMap.put("PO_NO", po_no_str);
						poMap.put("PO_ITEM_NO", po_item_no_str);
						poNOList.add(poMap);
						
					}
				}
				
				//为查询委外清单做准备
				Map<String, Object> inboundNoMap = new HashMap <String, Object>();
				inboundNoMap.put("INBOUND_NO", inbounditemlist.get(i).get("INBOUND_NO"));
				inboundNoMap.put("INBOUND_ITEM_NO", inbounditemlist.get(i).get("INBOUND_ITEM_NO"));
				inboundNOList.add(inboundNoMap);
			}
		}
		
		List<Map<String, Object>> inPoCptCounsumeListRet=new ArrayList<Map<String, Object>>();
		if(inboundNOList.size()>0){
			inPoCptCounsumeListRet=wmsInInboundDao.getInpoCptConsume(inboundNOList);
		}
		
		
		if(inPoCptCounsumeListRet!=null&&inPoCptCounsumeListRet.size()>0){
			for(Map<String, Object> cptCounsumeMap:inPoCptCounsumeListRet){
				for(Map<String, Object> inbounditemMap:inbounditemlist){
					if(cptCounsumeMap.get("INBOUND_NO").equals(inbounditemMap.get("INBOUND_NO"))&&
							cptCounsumeMap.get("INBOUND_ITEM_NO").equals(inbounditemMap.get("INBOUND_ITEM_NO"))
							){
						inbounditemMap.put("PSTYP", "3");//存在委外清单
					}
				}
			}
		}
		
		//
		if(poNOList!=null&&poNOList.size()>0){
			List<Map<String, Object>> poitemlistRet=wmsInInboundDao.getPOITEMByList(poNOList);
			if(poitemlistRet!=null&&poitemlistRet.size()>0){
				for(Map<String, Object> poitemMap:poitemlistRet){
					for(Map<String, Object> inboundItemMap:inbounditemlist){
						//比较po_no和po_item_no相等的
						if(poitemMap.get("EBELN").equals(inboundItemMap.get("PO_NO"))&&
								poitemMap.get("EBELP").equals(inboundItemMap.get("PO_ITEM_NO"))){
							inboundItemMap.put("UMREN", poitemMap.get("UMREN"));
							inboundItemMap.put("UMREZ", poitemMap.get("UMREZ"));
						}
					}
				}
			}
		}
		
		
		return inbounditemlist;
	}

	/**
	 * 1 扣减收料房库存
	 * 2 收货单数据更新
	 * 3 累加进仓单的已进仓数量，再更新进仓单状态
	 * 4 更新核销表信息
	 * 5 库存增加
	 * 6 生成凭证信息
	 * 7 判断是否过账sap
	 * 8 过账sap
	 * 9 是否启用立库
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public Map<String, Object> handover(Map<String, Object> params)  {
		List<List<Map<String,Object>>> sapList=new ArrayList<List<Map<String,Object>>>();//sap过账准备的
		List<List<Map<String,Object>>> wmsList=new ArrayList<List<Map<String,Object>>>();//wms保存事务记录准备的
		
		List<Map<String,Object>> matList=new ArrayList<Map<String,Object>>();//库存增加准备的
		
		List<Map<String,Object>> matList_kouj=new ArrayList<Map<String,Object>>();//用于有破坏数量的，扣减库存数量
		
		String SAP_MOVE_TYPE="";
		//为sap过账准备集合
		Map<String,Object> moveSyn=new HashMap<String,Object>();
		Map<String, Object> sapgzMap=new HashMap<String,Object>();
		
		JSONArray jarr = JSON.parseArray(params.get("ARRLIST").toString());
		for(int i=0;i<jarr.size();i++){
			JSONObject itemData=  jarr.getJSONObject(i);
			
			String inbound_type="";//进仓单类型    00 外购进仓单 01 自制进仓单
			List<Map<String,Object>> wmsinboundheadlist=new ArrayList<Map<String,Object>>();
		if(itemData.getString("LABEL_FALG")==null||"".equals(itemData.getString("LABEL_FALG"))){//如果不是标签交接的数据
			Map<String, Object> inboundHeadMap = new HashMap <String, Object>();
			inboundHeadMap.put("INBOUND_NO", itemData.getString("INBOUND_NO"));
			wmsinboundheadlist=wmsInInboundDao.getInBoundHead(inboundHeadMap);
			
			if(wmsinboundheadlist!=null&&wmsinboundheadlist.size()>0){
				if(wmsinboundheadlist.get(0).get("INBOUND_TYPE")!=null){
					inbound_type=wmsinboundheadlist.get(0).get("INBOUND_TYPE").toString();
				}
			}
		}
		
		//如果存在收货单的破坏数量，则按照以下规则重新设置MAY_IN_QTY；
		BigDecimal destroy_qty_d=BigDecimal.ZERO;//破坏数量
		BigDecimal destroy_gz_qty_d=BigDecimal.ZERO;//破坏过账数量
		BigDecimal destroy_qty_sub=BigDecimal.ZERO;
		
		if(!"".equals(itemData.getString("DESTROY_QTY"))){
			
			destroy_qty_d=new BigDecimal(itemData.getString("DESTROY_QTY").toString());
			if(!"".equals(itemData.getString("DESTROY_GZ_QTY"))){
				destroy_gz_qty_d=new BigDecimal(itemData.getString("DESTROY_GZ_QTY").toString());
			}
			
			destroy_qty_sub=destroy_qty_d.subtract(destroy_gz_qty_d);//破坏数量 与 破坏过账数量的差值
			BigDecimal may_in_qty_d=new BigDecimal(itemData.getString("MAY_IN_QTY").toString());
			if(may_in_qty_d.compareTo(destroy_qty_sub)>0){
				itemData.put("DESTROY_GZ_QTY", destroy_qty_sub);//做201的过账数量
			}
			//增加收货单破坏投料数量
			Map<String, Object> receiptDestroyMap = new HashMap <String, Object>();
			receiptDestroyMap.put("RECEIPT_NO", itemData.getString("RECEIPT_NO"));
			receiptDestroyMap.put("RECEIPT_ITEM_NO", itemData.getString("RECEIPT_ITEM_NO"));
			receiptDestroyMap.put("DESTROY_GZ_QTY", destroy_qty_sub);
			wmsInInboundDao.updateReceiptDestroyQty(receiptDestroyMap);
		}
			
			//1 更新收料房[WMS_IN_RH_STOCK]库存数量（收料房库存数量减少，当收料房库存更新为0时，删除该行数据）
			if("00".equals(inbound_type)||"RECEIPT".equals(itemData.getString("LABEL_FALG"))){
				Map<String, Object> rhParamsMap = new HashMap <String, Object>();
				rhParamsMap.put("MAY_IN_QTY", itemData.getString("MAY_IN_QTY"));
				rhParamsMap.put("WERKS", itemData.getString("WERKS"));
				rhParamsMap.put("WH_NUMBER", itemData.getString("WH_NUMBER"));
				rhParamsMap.put("MATNR", itemData.getString("MATNR"));
				rhParamsMap.put("BATCH", itemData.getString("BATCH"));
				rhParamsMap.put("LGORT", itemData.getString("LGORT"));
				rhParamsMap.put("EDITOR", params.get("USERNAME").toString());
				rhParamsMap.put("EDIT_DATE", DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
				wmsInInboundDao.updateRHStock(rhParamsMap);
			}
			//2 收货单数据更新
			//首先查询出行表中 收货单号 行项目号 头表中的质检状态
			if("00".equals(inbound_type)||"RECEIPT".equals(itemData.getString("LABEL_FALG"))){
				String qc_result="";
				if(wmsinboundheadlist!=null&&wmsinboundheadlist.size()>0){
					if(wmsinboundheadlist.get(0).get("QC_RESULT")!=null){
						qc_result=wmsinboundheadlist.get(0).get("QC_RESULT").toString();
					}
				}
				if("RECEIPT".equals(itemData.getString("LABEL_FALG"))){
					qc_result="01";
				}
				//再更新收货单数据   进仓单质检状况：选合格 业务的进仓单对应的收货单扣减可进仓数量 增加进仓数量，进仓单质检状况：选不合格 业务的进仓单对应收货单扣减可退货数量 增加进仓数量，试装储物的物料还需要更新 试装进仓数量
				Map<String, Object> receiptMap = new HashMap <String, Object>();
				receiptMap.put("RECEIPT_NO", itemData.getString("RECEIPT_NO"));
				receiptMap.put("RECEIPT_ITEM_NO", itemData.getString("RECEIPT_ITEM_NO"));
				receiptMap.put("MAY_IN_QTY", itemData.getString("MAY_IN_QTY"));
				//判断是否是试装储位
				Map<String, Object> whbinMap = new HashMap <String, Object>();
				whbinMap.put("WH_NUMBER", itemData.getString("WH_NUMBER"));
				whbinMap.put("BIN_CODE", itemData.getString("BIN_CODE"));
				whbinMap.put("DEL", "0");
				List<WmsCoreWhBinEntity> whBinList=coreWhbinService.selectByMap(whbinMap);
				if(whBinList!=null&&whBinList.size()>0){//存在试装储位
					if("04".equals(whBinList.get(0).getBinType())){//试装储位类型
						receiptMap.put("BIN_CODE", "SZ");
					}
				}else{
					receiptMap.put("BIN_CODE", itemData.getString("BIN_CODE"));
				}
				receiptMap.put("QC_RESULT", qc_result);
				wmsInInboundDao.updateReceipt(receiptMap);
			}
			// 3 累加进仓单的已进仓数量，再更新进仓单状态
			Map<String, Object> inbounditemqtyMap = new HashMap <String, Object>();
			inbounditemqtyMap.put("INBOUND_NO", itemData.getString("INBOUND_NO"));
			inbounditemqtyMap.put("INBOUND_ITEM_NO", itemData.getString("INBOUND_ITEM_NO"));
			inbounditemqtyMap.put("MAY_IN_QTY", itemData.getString("MAY_IN_QTY"));
			wmsInInboundDao.updateInboundItemRealQty(inbounditemqtyMap);//累加进仓单的已进仓数量
			
			wmsInInboundDao.updateInboundItemStatus(inbounditemqtyMap);//更新进仓单行表状态
			
			//更新进仓单头表状态 详细见 方法 updateHeadStatus 避免同一个事务里面修改同一个字段，查询不到。
			
			
			
			// 为库存增加 准备数据
				//费用性采购订单 不需要增加库存 
				//通过INBOUND_NO INBOUND_ITEM_NO查询wms_in_inbound_item得出receipt_no,receipt_item_no
			String knttp_str="";
			
			String po_no_str="";
			String po_item_no_str="";
			Map<String, Object> inbounditemreceiptMap = new HashMap <String, Object>();
			inbounditemreceiptMap.put("INBOUND_NO", itemData.getString("INBOUND_NO"));
			inbounditemreceiptMap.put("INBOUND_ITEM_NO", itemData.getString("INBOUND_ITEM_NO"));
			List<Map<String, Object>> inbounditemlist=wmsInInboundDao.getItemReceipt(inbounditemreceiptMap);
			if((inbounditemlist!=null&&inbounditemlist.size()>0)||"RECEIPT".equals(itemData.getString("LABEL_FALG"))){//收货产生的标签 交接通过收货单来判断 费用性采购订单
				String receipt_str="";
				String receipt_item_str="";
				
				if("RECEIPT".equals(itemData.getString("LABEL_FALG"))){
					receipt_str=itemData.getString("RECEIPT_NO");
					receipt_item_str=itemData.getString("RECEIPT_ITEM_NO");
				}else{
				if(inbounditemlist.get(0).get("RECEIPT_NO")!=null){
					receipt_str=inbounditemlist.get(0).get("RECEIPT_NO").toString();
				}
				if(inbounditemlist.get(0).get("RECEIPT_ITEM_NO")!=null){
					receipt_item_str=inbounditemlist.get(0).get("RECEIPT_ITEM_NO").toString();
				}
				}
				if(!"".equals(receipt_str)){
					Map<String, Object> inboundreceiptMap = new HashMap <String, Object>();
					inboundreceiptMap.put("RECEIPT_NO", receipt_str);
					inboundreceiptMap.put("RECEIPT_ITEM_NO", receipt_item_str);
					List<Map<String, Object>> receiptPolist=wmsInInboundDao.getReceiptByReceiptNo(inboundreceiptMap);
					if(receiptPolist!=null&&receiptPolist.size()>0){
						
						if(receiptPolist.get(0)!=null&&receiptPolist.get(0).get("PO_NO")!=null){
							po_no_str=receiptPolist.get(0).get("PO_NO").toString();
						}
						if(receiptPolist.get(0)!=null&&receiptPolist.get(0).get("PO_ITEM_NO")!=null){
							po_item_no_str=receiptPolist.get(0).get("PO_ITEM_NO").toString();
						}
						if(!"".equals(po_no_str)){
							Map<String, Object> poitemMap = new HashMap <String, Object>();
							poitemMap.put("PO_NO", po_no_str);
							poitemMap.put("PO_ITEM_NO", po_item_no_str);
							List<Map<String, Object>> poitemlist=wmsInInboundDao.getPOITEM(poitemMap);
							if(poitemlist!=null&&poitemlist.size()>0){
								if(poitemlist.get(0).get("KNTTP")!=null){
									knttp_str=poitemlist.get(0).get("KNTTP").toString();
								}
							}
						}
					}
				}
			}
			//不是以下这几个类型的才增加库存
			if((!"F".equals(knttp_str))||(!"K".equals(knttp_str))||(!"P".equals(knttp_str))){
				//增加库存操作数据准备
				Map<String, Object> matMap = new HashMap <String, Object>();
				
				String meins="";
				Map<String, Object> sapMaterialMap = new HashMap <String, Object>();
				sapMaterialMap.put("WERKS", itemData.getString("WERKS"));
				sapMaterialMap.put("MATNR", itemData.getString("MATNR"));
				List<WmsSapMaterialEntity> sapMateriallist=wmsSapMaterialService.selectByMap(sapMaterialMap);
				if(sapMateriallist.size()>0){
					meins=sapMateriallist.get(0).getMeins();
					matMap.put("MEINS", meins);
				}else{
					//抛出异常 工厂*** 料号*** 物料信息没有同步，请使用物料数据同步功能，同步信息！
					throw new RuntimeException("工厂 "+itemData.getString("WERKS")+" 料号"+itemData.getString("MATNR")+" 物料信息没有同步，请使用物料数据同步功能，同步信息！");
				}
				
				matMap.put("MATNR", itemData.getString("MATNR"));
				matMap.put("UNIT", itemData.getString("UNIT"));
				matMap.put("STOCK_QTY", itemData.getString("MAY_IN_QTY"));
				
				matMap.put("WERKS", itemData.getString("WERKS"));
				matMap.put("WH_NUMBER", itemData.getString("WH_NUMBER"));
				matMap.put("LGORT", itemData.getString("LGORT"));
				matMap.put("MATNR", itemData.getString("MATNR"));
				matMap.put("MAKTX", itemData.getString("MAKTX"));
				matMap.put("F_BATCH", itemData.getString("F_BATCH"));
				matMap.put("BATCH", itemData.getString("BATCH"));
				matMap.put("BIN_CODE", (itemData.getString("BIN_CODE_SHELF")==null||"".equals(itemData.getString("BIN_CODE_SHELF")))?itemData.getString("BIN_CODE"):itemData.getString("BIN_CODE_SHELF"));
				matMap.put("BIN_NAME", itemData.getString("BIN_NAME"));
				matMap.put("SOBKZ", itemData.getString("SOBKZ"));
				matMap.put("LIFNR", itemData.getString("LIFNR"));
				matMap.put("LIKTX", itemData.getString("LIKTX"));
				matMap.put("EDITOR", params.get("USERNAME").toString());
				matMap.put("EDIT_DATE", DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
				matMap.put("LABEL_NO", itemData.get("LABEL_NO").toString());
				matList.add(matMap);
				
				if(destroy_qty_d.compareTo(BigDecimal.ZERO)>0){//存在破坏数量的
					matMap.put("DESTROY_QTY_SUB", destroy_qty_sub);
					matList_kouj.add(matMap);
				}
			}
			//4 更新核销表信息
			//a BUSINESS_NAME=07和BUSINESS_TYPE=03 更新WMS_HX_PO表的SS105
			if(!"".equals(po_no_str)&&!"".equals(po_item_no_str)&&"07".equals(itemData.getString("BUSINESS_NAME"))&&"03".equals(itemData.getString("BUSINESS_TYPE"))){
				Map<String,Object> hxmap=new HashMap<String,Object>();//
				hxmap.put("MAY_IN_QTY", itemData.getString("MAY_IN_QTY"));
				hxmap.put("EBELN", po_no_str);
				hxmap.put("EBELP", po_item_no_str);
				hxmap.put("WERKS", itemData.getString("WERKS"));
				hxmap.put("WH_NUMBER", itemData.getString("WH_NUMBER"));
				wmsInInboundDao.updateHXPO(hxmap);
			}
			
			//7 判断是否过账sap
			Map<String,Object> cdmap=new HashMap<String,Object>();
			cdmap.put("BUSINESS_NAME", itemData.getString("BUSINESS_NAME"));//
			cdmap.put("BUSINESS_TYPE", itemData.getString("BUSINESS_TYPE"));//
			
			
			//查询BUSINESS_CLASS
			Map<String,Object> bussinessmap=new HashMap<String,Object>();
			bussinessmap.put("BUSINESS_NAME", itemData.getString("BUSINESS_NAME"));
			bussinessmap.put("BUSINESS_TYPE", itemData.getString("BUSINESS_TYPE"));
			bussinessmap.put("SOBKZ", itemData.getString("SOBKZ"));
			List<Map<String,Object>> retBusinessList=wmsInInboundDao.getWmsBusinessClass(bussinessmap);
			if(retBusinessList!=null&&retBusinessList.size()>0){
				cdmap.put("BUSINESS_CLASS", retBusinessList.get(0).get("BUSINESS_CLASS"));
			}
			
			
			cdmap.put("WERKS", itemData.getString("WERKS"));
			cdmap.put("SOBKZ", itemData.getString("SOBKZ"));
			cdmap.put("LIFNR", itemData.getString("LIFNR")==null?"":itemData.getString("LIFNR"));
			moveSyn=commonDao.getMoveAndSyn(cdmap);
			if(moveSyn==null || moveSyn.get("WMS_MOVE_TYPE")==null) {
				throw new RuntimeException(itemData.getString("WERKS")+"工厂未配置业务类型！");
			}
			//应该要判断moveSyn 是否存在，不存在抛出异常
			SAP_MOVE_TYPE = moveSyn.get("SAP_MOVE_TYPE")==null?"":moveSyn.get("SAP_MOVE_TYPE").toString();
			String WMS_MOVE_TYPE=moveSyn.get("WMS_MOVE_TYPE")==null?"":moveSyn.get("WMS_MOVE_TYPE").toString();;
			
			// 为添加凭证记录行项目 准备数据
			Map<String, Object> docitemMap = new HashMap <String, Object>();
			docitemMap.put("BUSINESS_NAME", itemData.getString("BUSINESS_NAME"));
			docitemMap.put("BUSINESS_TYPE", itemData.getString("BUSINESS_TYPE"));
			docitemMap.put("WMS_MOVE_TYPE", WMS_MOVE_TYPE);
			docitemMap.put("SAP_MOVE_TYPE", SAP_MOVE_TYPE);
			docitemMap.put("SYN_FLAG", moveSyn.get("SYN_FLAG"));
			docitemMap.put("SOBKZ", itemData.getString("SOBKZ"));
			docitemMap.put("MATNR", itemData.getString("MATNR"));
			docitemMap.put("MAKTX", itemData.getString("MAKTX"));
			docitemMap.put("WERKS", itemData.getString("WERKS"));
			docitemMap.put("WH_NUMBER", itemData.getString("WH_NUMBER"));
			if("10".equals(itemData.getString("BUSINESS_NAME"))&&"06".equals(itemData.getString("BUSINESS_TYPE"))){
				docitemMap.put("LGORT", itemData.getString("LGORT_311"));
				docitemMap.put("F_LGORT", itemData.getString("F_LGORT"));
				docitemMap.put("F_WERKS", itemData.getString("F_WERKS"));
				docitemMap.put("F_WH_NUMBER", itemData.getString("F_WH_NUMBER"));
				docitemMap.put("MOVE_STLOC", itemData.getString("LGORT")==null?"":itemData.getString("LGORT"));
			}else{
				docitemMap.put("LGORT", itemData.getString("LGORT"));
			}
			
			docitemMap.put("BIN_CODE", (itemData.getString("BIN_CODE_SHELF")==null||"".equals(itemData.getString("BIN_CODE_SHELF")))?itemData.getString("BIN_CODE"):itemData.getString("BIN_CODE_SHELF"));
			docitemMap.put("UNIT", itemData.getString("UNIT"));
			docitemMap.put("QTY_WMS", itemData.getString("MAY_IN_QTY")==null?"":itemData.getString("MAY_IN_QTY"));
			docitemMap.put("QTY_SAP", itemData.getString("MAY_IN_QTY")==null?"":itemData.getString("MAY_IN_QTY"));
			docitemMap.put("BATCH", itemData.getString("BATCH"));
			docitemMap.put("BATCH_SAP", itemData.getString("BATCH"));
			docitemMap.put("HANDOVER", params.get("HANDOVER").toString());
			docitemMap.put("RECEIPT_NO", itemData.getString("RECEIPT_NO")==null?"":itemData.getString("RECEIPT_NO"));
			docitemMap.put("RECEIPT_ITEM_NO", itemData.getString("RECEIPT_ITEM_NO")==null?"":itemData.getString("RECEIPT_ITEM_NO"));
			docitemMap.put("PO_NO", po_no_str);//po_no_str
			docitemMap.put("PO_ITEM_NO", po_item_no_str);//po_item_no_str
			docitemMap.put("LIFNR", itemData.getString("LIFNR")==null?null:itemData.getString("LIFNR"));
			docitemMap.put("MO_NO", itemData.getString("MO_NO")==null?null:itemData.getString("MO_NO"));
			docitemMap.put("MO_ITEM_NO", itemData.getString("MO_ITEM_NO")==null?null:itemData.getString("MO_ITEM_NO"));
			docitemMap.put("IO_NO", itemData.getString("IO_NO")==null?null:itemData.getString("IO_NO"));
			docitemMap.put("COST_CENTER", itemData.getString("COST_CENTER")==null?null:itemData.getString("COST_CENTER"));
			docitemMap.put("WBS", itemData.getString("WBS")==null?null:itemData.getString("WBS"));
			docitemMap.put("SAKTO", itemData.getString("SAKTO")==null?null:itemData.getString("SAKTO"));
			docitemMap.put("ANLN1", itemData.getString("ANLN1")==null?null:itemData.getString("ANLN1"));
			docitemMap.put("INBOUND_NO", itemData.getString("INBOUND_NO"));
			docitemMap.put("INBOUND_ITEM_NO", itemData.getString("INBOUND_ITEM_NO"));
			docitemMap.put("SAP_MATDOC_NO", itemData.getString("SAP_MATDOC_NO")==null?"":itemData.getString("SAP_MATDOC_NO"));
			docitemMap.put("SAP_MATDOC_ITEM_NO", itemData.getString("SAP_MATDOC_ITEM_NO")==null?"":itemData.getString("SAP_MATDOC_ITEM_NO"));
			docitemMap.put("CREATOR", params.get("USERNAME").toString());
			docitemMap.put("CREATE_DATE", DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));//
			docitemMap.put("ITEM_TEXT", itemData.getString("ITEM_TEXT")==null?"":itemData.getString("ITEM_TEXT"));
			docitemMap.put("HEADER_TXT", itemData.getString("TXTRULE")==null?"":itemData.getString("TXTRULE"));
			
			docitemMap.put("REF_SAP_MATDOC_YEAR", itemData.getString("REF_SAP_MATDOC_YEAR")==null?"":itemData.getString("REF_SAP_MATDOC_YEAR"));
			docitemMap.put("REF_SAP_MATDOC_NO", itemData.getString("REF_SAP_MATDOC_NO")==null?"":itemData.getString("REF_SAP_MATDOC_NO"));
			docitemMap.put("REF_SAP_MATDOC_ITEM_NO", itemData.getString("REF_SAP_MATDOC_ITEM_NO")==null?"":itemData.getString("REF_SAP_MATDOC_ITEM_NO"));
			
			docitemMap.put("BUSINESS_CLASS", cdmap.get("BUSINESS_CLASS"));
			docitemMap.put("LABEL_NO", itemData.getString("LABEL_NO")==null?"":itemData.getString("LABEL_NO"));
			
			docitemMap.put("REF_SAP_MATDOC_NO", itemData.getString("REF_SAP_MATDOC_NO")==null?"":itemData.getString("REF_SAP_MATDOC_NO"));
			docitemMap.put("REF_SAP_MATDOC_ITEM_NO", itemData.getString("REF_SAP_MATDOC_ITEM_NO")==null?"":itemData.getString("REF_SAP_MATDOC_ITEM_NO"));
			docitemMap.put("REF_SAP_MATDOC_YEAR", itemData.getString("REF_SAP_MATDOC_YEAR")==null?"":itemData.getString("REF_SAP_MATDOC_YEAR"));
			
			if(destroy_qty_sub.compareTo(BigDecimal.ZERO)>0){//有质检破坏数量,方便后面取出，单独过账
				docitemMap.put("COST_CENTER", itemData.getString("IQC_COST_CENTER"));
				docitemMap.put("DESTORY_QTY", destroy_qty_sub);
				docitemMap.put("DESTROY_FLAG", "true");
			}else{
				docitemMap.put("DESTROY_FLAG", "false");
				docitemMap.put("DESTORY_QTY", "");
			}
			
			
			
			if(StringUtils.isNoneBlank(SAP_MOVE_TYPE)) {
				String[] strs=SAP_MOVE_TYPE.split("#");
				SAP_MOVE_TYPE="[";
				for(String s:strs) {
					SAP_MOVE_TYPE+="{\"moveType\":\""+s+"\",\"sapDoc\":\""+"\"}"+",";
				}
				SAP_MOVE_TYPE=SAP_MOVE_TYPE.substring(0, SAP_MOVE_TYPE.length()-1);
				SAP_MOVE_TYPE+="]";
				//
				if(sapList!=null&&sapList.size()>0){//sap过账结果集中有数据
					for(int z=0;z<sapList.size();z++){
						List<Map<String, Object>> tempsaplist=sapList.get(z);//从已经添加的list中取出来
						
						//判断当前cdmap.get("BUSINESS_CLASS")的BUSINESS_CLASS是否和sapList结果集中的一样，是的话就从添加到已有的sapList中
							if(((cdmap.get("BUSINESS_CLASS").toString()).equals(tempsaplist.get(0).get("BUSINESS_CLASS")))
									&&((cdmap.get("BUSINESS_NAME").toString()).equals(tempsaplist.get(0).get("BUSINESS_NAME")))
									&&((cdmap.get("BUSINESS_TYPE").toString()).equals(tempsaplist.get(0).get("BUSINESS_TYPE")))
									&&((cdmap.get("SOBKZ").toString()).equals(tempsaplist.get(0).get("SOBKZ")))
									&&((cdmap.get("LIFNR").toString()).equals(tempsaplist.get(0).get("LIFNR")))){
								
								List<Map<String, Object>> matlistmap=(List<Map<String, Object>>) sapList.get(z).get(0).get("matList");
								matlistmap.add(docitemMap);
								sapList.get(z).get(0).put("matList", matlistmap);
								
								break;
							}else{//不是的就新增一个list保存
								
								List<Map<String, Object>> listsaptempMap1 = new ArrayList<Map<String, Object>>();
								
								List<Map<String, Object>> matlisttemp=new ArrayList<Map<String, Object>>();
								Map<String, Object> saptempmap=new HashMap<String, Object>();
								saptempmap.put("BUSINESS_CLASS", cdmap.get("BUSINESS_CLASS"));
								saptempmap.put("BUSINESS_NAME", cdmap.get("BUSINESS_NAME"));
								saptempmap.put("BUSINESS_TYPE", cdmap.get("BUSINESS_TYPE"));
								saptempmap.put("WERKS", cdmap.get("WERKS"));
								saptempmap.put("SOBKZ", itemData.getString("SOBKZ"));
								saptempmap.put("LIFNR", itemData.getString("LIFNR"));
								
								saptempmap.put("PZ_DATE", params.get("PZDDT"));
								saptempmap.put("JZ_DATE", params.get("JZDDT"));
								matlisttemp.add(docitemMap);
								saptempmap.put("matList", matlisttemp);
								
								if(saptempmap!=null&&saptempmap.size()>0){
								listsaptempMap1.add(saptempmap);
								sapList.add(listsaptempMap1);
								
								}
								break;
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
					
					saptempmap.put("PZ_DATE", params.get("PZDDT"));
					saptempmap.put("JZ_DATE", params.get("JZDDT"));
					matlisttemp.add(docitemMap);
					saptempmap.put("matList", matlisttemp);
					listsaptempMap1.add(saptempmap);
					sapList.add(listsaptempMap1);
					
				}
				
				//
				
				
			}else {//不需要sap 过账
				
			}
			
			//准备wms事务记录插入数据
			if(wmsList!=null&&wmsList.size()>0){
				//wmsList结果集中有数据
				for(int z=0;z<wmsList.size();z++){
					List<Map<String, Object>> tempwmslist=wmsList.get(z);//从已经添加的list中取出来
					
					//判断当前cdmap.get("BUSINESS_CLASS")的BUSINESS_CLASS等参数是否和sapList结果集中的一样，是的话就从添加到已有的sapList中
						if(((cdmap.get("BUSINESS_CLASS").toString()).equals(tempwmslist.get(0).get("BUSINESS_CLASS")))
								&&((cdmap.get("BUSINESS_NAME").toString()).equals(tempwmslist.get(0).get("BUSINESS_NAME")))
								&&((cdmap.get("BUSINESS_TYPE").toString()).equals(tempwmslist.get(0).get("BUSINESS_TYPE")))
								&&((cdmap.get("SOBKZ").toString()).equals(tempwmslist.get(0).get("SOBKZ")))
								&&((cdmap.get("LIFNR").toString()).equals(tempwmslist.get(0).get("LIFNR")))){
							//把当前的取出来，放到已存在的结果集中去
							tempwmslist.add(docitemMap);
							
							
							break;
						}else{//不是的就新增一个list保存
							
							List<Map<String, Object>> matlisttemp=new ArrayList<Map<String, Object>>();
							
							matlisttemp.add(docitemMap);
							wmsList.add(matlisttemp);//wms事务记录准备
							
							break;
						}
					
				}
			
			}else{//
				List<Map<String, Object>> matlisttemp=new ArrayList<Map<String, Object>>();
				
				matlisttemp.add(docitemMap);
				wmsList.add(matlisttemp);//wms事务记录准备
			}
			
			//标签进仓交接的，要更新标签数量和状态
			wmsInInboundDao.updateLabelQtyAndStatus(itemData);
			//
			
			//判断AUTO_PUTAWAY_FLAG =0，则产生上架任务     对应条码标签状态改成 07已进仓 ，否则不产生上架任务，对应条码标签状态改成 08已上架
			
			String labelstr=itemData.get("LABEL_NO").toString();
			String[] labelLst1 = labelstr.split(",");//取出label_no
			
			if("0".equals(itemData.getString("AUTO_PUTAWAY_FLAG"))){
				this.saveWhTask(itemData);
				
				if(labelLst1!=null&&labelLst1.length>0){
					for(int m=0;m<labelLst1.length;m++){
						Map<String, Object> labelMapTemp=new HashMap<String, Object>();
						labelMapTemp.put("LABEL_NO", labelLst1[m].trim());
						labelMapTemp.put("LABEL_STATUS", "07");
						wmsInInboundDao.updateLabelStatusByLabelNo(labelMapTemp);
					}
				}
			}else{
				if(labelLst1!=null&&labelLst1.length>0){
					for(int m=0;m<labelLst1.length;m++){
						Map<String, Object> labelMapTemp=new HashMap<String, Object>();
						labelMapTemp.put("LABEL_NO", labelLst1[m].trim());
						labelMapTemp.put("LABEL_STATUS", "08");
						wmsInInboundDao.updateLabelStatusByLabelNo(labelMapTemp);
					}
				}
			}
			
		}
		
		//判断工厂是否启用条码,启用了条码的需要需要插入wms_core_stock_label，把进仓单中的条码信息拆分，一个条码一行插入该表中，同时wms_core_stock的bin_code置位空
		Map<String, Object> tempMap=new HashMap<String, Object>();
    	tempMap.put("WERKS", params.get("WERKS"));
    	tempMap.put("WH_NUMBER", params.get("WH_NUMBER"));
    	List<WmsCWhEntity> retCWh=wmsCWhService.selectByMap(tempMap);
    	
    	if(retCWh!=null&&retCWh.size()>0){//存在则 启用了条码
    		List<Map<String, Object>> wmsCoreStockLabelList=new ArrayList<Map<String, Object>>();
    		for(int m=0;m<matList.size();m++){
    			
    			String Label_no_str=matList.get(m).get("LABEL_NO").toString();
    			//List<String> labelList = JSON.parseArray(Label_no_str, String.class);//取出label_no
    			String[] labelList = Label_no_str.split(",");//取出label_no
    			for(int n=0;n<labelList.length;n++){
    				String label_no=labelList[n];
    				Map<String, Object> wmsStockLabelMap=new HashMap<String, Object>();
    				wmsStockLabelMap.put("WERKS", matList.get(m).get("WERKS"));
    				wmsStockLabelMap.put("WH_NUMBER", matList.get(m).get("WH_NUMBER"));
    				wmsStockLabelMap.put("MATNR", matList.get(m).get("MATNR"));
    				wmsStockLabelMap.put("BATCH", matList.get(m).get("BATCH"));
    				//wmsStockLabelMap.put("LABEL_NO", JSON.toJSONString(label_no));
    				wmsStockLabelMap.put("LABEL_NO", label_no.trim());
    				wmsStockLabelMap.put("BIN_CODE", matList.get(m).get("BIN_CODE"));
    				wmsStockLabelMap.put("BIN_NAME", matList.get(m).get("BIN_NAME"));
    				wmsStockLabelMap.put("EDITOR", matList.get(m).get("EDITOR"));
    				wmsStockLabelMap.put("EDIT_DATE", matList.get(m).get("EDIT_DATE"));
    				wmsCoreStockLabelList.add(wmsStockLabelMap);
    			}
    			
    			//matList.get(m).put("BIN_CODE", "");//将储位置位空，方便后续插入wms_core_stock
    		}
    		wmsInInboundDao.insertWmsCoreStockLabel(wmsCoreStockLabelList);
    	}
    	
		
		
		// 5库存增加
		if(matList.size()>0){
			Map<String, Object> wmsstockParams=new HashMap<String, Object>();
			wmsstockParams.put("STOCK_TYPE", "STOCK_QTY");//增加正常库存
			wmsstockParams.put("matList", matList);
			commonService.saveWmsStock(wmsstockParams);
		}
		
		//存在质检破坏数量的，扣减对应的库存数量
		if(matList_kouj.size()>0){
			for(Map<String, Object> koujMap:matList_kouj){
				BigDecimal destroy_qty_sub=BigDecimal.ZERO;
				if(koujMap.get("DESTROY_QTY_SUB")!=null&&!"".equals(koujMap.get("DESTROY_QTY_SUB"))){
					//将扣减的数量变成负数
					destroy_qty_sub=BigDecimal.ZERO.subtract(new BigDecimal(koujMap.get("DESTROY_QTY_SUB").toString()));
				}
				koujMap.put("STOCK_QTY", destroy_qty_sub);
			}
			
			Map<String, Object> wmsstockParams_=new HashMap<String, Object>();
			wmsstockParams_.put("STOCK_TYPE", "STOCK_QTY");//扣减正常库存
			wmsstockParams_.put("matList", matList_kouj);
			commonService.saveWmsStock(wmsstockParams_);
		}
		
		
		
		//6 生成凭证信息  
		Map<String,Object> head=new HashMap<String,Object>();
		head.put("WERKS", jarr.getJSONObject(0).getString("WERKS"));
		head.put("PZ_DATE", params.get("PZDDT"));
		head.put("JZ_DATE", params.get("JZDDT"));
		head.put("PZ_YEAR", params.get("PZDDT").toString().substring(0,4));
		head.put("HEADER_TXT", jarr.getJSONObject(0).getString("TXTRULE"));
		head.put("TYPE",  "00");//标准凭证
		head.put("CREATOR", params.get("USERNAME").toString());
		head.put("CREATE_DATE", DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));//
		head.put("SAP_MOVE_TYPE",  SAP_MOVE_TYPE);
		String WMS_NO="";
		if(wmsList!=null&&wmsList.size()>0){
			for(int p=0;p<wmsList.size();p++){
				String WMS_NO_temp=commonService.saveWMSDoc(head, wmsList.get(p));
				WMS_NO=WMS_NO+" "+WMS_NO_temp;
			}
		}
		
		//8 过账sap
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
		
		//201出库过账 并保存凭证记录 开始
				//判断是否质检破坏数量 的标志，找出来 产生WMS成本中心投料凭证（201）
				List<List<Map<String,Object>>> sapList_201=new ArrayList<List<Map<String,Object>>>();//成本中心投料凭证（201）sap过账准备的
				for(int k=0;k<sapList.size();k++){
					List<Map<String,Object>> sapmaplist_201=new ArrayList<Map<String,Object>>();
					List<Map<String,Object>> sapmaplist=sapList.get(k);
					for(int h=0;h<sapmaplist.size();h++){
						//重新赋值
						Map<String,Object> sapMap_201=sapmaplist.get(h);
						sapMap_201.put("BUSINESS_CLASS", "07");
						sapMap_201.put("BUSINESS_NAME", "44");
						sapMap_201.put("BUSINESS_TYPE", "14");
						
						List<Map<String,Object>> matList_=(List<Map<String, Object>>) sapmaplist.get(h).get("matList");
						
						List<Map<String,Object>> matList_201=new ArrayList<Map<String,Object>>();
						for(Map<String, Object> matMap:matList_){
							if("true".equals(matMap.get("DESTROY_FLAG").toString())){
								//重新赋值
								matMap.put("COST_CENTER", matMap.get("COST_CENTER"));
								matMap.put("QTY_WMS", matMap.get("DESTORY_QTY"));
								matMap.put("QTY_SAP", matMap.get("DESTORY_QTY"));
								
								matMap.put("BUSINESS_CLASS", "07");
								matMap.put("BUSINESS_NAME", "44");
								matMap.put("BUSINESS_TYPE", "14");
								
								matMap.put("WMS_MOVE_TYPE", "201");
								matMap.put("SAP_MOVE_TYPE", "201");
								
								matMap.put("PO_NO", null);
								matMap.put("PO_ITEM_NO", null);
								
								
								matList_201.add(matMap);
							}
						}
						
						sapMap_201.put("matList", matList_201);
						
						sapmaplist_201.add(sapMap_201);
					}
					sapList_201.add(sapmaplist_201);
					
				}
				String SAP_NO_201="";
				for(int d=0;d<sapList_201.size();d++){
					List<Map<String,Object>> sapmaplist_201=sapList_201.get(d);
					for(int e=0;e<sapmaplist_201.size();e++){
						List<Map<String,Object>> matList_201=(List<Map<String,Object>>) sapmaplist_201.get(e).get("matList");
						if(matList_201!=null&&matList_201.size()>0){
							String SAP_NO_temp=commonService.doSapPost(sapmaplist_201.get(e));
							SAP_NO_201=SAP_NO_201+" "+SAP_NO_temp;
							SAP_NO=SAP_NO+SAP_NO_201;
							
						}
						
					}
				}
				
				//事务记录处理 用于201
				List<List<Map<String,Object>>> wmsList_201=new ArrayList<List<Map<String,Object>>>();;
				for(int f=0;f<wmsList.size();f++){
					List<Map<String, Object>> matlisttemp_201=new ArrayList<Map<String, Object>>();
					
					List<Map<String, Object>> matlisttemp_=wmsList.get(f);
					for(Map<String, Object> matwmsMap:matlisttemp_){
						if("true".equals(matwmsMap.get("DESTROY_FLAG").toString())){
							//重新赋值
							matwmsMap.put("COST_CENTER", matwmsMap.get("COST_CENTER"));
							matwmsMap.put("QTY_WMS", matwmsMap.get("DESTORY_QTY"));
							matwmsMap.put("QTY_SAP", matwmsMap.get("DESTORY_QTY"));
							
							matwmsMap.put("BUSINESS_CLASS", "07");
							matwmsMap.put("BUSINESS_NAME", "44");
							matwmsMap.put("BUSINESS_TYPE", "14");
							
							matwmsMap.put("WMS_MOVE_TYPE", "201");
							matwmsMap.put("SAP_MOVE_TYPE", "201");
							
							matwmsMap.put("PO_NO", null);
							matwmsMap.put("PO_ITEM_NO", null);
							matlisttemp_201.add(matwmsMap);
						}
					}
					wmsList_201.add(matlisttemp_201);
				}
				
				Map<String,Object> head_201=new HashMap<String,Object>();
				head_201.put("WERKS", jarr.getJSONObject(0).getString("WERKS"));
				head_201.put("PZ_DATE", params.get("PZDDT"));
				head_201.put("JZ_DATE", params.get("JZDDT"));
				head_201.put("PZ_YEAR", params.get("PZDDT").toString().substring(0,4));
				head_201.put("HEADER_TXT", jarr.getJSONObject(0).getString("TXTRULE"));
				head_201.put("TYPE",  "00");//标准凭证
				head_201.put("CREATOR", params.get("USERNAME").toString());
				head_201.put("CREATE_DATE", DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));//
				head_201.put("SAP_MOVE_TYPE",  "201");
				String WMS_NO_201="";
				if(wmsList_201!=null&&wmsList_201.size()>0){
					for(int p=0;p<wmsList_201.size();p++){
						List<Map<String, Object>> wmslistret_201=wmsList_201.get(p);
						if(wmslistret_201!=null&&wmslistret_201.size()>0){
							String WMS_NO_temp=commonService.saveWMSDoc(head_201, wmsList_201.get(p));
							WMS_NO=WMS_NO+" "+WMS_NO_temp;
						}
						
					}
				}
				//201出库过账 并保存凭证记录 结束
		
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
	 * 更新头表状态，单独事务处理
	 */
	@Override
	public Map<String, Object> updateHeadStatus(Map<String, Object> params)  {
		JSONArray jarr = JSON.parseArray(params.get("ARRLIST").toString());
		
		for(int i=0;i<jarr.size();i++){
			JSONObject itemData=  jarr.getJSONObject(i);
			Map<String, Object> inbounditemstatusMap = new HashMap <String, Object>();
			inbounditemstatusMap.put("INBOUND_NO", itemData.getString("INBOUND_NO"));
			inbounditemstatusMap.put("DEL", "0");
			
			List<Map<String, Object>>  retitemstatus=wmsInInboundDao.getItemLs(inbounditemstatusMap);
			List<String> statusList=new ArrayList<String>();
			if(retitemstatus.size()>0){
				for(int m=0;m<retitemstatus.size();m++){
					if(retitemstatus.get(m).get("ITEM_STATUS")!=null){
						statusList.add(retitemstatus.get(m).get("ITEM_STATUS").toString());
					}
				}
			}
			if(statusList.size()>0){
				//如果行表状态都是02，则更新头表状态为02
				boolean status_02=true;
				boolean status_00=true;
				for(String sts:statusList){
					if(!"02".equals(sts)){
						status_02=false;
					}
				}
				for(String sts:statusList){
					if(!"00".equals(sts)){
						status_00=false;
					}
				}
				
				Map<String, Object> inboundheadtatusMap = new HashMap <String, Object>();
				inboundheadtatusMap.put("INBOUND_NO", itemData.getString("INBOUND_NO"));
				if(status_02){//全部为02，已完成
					inboundheadtatusMap.put("INBOUND_STATUS", "02");
				}else if(status_02==false&&status_00==false){//不全部为02 也不全部为00，则为部分进仓
					inboundheadtatusMap.put("INBOUND_STATUS", "01");
				}
				wmsInInboundDao.updateInboundHeadStatus(inboundheadtatusMap);
			}
		}
		return null;
	}

	@Override
	public List<Map<String, Object>> getLabelList(Map<String, Object> map) {
		
		boolean receipt_flag=false;//收货产生的标签，外购进仓单标签
		boolean inbound_flag=false;//创建进仓单产生的标签，自制进仓单标签
		List<Map<String, Object>> retLabelList=wmsInInboundDao.getLabelList(map);
		if(retLabelList!=null&&retLabelList.size()>0){
			retLabelList.get(0).put("MAY_IN_QTY", retLabelList.get(0).get("IN_QTY"));
			String inboundBinCode="";
			String inboundWhManager="";
			
			String MO_NO="";
			String MO_ITEM_NO="";
			
			String SO_NO="";
			String SO_ITEM_NO="";
			//判断是收货产生的标签，还是创建进仓单产生的进仓单
			if(retLabelList.get(0).get("RECEIPT_NO")!=null&&retLabelList.get(0).get("RECEIPT_ITEM_NO")!=null){
				receipt_flag=true;
				retLabelList.get(0).put("LABEL_FALG", "RECEIPT");
			}else if(retLabelList.get(0).get("INBOUND_NO")!=null&&retLabelList.get(0).get("INBOUND_ITEM_NO")!=null){
				inbound_flag=true;
				retLabelList.get(0).put("LABEL_FALG", "INBOUND");
			}
			//检查扫描的外购标签是否已经创建了进仓单
			if(receipt_flag){
				//通过label_no查询进仓单表
				Map<String, Object> inboundItemMapLabel=new HashMap<String, Object>();
				inboundItemMapLabel.put("LABEL_NO", retLabelList.get(0).get("LABEL_NO").toString());
				List<Map<String,Object>> inbounditemretlist=wmsInInboundDao.getItemLsByLabelNo(inboundItemMapLabel);
				if(inbounditemretlist!=null&&inbounditemretlist.size()>0){
					//该标签号存在
					throw new RuntimeException("标签号"+retLabelList.get(0).get("LABEL_NO").toString()+"在进仓单表中已经存在，不能再次进仓！");
				}
			}
			
			//生成行文本
			String Business_name_str="";
			String Business_type_str="";
			if(receipt_flag){
				List<Map<String, Object>> retBusinessNameReceiveList=wmsInInboundDao.getBusinessNameByReceiveNo(retLabelList.get(0));
				if(retBusinessNameReceiveList!=null&&retBusinessNameReceiveList.size()>0){
					Business_type_str=retBusinessNameReceiveList.get(0).get("BUSINESS_TYPE")==null?"":retBusinessNameReceiveList.get(0).get("BUSINESS_TYPE").toString();
				}
				Business_name_str="10";
			}else if(inbound_flag){
				List<Map<String, Object>> retBusinessNameInboundList=wmsInInboundDao.getBusinessNameByInboundNo(retLabelList.get(0));
				if(retBusinessNameInboundList!=null&&retBusinessNameInboundList.size()>0){
					Business_name_str=retBusinessNameInboundList.get(0).get("BUSINESS_NAME")==null?"":retBusinessNameInboundList.get(0).get("BUSINESS_NAME").toString();
					Business_type_str=retBusinessNameInboundList.get(0).get("BUSINESS_TYPE")==null?"":retBusinessNameInboundList.get(0).get("BUSINESS_TYPE").toString();
					inboundBinCode=retBusinessNameInboundList.get(0).get("BIN_CODE")==null?"":retBusinessNameInboundList.get(0).get("BIN_CODE").toString();
					inboundWhManager=retBusinessNameInboundList.get(0).get("WH_MANAGER")==null?"":retBusinessNameInboundList.get(0).get("WH_MANAGER").toString();
					MO_NO=retBusinessNameInboundList.get(0).get("MO_NO")==null?"":retBusinessNameInboundList.get(0).get("MO_NO").toString();
					MO_ITEM_NO=retBusinessNameInboundList.get(0).get("MO_ITEM_NO")==null?"":retBusinessNameInboundList.get(0).get("MO_ITEM_NO").toString();
					SO_NO=retBusinessNameInboundList.get(0).get("SO_NO")==null?"":retBusinessNameInboundList.get(0).get("SO_NO").toString();
					SO_ITEM_NO=retBusinessNameInboundList.get(0).get("SO_ITEM_NO")==null?"":retBusinessNameInboundList.get(0).get("SO_ITEM_NO").toString();
				}
			}
			//判断是否是303，Z23产生的标签
			List<Map<String, Object>> labeltemplist=new ArrayList<Map<String, Object>>();
			Map<String, Object> labeltempMap=new HashMap<String, Object>();
			labeltempMap.put("LABEL_NO", retLabelList.get(0).get("LABEL_NO"));
			labeltemplist.add(labeltempMap);
			List<Map<String, Object>> labelretlist=wmsInInboundDao.getLabelInfoBy303Z23(labeltemplist);
			if(labelretlist!=null&&labelretlist.size()>0){
				String wms_move_type_str=labelretlist.get(0).get("WMS_MOVE_TYPE")==null?"":labelretlist.get(0).get("WMS_MOVE_TYPE").toString();
				if("303".equals(wms_move_type_str)){
					Business_name_str="21";
					Business_type_str="08";
				}
				if("Z23".equals(wms_move_type_str)){
					Business_name_str="79";
					Business_type_str="08";
				}
			}
			//
			Map<String, String> txtMap=new HashMap<String, String>();
			txtMap.put("BUSINESS_NAME", Business_name_str);
			txtMap.put("WERKS", map.get("WERKS")==null?"":map.get("WERKS").toString());
			txtMap.put("JZ_DATE", map.get("JZ_DATE")==null?"":map.get("JZ_DATE").toString());
			txtMap.put("FULL_NAME", map.get("FULL_NAME")==null?"":map.get("FULL_NAME").toString());
			Map<String, Object> retrule=wmsCtxService.getRuleTxt(txtMap);
			
			if(!retrule.isEmpty()){
				if("success".equals(retrule.get("msg"))){
					String txtrule=retrule.get("txtrule").toString();//头文本
					String txtruleitem=retrule.get("txtruleitem").toString();//行文本
					retLabelList.get(0).put("TXTRULE", txtrule);
					retLabelList.get(0).put("ITEM_TEXT", txtruleitem);
				}else{
					throw new RuntimeException((String) retrule.get("msg"));
				}
			}
			//生成行文本 结束
			retLabelList.get(0).put("BUSINESS_NAME", Business_name_str);
			retLabelList.get(0).put("BUSINESS_TYPE", Business_type_str);
			//储位 仓管员
			if(receipt_flag){
				//获取储位
				Map<String,Object> bincodemap=new HashMap<String,Object>();
				bincodemap.put("WERKS", retLabelList.get(0).get("WERKS"));
				bincodemap.put("WH_NUMBER", retLabelList.get(0).get("WH_NUMBER"));
				bincodemap.put("MATNR", retLabelList.get(0).get("MATNR"));
				List<WmsCMatStorageEntity> matstoragelist=matstorageservice.selectByMap(bincodemap);
				if(matstoragelist!=null&&matstoragelist.size()>0){
					//retLabelList.get(0).put("BIN_CODE", matstoragelist.get(0).getBinCode());//储位
					retLabelList.get(0).put("BIN_CODE", "");//储位
				}else{
					retLabelList.get(0).put("BIN_CODE", "AAAA");
				}
				
				//仓管员
				Map<String,Object> whmap=new HashMap<String,Object>();
				whmap.put("WERKS", retLabelList.get(0).get("WERKS"));
				whmap.put("WHNUMBER", retLabelList.get(0).get("WH_NUMBER"));
				whmap.put("MATNR", retLabelList.get(0).get("MATNR"));
				whmap.put("LGORT", retLabelList.get(0).get("LGORT"));
				Map<String,Object> retWhMap=getwhManager(whmap);
				retLabelList.get(0).put("WH_MANAGER",retWhMap.get("WH_MANAGER"));
			}else if(inbound_flag){
				retLabelList.get(0).put("BIN_CODE", inboundBinCode);
				retLabelList.get(0).put("WH_MANAGER",inboundWhManager);
				retLabelList.get(0).put("MO_NO",MO_NO);
				retLabelList.get(0).put("MO_ITEM_NO",MO_ITEM_NO);
				retLabelList.get(0).put("SO_NO",SO_NO);
				retLabelList.get(0).put("SO_ITEM_NO",SO_ITEM_NO);
			}
			
		}
		return retLabelList;
	}
	
	@Override
	public List<Map<String, Object>> getWMSDOCMatDoc(Map<String, Object> map) {
		// TODO Auto-generated method stub
		return wmsInInboundDao.getWMSDOCMatDoc(map);
	}

	@Override
	public List<Map<String, Object>> getSAPDOCDocDate(Map<String, Object> map) {
		// TODO Auto-generated method stub
		return wmsInInboundDao.getSAPDOCDocDate(map);
	}
	/*
	 * 保存上架任务
	 */
	String saveWhTask(Map<String, Object> map){
		List<Map<String,Object>> whtakslist=new ArrayList<Map<String,Object>>();
		Map<String,Object> taskMap=new HashMap<String,Object>();
		taskMap.put("WERKS", map.get("WERKS"));
		taskMap.put("WH_NUMBER", map.get("WH_NUMBER"));
		taskMap.put("PROCESS_TYPE", "00");//上架
		taskMap.put("PRIORITY_LEVEL", "");
		taskMap.put("MATNR", map.get("MATNR"));
		taskMap.put("MAKTX", map.get("MAKTX"));
		taskMap.put("QUANTITY", map.get("MAY_IN_QTY"));
		taskMap.put("CONFIRM_QUANTITY", map.get("MAY_IN_QTY"));
		taskMap.put("UNIT", map.get("UNIT"));
		taskMap.put("STOCK_TYPE", map.get("SOBKZ"));
		taskMap.put("BATCH", map.get("BATCH"));
		taskMap.put("LABEL_NO", map.get("LABEL_NO"));
		
		Map<String,Object> whbinMap=new HashMap<String,Object>();
		whbinMap.put("WH_NUMBER", map.get("WH_NUMBER"));
		whbinMap.put("BIN_CODE", (map.get("BIN_CODE_SHELF")==null||"".equals(map.get("BIN_CODE_SHELF").toString()))?map.get("BIN_CODE"):map.get("BIN_CODE_SHELF"));
		List<WmsCoreWhBinEntity> whBinList=wmsCoreWhBinDao.selectByMap(whbinMap);
		if(whBinList!=null&&whBinList.size()>0){
			taskMap.put("FROM_STORAGE_AREA", whBinList.get(0).getStorageAreaCode());
		}else{
			taskMap.put("FROM_STORAGE_AREA", "");
		}
		
		taskMap.put("FROM_BIN_CODE", (map.get("BIN_CODE_SHELF")==null||"".equals(map.get("BIN_CODE_SHELF").toString()))?map.get("BIN_CODE"):map.get("BIN_CODE_SHELF"));
		
		Map<String,Object> whbinMap_=new HashMap<String,Object>();
		whbinMap_.put("WH_NUMBER", map.get("WH_NUMBER"));
		whbinMap_.put("BIN_CODE", map.get("BIN_CODE"));
		List<WmsCoreWhBinEntity> whBinList_=wmsCoreWhBinDao.selectByMap(whbinMap_);
		if(whBinList_!=null&&whBinList_.size()>0){
			taskMap.put("TO_STORAGE_AREA", whBinList_.get(0).getStorageAreaCode());
		}else{
			taskMap.put("TO_STORAGE_AREA", "");
		}
		taskMap.put("TO_BIN_CODE", map.get("BIN_CODE"));
		taskMap.put("REFERENCE_DELIVERY_NO", map.get("INBOUND_NO"));
		taskMap.put("REFERENCE_DELIVERY_ITEM", map.get("INBOUND_ITEM_NO"));
		taskMap.put("WAVE", "");
		taskMap.put("WAVE_ITEM", "");
		taskMap.put("CONFIRMOR", "");
		taskMap.put("CONFIRM_TIME", "");
		taskMap.put("EXCEPTION_CODE", "");
		taskMap.put("REMARK", "");
		taskMap.put("DEL", "0");
		taskMap.put("CREATE_DATE", DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
		taskMap.put("VERIFY", "");
		taskMap.put("CREATOR", "");
		taskMap.put("EDITOR", "");
		taskMap.put("EDIT_DATE", DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
		taskMap.put("LIFNR", map.get("LIFNR"));
		taskMap.put("MOULD_NO", "");
		taskMap.put("SOBKZ", map.get("SOBKZ"));
		whtakslist.add(taskMap);
		return warehouseTasksService.saveWHTask(whtakslist);
	}

	@Override
	public Map<String, Object> validLabelQty(Map<String, Object> params) {
		Map<String, Object> labelMap = new HashMap <String, Object>();
		labelMap.put("LABEL_NO", params.get("LABEL_NO"));
		labelMap.put("WERKS", params.get("WERKS"));
		labelMap.put("WH_NUMBER", params.get("WH_NUMBER"));
		
		List<Map<String, Object>> retLabelList=wmsInInboundDao.getLabelList(labelMap);//获取页面即将查询的这一条标签
		JSONArray jarr = new JSONArray();
		if(params.get("ARRLIST")!=null && params.get("ARRLIST").toString().contains("{")) {
			jarr = JSON.parseArray(params.get("ARRLIST").toString());
		}

		boolean receipt_flag=false;
		boolean inbound_flag=false;
		String labelnew_falg="";//待查询的标签类型，外购还是自制
		if(retLabelList!=null&&retLabelList.size()>0){
			//判断是收货产生的标签，还是创建进仓单产生的进仓单
			if(retLabelList.get(0).get("RECEIPT_NO")!=null&&retLabelList.get(0).get("RECEIPT_ITEM_NO")!=null){
				receipt_flag=true;
				labelnew_falg="receive";
			}else if(retLabelList.get(0).get("INBOUND_NO")!=null&&retLabelList.get(0).get("INBOUND_ITEM_NO")!=null){
				inbound_flag=true;
				labelnew_falg="inbound";
			}
			
			String exist_flag="";//在列表中的标签类型，外购还是自制
			if(jarr.size()>0){
				if(jarr.getJSONObject(0).getString("RECEIPT_NO")!=null&&jarr.getJSONObject(0).getString("RECEIPT_ITEM_NO")!=null){
					if(!"".equals(jarr.getJSONObject(0).getString("RECEIPT_NO"))&&!"".equals(jarr.getJSONObject(0).getString("RECEIPT_ITEM_NO"))){
					exist_flag="receive";
					}
				} 
				if(jarr.getJSONObject(0).getString("INBOUND_NO")!=null&&jarr.getJSONObject(0).getString("INBOUND_ITEM_NO")!=null){
					if(!"".equals(jarr.getJSONObject(0).getString("INBOUND_NO"))&&!"".equals(jarr.getJSONObject(0).getString("INBOUND_ITEM_NO"))){
					exist_flag="inbound";
					}
				}
			}
			
			if(!labelnew_falg.equals(exist_flag)&&!"".equals(exist_flag)){//待查询的标签和列表中存在的标签类型不一致，报错
				throw new RuntimeException("标签类型不一致！");
			}
			
		}
		//此处注释内容在交接处 判断
		/*
		if(receipt_flag){
			//标签数量
			BigDecimal label_qty=BigDecimal.ZERO;
			List<Map<String, Object>> retReceiveQtyList=wmsInInboundDao.getLabelQtyByReceiveNoAndItemNo(retLabelList.get(0));
			if(retReceiveQtyList!=null&&retReceiveQtyList.size()>0){
				 label_qty=new BigDecimal(retReceiveQtyList.get(0).get("LABEL_QTY").toString());
			}
			
			//收货单可进仓数量
			BigDecimal receive_in_qty=BigDecimal.ZERO;
			List<Map<String, Object>> retReceiptQtyList=wmsInInboundDao.getReceiptQtyByReceiveNoAndItemNo(retLabelList.get(0));
			if(retReceiptQtyList!=null&&retReceiptQtyList.size()>0){
				 receive_in_qty=new BigDecimal(retReceiptQtyList.get(0).get("IN_QTY").toString());
			}
			
			//进仓单合格进仓数量
			BigDecimal inbound_in_qty=BigDecimal.ZERO;
			List<Map<String, Object>> retInboundQtyList=wmsInInboundDao.geInboundQtyByReceiveNoAndItemNo(retLabelList.get(0));
			if(retInboundQtyList!=null&&retInboundQtyList.size()>0){
				inbound_in_qty=new BigDecimal(retInboundQtyList.get(0).get("IN_QTY").toString());
			}
			
			//首先查询当前 标签 对应的数量是否 合理
			//收货单可进仓数量-标签数量-进仓单合格进仓数量
			BigDecimal subBig=receive_in_qty.subtract(label_qty).subtract(inbound_in_qty);
			if(subBig.compareTo(BigDecimal.ZERO)<0){
				throw new RuntimeException("收货单号"+retLabelList.get(0).get("RECEIPT_NO")+" 行项目号"+retLabelList.get(0).get("RECEIPT_ITEM_NO")+" 进仓数量不能大于合格数量！");
			}else{//继续比较 页面上传来的 数据
				//找出已有列表 中同一个 RECEIPT_NO，RECEIPT_ITEM_NO的可进仓数量
				if(jarr.size()>0){
					for(int i=0;i<jarr.size();i++){
						JSONObject itemData=  jarr.getJSONObject(i);
						String receipt_no=itemData.getString("RECEIPT_NO");
						String receipt_item_no=itemData.getString("RECEIPT_ITEM_NO");
						String may_in_qty=itemData.getString("MAY_IN_QTY");//页面上的可进仓数量
						BigDecimal may_in_qty_d=BigDecimal.ZERO;
						if(!"".equals(may_in_qty)){
							may_in_qty_d=new BigDecimal(may_in_qty);
						}
						if(receipt_no.equals(retLabelList.get(0).get("RECEIPT_NO"))&&receipt_item_no.equals(retLabelList.get(0).get("RECEIPT_ITEM_NO"))){
							//当前标签的RECEIPT_NO，RECEIPT_ITEM_NO存在相同的
							subBig=subBig.subtract(may_in_qty_d);
							if(subBig.compareTo(BigDecimal.ZERO)<0){
								throw new RuntimeException("收货单号"+retLabelList.get(0).get("RECEIPT_NO")+" 行项目号"+retLabelList.get(0).get("RECEIPT_ITEM_NO")+" 进仓数量不能大于合格数量！");
							}
						}					
					}
				}
		
			}
			
		}
		
		if(inbound_flag){
			//标签数量
			BigDecimal label_qty=BigDecimal.ZERO;
			List<Map<String, Object>> retReceiveQtyList=wmsInInboundDao.getLabelQtyByInboundNoAndItemNo(retLabelList.get(0));
			if(retReceiveQtyList!=null&&retReceiveQtyList.size()>0){
				 label_qty=new BigDecimal(retReceiveQtyList.get(0).get("LABEL_QTY").toString());
			}
			
			//进仓单合格进仓数量
			BigDecimal inbound_in_qty=BigDecimal.ZERO;
			List<Map<String, Object>> retInboundQtyList=wmsInInboundDao.geInboundQtyByInboundNoAndItemNo(retLabelList.get(0));
			if(retInboundQtyList!=null&&retInboundQtyList.size()>0){
				inbound_in_qty=new BigDecimal(retInboundQtyList.get(0).get("IN_QTY").toString());
			}
			//找出已有列表 中同一个 INBOUND_NO，INBOUND_ITEM_NO的可进仓数量
			if(jarr.size()>0){
				for(int i=0;i<jarr.size();i++){
					JSONObject itemData=  jarr.getJSONObject(i);
					String inbound_no=itemData.getString("INBOUND_NO");
					String inbound_item_no=itemData.getString("INBOUND_ITEM_NO");
					String may_in_qty=itemData.getString("MAY_IN_QTY");//页面上标签的可进仓数量
					BigDecimal may_in_qty_d=BigDecimal.ZERO;
					if(!"".equals(may_in_qty)){
						may_in_qty_d=new BigDecimal(may_in_qty);
					}
					
					if(inbound_no.equals(retLabelList.get(0).get("INBOUND_NO"))&&inbound_item_no.equals(retLabelList.get(0).get("INBOUND_ITEM_NO"))){
						//当前标签的RECEIPT_NO，RECEIPT_ITEM_NO存在相同的
						label_qty=label_qty.add(may_in_qty_d);
						
					}
				}
			}
			
			if(label_qty.subtract(inbound_in_qty).compareTo(BigDecimal.ZERO)>0){
				throw new RuntimeException("进仓单号"+retLabelList.get(0).get("INBOUND_NO")+" 行项目号"+retLabelList.get(0).get("INBOUND_ITEM_NO")+" 进仓数量不能大于进仓单可进仓数量！");
			}
		}
		*/
		return null;
	}

	@Override
	public List<Map<String,Object>> getLabelInfo(Map<String, Object> params) {
		
		return wmsInInboundDao.getLabelInfo(params);
	}

	@Override
	public List<Map<String, Object>> getInpoCptConsume(List<Map<String, Object>> cptList) {
		// TODO Auto-generated method stub
		return wmsInInboundDao.getInpoCptConsume(cptList);
	}

	/**
	 * 1 扣减收料房库存
	 * 2 收货单数据更新
	 * 3 累加进仓单的已进仓数量，再更新进仓单状态
	 * 4 更新核销表信息
	 * 5 库存增加
	 * 6 生成凭证信息
	 * 7 判断是否过账sap
	 * 8 过账sap
	 * 9 是否启用立库
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public Map<String, Object> handover_new(Map<String, Object> params) {
		//将过账的行项目从前台json转成集合
		List<Map<String, Object>> f_list=this.JSONArrayToList(params);
		
		//从头表获取进仓单类型的结果集
		List<Map<String, Object>> retInboundHeadList=wmsInInboundDao.getInboundHeadList(f_list);
		//保存待更新的破坏数量结果集
		List<Map<String, Object>> retDestroyList=new ArrayList<Map<String, Object>>();
		//收料房待更新的结果集
		List<Map<String, Object>> retrhStockList=new ArrayList<Map<String, Object>>();
		//收货单待更新的结果集
		List<Map<String, Object>> retreceiptList=new ArrayList<Map<String, Object>>();
		//获取仓库储位 表信息 WMS_CORE_WH_BIN
		List<Map<String, Object>> retWmsCoreWhBinList=wmsInInboundDao.getWmsCoreWhBinByList(f_list);
		//累计进仓单的数量的结果集
		List<Map<String, Object>> retinboundItemQtyList=new ArrayList<Map<String, Object>>();
		//核销的结果集
		List<Map<String, Object>> retHxList=new ArrayList<Map<String, Object>>();
		
		//查询BUSINESS_CLASS 所有结果集
		Map<String,Object> bussinessmap=new HashMap<String,Object>();
		List<Map<String,Object>> retBusinessList=wmsInInboundDao.getWmsBusinessClass(bussinessmap);
		//查询业务类型 所有结果集
		Map<String,Object> cdmap=new HashMap<String,Object>();
		List<Map<String,Object>> moveAndSyncList=wmsInInboundDao.getMoveAndSynByAll(cdmap);
		//
		List<Map<String, Object>> labelMapList=new ArrayList<Map<String, Object>>();
		//查询全部的仓库配置
		Map<String,Object> cWhmap=new HashMap<String,Object>();
		List<Map<String, Object>> cwhList=wmsInInboundDao.queryCWh(cWhmap);
		
		//
		List<List<Map<String,Object>>> sapList=new ArrayList<List<Map<String,Object>>>();//sap过账准备的
		List<List<Map<String,Object>>> wmsList=new ArrayList<List<Map<String,Object>>>();//wms保存事务记录准备的
		List<Map<String,Object>> matList=new ArrayList<Map<String,Object>>();//库存增加准备的
		//待插入标签集合
		List<Map<String, Object>> wmsCoreStockLabelList=new ArrayList<Map<String, Object>>();
		//通过进仓单号 查询  进仓采购订单组件消耗表  （委外表）
		List<Map<String, Object>> poCptConsumeList=wmsInInboundDao.getInpoCptConsume(f_list);
		
		//用于验证是否需要合并过账和凭证的记录
		Map<String, Object> checkMap=new HashMap<String, Object>();
		Map<String, Object> checkMapSap=new HashMap<String, Object>();
		//验证库存是否需要合并的记录
		Map<String, Object> checkMapStock=new HashMap<String, Object>();
		
		String WMS_NO="";
		
		try {
			for(Map<String, Object> itemMap:f_list){
				String inbound_type="";//进仓单类型    00 外购进仓单 01 自制进仓单
				//如果不是标签交接的数据,才取进仓单类型 INBOUND_TYPE
				if(itemMap.get("LABEL_FALG")==null||"".equals(itemMap.get("LABEL_FALG"))){
					for(Map<String, Object> inboundHeadMap:retInboundHeadList){
						if(inboundHeadMap.get("INBOUND_NO").equals(itemMap.get("INBOUND_NO"))){
							inbound_type=inboundHeadMap.get("INBOUND_TYPE")==null?"":inboundHeadMap.get("INBOUND_TYPE").toString();
						}
					}
				}
				
				//如果存在收货单的破坏数量，则按照以下规则重新设置MAY_IN_QTY,并准备集合，更新收货单
				Map<String, Object> destoryMapRet=new HashMap<String, Object>();
				if(itemMap.get("DESTROY_QTY")!=null&&!"".equals(itemMap.get("DESTROY_QTY"))){
					destoryMapRet=this.updateDestoryQty(itemMap);
					retDestroyList.add(destoryMapRet);
				}
				
				//1 更新收料房[WMS_IN_RH_STOCK]库存数量（收料房库存数量减少，当收料房库存更新为0时，删除该行数据）
				//准备集合，更新收料房
				Map<String, Object> rhStockMap=this.getRhStockList(inbound_type,itemMap);
				if(rhStockMap!=null){
					retrhStockList.add(rhStockMap);
				}
				
				//2 收货单数据更新
				//首先查询出行表中 收货单号 行项目号 头表中的质检状态
				if("00".equals(inbound_type)||"RECEIPT".equals(itemMap.get("LABEL_FALG"))){
					String qc_result="";
					
					for(Map<String, Object> inBoundHead:retInboundHeadList){
						if(inBoundHead.get("INBOUND_NO").equals(itemMap.get("INBOUND_NO"))){
							qc_result=inBoundHead.get("QC_RESULT").toString();
						}
					}
					if("RECEIPT".equals(itemMap.get("LABEL_FALG"))){
						qc_result="01";
					}
					Map<String, Object> receiptMap=this.getReceiptList(retWmsCoreWhBinList,itemMap);
					receiptMap.put("QC_RESULT", qc_result);
					retreceiptList.add(receiptMap);
				}
				
				// 3 累加进仓单的已进仓数量，再更新进仓单状态
				Map<String, Object> inboundQtyMap=this.getInboundQtyList(itemMap);
				retinboundItemQtyList.add(inboundQtyMap);
				
				//获取knttp 用来判断是否增加储位
				//查询费用性采购订单 ,以及查询采购订单表的转换数量
				String knttp_str="";
				String po_no_str="";
				String po_item_no_str="";
				
				String umrez_str="1";
				String umren_str="1";
				if(!"".equals(itemMap.get("INBOUND_NO"))||"RECEIPT".equals(itemMap.get("LABEL_FALG"))){
					List<Map<String, Object>> knttpList=wmsInInboundDao.getKnttp(itemMap);
					//采购订单101 进仓的,重新获取
					if("67".equals(itemMap.get("BUSINESS_NAME"))&&"02".equals(itemMap.get("BUSINESS_TYPE"))){
						knttpList=wmsInInboundDao.getKnttpByInternal(itemMap);//
						if("".equals(itemMap.get("MATNR"))&&(knttpList!=null&&knttpList.size()>0)){
							for(Map<String, Object> knttpMap:knttpList){
								knttpMap.put("UMREZ", "1");
								knttpMap.put("UMREN", "1");
							}
						}
					}
					if("10".equals(itemMap.get("BUSINESS_NAME"))){//只有外购的才判断 单位换算关系，20191108 pengtao update
					if("01".equals(itemMap.get("BUSINESS_TYPE"))||"02".equals(itemMap.get("BUSINESS_TYPE"))||"06".equals(itemMap.get("BUSINESS_TYPE"))||"20".equals(itemMap.get("BUSINESS_TYPE"))){
					if(knttpList!=null&&knttpList.size()>0){
						//2019-08-12 add by thw 处理自动同步的PO无法同步到单位转换值
						if("0".equals(knttpList.get(0).get("UMREZ").toString())) {
							//PO行项目未同步到单位换算值，报错提示使用手工同步PO后再进仓
							throw new RuntimeException(knttpList.get(0).get("PO_NO")+"采购订单采购单位和基本单位不一致且未获取到单位换算值，请使用采购订单同步功能获取单位换算值！");
						}
						
						knttp_str=knttpList.get(0).get("KNTTP")==null?"":knttpList.get(0).get("KNTTP").toString();
						
						po_no_str=knttpList.get(0).get("PO_NO")==null?"":knttpList.get(0).get("PO_NO").toString();
						po_item_no_str=knttpList.get(0).get("PO_ITEM_NO")==null?"":knttpList.get(0).get("PO_ITEM_NO").toString();
						
						umrez_str=knttpList.get(0).get("UMREZ")==null?"1":knttpList.get(0).get("UMREZ").toString();
						umren_str=knttpList.get(0).get("UMREN")==null?"1":knttpList.get(0).get("UMREN").toString();
					}else{
						throw new RuntimeException("进仓单关联的PO采购单位在WMS单位主数据表不存在！");
					}
				}
				}
				}
				
				
				if("67".equals(itemMap.get("BUSINESS_NAME"))&&"02".equals(itemMap.get("BUSINESS_TYPE"))){
					//采购订单101 进仓的
					
				}else{
					itemMap.put("UMREZ", umrez_str);//umrez_str
					itemMap.put("UMREN", umren_str);//umren_str
				}
				
				
				String cmms_flag="";
				for(Map<String, Object> cwhMap:cwhList){
		    		if(cwhMap.get("WERKS").equals(params.get("WERKS"))
		    				&&cwhMap.get("WH_NUMBER").equals(params.get("WH_NUMBER"))){
		    			cmms_flag=cwhMap.get("CMMS_FLAG")==null?"":cwhMap.get("CMMS_FLAG").toString();
		    		}
		    	}
				
				if("0".equals(cmms_flag)){//  工厂配置是启用费用性物料管理 不启用
					if((!"F".equals(knttp_str))&&(!"K".equals(knttp_str))&&(!"P".equals(knttp_str))){//非费用性物料
						
						this.handWmsData(matList, itemMap, destoryMapRet,checkMapStock);
						
					}
				}else{//启用
					//if(("F".equals(knttp_str))||("K".equals(knttp_str))||("P".equals(knttp_str))){//费用性物料
						this.handWmsData(matList, itemMap, destoryMapRet,checkMapStock);
						
					//}
				}
				
				itemMap.put("DESTROY_QTY_SUB", destoryMapRet.get("DESTROY_GZ_QTY"));
				
				//如果委外消耗清单存在
				List<Map<String, Object>> poComponentList=new ArrayList<Map<String, Object>>();
				if(poCptConsumeList.size()>0){
					for(Map<String, Object> pocptConsumeMap:poCptConsumeList){
						Map<String, Object> poComponentMap=new HashMap<String, Object>();
						
					 if(itemMap.get("INBOUND_NO")!=null&&!"".equals(itemMap.get("INBOUND_NO"))){
						if(pocptConsumeMap.get("INBOUND_NO").equals(itemMap.get("INBOUND_NO"))&&
								pocptConsumeMap.get("INBOUND_ITEM_NO").equals(itemMap.get("INBOUND_ITEM_NO"))){
							poComponentMap.put("INBOUND_NO", pocptConsumeMap.get("INBOUND_NO"));
							poComponentMap.put("INBOUND_ITEM_NO", pocptConsumeMap.get("INBOUND_ITEM_NO"));
							poComponentMap.put("EBELN", pocptConsumeMap.get("EBELN"));
							poComponentMap.put("EBELP", pocptConsumeMap.get("EBELP"));
							poComponentMap.put("MATN2", pocptConsumeMap.get("MATN2"));
							poComponentMap.put("MAKTX2", pocptConsumeMap.get("MAKTX2"));
							poComponentMap.put("MENG2", pocptConsumeMap.get("MENG2"));
							poComponentMap.put("MEIN2", pocptConsumeMap.get("MEIN2"));
							poComponentMap.put("BATCH", pocptConsumeMap.get("BATCH"));
							poComponentMap.put("WMS_MOVE_TYPE", "543");
							poComponentMap.put("SAP_MOVE_TYPE", "543");
							poComponentMap.put("REVERSAL_FLAG", "X");//不允许冲销
							poComponentMap.put("CANCEL_FLAG", "X");//不允许取消
							
							poComponentMap.put("BUSINESS_NAME", itemMap.get("BUSINESS_NAME"));
							poComponentMap.put("BUSINESS_TYPE", itemMap.get("BUSINESS_TYPE"));
							poComponentMap.put("BUSINESS_CLASS", itemMap.get("BUSINESS_CLASS"));
							poComponentMap.put("WERKS", itemMap.get("WERKS"));
							poComponentMap.put("WH_NUMBER", itemMap.get("WH_NUMBER"));
							
							poComponentList.add(poComponentMap);
						}
						}
					}
					
					itemMap.put("poComonentList", poComponentList);
				}
				
				
				//4 更新核销表信息 数据准备
				//a BUSINESS_NAME=07和BUSINESS_TYPE=03 更新WMS_HX_PO表的SS105
				if(!"".equals(po_no_str)&&!"".equals(po_item_no_str)&&"07".equals(itemMap.get("BUSINESS_NAME"))&&"03".equals(itemMap.get("BUSINESS_TYPE"))){
					Map<String,Object> hxmap=new HashMap<String,Object>();//
					hxmap.put("MAY_IN_QTY", itemMap.get("MAY_IN_QTY"));
					hxmap.put("EBELN", po_no_str);
					hxmap.put("EBELP", po_item_no_str);
					hxmap.put("WERKS", itemMap.get("WERKS"));
					hxmap.put("WH_NUMBER",itemMap.get("WH_NUMBER"));
					retHxList.add(hxmap);
				}
				
				//7 判断是否过账sap
				
				//查询BUSINESS_CLASS
				String businuss_class_str="";
				for(Map<String,Object> businessMap:retBusinessList){
					if(businessMap.get("BUSINESS_NAME").equals(itemMap.get("BUSINESS_NAME"))
							&&businessMap.get("BUSINESS_TYPE").equals(itemMap.get("BUSINESS_TYPE"))
							&&businessMap.get("SOBKZ").equals(itemMap.get("SOBKZ"))
							){
						businuss_class_str=businessMap.get("BUSINESS_CLASS").toString();
					}
				}
				boolean business_flag=false;//判断业务类型是否存在
				String SAP_MOVE_TYPE="";
				String WMS_MOVE_TYPE="";
				for(Map<String,Object> moveTypeMap:moveAndSyncList){//查找当前行的过账类型
					if(moveTypeMap.get("BUSINESS_NAME").equals(itemMap.get("BUSINESS_NAME"))
							&&moveTypeMap.get("BUSINESS_TYPE").equals(itemMap.get("BUSINESS_TYPE"))
							&&moveTypeMap.get("BUSINESS_CLASS").equals(businuss_class_str)
							&&(moveTypeMap.get("WERKS")==null?itemMap.get("WERKS"):moveTypeMap.get("WERKS")).equals(itemMap.get("WERKS"))
							&&(moveTypeMap.get("SOBKZ")==null?itemMap.get("SOBKZ"):moveTypeMap.get("SOBKZ")).equals(itemMap.get("SOBKZ"))){
						SAP_MOVE_TYPE=moveTypeMap.get("SAP_MOVE_TYPE")==null?"":moveTypeMap.get("SAP_MOVE_TYPE").toString();
						WMS_MOVE_TYPE=moveTypeMap.get("WMS_MOVE_TYPE")==null?"":moveTypeMap.get("WMS_MOVE_TYPE").toString();
						business_flag=true;
					}
				}
				//20190710 注释，允许sap过账移动类型为空  
				/*if("".equals(SAP_MOVE_TYPE)) {
					throw new RuntimeException(itemMap.get("WERKS")+"工厂未配置业务类型！");
				}*/
				//20190724增加业务类型判断标志
				if(!business_flag){
					
					throw new RuntimeException(itemMap.get("WERKS")+"工厂未配置业务类型！");
				}
				
				// 为添加凭证记录 过账记录 准备数据
				itemMap.put("WMS_MOVE_TYPE", WMS_MOVE_TYPE);
				itemMap.put("SAP_MOVE_TYPE", SAP_MOVE_TYPE);
				itemMap.put("BUSINESS_CLASS",businuss_class_str);
				//311过账类型 源库位和 目标库位处理
				if("10".equals(itemMap.get("BUSINESS_NAME"))&&"06".equals(itemMap.get("BUSINESS_TYPE"))){
					//目标库位
					itemMap.put("MOVE_STLOC", itemMap.get("LGORT")==null?"":itemMap.get("LGORT"));
					//再重新赋值,源库位
					itemMap.put("LGORT", itemMap.get("F_LGORT")==null?"":itemMap.get("F_LGORT"));
				}
				//itemMap.put("BIN_CODE", (itemMap.get("BIN_CODE_SHELF")==null||"".equals(itemMap.get("BIN_CODE_SHELF")))?itemMap.get("BIN_CODE"):itemMap.get("BIN_CODE_SHELF"));
				itemMap.put("BIN_CODE", itemMap.get("BIN_CODE")==null?"":itemMap.get("BIN_CODE"));
				itemMap.put("QTY_WMS", itemMap.get("MAY_IN_QTY")==null?"":itemMap.get("MAY_IN_QTY"));
				itemMap.put("QTY_SAP", itemMap.get("MAY_IN_QTY")==null?"":itemMap.get("MAY_IN_QTY"));
				itemMap.put("BATCH_SAP", itemMap.get("BATCH"));
				itemMap.put("HANDOVER", params.get("HANDOVER"));
				itemMap.put("HEADER_TXT", itemMap.get("TXTRULE")==null?"":itemMap.get("TXTRULE"));
				/*if("67".equals(itemMap.get("BUSINESS_NAME"))&&"02".equals(itemMap.get("BUSINESS_TYPE"))){
					//采购订单101的  po_no,PO_ITEM_NO不要再重置
				}else{
					itemMap.put("PO_NO", po_no_str);//po_no_str
					itemMap.put("PO_ITEM_NO", po_item_no_str);//po_item_no_str
				}*/
				
				if(!"".equals(po_no_str)&&!"".equals(po_item_no_str)){
					itemMap.put("PO_NO", po_no_str);//po_no_str
					itemMap.put("PO_ITEM_NO", po_item_no_str);//po_item_no_str
				}
				if("10".equals(itemMap.get("BUSINESS_NAME"))&&"06".equals(itemMap.get("BUSINESS_TYPE"))){
					//311移动类型  po_no,PO_ITEM_NO不要传
					itemMap.put("PO_NO", "");
					itemMap.put("PO_ITEM_NO", "");
				}
				
				
				BigDecimal destroy_qty_sub=BigDecimal.ZERO;
				if(itemMap.get("DESTROY_QTY_SUB")!=null){
					destroy_qty_sub=new BigDecimal(itemMap.get("DESTROY_QTY_SUB").toString());
				}
					
				if(destroy_qty_sub.compareTo(BigDecimal.ZERO)>0){//有质检破坏数量,方便后面取出，单独过账
					itemMap.put("COST_CENTER", itemMap.get("IQC_COST_CENTER"));
					itemMap.put("DESTORY_QTY", destroy_qty_sub);
					itemMap.put("DESTROY_FLAG", "true");
				}else{
					itemMap.put("DESTROY_FLAG", "false");
					itemMap.put("DESTORY_QTY", "");
				}
				
				
				if(StringUtils.isNoneBlank(SAP_MOVE_TYPE)) {//需要过账
					String[] strs=SAP_MOVE_TYPE.split("#");
					SAP_MOVE_TYPE="[";
					for(String s:strs) {
						SAP_MOVE_TYPE+="{\"moveType\":\""+s+"\",\"sapDoc\":\""+"\"}"+",";
					}
					SAP_MOVE_TYPE=SAP_MOVE_TYPE.substring(0, SAP_MOVE_TYPE.length()-1);
					SAP_MOVE_TYPE+="]";
					
					//整理凭证和过账的数据
					this.getsaplistData(sapList,itemMap,checkMapSap);
				}else{//不需要过账
					
				}
				//准备事务记录的数据
				this.getwmslistdata(wmsList,itemMap,checkMap);
				
				//判断AUTO_PUTAWAY_FLAG =0 手动上架，则产生上架任务     对应条码标签状态改成 07已进仓 ，否则不产生上架任务，对应条码标签状态改成 08已上架
				
				String labelstr=itemMap.get("LABEL_NO").toString();
				String[] labelLst1 = labelstr.split(",");//取出label_no
				
				if("0".equals(itemMap.get("AUTO_PUTAWAY_FLAG"))){
					this.saveWhTask(itemMap);
					
					if(labelLst1!=null&&labelLst1.length>0){
						for(int m=0;m<labelLst1.length;m++){
							Map<String, Object> labelMapTemp=new HashMap<String, Object>();
							labelMapTemp.put("LABEL_NO", labelLst1[m].trim());
							labelMapTemp.put("LABEL_STATUS", "07");
							labelMapList.add(labelMapTemp);
						}
					}
				}else{
					if(labelLst1!=null&&labelLst1.length>0){
						for(int m=0;m<labelLst1.length;m++){
							Map<String, Object> labelMapTemp=new HashMap<String, Object>();
							labelMapTemp.put("LABEL_NO", labelLst1[m].trim());
							labelMapTemp.put("LABEL_STATUS", "08");
							labelMapList.add(labelMapTemp);
						}
					}
				}
				//
			
			}
			
			
			//存在破坏数量的
			if(retDestroyList.size()>0){
				wmsInInboundDao.updateReceiptDestroyQtyByList(retDestroyList);
			}
			//更新收料房结果集
			if(retrhStockList.size()>0){
				//查询出同一个 工厂、仓库号、批次、物料号的收料房库存数据，与待更新的数据比对
				//MAY_IN_QTY
				List<Map<String, Object>> retRhStockQtylist=wmsInInboundDao.getRhStockList(retrhStockList);
				if(retRhStockQtylist!=null&&retRhStockQtylist.size()>0){
					for(Map<String, Object> rhStockQtyMap:retRhStockQtylist){
						for(Map<String, Object> rhStockMap:retrhStockList){
							if(rhStockQtyMap.get("WERKS").equals(rhStockMap.get("WERKS"))
									&&rhStockQtyMap.get("WH_NUMBER").equals(rhStockMap.get("WH_NUMBER"))
									&&rhStockQtyMap.get("BATCH").equals(rhStockMap.get("BATCH"))
									&&rhStockQtyMap.get("MATNR").equals(rhStockMap.get("MATNR"))
									&&rhStockQtyMap.get("LGORT").equals(rhStockMap.get("LGORT"))
									&&rhStockQtyMap.get("SOBKZ").equals(rhStockMap.get("SOBKZ"))
									&&rhStockQtyMap.get("LIFNR").equals(rhStockMap.get("LIFNR"))){
								BigDecimal rh_qty_d=new BigDecimal(rhStockQtyMap.get("RH_QTY").toString());
								BigDecimal may_in_qty_d=new BigDecimal(rhStockMap.get("MAY_IN_QTY").toString());
								if(may_in_qty_d.subtract(rh_qty_d).compareTo(BigDecimal.ZERO)>0){
									
									throw new RuntimeException("工厂"+rhStockMap.get("WERKS")+" 料号"+rhStockMap.get("MATNR")+" 批次 "+rhStockMap.get("BATCH")+" 交接数量不能大于收料房数量！");
								}
							}
						}
					}
				}
				//更新收料房库存
				wmsInInboundDao.updateRHStockByList(retrhStockList);
				
				//硬删除同一个 工厂、仓库号、批次、物料号的收料房的del=‘X’的库存数据
				wmsInInboundDao.deleteRHZeroStock(retrhStockList);
			}
			//更新收货单结果集
			if(retreceiptList.size()>0){
				wmsInInboundDao.updateReceiptByList(retreceiptList);
			}
			//累加进仓单的已进仓数量，再更新进仓单状态
			if(retinboundItemQtyList.size()>0){
				wmsInInboundDao.updateInboundItemRealQtyByList(retinboundItemQtyList);
				wmsInInboundDao.updateInboundItemStatusByList(retinboundItemQtyList);
			}
			//更新核销表信息
			if(retHxList.size()>0){
				wmsInInboundDao.updateHXPOByList(retHxList);
			}
			//更新标签状态
			if(labelMapList.size()>0){
				wmsInInboundDao.updateLabelStatusByLabelNoByList(labelMapList);
			}
			
			//判断工厂是否启用条码,启用了条码的需要需要插入wms_core_stock_label，把进仓单中的条码信息拆分，一个条码一行插入该表中，同时wms_core_stock的bin_code置位空
			boolean cwh_flag=false;
	    	for(Map<String, Object> cwhMap:cwhList){
	    		if(cwhMap.get("WERKS").equals(params.get("WERKS"))
	    				&&cwhMap.get("WH_NUMBER").equals(params.get("WH_NUMBER"))){
	    			cwh_flag=true;
	    		}
	    	}
		
	    	if(cwh_flag){//存在则 启用了条码
	    		
	    		for(int m=0;m<matList.size();m++){
	    			
	    			String Label_no_str=matList.get(m).get("LABEL_NO").toString();
	    			//List<String> labelList = JSON.parseArray(Label_no_str, String.class);//取出label_no
	    			String[] labelList = Label_no_str.split(",");//取出label_no
	    			for(int n=0;n<labelList.length;n++){
	    				String label_no=labelList[n];
	    				Map<String, Object> wmsStockLabelMap=new HashMap<String, Object>();
	    				wmsStockLabelMap.put("WERKS", matList.get(m).get("WERKS"));
	    				wmsStockLabelMap.put("WH_NUMBER", matList.get(m).get("WH_NUMBER"));
	    				wmsStockLabelMap.put("MATNR", matList.get(m).get("MATNR"));
	    				wmsStockLabelMap.put("BATCH", matList.get(m).get("BATCH"));
	    				//wmsStockLabelMap.put("LABEL_NO", JSON.toJSONString(label_no));
	    				wmsStockLabelMap.put("LABEL_NO", label_no.trim());
	    				wmsStockLabelMap.put("BIN_CODE", matList.get(m).get("BIN_CODE"));
	    				wmsStockLabelMap.put("BIN_NAME", matList.get(m).get("BIN_NAME"));
	    				wmsStockLabelMap.put("EDITOR", matList.get(m).get("EDITOR"));
	    				wmsStockLabelMap.put("EDIT_DATE", matList.get(m).get("EDIT_DATE"));
	    				wmsCoreStockLabelList.add(wmsStockLabelMap);
	    			}
	    			
	    			//matList.get(m).put("BIN_CODE", "");//将储位置位空，方便后续插入wms_core_stock
	    		}
	    		if(wmsCoreStockLabelList.size()>0){
	    		wmsInInboundDao.insertWmsCoreStockLabel(wmsCoreStockLabelList);
	    		}
	    	}
	    	// 5库存增加
			if(matList.size()>0){
				Map<String, Object> wmsstockParams=new HashMap<String, Object>();
				wmsstockParams.put("STOCK_TYPE", "STOCK_QTY");//增加正常库存
				wmsstockParams.put("matList", matList);
				commonService.saveWmsStock(wmsstockParams);
			}
			//6 生成凭证信息  
			JSONArray jarr = JSON.parseArray(params.get("ARRLIST").toString());
			Map<String,Object> head=new HashMap<String,Object>();
			head.put("WERKS", jarr.getJSONObject(0).getString("WERKS"));
			head.put("PZ_DATE", params.get("PZDDT"));
			head.put("JZ_DATE", params.get("JZDDT"));
			head.put("PZ_YEAR", params.get("PZDDT").toString().substring(0,4));
			head.put("HEADER_TXT", jarr.getJSONObject(0).getString("TXTRULE"));
			head.put("TYPE",  "00");//标准凭证
			head.put("CREATOR", params.get("USERNAME")+"："+params.get("FULL_NAME"));
			head.put("CREATE_DATE", DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));//
			//head.put("SAP_MOVE_TYPE",  SAP_MOVE_TYPE);
			
			
			
			if(wmsList!=null&&wmsList.size()>0){
				for(int p=0;p<wmsList.size();p++){
					//311过账类型 源库位和 目标库位处理
					for(Map itemMap:wmsList.get(p)){
						if("10".equals(itemMap.get("BUSINESS_NAME"))&&"06".equals(itemMap.get("BUSINESS_TYPE"))){
							//再重新赋值,库位
							itemMap.put("LGORT", itemMap.get("MOVE_STLOC")==null?"":itemMap.get("MOVE_STLOC"));
						}
					}
	
					String WMS_NO_temp=commonService.saveWMSDoc(head, wmsList.get(p));
					WMS_NO=WMS_NO+" "+WMS_NO_temp;
				}
			}
		} catch (Exception e) {
			//WMS报的错误，立即解锁
			List<String> keylist = (List<String>)params.get("keylist");
			for(String keys : keylist) {
				redisUtils.unlock(keys, params.get("UID").toString()); //解锁
			}
			//为了解锁，统一在这里抛异常
			throw new RuntimeException(e.getMessage());
		}
		
		//8 过账sap
		
		String SAP_NO="";
		for(int d=0;d<sapList.size();d++){
			List<Map<String,Object>> sapmaplist=sapList.get(d);
			for(int e=0;e<sapmaplist.size();e++){
				List<Map<String,Object>> matList_Temp=(List<Map<String, Object>>) sapmaplist.get(e).get("matList");
				//311过账类型 源库位和 目标库位处理
					for(Map<String,Object> itemMap:matList_Temp){
						if("10".equals(itemMap.get("BUSINESS_NAME"))&&"06".equals(itemMap.get("BUSINESS_TYPE"))){
							//再重新赋值,源库位
							itemMap.put("LGORT", itemMap.get("F_LGORT")==null?"":itemMap.get("F_LGORT"));
						}
					}
				
				String SAP_NO_temp=commonService.doSapPost(sapmaplist.get(e));
				SAP_NO=SAP_NO+" "+SAP_NO_temp;
			}
		}
		
		//存在质检破坏数量的，扣减对应的库存数量
		if(matList.size()>0){
			for(Map<String, Object> koujMap:matList){
				BigDecimal destroy_qty_sub=BigDecimal.ZERO;
				if(koujMap.get("DESTROY_QTY_SUB")!=null&&!"".equals(koujMap.get("DESTROY_QTY_SUB"))){
					//将扣减的数量变成负数
					destroy_qty_sub=BigDecimal.ZERO.subtract(new BigDecimal(koujMap.get("DESTROY_QTY_SUB").toString()));
				}
				koujMap.put("STOCK_QTY", destroy_qty_sub);
			}
			
			Map<String, Object> wmsstockParams_=new HashMap<String, Object>();
			wmsstockParams_.put("STOCK_TYPE", "STOCK_QTY");//扣减正常库存
			wmsstockParams_.put("matList", matList);
			commonService.saveWmsStock(wmsstockParams_);
		}
		//201出库过账 并保存凭证记录 开始
		//判断是否质检破坏数量 的标志，找出来 产生WMS成本中心投料凭证（201）
		//必须先保存事务 保存事务记录号，再过账，方便修改事务记录中的sap凭证记录字段
		String doc_no_201=this.doc201(wmsList, params);
		String sap_no_201=this.gz201(sapList);
		
		
		SAP_NO=SAP_NO+sap_no_201;
		WMS_NO=WMS_NO+doc_no_201;
		//201出库过账 并保存凭证记录 结束
		
		//更新头表
		this.updateHeadStatus_new(params);
		//20190813 add start
		String docNo=this.saveinbound303(params);
		//20190813 add end
		
		StringBuilder msg=new StringBuilder();
		
		msg.append(" WMS凭证:");
		msg.append(WMS_NO);
		if(StringUtils.isNotBlank(SAP_NO)) {
			msg.append(" SAP凭证:");
			msg.append(SAP_NO);
		}
		
		if(!"".equals(docNo)){
			msg.append(" 进仓单号："+docNo);
		}
		return R.ok(msg.toString());
	}
	
	List<Map<String, Object>> JSONArrayToList(Map<String, Object> params){
		List<Map<String, Object>> retList=new ArrayList<Map<String, Object>>();
		
		JSONArray jarr = JSON.parseArray(params.get("ARRLIST").toString());
		for(int i=0;i<jarr.size();i++){
			Map<String, Object> itemMap=new HashMap<String, Object>();
			JSONObject jsonData=  jarr.getJSONObject(i);
			itemMap.put("INBOUND_NO", jsonData.getString("INBOUND_NO"));
			itemMap.put("INBOUND_ITEM_NO", jsonData.getString("INBOUND_ITEM_NO"));
			itemMap.put("BATCH", jsonData.getString("BATCH"));
			itemMap.put("MATNR", jsonData.getString("MATNR"));
			itemMap.put("MAKTX", jsonData.getString("MAKTX"));
			itemMap.put("LGORT", jsonData.getString("LGORT"));
			itemMap.put("BIN_CODE", jsonData.getString("BIN_CODE"));
			itemMap.put("IN_QTY", jsonData.getString("IN_QTY"));
			itemMap.put("REAL_QTY", jsonData.getString("REAL_QTY"));
			itemMap.put("MAY_IN_QTY", jsonData.getString("MAY_IN_QTY"));
			itemMap.put("UNIT", jsonData.getString("UNIT"));
			itemMap.put("SOBKZ", jsonData.getString("SOBKZ"));
			itemMap.put("LIFNR", jsonData.getString("LIFNR"));
			itemMap.put("LIKTX", jsonData.getString("LIKTX"));
			itemMap.put("WH_MANAGER", jsonData.getString("WH_MANAGER"));
			itemMap.put("ITEM_TEXT", jsonData.getString("ITEM_TEXT"));
			itemMap.put("urgentFlag", jsonData.getString("urgentFlag"));
			itemMap.put("WERKS", jsonData.getString("WERKS"));
			itemMap.put("WH_NUMBER", jsonData.getString("WH_NUMBER"));
			itemMap.put("RECEIPT_NO", jsonData.getString("RECEIPT_NO"));
			itemMap.put("RECEIPT_ITEM_NO", jsonData.getString("RECEIPT_ITEM_NO"));
			itemMap.put("ASNNO",jsonData.getString("ASNNO"));
			itemMap.put("ASNITM",jsonData.getString("ASNITM"));
			itemMap.put("MO_NO", jsonData.getString("MO_NO"));
			itemMap.put("MO_ITEM_NO", jsonData.getString("MO_ITEM_NO"));
			itemMap.put("IO_NO", jsonData.getString("IO_NO"));
			itemMap.put("COST_CENTER", jsonData.getString("COST_CENTER"));
			itemMap.put("SAKTO", jsonData.getString("SAKTO"));
			itemMap.put("ANLN1", jsonData.getString("ANLN1"));
			itemMap.put("WBS", jsonData.getString("WBS"));
			itemMap.put("F_BATCH", jsonData.getString("F_BATCH"));
			itemMap.put("BIN_NAME", jsonData.getString("BIN_NAME"));
			itemMap.put("TXTRULE", jsonData.getString("TXTRULE"));
			itemMap.put("BUSINESS_NAME", jsonData.getString("BUSINESS_NAME"));
			itemMap.put("BUSINESS_TYPE", jsonData.getString("BUSINESS_TYPE"));
			itemMap.put("LABEL_FALG", jsonData.getString("LABEL_FALG"));
			itemMap.put("REF_SAP_MATDOC_YEAR", jsonData.getString("REF_SAP_MATDOC_YEAR"));
			itemMap.put("REF_SAP_MATDOC_NO", jsonData.getString("REF_SAP_MATDOC_NO"));
			itemMap.put("REF_SAP_MATDOC_ITEM_NO", jsonData.getString("REF_SAP_MATDOC_ITEM_NO"));
			itemMap.put("LABEL_NO", jsonData.getString("LABEL_NO"));
			itemMap.put("BIN_CODE_SHELF", jsonData.getString("BIN_CODE_SHELF"));
			itemMap.put("AUTO_PUTAWAY_FLAG", jsonData.getString("AUTO_PUTAWAY_FLAG"));
			/*itemMap.put("LGORT_311", jsonData.getString("LGORT_311"));
			itemMap.put("F_LGORT", jsonData.getString("LGORT_311"));
			itemMap.put("F_WERKS", jsonData.getString("WERKS_311"));
			itemMap.put("F_WH_NUMBER", jsonData.getString("WH_NUMBER_311"));*/
			
			itemMap.put("LGORT_311", jsonData.getString("F_LGORT"));
			itemMap.put("F_LGORT", jsonData.getString("F_LGORT"));
			itemMap.put("F_WERKS", jsonData.getString("F_WERKS"));
			itemMap.put("F_WH_NUMBER", jsonData.getString("F_WH_NUMBER"));
			
			itemMap.put("DESTROY_QTY", jsonData.getString("DESTROY_QTY"));
			itemMap.put("DESTROY_GZ_QTY", jsonData.getString("DESTROY_GZ_QTY"));
			itemMap.put("IQC_COST_CENTER", jsonData.getString("IQC_COST_CENTER"));
			itemMap.put("PSTYP", jsonData.getString("PSTYP"));
			itemMap.put("PZ_DATE", params.get("PZDDT"));
			itemMap.put("JZ_DATE", params.get("JZDDT"));
			itemMap.put("HANDOVER", params.get("HANDOVER"));
			
			itemMap.put("UMREZ", jsonData.getString("UMREZ"));
			itemMap.put("UMREN", jsonData.getString("UMREN"));
			itemMap.put("PO_NO", jsonData.getString("PO_NO"));
			itemMap.put("PO_ITEM_NO", jsonData.getString("PO_ITEM_NO"));
			itemMap.put("SO_NO", jsonData.getString("SO_NO"));
			itemMap.put("SO_ITEM_NO", jsonData.getString("SO_ITEM_NO"));
			
			itemMap.put("EDITOR", params.get("USERNAME").toString());
			itemMap.put("EDIT_DATE", DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
			retList.add(itemMap);
		}
		return retList;
	}
	
	Map<String, Object> updateDestoryQty(Map<String, Object> itemMap){
		Map<String, Object> receiptDestroyMap = new HashMap <String, Object>();
		//如果存在收货单的破坏数量，则按照以下规则重新设置MAY_IN_QTY；
				BigDecimal destroy_qty_d=BigDecimal.ZERO;//破坏数量
				BigDecimal destroy_gz_qty_d=BigDecimal.ZERO;//破坏过账数量
				BigDecimal destroy_qty_sub=BigDecimal.ZERO;
				
				if(itemMap.get("DESTROY_QTY")!=null && !"".equals(itemMap.get("DESTROY_QTY"))){
					
					destroy_qty_d=new BigDecimal(itemMap.get("DESTROY_QTY").toString());
					if(!"".equals(itemMap.get("DESTROY_GZ_QTY"))){
						destroy_gz_qty_d=new BigDecimal(itemMap.get("DESTROY_GZ_QTY").toString());
					}
					
					destroy_qty_sub=destroy_qty_d.subtract(destroy_gz_qty_d);//破坏数量 与 破坏过账数量的差值
					BigDecimal may_in_qty_d=new BigDecimal(itemMap.get("MAY_IN_QTY").toString());
					if(may_in_qty_d.compareTo(destroy_qty_sub)>0){
						itemMap.put("DESTROY_GZ_QTY", destroy_qty_sub);//做201的过账数量
					}
					//增加收货单破坏投料数量
					
					receiptDestroyMap.put("RECEIPT_NO", itemMap.get("RECEIPT_NO"));
					receiptDestroyMap.put("RECEIPT_ITEM_NO", itemMap.get("RECEIPT_ITEM_NO"));
					receiptDestroyMap.put("DESTROY_GZ_QTY", destroy_qty_sub);
				}
		return receiptDestroyMap;
	}
	//收料房数据准备
	Map<String, Object> getRhStockList(String inbound_type,Map<String, Object> itemMap){
		Map<String, Object> rhParamsMap = new HashMap <String, Object>();
		if("00".equals(inbound_type)||"RECEIPT".equals(itemMap.get("LABEL_FALG"))){
			rhParamsMap.put("MAY_IN_QTY", itemMap.get("MAY_IN_QTY"));
			rhParamsMap.put("WERKS", itemMap.get("WERKS"));
			rhParamsMap.put("WH_NUMBER", itemMap.get("WH_NUMBER"));
			rhParamsMap.put("MATNR", itemMap.get("MATNR"));
			rhParamsMap.put("BATCH", itemMap.get("BATCH"));
			//SAP交货单收货业务    20190722 update F_LGORT 是对应的收货单的库位，所以收料房更新都取这个库位
			/*if("10".equals(itemMap.get("BUSINESS_NAME"))&&"06".equals(itemMap.get("BUSINESS_TYPE"))){
				rhParamsMap.put("LGORT", itemMap.get("F_LGORT"));
			}else{
				rhParamsMap.put("LGORT", itemMap.get("LGORT"));
			}*/
			rhParamsMap.put("LGORT", itemMap.get("F_LGORT"));
			
			rhParamsMap.put("SOBKZ", itemMap.get("SOBKZ"));
			rhParamsMap.put("LIFNR", itemMap.get("LIFNR"));
			rhParamsMap.put("EDITOR", itemMap.get("USERNAME"));
			rhParamsMap.put("EDIT_DATE", itemMap.get("EDIT_DATE"));
		}
		return rhParamsMap;
	}
	
	// 收货单数据准备
	Map<String, Object> getReceiptList(List<Map<String, Object>> WmsCoreWhBinList,Map<String, Object> itemMap){
		Map<String, Object> receiptMap = new HashMap <String, Object>();
		receiptMap.put("RECEIPT_NO", itemMap.get("RECEIPT_NO"));
		receiptMap.put("RECEIPT_ITEM_NO", itemMap.get("RECEIPT_ITEM_NO"));
		receiptMap.put("MAY_IN_QTY", itemMap.get("MAY_IN_QTY"));
		
		//判断是否是试装储位
		for(Map<String, Object> coreWhBin:WmsCoreWhBinList){
			if(coreWhBin.get("WH_NUMBER").equals(itemMap.get("WH_NUMBER"))&&
					coreWhBin.get("BIN_CODE").equals(itemMap.get("BIN_CODE"))){//存在试装储位
				if("04".equals(itemMap.get("BIN_TYPE"))){
					receiptMap.put("BIN_CODE", "SZ");
				}else{
					receiptMap.put("BIN_CODE", itemMap.get("BIN_CODE"));
				}
			}else{
				receiptMap.put("BIN_CODE", itemMap.get("BIN_CODE"));
			}
		}
		
		return receiptMap;
	}
	
	Map<String, Object> getInboundQtyList(Map<String, Object> itemMap){
		Map<String, Object> inbounditemqtyMap = new HashMap <String, Object>();
		inbounditemqtyMap.put("INBOUND_NO", itemMap.get("INBOUND_NO"));
		inbounditemqtyMap.put("INBOUND_ITEM_NO", itemMap.get("INBOUND_ITEM_NO"));
		inbounditemqtyMap.put("MAY_IN_QTY", itemMap.get("MAY_IN_QTY"));
		
		return inbounditemqtyMap;
	}
	//处理增加库存的数据
	Map<String, Object> getStockData(Map<String, Object> itemMap){

		//增加库存操作数据准备
		Map<String, Object> matMap = new HashMap <String, Object>();
		
		String meins="";
		Map<String, Object> sapMaterialMap = new HashMap <String, Object>();
		sapMaterialMap.put("WERKS", itemMap.get("WERKS"));
		sapMaterialMap.put("MATNR", itemMap.get("MATNR"));
		List<WmsSapMaterialEntity> sapMateriallist=wmsSapMaterialService.selectByMap(sapMaterialMap);
		if(sapMateriallist.size()>0){//获取基本计量单位
			meins=sapMateriallist.get(0).getMeins();
			matMap.put("MEINS", meins);
		}else{
			//抛出异常 工厂*** 料号*** 物料信息没有同步，请使用物料数据同步功能，同步信息！
			throw new RuntimeException("工厂 "+itemMap.get("WERKS")+" 料号"+itemMap.get("MATNR")+" 物料信息没有同步，请使用物料数据同步功能，同步信息！");
		}
		
		matMap.put("MATNR", itemMap.get("MATNR"));
		matMap.put("UNIT", itemMap.get("UNIT"));
		matMap.put("STOCK_QTY", itemMap.get("MAY_IN_QTY"));
		
		matMap.put("WERKS", itemMap.get("WERKS"));
		matMap.put("WH_NUMBER",itemMap.get("WH_NUMBER"));
		matMap.put("LGORT",itemMap.get("LGORT"));
		matMap.put("MATNR",itemMap.get("MATNR"));
		matMap.put("MAKTX", itemMap.get("MAKTX"));
		matMap.put("F_BATCH", itemMap.get("F_BATCH"));
		matMap.put("BATCH", itemMap.get("BATCH"));
		matMap.put("BIN_CODE", (itemMap.get("BIN_CODE_SHELF")==null||"".equals(itemMap.get("BIN_CODE_SHELF")))?itemMap.get("BIN_CODE"):itemMap.get("BIN_CODE_SHELF"));
		matMap.put("BIN_NAME", itemMap.get("BIN_NAME"));
		matMap.put("SOBKZ", itemMap.get("SOBKZ"));
		matMap.put("LIFNR", itemMap.get("LIFNR"));
		matMap.put("LIKTX", itemMap.get("LIKTX"));
		matMap.put("EDITOR", itemMap.get("EDITOR"));
		matMap.put("EDIT_DATE", itemMap.get("EDIT_DATE"));
		matMap.put("LABEL_NO", itemMap.get("LABEL_NO"));
		
		matMap.put("UMREZ", itemMap.get("UMREZ"));//umrez_str
		matMap.put("UMREN", itemMap.get("UMREN"));//umren_str
		
		matMap.put("SO_NO", itemMap.get("SO_NO"));
		matMap.put("SO_ITEM_NO", itemMap.get("SO_ITEM_NO"));
	
		return matMap;
	}
	//整理过账数据
	List<List<Map<String,Object>>> getsaplistData(List<List<Map<String,Object>>> sapList,Map<String, Object> itemMap,Map<String, Object> checkMapSap){
			
			//
			if(sapList!=null&&sapList.size()>0){//sap过账结果集中有数据
				for(int z=0;z<sapList.size();z++){
					List<Map<String, Object>> tempsaplist=sapList.get(z);//从已经添加的list中取出来
					
					//判断当前cdmap.get("BUSINESS_CLASS")的BUSINESS_CLASS是否和sapList结果集中的一样，是的话就从添加到已有的sapList中
						if(((itemMap.get("BUSINESS_CLASS").toString()).equals(tempsaplist.get(0).get("BUSINESS_CLASS")))
								&&((itemMap.get("BUSINESS_NAME").toString()).equals(tempsaplist.get(0).get("BUSINESS_NAME")))
								&&((itemMap.get("BUSINESS_TYPE").toString()).equals(tempsaplist.get(0).get("BUSINESS_TYPE")))
								&&((itemMap.get("SOBKZ").toString()).equals(tempsaplist.get(0).get("SOBKZ")))
								&&((itemMap.get("LIFNR").toString()).equals(tempsaplist.get(0).get("LIFNR")))){
							
							List<Map<String, Object>> matlistmap=(List<Map<String, Object>>) sapList.get(z).get(0).get("matList");
							matlistmap.add(itemMap);
							sapList.get(z).get(0).put("matList", matlistmap);
							
							break;
						}else{//不是的就新增一个list保存
							if(z==sapList.size()-1){//如果当前行比较的是集合中最后一个，也没有满足的合并的，那么就新增
							List<Map<String, Object>> listsaptempMap1 = new ArrayList<Map<String, Object>>();
							
							List<Map<String, Object>> matlisttemp=new ArrayList<Map<String, Object>>();
							Map<String, Object> saptempmap=new HashMap<String, Object>();
							saptempmap.put("BUSINESS_CLASS", itemMap.get("BUSINESS_CLASS"));
							saptempmap.put("BUSINESS_NAME", itemMap.get("BUSINESS_NAME"));
							saptempmap.put("BUSINESS_TYPE", itemMap.get("BUSINESS_TYPE"));
							saptempmap.put("WERKS", itemMap.get("WERKS"));
							saptempmap.put("SOBKZ", itemMap.get("SOBKZ"));
							saptempmap.put("LIFNR", itemMap.get("LIFNR"));
							
							saptempmap.put("PZ_DATE", itemMap.get("PZ_DATE"));
							saptempmap.put("JZ_DATE", itemMap.get("JZ_DATE"));
							matlisttemp.add(itemMap);
							saptempmap.put("matList", matlisttemp);
							
							if(saptempmap!=null&&saptempmap.size()>0){
							listsaptempMap1.add(saptempmap);
							sapList.add(listsaptempMap1);
							
							}
							break;
							}else{//否则继续比较集合的下一条
								continue;
							}
						}
					
					/*String itemchek=itemMap.get("BUSINESS_CLASS")+"#"+itemMap.get("BUSINESS_NAME")+"#"+itemMap.get("BUSINESS_TYPE")+"#"+itemMap.get("SOBKZ")+"#"+itemMap.get("LIFNR");
					if(checkMapSap.containsKey(itemchek)){
						List<Map<String, Object>> matlistmap=(List<Map<String, Object>>) sapList.get(z).get(0).get("matList");
						matlistmap.add(itemMap);
						sapList.get(z).get(0).put("matList", matlistmap);
						break;
					}else{
						List<Map<String, Object>> listsaptempMap1 = new ArrayList<Map<String, Object>>();
						
						List<Map<String, Object>> matlisttemp=new ArrayList<Map<String, Object>>();
						Map<String, Object> saptempmap=new HashMap<String, Object>();
						saptempmap.put("BUSINESS_CLASS", itemMap.get("BUSINESS_CLASS"));
						saptempmap.put("BUSINESS_NAME", itemMap.get("BUSINESS_NAME"));
						saptempmap.put("BUSINESS_TYPE", itemMap.get("BUSINESS_TYPE"));
						saptempmap.put("WERKS", itemMap.get("WERKS"));
						saptempmap.put("SOBKZ", itemMap.get("SOBKZ"));
						saptempmap.put("LIFNR", itemMap.get("LIFNR"));
						
						saptempmap.put("PZ_DATE", itemMap.get("PZ_DATE"));
						saptempmap.put("JZ_DATE", itemMap.get("JZ_DATE"));
						matlisttemp.add(itemMap);
						saptempmap.put("matList", matlisttemp);
						
						if(saptempmap!=null&&saptempmap.size()>0){
							listsaptempMap1.add(saptempmap);
							sapList.add(listsaptempMap1);
							checkMapSap.put(itemchek, itemchek);
						}
						break;
					}*/
				}
				
				
			}else{//sap过账结果集中没有数据
				List<Map<String, Object>> listsaptempMap1 = new ArrayList<Map<String, Object>>();
				
				List<Map<String, Object>> matlisttemp=new ArrayList<Map<String, Object>>();
				Map<String, Object> saptempmap=new HashMap<String, Object>();
				saptempmap.put("BUSINESS_CLASS", itemMap.get("BUSINESS_CLASS"));
				saptempmap.put("BUSINESS_NAME", itemMap.get("BUSINESS_NAME"));
				saptempmap.put("BUSINESS_TYPE", itemMap.get("BUSINESS_TYPE"));
				saptempmap.put("WERKS", itemMap.get("WERKS"));
				saptempmap.put("SOBKZ", itemMap.get("SOBKZ"));
				saptempmap.put("LIFNR", itemMap.get("LIFNR"));
				
				saptempmap.put("PZ_DATE", itemMap.get("PZ_DATE"));
				saptempmap.put("JZ_DATE", itemMap.get("JZ_DATE"));
				matlisttemp.add(itemMap);
				saptempmap.put("matList", matlisttemp);
				listsaptempMap1.add(saptempmap);
				sapList.add(listsaptempMap1);
				
				//往checkMap赋值，便于验证合并
				/*String itemMap_f=itemMap.get("BUSINESS_CLASS")+"#"+itemMap.get("BUSINESS_NAME")+"#"+itemMap.get("BUSINESS_TYPE")+"#"+itemMap.get("SOBKZ")+"#"+itemMap.get("LIFNR");
				checkMapSap.put(itemMap_f, itemMap_f);*/
				
			}
		
		return sapList;
	}
	
	List<List<Map<String,Object>>> getwmslistdata(List<List<Map<String,Object>>> wmsList,Map<String, Object> itemMap,Map<String, Object> checkMap){
		
		if(wmsList!=null&&wmsList.size()>0){
			//wmsList结果集中有数据
			for(int z=0;z<wmsList.size();z++){
				List<Map<String, Object>> tempwmslist=wmsList.get(z);//从已经添加的list中取出来
				
				//判断当前cdmap.get("BUSINESS_CLASS")的BUSINESS_CLASS等参数是否和wmsList结果集中的一样，是的话就从添加到已有的wmsList中
					if(((itemMap.get("BUSINESS_CLASS").toString()).equals(tempwmslist.get(0).get("BUSINESS_CLASS")))
							&&((itemMap.get("BUSINESS_NAME").toString()).equals(tempwmslist.get(0).get("BUSINESS_NAME")))
							&&((itemMap.get("BUSINESS_TYPE").toString()).equals(tempwmslist.get(0).get("BUSINESS_TYPE")))
							&&((itemMap.get("SOBKZ").toString()).equals(tempwmslist.get(0).get("SOBKZ")))
							&&((itemMap.get("LIFNR").toString()).equals(tempwmslist.get(0).get("LIFNR")))){
						//把当前的取出来，放到已存在的结果集中去
						tempwmslist.add(itemMap);
						
						
						break;
					}else{//不是的就新增一个list保存
					  if(z==wmsList.size()-1){//如果当前行比较的是集合中最后一个，也没有满足的合并的，那么就新增
						List<Map<String, Object>> matlisttemp=new ArrayList<Map<String, Object>>();
						
						matlisttemp.add(itemMap);
						wmsList.add(matlisttemp);//wms事务记录准备
						
						break;
						}else{//否则继续比较集合的下一条
							continue;
						}
					}
				
					/*String itemchek=itemMap.get("BUSINESS_CLASS")+"#"+itemMap.get("BUSINESS_NAME")+"#"+itemMap.get("BUSINESS_TYPE")+"#"+itemMap.get("SOBKZ")+"#"+itemMap.get("LIFNR");
					if(checkMap.containsKey(itemchek)){
						tempwmslist.add(itemMap);
						break;
					}else{
						List<Map<String, Object>> matlisttemp=new ArrayList<Map<String, Object>>();
						
						matlisttemp.add(itemMap);
						wmsList.add(matlisttemp);//wms事务记录准备
						checkMap.put(itemchek, itemchek);
						break;
					}*/
			}
		
		}else{//
			List<Map<String, Object>> matlisttemp=new ArrayList<Map<String, Object>>();
			
			matlisttemp.add(itemMap);
			wmsList.add(matlisttemp);//wms事务记录准备
			//往checkMap赋值，便于验证合并
			/*String itemMap_f=itemMap.get("BUSINESS_CLASS")+"#"+itemMap.get("BUSINESS_NAME")+"#"+itemMap.get("BUSINESS_TYPE")+"#"+itemMap.get("SOBKZ")+"#"+itemMap.get("LIFNR");
			checkMap.put(itemMap_f, itemMap_f);*/
			
		}
		return wmsList;
	}
	
	String gz201(List<List<Map<String,Object>>> sapList){
		//判断是否质检破坏数量 的标志，找出来 产生WMS成本中心投料凭证（201）
		List<List<Map<String,Object>>> sapList_201=new ArrayList<List<Map<String,Object>>>();//成本中心投料凭证（201）sap过账准备的
		for(int k=0;k<sapList.size();k++){
			List<Map<String,Object>> sapmaplist_201=new ArrayList<Map<String,Object>>();
			List<Map<String,Object>> sapmaplist=sapList.get(k);
			for(int h=0;h<sapmaplist.size();h++){
				//重新赋值
				Map<String,Object> sapMap_201=sapmaplist.get(h);
				sapMap_201.put("BUSINESS_CLASS", "07");
				sapMap_201.put("BUSINESS_NAME", "44");
				sapMap_201.put("BUSINESS_TYPE", "14");
				
				List<Map<String,Object>> matList_=(List<Map<String, Object>>) sapmaplist.get(h).get("matList");
				
				List<Map<String,Object>> matList_201=new ArrayList<Map<String,Object>>();
				for(Map<String, Object> matMap:matList_){
					if(matMap.get("DESTROY_FLAG")!=null){
					if("true".equals(matMap.get("DESTROY_FLAG").toString())){
						//重新赋值
						matMap.put("COST_CENTER", matMap.get("COST_CENTER"));
						matMap.put("QTY_WMS", matMap.get("DESTORY_QTY"));
						matMap.put("QTY_SAP", matMap.get("DESTORY_QTY"));
						
						matMap.put("BUSINESS_CLASS", "07");
						matMap.put("BUSINESS_NAME", "44");
						matMap.put("BUSINESS_TYPE", "14");
						
						matMap.put("WMS_MOVE_TYPE", "201");
						matMap.put("SAP_MOVE_TYPE", "201");
						
						matMap.put("PO_NO", null);
						matMap.put("PO_ITEM_NO", null);
						
						
						
						matList_201.add(matMap);
					}
					}
				}
				
				sapMap_201.put("matList", matList_201);
				
				sapmaplist_201.add(sapMap_201);
			}
			sapList_201.add(sapmaplist_201);
			
		}
		String SAP_NO_201="";
		for(int d=0;d<sapList_201.size();d++){
			List<Map<String,Object>> sapmaplist_201=sapList_201.get(d);
			for(int e=0;e<sapmaplist_201.size();e++){
				List<Map<String,Object>> matList_201=(List<Map<String,Object>>) sapmaplist_201.get(e).get("matList");
				if(matList_201!=null&&matList_201.size()>0){
					String SAP_NO_temp=commonService.doSapPost(sapmaplist_201.get(e));
					SAP_NO_201=SAP_NO_201+" "+SAP_NO_temp;
					
				}
				
			}
		}
		
		return SAP_NO_201;
	}
	
	String doc201(List<List<Map<String,Object>>> wmsList,Map<String, Object> params){
		//事务记录处理 用于201
		List<List<Map<String,Object>>> wmsList_201=new ArrayList<List<Map<String,Object>>>();;
		for(int f=0;f<wmsList.size();f++){
			List<Map<String, Object>> matlisttemp_201=new ArrayList<Map<String, Object>>();
			
			List<Map<String, Object>> matlisttemp_=wmsList.get(f);
			for(Map<String, Object> matwmsMap:matlisttemp_){
				if(matwmsMap.get("DESTROY_FLAG")!=null){
				if("true".equals(matwmsMap.get("DESTROY_FLAG").toString())){
					//重新赋值
					matwmsMap.put("COST_CENTER", matwmsMap.get("COST_CENTER"));
					matwmsMap.put("QTY_WMS", matwmsMap.get("DESTORY_QTY"));
					matwmsMap.put("QTY_SAP", matwmsMap.get("DESTORY_QTY"));
					
					matwmsMap.put("BUSINESS_CLASS", "07");
					matwmsMap.put("BUSINESS_NAME", "44");
					matwmsMap.put("BUSINESS_TYPE", "14");
					
					matwmsMap.put("WMS_MOVE_TYPE", "201");
					matwmsMap.put("SAP_MOVE_TYPE", "201");
					
					matwmsMap.put("PO_NO", null);
					matwmsMap.put("PO_ITEM_NO", null);
					
					
					matlisttemp_201.add(matwmsMap);
				}
				}
			}
			wmsList_201.add(matlisttemp_201);
		}
		JSONArray jarr = JSON.parseArray(params.get("ARRLIST").toString());
		Map<String,Object> head_201=new HashMap<String,Object>();
		head_201.put("WERKS", jarr.getJSONObject(0).getString("WERKS"));
		head_201.put("PZ_DATE", params.get("PZDDT"));
		head_201.put("JZ_DATE", params.get("JZDDT"));
		head_201.put("PZ_YEAR", params.get("PZDDT").toString().substring(0,4));
		head_201.put("HEADER_TXT", jarr.getJSONObject(0).getString("TXTRULE"));
		head_201.put("TYPE",  "00");//标准凭证
		head_201.put("CREATOR", params.get("USERNAME").toString());
		head_201.put("CREATE_DATE", DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));//
		//head_201.put("SAP_MOVE_TYPE",  "201");
		String WMS_NO_201="";
		if(wmsList_201!=null&&wmsList_201.size()>0){
			for(int p=0;p<wmsList_201.size();p++){
				List<Map<String, Object>> wmslistret_201=wmsList_201.get(p);
				if(wmslistret_201!=null&&wmslistret_201.size()>0){
					String WMS_NO_temp=commonService.saveWMSDoc(head_201, wmsList_201.get(p));
					WMS_NO_201=WMS_NO_201+" "+WMS_NO_temp;
				}
				
			}
		}
		return WMS_NO_201;
	}
	/*
	 * 库存主键相同的数据合并
	 */
	void handWmsData(List<Map<String,Object>> matList,Map<String, Object> itemMap,Map<String, Object> destoryMapRet,Map<String, Object> checkMapStock){
		Map<String, Object> matMap=new HashMap<String, Object>();
		if(!"".equals(itemMap.get("MATNR"))){//物料号不为空的才处理库存
		 matMap=this.getStockData(itemMap);
		//用于破坏数量
		matMap.put("DESTROY_QTY_SUB", destoryMapRet.get("DESTROY_GZ_QTY"));
		}
		/*String checkItem=matMap.get("WERKS")+"#"+matMap.get("WH_NUMBER")+"#"+matMap.get("MATNR")+"#"+matMap.get("F_BATCH")
				+"#"+matMap.get("BATCH")+"#"+matMap.get("LGORT")+"#"+matMap.get("BIN_CODE")+"#"+matMap.get("LIFNR")
				+"#"+matMap.get("SOBKZ")+"#"+matMap.get("SO_NO")+"#"+matMap.get("SO_ITEM_NO")+"#"+matMap.get("UNIT");*/
		if(matList!=null&&matList.size()>0){
			for(int k=0;k<matList.size();k++){
				//已有的结果集中 下面条件相同的，只是把现有的一行累加数量，不添加新的一行,避免添加到库存表 报错主键唯一异常
				if(matList.get(k).get("WERKS").equals(matMap.get("WERKS"))
						&&matList.get(k).get("WH_NUMBER").equals(matMap.get("WH_NUMBER"))
						&&matList.get(k).get("MATNR").equals(matMap.get("MATNR"))
						&&matList.get(k).get("F_BATCH").equals(matMap.get("F_BATCH"))
						&&matList.get(k).get("BATCH").equals(matMap.get("BATCH"))
						&&matList.get(k).get("LGORT").equals(matMap.get("LGORT"))
						&&matList.get(k).get("BIN_CODE").equals(matMap.get("BIN_CODE"))
						&&matList.get(k).get("LIFNR").equals(matMap.get("LIFNR"))
						&&matList.get(k).get("SOBKZ").equals(matMap.get("SOBKZ"))
						&&matList.get(k).get("SO_NO").equals(matMap.get("SO_NO"))
						&&matList.get(k).get("SO_ITEM_NO").equals(matMap.get("SO_ITEM_NO"))
						&&matList.get(k).get("UNIT").equals(matMap.get("UNIT"))){
					matList.get(k).put("STOCK_QTY", new BigDecimal(matList.get(k).get("STOCK_QTY").toString()).add(new BigDecimal(matMap.get("STOCK_QTY").toString())));
					break;
				}else{//否则还是添加
					if(k==matList.size()-1){//如果是最后一个才添加
					matList.add(matMap);
					break;
					}else{//否则继续比较
						continue;
					}
				}
				/*if(checkMapStock.containsKey(checkItem)){
					matMapTmp.put("STOCK_QTY", new BigDecimal(matMapTmp.get("STOCK_QTY").toString()).add(new BigDecimal(matMap.get("STOCK_QTY").toString())));
					break;
				}else{
					matList.add(matMap);
					checkMapStock.put(checkItem, checkItem);
					break;
				}*/
			}
		}else{
			if(matMap.size()>0){
			matList.add(matMap);
			}
			
			//checkMapStock.put(checkItem, checkItem);
		}
		
	}
	
	/**
	 * 更新头表状态，单独事务处理
	 */
	@Override
	 public Map<String, Object> updateHeadStatus_new(Map<String, Object> params)  {
		JSONArray jarr = JSON.parseArray(params.get("ARRLIST").toString());
		List<Map<String, Object>> inboundheadtatusMapList=new ArrayList<Map<String, Object>>();
		for(int i=0;i<jarr.size();i++){
			JSONObject itemData=  jarr.getJSONObject(i);
			Map<String, Object> inbounditemstatusMap = new HashMap <String, Object>();
			inbounditemstatusMap.put("INBOUND_NO", itemData.getString("INBOUND_NO"));
			inbounditemstatusMap.put("DEL", "0");
			
			List<Map<String, Object>>  retitemstatus=wmsInInboundDao.getItemLs(inbounditemstatusMap);
			List<String> statusList=new ArrayList<String>();
			if(retitemstatus.size()>0){
				for(int m=0;m<retitemstatus.size();m++){
					if(retitemstatus.get(m).get("ITEM_STATUS")!=null){
						statusList.add(retitemstatus.get(m).get("ITEM_STATUS").toString());
					}
				}
			}
			
			if(statusList.size()>0){
				//如果行表状态都是02，则更新头表状态为02
				boolean status_02=true;
				boolean status_00=true;
				for(String sts:statusList){
					if(!"02".equals(sts)){
						status_02=false;
					}
				}
				for(String sts:statusList){
					if(!"00".equals(sts)){
						status_00=false;
					}
				}
				
				Map<String, Object> inboundheadtatusMap = new HashMap <String, Object>();
				inboundheadtatusMap.put("INBOUND_NO", itemData.getString("INBOUND_NO"));
				if(status_02){//全部为02，已完成
					inboundheadtatusMap.put("INBOUND_STATUS", "02");
				}else if(status_02==false&&status_00==false){//不全部为02 也不全部为00，则为部分进仓
					inboundheadtatusMap.put("INBOUND_STATUS", "01");
				}
				inboundheadtatusMapList.add(inboundheadtatusMap);
				
			}
		}
		if(inboundheadtatusMapList.size()>0){
		wmsInInboundDao.updateInboundHeadStatusByList(inboundheadtatusMapList);
		}
		return null;
	}
	
	Map<String, Object> getwhManager(Map<String, Object> param){
		Map<String, Object> retMap=new HashMap<String, Object>();
		List<Map<String,Object>> relatedAreaName=wmsInInboundDao.getRelatedAreaName(param);//根据工厂仓库号查询配置
		if(relatedAreaName!=null&&relatedAreaName.size()>0){
			List<Map<String,Object>> managerList=new ArrayList<Map<String,Object>>();
			if("00".equals(relatedAreaName.get(0).get("MAT_MANAGER_TYPE"))){
				managerList=wmsInInboundDao.getManagerByMaterial(param);
			}else if("20".equals(relatedAreaName.get(0).get("MAT_MANAGER_TYPE"))){
				managerList=wmsInInboundDao.getManagerByLgort(param);
			}else{
				retMap.put("WH_MANAGER", "");
			}
			if(managerList.size()>0){
				retMap.put("WH_MANAGER", managerList.get(0).get("MANAGER"));
			}
		}else{//没有查询到配置
			retMap.put("WH_MANAGER", "");
		}
		return retMap;
	}

	/**
	 * 判断进仓数量和可进仓数量
	 */
	@Override
	public Map<String, Object> labelHandoverValid(Map<String, Object> params) {
		JSONArray jarr = JSON.parseArray(params.get("ARRLIST").toString());
		//列表中的都是同一种标签的
		
		boolean receipt_flag=false;//收货产生的标签
		boolean inbound_flag=false;//自制进仓产生的标签
		
		if(jarr.size()>0){
			if(jarr.getJSONObject(0).getString("RECEIPT_NO")!=null&&jarr.getJSONObject(0).getString("RECEIPT_ITEM_NO")!=null){
				if(!"".equals(jarr.getJSONObject(0).getString("RECEIPT_NO"))&&!"".equals(jarr.getJSONObject(0).getString("RECEIPT_ITEM_NO"))){
					receipt_flag=true;
				}
			} 
			if(jarr.getJSONObject(0).getString("INBOUND_NO")!=null&&jarr.getJSONObject(0).getString("INBOUND_ITEM_NO")!=null){
				if(!"".equals(jarr.getJSONObject(0).getString("INBOUND_NO"))&&!"".equals(jarr.getJSONObject(0).getString("INBOUND_ITEM_NO"))){
					inbound_flag=true;
				}
			}
		}
		
		List<Map<String, Object>> labelMapList=new ArrayList<Map<String, Object>>();//页面传来的标签数据
		for(int i=0;i<jarr.size();i++){
			Map<String, Object> itemMap=new HashMap<String, Object>();
			JSONObject jsonData=  jarr.getJSONObject(i);
			itemMap.put("RECEIPT_NO", jsonData.getString("RECEIPT_NO"));
			itemMap.put("RECEIPT_ITEM_NO", jsonData.getString("RECEIPT_ITEM_NO"));
			itemMap.put("INBOUND_NO", jsonData.getString("INBOUND_NO"));
			itemMap.put("INBOUND_ITEM_NO", jsonData.getString("INBOUND_ITEM_NO"));
			itemMap.put("LABEL_NO", jsonData.getString("LABEL_NO"));
			itemMap.put("MAY_IN_QTY", jsonData.getString("MAY_IN_QTY"));
			labelMapList.add(itemMap);
		}
		
		if(receipt_flag){
			//比较相同收货单号和行项目号 的数量汇总 与 对应收货单号和行项目号的可进仓数量
			Map<String, Object> receiptMap=new HashMap<String, Object>();
			for(Map<String, Object> receiptTemp: labelMapList){//循环页面的数据
				if(receiptMap.size()>0){
					if(receiptMap.containsKey(receiptTemp.get("RECEIPT_NO")+"#"+receiptTemp.get("RECEIPT_ITEM_NO"))){
						//存在相同的收货单号和行项目号,则把页面的MAY_IN_QTY累加
						String may_in_qty_str=(String) receiptMap.get(receiptTemp.get("RECEIPT_NO")+"#"+receiptTemp.get("RECEIPT_ITEM_NO"));
						String may_in_qty_str_f=receiptTemp.get("MAY_IN_QTY").toString();//前台的MAY_IN_QTY
						
						BigDecimal may_in_qty_d=new BigDecimal(may_in_qty_str).add(new BigDecimal(may_in_qty_str_f));
						receiptMap.put(receiptTemp.get("RECEIPT_NO")+"#"+receiptTemp.get("RECEIPT_ITEM_NO"), may_in_qty_d);
					}else{//不相同的RECEIPT_NO RECEIPT_ITEM_NO
						receiptMap.put(receiptTemp.get("RECEIPT_NO")+"#"+receiptTemp.get("RECEIPT_ITEM_NO"), receiptTemp.get("MAY_IN_QTY"));
					}
				}else{//
					receiptMap.put(receiptTemp.get("RECEIPT_NO")+"#"+receiptTemp.get("RECEIPT_ITEM_NO"), receiptTemp.get("MAY_IN_QTY"));
				}
			}
			
			for(String key:receiptMap.keySet()){
				String receipt_may_inqty_str=(String) receiptMap.get(key);
				
				String[] receipt_str_arr=key.split("#");
				Map<String, Object> receiptQuery=new HashMap<String, Object>();
				receiptQuery.put("RECEIPT_NO", receipt_str_arr[0]);
				receiptQuery.put("RECEIPT_ITEM_NO", receipt_str_arr[1]);
				//收货单可进仓数量
				BigDecimal receive_in_qty=BigDecimal.ZERO;
				List<Map<String, Object>> retReceiptQtyList=wmsInInboundDao.getReceiptQtyByReceiveNoAndItemNo(receiptQuery);
				if(retReceiptQtyList!=null&&retReceiptQtyList.size()>0){
					 receive_in_qty=new BigDecimal(retReceiptQtyList.get(0).get("IN_QTY").toString());
				}
				
				if(new BigDecimal(receipt_may_inqty_str).subtract(receive_in_qty).compareTo(BigDecimal.ZERO)>0){
					throw new RuntimeException("收货单号"+receiptQuery.get("RECEIPT_NO")+" 行项目号"+receiptQuery.get("RECEIPT_ITEM_NO")+" 进仓数量不能大于合格数量！");
				}
			}
			
		}
		
		if(inbound_flag){

			//比较相同进仓单号和行项目号 的数量汇总 与 对应进仓单号和行项目号的可进仓数量
			Map<String, Object> inboundMap=new HashMap<String, Object>();
			for(Map<String, Object> inboundTemp: labelMapList){//循环页面的数据
				if(inboundMap.size()>0){
					if(inboundMap.containsKey(inboundTemp.get("INBOUND_NO")+"#"+inboundTemp.get("INBOUND_ITEM_NO"))){
						//存在相同的进仓单号和行项目号,则把页面的MAY_IN_QTY累加
						String may_in_qty_str=(String) inboundMap.get(inboundTemp.get("INBOUND_NO")+"#"+inboundTemp.get("INBOUND_ITEM_NO"));
						String may_in_qty_str_f=inboundTemp.get("MAY_IN_QTY").toString();//前台的MAY_IN_QTY
						
						BigDecimal may_in_qty_d=new BigDecimal(may_in_qty_str).add(new BigDecimal(may_in_qty_str_f));
						inboundMap.put(inboundTemp.get("INBOUND_NO")+"#"+inboundTemp.get("INBOUND_ITEM_NO"), may_in_qty_d.toString());
					}else{//不相同的INBOUND_NO INBOUND_ITEM_NO
						inboundMap.put(inboundTemp.get("INBOUND_NO")+"#"+inboundTemp.get("INBOUND_ITEM_NO"), inboundTemp.get("MAY_IN_QTY"));
					}
				}else{//
					inboundMap.put(inboundTemp.get("INBOUND_NO")+"#"+inboundTemp.get("INBOUND_ITEM_NO"), inboundTemp.get("MAY_IN_QTY"));
				}
			}
			
			for(String key:inboundMap.keySet()){
				String inbound_inqty_str=(String) inboundMap.get(key);
				
				String[] inbound_str_arr=key.split("#");
				Map<String, Object> inboundQuery=new HashMap<String, Object>();
				inboundQuery.put("INBOUND_NO", inbound_str_arr[0]);
				inboundQuery.put("INBOUND_ITEM_NO", inbound_str_arr[1]);
				
				//进仓单合格进仓数量
				BigDecimal inbound_in_qty=BigDecimal.ZERO;
				List<Map<String, Object>> retInboundQtyList=wmsInInboundDao.geInboundQtyByInboundNoAndItemNo(inboundQuery);
				if(retInboundQtyList!=null&&retInboundQtyList.size()>0){
					inbound_in_qty=new BigDecimal(retInboundQtyList.get(0).get("IN_QTY").toString());
				}
				
				if(new BigDecimal(inbound_inqty_str).subtract(inbound_in_qty).compareTo(BigDecimal.ZERO)>0){
					throw new RuntimeException("进仓单号"+inboundQuery.get("INBOUND_NO")+" 行项目号"+inboundQuery.get("INBOUND_ITEM_NO")+" 进仓数量不能大于进仓单可进仓数量！");
				}
			}
		
		}
		return null;
	}

	/**
	 * 查询标签表中是否是303，Z23的移动类型
	 */
	@Override
	public List<Map<String, Object>> getLabelInfoBy303Z23(Map<String, Object> params) {
		List<Map<String, Object>> labellist=new ArrayList<Map<String, Object>>();
		JSONArray jarr = JSON.parseArray(params.get("ARRLIST").toString());
		if(jarr.size()>0){
			for(int i=0;i<jarr.size();i++){
				JSONObject itemData=  jarr.getJSONObject(i);
				Map<String, Object> labelitem = new HashMap <String, Object>();
				labelitem.put("LABEL_NO", itemData.get("LABEL_NO"));
				labellist.add(labelitem);
			}
		}
		
		List<Map<String, Object>> labelretlist=wmsInInboundDao.getLabelInfoBy303Z23(labellist);
		//把页面的数量和库位重新赋值
		if(labelretlist!=null&&labelretlist.size()>0){//前提是页面的label_no是唯一的一行
			for(Map<String, Object> tempMap:labelretlist){
				for(int i=0;i<jarr.size();i++){
					JSONObject itemData=  jarr.getJSONObject(i);
					Map<String, Object> labelitem = new HashMap <String, Object>();
					if(tempMap.get("LABEL_NO").toString().equals(itemData.get("LABEL_NO").toString())){
						//
						tempMap.put("IN_QTY", itemData.get("MAY_IN_QTY"));
						tempMap.put("LGORT", itemData.get("LGORT"));
						tempMap.put("WH_MANAGER", itemData.get("WH_MANAGER"));
						tempMap.put("MEMO", itemData.get("REMARK"));
					}
				}
			}
		}
		return labelretlist;
	}

	@Override
	public String saveinbound303(Map<String, Object> param) {
		List<Map<String,Object>> retmapList = (List<Map<String,Object>>)param.get("retmapList");
		List<Map<String,Object>> inbounditemlist= new ArrayList<Map<String,Object>>();
    	List<Map<String,Object>> itemlist= (List<Map<String,Object>>)param.get("inbounditemlist");
    	String docNo="";
    	
    	if(itemlist!=null&&itemlist.size()>0){
			Map<String, Object> docParma=new HashMap<String, Object>();//WMS_DOC_TYPE
	    	docParma.put("WMS_DOC_TYPE", "07");//进仓单
	    	Map<String, Object> retNo=wmsCDocNoService.getDocNo(docParma);
	    	
	    	if("success".equals(retNo.get("MSG"))){
	    		docNo=retNo.get("docno").toString();
	    	}
    	
    	//1、封装进仓单抬头数据-WMS_IN_INBOUND_HEAD
		
    			Map<String, Object> tempHeadmap=new HashMap();
    			tempHeadmap.put("INBOUND_NO", docNo);
    			tempHeadmap.put("INBOUND_TYPE", "00");//进仓单类型 00 外购进仓单 01 自制进仓单
    			tempHeadmap.put("INBOUN_STATUS", "02");//已完成状态
    			tempHeadmap.put("WERKS", itemlist.get(0).get("WERKS"));
    			tempHeadmap.put("WH_NUMBER", itemlist.get(0).get("WH_NUMBER"));
    			tempHeadmap.put("DEL", "0");
    			tempHeadmap.put("QC_RESULT", "01");//质检合格
    			tempHeadmap.put("IS_AUTO", "0");//后台创建
    			tempHeadmap.put("CREATOR", itemlist.get(0).get("CREATOR"));
    			tempHeadmap.put("CREATE_DATE", DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
    			
    			//2、封装进仓单行项目数据-WMS_IN_INBOUND_ITEM
    			
    			String BUSINESS_CODE="";
    			String BUSINESS_NAME="";
    			String BUSINESS_TYPE="";
    			String SOBKZ=itemlist.get(0).get("SOBKZ")==null?"Z":itemlist.get(0).get("SOBKZ").toString();//库存类型
    			
    			Map<String, Object> businessmap = new HashMap<>();
    			if("303".equals(itemlist.get(0).get("WMS_MOVE_TYPE"))){//对应进仓交接是 305的21
    				businessmap.put("BUSINESS_NAME", "21");
    			}
    			if("Z23".equals(itemlist.get(0).get("WMS_MOVE_TYPE"))){//对应进仓交接是 Z25 的79
    				businessmap.put("BUSINESS_NAME", "79");
    			}
    			
    			businessmap.put("SOBKZ", SOBKZ);
    			List<Map<String, Object>> businesslist=wmsInInboundDao.getBusinessInfo(businessmap);
    			
    			if(businesslist.size()>0){
    				if(businesslist.get(0).get("BUSINESS_CODE")!=null){
    					BUSINESS_CODE=businesslist.get(0).get("BUSINESS_CODE").toString();
    				}
    				if(businesslist.get(0).get("BUSINESS_NAME")!=null){
    					BUSINESS_NAME=businesslist.get(0).get("BUSINESS_NAME").toString();
    				}
    				if(businesslist.get(0).get("BUSINESS_TYPE")!=null){
    					BUSINESS_TYPE=businesslist.get(0).get("BUSINESS_TYPE").toString();
    				}
    			}
    			
    		for(int m=0;m<itemlist.size();m++){

        		Map<String, Object> tempItemmap=new HashMap();
    			tempItemmap.put("INBOUND_NO", docNo);
    			tempItemmap.put("INBOUND_ITEM_NO", m+1);
    			tempItemmap.put("ITEM_STATUS", "02");//已完成状态
    			tempItemmap.put("DEL", "0");
    			tempItemmap.put("WERKS", itemlist.get(m).get("WERKS"));
    			tempItemmap.put("WH_NUMBER", itemlist.get(m).get("WH_NUMBER"));
    			tempItemmap.put("LGORT", itemlist.get(m).get("LGORT"));
    			tempItemmap.put("MATNR", itemlist.get(m).get("MATNR"));
    			tempItemmap.put("MAKTX", itemlist.get(m).get("MAKTX"));
    			tempItemmap.put("BIN_CODE", itemlist.get(m).get("BIN_CODE"));
    			tempItemmap.put("UNIT", itemlist.get(m).get("UNIT"));
    			tempItemmap.put("IN_QTY", itemlist.get(m).get("IN_QTY"));
    			tempItemmap.put("REAL_QTY", itemlist.get(m).get("IN_QTY"));//创建的即是已交接的数量
    			tempItemmap.put("FULL_BOX_QTY", itemlist.get(m).get("FULL_BOX_QTY"));
    			tempItemmap.put("LIFNR", itemlist.get(m).get("LIFNR"));
    			tempItemmap.put("LIKTX", itemlist.get(m).get("LIKTX"));
    			
    			BigDecimal box_count=new BigDecimal(1);//件数
    			
    			BigDecimal entry_qty=BigDecimal.ZERO;
    			String in_qty_str_temp=itemlist.get(m).get("IN_QTY")==null?"0":itemlist.get(m).get("IN_QTY").toString();
    			if(!"".equals(in_qty_str_temp)){
    				entry_qty=new BigDecimal(in_qty_str_temp);
    			}
    			
    			String full_box_qty_str=itemlist.get(m).get("FULL_BOX_QTY")==null?"1":itemlist.get(m).get("FULL_BOX_QTY").toString();
    			BigDecimal fullBoxQty_d=new BigDecimal(full_box_qty_str);
    			if(fullBoxQty_d!=null){
    				box_count=entry_qty.divide(fullBoxQty_d, RoundingMode.UP);//向上取整
    			}
    			tempItemmap.put("BOX_COUNT", box_count);
    			
    			tempItemmap.put("BUSINESS_CODE", BUSINESS_CODE);
    			tempItemmap.put("BUSINESS_NAME", BUSINESS_NAME);
    			tempItemmap.put("BUSINESS_TYPE", BUSINESS_TYPE);
    			tempItemmap.put("SOBKZ", SOBKZ);
        				
    			tempItemmap.put("SAP_MATDOC_NO", itemlist.get(m).get("SAP_MATDOC_NO"));
    			tempItemmap.put("SAP_MATDOC_ITEM_NO", itemlist.get(m).get("SAP_MATDOC_ITEM_NO"));
    			
    			tempItemmap.put("F_WERKS", itemlist.get(m).get("F_WERKS"));
    			tempItemmap.put("F_WH_NUMBER", itemlist.get(m).get("F_WH_NUMBER"));
    			tempItemmap.put("F_LGORT", itemlist.get(m).get("F_LGORT"));
    			tempItemmap.put("LABEL_NO", itemlist.get(m).get("LABEL_NO"));
    			
    			tempItemmap.put("CREATOR", itemlist.get(m).get("CREATOR"));
    			tempItemmap.put("CREATE_DATE", DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
        		
    			tempItemmap.put("DANGER_FLAG", "0");
    			tempItemmap.put("RECEIPT_DATE", DateUtils.format(new Date(),DateUtils.DATE_PATTERN));
    			
    			tempItemmap.put("BATCH", retmapList.get(m).get("BATCH"));//
    			tempItemmap.put("F_BATCH", retmapList.get(m).get("F_BATCH"));//
    			
    			
    			
    			inbounditemlist.add(tempItemmap);
        				
    		}
    		
    		//3、获取推荐上架的储位 start
    		warehouseTasksService.searchBinForPutaway(inbounditemlist);
    		
    		//报错字符串
    		StringBuffer err_bf = new StringBuffer();
    		for (int i=0;i<inbounditemlist.size();i++) {
    			String BIN_CODE_SHELF="";
    			if(inbounditemlist.get(i).get("BIN_CODE_SHELF")!=null){
    				BIN_CODE_SHELF=inbounditemlist.get(i).get("BIN_CODE_SHELF").toString();
    			}
    			String BIN_CODE="";
    			if(inbounditemlist.get(i).get("BIN_CODE")!=null){
    				BIN_CODE=inbounditemlist.get(i).get("BIN_CODE").toString();
    			}
    			
    			if("".equals(BIN_CODE_SHELF)||"".equals(BIN_CODE)){//BIN_CODE_SHELF或者BIN_CODE为空报错
    				err_bf.append(" 第 "+inbounditemlist.get(i).get("RN")+" 行,  物料号为 "+inbounditemlist.get(i).get("MATNR")+"的数据， 获取推荐的储位为空，不能创建进仓单！");
    			}
    			
    			inbounditemlist.get(i).put("LABEL_STATUS", "03");	//ModBy:YK190528 BUG530:生成的 标签状态 是00  应该是03；
    			
    			if(!"".equals(BIN_CODE_SHELF)&&!"".equals(BIN_CODE)&&(BIN_CODE_SHELF.equals(BIN_CODE))){
    				//BIN_CODE和BIN_CODE_SHELF相等，则AUTO_PUTAWAY_FLAG表示为自动上架
    				inbounditemlist.get(i).put("AUTO_PUTAWAY_FLAG", "X");
    			}else{
    				inbounditemlist.get(i).put("AUTO_PUTAWAY_FLAG", "0");
    			}
    			
    		}
    		if(err_bf.toString().length()>0) {
    			throw new RuntimeException(err_bf.toString());
    		}
    		
    		
    		//更新标签
        	
        	wmsInInboundDao.updateLabelInboundByList(inbounditemlist);
        	//保存进仓单
        	wmsInInboundDao.insertWmsInboundHead(tempHeadmap);
    		wmsInInboundDao.insertWmsInboundItem(inbounditemlist);
    	}

		return docNo;
	}
	
	
}


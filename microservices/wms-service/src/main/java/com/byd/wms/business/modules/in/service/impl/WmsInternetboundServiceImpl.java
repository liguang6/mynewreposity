package com.byd.wms.business.modules.in.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.byd.utils.DateUtils;
import com.byd.wms.business.modules.common.service.WarehouseTasksService;
import com.byd.wms.business.modules.common.service.WmsCDocNoService;
import com.byd.wms.business.modules.common.service.WmsCoreMatBatchService;
import com.byd.wms.business.modules.common.service.WmsSapRemote;
import com.byd.wms.business.modules.config.entity.WmsCMatStorageEntity;
import com.byd.wms.business.modules.config.entity.WmsCMatUrgentEntity;
import com.byd.wms.business.modules.config.entity.WmsCPlant;
import com.byd.wms.business.modules.config.entity.WmsCWhEntity;
import com.byd.wms.business.modules.config.entity.WmsSapMaterialEntity;
import com.byd.wms.business.modules.config.entity.WmsSapVendor;
import com.byd.wms.business.modules.config.service.WmsCMatStorageService;
import com.byd.wms.business.modules.config.service.WmsCMatUrgentService;
import com.byd.wms.business.modules.config.service.WmsCPlantService;
import com.byd.wms.business.modules.config.service.WmsCWhService;
import com.byd.wms.business.modules.config.service.WmsSapMaterialService;
import com.byd.wms.business.modules.config.service.WmsSapVendorService;
import com.byd.wms.business.modules.in.dao.WmsInInboundDao;
import com.byd.wms.business.modules.in.dao.WmsInReceiptDao;
import com.byd.wms.business.modules.in.entity.WmsInReceiptEntity;
import com.byd.wms.business.modules.in.service.WmsInternetboundService;

/** 
 * @author 作者 E-mail: peng.tao1@byd.com
 * @version 创建时间：2018年9月21日 上午10:40:11 
 * 类说明 
 */
@Service
public class WmsInternetboundServiceImpl implements WmsInternetboundService {
	@Autowired
	private WmsSapMaterialService wmsSapMaterialService;
	@Autowired
	private WmsInInboundDao wmsInInboundDao;
	@Autowired
	private WmsSapVendorService vendorservice;
	@Autowired
	private WmsCMatStorageService matstorageservice;
	@Autowired
    WmsCDocNoService wmsCDocNoService;
    @Autowired
	private WmsInReceiptDao wmsInReceiptDao;
    @Autowired
	private WmsCMatUrgentService wmsCMatUrgentService;
    @Autowired
    private WmsCPlantService wmsCPlantService;
    @Autowired
	private WmsSapRemote wmsSapRemote;
    @Autowired 
    private WmsCWhService wmsCWhService;
    @Autowired
	private WarehouseTasksService warehouseTasksService;
	
	public List<Map<String,Object>> trunedResult303(Map<String,Object> result){
		
		List<Map<String,Object>> retMapList=new ArrayList<Map<String,Object>>();
		
		List<Map<String,Object>> itemmaplist=(List<Map<String, Object>>) result.get("GOODSMVT_ITEMS");
		
		Map<String,Object> retMsg=new HashMap<String,Object>();
		if(itemmaplist!=null&&itemmaplist.size()>0){
			if(!"303".equals(itemmaplist.get(0).get("MOVE_TYPE")) && !"Z23".equals(itemmaplist.get(0).get("MOVE_TYPE"))){
				retMsg.put("retMsg", "不是有效的303凭证，不能进仓!");
				retMapList.add(retMsg);
				return retMapList;
			}
			
			boolean iswerks=false;
			for(int i=0;i<itemmaplist.size();i++){
				Map<String,Object> item=itemmaplist.get(i);
				
				if(item.get("PLANT")!=null){//读取返回的结果里面每一行的工厂
					String werks=item.get("PLANT").toString();
					if(result.get("recWerks").toString().equals(werks)){//页面工厂在返回的行项目里面存在
						iswerks=true;
					}
				}
			}
			
			if(!iswerks){
				retMsg.put("retMsg", "调入工厂为"+result.get("recWerks").toString()+"与凭证工厂不一致,不能进仓！");
				retMapList.add(retMsg);
				return retMapList;
			}
			
			for(int j=0;j<itemmaplist.size();j++){
				Map<String,Object> retMap=new HashMap<String,Object>();
				
				Map<String,Object> item=itemmaplist.get(j);
				
				//调入数量  凭证数量-（汇总对应进仓单数量：状态为创建，部分进仓 已完成  取进仓数量，关闭状态取已进仓数量，删除状态的不需要取值）
				Map<String,Object> qtymap=new HashMap<String,Object>();
				qtymap.put("SAP_MATDOC_NO", item.get("MAT_DOC"));
				qtymap.put("SAP_MATDOC_ITEM_NO", item.get("MATDOC_ITM"));
				
				Map<String,Object> retqty=wmsInInboundDao.getEntryQty(qtymap);
				BigDecimal entry_qty_has=BigDecimal.ZERO;//状态为创建，部分进仓 已完成  取进仓数量，关闭状态取已进仓数量
				if(retqty!=null){
					String qty_str=retqty.get("QTY").toString();
					
					if(qty_str!=null){
						entry_qty_has=new BigDecimal(qty_str);
					}
				}
				BigDecimal entry_qty=BigDecimal.ZERO;
				if(item.get("ENTRY_QNT")!=null){
					entry_qty=(new BigDecimal(item.get("ENTRY_QNT").toString())).subtract(entry_qty_has);
					if(entry_qty.compareTo(BigDecimal.ZERO)<=0){
						/*retMap.put("retMsg", "凭证号："+item.get("MAT_DOC")+"  行项目号："+item.get("MATDOC_ITM")+"已经进仓！");
						return retMap;*/
						continue;
					}
				}
				
				String werks=item.get("PLANT").toString();
				//正常返回数据准备
			if(!result.get("recWerks").toString().equals(werks)){
				retMap.put("MAT_DOC", item.get("MAT_DOC"));//凭证号
				retMap.put("MATDOC_ITM", item.get("MATDOC_ITM"));//行项目号
				retMap.put("MATERIAL", item.get("MATERIAL"));//物料号
				//物料描述
				Map<String,Object> matMap=new HashMap<String,Object>();
				matMap.put("matnr", item.get("MATERIAL"));
				matMap.put("werks", result.get("recWerks").toString());
				List<WmsSapMaterialEntity> matlist=wmsSapMaterialService.selectByMap(matMap);
				if(matlist!=null&&matlist.size()>0){
					retMap.put("MATERIALDESC", matlist.get(0).getMaktx());
				}else{
					retMsg.put("retMsg", "工厂 "+result.get("recWerks").toString()+" 物料号 "+item.get("MATERIAL")+" 不存在!请使用物料数据同步SAP功能同步");
					retMapList.add(retMsg);
					continue;
				}
				
				//判断核销
				//首先判断调入工厂是否启用核销
				String hx_sy=""; // 为空，核销表没有记录，1：303虚收剩余核销数量大于0，0：303虚收剩余核销数量等于0
				Map<String, Object> hxparams=new HashMap<String, Object>();
				hxparams.put("werks", result.get("recWerks"));
				hxparams.put("del_flag", "0");
				
				List<WmsCPlant> cplantList=wmsCPlantService.selectByMap(hxparams);
				String hx_flag="0";
				if(cplantList.size()>0){
					 hx_flag=cplantList.get(0).getHxFlag();
				}
				if("X".equals(hx_flag)){//启用核销
					//检查 WMS_HX_TO中的HX_QTY_XS  303虚收剩余核销数量
					Map<String, Object> hxsyparams=new HashMap<String, Object>();
					hxsyparams.put("MAT_DOC", item.get("MAT_DOC"));
					hxsyparams.put("MAT_DOC_ITM", item.get("MATDOC_ITM"));
					List<Map<String,Object>> hxsylist=wmsInInboundDao.getHxInfo(hxsyparams);
					if(hxsylist.size()>0){
						BigDecimal hsxs=BigDecimal.ZERO;
						if(hxsylist.get(0).get("HX_QTY_XS")!=null){
							hsxs=new BigDecimal(hxsylist.get(0).get("HX_QTY_XS").toString());
						}
						
						if(hsxs.compareTo(BigDecimal.ZERO)>0){
							hx_sy="1";
							
						}
						if(hsxs.compareTo(BigDecimal.ZERO)==0){
							hx_sy="0";//  虚收数量等于0的要重新计算调入数量
							Map<String, Object> receiptParam=new HashMap<String, Object>();//
							receiptParam.put("SAP_303_NO", item.get("MAT_DOC"));
							receiptParam.put("SAP_303_ITEM_NO", item.get("MATDOC_ITM"));
							List<WmsInReceiptEntity> inReceiptList=wmsInReceiptDao.selectByMap(receiptParam);
							if(inReceiptList.size()>0){
								BigDecimal receiptQty_d=BigDecimal.ZERO;
								if(inReceiptList.get(0).getReceiptQty()!=null){
									receiptQty_d=new BigDecimal(inReceiptList.get(0).getReceiptQty());
								}
								BigDecimal returnQty_d=BigDecimal.ZERO;
								if(inReceiptList.get(0).getReturnQty()!=null){
									returnQty_d=new BigDecimal(inReceiptList.get(0).getReturnQty());
								}
								entry_qty=entry_qty.subtract(receiptQty_d.subtract(returnQty_d));
							}
						}
					}
				}
				
				retMap.put("HX_XS", hx_sy);//核销虚收
				//
				
				retMap.put("ENTRY_QNT", entry_qty);
				//进仓数量
				retMap.put("IN_QTY", entry_qty);
				retMap.put("UNIT", item.get("ENTRY_UOM"));
				//装箱数量
				
				//首先获取满箱数量
				BigDecimal fullBoxQty_d=null;
				Map<String,Object> mattem=new HashMap<String,Object>();
				mattem.put("WERKS", result.get("recWerks").toString());
				mattem.put("MATNR", item.get("MATERIAL"));
				//List<Map<String,Object>> matlilist=wmsInInboundDao.getWmsCMatLtSample(mattem);
				
				// new 
				mattem.put("PACKAGE_TYPE", "01");
				List<Map<String,Object>> matlilist_new=wmsInInboundDao.getWmsCMatPackageHead(mattem);
				
				if(matlilist_new!=null&&matlilist_new.size()>0){
					Object fullBoxQty=matlilist_new.get(0).get("FULL_BOX_QTY");
					if(fullBoxQty!=null){
						fullBoxQty_d=new BigDecimal(fullBoxQty.toString());
					}
				}
				
				BigDecimal box_count=new BigDecimal(1);//件数
				
				/*BigDecimal entry_qty=new BigDecimal(1);
				if(item.get("ENTRY_QNT")!=null){//默认等于进仓数量
					entry_qty=new BigDecimal(item.get("ENTRY_QNT").toString());
				}*/
				if(fullBoxQty_d!=null){
					box_count=entry_qty.divide(fullBoxQty_d, RoundingMode.UP);//向上取整
				}else{
					fullBoxQty_d=entry_qty;
				}
				
				retMap.put("FULL_BOX_QTY", fullBoxQty_d);
				retMap.put("BOX_COUNT", box_count.setScale(0, RoundingMode.UP));
				retMap.put("BEDNR", result.get("BEDNR"));//需求跟踪号
				
				retMap.put("PLANT", item.get("PLANT"));//调出工厂
				retMap.put("STGE_LOC", item.get("STGE_LOC"));//调出库位
				
				//验证供应商编码是否存在
				/*if(!"".equals(result.get("VENDORCODE"))){
					Map<String,Object> vendormap=new HashMap<String,Object>();
					vendormap.put("lifnr", result.get("VENDORCODE"));
					List<WmsSapVendor> vendorretlist=vendorservice.selectByMap(vendormap);
					if(vendorretlist!=null&&vendorretlist.size()>0){
						retMap.put("LIFNR", vendorretlist.get(0).getLifnr());//
						retMap.put("LIKTX", vendorretlist.get(0).getName1());//
					}else{
						retMsg.put("retMsg", "输入的供应商代码不存在!");
						retMapList.add(retMsg);
						return retMapList;
					}
				}*/
				//查询供应商
				if(!"".equals(item.get("PLANT"))){
				Map<String,Object> vendormap=new HashMap<String,Object>();
				vendormap.put("lifnr", "VBYD"+item.get("PLANT"));
				List<WmsSapVendor> vendorretlist=vendorservice.selectByMap(vendormap);
				if(vendorretlist!=null&&vendorretlist.size()>0){
					retMap.put("LIFNR", vendorretlist.get(0).getLifnr());//
					retMap.put("LIKTX", vendorretlist.get(0).getName1());//
				}else{
					retMsg.put("retMsg", "供应商不存在!");
					retMapList.add(retMsg);
					return retMapList;
				}
				}
				
				retMap.put("BEDNR", result.get("BEDNR"));//需求跟踪号
				
				retMap.put("RECWERKS", result.get("recWerks"));//接收工厂
				retMap.put("WHNUMBER", result.get("whNumber"));//接收仓库号
				retMap.put("RLGORT", result.get("rLgort"));//接收库位
				retMap.put("PRDDT", result.get("prddt"));//生产日期
				//获取储位
				retMap.put("BIN_CODE", "");//储位   在创建进仓单的时候获取
				//仓管员
				Map<String,Object> whmap=new HashMap<String,Object>();
				whmap.put("WERKS", result.get("recWerks").toString());
				whmap.put("WHNUMBER", result.get("whNumber").toString());
				whmap.put("MATNR", item.get("MATERIAL"));
				whmap.put("LGORT", result.get("rLgort"));
				Map<String,Object> retWhMap=getwhManager(whmap);
				retMap.put("WH_MANAGER", retWhMap.get("WH_MANAGER"));
				
				//进仓单类型
				retMap.put("INBOUND_TYPE", result.get("inbound_type").toString());
				//批次
				retMap.put("BATCH", item.get("BATCH"));
				//紧急物料
				Map<String,Object> urgentmap=new HashMap<String,Object>();
				urgentmap.put("WERKS", result.get("recWerks").toString());
				urgentmap.put("MATNR", item.get("MATERIAL"));
				List<WmsCMatUrgentEntity> returgentlist=wmsCMatUrgentService.selectByMap(urgentmap);
				if(returgentlist.size()>0){
					retMap.put("urgentFlag", "Y");
				}
				
				
				
				retMapList.add(retMap);
				
				}
				
				
			}
			
		}else{
			retMsg.put("retMsg", "不存在，请检查是否输入有误!");
			retMapList.add(retMsg);
			return retMapList;
		}
		
		return retMapList;
	}

	@SuppressWarnings("finally")
	@Override
	@Transactional
	public String saveInInternetbound(Map<String, Object> params) {
		List<Map<String,Object>> retmapList = (List<Map<String,Object>>)params.get("retmapList");
		
		Map<String, Object> docParma=new HashMap<String, Object>();//WMS_DOC_TYPE
    	docParma.put("WMS_DOC_TYPE", "07");//进仓单
    	Map<String, Object> retNo=wmsCDocNoService.getDocNo(docParma);
    	String docNo="";
    	if("success".equals(retNo.get("MSG"))){
    		docNo=retNo.get("docno").toString();
    	}
    	
    	// 获取前台传输的待进仓行并转化成json数据  ARRLIST
    	//JSONArray jarr = JSON.parseArray(params.get("ARRLIST").toString());
    	JSONArray jarr=(JSONArray) params.get("jarr");
    			
		//1、封装进仓单抬头数据-WMS_IN_INBOUND_HEAD
		
		Map<String, Object> tempHeadmap=new HashMap();
		tempHeadmap.put("INBOUND_NO", docNo);
		tempHeadmap.put("INBOUND_TYPE", "01");//进仓单类型 00 外购进仓单 01 自制进仓单
		tempHeadmap.put("INBOUN_STATUS", "00");//创建状态
		tempHeadmap.put("WERKS", jarr.getJSONObject(0).getString("RECWERKS")==null?"":jarr.getJSONObject(0).getString("RECWERKS"));
		tempHeadmap.put("WH_NUMBER", jarr.getJSONObject(0).getString("WHNUMBER")==null?"":jarr.getJSONObject(0).getString("WHNUMBER"));
		tempHeadmap.put("DEL", "0");
		tempHeadmap.put("IS_AUTO", "0");//后台创建
		tempHeadmap.put("CREATOR", params.get("USERNAME").toString());
		tempHeadmap.put("CREATE_DATE", DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
		
		params.put("tempHeadmap", tempHeadmap);
    			
    	//2、封装进仓单行项目数据-WMS_IN_INBOUND_ITEM
    			
		String BUSINESS_CODE="";
		String BUSINESS_NAME="";
		String BUSINESS_TYPE="";
		String SOBKZ=jarr.getJSONObject(0).getString("SOBKZ")==null?"Z":jarr.getJSONObject(0).getString("SOBKZ").toString();//库存类型
		
		Map<String, Object> businessmap = new HashMap<>();
		businessmap.put("BUSINESS_NAME", jarr.getJSONObject(0).getString("INBOUND_TYPE"));
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
    	//副产品核销信息
    	List<Map<String, Object>> HXMOcomplist=new ArrayList<Map<String, Object>>();
    	//成品核销信息
    	List<Map<String, Object>> HXMOitemlist=new ArrayList<Map<String, Object>>();
    	//进仓单行项目清单
    	List<Map<String, Object>> itemlist = new ArrayList<Map<String, Object>>();
    	//解析json数据数据
    	for(int i=0;i<jarr.size();i++){
			//判断销售订单号，对SOBKZ重新赋值
			//生产订单101  生产订单531
			if("11".equals(jarr.getJSONObject(i).getString("INBOUND_TYPE"))||"13".equals(jarr.getJSONObject(i).getString("INBOUND_TYPE"))){
				if(jarr.getJSONObject(i).getString("KDAUF")!=null&&!"".equals(jarr.getJSONObject(i).getString("KDAUF"))){//销售订单有数据
					SOBKZ="E";
				}
			}
    				
    		Map<String, Object> tempItemmap=new HashMap();
			tempItemmap.put("INBOUND_NO", docNo);
			tempItemmap.put("INBOUND_ITEM_NO", i+1);
			tempItemmap.put("ITEM_STATUS", "00");//创建状态
			tempItemmap.put("DEL", "0");
			tempItemmap.put("WERKS", jarr.getJSONObject(i).getString("RECWERKS"));
			tempItemmap.put("WH_NUMBER", jarr.getJSONObject(i).getString("WHNUMBER"));
			tempItemmap.put("LGORT", jarr.getJSONObject(i).getString("RLGORT"));
			tempItemmap.put("MATNR", jarr.getJSONObject(i).getString("MATERIAL"));
			tempItemmap.put("MAKTX", jarr.getJSONObject(i).getString("MATERIALDESC"));
			tempItemmap.put("BIN_CODE", jarr.getJSONObject(i).getString("BIN_CODE")==null?"":jarr.getJSONObject(i).getString("BIN_CODE"));
			tempItemmap.put("UNIT", jarr.getJSONObject(i).getString("UNIT")==null?"":jarr.getJSONObject(i).getString("UNIT"));
			tempItemmap.put("IN_QTY", jarr.getJSONObject(i).getString("IN_QTY")==null?"":jarr.getJSONObject(i).getString("IN_QTY"));
			tempItemmap.put("FULL_BOX_QTY", jarr.getJSONObject(i).getString("FULL_BOX_QTY")==null?"":jarr.getJSONObject(i).getString("FULL_BOX_QTY"));
			tempItemmap.put("BOX_COUNT", jarr.getJSONObject(i).getString("BOX_COUNT")==null?"":jarr.getJSONObject(i).getString("BOX_COUNT"));
			tempItemmap.put("WH_MANAGER", jarr.getJSONObject(i).getString("WH_MANAGER")==null?"":jarr.getJSONObject(i).getString("WH_MANAGER"));
			tempItemmap.put("LIFNR", jarr.getJSONObject(i).getString("LIFNR")==null?"":jarr.getJSONObject(i).getString("LIFNR"));
			tempItemmap.put("LIKTX", jarr.getJSONObject(i).getString("LIKTX")==null?"":jarr.getJSONObject(i).getString("LIKTX"));
			tempItemmap.put("PRODUCT_DATE", jarr.getJSONObject(i).getString("PRDDT")==null?"":jarr.getJSONObject(i).getString("PRDDT"));
			
			tempItemmap.put("BUSINESS_CODE", BUSINESS_CODE);
			tempItemmap.put("BUSINESS_NAME", BUSINESS_NAME);
			tempItemmap.put("BUSINESS_TYPE", BUSINESS_TYPE);
			tempItemmap.put("SOBKZ", SOBKZ);
    				
			tempItemmap.put("SAP_MATDOC_NO", jarr.getJSONObject(i).getString("MAT_DOC")==null?"":jarr.getJSONObject(i).getString("MAT_DOC"));
			tempItemmap.put("SAP_MATDOC_ITEM_NO", jarr.getJSONObject(i).getString("MATDOC_ITM")==null?"":jarr.getJSONObject(i).getString("MATDOC_ITM"));
			
			tempItemmap.put("CREATOR", params.get("USERNAME").toString());
			tempItemmap.put("CREATE_DATE", DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
    				
			tempItemmap.put("BEDNR", jarr.getJSONObject(i).getString("BEDNR")==null?"":jarr.getJSONObject(i).getString("BEDNR"));
			tempItemmap.put("MEMO", jarr.getJSONObject(i).getString("REMARK")==null?"":jarr.getJSONObject(i).getString("REMARK"));
			tempItemmap.put("CUSTOMER_MATNR", jarr.getJSONObject(i).getString("CUSTOMER_MACD")==null?"":jarr.getJSONObject(i).getString("CUSTOMER_MACD"));
			
			tempItemmap.put("MO_NO", jarr.getJSONObject(i).getString("MO_NO")==null?"":jarr.getJSONObject(i).getString("MO_NO"));
			tempItemmap.put("MO_ITEM_NO", jarr.getJSONObject(i).getString("MO_ITEM_NO")==null?"":jarr.getJSONObject(i).getString("MO_ITEM_NO"));
			//内部/生产订单号
			tempItemmap.put("IO_NO", jarr.getJSONObject(i).getString("IO_NO")==null?"":jarr.getJSONObject(i).getString("IO_NO"));
			//成本中心号
			tempItemmap.put("COST_CENTER", jarr.getJSONObject(i).getString("COST_CENTER")==null?"":jarr.getJSONObject(i).getString("COST_CENTER"));
			//WBS元素号
			tempItemmap.put("WBS", jarr.getJSONObject(i).getString("WBS")==null?"":jarr.getJSONObject(i).getString("WBS"));
			
			//车间
			tempItemmap.put("WORKSHOP", jarr.getJSONObject(i).getString("WORKSHOP")==null?"":jarr.getJSONObject(i).getString("WORKSHOP"));
			//班次
			tempItemmap.put("WORKGROUP_NO", jarr.getJSONObject(i).getString("WORKGROUP_NO")==null?"":jarr.getJSONObject(i).getString("WORKGROUP_NO"));
			//车型
			tempItemmap.put("CAR_TYPE", jarr.getJSONObject(i).getString("CAR_TYPE")==null?"":jarr.getJSONObject(i).getString("CAR_TYPE"));
			//模具编号
			tempItemmap.put("MOULD_NO", jarr.getJSONObject(i).getString("MOULD_NO")==null?"":jarr.getJSONObject(i).getString("MOULD_NO"));
			
			//作业员
			tempItemmap.put("OPERATOR", jarr.getJSONObject(i).getString("ZYY")==null?"":jarr.getJSONObject(i).getString("ZYY"));
			//物流器具
			tempItemmap.put("LT_WARE", jarr.getJSONObject(i).getString("LT_WARE")==null?"":jarr.getJSONObject(i).getString("LT_WARE"));
			//产品图号
			tempItemmap.put("DRAWING_NO", jarr.getJSONObject(i).getString("CPTH")==null?"":jarr.getJSONObject(i).getString("CPTH"));
			//工位
			tempItemmap.put("PRO_STATION", jarr.getJSONObject(i).getString("DIS_STATION")==null?"":jarr.getJSONObject(i).getString("DIS_STATION"));
			tempItemmap.put("PO_NO", jarr.getJSONObject(i).getString("PO_NO")==null?"":jarr.getJSONObject(i).getString("PO_NO"));
			tempItemmap.put("PO_ITEM_NO", jarr.getJSONObject(i).getString("PO_ITEM_NO")==null?"":jarr.getJSONObject(i).getString("PO_ITEM_NO"));
			
			tempItemmap.put("SO_NO", jarr.getJSONObject(i).getString("KDAUF")==null?"":jarr.getJSONObject(i).getString("KDAUF"));
			tempItemmap.put("SO_ITEM_NO", jarr.getJSONObject(i).getString("KDPOS")==null?"":jarr.getJSONObject(i).getString("KDPOS"));
			tempItemmap.put("RN", jarr.getJSONObject(i).getString("RN")==null?"":jarr.getJSONObject(i).getString("RN"));
			
			tempItemmap.put("DANGER_FLAG", "0");
			tempItemmap.put("RECEIPT_DATE", DateUtils.format(new Date(),DateUtils.DATE_PATTERN));
			
			tempItemmap.put("BATCH", retmapList.get(i).get("BATCH"));//SAP返回的批次
			tempItemmap.put("F_BATCH", retmapList.get(i).get("BATCH"));//SAP返回的批次
			
			itemlist.add(tempItemmap);
    				
			//更新核销行表准备数据
			if(("61".equals(BUSINESS_NAME))&&("11".equals(BUSINESS_TYPE))){
				Map<String, Object> hxitemmap=new HashMap<String, Object>();
				hxitemmap.put("AUFNR", tempItemmap.get("IO_NO"));
				hxitemmap.put("POSNR", jarr.getJSONObject(i).getString("POSNR"));
				hxitemmap.put("IN_QTY", jarr.getJSONObject(i).getString("IN_QTY"));
				hxitemmap.put("EDITOR", params.get("USERNAME").toString());
				hxitemmap.put("EDIT_DATE", DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
				
				HXMOitemlist.add(hxitemmap);
			}
    				
			//更新核销组件表准备数据
			if(("63".equals(BUSINESS_NAME))&&("11".equals(BUSINESS_TYPE))){
				Map<String, Object> hxcompmap=new HashMap<String, Object>();
				hxcompmap.put("AUFNR", tempItemmap.get("IO_NO"));
				hxcompmap.put("RSNUM", jarr.getJSONObject(i).getString("RSNUM"));
				hxcompmap.put("RSPOS", jarr.getJSONObject(i).getString("RSPOS"));
				hxcompmap.put("IN_QTY", jarr.getJSONObject(i).getString("IN_QTY"));
				hxcompmap.put("EDITOR", params.get("USERNAME").toString());
				hxcompmap.put("EDIT_DATE", DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
				
				HXMOcomplist.add(hxcompmap);
			}
    	}

		
		//4、获取推荐上架的储位 start
		warehouseTasksService.searchBinForPutaway(itemlist);
		
		//报错字符串
		StringBuffer err_bf = new StringBuffer();
		for (int i=0;i<itemlist.size();i++) {
			String BIN_CODE_SHELF="";
			if(itemlist.get(i).get("BIN_CODE_SHELF")!=null){
				BIN_CODE_SHELF=itemlist.get(i).get("BIN_CODE_SHELF").toString();
			}
			String BIN_CODE="";
			if(itemlist.get(i).get("BIN_CODE")!=null){
				BIN_CODE=itemlist.get(i).get("BIN_CODE").toString();
			}
			
			if("".equals(BIN_CODE_SHELF)||"".equals(BIN_CODE)){//BIN_CODE_SHELF或者BIN_CODE为空报错
				err_bf.append(" 第 "+itemlist.get(i).get("RN")+" 行,  物料号为 "+itemlist.get(i).get("MATNR")+"的数据， 获取推荐的储位为空，不能创建进仓单！");
			}
			
			itemlist.get(i).put("LABEL_STATUS", "03");	//ModBy:YK190528 BUG530:生成的 标签状态 是00  应该是03；
			
			if(!"".equals(BIN_CODE_SHELF)&&!"".equals(BIN_CODE)&&(BIN_CODE_SHELF.equals(BIN_CODE))){
				//BIN_CODE和BIN_CODE_SHELF相等，则AUTO_PUTAWAY_FLAG表示为自动上架
				itemlist.get(i).put("AUTO_PUTAWAY_FLAG", "X");
			}else{
				itemlist.get(i).put("AUTO_PUTAWAY_FLAG", "0");
			}
			
		}
		if(err_bf.toString().length()>0) {
			throw new RuntimeException(err_bf.toString());
		}

		//生成标签
    	List<Map<String, Object>> skList=new ArrayList<Map<String, Object>>();
    	this.saveCoreLabel(skList, itemlist);
    	
    	wmsInInboundDao.insertWmsInboundHead(tempHeadmap);
		wmsInInboundDao.insertWmsInboundItem(itemlist);
    			
		//更新核销行表 剩余核销数量
		if(HXMOitemlist!=null&&HXMOitemlist.size()>0){
			wmsInInboundDao.updateHXMOITEM(HXMOitemlist);
		}
		
		//更新核销组件表 剩余核销数量
		if(HXMOcomplist!=null&&HXMOcomplist.size()>0){
			wmsInInboundDao.updateHXMOCOMP(HXMOcomplist);
		}
    			
		return docNo;
	}
	
	/**
	 * 保存标签数据
	 */
	@Transactional
	public void saveCoreLabel(List<Map<String, Object>> skList, List<Map<String, Object>> matList) {
		//从行项目中获取收货单号和收货单行项目号
		//非SCM收货单情况需要拆分行项目计算包装箱信息
		Map<String,Object> params=new HashMap<String,Object>();
		params.put("WMS_DOC_TYPE", "08");//标签号
		params.put("WERKS", matList.get(0).get("WERKS"));
			
		for(Map m:matList) {
			Double RECEIPT_QTY=Double.valueOf(m.get("IN_QTY").toString());
			Double FULL_BOX_QTY=Double.valueOf(m.get("FULL_BOX_QTY").toString());
			if(FULL_BOX_QTY <= 0) {
				throw new RuntimeException("装箱数量不能小于等于0！");
			}
			int box_num=(int) Math.ceil(RECEIPT_QTY/FULL_BOX_QTY);
			
			StringBuffer LABEL_NO_SB = new StringBuffer();
			
			for(int i=1;i<=box_num;i++) {
				Map<String,Object> sk=new HashMap<String,Object>();		
				sk.putAll(m);
				
				String LABEL_NO="";
				Map<String,Object> doc=null;
				doc=wmsCDocNoService.getDocNo(params);
				if(doc.get("MSG")!=null&&!"success".equals(doc.get("MSG"))) {
					throw new RuntimeException(doc.get("MSG").toString());
				}
				LABEL_NO=doc.get("docno").toString();
				
				LABEL_NO_SB.append(LABEL_NO+",");
				
				String BOX_SN=i+"/"+box_num;
				Double BOX_QTY=0.0;//装箱数量，计算得出
				String END_FLAG="0";
				if(i==box_num) {
					END_FLAG="X";
				}				
				
				sk.put("LABEL_NO", LABEL_NO);
				//sk.put("LABEL_STATUS", "01");
				sk.put("RECEIPT_NO", m.get("RECEIPT_NO"));
				sk.put("RECEIPT_ITEM_NO", m.get("RECEIPT_ITEM_NO"));
				sk.put("BOX_SN", BOX_SN);
				sk.put("FULL_BOX_QTY", m.get("FULL_BOX_QTY"));
				if(i==box_num){
					sk.put("BOX_QTY", RECEIPT_QTY-(i-1)*FULL_BOX_QTY);//装箱数量，计算得出
				}else{
					sk.put("BOX_QTY", FULL_BOX_QTY);//装箱数量，计算得出
				}
				sk.put("END_FLAG", END_FLAG);
				
				sk.put("LABEL_STATUS", m.get("LABEL_STATUS"));		//ModBy:YK190528 BUG530:生成的 标签状态 是00  应该是03；
				sk.put("WH_NUMBER", m.get("WH_NUMBER"));
				sk.put("LGORT", m.get("LGORT"));
				sk.put("BIN_CODE", m.get("BIN_CODE"));
				sk.put("UNIT", m.get("UNIT"));
				
				sk.put("INBOUND_NO", m.get("INBOUND_NO"));
				sk.put("INBOUND_ITEM_NO", m.get("INBOUND_ITEM_NO"));
				
				sk.put("WORKSHOP", m.get("WORKSHOP")==null?"":m.get("WORKSHOP"));
				sk.put("WORKGROUP_NO", m.get("WORKGROUP_NO")==null?"":m.get("WORKGROUP_NO"));
				sk.put("CAR_TYPE", m.get("CAR_TYPE")==null?"":m.get("CAR_TYPE"));
				sk.put("MOLD", m.get("MOULD_NO")==null?"":m.get("MOULD_NO"));
				sk.put("OPERATOR", m.get("OPERATOR")==null?"":m.get("OPERATOR"));
				sk.put("WLQJ", m.get("WLQJ")==null?"":m.get("WLQJ"));
				sk.put("CAR_TYPE", m.get("CAR_TYPE")==null?"":m.get("CAR_TYPE"));
				sk.put("PRODUCT_NO", m.get("PRODUCT_NO")==null?"":m.get("PRODUCT_NO"));
				sk.put("STATION", m.get("STATION")==null?"":m.get("STATION"));
				skList.add(sk);
			}
			
			m.put("LABEL_NO", LABEL_NO_SB.toString());
		}
		wmsInReceiptDao.insertCoreLabel(skList);
	}
	
	/**
	 * 生产订单进仓101 531
	 * @param result
	 * @return
	 */
	public List<Map<String,Object>> queryList101_531(Map<String,Object> cond){
		
		List<Map<String,Object>> retlist101=new ArrayList<Map<String,Object>>();//生产订单 返回的列表
		String aufnrstr=cond.get("AUFNR")==null?"":cond.get("AUFNR").toString();//生产订单，多个使用;隔开
		String whnumber=cond.get("WHNUMBER").toString();//仓库号
		String WERKS=cond.get("WERKS").toString();//工厂
		String rLgort=cond.get("rLgort").toString();//库位
		String BEDNR=cond.get("BEDNR").toString();//需求跟踪号
		String INBOUND_TYPE=cond.get("inbound_type").toString();//
		
		String LIFNR=cond.get("LIFNR")==null?"":cond.get("LIFNR").toString();//
		String LIKTX=cond.get("LIKTX")==null?"":cond.get("LIKTX").toString();//
		
		List<Map<String,Object>> condMapList=new ArrayList<Map<String,Object>>();
		
		if("11".equals(INBOUND_TYPE)){
			//半成品生产订单(101)
			valid101(cond);
		}else if("13".equals(INBOUND_TYPE)){
			//前台传来的生产订单和物料号条件
			if(cond.get("ARRLIST")!=null){
			JSONArray jarr = JSON.parseArray(cond.get("ARRLIST").toString());
			for(int i=0;i<jarr.size();i++){
				Map<String,Object> condMap=new HashMap<String,Object>();
				
				condMap.put("AUFNR", jarr.getJSONObject(i).getString("AUFNR"));
				
				condMap.put("MATNR", jarr.getJSONObject(i).getString("MATNR"));
				
				condMap.put("WERKS", WERKS);
				
				if(!"".equals(jarr.getJSONObject(i).getString("AUFNR"))){
					condMapList.add(condMap);
				}
				
				if(!"".equals(jarr.getJSONObject(i).getString("AUFNR"))){//用于验证生产订单
					if("".equals(aufnrstr)){
						aufnrstr=jarr.getJSONObject(i).getString("AUFNR");
					}else{
						aufnrstr=aufnrstr+";"+jarr.getJSONObject(i).getString("AUFNR");
					}
					
				}
			}
			if(!"".equals(aufnrstr)){
			cond.put("AUFNR", aufnrstr);
			valid531(cond);
			}else{
				throw new RuntimeException("生产订单不能为空！");
			}
			}
		}
		
		String[] aufnr_arr=aufnrstr.split(";");
		//查询生产订单item,comp表
		for(int m=0;m<aufnr_arr.length;m++){
			Map<String,Object> aufnr_cond=new HashMap<String,Object>();
			aufnr_cond.put("AUFNR",aufnr_arr[m]);
			aufnr_cond.put("WERKS",WERKS);
			List<Map<String,Object>> retMolist=new ArrayList<Map<String,Object>>();
			if("11".equals(INBOUND_TYPE)){//生产订单进仓101
				retMolist=wmsInInboundDao.getMOListInfo(aufnr_cond);//查询生产订单item表
			}else if("13".equals(INBOUND_TYPE)){//生产订单副产品进仓531
				//retMolist=wmsInInboundDao.getMOCOMPListInfo(aufnr_cond);//查询生产订单comp表
			}
			if(retMolist!=null&&retMolist.size()>0){
				for(Map<String,Object> mo:retMolist){
					//检查物料主数据表是否带出了物料描述，否则报错
					if("".equals(mo.get("MATERIALDESC"))||mo.get("MATERIALDESC")==null){
						throw new RuntimeException("工厂 "+mo.get("WERKS")+" 物料"+mo.get("MATERIAL")+"没有维护物料主数据或者描述为空！");
					}else{
						retlist101.add(mo);
					}
				}
			}
		}
		
		if("13".equals(INBOUND_TYPE)){//生产订单副产品进仓531
			if(condMapList.size()>0){
				List<Map<String,Object>> retMolist_n=wmsInInboundDao.getMOCOMPListInfoBylist(condMapList);
				if(retMolist_n!=null&&retMolist_n.size()>0){
					for(Map<String,Object> mo:retMolist_n){
						//检查物料主数据表是否带出了物料描述，否则报错
						if("".equals(mo.get("MATERIALDESC"))||mo.get("MATERIALDESC")==null){
							throw new RuntimeException("工厂 "+mo.get("WERKS")+" 物料"+mo.get("MATERIAL")+"没有维护物料主数据或者描述为空！");
						}else{
							retlist101.add(mo);
						}
						//处理销售订单号
						if(mo.get("KDAUF")==null){//因为KDAUF为空的时候，同步的KDPOS为000000
							mo.put("KDPOS", null);
						}
					}
				}
			}
		}
		//开始处理查询出来的生产订单
		for(int n=0;n<retlist101.size();n++){
			
			Map<String,Object> retMap=retlist101.get(n);
			
			//已过账数量
			BigDecimal hasgz_d=BigDecimal.ZERO;
			Map<String,Object> tempgz=new HashMap<String,Object>();
			tempgz.put("MO_NO", retMap.get("AUFNR"));
			tempgz.put("MO_ITEM_NO", retMap.get("POSNR"));
			List<Map<String,Object>> ygzlist=wmsInInboundDao.getGZQty(tempgz);
			if(ygzlist!=null&&ygzlist.size()>0&&ygzlist.get(0)!=null){
				String hasgz_str=ygzlist.get(0).get("HASGZ").toString();
				hasgz_d=new BigDecimal(hasgz_str);
			}
			retMap.put("HASGZ", hasgz_d);
			
			//可进仓数量 订单数量-汇总（对应进仓单数据：状态为创建，部分进仓 取进仓数量-已进仓数量）-已过账数量
			BigDecimal kjcsl=BigDecimal.ZERO;//可进仓数量
			
			BigDecimal hasjc=BigDecimal.ZERO;
			List<Map<String,Object>> yjclist=wmsInInboundDao.getJCQty(tempgz);
			if(yjclist!=null&&yjclist.size()>0&&yjclist.get(0)!=null){
				hasjc=new BigDecimal(yjclist.get(0).get("HASJC").toString());
			}
			
			BigDecimal ddsl=new BigDecimal(retMap.get("PSMNG").toString());//生产订单数量
			kjcsl=ddsl.subtract(hasjc).subtract(hasgz_d);
			
			retMap.put("KJCSL", kjcsl);
			
			retMap.put("IN_QTY", kjcsl);//本次进仓数量
			
			//装箱数量
			//首先获取满箱数量
			BigDecimal fullBoxQty_d=null;
			Map<String,Object> mattem=new HashMap<String,Object>();
			mattem.put("WERKS", retMap.get("WERKS"));
			mattem.put("MATNR", retMap.get("MATERIAL"));
			List<Map<String,Object>> matlilist=wmsInInboundDao.getWmsCMatLtSample(mattem);
			if(matlilist!=null&&matlilist.size()>0){
				Object fullBoxQty=matlilist.get(0).get("FULL_BOX_QTY");
				if(fullBoxQty!=null){
					fullBoxQty_d=new BigDecimal(fullBoxQty.toString());
				}
				
				//物流器具
				Object LT_WARE=matlilist.get(0).get("LT_WARE");
				if(LT_WARE!=null){
					retMap.put("LT_WARE", LT_WARE.toString());
				}
				//车型
				Object CAR_TYPE=matlilist.get(0).get("CAR_TYPE");
				if(CAR_TYPE!=null){
					retMap.put("CAR_TYPE", CAR_TYPE.toString());
				}
				//模具编号 
				Object MOULD_NO=matlilist.get(0).get("MOULD_NO");
				if(MOULD_NO!=null){
					retMap.put("MOULD_NO", MOULD_NO.toString());
				}
				//工位
				Object DIS_STATION=matlilist.get(0).get("DIS_STATION");
				if(DIS_STATION!=null){
					retMap.put("DIS_STATION", DIS_STATION.toString());
				}
			}
			
			BigDecimal box_count=new BigDecimal(1);//件数
			
			if(fullBoxQty_d!=null){
				box_count=kjcsl.divide(fullBoxQty_d, RoundingMode.UP);//向上取整
			}else{
				fullBoxQty_d=kjcsl;
				retMap.put("FULL_BOX_QTY_FLAG", "X");
			}
			
			retMap.put("FULL_BOX_QTY", fullBoxQty_d);
			retMap.put("BOX_COUNT", box_count.setScale(0, RoundingMode.UP));
			
			//获取储位
			retMap.put("BIN_CODE", "");//储位   在创建进仓单的时候获取
			//仓管员
			Map<String,Object> whmap=new HashMap<String,Object>();
			whmap.put("WERKS", retMap.get("WERKS").toString());
			whmap.put("WHNUMBER", whnumber);
			whmap.put("MATNR", retMap.get("MATERIAL"));
			whmap.put("LGORT", rLgort);
			Map<String,Object> retWhMap=getwhManager(whmap);
			retMap.put("WH_MANAGER", retWhMap.get("WH_MANAGER"));
			
			//紧急物料
			Map<String,Object> urgentmap=new HashMap<String,Object>();
			urgentmap.put("WERKS", retMap.get("WERKS"));
			urgentmap.put("MATNR", retMap.get("MATERIAL"));
			List<WmsCMatUrgentEntity> returgentlist=wmsCMatUrgentService.selectByMap(urgentmap);
			if(returgentlist.size()>0){
				retMap.put("urgentFlag", "Y");
			}
			//产品图号
			if(retMap.get("MATERIALDESC")!=null){
				retMap.put("CPTH", StringUtils.substringBefore(retMap.get("MATERIALDESC").toString(), "_"));
			}
			
			retMap.put("RECWERKS", WERKS);
			retMap.put("WHNUMBER", whnumber);
			retMap.put("RLGORT", rLgort);
			retMap.put("INBOUND_TYPE", INBOUND_TYPE);
			retMap.put("BEDNR", cond.get("xqgzh"));
			retMap.put("PRDDT", DateUtils.format(new Date(),DateUtils.DATE_PATTERN));
			retMap.put("BEDNR", BEDNR);
			
			retMap.put("LIFNR", LIFNR);
			retMap.put("LIKTX", LIKTX);
			
			//重新赋值生产订单和行项目号
			retMap.put("MO_NO", retMap.get("AUFNR"));
			retMap.put("MO_ITEM_NO", retMap.get("POSNR"));
			
			retMap.put("UNIT", retMap.get("MEINS"));
			
		}
		//kjcsl 可进仓数量为0的不显示
		List<Map<String,Object>> retlist101_305=new ArrayList<Map<String,Object>>();
		for(int z=0;z<retlist101.size();z++){
			Map<String,Object> maptemp=retlist101.get(z);
			if(!"0".equals(maptemp.get("KJCSL").toString())){
				retlist101_305.add(maptemp);
			}
		}
		if(retlist101_305.size()<=0){
			throw new RuntimeException("没有查询到对应数据或者可进仓数量为0！");
		}
		
		return retlist101_305;
	}
	/**
	 * 生产订单进仓101   校验数据
	 */
	private void valid101(Map<String, Object> param){
		String AUFNR=param.get("AUFNR").toString();//生产订单号
		String WERKS=param.get("WERKS").toString();//
		//校验WMS数据库是否存在对应的数据,以及状态是否是REL  TECO
		String[] aufnrstr=AUFNR.split(";");
		for(int k=0;k<aufnrstr.length;k++){
			Map<String, Object> tempmap=new HashMap<String, Object>();
			tempmap.put("AUFNR", aufnrstr[k]);
			tempmap.put("WERKS", WERKS);
			List<Map<String,Object>> mohead=wmsInInboundDao.getMOHEADInfo(tempmap);
			if(mohead!=null&&mohead.size()>0){
				String status=mohead.get(0).get("ISTAT_TXT").toString();
				if(!status.contains("REL")||status.contains("DLFL") ||status.contains("LKD")){
					throw new RuntimeException("生产订单号 "+AUFNR+" 没有释放，不能做进仓！");
				}
			}else{
				throw new RuntimeException("生产订单号 "+AUFNR+" 不存在，请检查是否输入有误，如果确认无误，请操作同步SAP生产订单功能！");
			}
			
			
			//生产订单对应工厂和账号配置的权限校验
			
			List<Map<String,Object>> deptList=(List<Map<String, Object>>) param.get("deptList");
			if(mohead!=null&&mohead.size()>0){
				boolean isauth=false;
				String werks=mohead.get(0).get("WERKS").toString();//生产订单中的工厂
				if(deptList!=null&&deptList.size()>0){
					for (Map<String, Object> map : deptList) {
						if(werks.equals(map.get("CODE"))){//存在相同的   生产订单中的工厂等于权限对应的工厂 表示有权限
							isauth=true;
							break;
						}
					}
				}
				if(!isauth){
					throw new RuntimeException("生产订单号 "+AUFNR+" 您无权操作"+werks+"工厂的生产订单！");
				}
			}
			//核销检查
			Map<String, Object> hxparams=new HashMap<String, Object>();
			hxparams.put("werks", mohead.get(0).get("WERKS"));
			hxparams.put("del_flag", "0");
			
			//List<WmsCPlant> cplantList=wmsCPlantService.selectByMap(hxparams);
			List<WmsCWhEntity> cplantList=wmsCWhService.selectByMap(hxparams);
			String hx_flag="0";
			if(cplantList.size()>0){
				 hx_flag=cplantList.get(0).getHxFlag();
			}
			if("X".equals(hx_flag)){//启用了核销
				Map<String, Object> hxmoparams=new HashMap<String, Object>();
				hxmoparams.put("AUFNR", mohead.get(0).get("AUFNR"));
				//检查对应生产订单在核销信息表是否存在 还需核销数量>0的数据
				boolean ishxmo=false;
				List<Map<String,Object>> rethxmo=wmsInInboundDao.getHxMOInfo(hxmoparams);
				if(rethxmo!=null&&rethxmo.size()>0){//核销表存在
					for(int j=0;j<rethxmo.size();j++){
						if(rethxmo.get(j).get("PSMNG")!=null){
							String psmng_str=rethxmo.get(j).get("PSMNG").toString();//存在核销数量
							BigDecimal psmng_d=new BigDecimal(psmng_str);
							if(psmng_d.compareTo(BigDecimal.ZERO)>0){
								ishxmo=true;
							}
						}
					}
				}
				if(ishxmo){
					throw new RuntimeException("生产订单号 "+AUFNR+" 存在还需核销数量，请使用系统功能 半成品生产订单（A101）进仓！");
				}
			}
		}
	}
	/**
	 * 生产订单进仓531   校验数据
	 */
	public void valid531(Map<String, Object> param){
		boolean isflag=true;
		String AUFNR=param.get("AUFNR").toString();//生产订单号
		String WERKS=param.get("WERKS").toString();//
		//校验WMS数据库是否存在对应的数据
		String[] aufnrstr=AUFNR.split(";");
		for(int k=0;k<aufnrstr.length;k++){
			Map<String, Object> tempmap=new HashMap<String, Object>();
			tempmap.put("AUFNR", aufnrstr[k]);
			tempmap.put("WERKS", WERKS);
		List<Map<String,Object>> mohead=wmsInInboundDao.getMOHEADInfo(tempmap);
		if(mohead!=null&&mohead.size()>0){
			String status=mohead.get(0).get("ISTAT_TXT").toString();
			if(!status.contains("REL")||status.contains("DLFL") ||status.contains("LKD")){
				throw new RuntimeException("生产订单号 "+AUFNR+" 没有释放，不能做进仓！");
			}
		}else{
			throw new RuntimeException("生产订单号 "+AUFNR+" 不存在，请检查是否输入有误，如果确认无误，请操作同步SAP生产订单功能！");
		}
		
		
		//生产订单对应工厂和账号配置的权限校验
		
		List<Map<String,Object>> deptList=(List<Map<String, Object>>) param.get("deptList");
		if(mohead!=null&&mohead.size()>0){
			boolean isauth=false;
			String werks=mohead.get(0).get("WERKS").toString();//生产订单中的工厂
			if(deptList!=null&&deptList.size()>0){
				for (Map<String, Object> map : deptList) {
					if(werks.equals(map.get("CODE"))){//存在相同的   生产订单中的工厂等于权限对应的工厂 表示有权限
						isauth=true;
						break;
					}
				}
			}
			if(!isauth){
				throw new RuntimeException("生产订单号 "+AUFNR+" 您无权操作"+werks+"工厂的生产订单！");
			}
		}
		
		}
	}
	
	/**
	 * Co生产订单查询
	 * @param result
	 * @return
	 */
	public Map<String,Object> queryListCO(Map<String,Object> cond){
		Map<String,Object> retMap=new HashMap<String,Object>();
		retMap=wmsSapRemote.getSapBapiKaufOrder(cond.get("CO_NO").toString());
		return retMap;
	}
	
	/**
	 * Cost_center成本中心查询
	 * @param result
	 * @return
	 */
	public Map<String,Object> queryListCostCenter(Map<String,Object> cond){
		Map<String,Object> retMap=new HashMap<String,Object>();
		retMap=wmsSapRemote.getSapBapiCostcenterDetail(cond.get("COST_CENTER").toString());
		return retMap;
	}
	
	/**
	 * WBS查询
	 * @param result
	 * @return
	 */
	public Map<String,Object> queryListWBS(Map<String,Object> cond){
		Map<String,Object> retMap=new HashMap<String,Object>();
		retMap=wmsSapRemote.getSapBapiSapBapiWbs(cond.get("WBS").toString());
		return retMap;
	}
	/**
	 * 研发订单查询
	 * @param result
	 * @return
	 */
	public Map<String,Object> queryListYf(Map<String,Object> cond){
		Map<String,Object> retMap=new HashMap<String,Object>();
		retMap=wmsSapRemote.getSapBapiInternalorderDetail(cond.get("IO_NO").toString());
		return retMap;
	}
	
	/**
	 * 生产订单进仓查询A101
	 * @param result
	 * @return
	 */
	public List<Map<String,Object>> queryListA101(Map<String,Object> param){
		List<Map<String,Object>> retmaplist=new ArrayList<Map<String,Object>>();
		String AUFNR=param.get("AUFNR").toString();//生产订单号
		String WERKS=param.get("WERKS").toString();//
		String WHNUMBER=param.get("WHNUMBER").toString();//
		String INBOUND_TYPE=param.get("inbound_type").toString();//
		String RLGORT=param.get("rLgort").toString();//
		String BEDNR=param.get("BEDNR").toString();//
		//校验WMS数据库是否存在对应的数据
		String[] aufnrstr=AUFNR.split(";");
		
		Map<String,Object> cond=new HashMap<String,Object>();
		cond.put("AUFNRList", aufnrstr);
		cond.put("WERKS", WERKS);
		cond.put("WHNUMBER", WHNUMBER);
		retmaplist=wmsInInboundDao.getHXMOITEM(cond);
		
		if(retmaplist!=null&&retmaplist.size()>0){//循环结果集 查询订单数量
			for(int i=0;i<retmaplist.size();i++){
				retmaplist.get(i).put("DDSL", retmaplist.get(i).get("PSMNG"));// 订单数量
				
				BigDecimal HX_QTY=new BigDecimal(retmaplist.get(i).get("HX_QTY").toString());//剩余核销数量
				
				BigDecimal fullBoxQty_d=null;
				Map<String,Object> mattem=new HashMap<String,Object>();
				mattem.put("WERKS", retmaplist.get(i).get("WERKS"));
				mattem.put("MATNR", retmaplist.get(i).get("MATNR"));
				List<Map<String,Object>> matlilist=wmsInInboundDao.getWmsCMatLtSample(mattem);
				
				if(matlilist!=null&&matlilist.size()>0){
					Object fullBoxQty=matlilist.get(0).get("FULL_BOX_QTY");
					if(fullBoxQty!=null){
						fullBoxQty_d=new BigDecimal(fullBoxQty.toString());
					}
					
					//物流器具
					Object LT_WARE=matlilist.get(0).get("LT_WARE");
					if(LT_WARE!=null){
						retmaplist.get(i).put("LT_WARE", LT_WARE.toString());
					}
					//车型
					Object CAR_TYPE=matlilist.get(0).get("CAR_TYPE");
					if(CAR_TYPE!=null){
						retmaplist.get(i).put("CAR_TYPE", CAR_TYPE.toString());
					}
					//模具编号 
					Object MOULD_NO=matlilist.get(0).get("MOULD_NO");
					if(MOULD_NO!=null){
						retmaplist.get(i).put("MOULD_NO", MOULD_NO.toString());
					}
					//工位
					Object DIS_STATION=matlilist.get(0).get("DIS_STATION");
					if(DIS_STATION!=null){
						retmaplist.get(i).put("DIS_STATION", DIS_STATION.toString());
					}
				}
				BigDecimal box_count=new BigDecimal(1);//件数
				
				if(fullBoxQty_d!=null){
					box_count=HX_QTY.divide(fullBoxQty_d, RoundingMode.UP);//向上取整
				}else{
					fullBoxQty_d=HX_QTY;
				}
				
				retmaplist.get(i).put("FULL_BOX_QTY", fullBoxQty_d);
				retmaplist.get(i).put("BOX_COUNT", box_count.setScale(0, RoundingMode.UP));
				
				//获取储位
				Map<String,Object> bincodemap=new HashMap<String,Object>();
				bincodemap.put("WERKS", retmaplist.get(i).get("WERKS"));
				bincodemap.put("WH_NUMBER", retmaplist.get(i).get("WH_NUMBER"));
				bincodemap.put("MATNR", retmaplist.get(i).get("MATNR"));
				List<WmsCMatStorageEntity> matstoragelist=matstorageservice.selectByMap(bincodemap);
				if(matstoragelist!=null&&matstoragelist.size()>0){
					//retmaplist.get(i).put("BIN_CODE", matstoragelist.get(0).getBinCode());//储位
					retmaplist.get(i).put("BIN_CODE", "");//储位
				}else{
					retmaplist.get(i).put("BIN_CODE", "AAAA");
				}
				//仓管员
				Map<String,Object> whmap=new HashMap<String,Object>();
				whmap.put("WERKS", retmaplist.get(i).get("WERKS"));
				whmap.put("WHNUMBER", retmaplist.get(i).get("WH_NUMBER"));
				whmap.put("MATNR", retmaplist.get(i).get("MATNR"));
				whmap.put("LGORT", RLGORT);
				Map<String,Object> retWhMap=getwhManager(whmap);
				retmaplist.get(i).put("WH_MANAGER", retWhMap.get("WH_MANAGER"));
				
				//紧急物料
				Map<String,Object> urgentmap=new HashMap<String,Object>();
				urgentmap.put("WERKS", retmaplist.get(i).get("WERKS"));
				urgentmap.put("MATNR", retmaplist.get(i).get("MATNR"));
				List<WmsCMatUrgentEntity> returgentlist=wmsCMatUrgentService.selectByMap(urgentmap);
				if(returgentlist.size()>0){
					retmaplist.get(i).put("urgentFlag", "Y");
				}
				//产品图号
				if(retmaplist.get(i).get("MAKTX")!=null){
					retmaplist.get(i).put("CPTH", StringUtils.substringBefore(retmaplist.get(i).get("MAKTX").toString(), "_"));
				}
				
				retmaplist.get(i).put("MATERIAL", retmaplist.get(i).get("MATNR"));
				retmaplist.get(i).put("MATERIALDESC", retmaplist.get(i).get("MAKTX"));
				retmaplist.get(i).put("IO_NO", retmaplist.get(i).get("AUFNR"));
				retmaplist.get(i).put("UNIT", retmaplist.get(i).get("MEINS"));
				retmaplist.get(i).put("RLGORT", RLGORT);
				
				retmaplist.get(i).put("RECWERKS", retmaplist.get(i).get("WERKS"));
				retmaplist.get(i).put("WHNUMBER", retmaplist.get(i).get("WH_NUMBER"));
				retmaplist.get(i).put("INBOUND_TYPE", INBOUND_TYPE);
				retmaplist.get(i).put("BEDNR", BEDNR);
				retmaplist.get(i).put("PRDDT", DateUtils.format(new Date(),DateUtils.DATE_PATTERN));
				retmaplist.get(i).put("IN_QTY", HX_QTY);
			}
		}
		
		
		return retmaplist;
	}
	
	/**
	 * 生产订单进仓查询A531
	 * @param result
	 * @return
	 */
	public List<Map<String,Object>> queryListA531(Map<String,Object> param){
		List<Map<String,Object>> retmaplist=new ArrayList<Map<String,Object>>();
		String AUFNR=param.get("AUFNR").toString();//生产订单号
		String WERKS=param.get("WERKS").toString();//
		String WHNUMBER=param.get("WHNUMBER").toString();//
		String INBOUND_TYPE=param.get("inbound_type").toString();//
		String RLGORT=param.get("rLgort").toString();//
		String BEDNR=param.get("BEDNR").toString();//
		//校验WMS数据库是否存在对应的数据
		String[] aufnrstr=AUFNR.split(";");
		
		Map<String,Object> cond=new HashMap<String,Object>();
		cond.put("AUFNRList", aufnrstr);
		cond.put("WERKS", WERKS);
		retmaplist=wmsInInboundDao.getHXMOCOMPONENT(cond);
		
		if(retmaplist!=null&&retmaplist.size()>0){//循环结果集 查询订单数量
			for(int i=0;i<retmaplist.size();i++){
				//查询物料描述
				Map<String,Object> sapmaterialmap=new HashMap<String,Object>();
				sapmaterialmap.put("WERKS", WERKS);
				sapmaterialmap.put("MATNR", retmaplist.get(i).get("MATNR").toString());
				List<WmsSapMaterialEntity> retsapMateriallist=wmsSapMaterialService.selectByMap(sapmaterialmap);
				if(retsapMateriallist!=null&&retsapMateriallist.size()>0){
					retmaplist.get(i).put("MATERIALDESC",retsapMateriallist.get(0).getMaktx());
				}
				
				Map<String,Object> sapmoComponenttemp=new HashMap<String,Object>();
				sapmoComponenttemp.put("AUFNR", retmaplist.get(i).get("AUFNR"));
				sapmoComponenttemp.put("RSNUM", retmaplist.get(i).get("RSNUM"));
				sapmoComponenttemp.put("RSPOS", retmaplist.get(i).get("RSPOS"));
				List<Map<String,Object>> retsapmoComponentlist=wmsInInboundDao.getMOComponentListByhx(sapmoComponenttemp);
				if(retsapmoComponentlist!=null&&retsapmoComponentlist.size()>0){
					retmaplist.get(i).put("DDSL", retsapmoComponentlist.get(i).get("BDMNG"));// 订单数量
				}
				
				//
				
				BigDecimal HX_QTY=new BigDecimal(retmaplist.get(i).get("HX_QTY_BY").toString());//剩余核销数量
				
				BigDecimal fullBoxQty_d=null;
				Map<String,Object> mattem=new HashMap<String,Object>();
				mattem.put("WERKS", retmaplist.get(i).get("WERKS"));
				mattem.put("MATNR", retmaplist.get(i).get("MATNR"));
				List<Map<String,Object>> matlilist=wmsInInboundDao.getWmsCMatLtSample(mattem);
				
				if(matlilist!=null&&matlilist.size()>0){
					Object fullBoxQty=matlilist.get(0).get("FULL_BOX_QTY");
					if(fullBoxQty!=null){
						fullBoxQty_d=new BigDecimal(fullBoxQty.toString());
					}
					
					//物流器具
					Object LT_WARE=matlilist.get(0).get("LT_WARE");
					if(LT_WARE!=null){
						retmaplist.get(i).put("LT_WARE", LT_WARE.toString());
					}
					//车型
					Object CAR_TYPE=matlilist.get(0).get("CAR_TYPE");
					if(CAR_TYPE!=null){
						retmaplist.get(i).put("CAR_TYPE", CAR_TYPE.toString());
					}
					//模具编号 
					Object MOULD_NO=matlilist.get(0).get("MOULD_NO");
					if(MOULD_NO!=null){
						retmaplist.get(i).put("MOULD_NO", MOULD_NO.toString());
					}
					//工位
					Object DIS_STATION=matlilist.get(0).get("DIS_STATION");
					if(DIS_STATION!=null){
						retmaplist.get(i).put("DIS_STATION", DIS_STATION.toString());
					}
				}
				BigDecimal box_count=new BigDecimal(1);//件数
				
				if(fullBoxQty_d!=null){
					box_count=HX_QTY.divide(fullBoxQty_d, RoundingMode.UP);//向上取整
				}else{
					fullBoxQty_d=HX_QTY;
				}
				
				retmaplist.get(i).put("FULL_BOX_QTY", fullBoxQty_d);
				retmaplist.get(i).put("BOX_COUNT", box_count.setScale(0, RoundingMode.UP));
				
				//获取储位
				Map<String,Object> bincodemap=new HashMap<String,Object>();
				bincodemap.put("WERKS", retmaplist.get(i).get("WERKS"));
				bincodemap.put("WH_NUMBER", retmaplist.get(i).get("WH_NUMBER"));
				bincodemap.put("MATNR", retmaplist.get(i).get("MATNR"));
				List<WmsCMatStorageEntity> matstoragelist=matstorageservice.selectByMap(bincodemap);
				if(matstoragelist!=null&&matstoragelist.size()>0){
					//retmaplist.get(i).put("BIN_CODE", matstoragelist.get(0).getBinCode());//储位
					retmaplist.get(i).put("BIN_CODE", "");//储位
				}else{
					retmaplist.get(i).put("BIN_CODE", "AAAA");
				}
				//仓管员
				Map<String,Object> whmap=new HashMap<String,Object>();
				whmap.put("WERKS", retmaplist.get(i).get("WERKS"));
				whmap.put("WHNUMBER", retmaplist.get(i).get("WH_NUMBER"));
				whmap.put("MATNR", retmaplist.get(i).get("MATNR"));
				whmap.put("LGORT", RLGORT);
				Map<String,Object> retWhMap=getwhManager(whmap);
				retmaplist.get(i).put("WH_MANAGER", retWhMap.get("WH_MANAGER"));
				//紧急物料
				Map<String,Object> urgentmap=new HashMap<String,Object>();
				urgentmap.put("WERKS", retmaplist.get(i).get("WERKS"));
				urgentmap.put("MATNR", retmaplist.get(i).get("MATNR"));
				List<WmsCMatUrgentEntity> returgentlist=wmsCMatUrgentService.selectByMap(urgentmap);
				if(returgentlist.size()>0){
					retmaplist.get(i).put("urgentFlag", "Y");
				}
				//产品图号
				if(retmaplist.get(i).get("MAKTX")!=null){
					retmaplist.get(i).put("CPTH", StringUtils.substringBefore(retmaplist.get(i).get("MAKTX").toString(), "_"));
				}
				
				retmaplist.get(i).put("MATERIAL", retmaplist.get(i).get("MATNR"));
				retmaplist.get(i).put("IO_NO", retmaplist.get(i).get("AUFNR"));
				retmaplist.get(i).put("UNIT", retmaplist.get(i).get("MEINS"));
				retmaplist.get(i).put("RLGORT", RLGORT);
				
				retmaplist.get(i).put("RECWERKS", retmaplist.get(i).get("WERKS"));
				retmaplist.get(i).put("WHNUMBER", WHNUMBER);
				retmaplist.get(i).put("INBOUND_TYPE", INBOUND_TYPE);
				retmaplist.get(i).put("BEDNR", BEDNR);
				retmaplist.get(i).put("PRDDT", DateUtils.format(new Date(),DateUtils.DATE_PATTERN));
				retmaplist.get(i).put("IN_QTY", HX_QTY);
				retmaplist.get(i).put("HX_QTY", HX_QTY);
			}
		}
		
		
		return retmaplist;
	}

	

	@Override
	public List<Map<String, Object>> queryListPO101(Map<String, Object> params) {
		String PO_NO=params.get("PO_NO").toString();
		String PRDDT=params.get("PRDDT").toString();
		String WHNUMBER=params.get("WHNUMBER").toString();
		String WERKS=params.get("WERKS").toString();
		String INBOUND_TYPE=params.get("inbound_type").toString();
		
		List<Map<String,Object>> poitemList=wmsInInboundDao.getPOItemsByPoNO(params);
    	if(poitemList==null||poitemList.size()==0) {
    		throw new RuntimeException("采购订单"+PO_NO+"不存在 ，请核对是否输入有误，如果无误，请使用采购订单同步功能，同步SAP数据。");
    	}
    	
    	//
    	String werks_po=poitemList.get(0).get("WERKS").toString();
    	if(!WERKS.equals(werks_po)){
    		throw new RuntimeException("采购订单的工厂"+werks_po+"与进仓的工厂"+WERKS+"不一致！");
    	}
    	
    	//生产订单对应工厂和账号配置的权限校验
		
    	List<Map<String,Object>> deptList=(List<Map<String, Object>>) params.get("deptList");
    			if(poitemList!=null&&poitemList.size()>0){
    				boolean isauth=false;
    				String werks=poitemList.get(0).get("WERKS").toString();//采购订单中的工厂
    				if(deptList!=null&&deptList.size()>0){
    				 for (Map<String, Object> map : deptList) { //权限对应的工厂
     					if(werks.equals(map.get("CODE"))){//存在相同的   生产订单中的工厂等于权限对应的工厂 表示有权限
    						isauth=true;
    						break;
    					}
    				 }

    				}
    				if(!isauth){
    					throw new RuntimeException("采购订单号 "+params.get("PO_NO")+" 您无权操作"+werks+"工厂的生产订单！");
    				}
    			}
    	//通过采购订单抬头的采购凭证类型判断，如果是BYDS就报错
    	String BSART=poitemList.get(0).get("BSART")==null?"":poitemList.get(0).get("BSART").toString();		
    	if("BYDS".equals(BSART)) {
    		throw new RuntimeException("STO采购订单，请用SAP交货单收货");
    	}
    	
    	/**
    	 * 校验采购订单和工厂对应供应商管理信息表【WMS_C_VENDOR】配置 ：是否上SCM系统 ,字段IS_SCM=X ,报错提示：供应商***已经上SCM系统，请用送货单收货！
    	 */
    	String LIFNR=poitemList.get(0).get("LIFNR")==null?"":poitemList.get(0).get("LIFNR").toString();//供应商代码
    	if("X".equals(poitemList.get(0).get("IS_SCM"))) {
    		throw new RuntimeException("供应商"+LIFNR+"已经上SCM系统，请用送货单收货！");
    	}
    	
    	//是否存在核销
    	for(int m=0;m<poitemList.size();m++){
    		String RECEIPT_FLAG=poitemList.get(m).get("RECEIPT_FLAG").toString();
    		if("X".equals(RECEIPT_FLAG)){
    			throw new RuntimeException("采购订单号 "+params.get("PO_NO")+" 还存在核销数量！");
    		}
    	}
    	
    	//计算订单数量  订单数量=订单数量-收货单数量(数量-冲销/取消数量)-进仓单数量(数量-冲销/取消数量)
    	for(int n=0;n<poitemList.size();n++){
    		Map<String,Object> tempmap=new HashMap<String,Object>();
    		tempmap.put("PO_NO", poitemList.get(n).get("PO_NO"));
    		tempmap.put("PO_ITEM_NO", poitemList.get(n).get("PO_ITEM_NO"));
    		
    		List<Map<String,Object>> shdslMap=wmsInInboundDao.getReceiveQtyByPONo(tempmap);//收货单已进仓数量
    		
    		BigDecimal menge_big=BigDecimal.ZERO;//显示到前台的订单数量
    		if(poitemList.get(n).get("MAX_MENGE")!=null){
    			menge_big=new BigDecimal(poitemList.get(n).get("MAX_MENGE").toString());
    		}
    		
    		//收货单数量
    		if(shdslMap!=null&&shdslMap.get(0)!=null){
    			menge_big=menge_big.subtract(new BigDecimal(shdslMap.get(0).get("HASRECEIVEQTY").toString()));
    		}
    		
    		//通过采购订单号 PO_NO  PO_ITEM_NO 判断 进仓单状态
    		List<Map<String,Object>> jcdstatuslist=wmsInInboundDao.getItemLsByPONO(tempmap);
    		String status_inbound="";
    		
    		if(jcdstatuslist!=null&&jcdstatuslist.size()>0){
    			status_inbound=jcdstatuslist.get(0).get("ITEM_STATUS").toString();
    		}
    		
    		if("00".equals(status_inbound)||"01".equals(status_inbound)||"02".equals(status_inbound)){
    			// 进仓数量-冲销/取消数量（状态为00创建，01部分进仓，02已完成)
	    		List<Map<String,Object>> jcdslMap=wmsInInboundDao.getGZQtyByPONo(tempmap);
	    		if(jcdslMap!=null&&jcdslMap.get(0)!=null){
	    			menge_big=menge_big.subtract(new BigDecimal(jcdslMap.get(0).get("HASGZ").toString()));
	    		}
    		}else if("04".equals(status_inbound)){//关闭状态
    			List<Map<String,Object>> jcdslMap=wmsInInboundDao.getGZQtyByPONoGuanbi(tempmap);
	    		if(jcdslMap!=null&&jcdslMap.get(0)!=null){
	    			menge_big=menge_big.subtract(new BigDecimal(jcdslMap.get(0).get("HASGZ").toString()));
	    		}
    		}
    		
    		
    		poitemList.get(n).put("MENGE", menge_big);
    		poitemList.get(n).put("IN_QTY", menge_big);
    		
    		//
    		BigDecimal fullBoxQty_d=null;
			Map<String,Object> mattem=new HashMap<String,Object>();
			mattem.put("WERKS", poitemList.get(n).get("WERKS"));
			mattem.put("MATNR", poitemList.get(n).get("MATNR"));
			List<Map<String,Object>> matlilist=wmsInInboundDao.getWmsCMatLtSample(mattem);
			if(matlilist!=null&&matlilist.size()>0){
				Object fullBoxQty=matlilist.get(0).get("FULL_BOX_QTY");
				if(fullBoxQty!=null){
					fullBoxQty_d=new BigDecimal(fullBoxQty.toString());
				}
			}
    		BigDecimal box_count=new BigDecimal(1);//件数
			
			if(fullBoxQty_d!=null){
				box_count=menge_big.divide(fullBoxQty_d, RoundingMode.UP);//向上取整
			}else{
				fullBoxQty_d=menge_big;
				poitemList.get(n).put("FULL_BOX_QTY_FLAG", "X");
			}
			
			poitemList.get(n).put("FULL_BOX_QTY", fullBoxQty_d);//满箱数量
			poitemList.get(n).put("BOX_COUNT", box_count.setScale(0, RoundingMode.UP));//件数
			
			poitemList.get(n).put("BIN_CODE", "");//
			
			Map<String,Object> whmap=new HashMap<String,Object>();
			whmap.put("WERKS", poitemList.get(n).get("WERKS"));
			whmap.put("WHNUMBER", WHNUMBER);
			whmap.put("MATNR", poitemList.get(n).get("MATNR"));
			whmap.put("LGORT", poitemList.get(n).get("LGORT"));
			Map<String,Object> retWhMap=getwhManager(whmap);
			poitemList.get(n).put("WH_MANAGER", retWhMap.get("WH_MANAGER"));
			
			poitemList.get(n).put("PRDDT", PRDDT);
			
			poitemList.get(n).put("RECWERKS", WERKS);
			poitemList.get(n).put("WHNUMBER", WHNUMBER);
			
			poitemList.get(n).put("RLGORT", poitemList.get(n).get("LGORT"));
			
			poitemList.get(n).put("MATERIAL", poitemList.get(n).get("MATNR"));
			
			poitemList.get(n).put("MATERIALDESC", poitemList.get(n).get("MAKTX"));
    	}
    	
    	List<Map<String,Object>> retlist=new ArrayList<Map<String,Object>>();
		for(int z=0;z<poitemList.size();z++){
			Map<String,Object> maptemp=poitemList.get(z);
			maptemp.put("INBOUND_TYPE", INBOUND_TYPE);
			if(!"0".equals(maptemp.get("MENGE").toString())){
				retlist.add(maptemp);
			}
		}
		if(retlist.size()==0){
			throw new RuntimeException("没有可进仓的数据！请检查订单数量");
		}
    	
		return retlist;
	}

	@Override
	public void validMo262(Map<String, Object> param) {
		String AUFNR=param.get("AUFNR").toString();//生产订单号
		String WERKS=param.get("WERKS").toString();//
		//校验WMS数据库是否存在对应的数据,以及状态是否是REL  TECO
		String[] aufnrstr=AUFNR.split(";");
		for(int k=0;k<aufnrstr.length;k++){
			Map<String, Object> tempmap=new HashMap<String, Object>();
			tempmap.put("AUFNR", aufnrstr[k]);
			tempmap.put("WERKS", WERKS);
		List<Map<String,Object>> mohead=wmsInInboundDao.getMOHEADInfo(tempmap);
		if(mohead!=null&&mohead.size()>0){
			String status=mohead.get(0).get("ISTAT_TXT").toString();
			if(!status.contains("REL")||status.contains("DLFL") ||status.contains("LKD")){
				throw new RuntimeException("生产订单号 "+AUFNR+" 没有释放，不能做进仓！");
			}
		}else{
			throw new RuntimeException("生产订单号 "+AUFNR+" 不存在，请检查是否输入有误，如果确认无误，请操作同步SAP生产订单功能！");
		}
		
		
		//生产订单对应工厂和账号配置的权限校验
		
		List<Map<String,Object>> deptList=(List<Map<String, Object>>) param.get("deptList");
		if(mohead!=null&&mohead.size()>0){
			boolean isauth=false;
			String werks=mohead.get(0).get("WERKS").toString();//生产订单中的工厂
			if(deptList!=null&&deptList.size()>0){
				for (Map<String, Object> map : deptList) {
					if(werks.equals(map.get("CODE"))){//存在相同的   生产订单中的工厂等于权限对应的工厂 表示有权限
						isauth=true;
						break;
					}
				}
			}
			if(!isauth){
				throw new RuntimeException("生产订单号 "+AUFNR+" 您无权操作"+werks+"工厂的生产订单！");
			}
		}
		//核销检查
		Map<String, Object> hxparams=new HashMap<String, Object>();
		hxparams.put("werks", mohead.get(0).get("WERKS"));
		hxparams.put("del_flag", "0");
		
		List<WmsCWhEntity> cplantList=wmsCWhService.selectByMap(hxparams);
		String hx_flag="0";
		if(cplantList.size()>0){
			 hx_flag=cplantList.get(0).getHxFlag();
		}
		if("X".equals(hx_flag)){//启用了核销
			Map<String, Object> hxmoparams=new HashMap<String, Object>();
			hxmoparams.put("AUFNR", mohead.get(0).get("AUFNR"));
			//检查对应生产订单在核销信息表是否存在 还需核销数量>0的数据
			boolean ishxmo=false;
			List<Map<String,Object>> rethxmo=wmsInInboundDao.getHxMOInfo(hxmoparams);
			if(rethxmo!=null&&rethxmo.size()>0){//核销表存在
				for(int j=0;j<rethxmo.size();j++){
					if(rethxmo.get(j).get("PSMNG")!=null){
						String psmng_str=rethxmo.get(j).get("PSMNG").toString();//存在核销数量
						BigDecimal psmng_d=new BigDecimal(psmng_str);
						if(psmng_d.compareTo(BigDecimal.ZERO)>0){
							ishxmo=true;
						}
					}
				}
			}
			if(ishxmo){
				throw new RuntimeException("生产订单号 "+AUFNR+" 存在还需核销数量，收货A 成品车入库！");
			}
		}
		}
	
		
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
	 * 和方法 getwhManager内容一致，方便控制层调用
	 * 
	 */
	@Override
	public Map<String, Object> getwhManager_n(Map<String, Object> param) {

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
	
}

package com.byd.wms.business.modules.cswlms.service.impl;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang3.StringUtils;
import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tempuri.ArrayOfWSWMSDemand;
import org.tempuri.ArrayOfWSWMSDispatchingComponent;
import org.tempuri.ArrayOfWSWMSDispatchingHeader;
import org.tempuri.ArrayOfWSWMSDispatchingItem;
import org.tempuri.ArrayOfWSWMSLogisMvt;
import org.tempuri.WSWLMSToWMS;
import org.tempuri.WSWMSDemand;
import org.tempuri.WSWMSDispatchingComponent;
import org.tempuri.WSWMSDispatchingHeader;
import org.tempuri.WSWMSDispatchingItem;
import org.tempuri.WSWMSDispatchingList;
import org.tempuri.WSWMSList;
import org.tempuri.WSWMSLogisMvt;
import org.tempuri.liku.ClsBYDService;
import org.tempuri.liku.IBYDWMSInterface;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.byd.utils.DateUtils;
import com.byd.utils.R;
import com.byd.wms.business.modules.common.service.CommonService;
import com.byd.wms.business.modules.common.service.WmsCDocNoService;
import com.byd.wms.business.modules.config.entity.WmsCPlant;
import com.byd.wms.business.modules.config.entity.WmsCVendor;
import com.byd.wms.business.modules.config.entity.WmsCWhEntity;
import com.byd.wms.business.modules.config.service.WmsCPlantService;
import com.byd.wms.business.modules.config.service.WmsCVendorService;
import com.byd.wms.business.modules.config.service.WmsCWhService;
import com.byd.wms.business.modules.cswlms.dao.DispatchingJISBillPickingDAO;
import com.byd.wms.business.modules.cswlms.service.DisPatchingJISBillPickingService;
import com.byd.wms.business.modules.in.dao.WmsInInboundDao;

/** 
 * @author 作者 E-mail: peng.tao1@byd.com
 * @version 创建时间：2018年12月29日 下午6:05:55 
 * 类说明 
 */
@Service
public class DisPatchingJISBillServiceImpl implements DisPatchingJISBillPickingService{
	@Autowired
	private DispatchingJISBillPickingDAO dispatchingJISBillPickingDAO;
	@Autowired
    WmsCPlantService wmsCPlantService;
	@Autowired
    private CommonService commonService;
	@Autowired
	private WmsCDocNoService wmsCDocService;
	@Autowired
	private WmsCVendorService wmsCVendorService;
	@Autowired
	WmsCWhService wmsCWhService;
	@Autowired
	private WmsInInboundDao wmsInInboundDao;
	@Autowired
	DisPatchingJISBillPickingService disPatchingJISService;
	
	@Override
	public List<Map<String, Object>> selectDispatchingJISBillPicking(
			Map<String, Object> params) {
		List<Map<String, Object>> dispatchingJISretlist=dispatchingJISBillPickingDAO.selectDispatchingJISBillPicking(params);
		return dispatchingJISretlist;
	}

	@Override
	public List<Map<String, Object>> selectAssemblySortType(
			Map<String, Object> params) {
		List<Map<String, Object>> assemblySortTyperetlist=dispatchingJISBillPickingDAO.selectAssemblySortType(params);
		return assemblySortTyperetlist;
	}

	@Override
	public List<Map<String, Object>> selectAssemblyLogistics(
			Map<String, Object> params) {
		List<Map<String, Object>> assemblyLogisticsRetlist=dispatchingJISBillPickingDAO.selectAssemblyLogistics(params);
		return assemblyLogisticsRetlist;
	}

	@Override
	public List<Map<String, Object>> selectDispatchingJISBillPickingByDispatcingNo(
			Map<String, Object> params) {
		List<Map<String, Object>> dispatchingJISByDispatchingNoretlist=dispatchingJISBillPickingDAO.selectDispatchingJISBillPickingByDispatcingNo(params);
		return dispatchingJISByDispatchingNoretlist;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void JISPicking(List<Map<String, Object>> paramlist) {
		List<Map<String, Object>> itemparamlist=new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> headerparamlist=new ArrayList<Map<String, Object>>();
		
		for(int m=0;m<paramlist.size();m++){//
			List<Map<String,Object>> stockMatListParams =new ArrayList<Map<String,Object>>();
			Map<String,Object> stockMatParam=new HashMap<String,Object>();
			stockMatParam.put("WERKS", paramlist.get(m).get("FROM_PLANT_CODE")==null?"":paramlist.get(m).get("FROM_PLANT_CODE").toString());//供应工厂
			stockMatParam.put("LGORT", paramlist.get(m).get("LGORT_F")==null?"":paramlist.get(m).get("LGORT_F").toString());//发货库位
			stockMatParam.put("MATNR", paramlist.get(m).get("MATERIAL_CODE")==null?"":paramlist.get(m).get("MATERIAL_CODE").toString());//物料号
			stockMatParam.put("LIFNR", paramlist.get(m).get("VENDOR_CODE")==null?"":paramlist.get(m).get("VENDOR_CODE").toString());//供应商编码
			//判断是否启用智能储位
			Map<String, Object> cplantMap=new HashMap<String, Object>();
			cplantMap.put("WERKS", paramlist.get(m).get("FROM_PLANT_CODE"));
			List<WmsCWhEntity> cPlantList=wmsCWhService.selectByMap(cplantMap);
			String IG_FLAG="";
			if(cPlantList!=null&&cPlantList.size()>0){
				IG_FLAG=cPlantList.get(0).getIgFlag();
			}
			List<String> binCodeList=new ArrayList<String>();//启用智能储位的需要在库存表增加储位条件
			if("X".equals(IG_FLAG)){//启用智能储位
				Map<String, Object> corewhbinMap=new HashMap<String, Object>();
				corewhbinMap.put("WH_NUMBER", paramlist.get(m).get("FROM_PLANT_CODE"));//仓库号暂时取供应工厂
				corewhbinMap.put("BIN_TYPE", "02");
				List<Map<String,Object>> bin_code_list=dispatchingJISBillPickingDAO.selectCoreWhBin(corewhbinMap);
				if(bin_code_list!=null&&bin_code_list.size()>0){
					for(Map<String,Object> binCode:bin_code_list){
						binCodeList.add(binCode.get("BIN_CODE").toString());
					}
				}
			if(binCodeList.size()>0){
			stockMatParam.put("binCodeList", binCodeList);
			}
			}
			
			stockMatListParams.add(stockMatParam);
			
			List<Map<String, Object>> componentList=new ArrayList<Map<String, Object>>();//存放插入t_dispatching_component表的数据
			List<Map<String, Object>> pickingList=new ArrayList<Map<String, Object>>();//存放拣配下架的信息
			//需求数量
			BigDecimal splitQty =paramlist.get(m).get("QUANTITY")==null?(BigDecimal.ZERO):new BigDecimal(paramlist.get(m).get("QUANTITY").toString());
			//查询返回 符合条件的 库存
			List<Map<String, Object>> retMaterialStockList=commonService.getMaterialStock(stockMatListParams);
			if(retMaterialStockList!=null&&retMaterialStockList.size()>0){
				componentList=updateStockQty(retMaterialStockList,paramlist.get(m),splitQty);
			}else{
				throw new RuntimeException("工厂  "+paramlist.get(m).get("FROM_PLANT_CODE")+"  物料  "+paramlist.get(m).get("MATERIAL_CODE")+" 库存数量短缺!");
			}
			
			//修改拣配  T_DISPATCHING_COMPONENT 相关的信息
			pickingList=updateDispatchingComponent(componentList,paramlist.get(m));
			//更新 dispatching_item,dispatching_header表状态
			//如果component全为02，则item表为02
			if(ifWholeComponentChangedStatus(paramlist.get(m).get("DISPATCHING_NO").toString(),paramlist.get(m).get("ITEM_NO").toString(),"02")){
				Map<String, Object> itemparam=new HashMap<String, Object>();
				itemparam.put("DISPATCHING_NO", paramlist.get(m).get("DISPATCHING_NO"));
				itemparam.put("ITEM_NO", paramlist.get(m).get("ITEM_NO"));
				itemparam.put("STATUS", "02");
				itemparamlist.add(itemparam);
			}
			if(itemparamlist.size()>0){
				dispatchingJISBillPickingDAO.updateDispatchingStatusItemByList(itemparamlist);
			}
			//如果item全为02，则header表为02
			if(ifWholeItemChangedStatus(paramlist.get(m).get("DISPATCHING_NO").toString(),"02")){
				Map<String, Object> headerparam=new HashMap<String, Object>();
				headerparam.put("DISPATCHING_NO", paramlist.get(m).get("DISPATCHING_NO"));
				headerparam.put("STATUS", "02");
				headerparamlist.add(headerparam);
			}
			
			if(headerparamlist.size()>0){
				dispatchingJISBillPickingDAO.updateDispatchingStatusHeaderByList(headerparamlist);
			}
			//插入拣配下架表 
			insertPicking(pickingList,paramlist.get(m));
		}
		
		
		
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void FeiJISPicking(List<Map<String, Object>> paramlist) {
		List<Map<String, Object>> itemparamlist=new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> headerparamlist=new ArrayList<Map<String, Object>>();
		
		List<Map<String, Object>> likulist=new ArrayList<Map<String, Object>>();
		for(int m=0;m<paramlist.size();m++){//
			String trial_flag=paramlist.get(m).get("TRIAL_FLAG")==null?"":paramlist.get(m).get("TRIAL_FLAG").toString();
			
			List<Map<String,Object>> stockMatListParams =new ArrayList<Map<String,Object>>();
			Map<String,Object> stockMatParam=new HashMap<String,Object>();
			stockMatParam.put("WERKS", paramlist.get(m).get("FROM_PLANT_CODE"));//供应工厂
			stockMatParam.put("LGORT", paramlist.get(m).get("LGORT_F"));//发货库位
			stockMatParam.put("MATNR", paramlist.get(m).get("MATERIAL_CODE"));//物料号
			stockMatParam.put("LIFNR", paramlist.get(m).get("VENDOR_CODE"));//供应商编码
			//判断是否启用智能储位
			Map<String, Object> cplantMap=new HashMap<String, Object>();
			cplantMap.put("WERKS", paramlist.get(m).get("FROM_PLANT_CODE"));
			List<WmsCWhEntity> cPlantList=wmsCWhService.selectByMap(cplantMap);
			String IG_FLAG="";
			if(cPlantList!=null&&cPlantList.size()>0){
				IG_FLAG=cPlantList.get(0).getIgFlag();
			}
			List<String> binCodeList=new ArrayList<String>();//启用智能储位的需要在库存表增加储位条件
			if("X".equals(IG_FLAG)){//启用智能储位
				Map<String, Object> corewhbinMap=new HashMap<String, Object>();
				corewhbinMap.put("WH_NUMBER", paramlist.get(m).get("FROM_PLANT_CODE"));//仓库号暂时取供应工厂
				if(!"1".equals(trial_flag)){//非试装料
					corewhbinMap.put("BIN_TYPE", "02");
				}else if("1".equals(trial_flag)){//试装料
					corewhbinMap.put("BIN_TYPE", "04");
					corewhbinMap.put("BIN_STATUS", "01");
				}
				List<Map<String,Object>> bin_code_list=dispatchingJISBillPickingDAO.selectCoreWhBin(corewhbinMap);
				if(bin_code_list!=null&&bin_code_list.size()>0){
					for(Map<String,Object> binCode:bin_code_list){
						binCodeList.add(binCode.get("BIN_CODE").toString());
					}
				}
			if(binCodeList.size()>0){
			stockMatParam.put("binCodeList", binCodeList);
			}
			}
			
			stockMatListParams.add(stockMatParam);
			List<Map<String, Object>> componentList=new ArrayList<Map<String, Object>>();//存放插入t_dispatching_component表的数据
			List<Map<String, Object>> pickingList=new ArrayList<Map<String, Object>>();//存放拣配下架的信息
			//需求数量
			BigDecimal splitQty =paramlist.get(m).get("QUANTITY")==null?(BigDecimal.ZERO):new BigDecimal(paramlist.get(m).get("QUANTITY").toString());
			//查询返回 符合条件的 库存
			List<Map<String, Object>> retMaterialStock=commonService.getMaterialStock(stockMatListParams);
			if(retMaterialStock!=null&&retMaterialStock.size()>0){//循环查询得出的库存
				componentList=updateStockQty(retMaterialStock,paramlist.get(m),splitQty);
			}else{
				throw new RuntimeException("工厂  "+paramlist.get(m).get("FROM_PLANT_CODE")+"  物料  "+paramlist.get(m).get("MATERIAL_CODE")+" 库存数量短缺!");
			}
			//修改拣配  T_DISPATCHING_COMPONENT 相关的信息
			pickingList=updateDispatchingComponent(componentList,paramlist.get(m));
			//更新 dispatching_item,dispatching_header表状态
			//如果component全为02，则item表为02
			if(ifWholeComponentChangedStatus(paramlist.get(m).get("DISPATCHING_NO").toString(),paramlist.get(m).get("ITEM_NO").toString(),"02")){
				Map<String, Object> itemparam=new HashMap<String, Object>();
				itemparam.put("DISPATCHING_NO", paramlist.get(m).get("DISPATCHING_NO"));
				itemparam.put("ITEM_NO", paramlist.get(m).get("ITEM_NO"));
				itemparam.put("STATUS", "02");
				itemparamlist.add(itemparam);
			}
			if(itemparamlist.size()>0){
				dispatchingJISBillPickingDAO.updateDispatchingStatusItemByList(itemparamlist);
			}
			//如果item全为02，则header表为02
			if(ifWholeItemChangedStatus(paramlist.get(m).get("DISPATCHING_NO").toString(),"02")){
				Map<String, Object> headerparam=new HashMap<String, Object>();
				headerparam.put("DISPATCHING_NO", paramlist.get(m).get("DISPATCHING_NO"));
				headerparam.put("STATUS", "02");
				headerparamlist.add(headerparam);
			}
			if(headerparamlist.size()>0){
				dispatchingJISBillPickingDAO.updateDispatchingStatusHeaderByList(headerparamlist);
			}
			
			//插入拣配下架表 
			insertPicking(pickingList,paramlist.get(m));
			
			//为立库接口准备数据
			
			Map<String, Object> coreWhBinMap=new HashMap<String, Object>();
			coreWhBinMap.put("BIN_CODE", paramlist.get(m).get("PICKING_ADDRESS"));
			coreWhBinMap.put("BIN_TYPE", "05");
			coreWhBinMap.put("BIN_STATUS", "01");
			List<Map<String, Object>> retCoreWhBinlist=dispatchingJISBillPickingDAO.selectCoreWhBin(coreWhBinMap);
			if(retCoreWhBinlist!=null&retCoreWhBinlist.size()>0){//存在，就要调用立库出库接口，传给立库
				for(Map<String, Object> componentTemp:componentList){
				Map<String, Object> likuMap=new HashMap<String, Object>();
				likuMap.put("FROM_PLANT_CODE", componentTemp.get("FROM_PLANT_CODE"));
				likuMap.put("FROM_WAREHOUSING_CODE", componentTemp.get("FROM_WAREHOUSING_CODE"));
				likuMap.put("BARCODE", componentTemp.get("BARCODE"));
				likuMap.put("ITEM_NO", componentTemp.get("ITEM_NO"));
				likuMap.put("MATERIAL_CODE", componentTemp.get("MATERIAL_CODE"));
				likuMap.put("MATERIAL_DESC", componentTemp.get("MATERIAL_DESC"));
				likuMap.put("UNIT", componentTemp.get("UNIT"));
				likuMap.put("TYPE", componentTemp.get("TYPE"));
				likuMap.put("BATCH", componentTemp.get("BATCH"));
				likuMap.put("PACKAGE_MODEL", componentTemp.get("PACKAGE_MODEL"));
				likuMap.put("PACKAGE_QTY", componentTemp.get("PACKAGE_QTY"));
				likuMap.put("QUANTITY", componentTemp.get("QUANTITY"));
				likuMap.put("VENDOR_CODE", componentTemp.get("VENDOR_CODE"));
				likuMap.put("VENDOR_NAME", componentTemp.get("VENDOR_NAME"));
				likuMap.put("LGORT", componentTemp.get("LGORT"));
				likuMap.put("SOBKZ", componentTemp.get("SOBKZ"));
				likuMap.put("CREATE_USER_ID", componentTemp.get("CREATE_USER_ID"));
				likuMap.put("PICKING_ADDRESS", componentTemp.get("PICKING_ADDRESS"));
				likuMap.put("LINE_CATEGORY", componentTemp.get("LINE_CATEGORY"));
				likuMap.put("WAITING_LOCATION", componentTemp.get("WAITING_LOCATION"));
				likuMap.put("ELEVATOR_CODE", componentTemp.get("ELEVATOR_CODE"));
				likuMap.put("DISPATCHING_ADDRESS", componentTemp.get("DISPATCHING_ADDRESS"));
				likuMap.put("REMARK", componentTemp.get("REMARK"));
				likuMap.put("HANDOVER_DATE", componentTemp.get("HANDOVER_DATE"));
				likuMap.put("LINE_REQUIREMENT_DATE",componentTemp.get("LINE_REQUIREMENT_DATE"));
				likulist.add(likuMap);
				}
			}
		}
		
		
		if(likulist.size()>0){//调用立库接口
			//String retliku=outStockLiKu(likulist,"0");//调用立库出库
		}
	}

	public boolean ifWholeComponentChangedStatus(String dispathingNo,String ItemNo,String status){
		Map<String, Object> componentP=new HashMap<String, Object>();
		componentP.put("DISPATCHING_NO", dispathingNo);
		componentP.put("ITEM_NO", ItemNo);
		componentP.put("STATUS", status);
		int conts=dispatchingJISBillPickingDAO.ifWholeComponentChangedStatus(componentP);
		boolean flg=true;
		if(conts>0){
			flg=false;
		}
		return flg;
	}
	
	public boolean ifWholeItemChangedStatus(String dispathingNo,String status){
		Map<String, Object> itemP=new HashMap<String, Object>();
		itemP.put("DISPATCHING_NO", dispathingNo);
		itemP.put("STATUS", status);
		int conts=dispatchingJISBillPickingDAO.ifWholeItemChangedStatus(itemP);
		boolean flg=true;
		if(conts>0){
			flg=false;
		}
		return flg;
	}

	@Override
	public List<Map<String, Object>> selectDispatchingFeiJISBillPicking(
			Map<String, Object> params) {
		List<Map<String, Object>> dispatchingFeiJISretlist=dispatchingJISBillPickingDAO.selectDispatchingFeiJISBillPicking(params);
		return dispatchingFeiJISretlist;
	}
	
	//更新库存 并返回生成组件集合componentList
	public List<Map<String, Object>> updateStockQty(List<Map<String, Object>> retMaterialStock,Map<String, Object> paramMap,BigDecimal splitQty){
		List<Map<String, Object>> componentList=new ArrayList<Map<String, Object>>();//存放插入t_dispatching_component表的数据
		//循环查询得出的库存
		for(int z=0;z<retMaterialStock.size();z++){//循环库存列表
			Map<String, Object> componentBean=new HashMap<String, Object>();
			//库存更新
			//首先找出每条库存的数量
			BigDecimal stockQty=retMaterialStock.get(z).get("STOCK_QTY")==null?(BigDecimal.ZERO):new BigDecimal(retMaterialStock.get(z).get("STOCK_QTY").toString());
			if (splitQty.compareTo(BigDecimal.ZERO) <= 0) {
                break;
            }
			BigDecimal lockingQty = BigDecimal.ZERO;//该行库存下架的数量
			if (splitQty.compareTo(stockQty) >= 0) {
				lockingQty=stockQty;
			}else{
				lockingQty=splitQty;
			}
			Map<String, Object> xjStockMap=new HashMap<String, Object>();
			xjStockMap.put("S_ID", retMaterialStock.get(z).get("ID"));
			
			//下架储位
			Map<String, Object> corewhbinMap=new HashMap<String, Object>();
			corewhbinMap.put("WH_NUMBER", paramMap.get("FROM_PLANT_CODE"));//仓库号暂时取供应工厂
			corewhbinMap.put("BIN_TYPE", "02");
			List<Map<String,Object>> bin_code_list=dispatchingJISBillPickingDAO.selectCoreWhBin(corewhbinMap);
			if(bin_code_list!=null&&bin_code_list.size()>0){
				xjStockMap.put("XJ_BIN_CODE", bin_code_list.get(0).get("BIN_CODE"));
			}else{
				xjStockMap.put("XJ_BIN_CODE", "BBBB");
			}
			
			xjStockMap.put("QTY", lockingQty);
			dispatchingJISBillPickingDAO.updateStockQtyByDispatching(xjStockMap);//更新库存表
			
			//准备修改或者插入T_DISPATCHING_COMPONENT的数据
			if(componentList!=null&&componentList.size()>0){
				
						//componentBean.put("ID", paramlist.get(m).get("ID"));//COMPONENT表的id
						componentBean.put("BATCH", retMaterialStock.get(z).get("BATCH")==null?"":retMaterialStock.get(z).get("BATCH").toString());
						componentBean.put("QTY", lockingQty);
						componentBean.put("VENDOR_CODE", retMaterialStock.get(z).get("LIFNR"));
						componentBean.put("PICKING_ADDRESS", retMaterialStock.get(z).get("BIN_CODE"));
						//用于插入拣配表  数据准备字段
						componentBean.put("LGORT", retMaterialStock.get(z).get("LGORT"));
						componentBean.put("SOBKZ", retMaterialStock.get(z).get("SOBKZ"));
						componentBean.put("BIN_CODE_XJ", retMaterialStock.get(z).get("BIN_CODE"));
						
						componentList.add(componentBean);
					
			}else{//第一次添加
				//componentBean.put("ID", paramlist.get(m).get("ID"));//COMPONENT表的id
				componentBean.put("BATCH", retMaterialStock.get(z).get("BATCH")==null?"":retMaterialStock.get(z).get("BATCH").toString());
				componentBean.put("QTY", lockingQty);
				componentBean.put("VENDOR_CODE", retMaterialStock.get(z).get("LIFNR"));
				componentBean.put("PICKING_ADDRESS", retMaterialStock.get(z).get("BIN_CODE"));
				
				//用于插入拣配表  数据准备字段
				componentBean.put("LGORT", retMaterialStock.get(z).get("LGORT"));
				componentBean.put("SOBKZ", retMaterialStock.get(z).get("SOBKZ"));
				componentBean.put("BIN_CODE_XJ", retMaterialStock.get(z).get("BIN_CODE"));
				
				componentList.add(componentBean);
				
			}
			
			//
			splitQty=splitQty.subtract(lockingQty);//需求数量=需求数量-已下架的数量
		}
		if(splitQty.compareTo(BigDecimal.ZERO)>0){
			throw new RuntimeException("工厂  "+paramMap.get("FROM_PLANT_CODE")+"  物料  "+paramMap.get("MATERIAL_CODE")+" 库存数量短缺!");
		}
	return componentList;
		
	}
	
	//修改拣配  T_DISPATCHING_COMPONENT 相关的信息 方法 
	public List<Map<String, Object>> updateDispatchingComponent(List<Map<String, Object>> componentList,Map<String, Object> paramMap){
		List<Map<String, Object>> pickingList=new ArrayList<Map<String, Object>>();//存放拣配下架的信息
		if(componentList!=null&&componentList.size()>0){
			//componentList中如果 批次，供应商编码，库位，库存类型一样的则合并为一条，数量相加
			for(int r=0;r<componentList.size()-1;r++){
				for(int s=componentList.size()-1;s>r;s--){
					if((componentList.get(r).get("BATCH").equals(componentList.get(s).get("BATCH")))&&
							(componentList.get(r).get("LGORT").equals(componentList.get(s).get("LGORT")))
							){
						componentList.get(r).put("QTY", new BigDecimal(componentList.get(s).get("QTY").toString())
						.add(new BigDecimal(componentList.get(r).get("QTY").toString())));
						componentList.remove(s);
					}
				}
			}
			
			int idx=0;
			for(int k=0;k<componentList.size();k++){
				Map<String, Object> cVendorMap=new HashMap<String, Object>();//用于查询供应商名称
				Map<String, Object> pickBean=new HashMap<String, Object>();//下架信息bean
				
				if(idx==0){//修改component表中当前记录的数量，批次
					//
					String liktx_="";//供应商名称
					cVendorMap.put("WERKS", paramMap.get("FROM_PLANT_CODE"));
					cVendorMap.put("LIFNR", componentList.get(k).get("VENDOR_CODE"));
					List<WmsCVendor> cVendorList=wmsCVendorService.selectByMap(cVendorMap);
					if(cVendorList!=null&&cVendorList.size()>0){
						liktx_=cVendorList.get(0).getShortName();//
					}
					
					Map<String, Object> componentMap=new HashMap<String, Object>();
					componentMap.put("ID", paramMap.get("ID"));
					componentMap.put("BATCH", componentList.get(k).get("BATCH"));
					componentMap.put("QUANTITY", componentList.get(k).get("QTY"));
					componentMap.put("VENDOR_CODE", componentList.get(k).get("VENDOR_CODE"));
					componentMap.put("VENDOR_NAME", liktx_);
					componentMap.put("PICKING_ADDRESS", componentList.get(k).get("PICKING_ADDRESS"));
					componentMap.put("STATUS", "02");
					
					componentMap.put("UPDATE_USER_ID", paramMap.get("USERID"));
					componentMap.put("UPDATE_USER_NAME", paramMap.get("USERNAME"));
					componentMap.put("PICKING_USER_ID", paramMap.get("USERID"));
					componentMap.put("PICKING_USER_NAME", paramMap.get("USERNAME"));
					componentMap.put("UPDATE_DATE", DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
					
					dispatchingJISBillPickingDAO.updateDispatchingComponent(componentMap);
					
					//准备下架信息的插入
					Map<String, Object> componentpickbean=new HashMap<String, Object>();
					componentpickbean.put("ID", paramMap.get("ID"));
					List<Map<String, Object>>  componentpicklist=dispatchingJISBillPickingDAO.selectDispatchingComponent(componentpickbean);
					
					
					if(componentpicklist!=null&&componentpicklist.size()>0){
						pickBean.put("DISPATCHING_NO", componentpicklist.get(0).get("DISPATCHING_NO"));
						pickBean.put("DISPATCHING_ITEM_NO", componentpicklist.get(0).get("ITEM_NO"));
						pickBean.put("BARCODE", componentpicklist.get(0).get("BARCODE"));
						pickBean.put("WERKS", componentpicklist.get(0).get("FROM_PLANT_CODE"));
						pickBean.put("WH_NUMBER", componentpicklist.get(0).get("FROM_WAREHOUSING_CODE"));
						pickBean.put("ITEM_STATUS", "01");
						pickBean.put("DEL", "0");
						pickBean.put("MATNR", componentpicklist.get(0).get("MATERIAL_CODE"));
						pickBean.put("MAKTX", componentpicklist.get(0).get("MATERIAL_DESC"));
						pickBean.put("UNIT", componentpicklist.get(0).get("UNIT"));
						pickBean.put("LIFNR", componentList.get(k).get("VENDOR_CODE"));
						pickBean.put("LIKTX",liktx_);//
						pickBean.put("BIN_CODE", componentList.get(k).get("PICKING_ADDRESS"));
						pickBean.put("LGORT", componentList.get(k).get("LGORT"));
						pickBean.put("BIN_CODE_XJ", componentList.get(k).get("BIN_CODE_XJ"));
						pickBean.put("SOBKZ", componentList.get(k).get("SOBKZ"));
						pickBean.put("QTY", componentList.get(k).get("QTY"));
						pickBean.put("BATCH", componentList.get(k).get("BATCH"));
					}
					pickingList.add(pickBean);
					
				}else{
					Map<String, Object> componentInsert=new HashMap<String, Object>();//准备插入新的component记录
					
					Map<String, Object> componentparam=new HashMap<String, Object>();
					componentparam.put("ID", paramMap.get("ID"));//通过id查询出现有的记录，方便复制后插入新的记录
					List<Map<String, Object>> componentlistret=dispatchingJISBillPickingDAO.selectDispatchingComponent(componentparam);
					if(componentlistret!=null&&componentlistret.size()>0){
						String liktx="";//供应商名称
						cVendorMap.put("WERKS", componentlistret.get(0).get("FROM_PLANT_CODE"));
						cVendorMap.put("LIFNR", componentlistret.get(0).get("VENDOR_CODE"));
						List<WmsCVendor> cVendorList=wmsCVendorService.selectByMap(cVendorMap);							if(cVendorList!=null&&cVendorList.size()>0){
							liktx=cVendorList.get(0).getShortName();//
						}
						
						componentInsert.put("PLANT_CODE", componentlistret.get(0).get("PLANT_CODE"));
						componentInsert.put("FROM_PLANT_CODE", componentlistret.get(0).get("FROM_PLANT_CODE"));
						componentInsert.put("FROM_WAREHOUSING_CODE", componentlistret.get(0).get("FROM_WAREHOUSING_CODE"));
						componentInsert.put("DISPATCHING_NO", componentlistret.get(0).get("DISPATCHING_NO"));
						componentInsert.put("ITEM_NO", componentlistret.get(0).get("ITEM_NO"));
						componentInsert.put("MATERIAL_CODE", componentlistret.get(0).get("MATERIAL_CODE"));
						componentInsert.put("MATERIAL_DESC", componentlistret.get(0).get("MATERIAL_DESC"));
						componentInsert.put("UNIT", componentlistret.get(0).get("UNIT"));
						componentInsert.put("PACKAGE_MODEL", componentlistret.get(0).get("PACKAGE_MODEL"));
						componentInsert.put("PACKAGE_TYPE", componentlistret.get(0).get("PACKAGE_TYPE"));
						componentInsert.put("DEL", componentlistret.get(0).get("DEL"));
						componentInsert.put("VERIFY", componentlistret.get(0).get("VERIFY"));
						componentInsert.put("CREATE_DATE", DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
						componentInsert.put("CREATE_USER_ID", paramMap.get("USERID"));
						componentInsert.put("CREATE_USER_NAME", paramMap.get("USERNAME"));
						componentInsert.put("UPDATE_DATE", componentlistret.get(0).get("UPDATE_DATE"));
						componentInsert.put("UPDATE_USER_ID", componentlistret.get(0).get("UPDATE_USER_ID"));
						componentInsert.put("UPDATE_USER_NAME", componentlistret.get(0).get("UPDATE_USER_NAME"));
						componentInsert.put("PICKING_USER_ID", paramMap.get("USERID"));
						componentInsert.put("PICKING_USER_NAME", paramMap.get("USERNAME"));
						componentInsert.put("AUTO_FLAG", "1");
						componentInsert.put("PACKAGE_QTY", componentlistret.get(0).get("PACKAGE_QTY"));
						componentInsert.put("ACTUAL_PRINT_DATE", componentlistret.get(0).get("ACTUAL_PRINT_DATE"));
						componentInsert.put("ACTUAL_HANDOVER_DATE", componentlistret.get(0).get("ACTUAL_HANDOVER_DATE"));
						componentInsert.put("DELETE_REMARK", componentlistret.get(0).get("DELETE_REMARK"));
						componentInsert.put("HANDOVER_USER_ID", componentlistret.get(0).get("HANDOVER_USER_ID"));
						componentInsert.put("REQUIREMENT_TYPE", componentlistret.get(0).get("REQUIREMENT_TYPE"));
						componentInsert.put("COMFIRM_DATE", componentlistret.get(0).get("COMFIRM_DATE"));
						componentInsert.put("MATERIAL_ATTRE", componentlistret.get(0).get("MATERIAL_ATTRE"));
						
						String barcode=wmsCDocService.getDocNo("", "13");
						componentInsert.put("BARCODE", barcode);
						componentInsert.put("STATUS", "02");
						componentInsert.put("BATCH", componentList.get(k).get("BATCH"));
						componentInsert.put("QUANTITY", componentList.get(k).get("QTY"));
						componentInsert.put("VENDOR_CODE", componentList.get(k).get("VENDOR_CODE"));
						componentInsert.put("VENDOR_NAME", liktx);
						componentInsert.put("PICKING_ADDRESS", componentList.get(k).get("PICKING_ADDRESS"));
						
						Map<String, Object> componentNextNoParam=new HashMap<String, Object>();
						componentNextNoParam.put("DISPATCHING_NO", componentlistret.get(0).get("DISPATCHING_NO"));
						componentNextNoParam.put("ITEM_NO", componentlistret.get(0).get("ITEM_NO"));
						int COMPONENT_NO=dispatchingJISBillPickingDAO.nextComponentNo(componentNextNoParam);
						componentInsert.put("COMPONENT_NO", COMPONENT_NO);
						dispatchingJISBillPickingDAO.insertDispatchingComponent(componentInsert);
						
						//准备下架信息的插入
						
							pickBean.put("DISPATCHING_NO", componentInsert.get("DISPATCHING_NO"));
							pickBean.put("DISPATCHING_ITEM_NO", componentInsert.get("ITEM_NO"));
							pickBean.put("BARCODE", componentInsert.get("BARCODE"));
							pickBean.put("WERKS", componentInsert.get("FROM_PLANT_CODE"));
							pickBean.put("WH_NUMBER", componentInsert.get("FROM_WAREHOUSING_CODE"));
							pickBean.put("ITEM_STATUS", "01");
							pickBean.put("DEL", "0");
							pickBean.put("MATNR", componentInsert.get("MATERIAL_CODE"));
							pickBean.put("MAKTX", componentInsert.get("MATERIAL_DESC"));
							pickBean.put("UNIT", componentInsert.get("UNIT"));
							pickBean.put("LIFNR", componentInsert.get("VENDOR_CODE"));
							pickBean.put("LIKTX",liktx);//
							pickBean.put("BIN_CODE", componentInsert.get("PICKING_ADDRESS"));
							pickBean.put("LGORT", componentList.get(k).get("LGORT"));
							pickBean.put("BIN_CODE_XJ", componentList.get(k).get("BIN_CODE_XJ"));
							pickBean.put("SOBKZ", componentList.get(k).get("SOBKZ"));
							pickBean.put("QTY", componentList.get(k).get("QTY"));
							pickBean.put("BATCH", componentList.get(k).get("BATCH"));
							pickingList.add(pickBean);
					}
				}
				idx++;
			}
		}
		return pickingList;
	}
	
	public void insertPicking(List<Map<String, Object>> pickingList,Map<String, Object> paramMap){
		String pick_no=wmsCDocService.getDocNo("", "11");
		for(int d=0;d<pickingList.size();d++){//补全在上面生成没有set的字段
			pickingList.get(d).put("PICK_NO", pick_no);
			pickingList.get(d).put("PICK_ITEM_NO", d+1);
			pickingList.get(d).put("BUSINESS_CLASS", "");
			pickingList.get(d).put("BUSINESS_NAME", "");
			pickingList.get(d).put("BUSINESS_TYPE", "");
			pickingList.get(d).put("ITEM_TEXT", "");
			pickingList.get(d).put("HANDOVER_QTY", "");
			pickingList.get(d).put("CONFIRM", "");
			pickingList.get(d).put("CONFIRM_DATE", "");
			pickingList.get(d).put("LGORT_RECEIVE", "");
			pickingList.get(d).put("HANDOVER", "");
			pickingList.get(d).put("HANDOVER_DATE", "");
			pickingList.get(d).put("CREATOR", paramMap.get("USERID"));
			pickingList.get(d).put("CREATE_DATE", DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
			pickingList.get(d).put("EDITOR", "");
			pickingList.get(d).put("EDIT_DATE", "");
		}
		dispatchingJISBillPickingDAO.insertTPicking(pickingList);
	}

	@Override
	public void printJIS(List<Map<String, Object>> paramlist) {
		List<Map<String, Object>> componentparamList=new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> itemparamList=new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> headerparamList=new ArrayList<Map<String, Object>>();
		
		for(int m=0;m<paramlist.size();m++){
			
			//更新三个拣配相关表的状态为03 拣配中
			Map<String, Object> componentparam=new HashMap<String, Object>();
			componentparam.put("DISPATCHING_NO", paramlist.get(m).get("DISPATCHING_NO"));
			componentparam.put("STATUS", "03");
			//dispatchingJISBillPickingDAO.updateDispatchingStatusComponent(componentparam);
			componentparamList.add(componentparam);
			
			if(componentparamList.size()>0){
				dispatchingJISBillPickingDAO.updateDispatchingStatusComponentByList(componentparamList);
			}
			
			Map<String, Object> itemparam=new HashMap<String, Object>();
			itemparam.put("DISPATCHING_NO", paramlist.get(m).get("DISPATCHING_NO"));
			itemparam.put("STATUS", "03");
			itemparamList.add(itemparam);
			
			if(itemparamList.size()>0){
				dispatchingJISBillPickingDAO.updateDispatchingStatusItemByList(itemparamList);
			}
			
			Map<String, Object> headerparam=new HashMap<String, Object>();
			headerparam.put("DISPATCHING_NO", paramlist.get(m).get("DISPATCHING_NO"));
			headerparam.put("STATUS", "03");
			headerparamList.add(headerparam);
			
			if(headerparamList.size()>0){
				dispatchingJISBillPickingDAO.updateDispatchingStatusHeaderByList(headerparamList);
			}
		}
		
		
	}

	@Override
	public List<Map<String, Object>> selectDispatchingQueRen(Map<String, Object> params) {
		if(params.get("DISPATCHING_NO")!=null||!"".equals(params.get("DISPATCHING_NO"))){
			if(isDispatchingNo(params.get("DISPATCHING_NO").toString())){
				params.put("DISPATCHING_NO", params.get("DISPATCHING_NO"));
				params.put("BARCODE", "");
			}else{
				params.put("BARCODE", params.get("DISPATCHING_NO"));
				params.put("DISPATCHING_NO", "");
				
			}
		}
		
		List<Map<String, Object>> dispatchingretlist=dispatchingJISBillPickingDAO.selectDispatchingQueRen(params);
		return dispatchingretlist;
	}

	@Override
	/**
	 * 验证下架库存数量
	 */
	public void checkDispatchingQueRen(List<Map<String, Object>> paramlist) {
		for(int m=0;m<paramlist.size();m++){//
			List<Map<String,Object>> stockMatListParams =new ArrayList<Map<String,Object>>();
			Map<String,Object> stockMatParam=new HashMap<String,Object>();
			stockMatParam.put("WERKS", paramlist.get(m).get("FROM_PLANT_CODE"));//供应工厂
			stockMatParam.put("BATCH", paramlist.get(m).get("BATCH"));//批次
			stockMatParam.put("MATNR", paramlist.get(m).get("MATNR"));//物料号
			
			stockMatListParams.add(stockMatParam);
			
			//查询返回 符合条件的 库存
			BigDecimal stockQty=BigDecimal.ZERO;
			List<Map<String, Object>> retMaterialStock=commonService.getMaterialStock(stockMatListParams);
			//取出库存里面的下架数量
			if(retMaterialStock!=null&&retMaterialStock.size()>0){
				for(Map<String, Object> MapBean:retMaterialStock){
					BigDecimal stockQtyTemp=MapBean.get("XJ_QTY")==null?(BigDecimal.ZERO):new BigDecimal(MapBean.get("XJ_QTY").toString());
					stockQty=stockQty.add(stockQtyTemp);
				}
			}
			
			//拣配表里面的数量
			BigDecimal querenQty =paramlist.get(m).get("QUANTITY")==null?(BigDecimal.ZERO):new BigDecimal(paramlist.get(m).get("QUANTITY").toString());
			if(querenQty.compareTo(stockQty)>0){//如果拣配确认的数量比库存中的下架数量小，报错
				throw new RuntimeException(paramlist.get(m).get("BATCH")+"批次 下架数量短缺"+(querenQty.subtract(stockQty)));
			}
		}
	}

	@Override
	public void updateQueRen(List<Map<String, Object>> paramlist) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		List<Map<String, Object>> componentparamList=new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> itemparamList=new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> headerparamList=new ArrayList<Map<String, Object>>();
		for(int m=0;m<paramlist.size();m++){
			
			//更新三个拣配相关表的状态为08拣配中
			Map<String, Object> componentparam=new HashMap<String, Object>();
			componentparam.put("DISPATCHING_NO", paramlist.get(m).get("DISPATCHING_NO"));
			componentparam.put("ITEM_NO", paramlist.get(m).get("ITEM_NO"));
			componentparam.put("COMPONENT_NO", paramlist.get(m).get("COMPONENT_NO"));
			componentparam.put("STATUS", "08");
			
			//更新拣配确认时间
			componentparam.put("COMFIRM_DATE", sdf.parse(sdf.format(new Date())));
			
			//更新t_picking状态
			componentparam.put("BARCODE", paramlist.get(m).get("BARCODE"));
			componentparam.put("ITEM_STATUS", "02");
			
			componentparamList.add(componentparam);
			
			if(componentparamList.size()>0){
				dispatchingJISBillPickingDAO.updateDispatchingStatusComponentByList(componentparamList);
				dispatchingJISBillPickingDAO.updateDispatchingConformDateByList(componentparamList);
				
				//根据barcode 更新t_picking
				dispatchingJISBillPickingDAO.updateTPickingByBarcodeByList(componentparamList);
			}
			
			if(ifWholeComponentChangedStatus(paramlist.get(m).get("DISPATCHING_NO").toString(),paramlist.get(m).get("ITEM_NO").toString(),"08")){
				Map<String, Object> itemparam=new HashMap<String, Object>();
				itemparam.put("DISPATCHING_NO", paramlist.get(m).get("DISPATCHING_NO"));
				itemparam.put("ITEM_NO", paramlist.get(m).get("ITEM_NO"));
				itemparam.put("STATUS", "08");
				
				itemparamList.add(itemparam);
			}
			
			if(itemparamList.size()>0){
				dispatchingJISBillPickingDAO.updateDispatchingStatusItemByList(itemparamList);
			}
			
			if(ifWholeItemChangedStatus(paramlist.get(m).get("DISPATCHING_NO").toString(),"08")){
				Map<String, Object> headerparam=new HashMap<String, Object>();
				headerparam.put("DISPATCHING_NO", paramlist.get(m).get("DISPATCHING_NO"));
				headerparam.put("STATUS", "08");
				headerparamList.add(headerparam);
			}
			if(headerparamList.size()>0){
				dispatchingJISBillPickingDAO.updateDispatchingStatusHeaderByList(headerparamList);
			}
			
		}
		
		
		
	}

	@Override
	//@Transactional(rollbackFor = Exception.class)
	public void updateJiaojie(List<Map<String, Object>> paramlist) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		List<Map<String, Object>> componentparamList=new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> itemparamList=new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> headerparamList=new ArrayList<Map<String, Object>>();
		
		for(int m=0;m<paramlist.size();m++){
			//更新三个拣配相关表的状态为04拣配中
			Map<String, Object> componentparam=new HashMap<String, Object>();
			componentparam.put("DISPATCHING_NO", paramlist.get(m).get("DISPATCHING_NO"));
			componentparam.put("ITEM_NO", paramlist.get(m).get("ITEM_NO"));
			componentparam.put("COMPONENT_NO", paramlist.get(m).get("COMPONENT_NO"));
			componentparam.put("STATUS", "04");
			
			//更新需求交接时间 交接人 
			
			componentparam.put("ACTUAL_HANDOVER_DATE", sdf.parse(sdf.format(new Date())));
			componentparam.put("HANDOVER_USER_ID", paramlist.get(m).get("USERID"));
			
			componentparamList.add(componentparam);
			
			if(componentparamList.size()>0){
				dispatchingJISBillPickingDAO.updateDispatchingStatusComponentByList(componentparamList);
				dispatchingJISBillPickingDAO.updateDispatchingHandoverDateByList(componentparamList);
			}
			
			if(ifWholeComponentChangedStatus(paramlist.get(m).get("DISPATCHING_NO").toString(),paramlist.get(m).get("ITEM_NO").toString(),"04")){
				Map<String, Object> itemparam=new HashMap<String, Object>();
				itemparam.put("DISPATCHING_NO", paramlist.get(m).get("DISPATCHING_NO"));
				itemparam.put("ITEM_NO", paramlist.get(m).get("ITEM_NO"));
				itemparam.put("STATUS", "04");
				itemparamList.add(itemparam);
			}
			
			if(itemparamList.size()>0){
				dispatchingJISBillPickingDAO.updateDispatchingStatusItemByList(itemparamList);
			}
			
			if(ifWholeItemChangedStatus(paramlist.get(m).get("DISPATCHING_NO").toString(),"04")){
				Map<String, Object> headerparam=new HashMap<String, Object>();
				headerparam.put("DISPATCHING_NO", paramlist.get(m).get("DISPATCHING_NO"));
				headerparam.put("STATUS", "04");
				headerparamList.add(headerparam);
			}
			
			if(headerparamList.size()>0){
				dispatchingJISBillPickingDAO.updateDispatchingStatusHeaderByList(headerparamList);
			}
			
		}
		
	}
	/**
	 * 验证交接的库存数量是否满足
	 * @param paramlist
	 */
	public void checkJiaojieQty(List<Map<String, Object>> paramlist){
		//如果交接只允许同一个供应工厂
		Map<String, Object> logisticsMap=new HashMap<String, Object>();
		List<Map<String, Object>>  assemblyLogistics=disPatchingJISService.selectAssemblyLogistics(logisticsMap);
		//查询出配置表所有的记录
		if(assemblyLogistics!=null&&assemblyLogistics.size()>0){
			for(Map<String, Object> param:paramlist){
				for(Map<String, Object> assembly:assemblyLogistics){
					if(param.get("FROM_PLANT_CODE").equals(assembly.get("WERKS_F"))){
						//取出WMS_FLAG_F
						String wms_flag_f=assembly.get("WMS_FLAG_F")==null?"":assembly.get("WMS_FLAG_F").toString();
					}
				}
			}
		}
	} 
	
	@Override
	public List<Map<String, Object>> selectDispatchingBillPickingByPrint(
			Map<String, Object> params) {
		List<Map<String, Object>> dispatchingJISretlist=dispatchingJISBillPickingDAO.selectDispatchingBillPickingByPrint(params);
		return dispatchingJISretlist;
	}

	@Override
	public void printFeiJIS(List<Map<String, Object>> paramlist) {
		List<Map<String, Object>> componentparamList=new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> itemparamList=new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> headerparamList=new ArrayList<Map<String, Object>>();
		for(int m=0;m<paramlist.size();m++){
			
			//更新三个拣配相关表的状态为03 拣配中
			Map<String, Object> componentparam=new HashMap<String, Object>();
			componentparam.put("DISPATCHING_NO", paramlist.get(m).get("DISPATCHING_NO"));
			componentparam.put("ITEM_NO", paramlist.get(m).get("ITEM_NO"));
			componentparam.put("COMPONENT_NO", paramlist.get(m).get("COMPONENT_NO"));
			componentparam.put("STATUS", "03");
			componentparamList.add(componentparam);
			
			if(componentparamList.size()>0){
				dispatchingJISBillPickingDAO.updateDispatchingStatusComponentByList(componentparamList);
			}
			
		if(ifWholeComponentChangedStatus(paramlist.get(m).get("DISPATCHING_NO").toString(),paramlist.get(m).get("ITEM_NO").toString(),"03")){
			Map<String, Object> itemparam=new HashMap<String, Object>();
			itemparam.put("DISPATCHING_NO", paramlist.get(m).get("DISPATCHING_NO"));
			itemparam.put("ITEM_NO", paramlist.get(m).get("ITEM_NO"));
			itemparam.put("STATUS", "03");
			itemparamList.add(itemparam);
		}
		if(itemparamList.size()>0){
			dispatchingJISBillPickingDAO.updateDispatchingStatusItemByList(itemparamList);
		}
		
		if(ifWholeItemChangedStatus(paramlist.get(m).get("DISPATCHING_NO").toString(),"03")){
			Map<String, Object> headerparam=new HashMap<String, Object>();
			headerparam.put("DISPATCHING_NO", paramlist.get(m).get("DISPATCHING_NO"));
			headerparam.put("STATUS", "03");
			headerparamList.add(headerparam);
		}
		if(headerparamList.size()>0){
			dispatchingJISBillPickingDAO.updateDispatchingStatusHeaderByList(headerparamList);
		}
		
		}
		
	}
	
	@Override
	public List<Map<String, Object>> selectDispatchingByfabu(Map<String, Object> params) {
		if(params.get("BARCODE")!=null&&!"".equals(params.get("BARCODE"))){
			if(isDispatchingNo(params.get("BARCODE").toString())){
				params.put("DISPATCHING_NO", params.get("BARCODE"));
				params.put("BARCODE", "");
			}else{
				params.put("BARCODE", params.get("BARCODE"));
				params.put("DISPATCHING_NO", "");
				
			}
		}
		List<Map<String, Object>> dispatchingretlist=dispatchingJISBillPickingDAO.selectDispatchingByfabu(params);
		return dispatchingretlist;
	}
	
	/**
     * 判断是否为拣配单号 目前定义的规则是：以54开头且长度小于等于12为拣配单号，否则就是包装号
     *
     * @param dispatchingNo
     * @return
     */
    protected boolean isDispatchingNo(String dispatchingNo) {
        if (dispatchingNo.startsWith("54") && dispatchingNo.length() <= 12) {
            return true;
        } else if(dispatchingNo.startsWith("56")&& dispatchingNo.length() <= 12){
            return true;
        }else {
            return false;
        }
    }

    /**
     * sap过账，保存wms凭证信息
     */
	@Override
	//@Transactional(rollbackFor = Exception.class)
	public Map<String, Object> sapJiaojie(List<Map<String, Object>> params) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		List<List<Map<String,Object>>> sapList=new ArrayList<List<Map<String,Object>>>();//sap过账准备的
		List<List<Map<String,Object>>> wmsList=new ArrayList<List<Map<String,Object>>>();//wms保存事务记录准备的
		
		List<Map<String, Object>> listsaptempMap = new ArrayList<Map<String, Object>>();
		
		List<Map<String, Object>> matlist=new ArrayList<Map<String, Object>>();
		String SAP_MOVE_TYPE="";
		
		String sap_move_type="";
		String wms_move_type="";
        String sobkz="";
        String lgort_f="";
        String wms_flag_f="";//是否上wms
        String sap_flag_f="";//是否过账sap
        Map<String, Object> logisticsMap=new HashMap<String, Object>();
		logisticsMap.put("WERKS_F",params.get(0).get("FROM_PLANT_CODE"));
		List<Map<String, Object>>  assemblyLogistics=dispatchingJISBillPickingDAO.selectAssemblyLogistics(logisticsMap);
		if(assemblyLogistics!=null&&assemblyLogistics.size()>0){
			sap_move_type=assemblyLogistics.get(0).get("SAP_MOVE_TYPE")==null?"":assemblyLogistics.get(0).get("SAP_MOVE_TYPE").toString();
			wms_move_type=assemblyLogistics.get(0).get("WMS_MOVE_TYPE")==null?"":assemblyLogistics.get(0).get("WMS_MOVE_TYPE").toString();
			sobkz=assemblyLogistics.get(0).get("SOBKZ")==null?"":assemblyLogistics.get(0).get("SOBKZ").toString();
			lgort_f=assemblyLogistics.get(0).get("LGORT_F")==null?"":assemblyLogistics.get(0).get("LGORT_F").toString();
			wms_flag_f=assemblyLogistics.get(0).get("WMS_FLAG_F")==null?"":assemblyLogistics.get(0).get("WMS_FLAG_F").toString();
			sap_flag_f=assemblyLogistics.get(0).get("SAP_FLAG_F")==null?"":assemblyLogistics.get(0).get("SAP_FLAG_F").toString();
			
			SAP_MOVE_TYPE=sap_move_type;
		}
		
		for(int i=0;i<params.size();i++){
			//准备过账的业务类型，移动类型数据
			
			Map<String, Object> cond=new HashMap<String, Object>();
			String business_name="46";
			String business_class="07";
			cond.put("BUSINESS_NAME", business_name);
			cond.put("BUSINESS_CLASS", business_class);
			cond.put("SOBKZ", sobkz);
			List<Map<String, Object>> retBusinessInfo=wmsInInboundDao.getBusinessInfo_cond(cond);
			String business_type="";
			if(retBusinessInfo!=null&&retBusinessInfo.size()>0){
				business_type=retBusinessInfo.get(0).get("BUSINESS_TYPE").toString();
			}
			
			//查询TPICKING
			Map<String, Object> pickTemp=new HashMap<String, Object>();
			pickTemp.put("BARCODE", params.get(i).get("BARCODE"));
			List<Map<String, Object>> retPicklist=dispatchingJISBillPickingDAO.selectTPicking(pickTemp);
			String bin_code="";
			String unit="";
			String lifnr="";
			String whnumber="";
			String werks="";
			if(retPicklist!=null&&retPicklist.size()>0){
				bin_code=retPicklist.get(0).get("BIN_CODE")==null?"":retPicklist.get(0).get("BIN_CODE").toString();
				unit=retPicklist.get(0).get("UNIT")==null?"":retPicklist.get(0).get("UNIT").toString();
				lifnr=retPicklist.get(0).get("LIFNR")==null?"":retPicklist.get(0).get("LIFNR").toString();
				werks=retPicklist.get(0).get("WERKS")==null?"":retPicklist.get(0).get("WERKS").toString();
				whnumber=retPicklist.get(0).get("WH_NUMBER")==null?"":retPicklist.get(0).get("WH_NUMBER").toString();
			}
			
			Map<String, Object> docitemMap = new HashMap <String, Object>();
			
			docitemMap.put("BUSINESS_NAME", business_name);
			docitemMap.put("BUSINESS_TYPE", business_type);
			docitemMap.put("WMS_MOVE_TYPE", wms_move_type);
			docitemMap.put("SAP_MOVE_TYPE", sap_move_type);
			docitemMap.put("SOBKZ", sobkz);
			docitemMap.put("MATNR", params.get(i).get("MATNR"));
			docitemMap.put("MAKTX", params.get(i).get("MAKTX"));
			docitemMap.put("WERKS", werks);
			docitemMap.put("WH_NUMBER", whnumber);
			docitemMap.put("LGORT", lgort_f);
			docitemMap.put("BIN_CODE", bin_code);
			docitemMap.put("UNIT", unit);
			docitemMap.put("QTY_WMS", params.get(i).get("QUANTITY"));
			docitemMap.put("QTY_SAP", params.get(i).get("QUANTITY"));
			docitemMap.put("BATCH", params.get(i).get("BATCH"));
			docitemMap.put("BATCH_SAP", params.get(i).get("BATCH"));
			docitemMap.put("HANDOVER", params.get(0).get("USERNAME"));
			docitemMap.put("RECEIPT_NO", "");
			docitemMap.put("RECEIPT_ITEM_NO", "");
			docitemMap.put("PO_NO", "");//po_no_str
			docitemMap.put("PO_ITEM_NO", "");//po_item_no_str
			docitemMap.put("LIFNR", lifnr);
			docitemMap.put("MO_NO", "");
			docitemMap.put("MO_ITEM_NO", "");
			docitemMap.put("IO_NO", "");
			docitemMap.put("COST_CENTER", "");
			docitemMap.put("WBS", "");
			docitemMap.put("SAKTO", "");
			docitemMap.put("ANLN1", "");
			docitemMap.put("INBOUND_NO", "");
			docitemMap.put("INBOUND_ITEM_NO", "");
			docitemMap.put("SAP_MATDOC_NO", "");
			docitemMap.put("SAP_MATDOC_ITEM_NO", "");
			docitemMap.put("CREATOR", params.get(0).get("USERNAME"));
			docitemMap.put("CREATE_DATE", DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));//
			docitemMap.put("ITEM_TEXT", "");
			docitemMap.put("HEADER_TXT","");
			docitemMap.put("REF_SAP_MATDOC_YEAR", "");
			docitemMap.put("REF_SAP_MATDOC_NO", "");
			docitemMap.put("REF_SAP_MATDOC_ITEM_NO", "");
			
			docitemMap.put("BUSINESS_CLASS", business_class);
			
			matlist.add(docitemMap);
			
			Map<String, Object> sapmap=new HashMap<String, Object>();
			sapmap.put("BUSINESS_CLASS", business_class);
			sapmap.put("BUSINESS_NAME", business_name);
			sapmap.put("BUSINESS_TYPE", business_type);
			sapmap.put("WERKS", werks);
			sapmap.put("SOBKZ", sobkz);
			sapmap.put("LIFNR", lifnr);
			
			sapmap.put("PZ_DATE", params.get(i).get("PZDDT"));
			sapmap.put("JZ_DATE", params.get(i).get("JZDDT"));
			
			sapmap.put("SAP_MOVE_TYPE", SAP_MOVE_TYPE);
			
			sapmap.put("matList", matlist);
			
			listsaptempMap.add(sapmap);
			sapList.add(listsaptempMap);
		}
		
		//wms事务记录
		wmsList.add(matlist);
		
		//生成凭证信息  
		Map<String,Object> head=new HashMap<String,Object>();
		head.put("WERKS", params.get(0).get("FROM_PLANT_CODE"));
		head.put("PZ_DATE", params.get(0).get("PZDDT"));
		head.put("JZ_DATE", params.get(0).get("JZDDT"));
		head.put("PZ_YEAR", params.get(0).get("PZDDT").toString().substring(0,4));
		head.put("HEADER_TXT", "");
		head.put("TYPE",  "00");//标准凭证
		head.put("CREATOR", params.get(0).get("USERNAME"));
		head.put("CREATE_DATE", DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));//
		head.put("SAP_MOVE_TYPE",  SAP_MOVE_TYPE);
		String WMS_NO="";
		if(wmsList!=null&&wmsList.size()>0){
			for(int p=0;p<wmsList.size();p++){
					String WMS_NO_temp=commonService.saveWMSDoc(head, wmsList.get(p));
					WMS_NO=WMS_NO+" "+WMS_NO_temp;
				}
			}
		
		// 过账sap
		
		String SAP_NO="";
		if("01".equals(sap_flag_f)){//实时过账的
		for(int d=0;d<sapList.size();d++){
			List<Map<String,Object>> sapmaplist=sapList.get(d);
			for(int e=0;e<sapmaplist.size();e++){
				String SAP_NO_temp=commonService.doSapPostForWlms(sapmaplist.get(e));
				SAP_NO=SAP_NO+" "+SAP_NO_temp;
			}
		}
		}
		
		StringBuilder msg=new StringBuilder();
		
		msg.append(" WMS凭证:");
		msg.append(WMS_NO);
		if(StringUtils.isNotBlank(SAP_NO)) {
			msg.append(" SAP凭证:");
			msg.append(SAP_NO);
		}
		Map<String,Object> retmap=new HashMap<String,Object>();
		retmap.put("retstr", msg);
		return retmap;
		
	}

	@Override
	public List<Map<String, Object>> selectDispatchingItem(Map<String, Object> params) {
		// TODO Auto-generated method stub
		return dispatchingJISBillPickingDAO.selectDispatchingItem(params);
	}

	@Override
	public List<Map<String, Object>> selectDispatchingHeader(Map<String, Object> params) {
		// TODO Auto-generated method stub
		return dispatchingJISBillPickingDAO.selectDispatchingHeader(params);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateFabu(List<Map<String, Object>> paramlist) {
		for(int m=0;m<paramlist.size();m++){
			//更新三个拣配相关表的状态为01拣配中
			Map<String, Object> componentparam=new HashMap<String, Object>();
			componentparam.put("DISPATCHING_NO", paramlist.get(m).get("DISPATCHING_NO"));
			componentparam.put("ITEM_NO", paramlist.get(m).get("ITEM_NO"));
			componentparam.put("COMPONENT_NO", paramlist.get(m).get("COMPONENT_NO"));
			componentparam.put("STATUS", "01");
			dispatchingJISBillPickingDAO.updateDispatchingStatusComponent(componentparam);
			
			//更新需求交接时间 交接人 
			componentparam.put("HANDOVER_USER_ID", paramlist.get(m).get("USERID"));
			dispatchingJISBillPickingDAO.updateDispatchingHandoverDate(componentparam);
			
			if(ifWholeComponentChangedStatus(paramlist.get(m).get("DISPATCHING_NO").toString(),paramlist.get(m).get("ITEM_NO").toString(),"01")){
				Map<String, Object> itemparam=new HashMap<String, Object>();
				itemparam.put("DISPATCHING_NO", paramlist.get(m).get("DISPATCHING_NO"));
				itemparam.put("ITEM_NO", paramlist.get(m).get("ITEM_NO"));
				itemparam.put("STATUS", "01");
				dispatchingJISBillPickingDAO.updateDispatchingStatusItem(itemparam);
			}
			
			if(ifWholeItemChangedStatus(paramlist.get(m).get("DISPATCHING_NO").toString(),"01")){
				Map<String, Object> headerparam=new HashMap<String, Object>();
				headerparam.put("DISPATCHING_NO", paramlist.get(m).get("DISPATCHING_NO"));
				headerparam.put("STATUS", "01");
				dispatchingJISBillPickingDAO.updateDispatchingStatusHeader(headerparam);
			}
			
			
		}
		
	}

	
	@Override
	@Transactional(rollbackFor = Exception.class)
	public String dispatchingFabu(List<Map<String, Object>> paramslist) throws ParseException {
		List<Map<String, Object>> likulist=new ArrayList<Map<String, Object>>();
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		//调用新物流需求重新发布接口  
				WSWMSDispatchingList wswmsDispatchinglist=new WSWMSDispatchingList();
		        wswmsDispatchinglist.setUserName("WLMS");
		        wswmsDispatchinglist.setPassword("WLMS2014");
		        ArrayOfWSWMSDispatchingHeader arrayWsWmsDispatchingHeader=new ArrayOfWSWMSDispatchingHeader();
	            List<WSWMSDispatchingHeader> wswmsDispatchingHeaderlist=arrayWsWmsDispatchingHeader.getWSWMSDispatchingHeader();
		
		//更新三个表状态为01-发布
		this.updateFabu(paramslist);
		
		//非限制-STOCK_QTY数量（增加）、下架数量- XJ_QTY（减少）
				//1 根据barcode查询t_picking对应的数据
				for(Map<String, Object> tempMap:paramslist){
					Map<String, Object> pickTemp=new HashMap<String, Object>();
					pickTemp.put("BARCODE", tempMap.get("BARCODE"));
					List<Map<String, Object>> retPicklist=dispatchingJISBillPickingDAO.selectTPicking(pickTemp);
					//2 根据t_picking表中的储位，批次等条件跟新库存表
					if(retPicklist!=null&&retPicklist.size()>0){
						Map<String, Object> stockMap=new HashMap<String, Object>();
						stockMap.put("WERKS", retPicklist.get(0).get("WERKS"));
						stockMap.put("WH_NUMBER", retPicklist.get(0).get("WH_NUMBER"));
						stockMap.put("BATCH", retPicklist.get(0).get("BATCH"));
						stockMap.put("BIN_CODE", retPicklist.get(0).get("BIN_CODE"));
						stockMap.put("LIFNR", retPicklist.get(0).get("LIFNR"));
						stockMap.put("SOBKZ", retPicklist.get(0).get("SOBKZ"));
						stockMap.put("QTY", retPicklist.get(0).get("QTY"));
						dispatchingJISBillPickingDAO.updateStockQtyByBatch(stockMap);
					}
					
					//清空【T_DISPATCHING_COMPONENT】中对应的‘PICKING_USER_ID’ ‘PICKING_USER_NAME’、‘批次-batch’、、‘拣配地址-PICKING_ADDRESS’‘供应商代码-VENDOR_CODE’‘供应商名称-VENDOR_NAME’
					dispatchingJISBillPickingDAO.updateDispatchingPickingInfoByBarcode(pickTemp);
					
					//更新【T_PICKING】字段DEL=X
					dispatchingJISBillPickingDAO.delTPickingByBarcode(pickTemp);
					
					//准备调用物流接口的数据
					//1 通过barcode查询组件表
					List<Map<String, Object>> retComponentlist=dispatchingJISBillPickingDAO.selectComponentByBarcode(pickTemp);
					//准备头表
					
	                WSWMSDispatchingHeader wsDispatchingHeader=new WSWMSDispatchingHeader();
	                wsDispatchingHeader.setOperateType("RELEASE");
	                wsDispatchingHeader.setDISPATCHINGNO(retComponentlist.get(0).get("DISPATCHING_NO").toString());
	                wsDispatchingHeader.setSTATUS(retComponentlist.get(0).get("STATUS").toString());//发布状态
	                wsDispatchingHeader.setIsExeOperate(1);//立即执行
	                
	              //准备行表
	                ArrayOfWSWMSDispatchingItem listItem=new ArrayOfWSWMSDispatchingItem();
	                List<WSWMSDispatchingItem> wswmsDispatchingItem=listItem.getWSWMSDispatchingItem();
	                WSWMSDispatchingItem wsDispatchingItem=new WSWMSDispatchingItem();
	                wsDispatchingItem.setDISPATCHINGNO(retComponentlist.get(0).get("DISPATCHING_NO").toString());
	                wsDispatchingItem.setITEMNO(retComponentlist.get(0).get("ITEM_NO").toString());
	                wsDispatchingItem.setSTATUS(retComponentlist.get(0).get("STATUS").toString());//发布状态
	                wsDispatchingItem.setIsExeOperate(1);//立即执行
	                
	              //准备组件表
	                ArrayOfWSWMSDispatchingComponent listComponent=new ArrayOfWSWMSDispatchingComponent();
	                List<WSWMSDispatchingComponent> wswmsDispatchingComponent=listComponent.getWSWMSDispatchingComponent();
	                WSWMSDispatchingComponent wsDispatchingComponent=new WSWMSDispatchingComponent();
	                wsDispatchingComponent.setBARCODE(retComponentlist.get(0).get("BARCODE").toString());
	                wsDispatchingComponent.setDISPATCHINGNO(retComponentlist.get(0).get("DISPATCHING_NO").toString());
	                wsDispatchingComponent.setITEMNO(retComponentlist.get(0).get("ITEM_NO").toString());
	                wsDispatchingComponent.setCOMPONENTNO(retComponentlist.get(0).get("COMPONENT_NO").toString());
	                wsDispatchingComponent.setSTATUS(retComponentlist.get(0).get("STATUS").toString());//发布状态
	                wsDispatchingComponent.setIsExeOperate(1);//立即执行
	                
	                wswmsDispatchingComponent.add(wsDispatchingComponent);
	                wsDispatchingItem.setListComponent(listComponent);
	                
	                wswmsDispatchingItem.add(wsDispatchingItem);
	                
	                
	                
	                wsDispatchingHeader.setListItem(listItem);
	                
	                
	                wswmsDispatchingHeaderlist.add(wsDispatchingHeader);
	                
	                //准备插入立库数据
	                Map<String, Object> coreWhBinMap=new HashMap<String, Object>();
	    			coreWhBinMap.put("BIN_CODE", tempMap.get("PICKING_ADDRESS"));
	    			coreWhBinMap.put("BIN_TYPE", "05");
	    			coreWhBinMap.put("BIN_STATUS", "01");
	    			List<Map<String, Object>> retCoreWhBinlist=dispatchingJISBillPickingDAO.selectCoreWhBin(coreWhBinMap);
	    			if(retCoreWhBinlist!=null&retCoreWhBinlist.size()>0){//存在，就要调用立库出库接口，传给立库
	    				
	    				Map<String, Object> likuMap=new HashMap<String, Object>();
	    				likuMap.put("FROM_PLANT_CODE", tempMap.get("FROM_PLANT_CODE"));
	    				likuMap.put("FROM_WAREHOUSING_CODE", tempMap.get("FROM_WAREHOUSING_CODE"));
	    				likuMap.put("BARCODE", tempMap.get("BARCODE"));
	    				likuMap.put("ITEM_NO", tempMap.get("ITEM_NO"));
	    				likuMap.put("MATERIAL_CODE", tempMap.get("MATERIAL_CODE"));
	    				likuMap.put("MATERIAL_DESC", tempMap.get("MATERIAL_DESC"));
	    				likuMap.put("UNIT", tempMap.get("UNIT"));
	    				likuMap.put("TYPE", tempMap.get("TYPE"));
	    				likuMap.put("BATCH", tempMap.get("BATCH"));
	    				likuMap.put("PACKAGE_MODEL", tempMap.get("PACKAGE_MODEL"));
	    				likuMap.put("PACKAGE_QTY", tempMap.get("PACKAGE_QTY"));
	    				likuMap.put("QUANTITY", tempMap.get("QUANTITY"));
	    				likuMap.put("VENDOR_CODE", tempMap.get("VENDOR_CODE"));
	    				likuMap.put("VENDOR_NAME", tempMap.get("VENDOR_NAME"));
	    				likuMap.put("LGORT", retPicklist.get(0).get("LGORT"));//但要确保t_picking中一个barcode对于component表的barcode一行
	    				likuMap.put("SOBKZ", retPicklist.get(0).get("SOBKZ"));//但要确保t_picking中一个barcode对于component表的barcode一行
	    				likuMap.put("CREATE_USER_ID", tempMap.get("CREATE_USER_ID"));
	    				likuMap.put("PICKING_ADDRESS", tempMap.get("PICKING_ADDRESS"));
	    				likuMap.put("LINE_CATEGORY", tempMap.get("LINE_CATEGORY"));
	    				likuMap.put("WAITING_LOCATION", tempMap.get("WAITING_LOCATION"));
	    				likuMap.put("ELEVATOR_CODE", tempMap.get("ELEVATOR_CODE"));
	    				likuMap.put("DISPATCHING_ADDRESS", tempMap.get("DISPATCHING_ADDRESS"));
	    				likuMap.put("REMARK", tempMap.get("REMARK"));
	    				likuMap.put("HANDOVER_DATE", tempMap.get("HANDOVER_DATE"));
	    				likuMap.put("LINE_REQUIREMENT_DATE",tempMap.get("LINE_REQUIREMENT_DATE"));
	    				likulist.add(likuMap);
	    				
	    			}
				}
				
				wswmsDispatchinglist.setListHeader(arrayWsWmsDispatchingHeader);
	            
	            WSWLMSToWMS wlmstowms=new WSWLMSToWMS();
	            
	            WSWMSDispatchingList retDispatcingList=wlmstowms.getWSWLMSToWMSSoap().addDispatchingReceive(wswmsDispatchinglist);
	            List<String> hintjiekouInfos = new ArrayList<String>();    //返回接口信息
	            if("S".equals(retDispatcingList.getStatus())){
	                 hintjiekouInfos.add("");
	             }else{
	            	 ArrayOfWSWMSDispatchingHeader arrayheaderlist=retDispatcingList.getListHeader();
	                 List<WSWMSDispatchingHeader> wsDispatchingHeaderllist=arrayheaderlist.getWSWMSDispatchingHeader();
	                 for(WSWMSDispatchingHeader disheader:wsDispatchingHeaderllist){
	                     hintjiekouInfos.add(disheader.getReturnMessage());
	                     ArrayOfWSWMSDispatchingItem arrayItemlist=disheader.getListItem();
	                     List<WSWMSDispatchingItem> wsDispatchingItemlist=arrayItemlist.getWSWMSDispatchingItem();
	                     for(WSWMSDispatchingItem itembean:wsDispatchingItemlist){
	                         hintjiekouInfos.add(itembean.getReturnMessage());
	                         ArrayOfWSWMSDispatchingComponent arraycomponentlist=itembean.getListComponent();
	                         List<WSWMSDispatchingComponent> wsdispatchingComplist=arraycomponentlist.getWSWMSDispatchingComponent();
	                         for(WSWMSDispatchingComponent comp:wsdispatchingComplist){
	                             hintjiekouInfos.add(comp.getReturnMessage());
	                           //插入异常信息表
	                             Map<String, Object> wlmsExceptionMap=new HashMap<String, Object>();
	                             wlmsExceptionMap.put("BARCODE", comp.getBARCODE());
	                             wlmsExceptionMap.put("DISPATCHING_NO", comp.getDISPATCHINGNO());
	                             wlmsExceptionMap.put("ISSUBMIT", "0");
	                             wlmsExceptionMap.put("CREATE_DATE", sdf.parse(sdf.format(new Date())));
	                             wlmsExceptionMap.put("TYPE", "2");
	                             
	                             wlmsExceptionMap.put("ITEM_NO", "");
	                             wlmsExceptionMap.put("COMPONENT_NO", "");
	                             wlmsExceptionMap.put("CREATE_USER_ID", "");
	                             wlmsExceptionMap.put("BARCODE1", "");
	                             wlmsExceptionMap.put("DISPATCHINGNO1", "");
	                             wlmsExceptionMap.put("BARCODE2", "");
	                             wlmsExceptionMap.put("DISPATCHINGNO2", "");
	                             
	                             dispatchingJISBillPickingDAO.insertToWlmsException(wlmsExceptionMap);
	                             //
	                         }
	                     }
	                 }
	             }
	            String xwlStr="";
	            if(hintjiekouInfos.size()>0){
	                for(int z=0;z<hintjiekouInfos.size();z++){
	                   xwlStr=xwlStr+hintjiekouInfos.get(z);
	                }
	            }
	            if(likulist.size()>0){//调用立库接口
	    			//String retliku=outStockLiKu(likulist,"2");//调用立库出库
	    		}
	        return xwlStr;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public String dispatchingChaifen(Map<String, Object> param) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String xwlStr="";
		//wms操作，先插入拆分的两条记录，再删除被拆分的记录
		
		String chaif_qty_str=param.get("CHAIFQTY")==null?"":param.get("CHAIFQTY").toString();
		//通过barcode查询component表
		Map<String, Object> compMap=new HashMap<String, Object>();
		compMap.put("BARCODE", param.get("BARCODE"));
		
		List<Map<String, Object>> retComponentlist=dispatchingJISBillPickingDAO.selectComponentByBarcode(compMap);
		Map<String, Object> component_del=new HashMap<String, Object>();//做删除用的对象
		if(retComponentlist!=null&&retComponentlist.size()>0){
			component_del=retComponentlist.get(0);
			Map<String, Object> component_add=retComponentlist.get(0);//做拆分新增用的
			Map<String, Object> component_add2=new HashMap<String, Object>();//做拆分新增用的
			component_add2.putAll(component_add);
			
			//再删除对应的 barcode关联的component表
			Map<String, Object> component_del_wms=new HashMap<String, Object>();
			component_del_wms.put("ID", component_del.get("ID"));
			dispatchingJISBillPickingDAO.deleteComponentByID(component_del_wms);
			
			//准备拆分的数据
			BigDecimal qty1=BigDecimal.ZERO;//第一个拆分的数量
            BigDecimal qty2=BigDecimal.ZERO;//第二个拆分的数量
            if(!"".equals(chaif_qty_str)){
            	qty1=new BigDecimal(chaif_qty_str.trim());
            	String quantity_str=component_add.get("QUANTITY")==null?"":component_add.get("QUANTITY").toString();
            	if(!"".equals(quantity_str)){
            		qty2=new BigDecimal(quantity_str).subtract(qty1);
            	}
            }
            
            String barcode=wmsCDocService.getDocNo("", "13");
            //给待插入的第一行component重新赋值
            component_add.put("BARCODE", barcode);
            component_add.put("QUANTITY", qty1);
            component_add.put("AUTO_FLAG", "1");
            //把时间重置为string
            if(component_add.get("CREATE_DATE")!=null&&!"".equals(component_add.get("CREATE_DATE"))){
            	component_add.put("CREATE_DATE", sdf.format(sdf.parse(component_add.get("CREATE_DATE").toString())));
            }
            if(component_add.get("UPDATE_DATE")!=null&&!"".equals(component_add.get("UPDATE_DATE"))){
            	component_add.put("UPDATE_DATE", sdf.format(sdf.parse(component_add.get("UPDATE_DATE").toString())));
            }
            if(component_add.get("ACTUAL_PRINT_DATE")!=null&&!"".equals(component_add.get("ACTUAL_PRINT_DATE"))){
            	component_add.put("ACTUAL_PRINT_DATE", sdf.format(sdf.parse(component_add.get("ACTUAL_PRINT_DATE").toString())));
            }
            if(component_add.get("ACTUAL_HANDOVER_DATE")!=null&&!"".equals(component_add.get("ACTUAL_HANDOVER_DATE"))){
            	component_add.put("ACTUAL_HANDOVER_DATE", sdf.format(sdf.parse(component_add.get("ACTUAL_HANDOVER_DATE").toString())));
            }
            if(component_add.get("COMFIRM_DATE")!=null&&!"".equals(component_add.get("COMFIRM_DATE"))){
            	component_add.put("COMFIRM_DATE", sdf.format(sdf.parse(component_add.get("COMFIRM_DATE").toString())));
            }
            //给待插入的第二行component重新赋值
            String barcode2=wmsCDocService.getDocNo("", "13");
            component_add2.put("BARCODE", barcode2);
            component_add2.put("QUANTITY", qty2);
            component_add2.put("AUTO_FLAG", "1");
          //把时间重置为string
            if(component_add2.get("CREATE_DATE")!=null&&!"".equals(component_add2.get("CREATE_DATE"))){
            	component_add2.put("CREATE_DATE", sdf.format(sdf.parse(component_add2.get("CREATE_DATE").toString())));
            }
            if(component_add2.get("UPDATE_DATE")!=null&&!"".equals(component_add2.get("UPDATE_DATE"))){
            	component_add2.put("UPDATE_DATE", sdf.format(sdf.parse(component_add2.get("UPDATE_DATE").toString())));
            }
            if(component_add2.get("ACTUAL_PRINT_DATE")!=null&&!"".equals(component_add2.get("ACTUAL_PRINT_DATE"))){
            	component_add2.put("ACTUAL_PRINT_DATE", sdf.format(sdf.parse(component_add2.get("ACTUAL_PRINT_DATE").toString())));
            }
            if(component_add2.get("ACTUAL_HANDOVER_DATE")!=null&&!"".equals(component_add2.get("ACTUAL_HANDOVER_DATE"))){
            	component_add2.put("ACTUAL_HANDOVER_DATE", sdf.format(sdf.parse(component_add2.get("ACTUAL_HANDOVER_DATE").toString())));
            }
            if(component_add2.get("COMFIRM_DATE")!=null&&!"".equals(component_add2.get("COMFIRM_DATE"))){
            	component_add2.put("COMFIRM_DATE", sdf.format(sdf.parse(component_add2.get("COMFIRM_DATE").toString())));
            }
            
            List<Map<String, Object>> comp_insert_list=new ArrayList<Map<String, Object>>();
            comp_insert_list.add(component_add);
            comp_insert_list.add(component_add2);
            //插入component表,其他的header,item表不动
            dispatchingJISBillPickingDAO.insertComponent(comp_insert_list);
            
            //调用新物流需求重新发布接口 开始
          //删除开始
            WSWMSDispatchingList wswmsDispatchinglist=new WSWMSDispatchingList();
            wswmsDispatchinglist.setUserName("WLMS");
            wswmsDispatchinglist.setPassword("WLMS2014");
            
            ArrayOfWSWMSDispatchingHeader arrayWsWmsDispatchingHeader=new ArrayOfWSWMSDispatchingHeader();
            List<WSWMSDispatchingHeader> wswmsDispatchingHeaderlist=arrayWsWmsDispatchingHeader.getWSWMSDispatchingHeader();
            
          //准备头表
            WSWMSDispatchingHeader wsDispatchingHeader=new WSWMSDispatchingHeader();
            wsDispatchingHeader.setOperateType("DELETE");
            wsDispatchingHeader.setIsExeOperate(0);//不执行
            
          //准备行表
            ArrayOfWSWMSDispatchingItem listItem=new ArrayOfWSWMSDispatchingItem();
            List<WSWMSDispatchingItem> wswmsDispatchingItem=listItem.getWSWMSDispatchingItem();
            WSWMSDispatchingItem wsDispatchingItem=new WSWMSDispatchingItem();
            wsDispatchingItem.setIsExeOperate(0);//不执行
            
          //准备组件表
            ArrayOfWSWMSDispatchingComponent listComponent=new ArrayOfWSWMSDispatchingComponent();
            List<WSWMSDispatchingComponent> wswmsDispatchingComponent=listComponent.getWSWMSDispatchingComponent();
            WSWMSDispatchingComponent wsDispatchingComponent=new WSWMSDispatchingComponent();
            wsDispatchingComponent.setBARCODE(component_del.get("BARCODE").toString());
            wsDispatchingComponent.setDISPATCHINGNO(component_del.get("DISPATCHING_NO").toString());
            wsDispatchingComponent.setITEMNO(component_del.get("ITEM_NO").toString());
            wsDispatchingComponent.setCOMPONENTNO(component_del.get("COMPONENT_NO").toString());
            wsDispatchingComponent.setDEL("1");//删除
            wsDispatchingComponent.setIsExeOperate(1);//立即执行
            
            wswmsDispatchingComponent.add(wsDispatchingComponent);
            wsDispatchingItem.setListComponent(listComponent);
            
            wswmsDispatchingItem.add(wsDispatchingItem);
            
            wsDispatchingHeader.setListItem(listItem);
            
            wswmsDispatchingHeaderlist.add(wsDispatchingHeader);
            
            wswmsDispatchinglist.setListHeader(arrayWsWmsDispatchingHeader);
            
            WSWLMSToWMS wlmstowms=new WSWLMSToWMS();
            
            WSWMSDispatchingList retDispatcingList=wlmstowms.getWSWLMSToWMSSoap().addDispatchingReceive(wswmsDispatchinglist);
            
            List<String> hintjiekouInfos = new ArrayList<String>();    //返回接口信息
            
            Map<String, Object> wlmsExceptionMap=new HashMap<String, Object>();
            if("S".equals(retDispatcingList.getStatus())){
                 hintjiekouInfos.add("");
             }else{
                ArrayOfWSWMSDispatchingHeader arrayheaderlist=retDispatcingList.getListHeader();
                List<WSWMSDispatchingHeader> wsDispatchingHeaderllist=arrayheaderlist.getWSWMSDispatchingHeader();
                for(WSWMSDispatchingHeader disheader:wsDispatchingHeaderllist){
                    hintjiekouInfos.add(disheader.getReturnMessage());
                    ArrayOfWSWMSDispatchingItem arrayItemlist=disheader.getListItem();
                    List<WSWMSDispatchingItem> wsDispatchingItemlist=arrayItemlist.getWSWMSDispatchingItem();
                    for(WSWMSDispatchingItem itembean:wsDispatchingItemlist){
                        hintjiekouInfos.add(itembean.getReturnMessage());
                        ArrayOfWSWMSDispatchingComponent arraycomponentlist=itembean.getListComponent();
                        List<WSWMSDispatchingComponent> wsdispatchingComplist=arraycomponentlist.getWSWMSDispatchingComponent();
                        for(WSWMSDispatchingComponent comp:wsdispatchingComplist){
                            hintjiekouInfos.add(comp.getReturnMessage());
                          //插入异常信息表
                            
                            wlmsExceptionMap.put("BARCODE", comp.getBARCODE());
                            wlmsExceptionMap.put("DISPATCHING_NO", comp.getDISPATCHINGNO());
                            wlmsExceptionMap.put("ISSUBMIT", "0");
                            wlmsExceptionMap.put("CREATE_DATE", sdf.parse(sdf.format(new Date())));
                            wlmsExceptionMap.put("TYPE", "3");
                            //
                        }
                    }
                }
            }
            //删除结束
            
          //新增拣配单开始
            WSWMSDispatchingList wswmsDispatchinglistadd=new WSWMSDispatchingList();
            wswmsDispatchinglistadd.setUserName("WLMS");
            wswmsDispatchinglistadd.setPassword("WLMS2014");
          
            ArrayOfWSWMSDispatchingHeader arrayWsWmsDispatchingHeaderadd=new ArrayOfWSWMSDispatchingHeader();
            List<WSWMSDispatchingHeader> wswmsDispatchingHeaderlistadd=arrayWsWmsDispatchingHeaderadd.getWSWMSDispatchingHeader();
            
          //可重复部分1 开始
            WSWMSDispatchingHeader wsDispatchingHeaderadd=new WSWMSDispatchingHeader();
            wsDispatchingHeaderadd.setOperateType("ADD");
            wsDispatchingHeaderadd.setIsExeOperate(0);//不执行
            
          //准备行表
            ArrayOfWSWMSDispatchingItem listItemadd=new ArrayOfWSWMSDispatchingItem();
            List<WSWMSDispatchingItem> wswmsDispatchingItemadd=listItemadd.getWSWMSDispatchingItem();
            WSWMSDispatchingItem wsDispatchingItemadd=new WSWMSDispatchingItem();
            wsDispatchingItemadd.setIsExeOperate(0);//不执行
            
          //准备组件表
            compMap.put("BARCODE", barcode);
            List<Map<String, Object>> retComponentadd_1=dispatchingJISBillPickingDAO.selectComponentByBarcode(compMap);
            Map<String, Object> component_add_1=retComponentadd_1.get(0);
            
            ArrayOfWSWMSDispatchingComponent listComponentadd=new ArrayOfWSWMSDispatchingComponent();
            List<WSWMSDispatchingComponent> wswmsDispatchingComponentadd=listComponentadd.getWSWMSDispatchingComponent();
            WSWMSDispatchingComponent wsDispatchingComponentadd=new WSWMSDispatchingComponent();
            
            if(component_add_1.get("ACTUAL_HANDOVER_DATE")!=null&&!"".equals(component_add_1.get("ACTUAL_HANDOVER_DATE"))){
            	wsDispatchingComponentadd.setACTUALHANDOVERDATE(sdf.format(sdf.parse(component_add_1.get("ACTUAL_HANDOVER_DATE").toString())));
            }
            if(component_add_1.get("ACTUAL_PRINT_DATE")!=null&&!"".equals(component_add_1.get("ACTUAL_PRINT_DATE"))){
                wsDispatchingComponentadd.setACTUALPRINTDATE(sdf.format(sdf.parse(component_add_1.get("ACTUAL_PRINT_DATE").toString())));
            }
            wsDispatchingComponentadd.setAUTOFLAG(component_add_1.get("AUTO_FLAG")==null?"":component_add_1.get("AUTO_FLAG").toString());
            wsDispatchingComponentadd.setBATCH(component_add_1.get("BATCH")==null?"":component_add_1.get("BATCH").toString());
            if(component_add_1.get("COMFIRM_DATE")!=null&&!"".equals(component_add_1.get("COMFIRM_DATE"))){
                wsDispatchingComponentadd.setCOMFIRMDATE(sdf.format(sdf.parse(component_add_1.get("COMFIRM_DATE").toString())));
            }
            wsDispatchingComponentadd.setDELETEREMARK(component_add_1.get("DELETE_REMARK")==null?"":component_add_1.get("DELETE_REMARK").toString());
            wsDispatchingComponentadd.setFROMPLANTCODE(component_add_1.get("FROM_PLANT_CODE")==null?"":component_add_1.get("FROM_PLANT_CODE").toString());
            wsDispatchingComponentadd.setFROMWAREHOUSINGCODE(component_add_1.get("FROM_WAREHOUSING_CODE")==null?"":component_add_1.get("FROM_WAREHOUSING_CODE").toString());
            wsDispatchingComponentadd.setHANDOVERUSERID(component_add_1.get("HANDOVER_USER_ID")==null?"":component_add_1.get("HANDOVER_USER_ID").toString());
            wsDispatchingComponentadd.setMATERIALCODE(component_add_1.get("MATERIAL_CODE")==null?"":component_add_1.get("MATERIAL_CODE").toString());
            wsDispatchingComponentadd.setMATERIALDESC(component_add_1.get("MATERIAL_DESC")==null?"":component_add_1.get("MATERIAL_DESC").toString());
            wsDispatchingComponentadd.setPACKAGEMODEL(component_add_1.get("PACKAGE_MODEL")==null?"":component_add_1.get("PACKAGE_MODEL").toString());
            if(component_add_1.get("PACKAGE_QTY")!=null&&!"".equals(component_add_1.get("PACKAGE_QTY"))){
                wsDispatchingComponentadd.setPACKAGEQTY(Double.parseDouble(component_add_1.get("PACKAGE_QTY").toString()));
            }
            wsDispatchingComponentadd.setPACKAGETYPE(component_add_1.get("PACKAGE_TYPE")==null?"":component_add_1.get("PACKAGE_TYPE").toString());
            wsDispatchingComponentadd.setPICKINGADDRESS(component_add_1.get("PICKING_ADDRESS")==null?"":component_add_1.get("PICKING_ADDRESS").toString());
            wsDispatchingComponentadd.setPICKINGUSERID(component_add_1.get("PICKING_USER_ID")==null?"":component_add_1.get("PICKING_USER_ID").toString());
            wsDispatchingComponentadd.setPICKINGUSERNAME(component_add_1.get("PICKING_USER_NAME")==null?"":component_add_1.get("PICKING_USER_NAME").toString());
            wsDispatchingComponentadd.setPLANTCODE(component_add_1.get("PLANT_CODE")==null?"":component_add_1.get("PLANT_CODE").toString());
            if(component_add_1.get("QUANTITY")!=null&&!"".equals(component_add_1.get("QUANTITY"))){
                wsDispatchingComponentadd.setQUANTITY(Double.parseDouble(component_add_1.get("QUANTITY").toString()));
            }
            wsDispatchingComponentadd.setREQUIREMENTTYPE(component_add_1.get("REQUIREMENT_TYPE")==null?"01":component_add_1.get("REQUIREMENT_TYPE").toString());
            wsDispatchingComponentadd.setSTATUS(component_add_1.get("STATUS")==null?"":component_add_1.get("STATUS").toString());
            wsDispatchingComponentadd.setUNIT(component_add_1.get("UNIT")==null?"":component_add_1.get("UNIT").toString());
            wsDispatchingComponentadd.setVENDORCODE(component_add_1.get("VENDOR_CODE")==null?"":component_add_1.get("VENDOR_CODE").toString());
            wsDispatchingComponentadd.setVENDORNAME(component_add_1.get("VENDOR_NAME")==null?"":component_add_1.get("VENDOR_NAME").toString());
            wsDispatchingComponentadd.setBARCODE(component_add_1.get("BARCODE")==null?"":component_add_1.get("BARCODE").toString());
            wsDispatchingComponentadd.setDISPATCHINGNO(component_add_1.get("DISPATCHING_NO")==null?"":component_add_1.get("DISPATCHING_NO").toString());
            wsDispatchingComponentadd.setITEMNO(component_add_1.get("ITEM_NO")==null?"":component_add_1.get("ITEM_NO").toString());
            wsDispatchingComponentadd.setCOMPONENTNO(component_add_1.get("COMPONENT_NO")==null?"":component_add_1.get("COMPONENT_NO").toString());
            wsDispatchingComponentadd.setDEL(component_add_1.get("DEL")==null?"":component_add_1.get("DEL").toString());//删除状态
            wsDispatchingComponentadd.setIsExeOperate(1);//立即执行
            
            wswmsDispatchingComponentadd.add(wsDispatchingComponentadd);
            wsDispatchingItemadd.setListComponent(listComponentadd);
            
            wswmsDispatchingItemadd.add(wsDispatchingItemadd);
            
            wsDispatchingHeaderadd.setListItem(listItemadd);
            
            wlmsExceptionMap.put("BARCODE1", barcode);
            wlmsExceptionMap.put("DISPATCHINGNO1", component_add_1.get("DISPATCHING_NO")==null?"":component_add_1.get("DISPATCHING_NO").toString());
            //可重复部分1 结束
            
          //可以重复部分2
            WSWMSDispatchingHeader wsDispatchingHeaderadd2=new WSWMSDispatchingHeader();
            wsDispatchingHeaderadd2.setOperateType("ADD");
            wsDispatchingHeaderadd2.setIsExeOperate(0);//不执行
            //准备行表
            ArrayOfWSWMSDispatchingItem listItemadd2=new ArrayOfWSWMSDispatchingItem();
            List<WSWMSDispatchingItem> wswmsDispatchingItemadd2=listItemadd2.getWSWMSDispatchingItem();
            WSWMSDispatchingItem wsDispatchingItemadd2=new WSWMSDispatchingItem();
            wsDispatchingItemadd2.setIsExeOperate(0);//不执行
            
          //准备组件表
            compMap.put("BARCODE", barcode2);
            List<Map<String, Object>> retComponentadd_2=dispatchingJISBillPickingDAO.selectComponentByBarcode(compMap);
            Map<String, Object> component_add_2=retComponentadd_2.get(0);
            ArrayOfWSWMSDispatchingComponent listComponentadd2=new ArrayOfWSWMSDispatchingComponent();
            List<WSWMSDispatchingComponent> wswmsDispatchingComponentadd2=listComponentadd2.getWSWMSDispatchingComponent();
            WSWMSDispatchingComponent wsDispatchingComponentadd2=new WSWMSDispatchingComponent();
            
            if(component_add_2.get("ACTUAL_HANDOVER_DATE")!=null&&!"".equals(component_add_2.get("ACTUAL_HANDOVER_DATE"))){
            	wsDispatchingComponentadd2.setACTUALHANDOVERDATE(sdf.format(sdf.parse(component_add_2.get("ACTUAL_HANDOVER_DATE").toString())));
            }
            if(component_add_2.get("ACTUAL_PRINT_DATE")!=null&&!"".equals(component_add_2.get("ACTUAL_PRINT_DATE"))){
                wsDispatchingComponentadd2.setACTUALPRINTDATE(sdf.format(sdf.parse(component_add_2.get("ACTUAL_PRINT_DATE").toString())));
            }
            wsDispatchingComponentadd2.setAUTOFLAG(component_add_2.get("AUTO_FLAG")==null?"":component_add_2.get("AUTO_FLAG").toString());
            wsDispatchingComponentadd2.setBATCH(component_add_2.get("BATCH")==null?"":component_add_2.get("BATCH").toString());
            if(component_add_2.get("COMFIRM_DATE")!=null&&!"".equals(component_add_2.get("COMFIRM_DATE"))){
                wsDispatchingComponentadd2.setCOMFIRMDATE(sdf.format(sdf.parse(component_add_2.get("COMFIRM_DATE").toString())));
            }
            wsDispatchingComponentadd2.setDELETEREMARK(component_add_2.get("DELETE_REMARK")==null?"":component_add_2.get("DELETE_REMARK").toString());
            wsDispatchingComponentadd2.setFROMPLANTCODE(component_add_2.get("FROM_PLANT_CODE")==null?"":component_add_2.get("FROM_PLANT_CODE").toString());
            wsDispatchingComponentadd2.setFROMWAREHOUSINGCODE(component_add_2.get("FROM_WAREHOUSING_CODE")==null?"":component_add_2.get("FROM_WAREHOUSING_CODE").toString());
            wsDispatchingComponentadd2.setHANDOVERUSERID(component_add_2.get("HANDOVER_USER_ID")==null?"":component_add_2.get("HANDOVER_USER_ID").toString());
            wsDispatchingComponentadd2.setMATERIALCODE(component_add_2.get("MATERIAL_CODE")==null?"":component_add_2.get("MATERIAL_CODE").toString());
            wsDispatchingComponentadd2.setMATERIALDESC(component_add_2.get("MATERIAL_DESC")==null?"":component_add_2.get("MATERIAL_DESC").toString());
            wsDispatchingComponentadd2.setPACKAGEMODEL(component_add_2.get("PACKAGE_MODEL")==null?"":component_add_2.get("PACKAGE_MODEL").toString());
            if(component_add_2.get("PACKAGE_QTY")!=null&&!"".equals(component_add_2.get("PACKAGE_QTY"))){
                wsDispatchingComponentadd2.setPACKAGEQTY(Double.parseDouble(component_add_2.get("PACKAGE_QTY").toString()));
            }
            wsDispatchingComponentadd2.setPACKAGETYPE(component_add_2.get("PACKAGE_TYPE")==null?"":component_add_2.get("PACKAGE_TYPE").toString());
            wsDispatchingComponentadd2.setPICKINGADDRESS(component_add_2.get("PICKING_ADDRESS")==null?"":component_add_2.get("PICKING_ADDRESS").toString());
            wsDispatchingComponentadd2.setPICKINGUSERID(component_add_2.get("PICKING_USER_ID")==null?"":component_add_2.get("PICKING_USER_ID").toString());
            wsDispatchingComponentadd2.setPICKINGUSERNAME(component_add_2.get("PICKING_USER_NAME")==null?"":component_add_2.get("PICKING_USER_NAME").toString());
            wsDispatchingComponentadd2.setPLANTCODE(component_add_2.get("PLANT_CODE")==null?"":component_add_2.get("PLANT_CODE").toString());
            if(component_add_2.get("QUANTITY")!=null&&!"".equals(component_add_2.get("QUANTITY"))){
                wsDispatchingComponentadd2.setQUANTITY(Double.parseDouble(component_add_2.get("QUANTITY").toString()));
            }
            wsDispatchingComponentadd2.setREQUIREMENTTYPE(component_add_2.get("REQUIREMENT_TYPE")==null?"01":component_add_2.get("REQUIREMENT_TYPE").toString());
            wsDispatchingComponentadd2.setSTATUS(component_add_2.get("STATUS")==null?"":component_add_2.get("STATUS").toString());
            wsDispatchingComponentadd2.setUNIT(component_add_2.get("UNIT")==null?"":component_add_2.get("UNIT").toString());
            wsDispatchingComponentadd2.setVENDORCODE(component_add_2.get("VENDOR_CODE")==null?"":component_add_2.get("VENDOR_CODE").toString());
            wsDispatchingComponentadd2.setVENDORNAME(component_add_2.get("VENDOR_NAME")==null?"":component_add_2.get("VENDOR_NAME").toString());
            wsDispatchingComponentadd2.setBARCODE(component_add_2.get("BARCODE")==null?"":component_add_2.get("BARCODE").toString());
            wsDispatchingComponentadd2.setDISPATCHINGNO(component_add_2.get("DISPATCHING_NO")==null?"":component_add_2.get("DISPATCHING_NO").toString());
            wsDispatchingComponentadd2.setITEMNO(component_add_2.get("ITEM_NO")==null?"":component_add_2.get("ITEM_NO").toString());
            wsDispatchingComponentadd2.setCOMPONENTNO(component_add_2.get("COMPONENT_NO")==null?"":component_add_2.get("COMPONENT_NO").toString());
            wsDispatchingComponentadd2.setDEL(component_add_2.get("DEL")==null?"":component_add_2.get("DEL").toString());//删除状态
            wsDispatchingComponentadd2.setIsExeOperate(1);//立即执行
            
            wswmsDispatchingComponentadd2.add(wsDispatchingComponentadd2);
            wsDispatchingItemadd2.setListComponent(listComponentadd2);
            
            wswmsDispatchingItemadd2.add(wsDispatchingItemadd2);
            
            wsDispatchingHeaderadd2.setListItem(listItemadd2);
            
            wlmsExceptionMap.put("BARCODE2", barcode2);
            wlmsExceptionMap.put("DISPATCHINGNO2", component_add_2.get("DISPATCHING_NO")==null?"":component_add_2.get("DISPATCHING_NO").toString());
          //可以重复部分2 结束
            
            wswmsDispatchingHeaderlistadd.add(wsDispatchingHeaderadd);
            wswmsDispatchingHeaderlistadd.add(wsDispatchingHeaderadd2);
            
            wswmsDispatchinglistadd.setListHeader(arrayWsWmsDispatchingHeaderadd);
        
            WSWLMSToWMS wlmstowmsadd=new WSWLMSToWMS();

            WSWMSDispatchingList retDispatcingaddListadd=wlmstowmsadd.getWSWLMSToWMSSoap().addDispatchingReceive(wswmsDispatchinglistadd);
            
            
          //新增拣配单结束
            if("S".equals(retDispatcingaddListadd.getStatus())){
                hintjiekouInfos.add("");
            }else{
               ArrayOfWSWMSDispatchingHeader arrayheaderlist=retDispatcingaddListadd.getListHeader();
               List<WSWMSDispatchingHeader> wsDispatchingHeaderllist=arrayheaderlist.getWSWMSDispatchingHeader();
               for(WSWMSDispatchingHeader disheader:wsDispatchingHeaderllist){
                   hintjiekouInfos.add(disheader.getReturnMessage());
                   ArrayOfWSWMSDispatchingItem arrayItemlist=disheader.getListItem();
                   List<WSWMSDispatchingItem> wsDispatchingItemlist=arrayItemlist.getWSWMSDispatchingItem();
                   for(WSWMSDispatchingItem itembean:wsDispatchingItemlist){
                       hintjiekouInfos.add(itembean.getReturnMessage());
                       ArrayOfWSWMSDispatchingComponent arraycomponentlist=itembean.getListComponent();
                       List<WSWMSDispatchingComponent> wsdispatchingComplist=arraycomponentlist.getWSWMSDispatchingComponent();
                       for(WSWMSDispatchingComponent comp:wsdispatchingComplist){
                           hintjiekouInfos.add(comp.getReturnMessage());
                       }
                   }
               }
              
           } 
            
            
            if(hintjiekouInfos.size()>0){
                for(int z=0;z<hintjiekouInfos.size();z++){
                   xwlStr=xwlStr+hintjiekouInfos.get(z);
                }
                
            }
            if(!"".equals(xwlStr)){
            	//只有异常的时候才插入
                wlmsExceptionMap.put("ITEM_NO", "");
                wlmsExceptionMap.put("COMPONENT_NO", "");
                wlmsExceptionMap.put("CREATE_USER_ID", "");
                
                wlmsExceptionMap.put("ISSUBMIT", "0");
                wlmsExceptionMap.put("CREATE_DATE", sdf.parse(sdf.format(new Date())));
                wlmsExceptionMap.put("TYPE", "3");
                
                dispatchingJISBillPickingDAO.insertToWlmsException(wlmsExceptionMap);
            }
            
		}
		
		return xwlStr;
	}

	@Override
	public List<Map<String, Object>> selectToWlmsException(
			Map<String, Object> params) {
		// TODO Auto-generated method stub
		return dispatchingJISBillPickingDAO.selectToWlmsException(params);
	}

	@Override
	public void insertToWlmsException(Map<String, Object> params) {
		dispatchingJISBillPickingDAO.insertToWlmsException(params);
		
	}

	@Override
	public String outStockLiKu(List<Map<String, Object>> listparams, String st) {
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String ret="";
        String failedstr="";
        ClsBYDService clsServ=new ClsBYDService();
        IBYDWMSInterface bydInterface=clsServ.getBasicHttpBindingIBYDWMSInterface();
        //生成xml格式的string
        String xmlString = "";  
  
        
        try { 
            
            for(Map<String, Object> dipcf:listparams){//循环拣配结果
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();  
                Document document = builder.newDocument();  
                document.setXmlStandalone(true); 

                Element CSS = document.createElement("CSS");  
                document.appendChild(CSS);

                Element Main = document.createElement("Main");  
                CSS.appendChild(Main);
                Element OutStock = document.createElement("OutStock");  
                Main.appendChild(OutStock);
                
                Element Plcd = document.createElement("Plcd");
                Plcd.setTextContent(dipcf.get("FROM_PLANT_CODE")==null?"":dipcf.get("FROM_PLANT_CODE").toString());
                OutStock.appendChild(Plcd);
                
                Element Whcd = document.createElement("Whcd");
                Whcd.setTextContent(dipcf.get("FROM_WAREHOUSING_CODE")==null?"":dipcf.get("FROM_WAREHOUSING_CODE").toString());
                OutStock.appendChild(Whcd);
                
                Element OutType = document.createElement("OutType");//出库类型
                OutType.setTextContent("1");// 物流出库
                OutStock.appendChild(OutType);
                
                Element BillNo = document.createElement("BillNo");
                BillNo.setTextContent(dipcf.get("BARCODE")==null?"":dipcf.get("BARCODE").toString());
                OutStock.appendChild(BillNo);
                
                Element ItemNO = document.createElement("ItemNO");
                ItemNO.setTextContent(dipcf.get("ITEM_NO")==null?"":dipcf.get("ITEM_NO").toString());
                OutStock.appendChild(ItemNO);
                
                Element MaterialCode = document.createElement("MaterialCode");
                MaterialCode.setTextContent(dipcf.get("MATERIAL_CODE")==null?"":dipcf.get("MATERIAL_CODE").toString());
                OutStock.appendChild(MaterialCode);
                
                Element MaterialDescription = document.createElement("MaterialDescription");
                MaterialDescription.setTextContent(dipcf.get("MATERIAL_DESC")==null?"":dipcf.get("MATERIAL_DESC").toString());
                OutStock.appendChild(MaterialDescription);
                
                Element Unit = document.createElement("Unit");
                Unit.setTextContent(dipcf.get("UNIT")==null?"":dipcf.get("UNIT").toString());
                OutStock.appendChild(Unit);
                
                Element OnLineModel = document.createElement("OnLineModel");//上线模式
                String onlinemodel="";
                if("01".equals(dipcf.get("TYPE"))){
                    onlinemodel="JIT";
                }else if("02".equals(dipcf.get("TYPE"))){
                    onlinemodel="JIS";
                }else if("03".equals(dipcf.get("TYPE"))){
                    onlinemodel="E-KANBAN";
                }else if("04".equals(dipcf.get("TYPE"))){
                    onlinemodel="批量";
                }else{
                    onlinemodel="未知";
                }
                OnLineModel.setTextContent(onlinemodel);//
                OutStock.appendChild(OnLineModel);
                
                Element Batch = document.createElement("Batch");
                Batch.setTextContent(dipcf.get("BATCH")==null?"":dipcf.get("BATCH").toString());
                OutStock.appendChild(Batch);
                
                Element PackageMode = document.createElement("PackageMode");//包装型号
                PackageMode.setTextContent(dipcf.get("PACKAGE_MODEL")==null?"":dipcf.get("PACKAGE_MODEL").toString());
                OutStock.appendChild(PackageMode);
                
                Element PackageQty = document.createElement("PackageQty");//包装数量
                PackageQty.setTextContent(dipcf.get("PACKAGE_QTY")==null?"":dipcf.get("PACKAGE_QTY").toString());
                OutStock.appendChild(PackageQty);
                
                Element Quantity = document.createElement("Quantity");
                Quantity.setTextContent(dipcf.get("QUANTITY")==null?"":dipcf.get("QUANTITY").toString());
                OutStock.appendChild(Quantity);
                
                Element VendorCode = document.createElement("VendorCode");
                VendorCode.setTextContent(dipcf.get("VENDOR_CODE")==null?"":dipcf.get("VENDOR_CODE").toString());
                OutStock.appendChild(VendorCode);
                
                Element VendorName = document.createElement("VendorName");
                VendorName.setTextContent(dipcf.get("VENDOR_NAME")==null?"":dipcf.get("VENDOR_NAME").toString());
                OutStock.appendChild(VendorName);
                
                Element Locd = document.createElement("Locd");
                Locd.setTextContent(dipcf.get("LGORT")==null?"":dipcf.get("LGORT").toString());
                OutStock.appendChild(Locd);
                
                Element StockType = document.createElement("StockType");
                StockType.setTextContent(dipcf.get("SOBKZ")==null?"":dipcf.get("SOBKZ").toString());
                OutStock.appendChild(StockType);
                
                Element StockStatus = document.createElement("StockStatus");
                StockStatus.setTextContent("");
                OutStock.appendChild(StockStatus);
                
                Element CreateUserId = document.createElement("CreateUserId");
                CreateUserId.setTextContent(dipcf.get("CREATE_USER_ID")==null?"":dipcf.get("CREATE_USER_ID").toString());
                OutStock.appendChild(CreateUserId);
                
                Element Wacd = document.createElement("Wacd");//拣配区
                Wacd.setTextContent("");
                OutStock.appendChild(Wacd);
                
                Element Bhcd = document.createElement("Bhcd");//拣配位
                Bhcd.setTextContent(dipcf.get("PICKING_ADDRESS")==null?"":dipcf.get("PICKING_ADDRESS").toString());
                OutStock.appendChild(Bhcd);
                
                Element BillCodecd = document.createElement("BillCodecd");//拣配单
                BillCodecd.setTextContent("");
                OutStock.appendChild(BillCodecd);
                
                Element LineCategory = document.createElement("LineCategory");//线别
                LineCategory.setTextContent(dipcf.get("LINE_CATEGORY")==null?"":dipcf.get("LINE_CATEGORY").toString());
                OutStock.appendChild(LineCategory);
                
                Element WaitingLocation = document.createElement("WaitingLocation");//到货口
                WaitingLocation.setTextContent(dipcf.get("WAITING_LOCATION")==null?"":dipcf.get("WAITING_LOCATION").toString());
                OutStock.appendChild(WaitingLocation);
                
                Element ElevatorCode = document.createElement("ElevatorCode");//配送点
                ElevatorCode.setTextContent(dipcf.get("ELEVATOR_CODE")==null?"":dipcf.get("ELEVATOR_CODE").toString());
                OutStock.appendChild(ElevatorCode);
                
                Element DispatchingAddress = document.createElement("DispatchingAddress");//配送地址
                DispatchingAddress.setTextContent(dipcf.get("DISPATCHING_ADDRESS")==null?"":dipcf.get("DISPATCHING_ADDRESS").toString());
                OutStock.appendChild(DispatchingAddress);
                
                Element Remark = document.createElement("Remark");//备注
                Remark.setTextContent(dipcf.get("REMARK")==null?"":dipcf.get("REMARK").toString());
                OutStock.appendChild(Remark);
                
                Element HandoverDate = document.createElement("HandoverDate");//最迟交接时间
                HandoverDate.setTextContent(dipcf.get("HANDOVER_DATE")==null?"":dipcf.get("HANDOVER_DATE").toString());
                OutStock.appendChild(HandoverDate);
                
                Element LineRequirementDate = document.createElement("LineRequirementDate");//线边需求时间
                LineRequirementDate.setTextContent(dipcf.get("LINE_REQUIREMENT_DATE")==null?"":dipcf.get("LINE_REQUIREMENT_DATE").toString());
                OutStock.appendChild(LineRequirementDate);
                
                Element CodeType = document.createElement("CodeType");//二维码中的T类型
                String codeTypeStr="";
                String typeStr=dipcf.get("TYPE")==null?"":dipcf.get("TYPE").toString();
                if("01".equals(typeStr)){
                	codeTypeStr="1";
                }
                if("02".equals(typeStr)){
                	codeTypeStr="2";
                }
                if("03".equals(typeStr)||"04".equals(typeStr)){
                	codeTypeStr="1";
                }
                CodeType.setTextContent(codeTypeStr);
                OutStock.appendChild(CodeType);
                
                Element Status = document.createElement("Status");//
                Status.setTextContent(st);
                OutStock.appendChild(Status);
                
                Element Del = document.createElement("Del");//
                Del.setTextContent("0");
                OutStock.appendChild(Del);
                
            TransformerFactory transFactory = TransformerFactory.newInstance();  
            Transformer transformer = transFactory.newTransformer();  
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");  
            
            DOMSource domSource = new DOMSource(document);  
  
            // xml transform String  
            ByteArrayOutputStream bos = new ByteArrayOutputStream(); 
            transformer.setOutputProperty(OutputKeys.ENCODING,"gbk");
            transformer.transform(domSource, new StreamResult(bos));  
            xmlString = bos.toString();
            xmlString =xmlString.substring(36);
            System.out.println(xmlString); 
            //一次只能传一个物料
            String retstr=bydInterface.sendOutStock(xmlString);//
            if(!"ok".equalsIgnoreCase(retstr)){
                failedstr=failedstr+dipcf.get("MATERIAL_CODE")+",";
            }
                
            }
            if(!"".equals(failedstr)){
                ret=failedstr;
            }else{
                ret="ok";
            }
            
            
        }catch (Exception e) {  
            e.printStackTrace();  
        }  
        return ret;
	}

	/**
	 * 更新拣配确认记录号(趟号)，拣配确认记录人
	 */
	@Override
	public void updatePickRecordNo(List<Map<String, Object>> paramlist) {
		Map<String, Object> docparam=new HashMap<String, Object>();
		docparam.put("WMS_DOC_TYPE", "15");//
		Map<String,Object> doc=wmsCDocService.getDocNo(docparam);
		if(doc.get("MSG")!=null&&!"success".equals(doc.get("MSG"))) {
			throw new RuntimeException(doc.get("MSG").toString());
		}
		String pick_record_no="";
		pick_record_no=doc.get("docno").toString();//
		
		for(int m=0;m<paramlist.size();m++){
			
			Map<String, Object> componentparam=new HashMap<String, Object>();
			componentparam.put("DISPATCHING_NO", paramlist.get(m).get("DISPATCHING_NO"));
			componentparam.put("ITEM_NO", paramlist.get(m).get("ITEM_NO"));
			componentparam.put("COMPONENT_NO", paramlist.get(m).get("COMPONENT_NO"));
			
			componentparam.put("PICK_RECORD_NO", pick_record_no);
			componentparam.put("PICK_RECORD_USER_ID", paramlist.get(m).get("USERID"));
			dispatchingJISBillPickingDAO.updateDispatchingPickRecordNoComponent(componentparam);
		}
	}

	@Override
	public Map<String, Object> pickRecordNoCount(String barcode) {
		Map<String, Object> barcodeMap=new HashMap<String, Object>();
		barcodeMap.put("BARCODE", barcode);
		List<Map<String,Object>> retRecordlist=dispatchingJISBillPickingDAO.selectPickRecordNoCount(barcodeMap);
		Map<String,Object> retMap=new HashMap<String, Object>();
		if(retRecordlist!=null&&retRecordlist.size()>0){
			retMap=retRecordlist.get(0);
		}
		return retMap;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Map<String, Object> dispatchingHandoverService(Map<String, Object> params) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		ArrayOfWSWMSDemand WMSdemand=new ArrayOfWSWMSDemand();//需求配送表
        List<WSWMSDemand> wmsdemandlist=WMSdemand.getWSWMSDemand();
        
        ArrayOfWSWMSLogisMvt WMSlogistMvt=new ArrayOfWSWMSLogisMvt();//交接过账数据表
        List<WSWMSLogisMvt> wswmsLogisMvtlist=WMSlogistMvt.getWSWMSLogisMvt();
        
		List<Map<String, Object>> paramlist=new ArrayList<Map<String, Object>>();
		
		JSONArray jarr = JSON.parseArray(params.get("ARRLIST").toString());
		
		for(int m=0;m<jarr.size();m++){
			JSONObject itemData=jarr.getJSONObject(m);
			
			Map<String, Object> querenMap=new HashMap<String, Object>();
			querenMap.put("FROM_PLANT_CODE", itemData.getString("FROM_PLANT_CODE"));
			querenMap.put("BATCH", itemData.getString("BATCH"));
			querenMap.put("MATNR", itemData.getString("MATERIAL_CODE"));
			querenMap.put("MAKTX", itemData.getString("MATERIAL_DESC"));
			querenMap.put("XJ_QTY", itemData.getString("XJ_QTY"));
			querenMap.put("QUANTITY", itemData.getString("QUANTITY"));
			querenMap.put("LGORT", itemData.getString("LGORT"));
			querenMap.put("DISPATCHING_NO", itemData.getString("DISPATCHING_NO"));
			querenMap.put("ITEM_NO", itemData.getString("ITEM_NO"));
			querenMap.put("COMPONENT_NO", itemData.getString("COMPONENT_NO"));
			querenMap.put("STATUS", itemData.getString("STATUS"));
			querenMap.put("USERID", params.get("USERID"));
			querenMap.put("USERNAME", params.get("USERNAME"));
			querenMap.put("BARCODE", itemData.getString("BARCODE"));
			
			querenMap.put("PZDDT", params.get("PZDDT"));
			querenMap.put("JZDDT", params.get("JZDDT"));
			paramlist.add(querenMap);
			
			//调用物流接口参数准备
			WSWMSDemand wmsdemandbean=new WSWMSDemand();
	        wmsdemandbean.setListNo("");//
	        wmsdemandbean.setItemNo(itemData.getString("ITEM_NO")==null?1:Integer.parseInt(itemData.getString("ITEM_NO").toString()));
	        wmsdemandbean.setPartLabelID(itemData.getString("BARCODE"));//标签ID、单号
	        wmsdemandbean.setPickListNo(itemData.getString("DISPATCHING_NO"));//配送单号
	        
	        //在t_dispatching_item中查询
	        Map<String, Object> itemMap=new HashMap<String, Object>();
	        itemMap.put("DISPATCHING_NO", itemData.getString("DISPATCHING_NO"));
	        itemMap.put("ITEM_NO", itemData.getString("ITEM_NO")==null?1:Integer.parseInt(itemData.getString("ITEM_NO").toString()));
	        List<Map<String,Object>> retItemList=disPatchingJISService.selectDispatchingItem(itemMap);
	        if(retItemList!=null&&retItemList.size()>0){
	        	wmsdemandbean.setDemandNo(retItemList.get(0).get("REQUIREMENT_NO")==null?"1":retItemList.get(0).get("REQUIREMENT_NO").toString());//物流需求号
	        }else{
	        	wmsdemandbean.setDemandNo("1");
	        }
	        
	        //在t_dispatching_header中查询
	        String ElevatorCode="";
	        String WorkingLocation="";
	        String LineCategory="";
	        
	        Map<String, Object> headerMap=new HashMap<String, Object>();
	        headerMap.put("DISPATCHING_NO", itemData.getString("DISPATCHING_NO"));
	        List<Map<String,Object>> retHeaderList=disPatchingJISService.selectDispatchingHeader(headerMap);
	        if(retHeaderList!=null&&retHeaderList.size()>0){
	        	wmsdemandbean.setDemandType(retHeaderList.get(0).get("TYPE")==null?"":retHeaderList.get(0).get("TYPE").toString());
	        	ElevatorCode=retHeaderList.get(0).get("ELEVATOR_CODE")==null?"":retHeaderList.get(0).get("ELEVATOR_CODE").toString();
	        	WorkingLocation=retHeaderList.get(0).get("WORKING_LOCATION")==null?"":retHeaderList.get(0).get("WORKING_LOCATION").toString();
	        	LineCategory=retHeaderList.get(0).get("LINE_CATEGORY")==null?"":retHeaderList.get(0).get("LINE_CATEGORY").toString();
	        }else{
	        	wmsdemandbean.setDemandType("");
	        }
	        
	        //
	        String sap_move_type="";
	        String sobkz="";
	        String lgort_f="";
	        Map<String, Object> logisticsMap=new HashMap<String, Object>();
			logisticsMap.put("WERKS_F",itemData.getString("FROM_PLANT_CODE"));
			List<Map<String, Object>>  assemblyLogistics=disPatchingJISService.selectAssemblyLogistics(logisticsMap);
			if(assemblyLogistics!=null&&assemblyLogistics.size()>0){
				sap_move_type=assemblyLogistics.get(0).get("SAP_MOVE_TYPE")==null?"":assemblyLogistics.get(0).get("SAP_MOVE_TYPE").toString();
				sobkz=assemblyLogistics.get(0).get("SOBKZ")==null?"":assemblyLogistics.get(0).get("SOBKZ").toString();
				lgort_f=assemblyLogistics.get(0).get("LGORT_F")==null?"":assemblyLogistics.get(0).get("LGORT_F").toString();
			}
			
	        
	        wmsdemandbean.setAreaCode(itemData.getString("FROM_PLANT_CODE"));
	        wmsdemandbean.setPartSAP(itemData.getString("MATERIAL_CODE"));
	        wmsdemandbean.setPartName(itemData.getString("MATERIAL_DESC"));
	        wmsdemandbean.setPartCode(itemData.getString("MATERIAL_CODE"));
	        wmsdemandbean.setSupplierNo(itemData.getString("VENDOR_CODE"));
	        wmsdemandbean.setSupplierName(itemData.getString("VENDOR_NAME"));
	        wmsdemandbean.setQty(itemData.getString("QUANTITY")==null?0:Double.parseDouble(itemData.getString("QUANTITY").toString()));
	        
	        wmsdemandbean.setPou(ElevatorCode);//配送地址
	        wmsdemandbean.setStation(WorkingLocation);//工位
	        wmsdemandbean.setLine(LineCategory);
	        wmsdemandbean.setBatch(itemData.getString("BATCH"));
	        wmsdemandbean.setMoveDirection(1);
	        wmsdemandbean.setHandOutEmp(itemData.getString("HANDOVER_USER_ID"));
	        
	        wmsdemandlist.add(wmsdemandbean);
	        
	        WSWMSLogisMvt wmslogismvtbean=new WSWMSLogisMvt();
	        wmslogismvtbean.setListNo("");
	        wmslogismvtbean.setPlant(itemData.getString("FROM_PLANT_CODE"));
	        wmslogismvtbean.setStgeLoc(lgort_f);//发货库位
	        wmslogismvtbean.setStckType(sobkz);//库存类型
	        wmslogismvtbean.setMaterial(itemData.getString("MATERIAL_CODE"));
	        wmslogismvtbean.setMaterialDesc(itemData.getString("MATERIAL_DESC"));
	        wmslogismvtbean.setVendor(itemData.getString("VENDOR_CODE"));
	        wmslogismvtbean.setEntryQnt(itemData.getString("QUANTITY")==null?0:Double.parseDouble(itemData.getString("QUANTITY").toString()));
	        wmslogismvtbean.setIsSAPAccount(1);
	        wmslogismvtbean.setMoveType(sap_move_type);
	        wmslogismvtbean.setMoveDirection(1);
	        wmslogismvtbean.setMovePlant(itemData.getString("PLANT_CODE"));
	        wmslogismvtbean.setMoveStloc("W001");//线边接收库位
	        wmslogismvtbean.setOrderID("");//订单号 是(261时)
	        wmslogismvtbean.setWBSElem("");//元素号 是(221时)
	        wmslogismvtbean.setHandOutEmp(itemData.getString("HANDOVER_USER_ID"));
	        wmslogismvtbean.setBatch(itemData.getString("BATCH"));
	        
	        wswmsLogisMvtlist.add(wmslogismvtbean);
		}
		this.updateJiaojie(paramlist);
		//判断库存的下架数量
		
		//过账
		Map<String, Object> retsapMap=this.sapJiaojie(paramlist);
		
		//调用总装物流交接接口
		JaxWsDynamicClientFactory dcf =JaxWsDynamicClientFactory.newInstance();
        org.apache.cxf.endpoint.Client wlmsclient =dcf.createClient("http://10.23.11.7/WLMS/WebService/WS_WLMS_ToWMS.asmx?wsdl");
        
        WSWMSList wlmsparamlist=new WSWMSList();
        Date curtdate=new Date();
        
        wlmsparamlist.setListNo(Long.toString(curtdate.getTime()));//时间戳
        wlmsparamlist.setUserName("WLMS");
        wlmsparamlist.setPassword("WLMS2014");
        wlmsparamlist.setIsDemandReceive(1);
        wlmsparamlist.setIsLogisMvt(1);
        
        wlmsparamlist.setListDemand(WMSdemand);
        wlmsparamlist.setListLogisMvt(WMSlogistMvt);
        
        Object[] objects=wlmsclient.invoke("Add_DemandReceive",wlmsparamlist);
        //输出调用结果
        WSWMSList retlist=(WSWMSList) objects[0];
        System.out.println("*****"+retlist.getStatus()+" message:"+retlist.getMessage());
        List<String> hintjiekouInfos = new ArrayList<String>(); 
        if("S".equals(retlist.getStatus())){
            hintjiekouInfos.add("");
        }else{
        	ArrayOfWSWMSDemand wmsdemand=retlist.getListDemand();
            List<WSWMSDemand> wmdemandlist=wmsdemand.getWSWMSDemand();
            for(WSWMSDemand bean:wmdemandlist){
                hintjiekouInfos.add(bean.getReturnMessage());
                //插入异常信息表
                Map<String, Object> wlmsExceptionMap=new HashMap<String, Object>();
                wlmsExceptionMap.put("BARCODE", bean.getPartLabelID());
                wlmsExceptionMap.put("DISPATCHING_NO", bean.getPickListNo());
                wlmsExceptionMap.put("ISSUBMIT", "0");
                wlmsExceptionMap.put("CREATE_DATE", sdf.parse(sdf.format(new Date())));
                wlmsExceptionMap.put("TYPE", "1");
                
                wlmsExceptionMap.put("ITEM_NO", "");
                wlmsExceptionMap.put("COMPONENT_NO", "");
                wlmsExceptionMap.put("CREATE_USER_ID", params.get("USERID"));
                wlmsExceptionMap.put("BARCODE1", "");
                wlmsExceptionMap.put("DISPATCHINGNO1", "");
                wlmsExceptionMap.put("BARCODE2", "");
                wlmsExceptionMap.put("DISPATCHINGNO2", "");
                
                disPatchingJISService.insertToWlmsException(wlmsExceptionMap);
                //
            }
            ArrayOfWSWMSLogisMvt wmslogismvt=retlist.getListLogisMvt();
            List<WSWMSLogisMvt> wmslogismvtlist=wmslogismvt.getWSWMSLogisMvt();
            for(WSWMSLogisMvt bean:wmslogismvtlist){
                hintjiekouInfos.add(bean.getReturnMessage());
            }
        }
        
        Map<String, Object> retMap=new HashMap<String, Object>();
        String xwlStr="";
        if(hintjiekouInfos.size()>0){
            for(int z=0;z<hintjiekouInfos.size();z++){
               xwlStr=xwlStr+hintjiekouInfos.get(z);
            }
        }
        
        String retsap="";
        if(retsapMap!=null){
        	retsap=retsapMap.get("retstr").toString();
        }
        
        retMap.put("jkinfo", xwlStr);
        retMap.put("retsap", retsap);
		return retMap;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public Map<String, Object> updateQueRenService(Map<String, Object> params) throws ParseException {
		List<Map<String, Object>> paramlist=new ArrayList<Map<String, Object>>();
		
		JSONArray jarr = JSON.parseArray(params.get("ARRLIST").toString());
		
		for(int m=0;m<jarr.size();m++){
			JSONObject itemData=jarr.getJSONObject(m);
			
			Map<String, Object> querenMap=new HashMap<String, Object>();
			querenMap.put("FROM_PLANT_CODE", itemData.getString("FROM_PLANT_CODE"));
			querenMap.put("BATCH", itemData.getString("BATCH"));
			querenMap.put("MATNR", itemData.getString("MATERIAL_CODE"));
			querenMap.put("XJ_QTY", itemData.getString("XJ_QTY"));
			querenMap.put("QUANTITY", itemData.getString("QUANTITY"));
			querenMap.put("LGORT", itemData.getString("LGORT"));
			querenMap.put("DISPATCHING_NO", itemData.getString("DISPATCHING_NO"));
			querenMap.put("ITEM_NO", itemData.getString("ITEM_NO"));
			querenMap.put("COMPONENT_NO", itemData.getString("COMPONENT_NO"));
			querenMap.put("STATUS", itemData.getString("STATUS"));
			querenMap.put("BARCODE", itemData.getString("BARCODE"));
			
			querenMap.put("USERNAME", params.get("USERNAME"));
			querenMap.put("FULL_NAME", params.get("FULL_NAME"));
			querenMap.put("USERID", params.get("USERID"));
			
			paramlist.add(querenMap);
		}
		this.updateQueRen(paramlist);
		//更新 拣配确认记录号(趟号)，拣配确认记录人
		this.updatePickRecordNo(paramlist);
		return null;
	}
	

}

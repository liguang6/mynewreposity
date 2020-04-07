package com.byd.wms.business.modules.out.service.impl;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.byd.utils.UserUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.byd.utils.DateUtils;
import com.byd.utils.R;
import com.byd.wms.business.modules.common.dao.CommonDao;
import com.byd.wms.business.modules.common.enums.WmsDocTypeEnum;
import com.byd.wms.business.modules.common.service.CommonService;
import com.byd.wms.business.modules.common.service.WmsCDocNoService;
import com.byd.wms.business.modules.common.service.WmsCTxtService;
import com.byd.wms.business.modules.config.dao.WmsSapPlantLgortDao;
import com.byd.wms.business.modules.config.entity.WmsCoreWhBinEntity;
import com.byd.wms.business.modules.config.entity.WmsSapPlantLgortEntity;
import com.byd.wms.business.modules.config.service.WmsCoreWhBinService;
import com.byd.wms.business.modules.in.dao.WmsInReceiptDao;
import com.byd.wms.business.modules.in.service.WmsInInboundService;
import com.byd.wms.business.modules.out.dao.ScannerOutDAO;
import com.byd.wms.business.modules.out.dao.WmsOutRequirementHeadDao;
import com.byd.wms.business.modules.out.dao.WmsOutRequirementItemDao;
import com.byd.wms.business.modules.out.entity.WmsOutPickingEntity;
import com.byd.wms.business.modules.out.entity.WmsOutRequirementHeadEntity;
import com.byd.wms.business.modules.out.entity.WmsOutRequirementItemEntity;
import com.byd.wms.business.modules.out.enums.OutModuleEnum;
import com.byd.wms.business.modules.out.enums.RequirementStatusEnum;
import com.byd.wms.business.modules.out.service.ScannerOutService;
import com.byd.wms.business.modules.out.service.WmsOutPickingService;

@Service
public class ScannerOutServiceImpl implements ScannerOutService{

	@Autowired
	WmsCDocNoService docService;
	
	@Autowired
	WmsOutPickingService outPickingService;
	
	@Autowired
	ScannerOutDAO scannerOutDao;
	
	@Autowired
	WmsOutRequirementHeadDao headDao;

	@Autowired
	WmsOutRequirementItemDao itemDao;
	
	@Autowired
	CommonService commonService;
	
	@Autowired
	CommonDao commonDao;
	
	@Autowired
	private WmsInReceiptDao wmsInReceiptDao; 
	
	@Autowired
	UserUtils userUtils;
	
	@Autowired
    private WmsCoreWhBinService wmsCoreWhBinService;
	
	@Autowired
	private WmsSapPlantLgortDao wmsSapPlantLgortDao;
	
	/**
	 * 下架
	 */
	@Override
	@Transactional
	public int obtained(List<Map<String, Object>> params) {
		int outPickingItemNo =1;
		for(Map<String,Object> record:params){
			//产生拣配下架记录
			WmsOutPickingEntity outPicking = new WmsOutPickingEntity();
			String werks = (String) record.get("WERKS");
			String whNumber = (String) record.get("WH_NUMBER");
			String outPickingNo =  docService.getDocNo(werks, WmsDocTypeEnum.OUT_PICKING.getCode());
			String businessName = (String)record.get("BUSINESS_NAME");
			String businessType = (String) record.get("BUSINESS_TYPE");
			String text = (String) record.get("TEXT"); 
			String matnr = (String) record.get("MATNR");
			String maktx = (String) record.get("MAKTX");
			String unit = (String) record.get("UNIT");
			String lifnr = (String) record.get("LIFNR");
			String binCode = (String) record.get("BIN_CODE");
			String lgort = (String) record.get("LGORT");
			//下架储位?
			//非限制下架数量?
			//锁定拣配下架数量?
			String batch = (String) record.get("BATCH");
			//非限制交接过账数量?
			//锁定交接过账数量?
			//接收库位?
			String sobkz = (String) record.get("SOBKZ");
			//核销标识 默认 0 否 X 是?
			//接收人
			outPicking.setReceiver((String)record.get("RECEIVER"));
			//交接时间
			outPicking.setHandoverDate(DateUtils.format(new Date(), DateUtils.DATE_TIME_PATTERN));
			//标签数量
			Double boxQty = Double.parseDouble((String)record.get("BOX_QTY"));
			
			outPicking.setQty(boxQty.longValue());
			outPicking.setPickNo(outPickingNo);
			outPicking.setPickItemNo(String.valueOf(outPickingItemNo));outPickingItemNo++;
			outPicking.setWerks(werks);
			outPicking.setWhNumber(whNumber);
			//已拣配
			outPicking.setReqItemStatus("01");
			outPicking.setItemText(text);
			outPicking.setCreator(record.get("USERNAME").toString());
			outPicking.setCreateDate(DateUtils.format(new Date(), DateUtils.DATE_TIME_PATTERN));
			outPicking.setMatnr(matnr);
			outPicking.setMaktx(maktx);
			outPicking.setUnit(unit);
			outPicking.setLifnr(lifnr);
			outPicking.setBinCode(binCode);
			outPicking.setLgort(lgort);
			outPicking.setBatch(batch);
			outPicking.setSobkz(sobkz);
			
//			outPickingService.insert(outPicking);
			
			if("05".equals(outPicking.getBinCode())){
				//如果是立库类型，调用立库接口
				callLKInterface();
			}
			
			//更新库存数据
			Map<String,Object> obj = new HashMap<>();
			obj.put("werks", werks);
			obj.put("batch", batch);
			obj.put("matnr", matnr);
			obj.put("qty", boxQty);
			obj.put("xjBinCode", binCode);
			scannerOutDao.updateStockXJQty(obj);
		}
		return 0;
	}
	
	/**
	 * 调用立库接口
	 */
	private void callLKInterface(){
		//TODO:待完成
	}

	@Override
	@Transactional
	public int cancelOntained(List<Map<String, Object>> params) {
		for(Map<String,Object> item:params){
			String werks = (String) item.get("WERKS");
			String whNumber = (String) item.get("WH_NUMBER");
			String matnr = (String) item.get("MATNR");
			String batch = (String) item.get("BATCH");
			String lgort = (String) item.get("LGORT");
			Double qty = Double.parseDouble((String)item.get("BOX_QTY"));
			String binCode = (String) item.get("BIN_CODE");
			String labelNo = (String) item.get("LABEL_NO");
			String businessName = (String) item.get("BUSINESS_NAME");
			if(StringUtils.isBlank(labelNo) || StringUtils.isBlank(werks) || StringUtils.isBlank(matnr) || StringUtils.isBlank(batch)){
				throw new IllegalArgumentException("必填参数不能为空");
			}
			//查询出库拣配数据
			//TODO: TEST
//			WmsOutPickingEntity outPicking = outPickingService.selectOne(
//					new EntityWrapper<WmsOutPickingEntity>()
//					.eq("WERKS", werks)
//					.eq("WH_NUMBER", whNumber)
//					.eq("BUSINESS_NAME", businessName)
//					.eq("MATNR", matnr)
//					.eq("BATCH", batch)
//					.eq("LGORT", lgort));
//			if(outPicking == null){
//				throw new IllegalArgumentException("没有找到出库拣配数据");
//			}
			
			//删除出库拣配记录,更新删除标识
			Map<String,Object> updateOutPickingMap = new HashMap<>();
			updateOutPickingMap.put("DEL","X");
//			updateOutPickingMap.put("PICK_NO", outPicking.getPickNo());
//			updateOutPickingMap.put("PICK_ITEM_NO", outPicking.getPickItemNo());
			scannerOutDao.updateOutPicking(updateOutPickingMap);
			
			//更新库存
			Map<String,Object> updateStockMap = new HashMap<String,Object>();
			updateStockMap.put("werks", werks);
			updateStockMap.put("matnr", matnr);
			updateStockMap.put("batch", batch);
			updateStockMap.put("qty", -qty);
			updateStockMap.put("xjBinCode", "");
			scannerOutDao.updateStockXJQty(updateStockMap);
			
			if("05".equals(binCode)){
				//调用立库接口
				//TODO: 待实现...
				callLKInterface();
			}
		}
		return 0;
	}
	

	public String isFirstBatch(List<Map<String, Object>> params){
		
		for (Map<String, Object> item : params) {
			String matnr = (String) item.get("MATNR");
			String werks = (String) item.get("WERKS");
			String wh_number = (String) item.get("WH_NUMBER");
			String lgort = (String) item.get("LGORT");
			String bin_code = (String) item.get("BIN_CODE");
			String batch = (String) item.get("BATCH");
			// 判断是否是最先批次
			Map<String, Object> queryMap = new HashMap<String, Object>();
			queryMap.put("werks", werks);
			queryMap.put("wh_number", wh_number);
			queryMap.put("lgort", lgort);
			queryMap.put("bin_code", bin_code);
			queryMap.put("matnr", matnr);
			List<Map<String,Object>> batchs =  scannerOutDao.selectMatBatch(queryMap);
			
			if(!CollectionUtils.isEmpty(batchs) && batchs.size() > 1){
				if(!batchs.get(0).get("BATCH").equals(batch)){
					return batch;
				}
			}
		}
		return null;
	}


	@Override
	public int updateBarcodeQty(Map<String, Object> data) {
		return scannerOutDao.updateBarcodeQty(data);
	}

	@Override
	public List<Map<String, Object>> queryCoreLabelByPda(Map<String, Object> data) {
		return scannerOutDao.queryCoreLabelByPda(data);
	}

	@Override
	public boolean saveLabelByPda(ArrayList params) {
		return scannerOutDao.saveLabelByPda(params);
	}

	@Override
	public List<Map<String, Object>> queryBarcodeCache(Map<String, Object> data) {
		return scannerOutDao.queryBarcodeCache(data);
	}

	@Override
	public List<Map<String,Object>>  queryBarcodeOnly(Map<String, Object> data) {
		return scannerOutDao.queryBarcodeOnly(data);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public String handoverComfirm(List<Map<String, Object>> params) {		
		String werks = (String) params.get(0).get("WERKS");
		String whNumber = (String) params.get(0).get("WH_NUMBER");
		String businessName = (String) params.get(0).get("BUSINESS_NAME");
		String businessType = (String) params.get(0).get("BUSINESS_TYPE");
		String pzDate = (String) params.get(0).get("certificateDate");//凭证日期
		String jzDate = (String) params.get(0).get("postingDate");//过账日期
		String sendLgort = (String) params.get(0).get("sendLgort");
		String receiveWerks = (String) params.get(0).get("jsgc");
		String receiveLgort = (String) params.get(0).get("reciveLgort");
		String sakto = (String) params.get(0).get("zzkm");
		String receiver = (String) params.get(0).get("jsf");
		String costCenter = (String) params.get(0).get("cbzx");
		String moNo = (String) params.get(0).get("nbdd");
		String wbsElementNo = (String) params.get(0).get("wbsElementNo");
		String w_vendor = (String) params.get(0).get("w_vendor");
		String costomerCode = (String) params.get(0).get("costomerCode");
		String jsgc = (String) params.get(0).get("jsgc");
		String userName = (String) userUtils.getUser().get("USERNAME");

		String businessClass = OutModuleEnum.BUSINESS_CLASS.getCode();
		WmsOutRequirementHeadEntity head = new WmsOutRequirementHeadEntity();
		List<WmsOutRequirementItemEntity> itemList = new ArrayList<WmsOutRequirementItemEntity>();
		List<Map<String, Object>> taskList = new ArrayList<Map<String, Object>>();
		
		String reqNo = docService.getDocNo(werks, WmsDocTypeEnum.OUT_WAREHOURSE.getCode());
		// 出库表头字段填充
		head.setRequirementNo(reqNo);
		head.setReceiver(receiver);
		head.setReceiveWerks(receiveWerks);
		head.setCreateDate(DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
		head.setCreator(userUtils.getUser().get("USERNAME").toString());
		head.setRequirementStatus("06");
		head.setRequirementType(businessName);
		head.setWerks(werks);
		head.setWhNumber(whNumber);
//			审批标识
//			head.setCheckFlag("X");
		
		List<Map<String, Object>> newparams = mergeList(params);
		int reqItemNo = 1;
		for (Map<String, Object> param : newparams) {
			param.put("EDITOR", userUtils.getUser().get("USERNAME").toString());
			param.put("EDIT_DATE", DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
			param.put("CREATE_DATE", DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
			param.put("REQUIREMENT_NO", reqNo);
			param.put("REQUIREMENT_ITEM_NO", String.valueOf(reqItemNo));
			WmsOutRequirementItemEntity item = new WmsOutRequirementItemEntity();
			Map<String,Object> taskMap = new HashMap<String,Object>();
			// 填充出库行项目字段信息
			item.setCreateDate(DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
			item.setCreator(userUtils.getUser().get("USERNAME").toString());
			item.setReqItemStatus("05");
			item.setQty(Double.parseDouble((String)param.get("BOX_QTY")));
			item.setQtyXj(Double.parseDouble((String)param.get("BOX_QTY")));
			item.setQtyReal(Double.parseDouble((String)param.get("BOX_QTY")));
			item.setSobkz((String) param.get("SOBKZ"));
			item.setMatnr((String) param.get("MATNR"));
			item.setMaktx((String) param.get("MAKTX"));		
			item.setRequirementItemNo(String.valueOf(reqItemNo));
			item.setRequirementNo(reqNo);
			item.setUnit((String) param.get("UNIT"));
			item.setDel("0");
			item.setLgort(sendLgort);
			item.setReceiveLgort(receiveLgort);
			item.setCostCenter(costCenter);
			item.setSakto(sakto);
			item.setMoNo(moNo);
			item.setBusinessName(businessName);
			item.setBusinessType(businessType);
			item.setWbs(wbsElementNo);
			if( "49".equals(businessName)) {//委外
				item.setCustomer(w_vendor);
			}
			if( "52".equals(businessName)) {//外部销售发货
				item.setCustomer(costomerCode);
			}
			
			itemList.add(item);
			
			//获取仓库任务号
			Map<String,Object> docparam = new HashMap<String,Object>();
			docparam.put("WMS_DOC_TYPE", "14");//仓库任务
			Map<String,Object> doc = docService.getDocNo(docparam);
			if(doc.get("MSG")!=null&&!"success".equals(doc.get("MSG"))) {
				throw new RuntimeException(doc.get("MSG").toString());
			}
			String WTNO = doc.get("docno").toString(); //仓库任务号
			taskMap.put("TASK_NUM", WTNO);
			taskMap.put("PROCESS_TYPE", "01");
			taskMap.put("WERKS", werks);
			taskMap.put("WH_NUMBER", whNumber);
			taskMap.put("WT_STATUS", "05");
			taskMap.put("MATNR", param.get("MATNR"));
			taskMap.put("MAKTX", param.get("MAKTX"));
			taskMap.put("UNIT", param.get("UNIT"));
			taskMap.put("LIFNR", param.get("LIFNR"));
			taskMap.put("LGORT", param.get("LGORT"));
			taskMap.put("SOBKZ", param.get("SOBKZ"));
			taskMap.put("HX_FLAG", "0");
			if (null != param.get("BIN_CODE")) {
				List<WmsCoreWhBinEntity> binList=wmsCoreWhBinService.selectList(
			    		 new EntityWrapper<WmsCoreWhBinEntity>()
			    		 .eq("WH_NUMBER", whNumber)
			             .eq("BIN_CODE", param.get("BIN_CODE")).eq("DEL","0"));
				if (binList.size()>0) {
					taskMap.put("FROM_STORAGE_AREA", binList.get(0).getStorageAreaCode());
					taskMap.put("TO_STORAGE_AREA", binList.get(0).getStorageAreaCode());
				}
			}
			taskMap.put("FROM_BIN_CODE", param.get("BIN_CODE"));
			taskMap.put("TO_BIN_CODE", "BBBB");
			taskMap.put("QUANTITY", param.get("BOX_QTY"));
			taskMap.put("CONFIRM_QUANTITY", param.get("BOX_QTY"));
			taskMap.put("CONFIRMOR", userUtils.getUser().get("USERNAME").toString());
			taskMap.put("CONFIRM_TIME", DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
			taskMap.put("BATCH", param.get("BATCH"));
			taskMap.put("LABEL_NO", param.get("LABEL_NO"));
			taskMap.put("REFERENCE_DELIVERY_NO", reqNo);
			taskMap.put("REFERENCE_DELIVERY_ITEM", reqItemNo);
			taskMap.put("REAL_QUANTITY", param.get("BOX_QTY"));
			taskMap.put("CREATOR", userUtils.getUser().get("USERNAME").toString());
			taskMap.put("CREATE_DATE", DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
			taskMap.put("EDITOR", userUtils.getUser().get("USERNAME").toString());
			taskMap.put("EDIT_DATE", DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
			taskList.add(taskMap);
			reqItemNo++;
			
			param.put("SOBKZ_BACK", param.get("SOBKZ"));
			if("48".equals(businessName)) {
				param.put("SOBKZ", "Z");
			}
		}
		
		// 新增出库表头
		headDao.insert(head);
		// 新增出库行项目
		for (WmsOutRequirementItemEntity item : itemList) {
			itemDao.insert(item);
		}
		if(taskList.size()>0)
		scannerOutDao.insertCoreWHTask(taskList);
		
		List<Map<String, Object>> retMaterialStockList=commonService.getMaterialStock(newparams);
		if(retMaterialStockList.size() < 1){
			throw new RuntimeException("库存数量短缺!");
		}
		
		for (Map<String, Object> param : newparams) {
			String matnr = (String) param.get("MATNR");
	    	String lgort = (String) param.get("LGORT");
	    	String lifnr = (String) param.get("LIFNR");
	    	String sobkz = (String) param.get("SOBKZ");
	    	String batch = (String) param.get("BATCH");
	    	String binCode = (String)param.get("BIN_CODE");
	    	BigDecimal boxqty = param.get("BOX_QTY") == null ? BigDecimal.ZERO : new BigDecimal(param.get("BOX_QTY").toString());
	    	
	    	for (Map<String,Object> stock : retMaterialStockList) { 
	    		String lifnrstock = stock.get("LIFNR") == null ? "" : stock.get("LIFNR").toString();
		    	if (matnr.equals(stock.get("MATNR")) && lgort.equals(stock.get("LGORT")) 
		    			&& lifnr.equals(lifnrstock) && sobkz.equals(stock.get("SOBKZ"))
		    			&& batch.equals(stock.get("BATCH")) && binCode.equals(stock.get("BIN_CODE"))
		    			&& !stock.get("BIN_CODE").equals("9010")) {
		    		
		    		BigDecimal stockqty = new BigDecimal(stock.get("STOCK_QTY").toString());
					if (boxqty.compareTo(stockqty) <= 0) {
						boxqty = BigDecimal.ZERO;
						break;
					} else {
						boxqty = boxqty.subtract(stockqty);
					}
		    	}
	    	}
	    	
	    	if (boxqty.compareTo(BigDecimal.ZERO) > 0) 
	    		throw new RuntimeException(matnr +"物料，库存短缺"+boxqty);
		}
		
		//查询配置的业务是否需要过账
		String SAP_MOVE_TYPE="";//SAP移动类型，为空不进行SAP过账
		String WMS_MOVE_TYPE = "";
		Map<String,Object> cdmap=new HashMap<String,Object>();
		cdmap.put("BUSINESS_NAME", businessName);
		cdmap.put("BUSINESS_TYPE", businessType);
		cdmap.put("BUSINESS_CLASS", businessClass);
		cdmap.put("WERKS", werks);
		cdmap.put("SOBKZ", params.get(0).get("SOBKZ_BACK"));
		Map<String,Object> moveSyn= commonDao.getMoveAndSyn(cdmap);
				
		if(moveSyn != null){
			SAP_MOVE_TYPE=(String)moveSyn.get("SAP_MOVE_TYPE");
			WMS_MOVE_TYPE = (String)moveSyn.get("WMS_MOVE_TYPE");
		}
				
		/**
		 * 库存地点调拨、工厂间调拨（301）业务，如果接受库位已上WMS，需要增加接受库位库存
		 */
		boolean uReceiveStock = false; //调拨业务，是否更新接收库位库存
    	String rWerks = "";
    	if ((businessName.equals("46") && SAP_MOVE_TYPE.contains("301")) || businessName.equals("47") || businessName.equals("48") || businessName.equals("77")) {
    		if (businessName.equals("47") || businessName.equals("48")) {
    			rWerks = werks;
    		} else {
    			rWerks = receiveWerks;
    		}
    		
    		List<WmsSapPlantLgortEntity> plantlgortlist = wmsSapPlantLgortDao.selectList(new EntityWrapper<WmsSapPlantLgortEntity>()
		             .eq(StringUtils.isNotBlank(rWerks),"WERKS", rWerks)
		             .eq(StringUtils.isNotBlank(receiveLgort),"LGORT", receiveLgort)
		             );
    		
    		if (plantlgortlist.size() > 0) {
    			uReceiveStock = true;
    		}
    	}
		
		//增加库存
		if(uReceiveStock) { 
			for (Map<String, Object> param : newparams) {
				param.put("BIN_CODE", "AAAA");
			}
			scannerOutDao.updateStock(newparams);
		}
	
		for(int i = 0;i<params.size();i++){
			String matnr = (String) params.get(i).get("MATNR");
			String batch = (String) params.get(i).get("BATCH");
			Double qty = Double.parseDouble((String)params.get(i).get("BOX_QTY"));
			String binCode = (String) params.get(i).get("BIN_CODE");
			String lgort = (String) params.get(i).get("LGORT");
			String reciveLgort = (String) params.get(i).get("reciveLgort");
			String lifnr = (String) params.get(i).get("LIFNR");
			String labelNo = (String) params.get(i).get("LABEL_NO");	
			String sobkz = (String) params.get(i).get("SOBKZ");	
		    Map<String,Object> updateStockMap = new HashMap<String,Object>();
		    updateStockMap.put("werks", werks);
		    updateStockMap.put("matnr", matnr);
		    updateStockMap.put("batch", batch);
		    //updateStockMap.put("xjBinCode", "9010");
		    updateStockMap.put("qty", qty);
		    //updateStockMap.put("xjQty", 0);
		    updateStockMap.put("SOBKZ", sobkz);
		    updateStockMap.put("binCode", binCode);
		    updateStockMap.put("lgort", lgort);
		    updateStockMap.put("lifnr", lifnr);
		    updateStockMap.put("EDITOR", userName);
		    updateStockMap.put("EDIT_DATE", DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
		    commonDao.updateStockXJQty(updateStockMap);
		    //更新条码标满箱数量为0
		    updateStockMap.put("labelNo", labelNo);
		    if("47".equals(businessName)||"48".equals(businessName)) 
		    	updateStockMap.put("reciveLgort", reciveLgort);
		    if("48".equals(businessName)) 
		    	updateStockMap.put("SOBKZ", (String) params.get(i).get("SOBKZ"));
		    scannerOutDao.updateBarcodeQty(updateStockMap);
						
		}		
		String key = (String) params.get(0).get("key");
		if(key!=null &&key.equals("pda")){
			//清除条码缓存表数据
			List<String> list = new ArrayList<String>();
			params.stream().forEach(e->list.add(e.get("LABEL_NO").toString()));
			scannerOutDao.deteleCacheBarcodeByCreator(list);
		}else if(key!=null &&key.equals("pc")){
		}
		
		
		
		
		//保存WMS凭证记录抬头和明细
		String txtrule = "";
		String txtruleitem = "";
		
//		Map<String, String> txtparams = new HashMap<String, String>(); 
//		txtparams.put("BUSINESS_NAME", businessName);
//		txtparams.put("JZ_DATE", jzDate);
//		txtparams.put("WERKS", werks);
//		txtparams.put("FULL_NAME", userName);
//		txtparams.put("PURPOSE", "");
//		txtparams.put("requirement_no", reqNo);
//		Map<String, Object> retrule=wmsCtxService.getRuleTxt(txtparams);
//		if("success".equals(retrule.get("msg"))){
//			txtrule=retrule.get("txtrule").toString();//头文本
//			txtruleitem=retrule.get("txtruleitem").toString();//行文本
//		}
		
		Map<String,Object> headParams = new HashMap<>();
		headParams.put("SAP_MOVE_TYPE", SAP_MOVE_TYPE);
		headParams.put("WMS_MOVE_TYPE", WMS_MOVE_TYPE);
		String SYN_FLAG = moveSyn.get("SYN_FLAG")==null?"":moveSyn.get("SYN_FLAG").toString();
		if("0".equals(SYN_FLAG))
			headParams.put("SYN_FLAG", "01");
		if("X".equals(SYN_FLAG))
			headParams.put("SYN_FLAG", "02");
		headParams.put("WERKS", werks);
		headParams.put("PZ_DATE", pzDate);
		headParams.put("JZ_DATE", jzDate);
		headParams.put("HEADER_TXT",txtrule);
		headParams.put("CREATOR",userName);
		headParams.put("CREATE_DATE",jzDate);
		headParams.put("BUSINESS_CLASS", businessClass);
		for(Map<String, Object> map : newparams) {
			map.put("SAP_MOVE_TYPE", SAP_MOVE_TYPE);
			map.put("WMS_MOVE_TYPE", WMS_MOVE_TYPE);
			map.put("QTY_SAP", map.get("BOX_QTY"));
			map.put("IN_QTY", map.get("BOX_QTY"));
			map.put("QTY_WMS", map.get("BOX_QTY"));
			map.put("COST_CENTER", costCenter);
//			map.put("ITEM_TEXT", txtruleitem);
			map.put("MOVE_PLANT", receiveWerks);
			map.put("MOVE_STLOC", receiveLgort);
			map.put("LGORT", sendLgort);
			map.put("BUSINESS_CLASS", businessClass);
			map.put("SOBKZ", map.get("SOBKZ_BACK"));
			
			map.put("CREATOR", userName);
			//TODO
		}
		String wmsNo =  commonService.saveWMSDoc(headParams, newparams);

		List<Map<String, Object>> labList = new ArrayList<>();
		if("77".equals(businessName)||"46".equals(businessName)||"53".equals(businessName)) {
			int itemNo = 1;
			for(Map<String, Object> m : params){
				Map<String, Object> map = new HashMap<String, Object>();
				map.putAll(m);
				map.put("WERKS", jsgc);
				map.put("WH_NUMBER", jsgc);
				map.put("WMS_NO", wmsNo);
				map.put("WMS_ITEM_NO", itemNo++);
				map.put("LABEL_STATUS", "00");
				map.put("LGORT", receiveLgort);
				map.put("BIN_CODE", "AAAA");
				map.put("CREATOR", userName);
				map.put("CREATE_DATE", DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
				labList.add(map);
			}
			
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("WERKS", jsgc);
			param.put("WH_NUMBER", jsgc);
			param.put("HEADER_TXT", txtrule);
			param.put("ITEMLIST", params);
			try {
			//	wmsInInboundService.saveInboundByout(param);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if(labList.size()>0)
		wmsInReceiptDao.insertCoreLabel(labList);
		
		List<Map<String, Object>> newParams = new ArrayList<Map<String,Object>>();
		HashSet<String> set = new HashSet<>();
		for(Map<String, Object> map : newparams) {
			if(set.add(map.get("MATNR").toString())) {
				map.put("QTY_SAP", map.get("BOX_QTY"));
				map.put("COSTCENTER", costCenter);
				newParams .add(map);
			}else {
				for(Map<String, Object> temp : newParams) {
					if(temp.get("MATNR").toString().equals(map.get("MATNR").toString())) {
						temp.put("QTY_SAP", Integer.valueOf(map.get("BOX_QTY").toString())+Integer.valueOf(temp.get("QTY_SAP").toString()));
					}
				}
				map.put("QTY_SAP", map.get("BOX_QTY"));
			}
			
		}
		headParams.put("BUSINESS_NAME", businessName);
		headParams.put("BUSINESS_TYPE", businessType);
		headParams.put("BUSINESS_CLASS", businessClass);
		
		//需要过账的物料
		headParams.put("matList", newParams);
		//过账SAP
		try {
			String sapNo = commonService.doSapPost(headParams);
			return String.format("交接成功，SAP凭证号：%s    WMS凭证号：%s",sapNo,wmsNo);
		} catch (Exception e2) {
			throw new RuntimeException(e2.getMessage());
		}
	}

	@Override
	public void deteleCacheBarcodeByCreator(List<String> data) {
		scannerOutDao.deteleCacheBarcodeByCreator(data);
	}
	
	public static List<Map<String,Object>> mergeList(List<Map<String,Object>> lists) {
		List<Map<String, Object>> newlist = new ArrayList<Map<String, Object>>();
		
		for (Map<String,Object> list : lists) {  
			String werks = (String) list.get("WERKS");
	    	String whNumber = (String) list.get("WH_NUMBER");
	    	String matnr = (String) list.get("MATNR");
	    	String lgort = (String) list.get("LGORT");
	    	String lifnr = (String) list.get("LIFNR");
	    	String sobkz = (String) list.get("SOBKZ");
	    	String batch = (String) list.get("BATCH");
	    		
	        int flag = 0;// 0为新增数据，1为增加 
	            
	        for (Map<String,Object> clist : newlist) {  
	        	String werks1 = (String) clist.get("WERKS");
		    	String whNumber1 = (String) clist.get("WH_NUMBER");
		    	String matnr1 = (String) clist.get("MATNR");
		    	String lgort1 = (String) clist.get("LGORT");
		    	String lifnr1 = (String) clist.get("LIFNR");
		    	String sobkz1 = (String) clist.get("SOBKZ");
		    	String batch1 = (String) clist.get("BATCH");
	  
	            if (werks.equals(werks1) && whNumber.equals(whNumber1) && matnr.equals(matnr1) 
	                	&& lgort.equals(lgort1) && lifnr.equals(lifnr1) && sobkz.equals(sobkz1) && batch.equals(batch1)) {  
	                	
	                BigDecimal boxqty = list.get("BOX_QTY") == null ? BigDecimal.ZERO:new BigDecimal(list.get("BOX_QTY").toString()); 
	                BigDecimal newqty = clist.get("BOX_QTY") == null ? BigDecimal.ZERO:new BigDecimal(clist.get("BOX_QTY").toString());
	                newqty = newqty.add(boxqty);
	                clist.put("BOX_QTY", newqty.toString());  
	                String lableno = list.get("LABEL_NO") == null ? "":list.get("LABEL_NO").toString();
	                String lableno1 = clist.get("LABEL_NO") == null ? "":clist.get("LABEL_NO").toString();
	                lableno = lableno.concat(",").concat(lableno1);
	                clist.put("LABEL_NO", lableno);  
	                flag = 1;  
	                break;  
	            }  
	        }  
	        if (flag == 0) {  
	        	Map<String,Object> dataMapTemp = new HashMap<String,Object>();
            	
				dataMapTemp.putAll(list);
				newlist.add(dataMapTemp);
	        }
        }
        
        return newlist;
	}
}

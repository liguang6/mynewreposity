package com.byd.wms.business.modules.qc.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.byd.utils.DateUtils;
import com.byd.utils.PageUtils;
import com.byd.utils.Query;
import com.byd.wms.business.modules.common.enums.WmsDocTypeEnum;
import com.byd.wms.business.modules.common.service.WmsCDocNoService;
import com.byd.wms.business.modules.config.entity.WmsCQcResultEntity;
import com.byd.wms.business.modules.config.service.WmsCQcResultService;
import com.byd.wms.business.modules.in.entity.WmsInReceiptEntity;
import com.byd.wms.business.modules.in.service.WmsInReceiptService;
import com.byd.wms.business.modules.qc.dao.WmsQcInspectionHeadDao;
import com.byd.wms.business.modules.qc.dao.WmsQcInspectionItemDao;
import com.byd.wms.business.modules.qc.entity.WmsQcInspectionHeadEntity;
import com.byd.wms.business.modules.qc.entity.WmsQcInspectionItemEntity;
import com.byd.wms.business.modules.qc.entity.WmsQcRecordEntity;
import com.byd.wms.business.modules.qc.entity.WmsQcResultEntity;
import com.byd.wms.business.modules.qc.enums.InspectionItemStatus;
import com.byd.wms.business.modules.qc.enums.InspectionStatus;
import com.byd.wms.business.modules.qc.enums.QcRecordType;
import com.byd.wms.business.modules.qc.service.WmsQcInspectionHeadService;
import com.byd.wms.business.modules.qc.service.WmsQcInspectionItemService;
import com.byd.wms.business.modules.qc.service.WmsQcRecordService;
import com.byd.wms.business.modules.qc.service.WmsQcResultService;



@Service("wmsQcInspectionHeadService")
public class InspectionHeadServiceImpl extends ServiceImpl<WmsQcInspectionHeadDao, WmsQcInspectionHeadEntity> implements WmsQcInspectionHeadService {

	@Autowired
	private WmsQcInspectionItemService itemService;
	@Autowired
	private WmsQcInspectionHeadService headService;
	@Autowired
	private WmsQcResultService qcResultService;
	@Autowired
	private WmsQcRecordService qcRecordService;
	@Autowired
	private WmsCQcResultService cQcResultService;
	@Autowired
	private WmsInReceiptService inReceiptService;
	@Autowired
	private WmsCDocNoService wmsCDocNoService;
	@Autowired
	private ServiceUtils serviceUtils;
	
	
	private static final String WMS_DOC_TYPE_RECORD = "04";
	private static final String WMS_DOC_TYPE_RESULT = "05";
	
	
	@Autowired
	WmsQcInspectionHeadDao headDAO ;
	
    @Override
    public PageUtils queryPage( Map<String, Object> params) {  	
    	Page<Map<String,Object>> page= new Query<Map<String,Object>>(params).getPage();
    	List<Map<String,Object>> results =  headDAO.queryInspectionList(page,params);
    	page.setRecords(results);
        return new PageUtils(page);
    }

    /**
     * 来料质检-未质检  批量质检结果或单批质检结果
     * 1.批量质检 batchInscpectionResults  为不同的送检单项
     * 2.单批质检batchInscpectionResults  为同一个送检单号，质检结果和质检数量不同
     */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void saveBatchInspectionResult(List<WmsQcInspectionItemEntity> batchInscpectionResults,String staffNumber) {	
		
		//SysUserEntity currentUser = ShiroUtils.getUserEntity();
		//1、更新质检单明细
		for(WmsQcInspectionItemEntity item:batchInscpectionResults){
			WmsQcInspectionItemEntity queryEntity = itemService.selectById(item.getId());
			if(queryEntity.getInspectionItemStatus().equals(InspectionItemStatus.FINISHED.getCode()))
				continue;
			queryEntity.setInspectionItemStatus(InspectionItemStatus.FINISHED.getCode());
			itemService.updateById(queryEntity);
		}

		//2.更新质检单抬头 
		//获取需要更新的送检单号 ，batchInscpectionResults中存在的质检单才需要更新状态
		Set<String> inpectionHeads = new HashSet<String>();
		for(WmsQcInspectionItemEntity item:batchInscpectionResults){
			inpectionHeads.add(item.getInspectionNo());
		}
		//更新送检单
		for(String inspectionNo:inpectionHeads){
			WmsQcInspectionHeadEntity inspectionHeadEntity = this.selectOne(new EntityWrapper<WmsQcInspectionHeadEntity>().eq("INSPECTION_NO", inspectionNo));
		    List<WmsQcInspectionItemEntity> inspectionItemEntitys = itemService.selectList(new EntityWrapper<WmsQcInspectionItemEntity>().eq("INSPECTION_NO", inspectionNo));
		    //如果所有的送检单明细项的状态都为已质检-则更新质检单抬头为已完成
		    Integer inspectionStatusFinished = 0;//记录更新完成的数量
		    for(WmsQcInspectionItemEntity item:inspectionItemEntitys){
		    	if(InspectionItemStatus.FINISHED.getCode().equals(item.getInspectionItemStatus())){
		    		inspectionStatusFinished ++;
		    	}
		    }
		    if(inspectionStatusFinished < inspectionItemEntitys.size() && inspectionStatusFinished !=0){
		    	inspectionHeadEntity.setInspectionStatus(InspectionStatus.PART_COMPLETE.getCode());
		    }else{
		    	inspectionHeadEntity.setInspectionStatus(InspectionStatus.COMPLETED.getCode());
		    }
		    //更新送检单抬头状态
		    this.updateAllColumnById(inspectionHeadEntity);
		}
		
		
		
		//3.判断是否为批量质检 
		boolean isBatch = false;
		if(StringUtils.isNotBlank(batchInscpectionResults.get(0).getBatchQcFlag())){
			isBatch = true;
		}
		
		//4.生成【检验记录】和【检验结果】
		List<WmsQcRecordEntity> inpectionQcRecords = new ArrayList<WmsQcRecordEntity>();
		List<WmsQcResultEntity> inspectionQcResults = new ArrayList<WmsQcResultEntity>();
		Map<String,Object> docNoParams = new HashMap<String,Object>();
		String signleBatchRecordNo = null;
		int itemNo = 1;
		for(WmsQcInspectionItemEntity itemEntity:batchInscpectionResults){
			 WmsQcInspectionItemEntity existedEntity = itemService.selectById(itemEntity.getId());
		     WmsQcRecordEntity record = new WmsQcRecordEntity();
		     record.setBatch(itemEntity.getBatch());
		     record.setWerks(itemEntity.getWerks());
		     record.setWhNumber(itemEntity.getWhNumber());
		     record.setUnit(itemEntity.getUnit());
		     record.setStockSource(existedEntity.getStockSource());
		     record.setRecordQty(itemEntity.getInspectionQty());
		     record.setQcResultCode(itemEntity.getQcResultCode());
		     record.setQcResultText(itemEntity.getQcResultText());
		     record.setQcResult(itemEntity.getReturnreason());
		     record.setQcRecordType(QcRecordType.INIT.getCode());
		     record.setReturnReasonType(itemEntity.getReturnreasontype());
		     
		     if(!isBatch && signleBatchRecordNo == null){
		    	 docNoParams.put("WERKS", itemEntity.getWerks());
			     docNoParams.put("WMS_DOC_TYPE", WMS_DOC_TYPE_RECORD);
		    	 signleBatchRecordNo = (String)wmsCDocNoService.getDocNo(docNoParams).get("docno");
		     }
		     if(!isBatch){
		    	 record.setQcRecordNo(signleBatchRecordNo);//单批质检，质检记录号相同。
		    	 record.setQcRecordItemNo(String.valueOf(itemNo));//单批质检
		     }else{
		    	 docNoParams.put("WERKS", itemEntity.getWerks());
			     docNoParams.put("WMS_DOC_TYPE", WMS_DOC_TYPE_RECORD);
			     record.setQcRecordNo((String)wmsCDocNoService.getDocNo(docNoParams).get("docno"));
			     record.setQcRecordItemNo(String.valueOf(1));//批量质检
			     
			     //工厂开启了标签管理
				if (serviceUtils.isBarcodeFlag(itemEntity.getWerks(), itemEntity.getWhNumber())) {
					resolveLabel(itemEntity);//更新标签状态
					record.setLabelNo(itemEntity.getLabelNo());
				}
			     
		     }
		     
		     record.setQcPeople(itemEntity.getQcPeople());
		     record.setQcDate(DateUtils.format(new Date(), DateUtils.DATE_TIME_PATTERN));//质检日期
		     record.setMatnr(itemEntity.getMatnr());
		     record.setMaktx(itemEntity.getMaktx());
		     record.setLgort(itemEntity.getLgort());
		     record.setLifnr(itemEntity.getLifnr());
		     record.setSobkz(itemEntity.getSobkz());
		     if(itemEntity.getDestoryQty() !=null && itemEntity.getDestoryQty()>0) {
		    	 record.setIqcCostCenter(itemEntity.getCostcenter());
		     }else {
		    	 record.setIqcCostCenter(null);
		     }
		     record.setInspectionNo(itemEntity.getInspectionNo());
		     record.setInspectionItemNo(itemEntity.getInspectionItemNo());
		     record.setDestroyQty(itemEntity.getDestoryQty());//批量质检-没有破坏数量
		     record.setStockSource(existedEntity.getStockSource());
		     //数量-分为批量质检和单批质检
		     if(itemEntity.getCheckedQty() == null){
		    	 record.setRecordQty(itemEntity.getInspectionQty());
		     }else{
		    	 record.setRecordQty(itemEntity.getCheckedQty());
		     }
		     record.setCreateDate(DateUtils.format(new Date(), DateUtils.DATE_TIME_PATTERN));
		     record.setCreator(staffNumber);
		     record.setEditor(staffNumber);
		     record.setEditDate(DateUtils.format(new Date(), DateUtils.DATE_TIME_PATTERN));
		     inpectionQcRecords.add(record);
		     
		     WmsQcResultEntity result = new WmsQcResultEntity();
		     result.setBatch(itemEntity.getBatch());
		     result.setWerks(itemEntity.getWerks());
		     result.setWhNumber(itemEntity.getWhNumber());
		     result.setUnit(itemEntity.getUnit());
		     result.setStockSource(existedEntity.getStockSource());
		     //数量-分为批量质检和单批质检
		     if(itemEntity.getCheckedQty() == null){
		    	 result.setResultQty(itemEntity.getInspectionQty());
		     }else{
		    	 result.setResultQty(itemEntity.getCheckedQty());
		     }
		     result.setLabelNo(record.getLabelNo());
		     result.setQcResultCode(itemEntity.getQcResultCode());
		     result.setQcResultText(itemEntity.getQcResultText());
		     result.setQcResult(itemEntity.getReturnreason());
		     result.setReturnReasonType(itemEntity.getReturnreasontype());
		     result.setQcRecordType(QcRecordType.INIT.getCode());
		     //设置检验结果号与检验记录号相同  QC_RESULT_NO = QC_RECORD_NO
		     result.setQcResultNo(record.getQcRecordNo());
		     result.setQcResultItemNo(record.getQcRecordItemNo());
		     
		     result.setQcPeople(itemEntity.getQcPeople());
		     result.setQcDate(DateUtils.format(new Date(), DateUtils.DATE_TIME_PATTERN));
		     result.setMatnr(itemEntity.getMatnr());
		     result.setMaktx(itemEntity.getMaktx());
		     result.setLgort(itemEntity.getLgort());
		     result.setLifnr(itemEntity.getLifnr());
		     result.setSobkz(itemEntity.getSobkz());
		     if(itemEntity.getDestoryQty() != null && itemEntity.getDestoryQty()>0) {
		    	 result.setIqcCostCenter(itemEntity.getCostcenter());
		     }else {
		    	 result.setIqcCostCenter(null);
		     }
		     
		     result.setInspectionNo(itemEntity.getInspectionNo());
		     result.setInspectionItemNo(itemEntity.getInspectionItemNo());
		     result.setDestroyQty(itemEntity.getDestoryQty());
		     result.setCreateDate(DateUtils.format(new Date(), DateUtils.DATE_TIME_PATTERN));
		     result.setCreator(staffNumber);
		     result.setEditor(staffNumber);
		     result.setEditDate(DateUtils.format(new Date(), DateUtils.DATE_TIME_PATTERN));
		     inspectionQcResults.add(result);
		     itemNo ++;
		}
		qcRecordService.insertBatch(inpectionQcRecords);
		qcResultService.insertBatch(inspectionQcResults);
		
		/*
		 * 4）根据【质检结果配置表】配置判定为可进仓的质检数量，
		 * 更新【WMS收货单】对应行项目的可进仓数量（INABLE_QTY），
		 * 判定为可退货的质检数量，更新【WMS收货单】对应行项目的可退货数量（RETURNABLE_QTY）。
		 */
		for(WmsQcInspectionItemEntity itemEntity:batchInscpectionResults){
			//1.质检结果配置
			WmsCQcResultEntity qcResultEntity =  cQcResultService.queryQcResult(itemEntity.getWerks(), itemEntity.getQcResultCode());
			//查询WMS收货单
			WmsInReceiptEntity inReceiptEntity = inReceiptService.selectOne(
					new EntityWrapper<WmsInReceiptEntity>()
					.eq("RECEIPT_NO",itemEntity.getReceiptNo())
					.eq("RECEIPT_ITEM_NO", itemEntity.getReceiptItemNo()));
			if(qcResultEntity == null) {
				throw new IllegalArgumentException(String.format("质检结果配置不存在,质检结果代码 ( %s )", itemEntity.getWerks(),itemEntity.getQcResultCode()));
			}
			if(inReceiptEntity == null){
				throw new IllegalArgumentException("WMS收货单不存在，无法更新收货单可进仓数量和可退货数量");
			}
			
			if(com.byd.utils.StringUtils.isNotBlank(itemEntity.getCostcenter()))
				inReceiptEntity.setIqcCostCenter(itemEntity.getCostcenter());
			
			if("X".equals(qcResultEntity.getWhFlag())){//可进仓
				//批量质检-进仓数量默认为送检数量
				if(itemEntity.getCheckedQty() == null)
				   inReceiptEntity.setInableQty(itemEntity.getInspectionQty());
				//单批质检-进仓数量为质检数量
				else{
					Double preInableQty = inReceiptEntity.getInableQty()==null?0:inReceiptEntity.getInableQty();
					inReceiptEntity.setInableQty(preInableQty + itemEntity.getCheckedQty());
				}
			}
			if("X".equals(qcResultEntity.getReturnFlag())){//可退货
				if(itemEntity.getCheckedQty() == null){
				    inReceiptEntity.setReturnableQty(itemEntity.getInspectionQty());
				}
				else{
					Double preReturnQty = inReceiptEntity.getReturnableQty()==null?0:inReceiptEntity.getReturnableQty();
					//收料房可退货数量
					inReceiptEntity.setReturnableQty(preReturnQty + itemEntity.getCheckedQty());
				}
			}
			//更新破坏数量(追加)
			if(itemEntity.getDestoryQty() != null){
				if(inReceiptEntity.getDestroyQty() == null){
					inReceiptEntity.setDestroyQty((long) 0);
				}
				inReceiptEntity.setDestroyQty((itemEntity.getDestoryQty()==null?0:itemEntity.getDestoryQty().longValue())+(inReceiptEntity.getDestroyQty()==null?0:inReceiptEntity.getDestroyQty()));
			}
			inReceiptService.updateById(inReceiptEntity);
		}
    }
	
	@Autowired
	private WmsQcInspectionItemDao wmsQcInspectionItemDao;
	
	//private static final String INIT = "01";//质检类型-初判
	private static final String QC_RESULT_TYPE_REVIEW = "02";//质检类型-复检
	

	@Override
	@Transactional(rollbackFor = Exception.class)
	/**
	 * 来料质检-质检中-单批质检-保存
	 * 1.找到质检单和质检结果&更新
	 * 2.更新WMS收货单
	 * @param qcResults 属于同一个批次的质检结果
	 */
	public void saveOnBatchInspection(List<WmsQcResultEntity> qcResults,String staffNumber) {
		if(CollectionUtils.isEmpty(qcResults)){
			return ;
		}
		/*
		 * 1）更新【检验记录】表和【检验结果】表数量（QTY）和检验结果（QC_RESULT_CODE）字段值，
		 * 质检记录类型（QC_RECORD_TYPE）由“01初判”更新为“02重判”。
		 * 拆分的质检结果生成新的检验记录行项目。
		 */
		//SysUserEntity currentUser = ShiroUtils.getUserEntity();
		//删除已经存在的质检结果
		//2019-06-26 优化 只删除质检中的质检结果
		
    	String werks = qcResults.get(0).getWerks();
    	//查询工厂对应的配置
    	List<WmsCQcResultEntity> cQcResultList = cQcResultService.selectList(
    			new EntityWrapper<WmsCQcResultEntity>().like(StringUtils.isNotBlank(werks),
    					"werks", werks));
    	if (cQcResultList.size() <= 0) {
    		//没有配置指定工厂的质检配置，取通用的配置
    		cQcResultList = cQcResultService.selectList(
    				new EntityWrapper<WmsCQcResultEntity>()
    						.isNull("WERKS").or().eq("WERKS", ""));
    	}
    	List<String> qcResultCodeList = new ArrayList<>();
    	for (WmsCQcResultEntity wmsCQcResultEntity : cQcResultList) {
    		if( "01".equals(wmsCQcResultEntity.getQcStatus()) ){
    			qcResultCodeList.add(wmsCQcResultEntity.getQcResultCode());
    		}
		}
		
		qcResultService.delete(new EntityWrapper<WmsQcResultEntity>()
				.eq("INSPECTION_NO",qcResults.get(0).getInspectionNo())
				.eq("INSPECTION_ITEM_NO", qcResults.get(0).getInspectionItemNo())
				.eq("BATCH", qcResults.get(0).getBatch())
				.in("QC_RESULT_CODE", qcResultCodeList)
				);	
		
		//新增记录
		List<WmsQcRecordEntity> needAddRecords = new ArrayList<WmsQcRecordEntity>();
		int itemNo = 1;
		//单批中的质检记录号和质检结果号都应该相同
		String resultAndRecordNo = wmsCDocNoService.getDocNo(qcResults.get(0).getWerks(), WMS_DOC_TYPE_RECORD);
		for(WmsQcResultEntity result:qcResults){
			result.setEditDate(DateUtils.format(new Date(), DateUtils.DATE_TIME_PATTERN));
			result.setEditor(staffNumber);
			result.setQcRecordType(QC_RESULT_TYPE_REVIEW);
			result.setQcDate(DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
			//设置质检结果号,和质检结果行项目号
			result.setQcResultNo(resultAndRecordNo);
			result.setQcResultItemNo(String.valueOf(itemNo));
			
			WmsQcRecordEntity record = new WmsQcRecordEntity();
			record.setBatch(result.getBatch());
			record.setCreateDate(DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
			record.setCreator(staffNumber);
			record.setDestroyQty(result.getDestroyQty());
			record.setEditDate(DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
			record.setEditor(staffNumber);
			record.setInspectionItemNo(result.getInspectionItemNo());
			record.setInspectionNo(result.getInspectionNo());
			record.setIqcCostCenter(result.getIqcCostCenter());
			record.setLgort(result.getLgort());
			record.setMaktx(result.getMaktx());
			record.setMatnr(result.getMatnr());
			record.setQcDate(result.getQcDate());
			record.setQcPeople(result.getQcPeople());
			//设置质检记录号码
			record.setQcRecordNo(resultAndRecordNo);
			record.setQcRecordItemNo(String.valueOf(itemNo));
			record.setQcRecordType(QC_RESULT_TYPE_REVIEW);
			record.setQcResult(result.getQcResult());
			record.setQcResultCode(result.getQcResultCode());
			record.setQcResultText(result.getQcResultText());
			record.setRecordQty(result.getResultQty());
			record.setStockSource(result.getStockSource());
			record.setUnit(result.getUnit());
			record.setWerks(result.getWerks());
			record.setWhNumber(result.getWhNumber());
			needAddRecords.add(record);
			itemNo ++;
		}
		//更新质检结果，追加质检记录
		qcResultService.insertBatch(qcResults);
		qcRecordService.insertBatch(needAddRecords);
		
		//新增
		//更新条码状态
		qcResults.forEach(r -> {
			WmsQcInspectionItemEntity inspect = itemService.selectOne(new EntityWrapper<WmsQcInspectionItemEntity>()
					.eq("INSPECTION_NO", r.getInspectionNo())
					.eq("INSPECTION_ITEM_NO", r.getInspectionItemNo()));
			resolveLabel(inspect);
		});
		
		/*
		 * 2）根据【质检结果配置表】配置判定为可进仓的质检数量，
		 * 更新【WMS收货单】对应行项目的可进仓数量（INABLE_QTY），
		 * 判定为可退货的质检数量，更新【WMS收货单】对应行项目的可退货数量（RETURNABLE_QTY）。
		 */
		Double inableQtyTotal = 0.0,returnableQtyTotal = 0.0;
		for(WmsQcResultEntity result:qcResults){
			WmsCQcResultEntity configResultEntity =serviceUtils.getCQcResult(result.getWerks(), result.getQcResultCode());
			if(configResultEntity == null){
				throw new IllegalArgumentException("质检配置不存在");
			}
			if(configResultEntity != null && configResultEntity.getReturnFlag().equals("X")){
				returnableQtyTotal +=result.getResultQty();
			}
			if(configResultEntity != null &&  configResultEntity.getWhFlag().equals("X")){
				inableQtyTotal += result.getResultQty();
			}
		}
		List<WmsQcInspectionItemEntity> inspectionItems = wmsQcInspectionItemDao.selectList(
				new EntityWrapper<WmsQcInspectionItemEntity>()
				.eq("INSPECTION_ITEM_NO", qcResults.get(0).getInspectionItemNo())
				.eq("INSPECTION_NO", qcResults.get(0).getInspectionNo())
				.eq("BATCH", qcResults.get(0).getBatch()));
		WmsInReceiptEntity receiptEntity  = inReceiptService.selectOne(new EntityWrapper<WmsInReceiptEntity>()
				.eq("RECEIPT_NO", inspectionItems.get(0).getReceiptNo())
				.eq("RECEIPT_ITEM_NO", inspectionItems.get(0).getReceiptItemNo())
				.eq("BATCH", inspectionItems.get(0).getBatch()));
		System.out.println("可退货数量:"+receiptEntity.getReturnableQty());
		Double receiptEnityInableQty = 0.00;
		if(receiptEntity.getInableQty() !=null && receiptEntity.getInableQty()>0) {
			receiptEnityInableQty = receiptEntity.getInableQty();
		}
		inableQtyTotal += receiptEnityInableQty;
		receiptEntity.setInableQty(inableQtyTotal);
		
		Double receiptEnityReturnableQty = 0.00;
		if(receiptEntity.getReturnableQty()!=null && receiptEntity.getReturnableQty()>0) {
			receiptEnityReturnableQty = receiptEntity.getReturnableQty();
		}
		returnableQtyTotal += receiptEnityReturnableQty;
		receiptEntity.setReturnableQty(returnableQtyTotal);
		inReceiptService.updateById(receiptEntity);
	}
	
	

	/**
	 * 质检中-批量质检
	 * @param qcResults
	 */
	@Transactional(rollbackFor = Exception.class)
	public void saveOnInspection(List<WmsQcResultEntity> qcResults,String staffNumber){
		//质检结果分类，属于同一个质检单明细的放到同一个列表
		Map<String,List<WmsQcResultEntity>> map = new HashMap<String,List<WmsQcResultEntity>>();
		for(WmsQcResultEntity result:qcResults){
			String key = result.getInspectionNo()+result.getInspectionItemNo();
			if(map.get(key)==null){
				map.put(key, new ArrayList<WmsQcResultEntity>());
			}
			map.get(key).add(result);
		}
		//SysUserEntity user = ShiroUtils.getUserEntity();
		for(String key:map.keySet()){
			List<WmsQcResultEntity> results = map.get(key);
			if(results.size()>1){
				//属于单批质检
				saveOnBatchInspection(results,staffNumber);
			}else{
				//批量质检
				WmsQcResultEntity  result = results.get(0);
				//更新质检结果 & 质检记录
				WmsQcRecordEntity dbRecord = qcRecordService.selectOne(
						new EntityWrapper<WmsQcRecordEntity>()
						.eq("INSPECTION_NO",result.getInspectionNo())
						.eq("INSPECTION_ITEM_NO", result.getInspectionItemNo()));
				WmsQcResultEntity dbResult = qcResultService.selectOne(new EntityWrapper<WmsQcResultEntity>()
						.eq("INSPECTION_NO", result.getInspectionNo())
						.eq("INSPECTION_ITEM_NO", result.getInspectionItemNo()));
				dbRecord.setQcResultCode(result.getQcResultCode());
				dbRecord.setQcResultText(result.getQcResultText());
				dbRecord.setQcResult(result.getQcResult());
				dbRecord.setQcRecordType(QC_RESULT_TYPE_REVIEW);
				dbRecord.setCreateDate(DateUtils.format(new Date(), DateUtils.DATE_TIME_PATTERN));
				dbRecord.setCreator(staffNumber);
				dbRecord.setQcDate(DateUtils.format(new Date(), DateUtils.DATE_TIME_PATTERN));
				dbRecord.setQcPeople(result.getQcPeople());
				//新增质检记录
				qcRecordService.insert(dbRecord);
				
				dbResult.setQcResultCode(result.getQcResultCode());
				dbResult.setQcResultText(result.getQcResultText());
				dbResult.setQcResult(result.getQcResult());
				dbResult.setQcRecordType(QC_RESULT_TYPE_REVIEW);
				dbResult.setEditDate(DateUtils.format(new Date(), DateUtils.DATE_TIME_PATTERN));
				dbResult.setEditor(staffNumber);
				dbResult.setQcPeople(result.getQcPeople());
				//更新质检结果
				qcResultService.updateById(dbResult);
				//更新WMS收货单
				WmsQcInspectionItemEntity inspitem = itemService.selectOne(new EntityWrapper<WmsQcInspectionItemEntity>()
						.eq("INSPECTION_NO", result.getInspectionNo())
						.eq("INSPECTION_ITEM_NO", result.getInspectionItemNo()));
				WmsInReceiptEntity receipt = inReceiptService.selectOne(new EntityWrapper<WmsInReceiptEntity>()
						.eq("RECEIPT_NO", inspitem.getReceiptNo())
						.eq("RECEIPT_ITEM_NO", inspitem.getReceiptItemNo())
						.eq("MATNR", inspitem.getMatnr()));

				 //查询质检结果配置
				 WmsCQcResultEntity qcresult = serviceUtils.getCQcResult(result.getWerks(),result.getQcResultCode());
				 if(qcresult == null){
				     throw new IllegalArgumentException("质检结果配置不存在");
				 }
				
				if(qcresult.getReturnFlag().equals("X")){
					receipt.setReturnableQty(result.getResultQty());
				}
				if(qcresult.getWhFlag().equals("X")){
					receipt.setInableQty(result.getResultQty());
				}
				inReceiptService.updateById(receipt);
			}
		}
	}
	
	
	/**
	 * 质检改判保存
	 */
	@Transactional(rollbackFor = Exception.class)
	public void reJudgeSave(List<WmsQcResultEntity> results,String staffNumber) {
        //SysUserEntity currentUser = ShiroUtils.getUserEntity();
		if (CollectionUtils.isEmpty(results)) {
			throw new IllegalArgumentException();
		}
		String inspectionNo = results.get(0).getInspectionNo();
		String inspectionItemNo = results.get(0).getInspectionItemNo();
		String werks =  results.get(0).getWerks();
		String whNumber = results.get(0).getWhNumber();
		
		//更新：2019-04-22 条码
		//查询是否开启了条码管理
		boolean barcodeFlag =  serviceUtils.isBarcodeFlag(werks, whNumber);
		
		/*
		 * 1）更新【检验记录】表和【检验结果】表质检记录类型（QC_RECORD_TYPE）由“01初判“更新为”02重判“，
		 * 同时更新数量（QTY）、检验结果（QC_RESULT_CODE）等字段值，拆分的质检结果生成新的检验记录行项目。
		 */
		// 1.删除存在的质检结果
		String l = "";
		//如果是PDA单箱复检的数据，，这时候只要更新PDA传过来的标签对应的质检记录， 整批复检需要更新送检单所有的质检记录。
		if(results.get(0).getPdaLabelNo() != null && results.get(0).getPdaLabelNo().split(",").length ==1)
		{	
			l = results.get(0).getPdaLabelNo();
		}
		qcResultService.delete(new EntityWrapper<WmsQcResultEntity>()
				.eq("INSPECTION_NO", inspectionNo)
				.eq("INSPECTION_ITEM_NO", inspectionItemNo)
				.like("LABEL_NO", l));
		// 2.新增质检结果,更新相关字段
		int qcResultItemNoSequnce = 1;
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("WERKS", results.get(0).getWerks());
		params.put("WMS_DOC_TYPE", WMS_DOC_TYPE_RESULT);
		String resultAndRecordNo = (String)wmsCDocNoService.getDocNo(params).get("docno");
		for(WmsQcResultEntity result:results){
			result.setQcResultNo(resultAndRecordNo);
			result.setQcResultItemNo(String.valueOf(qcResultItemNoSequnce));
			result.setQcRecordType(QC_RESULT_TYPE_REVIEW);//设置为重判
			result.setEditDate(DateUtils.format(new Date(), DateUtils.DATE_TIME_PATTERN));
			result.setEditor(staffNumber);
			
			
			if(barcodeFlag && StringUtils.isNotBlank(result.getPdaLabelNo())) {
				/*
				 * PDA端逻辑
				 * 如果开启了条码管理
				 * 质检结果为合格的，条码状态更新为03
				 * 如果质检为不合格的，条码状态更新为04
				 */
				WmsCQcResultEntity  c = serviceUtils.getCQcResult(werks, result.getQcResultCode());
				String labelStatus = "0".equals(c.getWhFlag()) || "X".equals(c.getReturnFlag()) ? "04" : "03";
				
				String[] lableArray  = result.getPdaLabelNo().split(",");
				for(String label : lableArray) {
					headDAO.updateWmsCoreLabelStatus(labelStatus, label);
				}
				
				/*
				 * 根据【标签表-- WMS_CORE_LABEL】标签号关联找到收货单号+收货单行项目，
				 * 再通过收货单号+收货单行项目关联【送检单明细-- WMS_QC_INSPECTION_ITEM】
				 * 表找到送检单+送检单行项目，再通过送检单+送检单行项目关联新生成的【检验记录-- WMS_QC_RECORD】
				 * 和【检验结果-- WMS_QC_RESULT】表，
				 * 把标签号信息保存到检验记录和检验结果表标签号字段（可能有多个值）。 
				 */
				 //保持原始的标签数据，nothing need TO DO
			}
			qcResultItemNoSequnce ++ ;
		}
		//新增质检结果
		qcResultService.insertBatch(results);
		// 3.追加质检记录
		qcRecordService.insertBatch(transferResultToRecord(results,resultAndRecordNo,staffNumber));
		/*
		 * 2）根据【质检结果配置表】配置判定为可进仓的质检数量，更新【WMS收货单】对应行项目的可进仓数量（INABLE_QTY），
		 * 判定为可退货的质检数量，更新【WMS收货单】对应行项目的可退货数量（RETURNABLE_QTY）。
		 */
		//1.统计可进仓总数，可退货总数
		Double totalInableQty = 0.0,totalRetunableQty = 0.0;
		for(WmsQcResultEntity result:results){
			if("X".equals(serviceUtils.getCQcResult(result.getWerks(),result.getQcResultCode()).getReturnFlag())){
				totalRetunableQty +=result.getResultQty();
			}
			if("X".equals(serviceUtils.getCQcResult(result.getWerks(),result.getQcResultCode()).getWhFlag())){
				totalInableQty += result.getResultQty();
			}
		}
		//2.查询送检单明细 再 查询 收货单
		WmsQcInspectionItemEntity inspectionItem = itemService.selectOne(new EntityWrapper<WmsQcInspectionItemEntity>().eq("INSPECTION_NO", inspectionNo).eq("INSPECTION_ITEM_NO", inspectionItemNo));
		if(inspectionItem == null ){
			throw new IllegalArgumentException("送检单明细不存在");
		}
		WmsInReceiptEntity receipt = inReceiptService.selectOne(new EntityWrapper<WmsInReceiptEntity>()
				.eq("RECEIPT_NO", inspectionItem.getReceiptNo())
				.eq("RECEIPT_ITEM_NO", inspectionItem.getReceiptItemNo())
				.eq("MATNR", inspectionItem.getMatnr()));
		if(receipt == null ){
			throw new IllegalArgumentException("收货单不存在");
		}
		//3.更新收货单
		receipt.setInableQty(totalInableQty);
		receipt.setReturnableQty(totalRetunableQty);
		inReceiptService.updateById(receipt);
	}
	
	/**
	 * 把质检结果转换成质检记录
	 * @param results：质检结果  no：单号
	 * @return
	 */
	private List<WmsQcRecordEntity> transferResultToRecord(List<WmsQcResultEntity> results,String no,String staffNumber){
		//SysUserEntity currentUser = ShiroUtils.getUserEntity();
		List<WmsQcRecordEntity> records = new ArrayList<WmsQcRecordEntity>();
		int recordItemSequnce = 1;
		for(WmsQcResultEntity result:results){
			WmsQcRecordEntity record = new WmsQcRecordEntity();
			record.setBatch(result.getBatch());
			record.setCreateDate(DateUtils.format(new Date(), DateUtils.DATE_TIME_PATTERN));
			record.setCreator(staffNumber);
			record.setEditDate(DateUtils.format(new Date(), DateUtils.DATE_TIME_PATTERN));
			record.setEditor(staffNumber);
			record.setDestroyQty(result.getDestroyQty());
			record.setInspectionItemNo(result.getInspectionItemNo());
			record.setInspectionNo(result.getInspectionNo());
			record.setIqcCostCenter(result.getIqcCostCenter());
			record.setLgort(result.getLgort());
			record.setMaktx(result.getMaktx());
			record.setMatnr(result.getMatnr());
			record.setQcDate(result.getQcDate());
			record.setQcPeople(result.getQcPeople());
			record.setQcRecordType(result.getQcRecordType());
			record.setQcResult(result.getQcResult());
			record.setQcResultCode(result.getQcResultCode());
			record.setQcResultText(result.getQcResultText());
			record.setRecordQty(result.getResultQty());
			record.setStockSource(result.getStockSource());
			record.setUnit(result.getUnit());
			record.setWerks(result.getWerks());
			record.setWhNumber(result.getWhNumber());
			record.setQcRecordNo(no);
			record.setQcRecordItemNo(String.valueOf(recordItemSequnce));recordItemSequnce ++;
			record.setLabelNo(result.getLabelNo());
			record.setLifnr(result.getLifnr());
			record.setSobkz(result.getSobkz());
			records.add(record);
		}
		return records;
	}
	
	
	

	@Override
	@Transactional(rollbackFor = Exception.class)
	public String addStockRejudgeInspect(List<Map<String, Object>> stocks,String staffNumber) {
		//SysUserEntity sysUser = ShiroUtils.getUserEntity();
		
		WmsQcInspectionHeadEntity head = new WmsQcInspectionHeadEntity();
		WmsQcInspectionItemEntity item = new WmsQcInspectionItemEntity();
		
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("WERKS", stocks.get(0).get("WERKS"));
		params.put("WMS_DOC_TYPE", WmsDocTypeEnum.QC_INSPECT.getCode());
	    String docNo = (String)	wmsCDocNoService.getDocNo(params).get("docno");
	    //创建送检单抬头
	    head.setInspectionNo(docNo);
	    head.setInspectionStatus("00");//创建
	    head.setInspectionType("02");//类型为库存复检
	    head.setWerks(stocks.get(0).get("WERKS").toString());
	    head.setWhNumber(stocks.get(0).get("WH_NUMBER").toString());
	    head.setIsAuto("X");
	    head.setMemo(stocks.get(0).get("MEMO").toString());
	    head.setCreator(staffNumber);
	    head.setCreateDate(DateUtils.format(new Date(), DateUtils.DATE_TIME_PATTERN));
	    head.setEditor(staffNumber);
	    head.setEditDate(DateUtils.format(new Date(), DateUtils.DATE_TIME_PATTERN));
	    
	    int itemNo =1;
	    for(Map<String, Object> stock:stocks){
	    	 //创建送检单项
		    item.setInspectionNo(docNo);
		    item.setInspectionItemNo(String.valueOf(itemNo));itemNo++;
		
		    item.setInspectionItemStatus(InspectionItemStatus.INIT.getCode());
		    item.setStockSource("02");//库存来源，库房
		    item.setWerks(stock.get("WERKS").toString());
		    item.setWhNumber(stock.get("WH_NUMBER").toString());
		    item.setLgort(stock.get("LGORT").toString());
		    item.setSobkz(stock.get("SOBKZ").toString());
		    item.setBinCode(stock.get("BIN_CODE").toString());
		    item.setMaktx(stock.get("MAKTX").toString());
		    item.setMatnr(stock.get("MATNR").toString());
		    item.setLiktx(stock.get("LIKTX").toString());
		    item.setLifnr(stock.get("LIFNR").toString());
		    item.setInspectionQty(Double.parseDouble(stock.get("INSPECT_QTY").toString()));
		    item.setBatch(stock.get("BATCH").toString());
		    item.setAfnam(staffNumber);
		    item.setCreator(staffNumber);
		    item.setCreateDate(DateUtils.format(new Date(), DateUtils.DATE_TIME_PATTERN));
		    item.setEditor(staffNumber);
		    item.setEditDate(DateUtils.format(new Date(), DateUtils.DATE_TIME_PATTERN));
		    item.setUnit((String)stock.get("MEINS"));
		    itemService.insert(item);
	    }
	   
	    headService.insert(head);
	    return docNo;
	}
	
	
	/**
	 * 启用条码管理：通过【送检单明细-- WMS_QC_INSPECTION_ITEM】表送检单号关联找到收货单号，
	 * 再通过收货单号关联【标签表-- WMS_CORE_LABEL】找到收货单对应的所有标签号，把对应的标签号信息存到
	 * 【检验记录-- WMS_QC_RECORD】和【检验结果-- WMS_QC_RESULT】标签号字段（可能有多个值，如0001，0002）
	 * ，同时根据质检结果更新【标签表-- WMS_CORE_LABEL】收货单对应的所有标签标签状态（质检结果为【质检结果配置表-- WMS_C_QC_RESULT】
	 * 中配置为可进仓的，标签状态更新为03待进仓，质检结果为【质检结果配置表-- WMS_C_QC_RESULT】中配置为可退货的，标签状态更新为04待退货），
	 * 质检结果为【质检结果配置表-- WMS_C_QC_RESULT】质检中状态时，标签状态不变，仍为01待质检）。
	 * @param itemEntity
	 */
	void resolveLabel(WmsQcInspectionItemEntity itemEntity) {
		  //工厂开启了标签管理
		if (serviceUtils.isBarcodeFlag(itemEntity.getWerks(), itemEntity.getWhNumber())) {
			// 没有设置标签字段，则关联的所有标签
			if (StringUtils.isBlank(itemEntity.getLabelNo())) {
				// 查询送检单号 + 行项目关联的标签号
				String label = serviceUtils.getInspectionItemLabels(itemEntity.getReceiptNo(),itemEntity.getReceiptItemNo());
				itemEntity.setLabelNo(label);
			}
			WmsCQcResultEntity result = serviceUtils.getCQcResult(itemEntity.getWerks(),itemEntity.getQcResultCode());
			String labelStatus = "03";// 可以进仓 --> 03待进仓(已质检)
			if (StringUtils.isNotBlank(result.getWhFlag()) && "0".equals(result.getWhFlag())) {
				labelStatus = "04";// 不能进仓 --> 04待退货
			}
			// 标签号不为空
			// 不属于质检中状态
			// 更新标签的状态
			if (StringUtils.isNotBlank(itemEntity.getLabelNo()) && !"01".equals(result.getQcStatus())) {
				String[] labelList = itemEntity.getLabelNo().split(",");
				for (String labelNo : labelList) {
					headDAO.updateWmsCoreLabelStatus(labelStatus, labelNo);
				}
			}
		}
	}
}

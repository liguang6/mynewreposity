package com.byd.wms.business.modules.qc.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.byd.utils.PageUtils;
import com.byd.utils.Query;
import com.byd.utils.StringUtils;
import com.byd.wms.business.modules.common.service.CommonService;
import com.byd.wms.business.modules.config.entity.WmsCQcMatSampleEntity;
import com.byd.wms.business.modules.config.entity.WmsCQcResultEntity;
import com.byd.wms.business.modules.config.service.WmsCQcMatSampleService;
import com.byd.wms.business.modules.qc.dao.WmsQcInspectionItemDao;
import com.byd.wms.business.modules.qc.entity.WmsQcInspectionItemEntity;
import com.byd.wms.business.modules.qc.enums.QcStatusEnum;
import com.byd.wms.business.modules.qc.service.WmsQcInspectionItemService;

@Service("wmsQcInspectionItemService")
public class InspectionItemServiceImpl extends
		ServiceImpl<WmsQcInspectionItemDao, WmsQcInspectionItemEntity>
		implements WmsQcInspectionItemService {

	@Autowired
	private WmsCQcMatSampleService matSampleService;
	
	@Autowired
	private WmsQcInspectionItemDao dao;
	@Autowired
	private ServiceUtils baseQcService;
	@Autowired
	private CommonService commonService;
	
	private void setInspectionQtyAndUnpackingQty(Page<WmsQcInspectionItemEntity> page){
		// 计算需质检数量和需开箱数量
				for (WmsQcInspectionItemEntity entity : page.getRecords()) {
					WmsCQcMatSampleEntity matSampleConfig = matSampleService
							.selectOne(new EntityWrapper<WmsCQcMatSampleEntity>()
									.eq("MATNR", entity.getMatnr())
									.eq("WERKS", entity.getWerks()));
					// 配置为空则不需计算
					if (matSampleConfig == null) {
						continue;
					}

					if (matSampleConfig.getSampling() != null
							&& matSampleConfig.getMinSample() != null) {
						//配置了质检参数
						Double sampleInspectionCount = entity.getInspectionQty()
								* matSampleConfig.getSampling() / 100;
						sampleInspectionCount = Math.ceil(sampleInspectionCount);//向上取整
						entity.setRequiredInspectionQtyCount(sampleInspectionCount < matSampleConfig
								.getMinSample() ? matSampleConfig.getMinSample()
								.doubleValue() : sampleInspectionCount);
					}

					if (matSampleConfig.getUnpacking() != null
							&& matSampleConfig.getMinUnpacking() != null && entity.getBoxCount() != null) {
						//配置了开箱参数
						Long sampleBoxCount = entity.getBoxCount()
								* matSampleConfig.getUnpacking() / 100;
						entity.setRequiredBoxCount(sampleBoxCount < matSampleConfig
								.getMinUnpacking() ? matSampleConfig.getMinUnpacking()
								: sampleBoxCount);
					}
				}
	}
	
	private Double parseToDouble(Object o) {
		return Double.parseDouble(o == null ? "0" : o.toString());
	}
	
	private Long parseToLong(Object o) {
		return Long.parseLong(o == null ? "0" : o.toString());
	}
	
	@Cacheable
	private WmsCQcMatSampleEntity getMatSampleConfig(String matnr,String werks) {
		WmsCQcMatSampleEntity matSampleConfig =
				         matSampleService
				        .selectOne(new EntityWrapper<WmsCQcMatSampleEntity>()
						.eq("MATNR", matnr)
						.eq("WERKS", werks));
		return matSampleConfig;
	}
	
	private void setInspectionQtyAndUnpackingQty(List<Map<String,Object>> page) {
		for (Map<String,Object> entity : page) {
			WmsCQcMatSampleEntity matSampleConfig = getMatSampleConfig( entity.get("MATNR").toString(),entity.get("WERKS").toString());
			// 配置为空则不需计算
			if (matSampleConfig == null) {
				continue;
			}

			if (matSampleConfig.getSampling() != null
					&& matSampleConfig.getMinSample() != null) {
				//配置了质检参数
				Double sampleInspectionCount = parseToDouble(entity.get("INSPECTION_QTY"))
						* matSampleConfig.getSampling() / 100;
				sampleInspectionCount = Math.ceil(sampleInspectionCount);//向上取整
				entity.put("REQUIRED_INSPECTION_QTY_COUNT", sampleInspectionCount < matSampleConfig
						.getMinSample() ? matSampleConfig.getMinSample()
						.doubleValue() : sampleInspectionCount);
			}

			if (matSampleConfig.getUnpacking() != null
					&& matSampleConfig.getMinUnpacking() != null && entity.get("BOX_COUNT") != null) {
				//配置了开箱参数
				Long sampleBoxCount =parseToLong(entity.get("BOX_COUNT"))
						* matSampleConfig.getUnpacking() / 100;
				entity.put("REQUIRED_BOX_COUNT", sampleBoxCount < matSampleConfig
						.getMinUnpacking() ? matSampleConfig.getMinUnpacking()
						: sampleBoxCount);
			}
		}
	}
	
	/**
	 * 未质检和质检中列表查询
	 */
	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		String FULL_NAME = (String) params.get("FULL_NAME");//用户名
		
		Page<WmsQcInspectionItemEntity> page = new Query<WmsQcInspectionItemEntity>(params).getPage();
		// 根据质检状态选择不同查询sql
		String resultType = String.valueOf(params.get("inspectionItemStatus") == null ? "": params.get("inspectionItemStatus"));
		if (StringUtils.isEmpty(resultType)) {
			throw new IllegalArgumentException("参数错误，质检状态不能为空");
		}
		List<WmsQcInspectionItemEntity> records = null;
		if (resultType.equals(QcStatusEnum.WAIT_INSPECT.getCode())) {
			//未质检
			records = dao.selectInspectionItemList(page, params);
			
			//设置标签
			for(WmsQcInspectionItemEntity e: records) {
				Map<String,Object> map = new HashMap<String, Object>();
				map.put("WERKS",e.getWerks());
				map.put("WH_NUMBER", e.getWerks());
				map.put("RECEIPT_ITEM_NO", e.getReceiptItemNo());
				map.put("RECEIPT_NO", e.getReceiptNo());
				map.put("MATNR", e.getMatnr());
				map.put("BATCH", e.getBatch());
				List<Map<String,Object>> labelList = dao.queryLabelInfo(map);
				if(labelList == null || labelList.size() == 0)
					continue;
				//转换成字符数组的显示
				List<String> labelArr = new ArrayList<String>();
				labelList.forEach(m-> {
					if(m.get("LABEL_NO") != null && StringUtils.isNotBlank(String.valueOf(m.get("LABEL_NO"))))
						labelArr.add(String.valueOf(m.get("LABEL_NO")));
				});
				if(labelArr.size() == 0) continue;
				String labelStr =  "";
				for(String s:labelArr)
					labelStr += s + ",";
				labelStr = labelStr.substring(0, labelStr.length() - 1);//去掉最后面的逗号
				e.setLabelNo(labelStr);
			}
		} else if (resultType.equals(QcStatusEnum.ON_INSPECT.getCode())) {
			//质检中
			records = dao.selectInspectionList2(page, params);
			String pageNo = params.get("pageNo")==null?null:params.get("pageNo").toString();
			//2019-06-26 批量质检查询，结果根据送检单号+行项目号汇总
			if(pageNo !=null) {
				//根据送检单号+行项目号汇总
				Map<String,WmsQcInspectionItemEntity> map = new HashMap<String,WmsQcInspectionItemEntity>();
				for(WmsQcInspectionItemEntity i : records) {
					String str = i.getInspectionNo()+"#*"+i.getInspectionItemNo();
					if(map.get(str) == null) {
						i.setInspectionQty(i.getCheckedQty());  //2019-06-26根据送检单号+行项目号汇总 质检数量 优化为 质检结果状态为质检中的质检数量之和
						map.put(str, i);
					}else {
						map.get(str).setInspectionQty(map.get(str).getInspectionQty()+i.getCheckedQty());
					}
				}
				records = new ArrayList<WmsQcInspectionItemEntity>();
				for(String k : map.keySet()) {
					records.add(map.get(k));
				}
			}

		}
		
		//设置质检员
		records.forEach(r -> r.setQcPeople(FULL_NAME));
		page.setRecords(records);
		setInspectionQtyAndUnpackingQty(page);
		return new PageUtils(page);
	}
	


	@Override
	public PageUtils queryHasInspectedListWithPage(Map<String, Object> params) {
		Page<Map<String,Object>> page = new Query<Map<String,Object>>(params).getPage();
		List<Map<String,Object>> records =  dao.selectHasInspectedItem(page, params);
		page.setRecords(records);
		return new PageUtils(page);
	}



	@Override
	public PageUtils queryStockReJudgeItemsWithPage(Map<String, Object> params) {
		Page<Map<String,Object>> page = new Query<Map<String,Object>>(params).getPage();
		List<Map<String,Object>> records = dao.selectStockReJudgeItems(params);
		page.setRecords(records);
		return new PageUtils(page);
	}



	@Override
	public PageUtils selectStockReJudgeNotInspected(Map<String, Object> params) {
		Page<Map<String,Object>> page = new Query<Map<String,Object>>(params).getPage();
		List<Map<String,Object>> records = dao.selectStockReJudgeNotInspected(params);
		//计算需质检数量
		for(Map<String,Object> record:records){
			WmsCQcMatSampleEntity cmat = matSampleService.selectOne(new EntityWrapper<WmsCQcMatSampleEntity>()
					.eq("WERKS", record.get("WERKS"))
					.eq("MATNR", record.get("MATNR")));
			if(cmat == null){
				continue;
			}
			
			Long minSample = cmat.getMinSample();
			Long sampling = cmat.getSampling();
			Long inspectionQty = Long.parseLong(record.get("INSPECTION_QTY").toString()) ;
			if(inspectionQty > minSample * (sampling / 100)){
				record.put("NEED_INSPECTION_QTY", inspectionQty);
			}else{
				record.put("NEED_INSPECTION_QTY", minSample * (sampling/100));
			}
		}
		//计算需质检数量，需开箱数量
		setInspectionQtyAndUnpackingQty(records);
		page.setRecords(records);
		return new PageUtils(page);
	}



	@Override
	public List<Map<String, Object>> selectStockRejudgeOnInspect(Map<String, Object> params) {
		List<Map<String, Object>> records = dao.selectStockRejudgeOnInspect(params);
		List<Map<String, Object>> onInspectRecord = new ArrayList<Map<String, Object>>();
		Set<String> INSPECTION_NO_STR_LIST = new HashSet<>();
		// 过滤掉不是质检中的
		for (Map<String, Object> record : records) {
			WmsCQcResultEntity config = baseQcService.getCQcResult(
					(String) record.get("WERKS"),
					(String) record.get("QC_RESULT_CODE"));
			if (config == null) {
				throw new IllegalArgumentException();
			}
			if (config.getQcStatus().equals(QcStatusEnum.ON_INSPECT.getCode())) {
				onInspectRecord.add(record);
				
				String INSPECTION_NO_STR = record.get("INSPECTION_NO").toString()+"#*"+record.get("INSPECTION_ITEM_NO");
				INSPECTION_NO_STR_LIST.add(INSPECTION_NO_STR);
			}
		}
		
		if (params.get("getAll") == null) {// getAll为空时，汇总
			List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
			for (String INSPECTION_NO_STR : INSPECTION_NO_STR_LIST) {
				String[] INSPECTION_NO_STR_ARR = INSPECTION_NO_STR.split("\\#\\*");
				String INSPECTION_NO = INSPECTION_NO_STR_ARR[0];
				String INSPECTION_ITEM_NO = INSPECTION_NO_STR_ARR[1];
				Map<String, Object> result = null;
				for (Map<String, Object> map : onInspectRecord) {
					if( INSPECTION_NO.equals(map.get("INSPECTION_NO").toString()) && 
							INSPECTION_ITEM_NO.equals(map.get("INSPECTION_ITEM_NO").toString()) ){
						if(result == null) {
							result = map;
						}else {
							result.put("RESULT_QTY", Double.valueOf( result.get("RESULT_QTY").toString() )+ Double.valueOf( map.get("RESULT_QTY").toString()));
						}
						
					}
				}
				
				resultList.add(result);
			}
			return resultList;
		}else {
			return onInspectRecord;
		}
		
		
	}



	@Override
	public List<WmsQcInspectionItemEntity> selectInspectionItems(
			Map<String, Object> params) {
		String inspectionNo = params.get("inspectionNo") == null ? null : (String) params.get("inspectionNo");
		List<WmsQcInspectionItemEntity> list =  this.selectList(new EntityWrapper<WmsQcInspectionItemEntity>()
				.eq("INSPECTION_NO", inspectionNo));
		return list;
	}
	
	/**
	 * 根据物料管理模式查询用户未质检任务信息
	 */
	@Override
	public List<Map<String, Object>>  getInspectionItemTask(Map<String, Object> params){
		List<Map<String, Object>> userInspectionItemTask = new ArrayList<Map<String, Object>>();
    	//1、根据质检员查询授权码、物料管理方式 清单
		List<Map<String, Object>> matManagerAuthCodeList = commonService.getMatManagerAuthCodeList(params);
		StringBuffer userTypeAuthCode = new StringBuffer();
		StringBuffer lgortTypeAuthCode = new StringBuffer();
		StringBuffer WERKS = new StringBuffer();
		matManagerAuthCodeList.forEach(map -> {
			map = (Map<String, Object>)map;
			String MAT_MANAGER_TYPE = map.get("MAT_MANAGER_TYPE")==null?"":map.get("MAT_MANAGER_TYPE").toString();
			if("00".equals(MAT_MANAGER_TYPE)) {
				WERKS.append( map.get("WERKS")==null?"":map.get("WERKS").toString() );
				userTypeAuthCode.append(map.get("AUTHORIZE_CODE")==null?"":map.get("AUTHORIZE_CODE").toString()+",");
			}
			if("20".equals(MAT_MANAGER_TYPE)) {
				WERKS.append( map.get("WERKS")==null?"":map.get("WERKS").toString() );
				lgortTypeAuthCode.append(map.get("AUTHORIZE_CODE")==null?"":map.get("AUTHORIZE_CODE").toString()+",");
			}
		});
		params.put("WERKS", WERKS.toString());
    	//2、物料管理方式-00（人） 查询人员管理的物料清单
		if(userTypeAuthCode.toString().length()>0) {
			params.put("AUTHORIZE_CODE", userTypeAuthCode.toString());
			List<Map<String, Object>> matList = commonService.getMatByAuthCode(params);
			
			//2.1 根据物料清单查询状态为未质检的送检单行项目清单
			List<Map<String, Object>> list = dao.queryInspectionItemByMatList(matList);
			
			userInspectionItemTask.addAll(list);
		}
    	
		if(lgortTypeAuthCode.toString().length()>0) {
			//3、物料管理方式-20（库存地点） 根据库存地点查询状态为未质检的送检单行项目清单
			params.put("LGORT", lgortTypeAuthCode.toString());
			List<Map<String, Object>> list = dao.queryInspectionItemByLgort(params);
			userInspectionItemTask.addAll(list);
		}
    	
		
		return userInspectionItemTask;
	}
	
}

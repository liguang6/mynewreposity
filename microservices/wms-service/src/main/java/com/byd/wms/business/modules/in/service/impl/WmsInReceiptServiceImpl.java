package com.byd.wms.business.modules.in.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.byd.utils.DateUtils;
import com.byd.utils.PageUtils;
import com.byd.utils.Query;
import com.byd.utils.R;
import com.byd.wms.business.modules.common.dao.CommonDao;
import com.byd.wms.business.modules.common.service.CommonService;
import com.byd.wms.business.modules.common.service.WmsCDocNoService;
import com.byd.wms.business.modules.common.service.WmsCoreMatBatchService;
import com.byd.wms.business.modules.in.dao.WmsInReceiptDao;
import com.byd.wms.business.modules.in.entity.WmsInReceiptEntity;
import com.byd.wms.business.modules.in.service.SCMDeliveryService;
import com.byd.wms.business.modules.in.service.WmsInReceiptService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


@Service("wmsInReceiptService")
public class WmsInReceiptServiceImpl extends ServiceImpl<WmsInReceiptDao, WmsInReceiptEntity> implements WmsInReceiptService {
	@Autowired
	private WmsInReceiptDao wmsInReceiptDao;
	@Autowired
	private CommonDao commonDao;
	@Autowired
	private WmsCoreMatBatchService wmsMatBatchService;
	@Autowired
	private WmsCDocNoService wmsCDocNoService;
	@Autowired
	private CommonService commonService;
	@Autowired
    private SCMDeliveryService scmDeliveryService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        Page<WmsInReceiptEntity> page = this.selectPage(
                new Query<WmsInReceiptEntity>(params).getPage(),
                new EntityWrapper<WmsInReceiptEntity>()
        );
        return new PageUtils(page);
    }

    @Override
    public List<Map<String, Object>> getLabelInfo(Map<String, Object> params){
    	return wmsInReceiptDao.getLabelInfo(params);
    }
    @Override
    public List<String> getMatStock(List<Map<String, Object>> matList){
    	return wmsInReceiptDao.getMatStock(matList);
    }

    /**
     * 根据工厂代码、系统时间查询紧急物料列表
     */
	@Override
	public List<String> getUrgentMatList(Map<String, Object> params) {
		params.put("CUR_DATE", DateUtils.format(new Date(), "yyyy-MM-dd"));
		return wmsInReceiptDao.getUrgentMatList(params);
	}
	/**
	 * 根据工厂代码查询危化品物料列表
	 */
	@Override
	public List<Map<String, Object>> getDangerMatList(Map<String, Object> params) {
		return wmsInReceiptDao.getDangerMatList(params);
	}
	/**
	 * 核对SCM送货单对应的采购订单行项目数据是否在WMS采购订单数据表中存在
	 */
	@Override
	public List<String> getPoItemListByPo(List<String> poitemList) {
		List<Map<String,Object>> condList=new ArrayList<Map<String,Object>>();

		for(String v:poitemList) {
			Map<String,Object> pomap=new HashMap<String,Object>();
			String EBELN=v.split("\\#\\*")[0];
			String EBELP=v.split("\\#\\*")[1];
			pomap.put("EBELN", EBELN);
			pomap.put("EBELP", EBELP);
			condList.add(pomap);
		}

		return wmsInReceiptDao.getPoItemListByPo(condList);
	}

	/**
	 * 根据采购订单号获取采购订单凭证类型信息
	 */
	@Override
	public List<String> getPoTypeListByPo(List<String> poList) {
		return wmsInReceiptDao.getPoTypeListByPo(poList);
	}

	/**
	 * SCM送货单
	 * 根据封装好的查询条件（送货单号##送货单行项目号）查询每条行项目已收货数量
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> getReceiptCount(List<String> asnList) {
		Map<String,Object> condMap=new HashMap<String,Object>();
		List<String> item_list=new ArrayList<String>();//送货单行项目列表
		String ASNNO=asnList.get(0).split("\\#\\*")[0];
		for(String v:asnList) {
			String item_no=v.split("\\#\\*")[1];
			item_list.add(item_no);
		}
		condMap.put("ASNNO", ASNNO);
		condMap.put("item_list", item_list);

		String data_json=wmsInReceiptDao.getReceiptCount(condMap);

		return JSONObject.parseObject(data_json, Map.class);
	}
	/**
	 * SAP 采购订单
	 * 根据封装好的查询条件（SAP采购订单号#*采购订单行项目号）查询每条行项目已收货数量
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> getReceiptCount(Map<String,Object> params) {
		Map<String,Object> condMap=new HashMap<String,Object>();
		List<String> item_list=new ArrayList<String>();//送货单行项目列表
		List<String> asnList=(List<String>) params.get("asnList");
		String BILL_TYPE=params.get("BILL_TYPE").toString();

		//String PO_NO=asnList.get(0).split("\\#\\*")[0];
		for(String v:asnList) {
			String item_no=v.split("\\#\\*")[1];
			item_list.add(item_no);
		}
		condMap.put(BILL_TYPE, asnList.get(0).split("\\#\\*")[0]);

		condMap.put("item_list", item_list);

		String data_json=wmsInReceiptDao.getReceiptCount(condMap);

		return JSONObject.parseObject(data_json, Map.class);
	}
	/**
	 * 根据供应商代码，工厂代码获取采购和供应商简称（判断WMS_C_PLANT表是否配置了工厂启用供应商管理）
	 */
	@Override
	public Map<String, Object> getVendorInfo(Map<String, Object> params) {

		return wmsInReceiptDao.getVendorInfo(params);
	}
	/**
	 * 根据采购订单、订单行项目号从采购订单表中抓取申请人、需求跟踪号、
	 * 库存类型（采购订单为K,WMS库存类型为K,采购订单数据为空，WMS库存类型为Z）
	 */
	@Override
	public Map<String, Object> getBendrAfnam(List<String> poitemList) {
		List<Map<String,Object>> condList=new ArrayList<Map<String,Object>>();

		for(String v:poitemList) {
			Map<String,Object> pomap=new HashMap<String,Object>();
			String pono=v.split("\\#\\*")[0];
			String poitm=v.split("\\#\\*")[1];
			pomap.put("EBELN", pono);
			pomap.put("EBELP", poitm);
			condList.add(pomap);
		}
		String data_json=wmsInReceiptDao.getBendrAfnam(condList);

		return JSONObject.parseObject(data_json, Map.class);
	}

	/**
	 * 根据物料行项目从物料质检配置表读取匹配的物料列表
	 * @param params
	 * @return
	 */
	@Override
	public List<Map<String, Object>> getQCMatList(Map<String, Object> params){
		return wmsInReceiptDao.getQCMatList(params);
	}

	/**
	 * 根据工厂代码、WMS业务类型代码、系统日期查询质检配置
	 * @param params
	 * @return
	 */
	@Override
	public String getTestFlag(Map<String, Object> params) {
		return wmsInReceiptDao.getTestFlag(params);
	}
	/**
	 * 根据SAP物料凭证获取关联的标签号
	 * @param lableNoList
	 * @return
	 */
	@Override
	public List<Map<String, Object>> getLabelNoByMatDocNo(Map<String, Object> params){
		List<Map<String, Object>> rtn =  wmsInReceiptDao.getLabelNoByMatDocNo(params);

		for (Map<String, Object> map : rtn) {
			String WMS_SAP_MAT_DOC = map.get("WMS_SAP_MAT_DOC").toString();
			String[] allDocArray = WMS_SAP_MAT_DOC.split(";");
			//一个303WMS凭证行项目只能产生一个303SAP凭证
			String[] docArry = allDocArray[0].split(":");
			map.put("MATDOC_ITM", docArry[1]);

			String LABEL_NO_STR = map.get("LABEL_NO")==null?"":map.get("LABEL_NO").toString();
			List<Map<String, Object>> allLabelNoList = new ArrayList<Map<String, Object>>();
			for (String label: LABEL_NO_STR.split(",")){
				Map<String, Object> labelMap = new HashMap<String, Object>();
				labelMap.put("BARCODE", label);
				labelMap.put("LABEL_NO", label);
				if(params.get("WERKS")!=null) {
					labelMap.put("WERKS", params.get("WERKS")==null?null:params.get("WERKS").toString());
				}

				allLabelNoList.add(labelMap);
			}
			map.put("allLabelNoList_item", allLabelNoList);

		}
		return rtn;
	}
	/**
	 * 根据标签号获取标签信息
	 * @param lableNoList
	 * @return
	 */
	@Override
	public List<Map<String, Object>> getLabelInfoByLabelNo(List<Map<String, Object>> labelNoList){
		return wmsInReceiptDao.getLabelInfoByLabelNo(labelNoList);
	}

	/**
	 * SCM送货单确认收货
	 */
	@SuppressWarnings("finally")
	@Override
	@Transactional(rollbackFor=RuntimeException.class)
	public R boundIn_01(Map<String, Object> params) {
		List<Map<String,Object>> matList=(List<Map<String,Object>>) params.get("matList");//待收货物料清单
		String WERKS = params.get("WERKS").toString();
		String SAP_MOVE_TYPE="";//SAP移动类型，为空不进行SAP过账
		String WMS_MOVE_TYPE = "";
		params.put("BUSINESS_NAME", "01");//SCM送货单收料
		params.put("BUSINESS_TYPE", "01");//PO采购订单
		params.put("BUSINESS_CLASS", "01");
		/**
		 * 获取WMS移动类型、移动原因、SAP移动类型、SAP过账标识
		 */
		Map<String,Object> cdmap=new HashMap<String,Object>();
		cdmap.put("BUSINESS_NAME", "01");//SCM送货单收料
		cdmap.put("BUSINESS_TYPE", "01");//SCM送货单
		cdmap.put("BUSINESS_CLASS", "01");
		cdmap.put("WERKS", WERKS);
		cdmap.put("SOBKZ", matList.get(0).get("SOBKZ"));
		Map<String,Object> moveSyn=commonDao.getMoveAndSyn(cdmap);
		if(moveSyn==null || moveSyn.get("WMS_MOVE_TYPE")==null) {
			return R.error("收货工厂"+WERKS+"未配置SCM送货单收料业务类型！");
		}
		if(moveSyn!=null) {
			SAP_MOVE_TYPE=(String)moveSyn.get("SAP_MOVE_TYPE");
			WMS_MOVE_TYPE = (String)moveSyn.get("WMS_MOVE_TYPE");
			params.put("SYN_FLAG", moveSyn.get("SYN_FLAG"));
		}
		params.put("SAP_MOVE_TYPE", SAP_MOVE_TYPE);
		params.put("WMS_MOVE_TYPE", WMS_MOVE_TYPE);

		/**
		 * 生成WMS批次信息
		 */
		/*String setMatBatchMsg = this.setMatBatch(params, matList);
		if(StringUtils.isNotEmpty(setMatBatchMsg)) {
			return R.error(setMatBatchMsg);
		}*/

		params.put("matList", matList);

		/**
		 * 保存收货单数据
		 */
		String RECEIPT_NO=this.saveReceiptInfo(params);//收货单号写入待收货行项目清单，并更新收货单行项目质检状态

		/**
		 * 保存收料房库存:先根据工厂代码、仓库号、库位、物料号、wms批次、特殊库存类型、供应商代码
		 * 查询收料房库存信息，存在则累加库存数量，否则新增库存数据
		 */
		this.saveRhStock(params);

		List<Map<String,Object>> skList=(List<Map<String,Object>>) params.get("skList");//SCM条码清单
		//匹配收货行项目关联的条码信息
		for(Map m:matList) {
			StringBuffer LABEL_NO = new StringBuffer();
			for (Map<String, Object> map : skList) {
				if(map.get("ASNNO").toString().equals(m.get("ASNNO").toString()) && map.get("ASNITM").toString().equals(m.get("ASNITM").toString())) {
					if(map.get("SKUID")!=null) {
						LABEL_NO.append(map.get("SKUID").toString()).append(",");
					}else {
						LABEL_NO.append(map.get("LABEL_NO").toString()).append(",");
					}
				}
			}
			if(LABEL_NO.length()>0) {
				m.put("LABEL_NO", LABEL_NO.toString().substring(0, LABEL_NO.length()-1));
			}else {
				m.put("LABEL_NO", "");
			}

			m.put("SAP_MOVE_TYPE", SAP_MOVE_TYPE);
		}
		/**
		 * 保存标签数据
		 */
		this.saveCoreLabel(skList,(List<Map<String,Object>>)params.get("matList")); //关联标签和收货单号，及根据物料质检类型设置标签状态

		/**
		 * 产生WMS凭证记录
		 *
		 */
		String WMS_NO=commonService.saveWMSDoc(params,(List<Map<String,Object>>)params.get("matList"));
		params.put("WMS_NO", WMS_NO);

		/**
		 *  质检，质检结果关联标签号信息
		 */
		String INSPECTION_NO=this.doQualityCheck(params);//送检单号,设置所有matList行项目物料质检类型 00 质检 01 免检 02 无需质检

		/**
		 * SAP过账：检查配置 是否及时过账到SAP，如果配置及时过账，抛数据给SAP过账，如果过账失败，显示报错信息，操作终止
		 * 非及时过账，延迟过账到SAP，先产生和更新WMS相关的数据后，再抛数据给SAP过账
		 * SAP过账失败回滚
		 */
		String SAP_NO= commonService.doSapPost(params);

		StringBuilder msg=new StringBuilder();
		msg.append("操作成功！收货单号:");
		msg.append(RECEIPT_NO);
		if(StringUtils.isNotBlank(INSPECTION_NO)) {
			msg.append(" 送检单号:");
			msg.append(INSPECTION_NO);
		}
		msg.append(" WMS凭证:");
		msg.append(WMS_NO);
		if(StringUtils.isNotBlank(SAP_NO)) {
			msg.append(" SAP凭证:");
			msg.append(SAP_NO);
		}

		return R.ok(msg.toString()).put("lableList", skList).put("inspectionList", matList);

	}

	/**
	 * 质检:读取工厂质检配置表：
	 *  00质检：产生送检单，读取和匹配物料质检配置表的免检物料数据，产生质检结果为免检的质检结果数据和质检记录数据，对应送检单抬头状态为全部完成，
	 *  行项目状态为完成，收货单可进仓数量等于对应送检单数量。
	 *  01免检 产生送检单，读取和匹配物料质检配置表的免检物料数据，产生质检结果为免检的质检结果数据和质检记录数据，对应送检单抬头状态为全部完成，
	 *  行项目状态为完成，收货单可进仓数量等于对应送检单数量。
	 *  02 无需质检 不产生送检单，收货单可进仓数量等于收货数量
	 * @return 收货单可进仓数量
	 */
	@Transactional
	public String doQualityCheck(Map<String, Object> params) {
		String TEST_FLAG=params.get("TEST_FLAG").toString();//工厂维护质检配置
		List<Map<String, Object>> matList=(List<Map<String, Object>>) params.get("matList");
		List<Map<String,Object>> match_list=new ArrayList<Map<String,Object>>();//检验结果明细数据列表

		String INSPECTION_NO="";//送检单号
		String INSPECTION_STATUS="00";//送检单抬头状态默认为创建（00） 送检单状态  字典定义：（00创建，01部分完成，02全部完成，04关闭）
		String INSPECTION_TYPE="01";//送检单类型为来料质检（01）
		String WH_NUMBER=(String)matList.get(0).get("WH_NUMBER");//仓库号
		String INSPECTION_ITEM_STATUS="00";//送检单行项目状态默认为未质检  （00未质检，01已质检，02关闭）
		String CREATOR=(String)matList.get(0).get("CREATOR");
		String CREATE_DATE=(String)matList.get(0).get("CREATE_DATE");
		//送检单抬头数据
		Map<String,Object> INSPECTION_MAP=new HashMap<String,Object>();

		//工厂维度配置的质检状态为“质检”、免检情况
		if("00".equals(TEST_FLAG)||"01".equals(TEST_FLAG)) {
			params.put("WMS_DOC_TYPE", "03");//送检单
			Map<String,Object> doc=wmsCDocNoService.getDocNo(params);
			if(doc.get("MSG")!=null&&!"success".equals(doc.get("MSG"))) {
				throw new RuntimeException(doc.get("MSG").toString());
			}
			INSPECTION_NO=doc.get("docno").toString();//送检单号

			int i=1;
			for(Map m:matList) {
				m.put("INSPECTION_NO", INSPECTION_NO);
				m.put("INSPECTION_ITEM_NO", i++);//送检单行项目号
				m.put("INSPECTION_ITEM_STATUS", INSPECTION_ITEM_STATUS);
				m.put("STOCK_SOURCE", "01");//送检单明细库存来源为收料房
				m.put("INSPECTION_QTY", m.get("RECEIPT_QTY"));//送检数量

				//判断物料维护质检配置是否为“免检”
				if(m.get("TEST_FLAG")!=null && "01".equals(m.get("TEST_FLAG").toString())) {
					//免检
					m.put("INSPECTION_ITEM_STATUS", "01"); //送检单状态修改为，已质检
					match_list.add(m);
				}

			}
			String QC_RECORD_NO="";//检验记录号
			String QC_RESULT_NO="";//检验结果号
			if(match_list !=null && match_list.size()>0) {
				//免检物料，产生质检结果和质检记录
				doc=new HashMap<String,Object>();
				params.put("WMS_DOC_TYPE", "04");//检验记录
				doc=wmsCDocNoService.getDocNo(params);//检验记录号
				if(doc.get("MSG")!=null&&!"success".equals(doc.get("MSG"))) {
					throw new RuntimeException(doc.get("MSG").toString());
				}
				QC_RECORD_NO=doc.get("docno").toString();//检验记录号

				params.put("WMS_DOC_TYPE", "05");//检验结果
				doc=wmsCDocNoService.getDocNo(params);//检验结果
				if(doc.get("MSG")!=null&&!"success".equals(doc.get("MSG"))) {
					throw new RuntimeException(doc.get("MSG").toString());
				}
				QC_RESULT_NO=doc.get("docno").toString();//检验结果

				//根据匹配行项目生成质检记录和质检结果
				int j=1;
				for(Map<String,Object> m:match_list) {
					m.put("QC_RECORD_NO", QC_RECORD_NO);
					m.put("QC_RESULT_NO", QC_RESULT_NO);

					m.put("QC_RESULT_CODE", "11");//检验结果：免检
					m.put("QC_RESULT_TEXT", "免检");//检验结果：免检
					m.put("QC_RESULT", "免检");
					m.put("QC_STATUS", "02");//质检状态 字典定义：00未质检 01质检中 02已质检
					m.put("QC_RESULT_ITEM_NO", j);
					m.put("QC_RECORD_ITEM_NO", j);
					m.put("QC_RECORD_TYPE", "01");//质检记录类型 字典定义： 01初判 02重判
					m.put("RESULT_QTY", m.get("RECEIPT_QTY"));//质检结果数量
					m.put("RECORD_QTY", m.get("RECEIPT_QTY"));//质检结果数量
					m.put("QC_DATE", m.get("CREATE_DATE"));
					j++;
				}
				/**
				 * 保存检验记录和检验结果
				 */
				wmsInReceiptDao.insertQCResult(match_list);
				wmsInReceiptDao.insertQCRecord(match_list);

				//根据质检结果行项目更新收货单行项目的可进仓数量
				wmsInReceiptDao.updateReceiptInableQty(match_list);

				if(match_list.size()<matList.size()) {
					INSPECTION_STATUS="01";//送检单抬头状态默认为创建（00） 送检单状态  字典定义：（00创建，01部分完成，02全部完成，04关闭）
				}else {
					INSPECTION_STATUS="02";
				}
			}

			INSPECTION_MAP.put("INSPECTION_NO", INSPECTION_NO);
			INSPECTION_MAP.put("INSPECTION_STATUS", INSPECTION_STATUS);
			INSPECTION_MAP.put("INSPECTION_TYPE", INSPECTION_TYPE);
			INSPECTION_MAP.put("WMS_NO", params.get("WMS_NO"));
			INSPECTION_MAP.put("WERKS", params.get("WERKS"));
			INSPECTION_MAP.put("WH_NUMBER", WH_NUMBER);
			INSPECTION_MAP.put("CREATOR", CREATOR);
			INSPECTION_MAP.put("CREATE_DATE", CREATE_DATE);
			INSPECTION_MAP.put("IS_AUTO", "X");
			INSPECTION_MAP.put("DEL", "0");
			//保存送检单抬头
			wmsInReceiptDao.insertInspectionHead(INSPECTION_MAP);
			//保存送检单明细
			wmsInReceiptDao.insertInspectionItem(matList);

			//更新WMS凭证行项目的送检单号和行项目号
			wmsInReceiptDao.updateWMSDocItemInspection(matList);

		}else {//无需质检，更新收货单行项目的可进仓数量为收货数量
			for(Map m:matList) {
				m.put("RESULT_QTY", m.get("RECEIPT_QTY"));//质检结果数量
			}
			wmsInReceiptDao.updateReceiptInableQty(matList);
		}
		return INSPECTION_NO;

	}

	/**
	 * 生成WMS批次信息
	 */
	@Override
	public String setMatBatch(Map<String, Object> params ,List<Map<String,Object>> matList) {
		String result = "";

		List<Map<String, Object>> condMapList = new ArrayList<Map<String, Object>>();

		matList.forEach(k->{
			k=(Map)k;

			Map<String, Object> condMap=new HashMap<String,Object>();
			condMap.put("WERKS", k.get("WERKS").toString());
			condMap.put("ASNNO", k.get("ASNNO")==null?"":k.get("ASNNO").toString());
			condMap.put("PO_NO", k.get("PO_NO")==null?"":k.get("PO_NO").toString());
			condMap.put("RECEIPT_DATE", DateUtils.format(new Date(),"yyyy-MM-dd"));

			String DELIVERY_DATE=k.get("DELIVERY_DATE")==null?"":k.get("DELIVERY_DATE").toString();
			String PRODUCT_DATE=k.get("PRODUCT_DATE")==null?"":k.get("PRODUCT_DATE").toString();
			String EFFECT_DATE=k.get("EFFECT_DATE")==null?"":k.get("EFFECT_DATE").toString();
			String MATNR =k.get("MATNR")==null?"":k.get("MATNR").toString();
			String LIFNR=k.get("LIFNR")==null?"":k.get("LIFNR").toString();
			String DANGER_FLAG=k.get("DANGER_FLAG")==null?"0":k.get("DANGER_FLAG").toString();
			String LGORT=k.get("LGORT")==null?"":k.get("LGORT").toString();
			String F_BATCH=k.get("F_BATCH")==null?"":k.get("F_BATCH").toString();
			condMap.put("DELIVERY_DATE", DELIVERY_DATE);
			condMap.put("PRODUCT_DATE", PRODUCT_DATE);
			condMap.put("EFFECT_DATE", EFFECT_DATE);
			condMap.put("MATNR", MATNR);
			condMap.put("LIFNR", LIFNR);
			condMap.put("DANGER_FLAG", DANGER_FLAG);
			condMap.put("LGORT", LGORT);
			condMap.put("F_BATCH", F_BATCH);

			condMap.put("BUSINESS_NAME", params.get("BUSINESS_NAME"));

			condMapList.add(condMap);
		});

		List<Map<String,Object>> retmapList = wmsMatBatchService.getBatch(condMapList);

		for (int i=0;i<retmapList.size();i++) {
			Map<String,Object> retmap = retmapList.get(i);
			/**
			 * 获取批次出错，抛出异常
			 */
			if(retmap.get("MSG")!=null&&!"success".equals(retmap.get("MSG"))) {
				result = retmap.get("MSG").toString();
				throw new RuntimeException((String) retmap.get("MSG"));
			}
			matList.get(i).put("BATCH", retmap.get("BATCH"));
		}
		return result;
	}


	/**
	 * 保存收货单
	 * @param params
	 */
	@Transactional
	public String saveReceiptInfo(Map<String,Object> params) {
		params.put("WMS_DOC_TYPE", "02");//收货单
		String RECEIPT_NO="";//收货单号
		Map<String,Object> doc=null;
		doc=wmsCDocNoService.getDocNo(params);
		if(doc.get("MSG")!=null&&!"success".equals(doc.get("MSG"))) {
			throw new RuntimeException(doc.get("MSG").toString());
		}
		RECEIPT_NO=doc.get("docno").toString();

		List<Map<String,Object>> matList=(List<Map<String, Object>>) params.get("matList");

		int i=1;
		for(Map m:matList) {
			m.put("RECEIPT_NO", RECEIPT_NO);
			m.put("RECEIPT_DATE", m.get("CREATE_DATE"));
			m.put("RECEIPT_ITEM_NO", i);
			m.put("BUSINESS_NAME", params.get("BUSINESS_NAME"));
			m.put("BUSINESS_TYPE", params.get("BUSINESS_TYPE"));
			m.put("GR_AREA", m.get("BIN_CODE"));
			m.put("INABLE_QTY", 0);
			i++;
		}

		wmsInReceiptDao.insertReceiptInfo(matList);

		return RECEIPT_NO;
	}

	/**
	 *保存收料房库存:先根据工厂代码、仓库号、库位、物料号、wms批次、特殊库存类型、供应商代码
	 * 查询收料房库存信息，存在则累加库存数量，否则新增库存数据
	 * @param params
	 */
	@Transactional
	public void saveRhStock(Map<String, Object> params) {
		List<Map<String,Object>> matList=(List<Map<String, Object>>) params.get("matList");
		for(Map<String,Object> m:matList) {
			m.put("WERKS", m.get("WERKS")==null?"":m.get("WERKS"));
			m.put("WH_NUMBER", m.get("WH_NUMBER")==null?"":m.get("WH_NUMBER"));
			m.put("LGORT", m.get("LGORT")==null?"":m.get("LGORT"));
			m.put("MATNR", m.get("MATNR")==null?"":m.get("MATNR"));
			m.put("MAKTX", m.get("MAKTX")==null?"":m.get("MAKTX"));
			m.put("F_BATCH", m.get("F_BATCH")==null?"":m.get("F_BATCH"));
			m.put("BATCH", m.get("BATCH")==null?"":m.get("BATCH"));
			m.put("UNIT", m.get("UNIT")==null?"":m.get("UNIT"));
			m.put("RH_QTY", m.get("RH_QTY")==null?m.get("RECEIPT_QTY"):m.get("RH_QTY"));
			m.put("SOBKZ", m.get("SOBKZ")==null?"":m.get("SOBKZ"));
			m.put("LIFNR", m.get("LIFNR")==null?"":m.get("LIFNR"));
			m.put("LIKTX", m.get("LIKTX")==null?"":m.get("LIKTX"));
		}

		if(matList.size()>0) {
			wmsInReceiptDao.updateRhStock(matList);
		}
	}

	/**
	 * 保存标签数据
	 */
	@Transactional
	public void saveCoreLabel(List<Map<String, Object>> skList, List<Map<String, Object>> matList) {
		String BUSINESS_NAME=matList.get(0).get("BUSINESS_NAME").toString();
		String LABEL_STATUS = "01";//00创建，01已收料（待质检），02已收料（无需质检）03待进仓(已质检)，04待退货(已质检)，05收料房退货，06库房退货，07已进仓，08已上架，09已下架，10已出库，11已冻结，12已锁定，20关闭）
		String QC_RESULT_CODE = null;
		//从行项目中获取收货单号和收货单行项目号
		if(BUSINESS_NAME.equals("01")||BUSINESS_NAME.equals("78")) {//SCM收货单、云平台送货单
			for (Map<String, Object> sk : skList) {
				for(Map<String,Object> m:matList) {
					String TEST_FLAG = m.get("TEST_FLAG").toString();
					if(TEST_FLAG.endsWith("01")) {
						//免检
						LABEL_STATUS = "03";
						QC_RESULT_CODE = "11";
					}
					if(TEST_FLAG.endsWith("02")) {
						//无需质检
						LABEL_STATUS = "02";
					}

					String mat_item_no="";
					String sk_item_no="";
					if(BUSINESS_NAME.equals("01")) {
						mat_item_no=m.get("ASNITM").toString();
						sk_item_no=sk.get("ASNITM").toString();
					}
					if(BUSINESS_NAME.equals("78")) {
						mat_item_no=m.get("ASNITM").toString();
						sk_item_no=sk.get("ASNITM").toString();
					}

					if(m.get("WERKS").equals(sk.get("WERKS"))&&m.get("LIFNR").equals(sk.get("LIFNR"))
								&&m.get("MATNR").equals(sk.get("MATNR"))&&mat_item_no.equals(sk_item_no)
							) {
								String LABEL_NO="";
								if(sk.get("SKUID")!=null) {
									LABEL_NO=sk.get("SKUID").toString();
								}else {
									LABEL_NO=sk.get("LABEL_NO").toString();
								}
								sk.put("TRY_QTY", m.get("TRY_QTY"));
								sk.put("GR_QTY", m.get("RECEIPT_QTY"));
								sk.put("SOBKZ", m.get("SOBKZ"));
								sk.put("WH_NUMBER", m.get("WH_NUMBER"));
								sk.put("LGORT", m.get("LGORT"));
								sk.put("LABEL_NO", LABEL_NO);
								sk.put("LABEL_STATUS", LABEL_STATUS);
								sk.put("QC_RESULT_CODE", QC_RESULT_CODE);
								sk.put("RECEIPT_NO", m.get("RECEIPT_NO"));
								sk.put("RECEIPT_ITEM_NO", m.get("RECEIPT_ITEM_NO")+"");
								sk.put("PRODUCT_DATE", m.get("PRDDT")==null?m.get("PRODUCT_DATE"):m.get("PRDDT"));
								sk.put("EFFECT_DATE", m.get("EFFECT_DATE")==null?"":m.get("EFFECT_DATE").toString());
								sk.put("LIFNR", m.get("LIFNR"));
								sk.put("LIKTX", m.get("LIKTX"));
								sk.put("CREATOR", m.get("CREATOR"));
								sk.put("CREATE_DATE", m.get("CREATE_DATE"));
								sk.put("BEDNR", m.get("BEDNR")==null?"":m.get("BEDNR").toString());
								sk.put("BATCH", m.get("BATCH")==null?"":m.get("BATCH").toString());
								break;
							}
					}
			}
		}else {//非SCM收货单情况需要拆分行项目计算包装箱信息
			Map<String,Object> params=new HashMap<String,Object>();
			params.put("WMS_DOC_TYPE", "08");//标签号
			params.put("WERKS", matList.get(0).get("WERKS"));

			for(Map m:matList) {
				String TEST_FLAG = m.get("TEST_FLAG").toString();
				if(TEST_FLAG.endsWith("01")) {
					//免检
					LABEL_STATUS = "03";
					QC_RESULT_CODE = "11";
				}
				if(TEST_FLAG.endsWith("02")) {
					//无需质检
					LABEL_STATUS = "02";
				}

				Double RECEIPT_QTY=Double.valueOf(m.get("RECEIPT_QTY").toString());
				Double FULL_BOX_QTY=Double.valueOf(m.get("FULL_BOX_QTY").toString());
				int box_num=(int) Math.ceil(RECEIPT_QTY/FULL_BOX_QTY);

				StringBuffer LABEL_NO_BF = new StringBuffer();

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
					String BOX_SN=i+"/"+box_num;
					Double BOX_QTY=FULL_BOX_QTY;//装箱数量，计算得出
					String END_FLAG="0";
					if(i==box_num) {
						BOX_QTY = RECEIPT_QTY-(box_num-1)*FULL_BOX_QTY;
						END_FLAG="X";
					}

					sk.put("LABEL_NO", LABEL_NO);
					sk.put("LABEL_STATUS", LABEL_STATUS);
					sk.put("QC_RESULT_CODE", QC_RESULT_CODE);
					sk.put("RECEIPT_NO", m.get("RECEIPT_NO"));
					sk.put("RECEIPT_ITEM_NO", m.get("RECEIPT_ITEM_NO")+"");
					sk.put("TRY_QTY", m.get("TRY_QTY"));
					sk.put("GR_QTY", m.get("RECEIPT_QTY"));
					sk.put("SOBKZ", m.get("SOBKZ"));
					sk.put("CREATOR", m.get("CREATOR"));
					sk.put("CREATE_DATE", m.get("CREATE_DATE"));
					sk.put("WH_NUMBER", m.get("WH_NUMBER"));
					sk.put("LGORT", m.get("LGORT"));
					sk.put("BOX_SN", BOX_SN);
					sk.put("FULL_BOX_QTY", m.get("FULL_BOX_QTY"));
					sk.put("BOX_QTY", BOX_QTY);
					sk.put("END_FLAG", END_FLAG);

					sk.put("PRODUCT_DATE", m.get("PRODUCT_DATE")==null?null:m.get("PRODUCT_DATE"));
					sk.put("EFFECT_DATE", m.get("EFFECT_DATE")==null?"":m.get("EFFECT_DATE").toString());
					sk.put("LIFNR", m.get("LIFNR"));
					sk.put("LIKTX", m.get("LIKTX"));
					sk.put("BEDNR", m.get("BEDNR")==null?"":m.get("BEDNR").toString());
					sk.put("PO_NO", m.get("PO_NO")==null?"":m.get("PO_NO").toString());
					sk.put("PO_ITEM_NO", m.get("PO_ITEM_NO")==null?"":m.get("PO_ITEM_NO").toString());

					skList.add(sk);

					LABEL_NO_BF.append(LABEL_NO).append(",");
				}

				if(LABEL_NO_BF.length()>0) {
					m.put("LABEL_NO", LABEL_NO_BF.toString().substring(0, LABEL_NO_BF.length()-1));
				}else {
					m.put("LABEL_NO", "");
				}

			}

		}
		if(skList.size()>0) {
			wmsInReceiptDao.insertCoreLabel(skList);
		}

	}

	@Override
	public List<Map<String, Object>> getPoItems(Map<String, Object> params) {

		return wmsInReceiptDao.getPOItems(params);
	}
	@Override
	public List<String> getPOLifnr(List<String> lifnrList) {

		return wmsInReceiptDao.getPOLifnr(lifnrList);
	}
	/**
	 * SAP采购订单收货确认
	 */
	@Override
	@Transactional(rollbackFor=Exception.class)
	public R boundIn_02(Map<String, Object> params) {
		List<Map<String,Object>> matList=(List<Map<String,Object>>) params.get("matList");
		List<Map<String,Object>> skList=new ArrayList<Map<String,Object>>();//包装箱信息
		String WERKS = params.get("WERKS").toString();
		String SAP_MOVE_TYPE="";//SAP移动类型，为空不进行SAP过账
		String WMS_MOVE_TYPE = "";

		params.put("BUSINESS_NAME", params.get("BUSINESS_NAME"));//PO采购订单收料
		params.put("BUSINESS_TYPE", "02");//PO采购订单
		if("04".equals(params.get("BUSINESS_NAME"))) {
			params.put("BUSINESS_TYPE", "04");//跨工厂PO采购订单
		}
		params.put("BUSINESS_CLASS", "01");
		/**
		 * 获取WMS移动类型、移动原因、SAP移动类型、SAP过账标识
		 */
		Map<String,Object> cdmap=new HashMap<String,Object>();
		cdmap.put("BUSINESS_NAME", params.get("BUSINESS_NAME"));//PO采购订单收料
		cdmap.put("BUSINESS_TYPE",  params.get("BUSINESS_TYPE"));//PO采购订单
		cdmap.put("BUSINESS_CLASS", "01");
		cdmap.put("WERKS", WERKS);
		cdmap.put("SOBKZ", matList.get(0).get("SOBKZ"));
		if("04".equals(params.get("BUSINESS_NAME"))) {//跨工厂PO采购订单,固定特殊库存类型为'Z'
			params.put("SOBKZ", "Z");
		}
		Map<String,Object> moveSyn=commonDao.getMoveAndSyn(cdmap);
		if(moveSyn==null || moveSyn.get("WMS_MOVE_TYPE")==null) {
			return R.error("收货工厂"+WERKS+"未配置PO采购订单收料业务类型！");
		}
		if(moveSyn!=null) {
			SAP_MOVE_TYPE=(String)moveSyn.get("SAP_MOVE_TYPE");
			WMS_MOVE_TYPE = (String)moveSyn.get("WMS_MOVE_TYPE");
			params.put("SYN_FLAG", moveSyn.get("SYN_FLAG"));
		}
		params.put("SAP_MOVE_TYPE", SAP_MOVE_TYPE);
		params.put("WMS_MOVE_TYPE", WMS_MOVE_TYPE);

		/**
		 * 生成WMS批次信息
		 */
		/*String setMatBatchMsg = this.setMatBatch(params, matList);
		if(StringUtils.isNotEmpty(setMatBatchMsg)) {
			return R.error(setMatBatchMsg);
		}*/
		params.put("matList", matList);

		/**
		 * 保存收货单数据
		 */
		String RECEIPT_NO=this.saveReceiptInfo(params);//收货单号

		/**
		 * 保存收料房库存:先根据工厂代码、仓库号、库位、物料号、wms批次、特殊库存类型、供应商代码
		 * 查询收料房库存信息，存在则累加库存数量，否则新增库存数据
		 */
		this.saveRhStock(params);

		/**
		 * 保存标签数据
		 */
		this.saveCoreLabel(skList,(List<Map<String,Object>>)params.get("matList"));

		/**
		 * 产生WMS凭证记录
		 *
		 */
		String WMS_NO=commonService.saveWMSDoc(params,(List<Map<String,Object>>)params.get("matList"));
		params.put("WMS_NO", WMS_NO);

		/**
		 *  质检
		 */
		String INSPECTION_NO=this.doQualityCheck(params);			//送检单号

		/**
		 * SAP过账：检查配置 是否及时过账到SAP，如果配置及时过账，抛数据给SAP过账，如果过账失败，显示报错信息，操作终止
		 * 非及时过账，延迟过账到SAP，先产生和更新WMS相关的数据后，再抛数据给SAP过账
		 * SAP过账失败回滚
		 */

		String SAP_NO= commonService.doSapPost(params);

		StringBuilder msg=new StringBuilder();
		msg.append("操作成功！收货单号:");
		msg.append(RECEIPT_NO);
		if(StringUtils.isNotBlank(INSPECTION_NO)) {
			msg.append(" 送检单号:");
			msg.append(INSPECTION_NO);
		}
		msg.append(" WMS凭证:");
		msg.append(WMS_NO);
		if(StringUtils.isNotBlank(SAP_NO)) {
			msg.append(" SAP凭证:");
			msg.append(SAP_NO);
		}

		return R.ok(msg.toString()).put("lableList", skList).put("inspectionList", matList);

	}

	@Override
	public void updateSCMStauts(List<Map<String,Object>> skList,List<Map<String,Object>> matList) {
		/**
		 * 通过数据库集成接口更新SCM送货单状态（抬头表状态和行项目状态） 收货数量 包装箱据状态
		 * 如果全部行项目状态为 已收货（3），抬头表状态为（3）
		 * 行项目已收数量-冲销/退货数量=送货数量，状态为已收货（3）
		 */
		try {
			String status_box="3";//包装箱状态：3：已收货
			String status_head="8";//送货单抬头状态，3：已收货，8：部分收货
			String status_item="8";//送货单行项目状态,3：已收货，8：部分收货
			//从收货单查询行项目状态，行项目已收数量-冲销/退货数量=送货数量，状态为已收货（3），如果不等于，状态为部分收货（8）
			//20190605 pengtao注释，唐工要求不需要更新收货数量
			//scmDeliveryService.updateTPO(matList);//更新SCM送货单收货数量

			for(Map<String,Object>sk:skList) {
				sk.put("BARCODE", sk.get("SKUID"));
				sk.put("STATE", status_box);
			}

			//更新包装箱信息
			scmDeliveryService.updateTDELIVERYDETAIL(skList);

			List<String> asnList=new ArrayList<String>(); //统计已收数量用的条件（送货单号##送货单行项目号）
			for(Map<String,Object> _m:matList) {
				asnList.add(_m.get("ASNNO")+"#*"+_m.get("ASNITM"));
			}
			/**
			 * 根据封装好的查询条件（送货单号##送货单行项目号）查询每条行项目已收货数量, 可收货数量
			 */
			Map<String,Object> receiptCount=this.getReceiptCount(asnList);
			double finishedQty=0;//已收数量
			double RECEIPT_QTY=0;//可收货数量
			for(Map<String,Object> m:matList) {
				String s=(String) receiptCount.get(m.get("ASNNO")+"#*"+m.get("MATNR")+"#*"+m.get("ASNITM"));
				if (s==null) {
					s="0";
				}
				String QTY=m.get("QTY").toString();
				finishedQty=Double.parseDouble(s);
				RECEIPT_QTY=Double.parseDouble(QTY)-finishedQty;
				if(RECEIPT_QTY<=0) {
					m.put("STATE", "3");
				}else {
					m.put("STATE", "8");
				}
				m.put("BARCODE", m.get("ASNNO"));
				m.put("ROWNO", m.get("ASNITM"));
			}
			//更新送货单行项目状态及送货单抬头状态
			Map<String,Object>rsMap=scmDeliveryService.updateTDELIVERYROWITEM(matList);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * SAP交货单收货确认
	 */
	@Override
	@Transactional(rollbackFor=Exception.class)
	public R boundIn_03(Map<String, Object> params) {
		List<Map<String,Object>> matList=(List<Map<String,Object>>) params.get("matList");
		List<Map<String,Object>> skList=new ArrayList<Map<String,Object>>();//包装箱信息
		String WERKS = params.get("WERKS").toString();
		String SAP_MOVE_TYPE="";//SAP移动类型，为空不进行SAP过账
		String WMS_MOVE_TYPE="";

		params.put("BUSINESS_NAME", params.get("BUSINESS_NAME"));//SAP交货单收料
		params.put("BUSINESS_TYPE", "06");//SAP交货单
		params.put("BUSINESS_CLASS", "01");
		/**
		 * 获取WMS移动类型、移动原因、SAP移动类型、SAP过账标识
		 */
		Map<String,Object> cdmap=new HashMap<String,Object>();
		cdmap.put("BUSINESS_NAME", params.get("BUSINESS_NAME"));//SAP交货单收料
		cdmap.put("BUSINESS_TYPE", "06");//SAP交货单
		cdmap.put("BUSINESS_CLASS", "01");
		cdmap.put("WERKS", WERKS);
		cdmap.put("SOBKZ", matList.get(0).get("SOBKZ"));
		Map<String,Object> moveSyn=commonDao.getMoveAndSyn(cdmap);
		if(moveSyn==null || moveSyn.get("WMS_MOVE_TYPE")==null) {
			return R.error("收货工厂"+WERKS+"未配置SAP交货单收料业务类型！");
		}
		if(moveSyn!=null) {
			SAP_MOVE_TYPE=(String)moveSyn.get("SAP_MOVE_TYPE");
			WMS_MOVE_TYPE=(String)moveSyn.get("WMS_MOVE_TYPE");
			params.put("SYN_FLAG", moveSyn.get("SYN_FLAG"));
		}
		params.put("SAP_MOVE_TYPE", SAP_MOVE_TYPE);
		params.put("WMS_MOVE_TYPE", WMS_MOVE_TYPE);

		/**
		 * 保存收货单数据
		 */
		String RECEIPT_NO=this.saveReceiptInfo(params);//收货单号

		/**
		 * 保存收料房库存:先根据工厂代码、仓库号、库位、物料号、wms批次、特殊库存类型、供应商代码
		 * 查询收料房库存信息，存在则累加库存数量，否则新增库存数据
		 */
		this.saveRhStock(params);

		/**
		 * 保存标签数据
		 */
		this.saveCoreLabel(skList, matList);

		/**
		 * 产生WMS凭证记录
		 *
		 */
		String WMS_NO=commonService.saveWMSDoc(params,matList);
		params.put("WMS_NO", WMS_NO);

		/**
		 *  质检
		 */
		String INSPECTION_NO=this.doQualityCheck(params);			//送检单号

		/**
		 * SAP过账：检查配置 是否及时过账到SAP，如果配置及时过账，抛数据给SAP过账，如果过账失败，显示报错信息，操作终止
		 * 非及时过账，延迟过账到SAP，先产生和更新WMS相关的数据后，再抛数据给SAP过账
		 * SAP过账失败回滚
		 */
		List<Map<String,Object>> matListNew = (List<Map<String,Object>>)params.get("matListNew");
		for (Map<String,Object> m : matListNew) {
	   		String VBELN = m.get("SAP_OUT_NO").toString();//交货单
			String POSNR = m.get("SAP_OUT_ITEM_NO").toString();//行项目
			Object BATCH = m.get("BATCH")==null?m.get("F_BATCH"):m.get("BATCH");

			for (Map<String, Object> matMap : matList) {
				if(VBELN.equals(matMap.get("SAP_OUT_NO").toString()) && POSNR.equals(matMap.get("SAP_OUT_ITEM_NO").toString())) {
					//覆盖
					m.put("WMS_NO", matMap.get("WMS_NO"));
					m.put("WMS_ITEM_NO", matMap.get("WMS_ITEM_NO"));
					if(null == BATCH || StringUtils.isEmpty(BATCH.toString())) {
						BATCH = matMap.get("BATCH");
					}
					m.put("VGBEL", matMap.get("VGBEL"));
					m.put("VGPOS", matMap.get("VGPOS"));
				}
			}
			m.put("BATCH", BATCH);
		}

		params.put("matList", matListNew);
		String SAP_NO= commonService.doSapPost(params);

		StringBuilder msg=new StringBuilder();
		msg.append("操作成功！收货单号:");
		msg.append(RECEIPT_NO);
		if(StringUtils.isNotBlank(INSPECTION_NO)) {
			msg.append(" 送检单号:");
			msg.append(INSPECTION_NO);
		}
		msg.append(" WMS凭证:");
		msg.append(WMS_NO);
		if(StringUtils.isNotBlank(SAP_NO)) {
			msg.append(" SAP凭证:");
			msg.append(SAP_NO);
		}
		return R.ok(msg.toString()).put("lableList", skList).put("inspectionList", matList)
				.put("RECEIPT_NO",RECEIPT_NO)
				.put("INSPECTION_NO",INSPECTION_NO)
				.put("WMS_NO",WMS_NO)
				.put("SAP_NO",SAP_NO);

	}
	/**
	 * 根据SAP采购订单号查询行项目的收货权限工厂
	 * @param params
	 * @return
	 */
	@Override
	public List<Map<String, String>> getItemAuthWerksList(Map<String, Object> params) {
		List<Map<String,String>> werksList=wmsInReceiptDao.getItemAuthWerksList(params);
		return werksList;
	}

	/**
	 * 303调拨单收货确认
	 */
	@Override
	@Transactional(rollbackFor=Exception.class)
	public R boundIn_06(Map<String, Object> params) {
		List<Map<String,Object>> matList=(List<Map<String,Object>>) params.get("matList");
		List<Map<String,Object>> skList=new ArrayList<Map<String,Object>>();//包装箱信息
		//获取303凭证关联的条码信息
		for (Map<String, Object> map : matList) {
			String ITEM_LABEL_LIST = map.get("ITEM_LABEL_LIST")==null?"":map.get("ITEM_LABEL_LIST").toString();
			if(!"".equals(ITEM_LABEL_LIST)) {
				JSONObject.parseArray(ITEM_LABEL_LIST, Map.class).forEach(label->{
					label=(Map<String,Object>)label;
					skList.add(label);
				});
			}
		}

		String WERKS = params.get("WERKS").toString();
		String SAP_MOVE_TYPE="";//SAP移动类型，为空不进行SAP过账
		String WMS_MOVE_TYPE = "";

		params.put("BUSINESS_NAME", params.get("BUSINESS_NAME"));
		params.put("BUSINESS_TYPE", "08");//303调拨
		params.put("BUSINESS_CLASS", "01");
		/**
		 * 获取WMS移动类型、移动原因、SAP移动类型、SAP过账标识
		 */
		Map<String,Object> cdmap=new HashMap<String,Object>();
		cdmap.put("BUSINESS_NAME", params.get("BUSINESS_NAME"));
		cdmap.put("BUSINESS_TYPE", "08");//303调拨
		cdmap.put("BUSINESS_CLASS", "01");
		cdmap.put("WERKS", WERKS);
		cdmap.put("SOBKZ", matList.get(0).get("SOBKZ"));
		Map<String,Object> moveSyn=commonDao.getMoveAndSyn(cdmap);
		if(moveSyn==null || moveSyn.get("WMS_MOVE_TYPE")==null) {
			return R.error("收货工厂"+WERKS+"未配置303调拨收料业务类型！");
		}
		if(moveSyn!=null) {
			SAP_MOVE_TYPE=(String)moveSyn.get("SAP_MOVE_TYPE");
			WMS_MOVE_TYPE=(String)moveSyn.get("WMS_MOVE_TYPE");
			params.put("SYN_FLAG", moveSyn.get("SYN_FLAG"));
		}
		params.put("SAP_MOVE_TYPE", SAP_MOVE_TYPE);
		params.put("WMS_MOVE_TYPE", WMS_MOVE_TYPE);
		/**
		 * 生成WMS批次信息
		 */
		/*String setMatBatchMsg = this.setMatBatch(params, matList);
		if(StringUtils.isNotEmpty(setMatBatchMsg)) {
			return R.error(setMatBatchMsg);
		}*/
		params.put("matList", matList);

		/**
		 * 保存收货单数据
		 */
		String RECEIPT_NO=this.saveReceiptInfo(params);//收货单号

		/**
		 * 保存收料房库存:先根据工厂代码、仓库号、库位、物料号、wms批次、特殊库存类型、供应商代码
		 * 查询收料房库存信息，存在则累加库存数量，否则新增库存数据
		 */
		this.saveRhStock(params);

		if(skList.size()<=0) {
			/**
			 * 保存标签数据
			 */
			this.saveCoreLabel(skList,(List<Map<String,Object>>)params.get("matList"));
		}

		/**
		 * 产生WMS凭证记录
		 *
		 */
		String WMS_NO=commonService.saveWMSDoc(params,(List<Map<String,Object>>)params.get("matList"));
		params.put("WMS_NO", WMS_NO);

		/**
		 *  质检
		 */
		String INSPECTION_NO=this.doQualityCheck(params);			//送检单号

		/**
		 * SAP过账：检查配置 是否及时过账到SAP，如果配置及时过账，抛数据给SAP过账，如果过账失败，显示报错信息，操作终止
		 * 非及时过账，延迟过账到SAP，先产生和更新WMS相关的数据后，再抛数据给SAP过账
		 * SAP过账失败回滚
		 */
		String SAP_NO = "无需过账到SAP系统";
		if(SAP_MOVE_TYPE !=null && !SAP_MOVE_TYPE.equals("")) {
			SAP_NO= commonService.doSapPost(params);
		}

		StringBuilder msg=new StringBuilder();
		msg.append("操作成功！收货单号:");
		msg.append(RECEIPT_NO);
		if(StringUtils.isNotBlank(INSPECTION_NO)) {
			msg.append(" 送检单号:");
			msg.append(INSPECTION_NO);
		}
		msg.append(" WMS凭证:");
		msg.append(WMS_NO);
		if(StringUtils.isNotBlank(SAP_NO)) {
			msg.append(" SAP凭证:");
			msg.append(SAP_NO);
		}
		return R.ok(msg.toString()).put("lableList", skList).put("inspectionList", matList);

	}

	@Override
	public List<Map<String, Object>> getMatListByMATNR(String matnr_str,String WERKS) {
		List<Map<String, Object>> matList=new	ArrayList<Map<String,Object>>();
		List<String> matnr_list=Arrays.asList(matnr_str.split(","));
		matList=wmsInReceiptDao.getMatListByMATNR(matnr_list,WERKS);//查询物料描述和单位

		return matList;
	}
	/**
	 * 无PO确认收货
	 */
	@Override
	@Transactional(rollbackFor=Exception.class)
	public R boundIn_05(Map<String, Object> params) {
		List<Map<String,Object>> matList=(List<Map<String,Object>>) params.get("matList");
		List<Map<String,Object>> skList=new ArrayList<Map<String,Object>>();//包装箱信息
		String WERKS = params.get("WERKS").toString();
		String SAP_MOVE_TYPE="";//SAP移动类型，为空不进行SAP过账
		String WMS_MOVE_TYPE = "";

		params.put("BUSINESS_NAME", params.get("BUSINESS_NAME"));
		params.put("BUSINESS_TYPE", "05");//无PO收货
		params.put("BUSINESS_CLASS", "01");
		/**
		 * 获取WMS移动类型、移动原因、SAP移动类型、SAP过账标识
		 */
		Map<String,Object> cdmap=new HashMap<String,Object>();
		cdmap.put("BUSINESS_NAME", params.get("BUSINESS_NAME"));
		cdmap.put("BUSINESS_TYPE", "05");//无PO收货
		cdmap.put("BUSINESS_CLASS", "01");
		cdmap.put("WERKS", WERKS);
		cdmap.put("SOBKZ", "Z");//无PO收货，只配置了 SOBKZ为Z的移动类型
		Map<String,Object> moveSyn=commonDao.getMoveAndSyn(cdmap);
		if(moveSyn==null || moveSyn.get("WMS_MOVE_TYPE")==null) {
			return R.error("收货工厂"+WERKS+"未配置无PO收料业务类型！");
		}
		if(moveSyn!=null) {
			SAP_MOVE_TYPE=(String)moveSyn.get("SAP_MOVE_TYPE");
			WMS_MOVE_TYPE=(String)moveSyn.get("WMS_MOVE_TYPE");
			params.put("SYN_FLAG", moveSyn.get("SYN_FLAG"));
		}
		params.put("SAP_MOVE_TYPE", SAP_MOVE_TYPE);
		params.put("WMS_MOVE_TYPE", WMS_MOVE_TYPE);
		/**
		 * 生成WMS批次信息
		 */
		/*String setMatBatchMsg = this.setMatBatch(params, matList);
		if(StringUtils.isNotEmpty(setMatBatchMsg)) {
			return R.error(setMatBatchMsg);
		}*/
		params.put("matList", matList);

		/**
		 * 保存收货单数据
		 */
		String RECEIPT_NO=this.saveReceiptInfo(params);//收货单号

		/**
		 * 保存收料房库存:先根据工厂代码、仓库号、库位、物料号、wms批次、特殊库存类型、供应商代码
		 * 查询收料房库存信息，存在则累加库存数量，否则新增库存数据
		 */
		this.saveRhStock(params);

		/**
		 * 保存标签数据
		 */
		this.saveCoreLabel(skList,(List<Map<String,Object>>)params.get("matList"));

		/**
		 * 产生WMS凭证记录
		 *
		 */
		String WMS_NO=commonService.saveWMSDoc(params,(List<Map<String,Object>>)params.get("matList"));
		params.put("WMS_NO", WMS_NO);

		/**
		 *  质检
		 */
		String INSPECTION_NO=this.doQualityCheck(params);			//送检单号

		String SAP_NO = "";
		if(SAP_MOVE_TYPE!=null&&!SAP_MOVE_TYPE.equals("")) {
			/**
			 * SAP过账：检查配置 是否及时过账到SAP，如果配置及时过账，抛数据给SAP过账，如果过账失败，显示报错信息，操作终止
			 * 非及时过账，延迟过账到SAP，先产生和更新WMS相关的数据后，再抛数据给SAP过账
			 * SAP过账失败回滚
			 */

			SAP_NO= commonService.doSapPost(params);
		}

		StringBuilder msg=new StringBuilder();
		msg.append("操作成功！收货单号:");
		msg.append(RECEIPT_NO);
		if(StringUtils.isNotBlank(INSPECTION_NO)) {
			msg.append(" 送检单号:");
			msg.append(INSPECTION_NO);
		}
		msg.append(" WMS凭证:");
		msg.append(WMS_NO);
		if(StringUtils.isNotBlank(SAP_NO)) {
			msg.append(" SAP凭证:");
			msg.append(SAP_NO);
		}
		return R.ok(msg.toString()).put("lableList", skList).put("inspectionList", matList);
	}

	@Override
	public List<Map<String, Object>> getHXMatList(Map<String, Object> params) {
		return wmsInReceiptDao.getHXMatList(params);
	}

	/**
	 * 303调拨(A)收货确认
	 */
	@Override
	@Transactional(rollbackFor=Exception.class)
	public R boundIn_09(Map<String, Object> params) {
		R r=new R();
		List<Map<String,Object>> matList=(List<Map<String,Object>>) params.get("matList");
		List<Map<String,Object>> skList=new ArrayList<Map<String,Object>>();//包装箱信息
		String WERKS = params.get("WERKS").toString();
		String SAP_MOVE_TYPE="";//SAP移动类型，为空不进行SAP过账
		String WMS_MOVE_TYPE = "";
		String curDate=DateUtils.format(new Date(),"yyyy-MM-dd HH:hh:ss");
		params.put("SEARCH_DATE", curDate);
		String TEST_FLAG=wmsInReceiptDao.getTestFlag(params);
		if(TEST_FLAG ==null || TEST_FLAG.equals("")) {
			//工厂质检主数据未配置
			return R.error("收货工厂"+WERKS+"未配置303调拨收料（A）的工厂质检标识主数据！");
		}
		params.put("TEST_FLAG", TEST_FLAG==null?"00":TEST_FLAG);
		params.put("BUSINESS_NAME", params.get("BUSINESS_NAME"));//SAP交货单收料
		params.put("BUSINESS_TYPE", "09");//303调拨(A)
		params.put("BUSINESS_CLASS", "01");
		/**
		 * 获取WMS移动类型、移动原因、SAP移动类型、SAP过账标识
		 */
		Map<String,Object> cdmap=new HashMap<String,Object>();
		cdmap.put("BUSINESS_NAME", params.get("BUSINESS_NAME"));//SAP交货单收料
		cdmap.put("BUSINESS_TYPE", "08");//303调拨
		cdmap.put("BUSINESS_CLASS", "01");
		cdmap.put("WERKS", WERKS);
		cdmap.put("SOBKZ", matList.get(0).get("SOBKZ"));
		Map<String,Object> moveSyn=commonDao.getMoveAndSyn(cdmap);
		if(moveSyn==null || moveSyn.get("WMS_MOVE_TYPE")==null) {
			return R.error("收货工厂"+WERKS+"未配置303调拨（A）收料业务类型！");
		}
		if(moveSyn!=null) {
			SAP_MOVE_TYPE=(String)moveSyn.get("SAP_MOVE_TYPE");
			WMS_MOVE_TYPE=(String)moveSyn.get("WMS_MOVE_TYPE");
			params.put("SYN_FLAG", moveSyn.get("SYN_FLAG"));
		}
		params.put("SAP_MOVE_TYPE", SAP_MOVE_TYPE);
		params.put("WMS_MOVE_TYPE", WMS_MOVE_TYPE);
		/**
		 * 生成WMS批次信息
		 */
		/*String setMatBatchMsg = this.setMatBatch(params, matList);
		if(StringUtils.isNotEmpty(setMatBatchMsg)) {
			return R.error(setMatBatchMsg);
		}*/
		params.put("matList", matList);

		/**
		 * 保存收货单数据
		 */
		String RECEIPT_NO=this.saveReceiptInfo(params);//收货单号

		/**
		 * 保存收料房库存:先根据工厂代码、仓库号、库位、物料号、wms批次、特殊库存类型、供应商代码
		 * 查询收料房库存信息，存在则累加库存数量，否则新增库存数据
		 */
		this.saveRhStock(params);

		/**
		 * 保存标签数据
		 */
		this.saveCoreLabel(skList,(List<Map<String,Object>>)params.get("matList"));

		/**
		 * 产生WMS凭证记录
		 *
		 */
		String WMS_NO=commonService.saveWMSDoc(params,(List<Map<String,Object>>)params.get("matList"));
		params.put("WMS_NO", WMS_NO);

		/**
		 *  质检
		 */
		String INSPECTION_NO=this.doQualityCheck(params);			//送检单号

		/**
		 * 更新SAP303调拨单核销信息表--WMS_HX_TO 实收303DB数量（SS303DB）、303剩余核销数量（HX_QTY_XS）
		 */
		wmsInReceiptDao.update303HXInfo(matList);

		/**
		 * SAP过账：检查配置 是否及时过账到SAP，如果配置及时过账，抛数据给SAP过账，如果过账失败，显示报错信息，操作终止
		 * 非及时过账，延迟过账到SAP，先产生和更新WMS相关的数据后，再抛数据给SAP过账
		 * SAP过账失败回滚
		 */

		String SAP_NO= commonService.doSapPost(params);

		StringBuilder msg=new StringBuilder();
		msg.append("操作成功！收货单号:");
		msg.append(RECEIPT_NO);
		if(StringUtils.isNotBlank(INSPECTION_NO)) {
			msg.append(" 送检单号:");
			msg.append(INSPECTION_NO);
		}
		msg.append(" WMS凭证:");
		msg.append(WMS_NO);
		if(StringUtils.isNotBlank(SAP_NO)) {
			msg.append(" SAP凭证:");
			msg.append(SAP_NO);
		}
		r.ok(msg.toString()).put("lableList", skList).put("inspectionList", matList);
		return r;

	}

	@Override
	public List<Map<String, Object>> getHXPOMatList(Map<String, Object> params) {
		return wmsInReceiptDao.getHXPOMatList(params);
	}

	/**
	 * SAP采购订单(A)确认收货
	 */
	@Override
	public R boundIn_07(Map<String, Object> params) {
		R r=new R();
		List<Map<String,Object>> matList=(List<Map<String,Object>>) params.get("matList");
		List<Map<String,Object>> skList=new ArrayList<Map<String,Object>>();//包装箱信息
		String WERKS = params.get("WERKS").toString();
		String SAP_MOVE_TYPE="";//SAP移动类型，为空不进行SAP过账
		String WMS_MOVE_TYPE = "";

		params.put("BUSINESS_NAME", params.get("BUSINESS_NAME"));//SAP交货单收料
		params.put("BUSINESS_TYPE", "03");//采购订单收货(A)
		params.put("BUSINESS_CLASS", "01");
		/**
		 * 获取WMS移动类型、移动原因、SAP移动类型、SAP过账标识
		 */
		Map<String,Object> cdmap=new HashMap<String,Object>();
		cdmap.put("BUSINESS_NAME", params.get("BUSINESS_NAME"));//SAP交货单收料
		cdmap.put("BUSINESS_TYPE", "03");//采购订单收货(A)
		cdmap.put("BUSINESS_CLASS", "01");
		cdmap.put("WERKS", WERKS);
		cdmap.put("SOBKZ", matList.get(0).get("SOBKZ"));
		Map<String,Object> moveSyn=commonDao.getMoveAndSyn(cdmap);
		if(moveSyn==null || moveSyn.get("WMS_MOVE_TYPE")==null) {
			return R.error("收货工厂"+WERKS+"未配置SAP采购订单收料（A）业务类型！");
		}
		if(moveSyn!=null) {
			SAP_MOVE_TYPE=(String)moveSyn.get("SAP_MOVE_TYPE");
			WMS_MOVE_TYPE=(String)moveSyn.get("WMS_MOVE_TYPE");
			params.put("SYN_FLAG", moveSyn.get("SYN_FLAG"));
		}
		params.put("SAP_MOVE_TYPE", SAP_MOVE_TYPE);
		params.put("WMS_MOVE_TYPE", WMS_MOVE_TYPE);
		/**
		 * 生成WMS批次信息
		 */
		/*String setMatBatchMsg = this.setMatBatch(params, matList);
		if(StringUtils.isNotEmpty(setMatBatchMsg)) {
			return R.error(setMatBatchMsg);
		}*/
		params.put("matList", matList);

		/**
		 * 保存收货单数据
		 */
		String RECEIPT_NO=this.saveReceiptInfo(params);//收货单号

		/**
		 * 保存收料房库存:先根据工厂代码、仓库号、库位、物料号、wms批次、特殊库存类型、供应商代码
		 * 查询收料房库存信息，存在则累加库存数量，否则新增库存数据
		 */
		this.saveRhStock(params);

		/**
		 * 保存标签数据
		 */
		this.saveCoreLabel(skList,(List<Map<String,Object>>)params.get("matList"));

		/**
		 * 产生WMS凭证记录
		 *
		 */
		String WMS_NO=commonService.saveWMSDoc(params,(List<Map<String,Object>>)params.get("matList"));
		params.put("WMS_NO", WMS_NO);
		/**
		 *  质检
		 */
		String INSPECTION_NO=this.doQualityCheck(params);			//送检单号

		/**
		 * 更新SAP采购订单核销信息表--WMS_HX_PO 实收SS103数量（SS103）、剩余核销数量（HX_QTY）
		 */
		wmsInReceiptDao.updatePOHXInfo(matList);

		/**
		 * SAP过账：检查配置 是否及时过账到SAP，如果配置及时过账，抛数据给SAP过账，如果过账失败，显示报错信息，操作终止
		 * 非及时过账，延迟过账到SAP，先产生和更新WMS相关的数据后，再抛数据给SAP过账
		 * SAP过账失败回滚
		 */

		String SAP_NO= commonService.doSapPost(params);

		StringBuilder msg=new StringBuilder();
		msg.append("操作成功！收货单号:");
		msg.append(RECEIPT_NO);
		if(StringUtils.isNotBlank(INSPECTION_NO)) {
			msg.append(" 送检单号:");
			msg.append(INSPECTION_NO);
		}
		msg.append(" WMS凭证:");
		msg.append(WMS_NO);
		if(StringUtils.isNotBlank(SAP_NO)) {
			msg.append(" SAP凭证:");
			msg.append(SAP_NO);
		}
		r.ok(msg.toString()).put("lableList", skList).put("inspectionList", matList);
		return r;
	}

	@Override
	public List<Map<String, Object>> getHXDNMatList(Map<String, Object> params) {
		return wmsInReceiptDao.getHXDNMatList(params);
	}

	/**
	 * SAP交货单(A)确认收货
	 */
	@Override
	public R boundIn_08(Map<String, Object> params) {
		R r=new R();
		List<Map<String,Object>> matList=(List<Map<String,Object>>) params.get("matList");
		List<Map<String,Object>> skList=new ArrayList<Map<String,Object>>();//包装箱信息
		String WERKS = params.get("WERKS").toString();
		String SAP_MOVE_TYPE="";//SAP移动类型，为空不进行SAP过账
		String WMS_MOVE_TYPE = "";

		params.put("BUSINESS_NAME", params.get("BUSINESS_NAME"));//SAP交货单（A）收料
		params.put("BUSINESS_TYPE", "07");//采购订单收货(A)
		params.put("BUSINESS_CLASS", "01");
		/**
		 * 获取WMS移动类型、移动原因、SAP移动类型、SAP过账标识
		 */
		Map<String,Object> cdmap=new HashMap<String,Object>();
		cdmap.put("BUSINESS_NAME", params.get("BUSINESS_NAME"));//SAP交货单收料
		cdmap.put("BUSINESS_TYPE", "07");//采购订单收货(A)
		cdmap.put("BUSINESS_CLASS", "01");
		cdmap.put("WERKS", WERKS);
		cdmap.put("SOBKZ", matList.get(0).get("SOBKZ"));
		Map<String,Object> moveSyn=commonDao.getMoveAndSyn(cdmap);
		if(moveSyn==null || moveSyn.get("WMS_MOVE_TYPE")==null) {
			return R.error("收货工厂"+WERKS+"未配置SAP交货单收料（A）业务类型！");
		}
		if(moveSyn!=null) {
			SAP_MOVE_TYPE=(String)moveSyn.get("SAP_MOVE_TYPE");
			WMS_MOVE_TYPE=(String)moveSyn.get("WMS_MOVE_TYPE");
			params.put("SYN_FLAG", moveSyn.get("SYN_FLAG"));
		}
		params.put("SAP_MOVE_TYPE", SAP_MOVE_TYPE);
		params.put("WMS_MOVE_TYPE", WMS_MOVE_TYPE);
		/**
		 * 生成WMS批次信息
		 */
		/*String setMatBatchMsg = this.setMatBatch(params, matList);
		if(StringUtils.isNotEmpty(setMatBatchMsg)) {
			return R.error(setMatBatchMsg);
		}*/
		params.put("matList", matList);

		/**
		 * 保存收货单数据
		 */
		String RECEIPT_NO=this.saveReceiptInfo(params);//收货单号

		/**
		 * 保存收料房库存:先根据工厂代码、仓库号、库位、物料号、wms批次、特殊库存类型、供应商代码
		 * 查询收料房库存信息，存在则累加库存数量，否则新增库存数据
		 */
		this.saveRhStock(params);

		/**
		 * 保存标签数据
		 */
		this.saveCoreLabel(skList,(List<Map<String,Object>>)params.get("matList"));

		/**
		 * 产生WMS凭证记录
		 *
		 */
		String WMS_NO=commonService.saveWMSDoc(params,(List<Map<String,Object>>)params.get("matList"));
		params.put("WMS_NO", WMS_NO);

		/**
		 *  质检
		 */
		String INSPECTION_NO=this.doQualityCheck(params);			//送检单号

		/**
		 * 更新SAP交货单核销信息表--WMS_HX_DN 实收SS103数量（SS103T）、剩余核销数量（HX_QTY_XS）
		 */
		wmsInReceiptDao.updateDNHXInfo(matList);

		/**
		 * SAP过账：检查配置 是否及时过账到SAP，如果配置及时过账，抛数据给SAP过账，如果过账失败，显示报错信息，操作终止
		 * 非及时过账，延迟过账到SAP，先产生和更新WMS相关的数据后，再抛数据给SAP过账
		 * SAP过账失败回滚
		 */

		String SAP_NO= commonService.doSapPost(params);

		StringBuilder msg=new StringBuilder();
		msg.append("操作成功！收货单号:");
		msg.append(RECEIPT_NO);
		if(StringUtils.isNotBlank(INSPECTION_NO)) {
			msg.append(" 送检单号:");
			msg.append(INSPECTION_NO);
		}
		msg.append(" WMS凭证:");
		msg.append(WMS_NO);
		if(StringUtils.isNotBlank(SAP_NO)) {
			msg.append(" SAP凭证:");
			msg.append(SAP_NO);
		}
		r.ok(msg.toString()).put("lableList", skList).put("inspectionList", matList);
		return r;
	}
	/**
	 * 从WMS_CORE_WH_ADDRESS表中获取仓库号
	 */
	@Override
	public String getWHAddr(String WERKS, String LGORT) {
		String WH_NUMBER = wmsInReceiptDao.getWHAddr(WERKS,LGORT);
		return WH_NUMBER;
	}

	/**
	 * 云平台送货单确认收货
	 */
	@Override
	@Transactional(rollbackFor=RuntimeException.class)
	public R boundIn_78(Map<String, Object> params) {
		List<Map<String,Object>> matList=(List<Map<String,Object>>) params.get("matList");//待收货物料清单
		String WERKS = params.get("WERKS").toString();
		String SAP_MOVE_TYPE="";//SAP移动类型，为空不进行SAP过账
		String WMS_MOVE_TYPE = "";
		params.put("BUSINESS_NAME", "78"); //云平台送货单收料
		params.put("BUSINESS_TYPE", "20"); //云平台送货单
		params.put("BUSINESS_CLASS", "01");
		/**
		 * 获取WMS移动类型、移动原因、SAP移动类型、SAP过账标识
		 */
		Map<String,Object> cdmap=new HashMap<String,Object>();
		cdmap.put("BUSINESS_NAME", "78"); //云平台送货单收料
		cdmap.put("BUSINESS_TYPE", "20");  //云平台送货单
		cdmap.put("BUSINESS_CLASS", "01");
		cdmap.put("WERKS", WERKS);
		cdmap.put("SOBKZ", matList.get(0).get("SOBKZ"));
		Map<String,Object> moveSyn=commonDao.getMoveAndSyn(cdmap);
		if(moveSyn==null || moveSyn.get("WMS_MOVE_TYPE")==null) {
			return R.error("收货工厂"+WERKS+"未配置云平台送货单收料业务类型！");
		}
		if(moveSyn!=null) {
			SAP_MOVE_TYPE=(String)moveSyn.get("SAP_MOVE_TYPE");
			WMS_MOVE_TYPE = (String)moveSyn.get("WMS_MOVE_TYPE");
			params.put("SYN_FLAG", moveSyn.get("SYN_FLAG"));
		}
		params.put("SAP_MOVE_TYPE", SAP_MOVE_TYPE);
		params.put("WMS_MOVE_TYPE", WMS_MOVE_TYPE);

		/**
		 * 生成WMS批次信息
		 */
		/*String setMatBatchMsg = this.setMatBatch(params, matList);
		if(StringUtils.isNotEmpty(setMatBatchMsg)) {
			return R.error(setMatBatchMsg);
		}*/
		params.put("matList", matList);

		/**
		 * 保存收货单数据
		 */
		String RECEIPT_NO=this.saveReceiptInfo(params);//收货单号写入待收货行项目清单，并更新收货单行项目质检状态

		/**
		 * 保存收料房库存:先根据工厂代码、仓库号、库位、物料号、wms批次、特殊库存类型、供应商代码
		 * 查询收料房库存信息，存在则累加库存数量，否则新增库存数据
		 */
		this.saveRhStock(params);

		List<Map<String,Object>> skList=(List<Map<String,Object>>) params.get("skList");//条码清单
		//匹配收货行项目关联的条码信息
		for(Map m:matList) {
			StringBuffer LABEL_NO = new StringBuffer();
			for (Map<String, Object> map : skList) {
				if(map.get("ASNNO").toString().equals(m.get("ASNNO").toString()) && map.get("ASNITM").toString().equals(m.get("ASNITM").toString())) {
					if(map.get("SKUID")!=null) {
						LABEL_NO.append(map.get("SKUID").toString()).append(",");
					}else {
						LABEL_NO.append(map.get("LABEL_NO").toString()).append(",");
					}
				}
				map.put("UNIT", map.get("BUNIT")) ;
				if("N".equals(map.get("END_FLAG"))) {
					map.put("END_FLAG","0") ;
				}else {
					map.put("END_FLAG","X") ;
				}
			}
			if(LABEL_NO.length()>0) {
				m.put("LABEL_NO", LABEL_NO.toString().substring(0, LABEL_NO.length()-1));
			}else {
				m.put("LABEL_NO", "");
			}
		}
		/**
		 * 保存标签数据
		 */
		this.saveCoreLabel(skList,(List<Map<String,Object>>)params.get("matList")); //关联标签和收货单号，及根据物料质检类型设置标签状态

		/**
		 * 产生WMS凭证记录
		 *
		 */
		String WMS_NO=commonService.saveWMSDoc(params,(List<Map<String,Object>>)params.get("matList"));
		params.put("WMS_NO", WMS_NO);

		/**
		 *  质检，质检结果关联标签号信息
		 */
		String INSPECTION_NO=this.doQualityCheck(params);//送检单号,设置所有matList行项目物料质检类型 00 质检 01 免检 02 无需质检

		/**
		 * SAP过账：检查配置 是否及时过账到SAP，如果配置及时过账，抛数据给SAP过账，如果过账失败，显示报错信息，操作终止
		 * 非及时过账，延迟过账到SAP，先产生和更新WMS相关的数据后，再抛数据给SAP过账
		 * SAP过账失败回滚
		 */
		String SAP_NO= commonService.doSapPost(params);

		StringBuilder msg=new StringBuilder();
		msg.append("操作成功！收货单号:");
		msg.append(RECEIPT_NO);
		if(StringUtils.isNotBlank(INSPECTION_NO)) {
			msg.append(" 送检单号:");
			msg.append(INSPECTION_NO);
		}
		msg.append(" WMS凭证:");
		msg.append(WMS_NO);
		if(StringUtils.isNotBlank(SAP_NO)) {
			msg.append(" SAP凭证:");
			msg.append(SAP_NO);
		}

		return R.ok(msg.toString()).put("lableList", skList).put("inspectionList", matList);

	}

}

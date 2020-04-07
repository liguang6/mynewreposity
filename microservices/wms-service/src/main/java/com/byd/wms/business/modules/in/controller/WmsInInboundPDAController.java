package com.byd.wms.business.modules.in.controller;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.byd.utils.DateUtils;
import com.byd.utils.R;
import com.byd.wms.business.modules.common.service.WmsCDocNoService;
import com.byd.wms.business.modules.config.dao.WmsCoreWhBinDao;
import com.byd.wms.business.modules.in.dao.WmsInInboundDao;
import com.byd.wms.business.modules.in.service.WmsInInboundService;
import com.byd.wms.business.modules.kn.dao.WmsKnStorageMoveDao;
import com.byd.wms.business.modules.kn.service.WmsKnStorageMoveService;

/**
 * PDA进仓
 * @author yang.lin35
 * @createDate 2019-05-05
 */
@RestController
@RequestMapping("in/wmsinbound/pda")
public class WmsInInboundPDAController {
	
	@Autowired
	WmsInInboundDao inBoundDAO;
	
	@Autowired
	WmsCoreWhBinDao whBinDAO;
	
	@Autowired
	WmsKnStorageMoveDao storageMaveDAO;
	
	@Autowired
	WmsCDocNoService docNoService;
	
	@Autowired
	WmsInInboundService inBoundService;
	
	
	@Autowired
	WmsKnStorageMoveService storeageMaveService;
	
	static final String PARAMS_ERROR_MSG = "必填参数为空";
	static final String DATA_ERROR_MSG = "数据错误";
	
	/**
	 * 仓库配置信息
	 */
	@CrossOrigin
	@RequestMapping("/wh_config")
	public R getWhCfg(@RequestBody Map<String,Object> data){
		String WERKS = (String) data.get("WERKS");
		String WH_NUMBER = (String) data.get("WH_NUMBER");
		if(StringUtils.isBlank(WERKS) || StringUtils.isBlank(WH_NUMBER)) {
			return R.error(PARAMS_ERROR_MSG);
		}
		List<Map<String,Object>> resultList =  inBoundDAO.queryCWh(data);
		if(resultList == null || resultList.size() != 1) {
			return R.error(DATA_ERROR_MSG);
		}
		return R.ok().put("data", resultList);
	}
	
	/**
	 * 标签
	 */
	@CrossOrigin
	@RequestMapping("/barcode")
	public R barcode(@RequestBody Map<String,Object> data) {
		String LABEL_NO = (String) data.get("LABEL_NO");
		if(StringUtils.isBlank(LABEL_NO)) {
			return R.error(PARAMS_ERROR_MSG);
		}
		
		List<Map<String,Object>> resultList =  inBoundDAO.queryLabel(data);
		if(resultList == null || resultList.size() == 0) {
			return R.error(DATA_ERROR_MSG);
		}
		return R.ok().put("data", resultList);
	}
	
	/**
	 * 进仓单
	 */
	@CrossOrigin
	@RequestMapping("/inInbound")
	public R inInbound(@RequestBody Map<String,Object> data) {
		String LABEL_NO = (String) data.get("LABEL_NO");
		if(StringUtils.isBlank(LABEL_NO)) {
			return R.error(PARAMS_ERROR_MSG);
		}
		List<Map<String,Object>> resultList = inBoundDAO.queryInInbound(data);
		if(resultList == null || resultList.size() == 0) {
			return R.error(DATA_ERROR_MSG);
		}
		return R.ok().put("data", resultList);
	}
	
	/**
	 * 创建进仓单
	 * data格式
	 * 
	 * {data:[labels...]}
	 */
	@SuppressWarnings("unchecked")
	@CrossOrigin
	@RequestMapping("/inboundTask")
	public R inboundTask(@RequestBody Map<String,Object> data) {
		List<String> labelNoList = (List<String>) data.get("data");
		String werks = data.get("WERKS").toString();
		String whNumber = data.get("WH_NUMBER").toString();
		String binCode = data.get("BIN_CODE").toString();
		String ltWare = data.get("LT_WARE").toString();
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("LABEL_NO_LIST", labelNoList);
		try {
			String inboundNo =  inBoundService.newInbound(labelNoList,werks,whNumber,binCode,ltWare);
			return R.ok().put("data", inboundNo);
		} catch (Exception e) {
		}
		return R.error("进仓单创建失败！");
	}
	
	@CrossOrigin
	@RequestMapping("/wh_bin_seq")
	public R whBinSEQ(@RequestBody Map<String,Object> data) {
		//data --> WH_NUMBER , SEQ_TYPE
		//根据仓库号和排序类别查询
		List<Map<String,Object>> binSeq =  inBoundDAO.queryWhBinSQE(data);
		return R.ok().put("data", binSeq);
	}
	
	
	@CrossOrigin
	@RequestMapping("/wh_task_list")
	public R wh_task_listn(@RequestBody Map<String, Object> data) {
		String WERKS = data.get("WERKS")==null?"":data.get("WERKS").toString();
		String WH_NUMBER = data.get("WH_NUMBER")==null?"":data.get("WH_NUMBER").toString();
		String MANAGER =  data.get("MANAGER")==null?"":data.get("MANAGER").toString();
		
		List<Map<String,Object>> whTaskList=inBoundService.getInboundTask(WERKS,WH_NUMBER,MANAGER);
		return R.ok().put("result",whTaskList);
		
	}
	
	/**
	 * 上架
	 * @param data
	 * @return
	 */
	@CrossOrigin
	@RequestMapping("/shelf")
	public R shelf(@RequestBody Map<String,Object> data) {
		String ID = (String) data.get("ID");
		String TASK_NUM = (String) data.get("TASK_NUM");
		String REAL_BIN_CODE = (String) data.get("REAL_BIN_CODE");
		if(StringUtils.isBlank(ID)) {
			return R.error(PARAMS_ERROR_MSG);
		}
		
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("ID", ID);
		params.put("TASK_NUM", TASK_NUM);
		List<Map<String,Object>> whTaskList =  inBoundDAO.queryWhTask(params);
		if(whTaskList == null || whTaskList.size() != 1) {
			return R.error("没有找到符合条件的进仓任务");
		}
		
		//1.更新条码状态为已上架
		Map<String,Object> whTask = whTaskList.get(0);
		String LABEL_NO = whTask.get("LABEL_NO") == null ? "" : whTask.get("LABEL_NO").toString();
		List<String> labelArray = JSON.parseArray(LABEL_NO, String.class);
		Map<String,Object> temp = new HashMap<String,Object>();
		for(String label:labelArray) {
			temp.clear();
			temp.put("LABEL_STATUS", "08");
			temp.put("LABEL_NO", label);
			inBoundDAO.updateLabelStatusByLabelNo(temp);
			//2.WMS_CORE_STOCK_LABEL 条码的储位改成实际储位
			if(StringUtils.isNotBlank(REAL_BIN_CODE)) {
				temp.clear();
				temp.put("LABEL_NO", label);
				temp.put("BIN_CODE", REAL_BIN_CODE);
				inBoundDAO.updateStockLabelByLabelNo(temp);
			}
		}
		//3.更新WMS_CORE_WH_TASK(上架任务)，状态为已确认 
		//TIP: 没有部分确认状态
		temp.clear();
		temp.put("ID", whTask.get("ID"));
		temp.put("WT_STATUS", "02");
		inBoundDAO.updateWhTaskById(temp);
		
		//TODO 4.移储操作
		return R.ok();
	}
	
	/**
	 * 扫描实物条码上架
	 * 查询条码信息
	 * @param params
	 * @return
	 */
	@CrossOrigin
	@RequestMapping("/scanner_real_material/label")
	public R scannerRealMaterialShelf(@RequestBody Map<String,Object> params) {
		String INBOUND_RELATED = (String) params.get("INBOUND_RELATED");//关联进仓单行项目
		String LABEL_NO = (String) params.get("LABEL_NO");
		if(StringUtils.isBlank(LABEL_NO)) {
			return R.error(PARAMS_ERROR_MSG);
		}
		
		Map<String,Object> temp = new HashMap<String, Object>();
		if(StringUtils.isNotBlank(INBOUND_RELATED)) {
			// 标签--> 进仓单 --> 标签列表 --> 返回标签列表对象
			temp.put("LABEL_NO", LABEL_NO);
			List<Map<String,Object>> inboundItem = inBoundDAO.queryInInboundItem(temp);
			if(inboundItem == null || inboundItem.size() !=1) {
				return R.error("标签没有关联进仓单，或者关联了多个进仓单");
			}
			List<String> labelList = Arrays.asList(inboundItem.get(0).get("LABEL_NO").toString().split(","));
			
			temp.clear();
			temp.put("LABEL_NO_LIST", labelList);
			List<Map<String,Object>> data =  inBoundDAO.queryLabel(temp);
			data.forEach(m->{
				m.put("INBOUND_NO", inboundItem.get(0).get("INBOUND_NO"));
				m.put("INBOUND_ITEM_NO", inboundItem.get(0).get("INBOUND_ITEM_NO"));
			});
			return R.ok().put("data", data);
		}else {
			//返回标签对象
			temp.put("LABEL_NO", LABEL_NO);
			List<Map<String,Object>> data =  inBoundDAO.queryLabel(temp);
			return R.ok().put("data", data);
		}
	}
	
	/**
	 * 获取最新上架储位
	 * 
	 * 1.根据标签获取批次
	 * 2.从移储表获取该批次最新的一条记录，的目标储位代码
	 */
	@CrossOrigin
	@RequestMapping("/scanner_real_material/latest_bincode")
	public R latestBinCode(@RequestBody Map<String,Object> params) {
		String LABEL_NO = (String) params.get("LABEL_NO");
		Map<String,Object> temp = new HashMap<String, Object>();
		temp.put("LABEL_NO", LABEL_NO);
		List<Map<String,Object>> label = inBoundDAO.queryLabel(temp);
		if(label == null || label.size() == 0) {
			return R.error("没有找到标签信息");
		}
		String BATCH = (String) label.get(0).get("LABEL_NO");
		String binCode = "";
		if(StringUtils.isNotBlank(BATCH)) {
			binCode = inBoundDAO.queryLatestMoveStoreageByBatch(BATCH);
			
		}
		return R.ok().put("data", binCode);	
	}
	
	/**
	 * 扫描实物条码上架 - 上架
	 * 参数
	 * <ul>
	 * <li>LABEL_LIST：上架标签</li>
	 * <li>WERKS：上架仓库</li>
	 * <li>TO_BIN_CODE：目标储位</li>
	 * </ul>
	 */
	@CrossOrigin
	@RequestMapping("/scanner_real_material/shelf")
	public R scannerShelf(@RequestBody Map<String,Object> params) {
		@SuppressWarnings("unchecked")
		List<String> LABEL_LIST = (List<String>) params.get("LABEL_LIST");
		String WH_NUMBER = (String) params.get("WH_NUMBER");//仓库
		String TO_BIN_CODE = (String) params.get("TO_BIN_CODE");//目标储位
		
		//上架移储
		//1.校验上架的储位是否存在
		List<Map<String,Object>> TO_BIN =  whBinDAO.findWhBinByBinCode(WH_NUMBER, TO_BIN_CODE);
		if(TO_BIN == null || TO_BIN.size() == 0) {
			return R.error("储位不存在");
		}
		//2.移储
		Map<String,Object> temp = new HashMap<String, Object>();
		temp.put("LABEL_NO_LIST", LABEL_LIST);
		List<Map<String,Object>> LABEL_LIST_MAP= inBoundDAO.queryLabel(temp);
		if(LABEL_LIST_MAP == null || LABEL_LIST_MAP.size() == 0) {
			return R.error("没有找到标签");
		}
		
		//上架数量
		Double LABEL_TOTAL_QTY = LABEL_LIST_MAP.stream().mapToDouble(m -> {
			return Double.parseDouble(m.get("BOX_QTY").toString());
		}).sum();
		
		//找到库存
		String MATNR = LABEL_LIST_MAP.get(0).get("MATNR").toString();
		String BATCH = LABEL_LIST_MAP.get(0).get("BATCH").toString();
		String LIFNR = LABEL_LIST_MAP.get(0).get("LIFNR").toString();
		String B_WH_NUMBER = LABEL_LIST_MAP.get(0).get("WH_NUMBER").toString();
		String WERKS = LABEL_LIST_MAP.get(0).get("WERKS").toString();
		temp.put("MATNR", MATNR);
		temp.put("BATCH", BATCH);
		temp.put("LIFNR", LIFNR);
		temp.put("WH_NUMBER", B_WH_NUMBER);
		temp.put("WERKS", WERKS);
		List<Map<String,Object>> storeageList =  storageMaveDAO.getStockInfoList(temp);
		if(storeageList == null || storeageList.size() == 0) {
			return R.error("库存信息不存在");
		}
		//设置要更新的库存数量，目标储位
		storeageList.forEach( s -> {
			s.put("STOCK_QTY", LABEL_TOTAL_QTY);
			s.put("TARGET_BIN_CODE", TO_BIN);
			s.put("EDITOR", "");//TODO: 获取用户信息
			s.put("EDIT_DATE", DateUtils.format(new Date(), DateUtils.DATE_TIME_PATTERN));
		});
		
		storeageMaveService.save(storeageList);
		return R.ok();
	}
	
	//UB转储上架-标签查询
	@CrossOrigin
	@RequestMapping("/ub/label")
	public R queryUbLabel(@RequestBody Map<String,Object> params) {
		String LABEL_NO = (String) params.get("LABEL_NO");
		if(StringUtils.isBlank(LABEL_NO)) {
			return R.error("标签不能为空");
		}
		
		Map<String,Object> temp = new HashMap<String, Object>();
		temp.put("LABEL_NO", LABEL_NO);
		List<Map<String,Object>> labelList =  inBoundDAO.queryLabel(temp);
		if(labelList == null || labelList.size() == 0) {
			return R.error("标签数据不存在");
		}
		String LABEL_STATUS = labelList.get(0).get("LABEL_STATUS").toString();
		if(!"00".equals(LABEL_STATUS) && !"03".equals(LABEL_STATUS)) {
			return R.error("标签状态不是收货或待进仓状态");
		}
		//根据标签获取进仓单
		temp.clear();
		temp.put("INBOUND_NO", labelList.get(0).get("INBOUND_NO"));
		temp.put("INBOUND_ITEM_NO", labelList.get(0).get("INBOUND_ITEM_NO"));
		temp.put("LABEL_NO", labelList.get(0).get("LABEL_NO"));
		List<Map<String,Object>> inBoundList =  inBoundDAO.queryInInbound(temp);
		if(inBoundList == null || inBoundList.size() == 0) {
			return R.error("进仓单不存在");
		}
		String ITEM_STATUS = inBoundList.get(0).get("ITEM_STATUS").toString();
		if(!"00".equals(ITEM_STATUS) && !"01".equals(ITEM_STATUS)) {
			return R.error("条码对应的进仓单不是创建或者部分进仓状态");
		}
		//返回标签和进仓单数据
		labelList.forEach(l-> {
			l.put("PO_NO", inBoundList.get(0).get("PO_NO"));
			l.put("PO_ITEM_NO", inBoundList.get(0).get("PO_ITEM_NO"));
			l.put("LGORT", inBoundList.get(0).get("LGORT"));
			l.put("INBOUND_NO", inBoundList.get(0).get("INBOUND_NO"));
			l.put("INBOUND_ITEM_NO", inBoundList.get(0).get("INBOUND_ITEM_NO"));
		});
		return R.ok().put("data", labelList);
	}
	
	//UB转储上架-库位查询
	@CrossOrigin
	@RequestMapping("/ub/logrt")
	public R lgortFromInbound(@RequestBody Map<String,Object> params) {
		String WERKS = (String) params.get("WERKS");
		String WH_NUMBER = (String) params.get("WH_NUMBER");
		if(StringUtils.isBlank(WERKS) || StringUtils.isBlank(WH_NUMBER)) {
			return R.error(PARAMS_ERROR_MSG);
		}
		List<Map<String,Object>> logrtList =  inBoundDAO.queryLogrtFromInInbound(WERKS, WH_NUMBER);
		return R.ok().put("data", logrtList);
	}
}

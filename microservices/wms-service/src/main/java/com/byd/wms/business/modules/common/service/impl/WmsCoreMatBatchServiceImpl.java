package com.byd.wms.business.modules.common.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.cxf.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.byd.utils.DateUtils;
import com.byd.wms.business.modules.common.dao.CommonDao;
import com.byd.wms.business.modules.common.dao.WmsCoreMatBatchDao;
import com.byd.wms.business.modules.common.entity.WmsCBatchPlanRulesEntity;
import com.byd.wms.business.modules.common.entity.WmsCBatchRulesEntity;
import com.byd.wms.business.modules.common.entity.WmsCoreMatBatchEntity;
import com.byd.wms.business.modules.common.service.WmsCBatchPlanRulesService;
import com.byd.wms.business.modules.common.service.WmsCBatchRulesService;
import com.byd.wms.business.modules.common.service.WmsCoreMatBatchService;

/** 
 * @author 作者 E-mail: peng.tao1@byd.com
 * @version 创建时间：2018年8月21日 上午10:19:22 
 * 类说明 
 */
@Service
public class WmsCoreMatBatchServiceImpl extends ServiceImpl<WmsCoreMatBatchDao, WmsCoreMatBatchEntity> implements WmsCoreMatBatchService {
	static String BATCH_STR="123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	@Autowired
	WmsCBatchRulesService wmscBatchRulesService;
	@Autowired
	WmsCBatchPlanRulesService wmscBatchPlanRulesService;
	@Autowired
	WmsCoreMatBatchDao wmsCoreMatBatchDao;
	@Autowired
	CommonDao commonDao;
	
	@Autowired
	DataSourceTransactionManager dataSourceTransactionManager;
	@Autowired
	TransactionDefinition transactionDefinition;

	@Override
	public void deleteBatch(List<Map<String, Object>> paramsList) {
		wmsCoreMatBatchDao.deleteBatch(paramsList);
	}

	/**
	 * 1 WmsCoreMatBatchEntity中 fBatch有值的，就插入
	 * 2 没有值的就说明是要获取批次 根据matnr  asnno lifnr receiptDate deliveryDate productDate effectDate 查询批次
	 * 3 查询有批次的，说明该条件下存在，就返回该批次；没有的，就根据配置生成批次的字段生成批次
	 * 
	 */
	/**
	 * params传入 BUSINESS_NAME 业务类型名称 , WERKS 工厂,MATNR 物料号 ，LGORT 库位(可选) ，DANGER_FLAG 危化品标识
	 */
	@Override
	//@Transactional(rollbackFor=RuntimeException.class)
	public List<Map<String, Object>> getBatch(List<Map<String, Object>> paramsList) {
		List<Map<String, Object>> retMapList = new ArrayList<Map<String, Object>>();
		SimpleDateFormat ss = new SimpleDateFormat("yyyy-MM-dd");
		String maxNo = null;
		for (Map<String, Object> params : paramsList) {
			String retbatch="";
			Map<String, Object> retMap=new HashMap<String, Object>();
			
			if(params.get("BUSINESS_NAME")==null||"".equals(params.get("BUSINESS_NAME"))){
				retMap.put("MSG", "业务类型名称不能为空!");
				retMapList.add(retMap);
				return retMapList;
			}
			if(params.get("WERKS")==null||"".equals(params.get("WERKS"))){
				retMap.put("MSG", "工厂不能为空!");
				retMapList.add(retMap);
				return retMapList;
			}
			if(params.get("DANGER_FLAG")==null||"".equals(params.get("DANGER_FLAG"))){
				retMap.put("MSG", "危化品不能为空!");
				retMapList.add(retMap);
				return retMapList;
			}
			//查找批次规则
			WmsCBatchPlanRulesEntity wmscbatchplanrulesbean=new WmsCBatchPlanRulesEntity();
			wmscbatchplanrulesbean.setBusinessName(params.get("BUSINESS_NAME").toString());
			wmscbatchplanrulesbean.setWerks(params.get("WERKS").toString());
			wmscbatchplanrulesbean.setDangerFlag(params.get("DANGER_FLAG")==null?"":params.get("DANGER_FLAG").toString());
			//WMS_C_BATCH_PLAN_RULES  传入 工厂，库位 ，危化品标识
			List<WmsCBatchPlanRulesEntity> retplanruleslist=new ArrayList<WmsCBatchPlanRulesEntity>();
			
			if(params.get("LGORT")!=null){//有库位条件
				retplanruleslist=wmscBatchPlanRulesService.selectList(new EntityWrapper<WmsCBatchPlanRulesEntity>().eq("WERKS", params.get("WERKS"))
						.eq("BUSINESS_NAME", params.get("BUSINESS_NAME"))
		    			.eq("DANGER_FLAG", params.get("DANGER_FLAG")).eq("LGORT", params.get("LGORT")).eq("DEL","0"));
				if(retplanruleslist.size()==0){//有传库位，查询后没结果的，则去掉库位再查询
					retplanruleslist=wmscBatchPlanRulesService.selectList(new EntityWrapper<WmsCBatchPlanRulesEntity>().eq("WERKS", params.get("WERKS"))
							.eq("BUSINESS_NAME", params.get("BUSINESS_NAME")).eq("DANGER_FLAG", params.get("DANGER_FLAG")).eq("DEL","0"));
				}
			}else{//没有库位条件
				retplanruleslist=wmscBatchPlanRulesService.selectList(new EntityWrapper<WmsCBatchPlanRulesEntity>().eq("WERKS", params.get("WERKS"))
						.eq("BUSINESS_NAME", params.get("BUSINESS_NAME")).eq("DANGER_FLAG", params.get("DANGER_FLAG")).eq("DEL","0"));
			}
			if(retplanruleslist.size()==0){
				retMap.put("MSG", "无法产生料号"+params.get("MATNR")+"的批次信息，该工厂对应的业务类型未维护批次生成规则!");
				retMapList.add(retMap);
				return retMapList;
			}
			//找到 批次规则代码
			String batchrulecode=retplanruleslist.get(0).getBatchRuleCode();
			String fBatchFlag = retplanruleslist.get(0).getfBatchFlag();// 是否沿用源批次
			
			//Map materialMap = commonDao.getMaterialInfo(params.get("WERKS").toString(), params.get("MATNR")==null?"":params.get("MATNR").toString());
		   if(params.get("F_BATCH")==null||"".equals(params.get("F_BATCH")) || !fBatchFlag.equals("X") 
				   ){//不存在源批次的，或者不沿用源批次的，获取收货工厂物料未启用批次规则，产生新批次  || (materialMap !=null && (materialMap.get("XCHPF")==null || "".equals(materialMap.get("XCHPF") ))) 需求变更

				List<WmsCBatchRulesEntity> retruleslist=wmscBatchRulesService.selectList(new EntityWrapper<WmsCBatchRulesEntity>().eq("BATCH_RULE_CODE", batchrulecode)
		    			.eq("DEL","0"));
				if(retruleslist.size()>0){
					
					String mergeRules=retruleslist.get(0).getMergeRule();//
					String batchRule=retruleslist.get(0).getBatchRule();//产生批次的字段
					String sys=retruleslist.get(0).getSys()==null?null:retruleslist.get(0).getSys();
					int flowcodelen=retruleslist.get(0).getFlowCodeLength();
					//把mergeRules中的字段在输入的条件中，作为条件，在批次流水表 wms_core_mat_batch查询
					if(mergeRules!=null){
						Map<String, Object> condparam=new HashMap<String, Object>();
						String[] condArray=mergeRules.split("\\+");//数据库表wms_c_batch_rules配置的属性
						//验证参数中列表中是否传了对应的参数 
						for(int z=0;z<condArray.length;z++){
							if(!"67".equals(params.get("BUSINESS_NAME"))){//20190925自制进仓 采购订单101 类型不验证规则的字段是否有值
							if(params.get(condArray[z])==null){
								retMap.put("MSG", "规则规则("+batchrulecode+")的批次合并规则:"+mergeRules+"中的参数"+condArray[z]+"没找到!");
								retMapList.add(retMap);
								return retMapList;
							}
							if(params.get(condArray[z]).equals("")){
								retMap.put("MSG", "规则规则("+batchrulecode+")的批次合并规则:"+mergeRules+"中的参数"+condArray[z]+"未维护值!");
								retMapList.add(retMap);
								return retMapList;
							}
							}
						}
						//
						for (String key : params.keySet()) {
			    			for(int i=0;i<condArray.length;i++){
				    			if(key.equals(condArray[i])){//批次规则代码 中配置的属性名称 与传入的相同
				    				condparam.put(condArray[i], params.get(key));
				    			}
			    			}
						}
						//2019-07-01优化，查找已存在批次时，需增加工厂代码条件，防止工厂间批次重复 ---通过修改批次合并规则新增WERKS实现
						
						//按照配置的规则，找出在批次流水表中的记录 WMS_CORE_MAT_BATCH
						List<Map<String,Object>> wmscorematbatchlistCond=wmsCoreMatBatchDao.selectCoreMatBatchList(condparam);
						//存在就 返回批次流水表中的批次 WMS_CORE_MAT_BATCH
						//不存在，就插入
						if(wmscorematbatchlistCond.size()>0){//存在
							retMap.put("MSG", "success");
							retMap.put("BATCH", wmscorematbatchlistCond.get(0).get("BATCH"));
						}else{//不存在 则插入
							//生产新的批次
							String batchdate=params.get(batchRule)==null?"":params.get(batchRule).toString();//规则日期  比如 2018-08-22
							if("".equals(batchdate)||batchdate.length()<10){//查看规则日期 是否有值，并且长度是否正确
								retMap.put("MSG", "参数"+batchRule+"的值不正确，格式应该为yyyy-MM-dd !");
								retMapList.add(retMap);
								return retMapList;
							}
							String tempBatch=String.valueOf(batchdate.charAt(2)).concat(String.valueOf(batchdate.charAt(3)))
									.concat(String.valueOf(batchdate.charAt(5))).concat(String.valueOf(batchdate.charAt(6)))
									.concat(String.valueOf(batchdate.charAt(8))).concat(String.valueOf(batchdate.charAt(9)));
							Map<String,Object> batchParams = new HashMap<String,Object>();
							//batchParams.put("WERKS", params.get("WERKS").toString());
							if(StringUtils.isEmpty(sys)) {
								batchParams.put("BATCH_DATE", tempBatch);
								batchParams.put("SYS_COUNT", 0);
							}else {
								batchParams.put("BATCH_DATE", sys.concat(tempBatch));
								batchParams.put("SYS_COUNT", 1);
							}
							
							
							batchParams.put("BATCH_RULE_CODE", batchrulecode);
							maxNo = wmsCoreMatBatchDao.getMaxBatch(batchParams);
							String newBatchNo = "";
							if(null != maxNo) {
								char[] charArry = maxNo.toCharArray();
								boolean b = false;
								for(int i=charArry.length-1;i>=0;i--) {
									if(!String.valueOf(charArry[i]).equals("Z")) {
										//使用下一位替换
										charArry[i] = BATCH_STR.charAt(BATCH_STR.indexOf(charArry[i])+1);
										for(int j = i+1;j<flowcodelen;j++) {
											charArry[j] = '0';
										}
										b = true;
										break;
									}else {
										continue;
									}
									
								}
								if(!b) {
									retMap.put("MSG", "流水位数不够，无法产生新的批次!");
									retMapList.add(retMap);
									return retMapList;
								}
								newBatchNo = String.valueOf(charArry);;
							}else {
								newBatchNo = pixStrZero("1",flowcodelen);
							}
							if(StringUtils.isEmpty(sys)) {
								retbatch = tempBatch.concat(newBatchNo);
							}else {
								retbatch = sys.concat(tempBatch).concat(newBatchNo);
							}
							retMap.put("MSG", "success");
							retMap.put("BATCH", retbatch);
							//再插入到批次流水表WMS_CORE_MAT_BATCH
							condparam.put("EFFECT_DATE", params.get("EFFECT_DATE"));
							condparam.put("BATCH", retbatch);
							condparam.put("BATCH", retbatch);
							condparam.put("GENERATE_DATE", DateUtils.format(new Date(), DateUtils.DATE_TIME_PATTERN));
							TransactionStatus transactionStatus = dataSourceTransactionManager.getTransaction(transactionDefinition);
							try {
								wmsCoreMatBatchDao.insertCoreMatBatch(condparam);
								dataSourceTransactionManager.commit(transactionStatus);//提交
							} catch(Exception e) {
								dataSourceTransactionManager.rollback(transactionStatus);
								e.printStackTrace();
							} 
						}
					}
				}
			}else{//存在源批次的
				
				Map<String, Object> condparam=new HashMap<String, Object>();
				for (String key : params.keySet()) {
					condparam.put(key, params.get(key));
					
				}
				condparam.put("RECEIPT_DATE", ss.format(new Date()));
				condparam.put("GENERATE_DATE", ss.format(new Date()));
				condparam.put("BATCH", params.get("F_BATCH"));
				condparam.put("EFFECT_DATE", params.get("EFFECT_DATE"));
				TransactionStatus transactionStatus = dataSourceTransactionManager.getTransaction(transactionDefinition);
				try {
					wmsCoreMatBatchDao.insertCoreMatBatch(condparam);
					dataSourceTransactionManager.commit(transactionStatus);//提交
				} catch(Exception e) {
					dataSourceTransactionManager.rollback(transactionStatus);
					e.printStackTrace();
				} 
				retMap.put("BATCH", params.get("F_BATCH"));
				retMap.put("MSG", "success");
			}
			
		   retMapList.add(retMap);
		}
		
		return retMapList;
	}
	
	public String getRandom(int len){
		String str="ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		StringBuilder sb=new StringBuilder(len);
		for(int i=0;i<len;i++)
		{
			char ch=str.charAt(new Random().nextInt(str.length()));
			sb.append(ch);
		}
		return sb.toString();
	}
	protected String pixStrZero(String str,int length) {
		int pxnum = length-str.length();
		if(pxnum >0) str = String.format("%0"+(pxnum)+"d", 0) + str;    
		if(pxnum <0) str = str.substring(0, length);    
		return str;
	}

}

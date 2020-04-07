package com.byd.sap.service.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.byd.sap.bapi.MATERIAL_STOCK_READ;
import com.byd.sap.bapi.bean.MaterialStockReadBean;
import com.byd.sap.modules.job.dao.ISapSyncDao;
import com.byd.sap.modules.job.entity.SapMaterialEntity;
import com.byd.sap.modules.job.entity.SapMaterialUnitEntity;
import com.byd.sap.modules.job.entity.SapPoAccountEntity;
import com.byd.sap.modules.job.entity.SapPoHeadEntity;
import com.byd.sap.modules.job.entity.SapPoItemEntity;
import com.byd.sap.modules.job.entity.ScheduleJobLogEntity;
import com.byd.sap.rfc.SAPBapi;
import com.byd.sap.service.IwmsSapService;
import com.sap.conn.jco.JCoContext;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoStructure;
import com.sap.conn.jco.JCoTable;

@Service("wmsSapService")
public class WmsSapServiceImpl implements IwmsSapService {
	private static final Logger log = LoggerFactory.getLogger("WmsSapServiceImpl");
	@Autowired
	private SAPBapi sapBapi;
	@Autowired
	private ISapSyncDao sapSyncDao;
	@Autowired
	private MATERIAL_STOCK_READ materialStockRead;
	
	@Override
	public synchronized List<JCoTable> getSapJcoData(String prepareFunName, String readFunName, String deleteFunName,
			String paraTableName, List<Map<String,String>> prepareDataList, Map<String, String> prepareInputMap,
			Map<String, String> readParamMap, List<String> outParamList,List<Integer> checkList){		
		List<JCoTable> outputList = new ArrayList<JCoTable>();
		try {
			outputList = doSapJcoData(prepareFunName, readFunName, deleteFunName, paraTableName, 
					prepareDataList, prepareInputMap, readParamMap, outParamList,checkList);
		} catch (Exception e) {
			log.info("---->getSapJcoData execute ERR:" + e.getMessage() + ";20秒后第一次重试");
			try {
				Thread.sleep(20000);
				outputList = doSapJcoData(prepareFunName, readFunName, deleteFunName, paraTableName, 
						prepareDataList, prepareInputMap, readParamMap, outParamList,checkList);
			} catch (Exception e1) {
				log.info("---->getSapJcoData execute ERR:" + e1.getMessage() + ";30秒后第二次重试");
				try {
					Thread.sleep(30000);
					outputList = doSapJcoData(prepareFunName, readFunName, deleteFunName, paraTableName, 
							prepareDataList, prepareInputMap, readParamMap, outParamList,checkList);
				} catch (Exception e2) {
					log.info("---->getSapJcoData execute ERR:" + e2.getMessage() + ";尝试三次失败 结束");
				}
			}
		}
		return outputList;
	}

	@Override
	public JCoTable getSapBapiData(String funName,Map<String,String> paramMap,String outputTableName) {
		sapBapi.init();
		JCoFunction function = null;  
	    JCoDestination destination = sapBapi.connect();  
	    JCoTable outputTable = null;
	    try { 
	    	function = destination.getRepository().getFunction(funName);  
            JCoParameterList input = function.getImportParameterList();  
            
			Iterator<Map.Entry<String,String>> entries = paramMap.entrySet().iterator();
			while (entries.hasNext()) {
				Map.Entry<String,String> entry = entries.next();
				input.setValue(entry.getKey(), entry.getValue()); 
			}
			
			function.execute(destination);   
			
			outputTable = function.getTableParameterList().getTable(outputTableName);
	    }catch (Exception e) {  
            e.printStackTrace();  
        }
		return outputTable;
	}
	
	protected List<JCoTable> doSapJcoData(String prepareFunName, String readFunName, String deleteFunName,
			String paraTableName, List<Map<String,String>> prepareDataList, Map<String, String> prepareInputMap,
			Map<String, String> readParamMap, List<String> outParamList,List<Integer> checkList) throws Exception {
		sapBapi.init();
		JCoDestination destination = sapBapi.connect();
		JCoFunction prepare = null;
		JCoTable prepareData = null;		
		JCoFunction read = null;
		List<JCoTable> outputList = new ArrayList<JCoTable>();		
		JCoFunction delete = null;
		
		JCoContext.begin(destination); 
		
		try {
			prepare = destination.getRepository().getFunction(prepareFunName);
			prepareData = prepare.getTableParameterList().getTable(paraTableName);
			prepareData.appendRows(prepareDataList.size());
						
			for(int i=0;i<prepareDataList.size();i++) {
				prepareData.setRow(i);
				Iterator<String> iter = prepareDataList.get(i).keySet().iterator();
				while (iter.hasNext()) {
				    String key = iter.next();				
					prepareData.setValue(key,prepareDataList.get(i).get(key));
				}
			}
			
			JCoParameterList prepareInput = prepare.getImportParameterList();	
			Iterator<Map.Entry<String,String>> entries = prepareInputMap.entrySet().iterator();	
			while (entries.hasNext()) {
				Map.Entry<String,String> entry = entries.next();
				prepareInput.setValue(entry.getKey(),entry.getValue());
			}
			
			read = destination.getRepository().getFunction(readFunName);
			entries = readParamMap.entrySet().iterator();	
			while (entries.hasNext()) {
				Map.Entry<String,String> entry = entries.next();
				read.getImportParameterList().setValue(entry.getKey(),entry.getValue());
			}
			
			for(int i=0;i<outParamList.size();i++) {
				JCoTable output = read.getTableParameterList().getTable(outParamList.get(i));
				outputList.add(output);
			}
			
			delete = destination.getRepository().getFunction(deleteFunName);
			
			prepare.execute(destination); //sap.execute(prepare);
            String status = prepare.getExportParameterList().getString("RETURN") + "";
            log.info("-->doSapJcoData PREPARE status == " + status.trim().toString());
            
            if (status == null || (!checkStats(status.trim(),checkList))) {
            	JCoContext.end(destination);
                throw new Exception("准备数据失败");
            }
            
            Thread.sleep(20000);
			
		}catch (Exception ex) {
        	log.info("-->prepare ERR = " + ex.getMessage());
        	JCoContext.end(destination);
        	throw new Exception("准备数据失败");
        }
		
		try {
			read.execute(destination);
			String status = read.getExportParameterList().getString("RETURN");
			log.info("-->doSapJcoData READ status = " + status);

			if (status == null || (!checkStats(status.trim(),checkList))) {
				JCoContext.end(destination);
				throw new Exception("读取数据失败");
			}
		} catch (Exception ex) {
			log.info("-->READ ERR = " + ex.getMessage());
			JCoContext.end(destination);
			throw new Exception("读取数据失败");
		}
		try {
			delete.execute(destination);
		} catch (Exception ex) {
			log.info(ex.toString());
			JCoContext.end(destination);
		}
		JCoContext.end(destination);
		
		return outputList;
	}
	
	public static boolean checkStats(String status,List<Integer> checkList) {
		boolean result = true;
		if(checkList == null)return result;
		for(int i=0;i<checkList.size();i++) {
			if(status.substring(checkList.get(i)-1, checkList.get(i)).equals("N")) {
				result = false;
			}
		}
		return result;
	}
	
	@Override
	@Transactional
	public int syncSapVendorData(JCoTable output1) {
		Date dNow = new Date();
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 	//设置时间格式
		String IMPORT_DATE = sdf.format(dNow);
		if(output1 != null){
			int rowCount = output1.getNumRows();
			for (int i = 0; i < rowCount; i++) {
				output1.setRow(i);
				String LIFNR = output1.getString("LIFNR").replaceFirst("^0*", "");
				String BUKRS = output1.getString("BUKRS");
				Map<String,Object> queryMap=new HashMap<String,Object>();
				queryMap.put("LIFNR", LIFNR);
				queryMap.put("BUKRS", BUKRS);
				String v_id = sapSyncDao.get_VENDOR_id(queryMap);
				
				String WERKS = "";
				String NAME1 = output1.getString("NAME1");
				String NAME2 = output1.getString("NAME2");
				String EDITOR_ID = "";
				String EDITOR = "";
				String EDIT_DATE = "";
				String VERIFY = "";
				String STRAS = output1.getString("STRAS");
				String LOEVM = output1.getString("LOEVM");
				String REMARK = output1.getString("REMARK");
				String TELFX = output1.getString("TELFX");
				String LAND1T = output1.getString("LAND1T");
				String PSTLZ = output1.getString("PSTLZ");
				String IS_SCM = "";
				String VENDOR_MANAGER = "";
				String SHORT_NAME = "";
				
				queryMap.put("ID", v_id);
				queryMap.put("WERKS", WERKS);
				queryMap.put("LIFNR", LIFNR);
				queryMap.put("NAME1", NAME1);
				queryMap.put("NAME2", NAME2);
				queryMap.put("EDITOR_ID", EDITOR_ID);
				queryMap.put("EDITOR", EDITOR);
				queryMap.put("EDIT_DATE", EDIT_DATE);
				queryMap.put("VERIFY", VERIFY);
				queryMap.put("STRAS", STRAS);
				queryMap.put("LOEVM", LOEVM);
				queryMap.put("REMARK", REMARK);
				queryMap.put("TELFX", TELFX);
				queryMap.put("LAND1T", LAND1T);
				queryMap.put("PSTLZ", PSTLZ);
				queryMap.put("IS_SCM", IS_SCM);
				queryMap.put("VENDOR_MANAGER", VENDOR_MANAGER);
				queryMap.put("SHORT_NAME", SHORT_NAME);
				queryMap.put("IMPORT_DATE", IMPORT_DATE);
				
				if(v_id == null){
					log.info("-->syncSap_VENDOR_Service INSERT: v_id = new");
					sapSyncDao.insert_VENDOR(queryMap);
				}else{
					log.info("-->syncSap_VENDOR_Service UPDATE: v_id = " + v_id);
					sapSyncDao.update_VENDOR(queryMap);
				}
			}
		}
		return 0;
	}
	
	@Override
	public Map<String,Object> syncSapCustomerData(String params){
		sapBapi.init();
		JCoFunction function = null;  
	    JCoDestination destination = sapBapi.connect();
	    Map<String,Object> returnMap = new HashMap<String,Object>();
	    try {
	    	function = destination.getRepository().getFunction("ZGSD_CRM_001_01");  
            JCoParameterList input = function.getTableParameterList(); 
            
    		int days = 5;
    		//int recordNum = 1000;
    		String[] plist = params.split(",");
    		for (int i=0;i<plist.length;i++) {
    			if(plist[i].indexOf("days")>=0) {
    				days = Integer.valueOf(plist[i].substring(plist[i].indexOf("=")+1, plist[i].length()));
    			}
    		}
    		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
    		Date dd = new Date();
    		String DATE_TO = df.format(dd);
    		String DATE_FROM = df.format(dd.getTime() - days * 24 * 60 * 60 * 1000);
            
            //I_ERDAT
            JCoTable struct = input.getTable("I_ERDAT");
            
            struct.appendRows(1);
            struct.setRow(0);
            struct.setValue("LOW", DATE_FROM); 
            struct.setValue("HIGH", DATE_TO); 
            struct.setValue("OPTION", "BT"); 
            struct.setValue("SIGN", "I"); 
            function.execute(destination); 
            
            JCoTable result = input.getTable("T_RESULT");
            System.out.println("-->NumRows = " + result.getNumRows());
            if(result.getNumRows()>0) {
            	this.saveSapCustomerData(result);
            }
	    }catch (Exception e) {  
            e.printStackTrace(); 
            returnMap.put("CODE", "-1");
            returnMap.put("MESSAGE", e.getMessage());
        }
	    returnMap.put("CODE", "0");
        returnMap.put("MESSAGE", "SCCESS");
	    return returnMap;
	    
	}
	
	/**
	 * 保存同步的客户信息到数据库
	 * @param output1
	 * @return
	 */
	@Transactional
	private int saveSapCustomerData(JCoTable output1) {
		Date dNow = new Date();
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 	//设置时间格式
		String IMPORT_DATE = sdf.format(dNow);
		if(output1 != null){
			int rowCount = output1.getNumRows();
			
			List<Map<String,Object>> list = new ArrayList<>();
			
			for (int i = 0; i < rowCount; i++) {
				output1.setRow(i);
				
				Map<String,Object> map = new HashMap<String,Object>();
				
				String KUNNR = output1.getString("KUNNR").replaceFirst("^0*", ""); //客户代码
				String NAME1 = output1.getString("NAME1");//客户名称
				String SORTL = output1.getString("SORTL");//简称
				String BUKRS = output1.getString("BUKRS");//公司代码
				String VKORG = output1.getString("VKORG");//销售组织
				String VTWEG = output1.getString("VTWEG");//分销渠道
				String SPART = output1.getString("SPART");//部门
				String ADDRESS = output1.getString("SMTP_ADDR");//地址
				
				map.put("KUNNR", KUNNR);
				map.put("NAME1", NAME1);
				map.put("SORTL", SORTL);
				map.put("BUKRS", BUKRS);
				map.put("VKORG", VKORG);
				map.put("VTWEG", VTWEG);
				map.put("SPART", SPART);
				map.put("ADDRESS", ADDRESS);
				map.put("IMPORT_DATE", IMPORT_DATE);
				
				list.add(map);
			}
			
			sapSyncDao.addOrUpdateCustomerInfo(list);
		}
		
		return 0;
	}
	
	@Override
	@Transactional
	public int syncSapMoData(JCoTable output1,JCoTable output2,JCoTable output3) {
		Date dNow = new Date();
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 	//设置时间格式
		String IMPORT_DATE = sdf.format(dNow);
		
		// 更新MO
		if(output1 != null){
			int rowCount = output1.getNumRows();
			for (int i = 0; i < rowCount; i++) {
				output1.setRow(i);
				String AUFNR = output1.getString("AUFNR").replaceFirst("^0*", "");
				String MO_ID = sapSyncDao.get_SapMoHead_id(AUFNR);
				Map<String,Object> moHeadQueryMap=new HashMap<String,Object>();
				moHeadQueryMap.put("ID", MO_ID);
				moHeadQueryMap.put("AUART", output1.getString("AUART"));
				moHeadQueryMap.put("AUFNR", AUFNR);
				moHeadQueryMap.put("WERKS", output1.getString("WERKS"));
				moHeadQueryMap.put("MATNR", output2.getString("MATNR"));
				moHeadQueryMap.put("MAKTX", output2.getString("MAKTX"));
				moHeadQueryMap.put("ISTAT_TXT", output1.getString("ISTAT_TXT"));
				moHeadQueryMap.put("IMPORT_DATE", IMPORT_DATE);
				moHeadQueryMap.put("UPDATE_DATE", IMPORT_DATE);
				if(MO_ID == null){
					sapSyncDao.insert_SapMoHead(moHeadQueryMap);
				}else{
					sapSyncDao.update_SapMoHead(moHeadQueryMap);
				}
			}
			rowCount = output2.getNumRows();
			List<Map<String,Object>> matList = new ArrayList<Map<String,Object>>();
			for (int i = 0; i < rowCount; i++) {
				output2.setRow(i);
				String AUFNR = output2.getString("AUFNR").replaceFirst("^0*", "");
				String POSNR = output2.getString("POSNR");
				//String MOHEAD_ID = sapSyncDao.get_SapMoItem_id(AUFNR,POSNR);
				Map<String,Object> moItemQueryMap=new HashMap<String,Object>();
				//moItemQueryMap.put("ID", MOHEAD_ID);
				moItemQueryMap.put("POSNR", POSNR);
				moItemQueryMap.put("AUFNR", AUFNR);
				moItemQueryMap.put("MATNR", output2.getString("MATNR"));
				moItemQueryMap.put("MAKTX", output2.getString("MAKTX"));
				moItemQueryMap.put("PSMNG", output2.getString("PSMNG"));
				moItemQueryMap.put("MEINS", output2.getString("MEINS"));
//				moItemQueryMap.put("KDAUF", output2.getString("KDAUF"));//销售订单号
//				moItemQueryMap.put("KDPOS", output2.getString("KDPOS"));//销售订单行项目号
				moItemQueryMap.put("IMPORT_DATE", IMPORT_DATE);
				moItemQueryMap.put("UPDATE_DATE", IMPORT_DATE);
				/*if(MOHEAD_ID == null){
					sapSyncDao.insert_SapMoItem(moItemQueryMap);
				}else{
					sapSyncDao.update_SapMoItem(moItemQueryMap);
				}*/
				matList.add(moItemQueryMap);
			}
			int pointsDataLimit = 2000;//限制条数
			Integer size = matList.size();
			//判断是否有必要分批
			if(pointsDataLimit<size){
				int part = size/pointsDataLimit;//分批数
				System.out.println("共有 ： "+size+"条，！"+" 分为 ："+part+"批  insertOrUpdateSapMoItem");
				for (int i = 0; i < part; i++) {
					//1000条
					List<Map<String,Object>> listPage = matList.subList(0, pointsDataLimit);
					//System.out.println(listPage);
					sapSyncDao.insertOrUpdateSapMoItem(listPage);
					//剔除
					matList.subList(0, pointsDataLimit).clear();
				}
				if(!matList.isEmpty()){//表示最后剩下的数据
					sapSyncDao.insertOrUpdateSapMoItem(matList);
				}
			}else {
				sapSyncDao.insertOrUpdateSapMoItem(matList);
			}
			
			rowCount = output3.getNumRows();
			List<Map<String,Object>> moList = new ArrayList<Map<String,Object>>();
			for (int i = 0; i < rowCount; i++) {
				output3.setRow(i);
				String BWART = output3.getString("BWART");
				if(BWART !=null && (BWART.equals("261")||BWART.equals("531"))) {
					Map<String,Object> moComQueryMap=new HashMap<String,Object>();
					moComQueryMap.put("AUFNR", removeZeroStr(output3.getString("AUFNR")));
					moComQueryMap.put("MATNR", output3.getString("MATNR"));
					moComQueryMap.put("RSNUM", removeZeroStr(output3.getString("RSNUM")));
					moComQueryMap.put("RSPOS", output3.getString("RSPOS"));
					//String COM_ID = sapSyncDao.get_SapMoComponent_id(moComQueryMap);
					//moComQueryMap.put("ID", COM_ID);
					moComQueryMap.put("POSNR", output3.getString("POSNR"));
					moComQueryMap.put("XLOEK", output3.getString("XLOEK"));
					moComQueryMap.put("WERKS", output3.getString("WERKS"));
					moComQueryMap.put("LGORT", output3.getString("LGORT"));
					moComQueryMap.put("CHARG", output3.getString("CHARG"));
					moComQueryMap.put("BDTER", output3.getString("BDTER"));
					moComQueryMap.put("BDMNG", output3.getString("BDMNG"));
					moComQueryMap.put("MEINS", output3.getString("MEINS"));
					moComQueryMap.put("BWART", output3.getString("BWART"));
					moComQueryMap.put("POSTP", output3.getString("POSTP"));
					moComQueryMap.put("SORTF", output3.getString("SORTF"));
					moComQueryMap.put("RGEKZ", output3.getString("RGEKZ"));
					moComQueryMap.put("DUMPS", output3.getString("DUMPS"));
					moComQueryMap.put("KZKUP", output3.getString("KZKUP"));
					moComQueryMap.put("IMPORT_DATE", IMPORT_DATE);
					moComQueryMap.put("IMPORT_PEOPLE", "WMS");
					moComQueryMap.put("UPDATE_DATE", IMPORT_DATE);
					moComQueryMap.put("UPDATE_PEOPLE", "WMS");
					moComQueryMap.put("VERIFY", "");
					/*if(COM_ID == null){
						sapSyncDao.insert_SapMoComponent(moComQueryMap);
					}else {
						sapSyncDao.update_SapMoComponent(moComQueryMap);
					}*/
					moList.add(moComQueryMap);
				}
			}
			size = moList.size();
			//判断是否有必要分批
			if(pointsDataLimit<size){
				int part = size/pointsDataLimit;//分批数
				System.out.println("共有 ： "+size+"条，！"+" 分为 ："+part+"批  insertOrUpdateSapMoComponent");
				for (int i = 0; i < part; i++) {
					//1000条
					List<Map<String,Object>> listPage = moList.subList(0, pointsDataLimit);
					//System.out.println(listPage);
					sapSyncDao.insertOrUpdateSapMoComponent(listPage);
					//剔除
					moList.subList(0, pointsDataLimit).clear();
				}
				if(!moList.isEmpty()){//表示最后剩下的数据
					sapSyncDao.insertOrUpdateSapMoComponent(moList);
				}
			}else {
				if(!moList.isEmpty()){
					sapSyncDao.insertOrUpdateSapMoComponent(moList);
				}
			}
		}
		return 0;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public int syncSapMoData(Map<String, Object> queryMap) {
		Date dNow = new Date();
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 	//设置时间格式
		String IMPORT_DATE = sdf.format(dNow);
		
		Map<String,String> headerMap = new HashMap<String,String>();
		headerMap = (Map<String, String>) queryMap.get("headerMap");
		String AUFNR = headerMap.get("AUFNR");
		String MO_ID = sapSyncDao.get_SapMoHead_id(AUFNR);
		Map<String,Object> moHeadQueryMap=new HashMap<String,Object>();
		moHeadQueryMap.put("ID", MO_ID);
		moHeadQueryMap.put("AUART", headerMap.get("AUFART"));//生产订单类型
		moHeadQueryMap.put("AUFNR", headerMap.get("AUFNR"));//生产订单号
		moHeadQueryMap.put("WERKS", headerMap.get("WERKS_D"));//生产工厂
		moHeadQueryMap.put("MATNR", headerMap.get("MATNR"));//料号
		moHeadQueryMap.put("MAKTX", headerMap.get("CO_MATXT"));
		moHeadQueryMap.put("ISTAT_TXT", headerMap.get("CO_STTXT"));//订单状态
		moHeadQueryMap.put("IMPORT_DATE", IMPORT_DATE);
		moHeadQueryMap.put("UPDATE_DATE", IMPORT_DATE);
		
		if(MO_ID == null){
			sapSyncDao.insert_SapMoHead(moHeadQueryMap);
		}else{
			sapSyncDao.update_SapMoHead(moHeadQueryMap);
		}
		//行项目
		List<Map<String,String>> positionList = new ArrayList<Map<String,String>>();
		List<Map<String,Object>> matList = new ArrayList<Map<String,Object>>();
		positionList = (List<Map<String,String>>) queryMap.get("positionList");
		for (Map<String, String> itemMap : positionList) {
			String CO_POSNR = itemMap.get("CO_POSNR");//生产行项目号
			//String ITEM_ID = sapSyncDao.get_SapMoItem_id(AUFNR,CO_POSNR);
			Map<String,Object> moItemQueryMap=new HashMap<String,Object>();
			//moItemQueryMap.put("ID", ITEM_ID);
			moItemQueryMap.put("AUART", itemMap.get("AUFART"));//生产订单类型
			moItemQueryMap.put("POSNR", CO_POSNR);
			moItemQueryMap.put("AUFNR", itemMap.get("AUFNR"));//生产订单号
			moItemQueryMap.put("WERKS", itemMap.get("WERKS_D"));//生产工厂
			moItemQueryMap.put("MATNR", itemMap.get("CO_MATNR"));//料号
			moItemQueryMap.put("MAKTX", itemMap.get("CO_MATXT"));//物料描述
			moItemQueryMap.put("PSMNG", itemMap.get("CO_PSMNG"));//生产数量
			moItemQueryMap.put("WEMNG", itemMap.get("CO_WEMNG"));//此订单项的收货数量 已收货数量 
			moItemQueryMap.put("MEINS", itemMap.get("LAGME"));//基本单位
			moItemQueryMap.put("KDAUF", itemMap.get("KDAUF"));//销售订单号
			moItemQueryMap.put("KDPOS", itemMap.get("KDPOS"));//销售订单行项目号
			moItemQueryMap.put("LGORT", itemMap.get("LGORT_D"));//库存地点
			moItemQueryMap.put("ELIKZ", itemMap.get("ELIKZ"));//"交货已完成"标识
			moItemQueryMap.put("CHARG", itemMap.get("CHARG_D"));//批号
			moItemQueryMap.put("AUFLOEKZ", itemMap.get("AUFLOEKZ"));//删除标识
			moItemQueryMap.put("IMPORT_DATE", IMPORT_DATE);
			moItemQueryMap.put("UPDATE_DATE", IMPORT_DATE);
			
			/*if(MO_ID == null){
				sapSyncDao.insert_SapMoItem(moItemQueryMap);
			}else{
				sapSyncDao.update_SapMoItem(moItemQueryMap);
			}*/
			matList.add(moItemQueryMap);
		}
		//sapSyncDao.insertOrUpdateSapMoItem(matList);
		int pointsDataLimit = 2000;//限制条数
		Integer size = matList.size();
		//判断是否有必要分批
		if(pointsDataLimit<size){
			int part = size/pointsDataLimit;//分批数
			System.out.println("共有 ： "+size+"条，！"+" 分为 ："+part+"批  insertOrUpdateSapMoItem");
			for (int i = 0; i < part; i++) {
				//1000条
				List<Map<String,Object>> listPage = matList.subList(0, pointsDataLimit);
				//System.out.println(listPage);
				sapSyncDao.insertOrUpdateSapMoItem(listPage);
				//剔除
				matList.subList(0, pointsDataLimit).clear();
			}
			if(!matList.isEmpty()){//表示最后剩下的数据
				sapSyncDao.insertOrUpdateSapMoItem(matList);
			}
		}else {
			sapSyncDao.insertOrUpdateSapMoItem(matList);
		}
		
		//returnMap.put("componentList", componentList);
		List<Map<String,String>> componentList = new ArrayList<Map<String,String>>();
		componentList = (List<Map<String, String>>) queryMap.get("componentList");
		List<Map<String,Object>> moList = new ArrayList<Map<String,Object>>();
		for(int i=0;i<componentList.size();i++) {
			Map<String,String> component = componentList.get(i);
			String BWART = component.get("BWART");
			if(BWART!=null && (BWART.equals("261")||BWART.equals("531"))) {
				Map<String,Object> moComQueryMap=new HashMap<String,Object>();
				moComQueryMap.put("AUFNR", component.get("AUFNR"));
				moComQueryMap.put("MATNR", component.get("MATNR"));
				moComQueryMap.put("RSNUM", component.get("RSNUM"));
				moComQueryMap.put("RSPOS", component.get("RSPOS"));
				//String COM_ID = sapSyncDao.get_SapMoComponent_id(moComQueryMap);
				//moComQueryMap.put("ID", COM_ID);
				moComQueryMap.put("XLOEK", component.get("XLOEK"));
				moComQueryMap.put("WERKS", component.get("WERKS_D"));
				moComQueryMap.put("LGORT", component.get("LGORT_D"));
				moComQueryMap.put("CHARG", component.get("CHARG_D"));
				moComQueryMap.put("BDTER", component.get("BDTER"));
				moComQueryMap.put("BDMNG", component.get("BDMNG"));
				moComQueryMap.put("MEINS", component.get("MEINS"));
				moComQueryMap.put("BWART", component.get("BWART"));
				moComQueryMap.put("POSTP", component.get("POSTP"));
				moComQueryMap.put("SORTF", component.get("SORTF"));
				moComQueryMap.put("RGEKZ", component.get("RGEKZ"));
				moComQueryMap.put("DUMPS", component.get("DUMPS"));
				moComQueryMap.put("KZKUP", component.get("KZKUP"));
				moComQueryMap.put("POSNR", component.get("POSNR"));
				moComQueryMap.put("IMPORT_DATE", IMPORT_DATE);
				moComQueryMap.put("IMPORT_PEOPLE", queryMap.get("WMS_USER"));
				moComQueryMap.put("UPDATE_DATE", IMPORT_DATE);
				moComQueryMap.put("UPDATE_PEOPLE", queryMap.get("WMS_USER"));
				moComQueryMap.put("VERIFY", "");
				/*if(COM_ID == null){
					sapSyncDao.insert_SapMoComponent(moComQueryMap);
				}else {
					sapSyncDao.update_SapMoComponent(moComQueryMap);
				}*/
				moList.add(moComQueryMap);
			}

		}
		size = moList.size();
		//判断是否有必要分批
		if(pointsDataLimit<size){
			int part = size/pointsDataLimit;//分批数
			System.out.println("共有 ： "+size+"条，！"+" 分为 ："+part+"批  insertOrUpdateSapMoComponent");
			for (int i = 0; i < part; i++) {
				//1000条
				List<Map<String,Object>> listPage = moList.subList(0, pointsDataLimit);
				//System.out.println(listPage);
				sapSyncDao.insertOrUpdateSapMoComponent(listPage);
				//剔除
				moList.subList(0, pointsDataLimit).clear();
			}
			if(!moList.isEmpty()){//表示最后剩下的数据
				sapSyncDao.insertOrUpdateSapMoComponent(moList);
			}
		}else {
			sapSyncDao.insertOrUpdateSapMoComponent(moList);
		}
		return 0;
	}
	
	@Override
	@Transactional
	public int syncSapMaterialData(SapMaterialEntity sapMaterialEntity) {
		Map<String,Object> queryMap=new HashMap<String,Object>();
		queryMap.put("MATNR", sapMaterialEntity.getMATNR());
		queryMap.put("WERKS", sapMaterialEntity.getWERKS());
		String v_id = sapSyncDao.get_SapMaterial_id(queryMap);
		if(v_id == null){
			sapSyncDao.insert_SapMaterial(sapMaterialEntity);
		}else {
			sapMaterialEntity.setID(Long.valueOf(v_id));
			sapSyncDao.update_SapMaterial(sapMaterialEntity);
		}
		return 0;
	}
	
	@Override
	@Transactional
	public int syncSapMaterialUnitData(List<SapMaterialUnitEntity> sapMaterialUnitEntityList) {
		for (SapMaterialUnitEntity sapMaterialUnitEntity : sapMaterialUnitEntityList) {
			Map<String,Object> queryMap=new HashMap<String,Object>();
			queryMap.put("MATNR", sapMaterialUnitEntity.getMATNR());
			queryMap.put("MEINH", sapMaterialUnitEntity.getMEINH());
			String v_id = sapSyncDao.get_SapMaterialUnit_id(queryMap);
			if(v_id == null){
				sapSyncDao.insert_SapMaterialUnit(sapMaterialUnitEntity);
			}else {
				sapMaterialUnitEntity.setID(Long.valueOf(v_id));
				sapSyncDao.update_SapMaterialUnit(sapMaterialUnitEntity);
			}
		}
		return 0;
	}
	
	@Override
	public int insert_ScheduleJobLog(ScheduleJobLogEntity scheduleJobLog) {
		return sapSyncDao.insert_ScheduleJobLog(scheduleJobLog);
	}
	
	@Override
	public List<Map<String,String>> getWmsSapCompanyList(Map<String,String> map){
		return sapSyncDao.getWmsSapCompanyList(map);
	}

	@Override
	@Transactional
	public int syncPoAccount(SapPoAccountEntity sappoaccount) {
		Map<String,Object> queryMap=new HashMap<String,Object>();
		queryMap.put("EBELN", sappoaccount.getEBELN());
		queryMap.put("EBELP", sappoaccount.getEBELP());
		String id=sapSyncDao.get_SAPPoAccount_id(queryMap);
		int re=0;
		if(id!=null){
			re=sapSyncDao.update_SapPoAccount(sappoaccount);
		}else{
			re=sapSyncDao.insert_SapPoAccount(sappoaccount);
		}
		return re;
	}

	@Override
	public String get_SAPPoAccount_id(Map<String, Object> queryMap) {
		// TODO Auto-generated method stub
		return sapSyncDao.get_SAPPoAccount_id(queryMap);
	}
	
	@Override
	public Map<String,String> getSapBapiInternalorderDetail(String orderId){
		sapBapi.init();
		JCoFunction function = null;  
	    JCoDestination destination = sapBapi.connect();  
	    JCoParameterList output = null;
	    Map<String,String> returnMap = new HashMap<String,String>();
	    try { 
	    	function = destination.getRepository().getFunction("BAPI_INTERNALORDER_GETDETAIL");  
            JCoParameterList input = function.getImportParameterList();  
            input.setValue("ORDERID", orderId); 
			
			function.execute(destination);  
			output = function.getExportParameterList();
			returnMap.put("MESSAGE", output.getStructure("RETURN").getString("MESSAGE"));
			returnMap.put("CODE", output.getStructure("RETURN").getString("CODE"));
			returnMap.put("ORDER", output.getStructure("MASTER_DATA").getString("ORDER"));
			returnMap.put("ORDER_TYPE", output.getStructure("MASTER_DATA").getString("ORDER_TYPE"));
			returnMap.put("ORDER_NAME", output.getStructure("MASTER_DATA").getString("ORDER_NAME"));
			returnMap.put("COMP_CODE", output.getStructure("MASTER_DATA").getString("COMP_CODE"));
			returnMap.put("PLANT", output.getStructure("MASTER_DATA").getString("PLANT"));
			returnMap.put("STATUS", "");
			if("".equals(output.getStructure("RETURN").getString("MESSAGE"))) {
				JCoTable outputTable  = null;
				outputTable = function.getTableParameterList().getTable(0);
				
				String status = "";
				for(int i=0;i<outputTable.getNumRows();i++) {
					outputTable.setRow(i);//TRAN|DESCRIPT
					status += outputTable.getString(0) + ":" + outputTable.getString(1) + ",";
				}
				status = status.substring(0,status.length()-1);
				System.out.println("-->status = " + status);
				returnMap.put("STATUS", status);
			}
			

	    }catch (Exception e) {  
            e.printStackTrace();  
        }
	    
		return returnMap;
	}
	
	@Override
	public Map<String,Object> getSapBapiProdordDetail(String orderId){
		sapBapi.init();
		JCoFunction function = null;  
		JCoFunction function1 = null;  
	    JCoDestination destination = sapBapi.connect();  
	    JCoTable outputTable = null;
	    Map<String,Object> returnMap = new HashMap<String,Object>();
	    try { 
	    	String WERKS = "";
	    	String RSNUM = null;
	    	function = destination.getRepository().getFunction("BAPI_PRODORD_GET_DETAIL");  
            JCoParameterList input = function.getImportParameterList();  
            input.setValue("NUMBER", orderId); 
			JCoStructure struct = input.getStructure("ORDER_OBJECTS");
            struct.setValue("COMPONENTS","X");
            struct.setValue("HEADER","X");
            struct.setValue("POSITIONS","X");
            
            function.execute(destination);  
            outputTable = function.getTableParameterList().getTable("HEADER");
            if(outputTable.getNumRows()==0) {
            	returnMap.put("CODE", "-1");
                returnMap.put("MESSAGE", "没有查询到数据");
                return returnMap;
            }
            outputTable.setRow(0);
            WERKS = outputTable.getString("PRODUCTION_PLANT");
            Map<String,String> headerMap = new HashMap<String,String>();
            headerMap.put("AUFNR", outputTable.getString("ORDER_NUMBER").replaceAll("^(0+)", ""));//生产订单号
            headerMap.put("WERKS_D", outputTable.getString("PRODUCTION_PLANT"));//生产工厂
            headerMap.put("CO_DISPO", outputTable.getString("MRP_CONTROLLER"));//MRP控制者
            headerMap.put("FEVOR", outputTable.getString("PRODUCTION_SCHEDULER"));
            headerMap.put("MATNR", outputTable.getString("MATERIAL"));//物料号
            headerMap.put("CO_AUFLD", outputTable.getString("EXPL_DATE"));
            headerMap.put("CO_AUFPL", outputTable.getString("ROUTING_NO"));
            headerMap.put("RSNUM", outputTable.getString("RESERVATION_NUMBER").replaceAll("^(0+)", ""));//预留号
            headerMap.put("CO_FTRMS", outputTable.getString("SCHED_RELEASE_DATE"));
            headerMap.put("CO_FTRMI", outputTable.getString("ACTUAL_RELEASE_DATE"));
            headerMap.put("CO_GLTRP", outputTable.getString("FINISH_DATE"));
            headerMap.put("CO_GSTRP", outputTable.getString("START_DATE"));
            headerMap.put("CO_GLTRS", outputTable.getString("PRODUCTION_FINISH_DATE"));
            headerMap.put("CO_GSTRS", outputTable.getString("PRODUCTION_START_DATE"));
            headerMap.put("CO_GSTRI", outputTable.getString("ACTUAL_START_DATE"));
            headerMap.put("CO_GLTRI", outputTable.getString("ACTUAL_FINISH_DATE"));
            headerMap.put("GASMG", outputTable.getString("SCRAP"));
            headerMap.put("GAMNG", outputTable.getString("TARGET_QUANTITY"));
            headerMap.put("CO_GMEIN", outputTable.getString("UNIT"));//单位
            headerMap.put("ISOCD_UNIT", outputTable.getString("UNIT_ISO"));
            headerMap.put("CO_APRIO", outputTable.getString("PRIORITY"));
            headerMap.put("AUFART", outputTable.getString("ORDER_TYPE"));//订单类型
            headerMap.put("AUFERFNAM", outputTable.getString("ENTERED_BY"));
            headerMap.put("AUFERFDAT", outputTable.getString("ENTER_DATE"));
            headerMap.put("AUFLOEKZ", outputTable.getString("DELETION_FLAG"));
            headerMap.put("PS_PSP_PNR", outputTable.getString("WBS_ELEMENT"));
            headerMap.put("CO_RUECK", outputTable.getString("CONF_NO"));
            headerMap.put("CIM_COUNT", outputTable.getString("CONF_CNT"));
            headerMap.put("CUOBJ", outputTable.getString("INT_OBJ_NO"));
            headerMap.put("CO_GLUZS", outputTable.getString("SCHED_FIN_TIME"));
            headerMap.put("CO_GSUZS", outputTable.getString("SCHED_START_TIME"));
            headerMap.put("CO_PRODNET", outputTable.getString("COLLECTIVE_ORDER"));
            headerMap.put("CY_SEQNR", outputTable.getString("ORDER_SEQ_NO"));
            headerMap.put("CO_GLUZP", outputTable.getString("FINISH_TIME"));
            headerMap.put("CO_GSUZP", outputTable.getString("START_TIME"));
            headerMap.put("CO_GSUZI", outputTable.getString("ACTUAL_START_TIME"));
            headerMap.put("CO_LAUFNR", outputTable.getString("LEADING_ORDER"));
            headerMap.put("KDAUF", outputTable.getString("SALES_ORDER")==null?null:outputTable.getString("SALES_ORDER").replaceAll("^(0+)", ""));//销售订单号
            headerMap.put("KDPOS", outputTable.getString("SALES_ORDER_ITEM"));//销售订单行项目
            headerMap.put("CO_PRODPRF", outputTable.getString("PROD_SCHED_PROFILE"));
            headerMap.put("CO_MATXT", outputTable.getString("MATERIAL_TEXT"));//物料描述
            headerMap.put("CO_STTXT", outputTable.getString("SYSTEM_STATUS"));
            headerMap.put("CO_VFMNG", outputTable.getString("CONFIRMED_QUANTITY"));
            headerMap.put("PLWRK", outputTable.getString("PLAN_PLANT"));//计划工厂
            headerMap.put("CHARG_D", outputTable.getString("BATCH"));//批次
            headerMap.put("MGV_MATERIAL_EXTERNAL", outputTable.getString("MATERIAL_EXTERNAL"));
            headerMap.put("MGV_MATERIAL_GUID", outputTable.getString("MATERIAL_GUID"));
            headerMap.put("MGV_MATERIAL_VERSION", outputTable.getString("MATERIAL_VERSION"));
            headerMap.put("CFB_BBD_PPPI", outputTable.getString("DATE_OF_EXPIRY"));
            headerMap.put("CFB_DATOFM", outputTable.getString("DATE_OF_MANUFACTURE"));
            
            returnMap.put("headerMap", headerMap);
            
            outputTable = function.getTableParameterList().getTable("POSITION"); //行项目
            List<Map<String,String>> positionList = new ArrayList<Map<String,String>>();
            System.out.println("-->POSITION NumRows = " + outputTable.getNumRows());
            for(int i=0;i<outputTable.getNumRows();i++) {
            	outputTable.setRow(i);
            	Map<String,String> position = new HashMap<String,String>();
            	position.put("AUFNR", outputTable.getString("ORDER_NUMBER").replaceAll("^(0+)", ""));//生产订单号
            	position.put("CO_POSNR", outputTable.getString("ORDER_ITEM_NUMBER"));//行项目号
            	position.put("KDAUF", outputTable.getString("SALES_ORDER").replaceAll("^(0+)", ""));//销售订单号 
            	position.put("KDPOS", outputTable.getString("SALES_ORDER_ITEM"));//销售订单行项目号
            	position.put("CO_PSAMG", outputTable.getString("SCRAP"));//项目中废品的数量
            	position.put("CO_PSMNG", outputTable.getString("QUANTITY"));//订单项数量 订单生产数量
            	position.put("CO_WEMNG", outputTable.getString("DELIVERED_QUANTITY"));//此订单项的收货数量 已收货数量 
            	position.put("LAGME", outputTable.getString("BASE_UNIT"));//基本计量单位
            	position.put("ISOCD_UNIT", outputTable.getString("BASE_UNIT_ISO")); //计量单位的 ISO 代码
            	position.put("CO_MATNR", outputTable.getString("MATERIAL"));//订单的物料编号
            	position.put("CO_LTRMI", outputTable.getString("ACTUAL_DELIVERY_DATE"));
            	position.put("CO_LTRMP", outputTable.getString("PLANNED_DELIVERY_DATE"));
            	position.put("CO_PWERK", outputTable.getString("PLAN_PLANT"));//订单的计划工厂
            	position.put("LGORT_D", outputTable.getString("STORAGE_LOCATION"));//库存地点
            	position.put("ELIKZ", outputTable.getString("DELIVERY_COMPL"));//"交货已完成"标识
            	position.put("VERID", outputTable.getString("PRODUCTION_VERSION"));//生产版本
            	position.put("WERKS_D", outputTable.getString("PROD_PLANT"));//生产工厂
            	position.put("AUFART", outputTable.getString("ORDER_TYPE"));//订单类型
            	position.put("CO_GLTRP", outputTable.getString("FINISH_DATE"));
            	position.put("CO_GLTRS", outputTable.getString("PRODUCTION_FINISH_DATE"));
            	position.put("CHARG_D", outputTable.getString("BATCH"));//批号
            	position.put("AUFLOEKZ", outputTable.getString("DELETION_FLAG"));//删除标识
            	position.put("BERID", outputTable.getString("MRP_AREA"));
            	position.put("CO_MATXT", outputTable.getString("MATERIAL_TEXT"));//物料描述
            	position.put("MGV_MATERIAL_EXTERNAL", outputTable.getString("MATERIAL_EXTERNAL"));
            	position.put("MGV_MATERIAL_GUID", outputTable.getString("MATERIAL_GUID"));
            	position.put("MGV_MATERIAL_VERSION", outputTable.getString("MATERIAL_VERSION"));
                      	
            	positionList.add(position);
            }
            returnMap.put("positionList", positionList);
            
            outputTable = function.getTableParameterList().getTable("COMPONENT");
            List<Map<String,String>> componentList = new ArrayList<Map<String,String>>();
            System.out.println("-->COMPONENT NumRows = " + outputTable.getNumRows());
            for(int i=0;i<outputTable.getNumRows();i++) {
            	outputTable.setRow(i);
            	
            	if(RSNUM==null) {
            		RSNUM = outputTable.getString("RESERVATION_NUMBER");
            	}
            	
            	Map<String,String> component = new HashMap<String,String>();
            	component.put("RSNUM", outputTable.getString("RESERVATION_NUMBER").replaceAll("^(0+)", ""));
            	component.put("RSPOS", outputTable.getString("RESERVATION_ITEM"));
            	component.put("RSART", outputTable.getString("RESERVATION_TYPE"));
            	component.put("XLOEK", outputTable.getString("DELETION_INDICATOR"));
            	component.put("MATNR",outputTable.getString("MATERIAL"));
            	component.put("WERKS_D",outputTable.getString("PROD_PLANT"));
            	component.put("LGORT_D",outputTable.getString("STORAGE_LOCATION"));
            	component.put("PRVBE",outputTable.getString("SUPPLY_AREA"));
            	component.put("CHARG_D",outputTable.getString("BATCH"));
            	component.put("SOBKZ",outputTable.getString("SPECIAL_STOCK"));
            	component.put("BDTER",outputTable.getString("REQ_DATE"));
            	component.put("BDMNG",outputTable.getString("REQ_QUAN"));
            	component.put("MEINS",outputTable.getString("BASE_UOM"));
            	component.put("ISOCD_UNIT",outputTable.getString("BASE_UOM_ISO"));
            	component.put("ENMNG",outputTable.getString("WITHDRAWN_QUANTITY"));
            	component.put("ERFMG",outputTable.getString("ENTRY_QUANTITY"));
            	component.put("ERFME",outputTable.getString("ENTRY_UOM"));
            	component.put("ISOCD_UNIT",outputTable.getString("ENTRY_UOM_ISO"));
            	component.put("AUFNR",outputTable.getString("ORDER_NUMBER").replaceAll("^(0+)", ""));
            	component.put("BWART",outputTable.getString("MOVEMENT_TYPE"));
            	component.put("POSTP",outputTable.getString("ITEM_CATEGORY"));
            	component.put("POSNR",outputTable.getString("ITEM_NUMBER"));
            	component.put("PLNFOLGE",outputTable.getString("SEQUENCE"));
            	component.put("VORNR",outputTable.getString("OPERATION"));
            	component.put("RGEKZ",outputTable.getString("BACKFLUSH"));
            	component.put("KZBWS",outputTable.getString("VALUATION_SPEC_STOCK"));
            	component.put("STATXT",outputTable.getString("SYSTEM_STATUS"));
            	component.put("MAKTX",outputTable.getString("MATERIAL_DESCRIPTION"));//物料描述
            	component.put("CO_VMENG",outputTable.getString("COMMITED_QUANTITY"));
            	component.put("CO_FMENG",outputTable.getString("SHORTAGE"));
            	component.put("BANFN",outputTable.getString("PURCHASE_REQ_NO"));
            	component.put("BNFPO",outputTable.getString("PURCHASE_REQ_ITEM"));
            	component.put("MGV_MATERIAL_EXTERNAL",outputTable.getString("MATERIAL_EXTERNAL"));
            	component.put("MGV_MATERIAL_GUID",outputTable.getString("MATERIAL_GUID"));
            	component.put("MGV_MATERIAL_VERSION",outputTable.getString("MATERIAL_VERSION"));
            	component.put("VERIFY", "");
            	
            	componentList.add(component);
            }
            
            //获取预留数据-虚拟项目标识和排序字符串
            Map<String,Object> resbMap = new HashMap<>();
            
/*            function1 = destination.getRepository().getFunction("ZGMM_MMD_001_00");  //ZGMM_MMD_001_00只能获取排序字符串，无法获取虚拟项目标识
            JCoParameterList input2 = function1.getImportParameterList(); 
            input2.setValue("P_WERKS", WERKS);
            JCoTable RESERVATION = function1.getTableParameterList().getTable("RSNUM");
            RESERVATION.appendRow();
            RESERVATION.setValue("RSNUM", RSNUM);
            function1.execute(destination);
            JCoTable RESB_TABLE = function1.getTableParameterList().getTable("P_OUT");
            */
            
            function1 = destination.getRepository().getFunction("ZMDM23_CO_IOR_001");
            JCoParameterList input2 = function1.getImportParameterList(); 
            input2.setValue("WERKS", WERKS);
            JCoTable AUFNR_TABLE = input2.getTable("AUFNR_LIST");
            AUFNR_TABLE.appendRow();
            AUFNR_TABLE.setValue(0, orderId);
            function1.execute(destination);
            
            JCoTable RESB_TABLE = function1.getTableParameterList().getTable("GI_RESB");
            
            for(int i=0;i<RESB_TABLE.getNumRows();i++) {
            	RESB_TABLE.setRow(i);
            	Map<String,String> resbItemMap = new HashMap<>();
            	resbItemMap.put("SORTF", RESB_TABLE.getString("SORTF"));
            	resbItemMap.put("DUMPS", RESB_TABLE.getString("DUMPS"));
            	resbMap.put(RESB_TABLE.getString("RSPOS"), resbItemMap);
            }
            
            for (Map<String,String> component : componentList) {
            	String RSPOS = component.get("RSPOS").toString();
            	Map<String,String> resbItemMap = (Map<String,String>)resbMap.get(RSPOS);
            	component.put("SORTF", resbItemMap.get("SORTF"));
            	component.put("DUMPS", resbItemMap.get("DUMPS"));
			}
            
            returnMap.put("componentList", componentList);
            returnMap.put("CODE", "0");
	    }catch (Exception e) {  
            e.printStackTrace();  
            returnMap.put("CODE", "-1");
            returnMap.put("MESSAGE", e.getMessage());
        }
	    
		return returnMap;
	}
	
	@Override
	public Map<String,Object> getSapBapiKaufOrder(String orderId){
		/*//sapBapi.init();
		sap.init();
		JCO.Function function = null;  
	    //JCoDestination destination = sapBapi.connect();  
	    //JCoTable outputTable = null;
	    Map<String,Object> returnMap = new HashMap<String,Object>();
	    try {
	    	//function = destination.getRepository().getFunction("ZGPP_KAUF_ORDER_READ");  
	    	function = sap.getJCOFunction("ZGPP_KAUF_ORDER_READ");
            JCO.ParameterList input = function.getImportParameterList();  
            //input.setValue("I_AUFNR", orderId); 
            input.setValue(orderId,"I_AUFNR"); 
            //function.execute();  
            sap.execute(function);
	    }catch (Exception e) {  
            e.printStackTrace();  
            returnMap.put("CODE", "-1");
            returnMap.put("MESSAGE", e.getMessage());
        }*/
		
		sapBapi.init();
		JCoFunction function = null;  
		JCoParameterList input = null;
	    JCoDestination destination = sapBapi.connect();
	    Map<String,Object> returnMap = new HashMap<String,Object>();
	    try {
	    	function = destination.getRepository().getFunction("ZGPP_KAUF_ORDER_READ");  
            input = function.getImportParameterList(); 
            input.setValue("I_AUFNR", pixStrZero(orderId,12)); 
            function.execute(destination);  
            returnMap.put("CODE", "0");
            //System.out.println(function.getExportParameterList().getStructure("E_COAS").getString("KTEXT"));
            returnMap.put("AUFNR", function.getExportParameterList().getStructure("E_COAS").getString("AUFNR"));
            returnMap.put("KTEXT", function.getExportParameterList().getStructure("E_COAS").getString("KTEXT"));
            returnMap.put("BUKRS", function.getExportParameterList().getStructure("E_COAS").getString("BUKRS"));
            returnMap.put("WERKS", function.getExportParameterList().getStructure("E_COAS").getString("WERKS"));
            returnMap.put("PHAS1", function.getExportParameterList().getStructure("E_COAS").getString("PHAS1"));
            returnMap.put("PHAS3", function.getExportParameterList().getStructure("E_COAS").getString("PHAS3"));
            returnMap.put("LOEKZ", function.getExportParameterList().getStructure("E_COAS").getString("LOEKZ"));
            returnMap.put("KOSTL", function.getExportParameterList().getStructure("E_COAS").getString("KOSTL"));
            returnMap.put("SAKNR", function.getExportParameterList().getStructure("E_COAS").getString("SAKNR"));
            returnMap.put("AUTYP", function.getExportParameterList().getStructure("E_COAS_OLD").getString("AUTYP"));
            //AUTYP-订单类别（2） 值为 01：内部订单 值为04：CO订单
	    }catch (Exception e) {  
	    	try {
	    		input.setValue("I_AUFNR", orderId); 
	            function.execute(destination);  
	            returnMap.put("CODE", "0");
	            //System.out.println(function.getExportParameterList().getStructure("E_COAS").getString("KTEXT"));
	            returnMap.put("AUFNR", function.getExportParameterList().getStructure("E_COAS").getString("AUFNR"));
	            returnMap.put("KTEXT", function.getExportParameterList().getStructure("E_COAS").getString("KTEXT"));
	            returnMap.put("BUKRS", function.getExportParameterList().getStructure("E_COAS").getString("BUKRS"));
	            returnMap.put("WERKS", function.getExportParameterList().getStructure("E_COAS").getString("WERKS"));
	            returnMap.put("PHAS1", function.getExportParameterList().getStructure("E_COAS").getString("PHAS1"));
	            returnMap.put("PHAS3", function.getExportParameterList().getStructure("E_COAS").getString("PHAS3"));
	            returnMap.put("LOEKZ", function.getExportParameterList().getStructure("E_COAS").getString("LOEKZ"));
	            returnMap.put("KOSTL", function.getExportParameterList().getStructure("E_COAS").getString("KOSTL"));
	            returnMap.put("SAKNR", function.getExportParameterList().getStructure("E_COAS").getString("SAKNR"));
	            returnMap.put("AUTYP", function.getExportParameterList().getStructure("E_COAS_OLD").getString("AUTYP"));
	            //AUTYP-订单类别（2） 值为 01：内部订单 值为04：CO订单
			} catch (Exception e2) {
	            e2.printStackTrace(); 
	            returnMap.put("CODE", "-1");
	            returnMap.put("MESSAGE", e.getMessage());
			}

        }
		
		return returnMap;
	}
	
	@Override
	public Map<String,Object> getSapBapiMaterialInfo(String plant,String material){
		sapBapi.init();
		JCoFunction function = null;  
	    JCoDestination destination = sapBapi.connect();
	    JCoParameterList output = null;
	    Map<String,Object> returnMap = new HashMap<String,Object>();
	    
	    if(plant == null || material == null) {
	    	returnMap.put("CODE", "-10");
            returnMap.put("MESSAGE", "传入参数不完整");
            return returnMap;
	    }
	    try {
	    	function = destination.getRepository().getFunction("BAPI_MATERIAL_GET_ALL");  
            JCoParameterList input = function.getImportParameterList();
            input.setValue("PLANT", plant);
            input.setValue("MATERIAL", material); 
            function.execute(destination); 
            output = function.getExportParameterList();
            if("".equals(output.getStructure("CLIENTDATA").getString("MATERIAL"))) {
            	returnMap.put("CODE", "-1");
                returnMap.put("MESSAGE", "没有查到对应的物料信息！");
                return returnMap;
            }
            if("".equals(output.getStructure("PLANTDATA").getString("PLANT"))) {
            	returnMap.put("CODE", "-1");
                returnMap.put("MESSAGE", "你要求的物料"+material+"在工厂"+plant+"中的数据不存!");
                return returnMap;
            }
            returnMap.put("CODE", "0");
            returnMap.put("MATERIAL", output.getStructure("CLIENTDATA").getString("MATERIAL"));		//MATNR
            returnMap.put("BASE_UOM", output.getStructure("CLIENTDATA").getString("BASE_UOM"));
            returnMap.put("PO_UNIT", output.getStructure("CLIENTDATA").getString("PO_UNIT"));
            returnMap.put("DEL_FLAG", output.getStructure("CLIENTDATA").getString("DEL_FLAG"));

            returnMap.put("PLANT", output.getStructure("PLANTDATA").getString("PLANT"));
            returnMap.put("PUR_STATUS", output.getStructure("PLANTDATA").getString("PUR_STATUS"));
            returnMap.put("DEL_FLAG", output.getStructure("PLANTDATA").getString("DEL_FLAG"));
            returnMap.put("ISSUE_UNIT", output.getStructure("PLANTDATA").getString("ISSUE_UNIT"));
            returnMap.put("PROC_TYPE", output.getStructure("PLANTDATA").getString("PROC_TYPE"));
            returnMap.put("SPPROCTYPE", output.getStructure("PLANTDATA").getString("SPPROCTYPE"));
            returnMap.put("BATCH_MGMT", output.getStructure("PLANTDATA").getString("BATCH_MGMT"));
			//质检周期
			returnMap.put("PRFRQ", output.getStructure("PLANTDATA").getString("INSP_INT"));

			returnMap.put("PRICE_CTRL", output.getStructure("VALUATIONDATA").getString("PRICE_CTRL"));
            returnMap.put("MOVING_PR", output.getStructure("VALUATIONDATA").getString("MOVING_PR"));
            returnMap.put("STD_PRICE", output.getStructure("VALUATIONDATA").getString("STD_PRICE"));
            returnMap.put("PRICE_UNIT", output.getStructure("VALUATIONDATA").getString("PRICE_UNIT"));

            JCoTable mat_table = function.getTableParameterList().getTable("MATERIALDESCRIPTION");
            int matCount = mat_table.getNumRows();
            for(int i=0;i<matCount;i++) {
            	mat_table.setRow(i);
            	if("1".equals(mat_table.getValue("LANGU"))) {
            		returnMap.put("MATL_DESC_ZH", mat_table.getValue("MATL_DESC"));
            	}else if("E".equals(mat_table.getValue("LANGU"))) {
            		returnMap.put("MATL_DESC_EN", mat_table.getValue("MATL_DESC"));
            	}
            }
            
            //只抓取参考单位等于基本计量单位的数据行
            JCoTable table = function.getTableParameterList().getTable("UNITSOFMEASURE");
            int unitCount = table.getNumRows();
            List<Map<String,Object>> unitList = new ArrayList<Map<String,Object>>();
            for(int i=0;i<unitCount;i++) {
            	table.setRow(i);
            	if(!output.getStructure("CLIENTDATA").getString("BASE_UOM").equals(table.getValue("ALT_UNIT"))) {
            		Map<String,Object> map = new HashMap<String,Object>();
            		map.put("ALT_UNIT", table.getValue("ALT_UNIT"));            
            		map.put("ALT_UNIT_ISO", table.getValue("ALT_UNIT_ISO"));
            		map.put("NUMERATOR", table.getValue("NUMERATOR"));
            		map.put("DENOMINATR", table.getValue("DENOMINATR"));
            		
            		unitList.add(map);
            	}
            }
            returnMap.put("unitList", unitList);  
	    }catch (Exception e) {  
            e.printStackTrace(); 
            returnMap.put("CODE", "-2");
            returnMap.put("MESSAGE", e.getMessage());
        }    
	    
	    return returnMap;
	}

	@Override
	public Map<String,Object> getSapBapiVendorInfo(String lifnr){
		sapBapi.init();
		JCoFunction function = null;
		JCoDestination destination = sapBapi.connect();
		JCoParameterList output = null;
		Map<String,Object> returnMap = new HashMap<String,Object>();

		try {
			String vendor = lifnr;
			String zero = "";
			//纯数字供应商共10位，不足补前导零
			if(vendor.matches("\\d*")){
				for(int j = vendor.length(); j< 10; j++){
					zero += "0";
				}
				vendor = zero + vendor;
			}
			function = destination.getRepository().getFunction("BAPI_CREDITOR_GETDETAIL");
			JCoParameterList input = function.getImportParameterList();
			input.setValue("CREDITORID", vendor);

			function.execute(destination);
			output = function.getExportParameterList();

			if(null == output.getStructure("CREDITOR_GENERAL_DETAIL").getString("VENDOR")
					|| "".equals(output.getStructure("CREDITOR_GENERAL_DETAIL").getString("VENDOR"))) {
				returnMap.put("CODE", "-1");
				returnMap.put("MESSAGE", "没有查到对应的供应商信息！");
				return returnMap;
			}
			Map<String,Object> queryMap=new HashMap<String,Object>();
			queryMap.put("LIFNR", lifnr);

			String v_id = sapSyncDao.get_VENDOR_id(queryMap);
			returnMap.put("ID", v_id);
			Date dNow = new Date();
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 	//设置时间格式
			String IMPORT_DATE = sdf.format(dNow);

			returnMap.put("CODE", "0");

			returnMap.put("IMPORT_DATE", IMPORT_DATE);
			returnMap.put("EDITOR", "");
			returnMap.put("EDIT_DATE", "");

			returnMap.put("LIFNR", lifnr);//供应商或债权人的帐号
			returnMap.put("NAME1", output.getStructure("CREDITOR_GENERAL_DETAIL").getString("NAME"));//名称
			returnMap.put("NAME2", output.getStructure("CREDITOR_GENERAL_DETAIL").getString("NAME_2"));//名称 2

			returnMap.put("NAME_3", output.getStructure("CREDITOR_GENERAL_DETAIL").getString("NAME_3"));//名称 3
			returnMap.put("NAME_4", output.getStructure("CREDITOR_GENERAL_DETAIL").getString("NAME_4"));//名称 4
			returnMap.put("CITY", output.getStructure("CREDITOR_GENERAL_DETAIL").getString("CITY"));//城市
			returnMap.put("DISTRICT", output.getStructure("CREDITOR_GENERAL_DETAIL").getString("DISTRICT"));//地区
			returnMap.put("PO_BOX", output.getStructure("CREDITOR_GENERAL_DETAIL").getString("PO_BOX"));//邮政信箱
			returnMap.put("POBX_PCD", output.getStructure("CREDITOR_GENERAL_DETAIL").getString("POBX_PCD"));//邮箱的邮编
			returnMap.put("POSTL_CODE", output.getStructure("CREDITOR_GENERAL_DETAIL").getString("POSTL_CODE"));//邮政编码
			returnMap.put("REGION", output.getStructure("CREDITOR_GENERAL_DETAIL").getString("REGION"));//地区（省/自治区/直辖市、市、县）
			returnMap.put("STREET", output.getStructure("CREDITOR_GENERAL_DETAIL").getString("STREET"));//住宅号及街道
			returnMap.put("COUNTRY", output.getStructure("CREDITOR_GENERAL_DETAIL").getString("COUNTRY"));//国家代码
			returnMap.put("POBX_CTY", output.getStructure("CREDITOR_GENERAL_DETAIL").getString("POBX_CTY"));//邮政信箱城市
			returnMap.put("LANGU", output.getStructure("CREDITOR_GENERAL_DETAIL").getString("LANGU"));//语言代码

			if(v_id == null){
				sapSyncDao.insert_VENDOR(returnMap);
			}else{
				sapSyncDao.update_VENDOR(returnMap);
			}
		}catch (Exception e) {
			e.printStackTrace();
			returnMap.put("CODE", "-2");
			returnMap.put("MESSAGE", e.getMessage());
		}

		return returnMap;
	}


	@Override
	public Map<String,Object> getSapBapiGoodsmvtDetail(String materialdocument,String matdocumentyear){
		sapBapi.init();
		JCoFunction function = null;  
	    JCoDestination destination = sapBapi.connect();
	    JCoParameterList output = null;
	    Map<String,Object> returnMap = new HashMap<String,Object>();
	    
	    if(materialdocument == null || matdocumentyear == null) {
	    	returnMap.put("CODE", "-10");
            returnMap.put("MESSAGE", "传入参数不完整");
            return returnMap;
	    }
	    
	    try {
	    	function = destination.getRepository().getFunction("BAPI_GOODSMVT_GETDETAIL");  
            JCoParameterList input = function.getImportParameterList();
            input.setValue("MATDOCUMENTYEAR", matdocumentyear); 
            input.setValue("MATERIALDOCUMENT", materialdocument); 
            function.execute(destination); 
            output = function.getExportParameterList();
            
			if(!"".equals(output.getStructure("GOODSMVT_HEADER").getString("MAT_DOC"))) {
				 //GOODSMVT_HEADER
				Map<String,String> header = new HashMap<String,String>();
				header.put("MAT_DOC", output.getStructure("GOODSMVT_HEADER").getString("MAT_DOC").replaceAll("^(0+)", ""));
				header.put("DOC_DATE", output.getStructure("GOODSMVT_HEADER").getString("DOC_DATE"));
				header.put("DOC_YEAR", output.getStructure("GOODSMVT_HEADER").getString("DOC_YEAR"));
				header.put("PSTNG_DATE", output.getStructure("GOODSMVT_HEADER").getString("PSTNG_DATE"));
				header.put("REF_DOC_NO", output.getStructure("GOODSMVT_HEADER").getString("REF_DOC_NO"));
				header.put("HEADER_TXT", output.getStructure("GOODSMVT_HEADER").getString("HEADER_TXT"));
				header.put("USERNAME", output.getStructure("GOODSMVT_HEADER").getString("USERNAME"));
				header.put("TR_EV_TYPE", output.getStructure("GOODSMVT_HEADER").getString("TR_EV_TYPE"));
				
				returnMap.put("GOODSMVT_HEADER", header);
	            //GOODSMVT_ITEMS
				List<Map<String,String>> itemList = new ArrayList<Map<String,String>>();
				JCoTable outputTable1=function.getTableParameterList().getTable("GOODSMVT_ITEMS");
				for(int i=0;i<outputTable1.getNumRows();i++) {
					outputTable1.setRow(i);
					Map<String,String> item = new HashMap<String,String>();
					item.put("MAT_DOC", outputTable1.getString("MAT_DOC").replaceAll("^(0+)", ""));
					item.put("SAP_MATDOC_NO", outputTable1.getString("MAT_DOC").replaceAll("^(0+)", ""));
					item.put("DOC_YEAR", outputTable1.getString("DOC_YEAR"));
					item.put("MATDOC_ITM", outputTable1.getString("MATDOC_ITM"));
					item.put("SAP_MATDOC_ITEM_NO", outputTable1.getString("MATDOC_ITM"));
					item.put("MATERIAL", outputTable1.getString("MATERIAL"));
					item.put("MATNR", outputTable1.getString("MATERIAL"));
					item.put("PLANT", outputTable1.getString("PLANT"));
					item.put("WERKS", outputTable1.getString("PLANT"));
					item.put("STGE_LOC", outputTable1.getString("STGE_LOC"));
					item.put("BATCH", outputTable1.getString("BATCH"));
					item.put("MOVE_TYPE", outputTable1.getString("MOVE_TYPE"));
					item.put("STCK_TYPE", outputTable1.getString("STCK_TYPE"));
					item.put("SPEC_STOCK", outputTable1.getString("SPEC_STOCK"));
					item.put("ENTRY_QNT", outputTable1.getString("ENTRY_QNT"));
					item.put("ENTRY_UOM", outputTable1.getString("ENTRY_UOM"));
					
					item.put("VENDOR", outputTable1.getString("VENDOR"));
					item.put("CUSTOMER", outputTable1.getString("CUSTOMER"));
					item.put("ITEM_TEXT", outputTable1.getString("ITEM_TEXT"));
					item.put("GR_RCPT", outputTable1.getString("GR_RCPT"));
					
					item.put("MOVE_MAT", outputTable1.getString("MOVE_MAT"));
					item.put("MOVE_PLANT", outputTable1.getString("MOVE_PLANT"));
					item.put("MOVE_STLOC", outputTable1.getString("MOVE_STLOC"));
					item.put("MOVE_BATCH", outputTable1.getString("MOVE_BATCH"));
					
					item.put("PO_NUMBER", outputTable1.getString("PO_NUMBER").replaceAll("^(0+)", ""));
					item.put("PO_ITEM", outputTable1.getString("PO_ITEM"));
					item.put("NO_MORE_GR", outputTable1.getString("NO_MORE_GR"));
					item.put("COSTCENTER", outputTable1.getString("COSTCENTER").replaceAll("^(0+)", ""));
					item.put("ORDERID", outputTable1.getString("ORDERID").replaceAll("^(0+)", ""));
					item.put("ORDER_ITNO", outputTable1.getString("ORDER_ITNO"));
					item.put("WBS_ELEM", outputTable1.getString("WBS_ELEM").replaceAll("^(0+)", ""));
					
					item.put("X_AUTO_CRE", outputTable1.getString("X_AUTO_CRE"));//项目的自动创建  
					
					itemList.add(item);
				}
				returnMap.put("GOODSMVT_ITEMS", itemList);
			}else {
				returnMap.put("CODE", "-1");
	            returnMap.put("MESSAGE", "没有查到此物料凭证信息");
			}
            
	    }catch (Exception e) {  
            e.printStackTrace(); 
            returnMap.put("CODE", "-2");
            returnMap.put("MESSAGE", e.getMessage());
        }
	    return returnMap;
	}
	
	@Override
	public Map<String,String> getCostcenterDetail(String Costcenter){
		sapBapi.init();
		JCoFunction function = null;  
	    JCoDestination destination = sapBapi.connect();
	    JCoParameterList output = null;
	    Map<String,String> returnMap = new HashMap<String,String>();
	    try {
	    	function = destination.getRepository().getFunction("BAPI_COSTCENTER_GETDETAIL1");  
            JCoParameterList input = function.getImportParameterList();  
            input.setValue("CONTROLLINGAREA", "BYD"); 
            //input.setValue("KEYDATE", "20180720"); 
            input.setValue("COSTCENTER", Costcenter); 
            function.execute(destination); 
            
            output = function.getExportParameterList();
            returnMap.put("COSTCENTER", output.getStructure("COSTCENTERDETAIL").getString("COSTCENTER").replaceAll("^(0+)", ""));
            returnMap.put("VALID_FROM", output.getStructure("COSTCENTERDETAIL").getString("VALID_FROM"));
            returnMap.put("VALID_TO", output.getStructure("COSTCENTERDETAIL").getString("VALID_TO"));
            returnMap.put("COMP_CODE", output.getStructure("COSTCENTERDETAIL").getString("COMP_CODE"));
            returnMap.put("NAME", output.getStructure("COSTCENTERDETAIL").getString("NAME"));
            returnMap.put("DESCRIPT", output.getStructure("COSTCENTERDETAIL").getString("DESCRIPT"));
            returnMap.put("CODE", "");
	    }catch (Exception e) {  
            e.printStackTrace(); 
            returnMap.put("CODE", "-1");
            returnMap.put("MESSAGE", e.getMessage());
        }
		
		return returnMap;
	}
	
	@Override
	public Map<String,Object> getDeliveryGetlist(String deliveryNO){
		sapBapi.init(); 
		JCoFunction function = null;
	    JCoDestination destination = sapBapi.connect();  
	    JCoTable inputData = null;
	    JCoTable outputTable = null;
	    JCoTable outputTable1 = null;
	    JCoTable outputTable2 = null;
	    JCoTable outputTable3 = null;
	    JCoTable outputTable4 = null;
	    JCoTable returnTable = null;
	    Map<String,Object> returnMap = new HashMap<String,Object>();
	    List<Map<String,Object>> itemList = new ArrayList<Map<String,Object>>();
	    List<Map<String,Object>> docList = new ArrayList<Map<String,Object>>();
	    try {
	    	 
	    	function = destination.getRepository().getFunction("BAPI_DELIVERY_GETLIST");
            JCoParameterList input = function.getImportParameterList();  
            JCoStructure struct = input.getStructure("IS_DLV_DATA_CONTROL");//取struct 并赋值
            
            struct.setValue("BYPASSING_BUFFER","X");
            struct.setValue("HEAD_STATUS","X");
            struct.setValue("HEAD_PARTNER","X");
            struct.setValue("ITEM","X");
            struct.setValue("ITEM_STATUS","X");
            struct.setValue("DOC_FLOW","X");
            struct.setValue("FT_DATA","X");
            struct.setValue("HU_DATA","X");
            struct.setValue("SERNO","X");
            
            inputData = function.getTableParameterList().getTable("IT_VBELN");//取table 并赋值
            inputData.appendRow();
            inputData.setRow(0);
            int len=inputData.getRecordMetaData().getLength(2);//获取DELIV_NUMB_LOW的长度
            inputData.setValue("SIGN", "I");
            inputData.setValue("OPTION", "EQ");
            if(deliveryNO.length()<len){
            	for(int i=0;i<(len-deliveryNO.length());i++){
            		deliveryNO="0".concat(deliveryNO);
            	}
            }
            inputData.setValue("DELIV_NUMB_LOW",deliveryNO);
            
            function.execute(destination);  
            
            returnTable = function.getTableParameterList().getTable("RETURN");
            if(returnTable !=null) {
                returnTable.setRow(0);
                String returnType = null;
                try {
                	returnType = returnTable.getString("TYPE");
				} catch (Exception e) {
				}
                if(returnType != null && !"S".equals(returnType)) {
                	/*
                	 * 获取交货单信息失败
                	 */
                    returnMap.put("CODE", returnTable.getValue("NUMBER"));
                    returnMap.put("MESSAGE", returnTable.getString("MESSAGE"));
                    return returnMap;
                }
            }
            
            outputTable = function.getTableParameterList().getTable("ET_DELIVERY_HEADER");
            outputTable.setRow(0);
            
            outputTable3 = function.getTableParameterList().getTable("ET_DELIVERY_HEADER_STS");
            outputTable3.setRow(0);
            
            Map<String,Object> headerMap = new HashMap<String,Object>();
            headerMap.put("VKORG", outputTable.getString("VKORG"));
            headerMap.put("VSTEL", outputTable.getString("VSTEL"));
            headerMap.put("VBELN", outputTable.getString("VBELN").replaceAll("^(0+)", ""));
            headerMap.put("WERKS", outputTable.getString("WERKS"));	//收货工厂
            headerMap.put("LFART", outputTable.getString("LFART"));
            headerMap.put("VBTYP", outputTable.getString("VBTYP"));
            headerMap.put("SDABW", outputTable.getString("SDABW"));
            headerMap.put("LGNUM", outputTable.getString("LGNUM"));
            headerMap.put("ERDAT", outputTable.getString("ERDAT"));
            headerMap.put("ERNAM", outputTable.getString("ERNAM"));
            headerMap.put("BTGEW", outputTable.getString("BTGEW"));
            headerMap.put("GEWEI", outputTable.getString("GEWEI"));
            headerMap.put("VOLUM", outputTable.getString("VOLUM"));
            headerMap.put("VOLEH", outputTable.getString("VOLEH"));
            headerMap.put("KUNNR", outputTable.getString("KUNNR"));
            headerMap.put("SPE_LOEKZ", outputTable.getString("SPE_LOEKZ"));//删除标识
            headerMap.put("WBSTK", outputTable3.getString("WBSTK"));//status
            
            returnMap.put("header", headerMap);
            
            outputTable1 = function.getTableParameterList().getTable("ET_DELIVERY_ITEM");
            outputTable4 = function.getTableParameterList().getTable("ET_DELIVERY_ITEM_STS");
            for(int i=0;i<outputTable1.getNumRows();i++){
            	outputTable1.setRow(i);
            	outputTable4.setRow(i);
            	if(Double.parseDouble(outputTable1.getString("LFIMG"))>0) {
            		/**
            		 * 删除批次分割后交货数量为0的行项目
            		 */
                	Map<String,Object> itemMap = new HashMap<String,Object>();
                	itemMap.put("POSNR", outputTable1.getString("POSNR"));//VGPOS 交货行项目
                	itemMap.put("MATNR", outputTable1.getString("MATNR"));//物料号
                	itemMap.put("VBELN", outputTable1.getString("VBELN").replaceAll("^(0+)", "")); //交货单号
                	itemMap.put("ARKTX", outputTable1.getString("ARKTX")); //销售订单项目短文本, 默认显示物料描述
                	itemMap.put("WERKS", outputTable1.getString("WERKS"));	//发货工厂
                	itemMap.put("LGORT", outputTable1.getString("LGORT"));//发货库位
                	itemMap.put("CHARG", outputTable1.getString("CHARG"));//发货批次
                	//itemMap.put("LGMNG", outputTable1.getString("LGMNG"));//交货数量 基于基本计量单位
                	itemMap.put("VRKME", outputTable1.getString("VRKME"));//交货单位
                	itemMap.put("MEINS", outputTable1.getString("MEINS"));//基本计量单位（3）
                	itemMap.put("LFIMG", outputTable1.getString("LFIMG"));//交货数量 实际已交货量（按销售单位）
                	itemMap.put("UMVKZ", outputTable1.getString("UMVKZ"));//销售数量转换成SKU的分子(因子)
                	itemMap.put("UMVKN", outputTable1.getString("UMVKN"));//销售数量转换为 SKU 的值（除数）
                	itemMap.put("VGBEL", outputTable1.getString("VGBEL"));//参考单据的单据编号(10)   采购订单号
                	itemMap.put("VGPOS", outputTable1.getString("VGPOS"));//参考项目的项目号(6) 采购订单行项目号
                	itemMap.put("VOLUM", outputTable1.getString("VOLUM")); //业务量 体积
                	itemMap.put("VOLEH", outputTable1.getString("VOLEH")); //体积单位
                	itemMap.put("BRGEW", outputTable1.getString("BRGEW")); //毛重
                	itemMap.put("GEWEI", outputTable1.getString("GEWEI"));//重量单位

					itemMap.put("VBELV", outputTable1.getString("VBELV"));//
					itemMap.put("POSNV", outputTable1.getString("POSNV"));//

                	itemMap.put("BWART", outputTable1.getString("BWART"));//移动类型
                	itemMap.put("SOBKZ", outputTable1.getString("SOBKZ"));//特殊库存标识 默认为空
                	if(outputTable1.getString("VBELN").equals(outputTable4.getString("VBELN"))&&outputTable1.getString("POSNR").equals(outputTable4.getString("POSNR"))){
                		//送货单号和行项目号一致
                		itemMap.put("WBSTA", outputTable4.getString("WBSTA"));//状态
                	}
                	itemList.add(itemMap);
            	}

            }
            returnMap.put("item", itemList);
            
            outputTable2 = function.getTableParameterList().getTable("ET_DOCUMENT_FLOW");
            for(int z=0;z<outputTable2.getNumRows();z++){
            	outputTable2.setRow(z);
            	Map<String,Object> flowMap = new HashMap<String,Object>();
            	flowMap.put("VBELN", outputTable2.getString("VBELN"));
            	flowMap.put("POSNN", outputTable2.getString("POSNN"));
            	flowMap.put("MJAHR", outputTable2.getString("MJAHR"));
            	flowMap.put("BWART", outputTable2.getString("BWART")); //移动类型
            	flowMap.put("MATNR", outputTable2.getString("MATNR")); 
            	flowMap.put("RFMNG", outputTable2.getString("RFMNG")); 
            	flowMap.put("MEINS", outputTable2.getString("MEINS")); 
            	docList.add(flowMap);
            }
            returnMap.put("docList", docList);
	    }catch (Exception e) {  
            e.printStackTrace();  
            returnMap.put("CODE", "-1");
            returnMap.put("MESSAGE", e.getMessage());
        }
		
		return returnMap;
		
	}

	@Override
	public int update_SapPoAccount(SapPoAccountEntity sappoaccount) {
		return sapSyncDao.update_SapPoAccount(sappoaccount);
	}

	@Override
	public int syncPoHead(SapPoHeadEntity sappohead) {
		Map<String,Object> queryMap=new HashMap<String,Object>();
		queryMap.put("EBELN", sappohead.getEBELN());
		String id=sapSyncDao.get_SAPPoHead_id(queryMap);
		int re=0;
		if(id!=null){
			re=sapSyncDao.update_SAPPoHead(sappohead);
		}else{
			re=sapSyncDao.insert_SapPoHead(sappohead);
		}
		return re;
	}

	@Override
	public int syncPoItem(SapPoItemEntity sappoitem) {
		Map<String,Object> queryMap=new HashMap<String,Object>();
		queryMap.put("EBELN", sappoitem.getEBELN());
		queryMap.put("EBELP", sappoitem.getEBELP());
		String id=sapSyncDao.get_SAPPoItem_id(queryMap);
		int re=0;
		if(id!=null){
			re=sapSyncDao.update_SAPPoItem(sappoitem);
		}else{
			re=sapSyncDao.insert_SapPoItem(sappoitem);
		}
		return re;
	}
	
	@Override
	public int syncPoComponent(Map<String,Object> queryMap) {
		String id=sapSyncDao.get_SapPoComponent_id(queryMap);
		int re=0;
		if(id!=null){
			re = sapSyncDao.update_SapPoComponent(queryMap);
		}else {
			re = sapSyncDao.insert_SapPoComponent(queryMap);
		}
		return re;
	}
	
	@Override
	public boolean getSapBapiPo(String poNo) {
		boolean ret=false;
		sapBapi.init();
		JCoFunction function = null;  
	    JCoDestination destination = sapBapi.connect();  
	    JCoParameterList output = null;
	    JCoStructure poheadertable=null;
	    JCoTable outputTable1 = null;
	    JCoTable outputTable2 = null;
	    JCoTable outputTable3 = null;
	    Date dNow = new Date();
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 	//设置时间格式
		String IMPORT_DATE = sdf.format(dNow);
	    try {
	    function = destination.getRepository().getFunction("BAPI_PO_GETDETAIL1");  
        JCoParameterList input = function.getImportParameterList();  
        input.setValue("PURCHASEORDER", poNo); 
        
        function.execute(destination);  
        
		 output = function.getExportParameterList();
		 poheadertable=output.getStructure("POHEADER");
		 //插入wms_sap_po_head
		 SapPoHeadEntity sapPoHeadEntity=new SapPoHeadEntity();
		 String PO_NUMBER = poheadertable.getString("PO_NUMBER");
		 sapPoHeadEntity.setEBELN(PO_NUMBER);
		 sapPoHeadEntity.setBSTYP("F");//poheadertable.getString("DOC_CAT")
		 sapPoHeadEntity.setBSART(poheadertable.getString("DOC_TYPE"));
		 String COMP_CODE = poheadertable.getString("COMP_CODE");
		 sapPoHeadEntity.setBUKRS(COMP_CODE);
		 sapPoHeadEntity.setEKORG(poheadertable.getString("PURCH_ORG"));
		 sapPoHeadEntity.setEKGRP(poheadertable.getString("PUR_GROUP"));
		 String LIFNR = removeZeroStr(poheadertable.getString("VENDOR"));
		 if(poheadertable.getString("VENDOR")==null || poheadertable.getString("VENDOR").equals("")) {
			 LIFNR = poheadertable.getString("SUPPL_PLNT");
		 }
		 sapPoHeadEntity.setLIFNR(LIFNR);
		 sapPoHeadEntity.setAEDAT(poheadertable.getString("CREAT_DATE"));
		 String FRGRL = poheadertable.getString("REL_STATUS")==null?"X":null;
		 sapPoHeadEntity.setFRGRL(FRGRL);
		 sapPoHeadEntity.setBEDAT(poheadertable.getString("DOC_DATE"));
		 //sapPoHeadEntity.setFRGGR(poheadertable.getString("REL_GROUP"));
		// sapPoHeadEntity.setFRGSX(poheadertable.getString("REL_STRAT"));
		 sapPoHeadEntity.setIMPORT_DATE(IMPORT_DATE);
		 sapPoHeadEntity.setUPDATE_DATE(IMPORT_DATE);
		 syncPoHead(sapPoHeadEntity);
		 //
		 
		 outputTable1=function.getTableParameterList().getTable("POITEM");
		 outputTable2=function.getTableParameterList().getTable("POACCOUNT");
		 outputTable3 = function.getTableParameterList().getTable("POCOMPONENTS");
		 Map<String,SapPoItemEntity> poItemMap = new HashMap<>();
		 //插入wms_sap_po_item
		 for(int i=0;i<outputTable1.getNumRows();i++) {
         	outputTable1.setRow(i);
         	
         	SapPoItemEntity sapPoItemEntity=new SapPoItemEntity();
         	sapPoItemEntity.setEBELN(PO_NUMBER);
         	sapPoItemEntity.setEBELP(outputTable1.getString("PO_ITEM"));
         	sapPoItemEntity.setLOEKZ(outputTable1.getString("DELETE_IND"));
         	sapPoItemEntity.setMATNR(outputTable1.getString("MATERIAL"));
         	sapPoItemEntity.setTXZ01(outputTable1.getString("SHORT_TEXT"));
         	sapPoItemEntity.setBUKRS(COMP_CODE);//
         	sapPoItemEntity.setWERKS(outputTable1.getString("PLANT"));
         	sapPoItemEntity.setLGORT(outputTable1.getString("STGE_LOC"));
         	sapPoItemEntity.setBEDNR(outputTable1.getString("TRACKINGNO"));
         	sapPoItemEntity.setMATKL(outputTable1.getString("MATL_GROUP"));
         	sapPoItemEntity.setMENGE(outputTable1.getString("QUANTITY")==null?new BigDecimal(0):new BigDecimal(outputTable1.getString("QUANTITY")));
         	sapPoItemEntity.setMEINS(outputTable1.getString("PO_UNIT"));
         	sapPoItemEntity.setLMEIN(outputTable1.getString("ORDERPR_UN"));
         	sapPoItemEntity.setPSTYP(outputTable1.getString("ITEM_CAT"));
         	sapPoItemEntity.setKNTTP(outputTable1.getString("ACCTASSCAT"));
         	//sapPoItemEntity.setLEWED(outputTable1.getString("LEWED"));
         	sapPoItemEntity.setELIKZ(outputTable1.getString("NO_MORE_GR"));
         	sapPoItemEntity.setUNTTO(outputTable1.getString("UNDER_DLV_TOL"));
         	sapPoItemEntity.setUEBTO(outputTable1.getString("OVER_DLV_TOL"));
         	sapPoItemEntity.setRETPO(outputTable1.getString("RET_ITEM"));
         	sapPoItemEntity.setAFNAM(outputTable1.getString("PREQ_NAME"));
         	sapPoItemEntity.setORDERPR_UN(outputTable1.getString("ORDERPR_UN"));
         	sapPoItemEntity.setUMREN(outputTable1.getString("CONV_NUM1"));
         	sapPoItemEntity.setUMREZ(outputTable1.getString("CONV_DEN1"));
         	//sapPoItemEntity.setMFRPN(outputTable1.getString("MFRPN"));
         	//sapPoItemEntity.setMFRNR(outputTable1.getString("MFRNR"));
         	
         	if("2".equals(outputTable1.getString("ITEM_CAT"))){
         		sapPoItemEntity.setSOBKZ("K");//特殊库存类型
			}else{
				sapPoItemEntity.setSOBKZ("Z");//特殊库存类型
				//sapPoItemEntity.setSOBKZ(outputTable1.getString("SPEC_STOCK"));
			}
         	//过量交货限制 UEBTO % 不为空，且 数量 MENGE 不为空
         	if(!"".equals(outputTable1.getString("OVER_DLV_TOL"))&&outputTable1.getString("OVER_DLV_TOL")!=null){
				if(!"".equals(outputTable1.getString("QUANTITY"))&&outputTable1.getString("QUANTITY")!=null){
					BigDecimal uebto_big=new BigDecimal(outputTable1.getString("OVER_DLV_TOL"));
					BigDecimal menge_big=new BigDecimal(outputTable1.getString("QUANTITY"));
					//((100+UEBTO)/100)*MENGE
					sapPoItemEntity.setMAX_MENGE((uebto_big.add(new BigDecimal(100))).divide(new BigDecimal(100)).multiply(menge_big).toString());
				}
			
			}else{//否则就取 数量 MENGE
				sapPoItemEntity.setMAX_MENGE(outputTable1.getString("QUANTITY"));
			}
         	
			sapPoItemEntity.setIMPORT_DATE(IMPORT_DATE);
			sapPoItemEntity.setUPDATE_DATE(IMPORT_DATE);
			
			poItemMap.put(outputTable1.getString("PO_ITEM"), sapPoItemEntity);
			
			syncPoItem(sapPoItemEntity);
			
		 }
		//插入wms_sap_po_account
		 for(int i=0;i<outputTable2.getNumRows();i++) {
			 SapPoAccountEntity sapPoAccountEntity=new SapPoAccountEntity();
			 outputTable2.setRow(i);
			 //sapPoAccountEntity.setEBELN(outputTable2.getString("EBELN"));
			 sapPoAccountEntity.setEBELP(outputTable2.getString("PO_ITEM"));
			 sapPoAccountEntity.setZEKKN(outputTable2.getString("SERIAL_NO"));
			 //sapPoAccountEntity.setLOEKZ(outputTable2.getString("LOEKZ"));
			 sapPoAccountEntity.setMENGE(outputTable2.getString("QUANTITY"));
			 //sapPoAccountEntity.setSAKTO(outputTable2.getString("SAKTO"));
			 sapPoAccountEntity.setKOSTL(outputTable2.getString("COSTCENTER"));
			 sapPoAccountEntity.setANLN1(outputTable2.getString("ASSET_NO"));
			 sapPoAccountEntity.setANLN2(outputTable2.getString("SUB_NUMBER"));
			 sapPoAccountEntity.setAUFNR(outputTable2.getString("ORDERID"));
			 sapPoAccountEntity.setPRCTR(outputTable2.getString("PROFIT_CTR"));
			 sapPoAccountEntity.setPS_PSP_PNR(outputTable2.getString("WBS_ELEMENT"));
			 
			 sapPoAccountEntity.setIMPORT_DATE(IMPORT_DATE);
			 syncPoAccount(sapPoAccountEntity);
		 }
		 //插入WMS_SAP_PO_COMPONENT
		 for(int i=0;i<outputTable3.getNumRows();i++) {
			 outputTable3.setRow(i);
			 
			 SapPoItemEntity sapPoItemEntity = poItemMap.get(outputTable3.getString("PO_ITEM"));
			 Map<String,Object> queryMap = new HashMap<String,Object>();
			 queryMap.put("EBELN", sapPoItemEntity.getEBELN());
			 queryMap.put("EBELP", sapPoItemEntity.getEBELP());
			 queryMap.put("ETENR", outputTable3.getString("ITEM_NO"));
			 queryMap.put("RSPOS", "");
			 queryMap.put("MATN1", sapPoItemEntity.getMATNR());
			 queryMap.put("MENG1", sapPoItemEntity.getMENGE().doubleValue());
			 queryMap.put("MEIN1", sapPoItemEntity.getMEINS());
			 queryMap.put("MATN2", outputTable3.getString("MATERIAL"));
			 queryMap.put("MENG2", Double.valueOf(outputTable3.getString("ENTRY_QUANTITY")));
			 queryMap.put("MEIN2", outputTable3.getString("ENTRY_UOM"));
			 queryMap.put("WERKS", outputTable3.getString("PLANT"));
			 queryMap.put("IMPORT_DATE", IMPORT_DATE);
			 queryMap.put("UPDATE_DATE", IMPORT_DATE);
			 
			 syncPoComponent(queryMap);
		 }
		 
		//20190611，获取采购订单行项目 采购单位换算关系 BAPI_PO_GETDETAIL
		 function = destination.getRepository().getFunction("BAPI_PO_GETDETAIL"); 
		 input = function.getImportParameterList();  
	     input.setValue("PURCHASEORDER", poNo); 
	     input.setValue("ITEMS", "X");// BAPI_PO_GETDETAIL需要设置此值才能获取到行项目信息
     
	     function.execute(destination); 
	     outputTable1=function.getTableParameterList().getTable("PO_ITEMS");
			//插入wms_sap_po_item
		 for(int i=0;i<outputTable1.getNumRows();i++) {
			SapPoItemEntity sapPoItemEntity=new SapPoItemEntity();
         	outputTable1.setRow(i);
         	sapPoItemEntity.setEBELN(PO_NUMBER);
         	sapPoItemEntity.setEBELP(outputTable1.getString("PO_ITEM"));
         	sapPoItemEntity.setLMEIN(outputTable1.getString("BASE_UNIT")); //物料基本单位
         	sapPoItemEntity.setUMREN(outputTable1.getString("CONV_NUM2"));
         	sapPoItemEntity.setUMREZ(outputTable1.getString("CONV_DEN2"));
			sapSyncDao.update_SAPPoItem_LMEIN(sapPoItemEntity);
		 }
		 
		 ret=true;
        }catch (Exception e) {  
            e.printStackTrace();  
            ret = false;
        }
		return ret;
	}

/*	@Override
	public boolean getSapBapiPo(String poNo) {
		boolean ret=false;
		sapBapi.init();
		JCoFunction function = null;  
	    JCoDestination destination = sapBapi.connect();  
	    JCoParameterList output = null;
	    JCoStructure poheadertable=null;
	    JCoTable outputTable1 = null;
	    JCoTable outputTable2 = null;
	    Date dNow = new Date();
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 	//设置时间格式
		String IMPORT_DATE = sdf.format(dNow);
	    try {
	    	//20190611，获取采购订单信息BAPI由BAPI_PO_GETDETAIL1变更为BAPI_PO_GETDETAIL
	    	function = destination.getRepository().getFunction("BAPI_PO_GETDETAIL");  
	    	JCoParameterList input = function.getImportParameterList();  
	    	input.setValue("PURCHASEORDER", poNo); 
	    	input.setValue("ITEMS", "X");// BAPI_PO_GETDETAIL需要设置此值才能获取到行项目信息
        
	    	function.execute(destination);  
        
	    	output = function.getExportParameterList();
	    	poheadertable = output.getStructure("PO_HEADER"); // output.getStructure("POHEADER");
	    	//插入wms_sap_po_head
	    	SapPoHeadEntity sapPoHeadEntity=new SapPoHeadEntity();
	    	String PO_NUMBER = poheadertable.getString("PO_NUMBER");
	    	sapPoHeadEntity.setEBELN(PO_NUMBER);
	    	sapPoHeadEntity.setBSTYP(poheadertable.getString("DOC_CAT"));
	    	sapPoHeadEntity.setBSART(poheadertable.getString("DOC_TYPE"));
	    	String COMP_CODE = poheadertable.getString("CO_CODE"); //COMP_CODE
	    	sapPoHeadEntity.setBUKRS(COMP_CODE);
	    	sapPoHeadEntity.setEKORG(poheadertable.getString("PURCH_ORG"));
	    	sapPoHeadEntity.setEKGRP(poheadertable.getString("PUR_GROUP"));
	    	String LIFNR = removeZeroStr(poheadertable.getString("VENDOR"));
			if(poheadertable.getString("VENDOR")==null || poheadertable.getString("VENDOR").equals("")) {
				LIFNR = poheadertable.getString("SUPPL_PLNT");
			}
			sapPoHeadEntity.setLIFNR(LIFNR);
			sapPoHeadEntity.setAEDAT(poheadertable.getString("CREATED_ON"));
			String FRGRL = poheadertable.getString("REL_STATUS")==null?"X":null;
			sapPoHeadEntity.setFRGRL(FRGRL);
			sapPoHeadEntity.setBEDAT(poheadertable.getString("DOC_DATE"));
			sapPoHeadEntity.setFRGGR(poheadertable.getString("REL_GROUP"));
			sapPoHeadEntity.setFRGSX(poheadertable.getString("REL_STRAT"));
			sapPoHeadEntity.setIMPORT_DATE(IMPORT_DATE);
			sapPoHeadEntity.setUPDATE_DATE(IMPORT_DATE);
			syncPoHead(sapPoHeadEntity);
		 
			outputTable1=function.getTableParameterList().getTable("PO_ITEMS");
			outputTable2=function.getTableParameterList().getTable("PO_ITEM_ACCOUNT_ASSIGNMENT");
			//插入wms_sap_po_item
			 for(int i=0;i<outputTable1.getNumRows();i++) {
				SapPoItemEntity sapPoItemEntity=new SapPoItemEntity();
	         	outputTable1.setRow(i);
	         	sapPoItemEntity.setEBELN(PO_NUMBER);
	         	sapPoItemEntity.setEBELP(outputTable1.getString("PO_ITEM"));
	         	sapPoItemEntity.setLOEKZ(outputTable1.getString("DELETE_IND"));
	         	sapPoItemEntity.setMATNR(outputTable1.getString("MATERIAL"));
	         	sapPoItemEntity.setTXZ01(outputTable1.getString("SHORT_TEXT"));
	         	sapPoItemEntity.setBUKRS(COMP_CODE);//
	         	sapPoItemEntity.setWERKS(outputTable1.getString("PLANT"));
	         	sapPoItemEntity.setLGORT(outputTable1.getString("STORE_LOC")); //STGE_LOC
	         	sapPoItemEntity.setBEDNR(outputTable1.getString("TRACKINGNO")); // 需求跟踪号
	         	sapPoItemEntity.setMATKL(outputTable1.getString("MAT_GRP"));//MATL_GROUP
	         	sapPoItemEntity.setMENGE(outputTable1.getString("QUANTITY")==null?new BigDecimal(0):new BigDecimal(outputTable1.getString("QUANTITY")));
	         	sapPoItemEntity.setMEINS(outputTable1.getString("UNIT"));//采购单位 PO_UNIT
	         	sapPoItemEntity.setLMEIN(outputTable1.getString("BASE_UNIT")); //物料基本单位
	         	sapPoItemEntity.setPSTYP(outputTable1.getString("ITEM_CAT")); //项目类别
	         	sapPoItemEntity.setKNTTP(outputTable1.getString("ACCTASSCAT")); //科目分配类别
	         	//sapPoItemEntity.setLEWED(outputTable1.getString("LEWED"));
	         	sapPoItemEntity.setELIKZ(outputTable1.getString("DEL_COMPL"));  //交货已完成标示  NO_MORE_GR
	         	sapPoItemEntity.setUNTTO(outputTable1.getString("UNDER_TOL")); //交货不足限制   UNDER_DLV_TOL
	         	sapPoItemEntity.setUEBTO(outputTable1.getString("OVERDELTOL")); //过量交货限制  OVER_DLV_TOL
	         	sapPoItemEntity.setRETPO(outputTable1.getString("RET_ITEM"));
	         	//sapPoItemEntity.setAFNAM(outputTable1.getString("PREQ_NAME"));
	         	sapPoItemEntity.setORDERPR_UN(outputTable1.getString("ORDERPR_UN"));
	         	sapPoItemEntity.setUMREN(outputTable1.getString("CONV_NUM2"));
	         	sapPoItemEntity.setUMREZ(outputTable1.getString("CONV_DEN2"));
	         	//sapPoItemEntity.setMFRPN(outputTable1.getString("MFRPN"));
	         	//sapPoItemEntity.setMFRNR(outputTable1.getString("MFRNR"));
	         	
	         	if("2".equals(outputTable1.getString("ITEM_CAT"))){
	         		sapPoItemEntity.setSOBKZ("K");//特殊库存类型
				}else{
					sapPoItemEntity.setSOBKZ("Z");//特殊库存类型
					//sapPoItemEntity.setSOBKZ(outputTable1.getString("SPEC_STOCK"));
				}
	         	//过量交货限制 UEBTO % 不为空，且 数量 MENGE 不为空
	         	if(!"".equals(outputTable1.getString("OVERDELTOL"))&&outputTable1.getString("OVERDELTOL")!=null){
					if(!"".equals(outputTable1.getString("QUANTITY"))&&outputTable1.getString("QUANTITY")!=null){
						BigDecimal uebto_big=new BigDecimal(outputTable1.getString("OVERDELTOL"));
						BigDecimal menge_big=new BigDecimal(outputTable1.getString("QUANTITY"));
						//((100+UEBTO)/100)*MENGE
						sapPoItemEntity.setMAX_MENGE((uebto_big.add(new BigDecimal(100))).divide(new BigDecimal(100)).multiply(menge_big).toString());
					}
				
				}else{//否则就取 数量 MENGE
					sapPoItemEntity.setMAX_MENGE(outputTable1.getString("QUANTITY"));
				}
	         	
				sapPoItemEntity.setIMPORT_DATE(IMPORT_DATE);
				sapPoItemEntity.setUPDATE_DATE(IMPORT_DATE);
				
				syncPoItem(sapPoItemEntity);
				
			 }
			 //插入wms_sap_po_account
			 for(int i=0;i<outputTable2.getNumRows();i++) {
				 SapPoAccountEntity sapPoAccountEntity=new SapPoAccountEntity();
				 outputTable2.setRow(i);
				 sapPoAccountEntity.setEBELN(PO_NUMBER);
				 sapPoAccountEntity.setEBELP(outputTable2.getString("PO_ITEM"));
				 sapPoAccountEntity.setZEKKN(outputTable2.getString("SERIAL_NO"));
				 //sapPoAccountEntity.setLOEKZ(outputTable2.getString("LOEKZ"));
				 sapPoAccountEntity.setMENGE(outputTable2.getString("QUANTITY"));
				 //sapPoAccountEntity.setSAKTO(outputTable2.getString("SAKTO"));
				 sapPoAccountEntity.setKOSTL(outputTable2.getString("COST_CTR")); //成本中心  COSTCENTER
				 sapPoAccountEntity.setANLN1(outputTable2.getString("ASSET_NO"));
				 sapPoAccountEntity.setANLN2(outputTable2.getString("SUB_NUMBER")); 
				 sapPoAccountEntity.setAUFNR(outputTable2.getString("ORDER_NO")); // ORDERID
				 sapPoAccountEntity.setPRCTR(outputTable2.getString("PROFIT_CTR")); //PROFIT_CTR
				 sapPoAccountEntity.setPS_PSP_PNR(outputTable2.getString("WBS_ELEM_E")); // WBS_ELEMENT
				 
				 sapPoAccountEntity.setIMPORT_DATE(IMPORT_DATE);
				 syncPoAccount(sapPoAccountEntity);
			 }
			 
			 //插入wms_sap_po_component
			 
			 ret=true;
        }catch (Exception e) {  
            e.printStackTrace();  
            ret = false;
        }
		return ret;
	}*/

	@Override
	public Map<String, Object> getSapBapiWbs(String wbsno) {
		sapBapi.init();
		JCoFunction function = null;  
	    JCoDestination destination = sapBapi.connect();  
	    JCoTable inputData = null;
	    JCoTable outputTable = null;
	    Map<String,Object> returnMap = new HashMap<String,Object>();
	    try {
	    	function = destination.getRepository().getFunction("BAPI_PROJECT_GETINFO");
	        
	        inputData = function.getTableParameterList().getTable("I_WBS_ELEMENT_TABLE");//取table 并赋值
            inputData.appendRow();
            inputData.setRow(0);
            inputData.setValue("WBS_ELEMENT",wbsno);
            
            function.execute(destination); 
            
            outputTable = function.getTableParameterList().getTable("E_WBS_ELEMENT_TABLE");
            outputTable.setRow(0);//WBS_ELEMENT 
            //System.out.print(">>>>>"+outputTable.getString("DESCRIPTION"));
            returnMap.put("CODE", "0");
            returnMap.put("WBS_ELEMENT", outputTable.getString("WBS_ELEMENT"));
            returnMap.put("DESCRIPTION", outputTable.getString("DESCRIPTION"));
            returnMap.put("COMP_CODE", outputTable.getString("COMP_CODE"));
	    	
	    }catch (Exception e) {  
            e.printStackTrace();  
            returnMap.put("CODE", "-1");
            returnMap.put("MESSAGE", e.getMessage());
        }
		return returnMap;
	}
	
	public static String removeZeroStr(String str) {
		if(str == null) return "";
		for(int i=0;i<str.length();i++) {
			if(!"0".equals(str.substring(i,i+1))) {
				str = str.substring(i);
				break;
			}
		}
		return str;
	}
	protected String pixStrZero(String str,int length) {
		int pxnum = length-str.length();
		if(pxnum >0) str = String.format("%0"+(pxnum)+"d", 0) + str;    
		if(pxnum <0) str = str.substring(0, length);    
	    //System.out.println(str);
		return str;
	}
	
	/**
	 * 获取SAP库存
	 */
	@Override
	public Map<String,Object> queryMaterialStock(Map<String,Object> params){
		Map<String,Object> returnMap = new HashMap<String,Object>();
		String werks = params.get("WERKS") == null ? "":params.get("WERKS").toString();
		String logrt = params.get("LGORT") == null ? "":params.get("LGORT").toString();
		
		MaterialStockReadBean bean = new MaterialStockReadBean();
		bean.setPlant(werks);
		bean.setStorageLoc(logrt);
		
		List<String> matnrRange = (List<String>) params.get("MATNRANGE");
		bean.setMatnrRange(matnrRange);
		returnMap = materialStockRead.execute(bean);
		return returnMap;
	}
}

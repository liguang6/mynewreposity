package com.byd.sap.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.toolkit.StringUtils;
import com.byd.sap.rfc.SAPBapi;
import com.byd.sap.service.IfutureTaskService;
import com.byd.sap.service.IwmsSapRealtimeService;
import com.sap.conn.jco.JCoContext;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoStructure;
import com.sap.conn.jco.JCoTable;

@Service("wmsSapRealtimeService")
public class WmsSapRealtimeService implements IwmsSapRealtimeService {
	@Autowired
	private SAPBapi sapBapi;
	@Autowired
	private IfutureTaskService futureTaskService;
	
	@Override
	public Map<String,Object> sapGoodsmvtCreateMB01(Map<String,Object> paramMap){
	    Map<String,Object> returnMap = new HashMap<String,Object>();
	    paramMap.put("GM_CODE", "01");
	    returnMap = sapGoodsmvtCreate(paramMap);	    
	    return returnMap;
	}
	
	@Override
	public Map<String,Object> sapGoodsmvtCreateMB02(Map<String,Object> paramMap){
	    Map<String,Object> returnMap = new HashMap<String,Object>();
	    paramMap.put("GM_CODE", "02");
	    returnMap = sapGoodsmvtCreate(paramMap);	    
	    return returnMap;
	}
	
	@Override
	public Map<String,Object> sapGoodsmvtCreateMB03(Map<String,Object> paramMap){
		Map<String,Object> returnMap = new HashMap<String,Object>();
	    paramMap.put("GM_CODE", "03");
	    returnMap = sapGoodsmvtCreate(paramMap);	    
	    return returnMap;
	}
	
	@Override
	public Map<String,Object> sapGoodsmvtCreateMB03b(Map<String,Object> paramMap){
		Map<String,Object> returnMap = new HashMap<String,Object>();
	    paramMap.put("GM_CODE", "03");
	    returnMap = sapGoodsmvtCreate(paramMap);	    
	    return returnMap;
	}
	
	@Override
	public Map<String,Object> sapGoodsmvtCreateMB03c(Map<String,Object> paramMap){
		Map<String,Object> returnMap = new HashMap<String,Object>();
	    paramMap.put("GM_CODE", "03");
	    returnMap = sapGoodsmvtCreate(paramMap);	    
	    return returnMap;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Map<String,Object> sapGoodsmvtCreateMB03_251(Map<String,Object> paramMap){
		Map<String,Object> returnMap = new HashMap<String,Object>();
	    paramMap.put("GM_CODE", "03");
	    List<Map<String,String>> itemList = (List<Map<String, String>>) paramMap.get("ITEMLIST");
	    for(int i=0;i<itemList.size();i++) {
	    	itemList.get(i).put("MOVE_TYPE", "251");	    	
	    }	    
	    returnMap = sapGoodsmvtCreate(paramMap);	    
	    return returnMap;
	}
	
	@Override
	public Map<String,Object> sapGoodsmvtCreateMB04(Map<String,Object> paramMap){
		Map<String,Object> returnMap = new HashMap<String,Object>();
	    paramMap.put("GM_CODE", "04");
	    /*List<Map<String,String>> itemList = (List<Map<String, String>>) paramMap.get("ITEMLIST");
	    for(int i=0;i<itemList.size();i++) {
	    	itemList.get(i).put("MOVE_TYPE", "601");	    	
	    }*/
	    returnMap = sapGoodsmvtCreate(paramMap);	    
	    return returnMap;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Map<String,Object> sapGoodsmvtCreateMB05_521(Map<String,Object> paramMap){
		Map<String,Object> returnMap = new HashMap<String,Object>();
	    paramMap.put("GM_CODE", "05");
	    List<Map<String,String>> itemList = (List<Map<String, String>>) paramMap.get("ITEMLIST");
	    for(int i=0;i<itemList.size();i++) {
	    	itemList.get(i).put("MOVE_TYPE", "521");	    	
	    }	    
	    returnMap = sapGoodsmvtCreate(paramMap);	    
	    return returnMap;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Map<String,Object> sapGoodsmvtCreateMB05_501(Map<String,Object> paramMap){
		Map<String,Object> returnMap = new HashMap<String,Object>();
	    paramMap.put("GM_CODE", "05");
	    List<Map<String,String>> itemList = (List<Map<String, String>>) paramMap.get("ITEMLIST");
	    for(int i=0;i<itemList.size();i++) {
	    	itemList.get(i).put("MOVE_TYPE", "501");	    	
	    }	    
	    returnMap = sapGoodsmvtCreate(paramMap);	    
	    return returnMap;
		
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Map<String,Object> sapGoodsmvtCreateMB05_511(Map<String,Object> paramMap){
		Map<String,Object> returnMap = new HashMap<String,Object>();
	    paramMap.put("GM_CODE", "05");
	    List<Map<String,String>> itemList = (List<Map<String, String>>) paramMap.get("ITEMLIST");
	    for(int i=0;i<itemList.size();i++) {
	    	itemList.get(i).put("MOVE_TYPE", "511");	    	
	    }	    
	    returnMap = sapGoodsmvtCreate(paramMap);	    
	    return returnMap;
		
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Map<String,Object> sapGoodsmvtCreateMB05_903(Map<String,Object> paramMap){
		Map<String,Object> returnMap = new HashMap<String,Object>();
	    paramMap.put("GM_CODE", "05");
	    List<Map<String,String>> itemList = (List<Map<String, String>>) paramMap.get("ITEMLIST");
	    for(int i=0;i<itemList.size();i++) {
	    	itemList.get(i).put("MOVE_TYPE", "903");	    	
	    }	    
	    returnMap = sapGoodsmvtCreate(paramMap);	    
	    return returnMap;
	}
	
	@Override
	public Map<String,Object> sapGoodsmvtCommonCreate(Map<String,Object> paramMap){
		Map<String,Object> returnMap = new HashMap<String,Object>();
		returnMap = sapGoodsmvtCreate(paramMap);
		return returnMap;
	}
	
/*	@SuppressWarnings("unchecked")
	protected Map<String, Object> sapGoodsmvtCreate(Map<String, Object> paramMap) {
		Map<String,Object> returnMap = new HashMap<String,Object>();
    	//2019-10-18 thw 考虑SAP资源紧张，响应慢，设置等待时长1分钟，如果超过1分钟未响应，转入后台将数据写入 Redis
		Future<HashMap<String,Object>> functionExecute = null;
    	try {
    		//开启SAP过账异步线程
    		functionExecute = futureTaskService.sapGoodsmvtCreateExecute(paramMap);
		} catch (Exception e) {
            e.printStackTrace();  
            returnMap.put("CODE", "-4");
            returnMap.put("MESSAGE", "开启SAP过账异步线程失败："+e.getMessage());
            return returnMap;
		}
    	try {
    		//获取结果，设置超过1分钟超时
    		//returnMap = functionExecute.get(1, TimeUnit.MINUTES);
    		returnMap = functionExecute.get();
		} catch (Exception e) {
			//1分钟内SAP未响应，SAP资源紧张，过账自动转为后台任务
            e.printStackTrace();  
            returnMap.put("CODE", "-5");
            returnMap.put("MESSAGE", "SAP过账线程执行异常！");
		}
		return returnMap;
	}*/

	@SuppressWarnings("unchecked")
	protected Map<String, Object> sapGoodsmvtCreate(Map<String, Object> paramMap) {
		Map<String,Object> returnMap = new HashMap<String,Object>();
		String sapInit = sapBapi.init(paramMap.get("WERKS").toString());
		if("".equals(sapInit)) {
			 //SAP连接成功
			JCoFunction function = null;  
			JCoFunction function2 = null;  
			JCoFunction function3 = null;
		    JCoDestination destination = sapBapi.connect();
		   
		    try {
		    	List<Map<String,String>> itemList = (List<Map<String, String>>) paramMap.get("ITEMLIST");
		    	JCoContext.begin(destination); 
		    	function = destination.getRepository().getFunction("BAPI_GOODSMVT_CREATE"); 
		    	function2 = destination.getRepository().getFunction("BAPI_TRANSACTION_COMMIT"); 
		    	function3 = destination.getRepository().getFunction("BAPI_TRANSACTION_ROLLBACK"); 
		    	
		    	JCoStructure struct1 = function.getImportParameterList().getStructure("GOODSMVT_CODE");
		    	String GM_CODE = "";
		    	//解决复合移动类型GM_CODE取值错误，先取行中的GM_CODE。 例如：411K+201 配置表中配置的是“03”，实际上411需要传04,而201才是传03. EDIT BY renwei
		        if(itemList.size() > 0 && itemList.get(0).get("GM_CODE") != null && !itemList.get(0).get("GM_CODE").equals("")) {
		    		GM_CODE = itemList.get(0).get("GM_CODE");
		    	}else {
		    		GM_CODE = (paramMap.get("GM_CODE") != null)?paramMap.get("GM_CODE").toString():"";
		    	}
		    	struct1.setValue("GM_CODE", GM_CODE);
		    	
		    	JCoStructure struct2 = function.getImportParameterList().getStructure("GOODSMVT_HEADER");
		    	struct2.setValue("PSTNG_DATE", paramMap.get("PSTNG_DATE"));
		    	struct2.setValue("DOC_DATE", paramMap.get("DOC_DATE"));
		    	struct2.setValue("HEADER_TXT", paramMap.get("HEADER_TXT"));
		    	if(paramMap.get("REF_DOC_NO")!=null && !StringUtils.isEmpty(paramMap.get("REF_DOC_NO").toString()))struct2.setValue("REF_DOC_NO", paramMap.get("REF_DOC_NO"));
		    	
		    	JCoParameterList input = function.getTableParameterList(); 
		    	
		    	JCoTable inputTable = input.getTable("GOODSMVT_ITEM");
		    	inputTable.appendRows(itemList.size());
		    	for(int i=0;i<itemList.size();i++) {
		    		inputTable.setRow(i);
		    		Map<String,String> item = itemList.get(i);
		    		if(item.get("MATERIAL") != null)inputTable.setValue("MATERIAL", item.get("MATERIAL"));
		    		if(item.get("PLANT") != null)inputTable.setValue("PLANT", item.get("PLANT"));
		    		if(item.get("STGE_LOC") != null)inputTable.setValue("STGE_LOC", item.get("STGE_LOC"));
		    		if(item.get("BATCH") != null && !StringUtils.isEmpty(item.get("BATCH")))inputTable.setValue("BATCH", item.get("BATCH"));
		    		if(item.get("MOVE_TYPE") != null && !StringUtils.isEmpty(item.get("MOVE_TYPE")))inputTable.setValue("MOVE_TYPE", item.get("MOVE_TYPE"));
		    		if(item.get("SPEC_STOCK") != null && !StringUtils.isEmpty(item.get("SPEC_STOCK")))inputTable.setValue("SPEC_STOCK", item.get("SPEC_STOCK"));
		    		if(item.get("ENTRY_QNT") != null && !StringUtils.isEmpty(item.get("ENTRY_QNT")))inputTable.setValue("ENTRY_QNT", item.get("ENTRY_QNT"));
		    		if(item.get("ENTRY_UOM") != null && !StringUtils.isEmpty(item.get("ENTRY_UOM")))inputTable.setValue("ENTRY_UOM", item.get("ENTRY_UOM"));
		    		//if(item.get("MEINS_QNT") != null && !StringUtils.isEmpty(item.get("MEINS_QNT")))inputTable.setValue("PO_PR_QNT", item.get("MEINS_QNT"));
		    		//if(item.get("MEINS") != null && !StringUtils.isEmpty(item.get("MEINS")))inputTable.setValue("ORDERPR_UN", item.get("MEINS"));
		    		
		    		if(item.get("MOVE_PLANT") != null && !StringUtils.isEmpty(item.get("MOVE_PLANT")))inputTable.setValue("MOVE_PLANT", item.get("MOVE_PLANT"));
		    		if(item.get("MOVE_MAT") != null && !StringUtils.isEmpty(item.get("MOVE_MAT")))inputTable.setValue("MOVE_MAT", item.get("MOVE_MAT"));
		    		if(item.get("MOVE_STLOC") != null && !StringUtils.isEmpty(item.get("MOVE_STLOC")))inputTable.setValue("MOVE_STLOC", item.get("MOVE_STLOC"));//发货库位
		    		if(item.get("MOVE_BATCH") != null && !StringUtils.isEmpty(item.get("MOVE_BATCH")))inputTable.setValue("MOVE_BATCH", item.get("MOVE_BATCH"));
		    		
		    		if(item.get("PO_NUMBER") != null && !StringUtils.isEmpty(item.get("PO_NUMBER")))inputTable.setValue("PO_NUMBER", pixStrZero(item.get("PO_NUMBER"),10));
		    		if(item.get("PO_ITEM") != null && !StringUtils.isEmpty(item.get("PO_ITEM")))inputTable.setValue("PO_ITEM", pixStrZero(item.get("PO_ITEM"),5));
		    		if(item.get("VENDOR") != null && !StringUtils.isEmpty(item.get("VENDOR").toString()))inputTable.setValue("VENDOR", isNumeric(item.get("VENDOR"))?pixStrZero(item.get("VENDOR"),10):item.get("VENDOR"));
		    		if( ( item.get("MOVE_TYPE") != null && !StringUtils.isEmpty(item.get("MOVE_TYPE"))&& "521".equals(item.get("MOVE_TYPE")) )
		    			|| ( item.get("IO_NO_FLAG") != null && "X".equals(item.get("IO_NO_FLAG")) )
		    		  ) {
		    			if(item.get("ORDERID") != null && !StringUtils.isEmpty(item.get("ORDERID")))inputTable.setValue("ORDERID", item.get("ORDERID"));
		    		}else {
		    			if(item.get("ORDERID") != null && !StringUtils.isEmpty(item.get("ORDERID")))inputTable.setValue("ORDERID", pixStrZero(item.get("ORDERID"),12));
		    		}
		    		
		    		if(item.get("ORDER_ITNO") != null && !StringUtils.isEmpty(item.get("ORDER_ITNO")))inputTable.setValue("ORDER_ITNO", item.get("ORDER_ITNO"));
		    		if(item.get("ITEM_TEXT") != null && !StringUtils.isEmpty(item.get("ITEM_TEXT")))inputTable.setValue("ITEM_TEXT", item.get("ITEM_TEXT"));
		    		if(item.get("MVT_IND") != null && !StringUtils.isEmpty(item.get("MVT_IND")))inputTable.setValue("MVT_IND", item.get("MVT_IND"));
		    		if(item.get("GR_RCPT") != null && !StringUtils.isEmpty(item.get("GR_RCPT")))inputTable.setValue("GR_RCPT", item.get("GR_RCPT"));
		    		if(item.get("CUSTOMER") != null && !StringUtils.isEmpty(item.get("CUSTOMER")))inputTable.setValue("CUSTOMER", pixStrZero(item.get("CUSTOMER"),10));
		    		if(item.get("RESERV_NO") != null && !StringUtils.isEmpty(item.get("RESERV_NO")))inputTable.setValue("RESERV_NO", pixStrZero(item.get("RESERV_NO"),10));
		    		if(item.get("RES_ITEM") != null && !StringUtils.isEmpty(item.get("RES_ITEM")))inputTable.setValue("RES_ITEM", pixStrZero(item.get("RES_ITEM"),5));
		    		if(item.get("COSTCENTER") != null && !StringUtils.isEmpty(item.get("COSTCENTER")))inputTable.setValue("COSTCENTER", item.get("COSTCENTER"));
		    		if(item.get("MOVE_REAS") != null && !StringUtils.isEmpty(item.get("MOVE_REAS")))inputTable.setValue("MOVE_REAS", item.get("MOVE_REAS"));
		    		if(item.get("WBS_ELEM") != null && !StringUtils.isEmpty(item.get("WBS_ELEM")))inputTable.setValue("WBS_ELEM", item.get("WBS_ELEM"));
		    		if(item.get("XSTOB") != null && !StringUtils.isEmpty(item.get("XSTOB")))inputTable.setValue("XSTOB", item.get("XSTOB"));
		    		if(item.get("REF_DOC_YR") != null && !StringUtils.isEmpty(item.get("REF_DOC_YR")))inputTable.setValue("REF_DOC_YR", item.get("REF_DOC_YR"));
		    		if(item.get("REF_DOC") != null && !StringUtils.isEmpty(item.get("REF_DOC")))inputTable.setValue("REF_DOC", item.get("REF_DOC"));
		    		if(item.get("REF_DOC_IT") != null && !StringUtils.isEmpty(item.get("REF_DOC_IT")))inputTable.setValue("REF_DOC_IT", item.get("REF_DOC_IT"));
		    		
		    		if(item.get("SALES_ORD") != null && !StringUtils.isEmpty(item.get("SALES_ORD")))inputTable.setValue("SALES_ORD", pixStrZero(item.get("SALES_ORD"),10));
		    		if(item.get("S_ORD_ITEM") != null && !StringUtils.isEmpty(item.get("S_ORD_ITEM")))inputTable.setValue("S_ORD_ITEM", pixStrZero(item.get("S_ORD_ITEM"),6));
		    		if(item.get("VAL_SALES_ORD") != null && !StringUtils.isEmpty(item.get("VAL_SALES_ORD")))inputTable.setValue("VAL_SALES_ORD", pixStrZero(item.get("VAL_SALES_ORD"),10));
		    		if(item.get("VAL_S_ORD_ITEM") != null && !StringUtils.isEmpty(item.get("VAL_S_ORD_ITEM")))inputTable.setValue("VAL_S_ORD_ITEM", pixStrZero(item.get("VAL_S_ORD_ITEM"),6));
		    		
		    		if(item.get("LINE_ID") != null && !StringUtils.isEmpty(item.get("LINE_ID")))inputTable.setValue("LINE_ID", pixStrZero(item.get("LINE_ID"),6));
		    		if(item.get("PARENT_ID") != null && !StringUtils.isEmpty(item.get("PARENT_ID")))inputTable.setValue("PARENT_ID", pixStrZero(item.get("PARENT_ID"),6));
		    	}
		    	
		    	function.execute(destination); 
		    	//检查是否返回错误信息
		    	JCoTable outputTable1 = function.getTableParameterList().getTable("RETURN");
		    	if(outputTable1.getNumRows() == 0) {
		    		function2.getImportParameterList().setValue("WAIT", "X");
			    	function2.execute(destination); 
			    	
			    	String matdocumentyear = function.getExportParameterList().getStructure(0).getString("DOC_YEAR");
			    	String materialdocument = function.getExportParameterList().getStructure(0).getString("MAT_DOC");
			    	//检查凭证号是否生成： 从SAP系统获取物料凭证信息
			    	JCoFunction function4 = destination.getRepository().getFunction("BAPI_GOODSMVT_GETDETAIL");  
		            JCoParameterList input4 = function4.getImportParameterList();
		            input4.setValue("MATDOCUMENTYEAR", matdocumentyear); 
		            input4.setValue("MATERIALDOCUMENT", materialdocument); 
		            function4.execute(destination); 
		            JCoParameterList output4 = function4.getExportParameterList();
		            JCoStructure output4_structure = output4.getStructure("GOODSMVT_HEADER");
		            
		            if(!StringUtils.isEmpty(output4_structure.getString("MAT_DOC"))) {
		            	returnMap.put("CODE", "0");
		            	returnMap.put("MESSAGE", "SUCCESS");
		            	returnMap.put("MATERIALDOCUMENT", materialdocument);
		            	returnMap.put("MATDOCUMENTYEAR", matdocumentyear);
		            }else {
		            	returnMap.put("CODE", "-1");
			            returnMap.put("MESSAGE", "没有生成有效的凭证号");
			            function3.execute(destination);
		            }
			    	
		    	}else {
		    		//调用失败
		    		returnMap.put("CODE", "-2");
		            returnMap.put("TYPE", function.getTableParameterList().getTable("RETURN").getString("TYPE"));
		            returnMap.put("SYMSGID", function.getTableParameterList().getTable("RETURN").getString("ID"));
		            returnMap.put("NUMBER", function.getTableParameterList().getTable("RETURN").getString("NUMBER"));
		            returnMap.put("MESSAGE", function.getTableParameterList().getTable("RETURN").getString("MESSAGE"));
		            function3.execute(destination); 
		    	}
		    	JCoContext.end(destination);
		    	
		    }catch (Exception e) {  
	            e.printStackTrace();  
	            returnMap.put("CODE", "-3");
	            returnMap.put("MESSAGE", e.getMessage());
	        }
			
		}else {
			returnMap.put("CODE", "-4");
			returnMap.put("MESSAGE", sapInit);
		}
		return returnMap;
	}

	@Override
	public Map<String, Object> sapGoodsmvtCancel(Map<String, Object> paramMap) {
		Map<String,Object> returnMap = new HashMap<String,Object>();
		String sapInit = sapBapi.init(paramMap.get("WERKS").toString());
		if("".equals(sapInit)) {
			/**
			 * 连接SAP成功
			 */
			JCoFunction function = null;  
			JCoFunction function2 = null;  
			JCoFunction function3 = null;
		    JCoDestination destination = sapBapi.connect();  
		    //JCoTable outputTable = null;
		   
		    try {
		    	JCoContext.begin(destination); 
		    	function = destination.getRepository().getFunction("BAPI_GOODSMVT_CANCEL"); 
		    	function2 = destination.getRepository().getFunction("BAPI_TRANSACTION_COMMIT"); 
		    	function3 = destination.getRepository().getFunction("BAPI_TRANSACTION_ROLLBACK"); 
		    	JCoParameterList input = function.getImportParameterList();
	            input.setValue("MATERIALDOCUMENT", paramMap.get("MATERIALDOCUMENT")==null?paramMap.get("MAT_DOC"):paramMap.get("MATERIALDOCUMENT")); 
	            input.setValue("MATDOCUMENTYEAR", paramMap.get("MATDOCUMENTYEAR")==null?paramMap.get("DOC_YEAR"):paramMap.get("MATDOCUMENTYEAR")); 
	            input.setValue("GOODSMVT_PSTNG_DATE", paramMap.get("GOODSMVT_PSTNG_DATE")==null?paramMap.get("JZ_DATE"):paramMap.get("GOODSMVT_PSTNG_DATE")); 
		    	
		    	List<Map<String,String>> itemList = paramMap.get("ITEMLIST")==null?null:(List<Map<String, String>>) paramMap.get("ITEMLIST");
		    	if(itemList!=null&&itemList.size()>0) {
		    		JCoParameterList inputTableParam = function.getTableParameterList(); 
			    	JCoTable inputTable = inputTableParam.getTable("GOODSMVT_MATDOCITEM");
			    	inputTable.appendRows(itemList.size());
			    	for(int i=0;i<itemList.size();i++) {
			    		inputTable.setRow(i);
			    		Map<String,String> item = itemList.get(i);
			    		if(item.get("MATDOC_ITEM") != null) {
			    			inputTable.setValue("MATDOC_ITEM", item.get("MATDOC_ITEM"));
			    		}else if(item.get("MATDOC_ITM") != null) {
			    			inputTable.setValue("MATDOC_ITEM", item.get("MATDOC_ITM"));
			    		}
			    	}
		    	}

	            function.execute(destination); 
	            JCoTable outputTable1 = function.getTableParameterList().getTable("RETURN");
		    	if(outputTable1.getNumRows() == 0) {
		    		function2.getImportParameterList().setValue("WAIT", "X");
			    	function2.execute(destination); 
		    		
			    	String matdocumentyear = function.getExportParameterList().getStructure(0).getString("DOC_YEAR");
			    	String materialdocument = function.getExportParameterList().getStructure(0).getString("MAT_DOC");
			    	//检查凭证号是否生成： 从SAP系统获取物料凭证信息
			    	JCoFunction function4 = destination.getRepository().getFunction("BAPI_GOODSMVT_GETDETAIL");  
		            JCoParameterList input4 = function4.getImportParameterList();
		            input4.setValue("MATDOCUMENTYEAR", matdocumentyear); 
		            input4.setValue("MATERIALDOCUMENT", materialdocument); 
		            function4.execute(destination); 
		            JCoParameterList output4 = function4.getExportParameterList();
		            
		            if(!StringUtils.isEmpty(output4.getStructure("GOODSMVT_HEADER").getString("MAT_DOC"))) {
		            	returnMap.put("CODE", "0");
		            	returnMap.put("MESSAGE", "SUCCESS");
		            	returnMap.put("MATERIALDOCUMENT", materialdocument);
		            	returnMap.put("MATDOCUMENTYEAR", matdocumentyear);
		            }else {
		            	returnMap.put("CODE", "-1");
			            returnMap.put("MESSAGE", "没有生成有效的凭证号");
		            }
		            
		    	}else {
		    		//调用失败
		    		returnMap.put("CODE", "-2");
		            returnMap.put("TYPE", function.getTableParameterList().getTable("RETURN").getString("TYPE"));
		            returnMap.put("SYMSGID", function.getTableParameterList().getTable("RETURN").getString("ID"));
		            returnMap.put("NUMBER", function.getTableParameterList().getTable("RETURN").getString("NUMBER"));
		            returnMap.put("MESSAGE", function.getTableParameterList().getTable("RETURN").getString("MESSAGE"));
		            function3.execute(destination);
		    	}
		    	JCoContext.end(destination);
		    }catch (Exception e) {  
	            e.printStackTrace();  
	            returnMap.put("CODE", "-1");
	            returnMap.put("MESSAGE", e.getMessage());
	        }
		}else {
			 returnMap.put("CODE", "-4");
			 returnMap.put("MESSAGE", sapInit);
		}
		
		return returnMap;
	}
	/**
	 * 交货单修改、简配
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> sapDeliveryChange(Map<String, Object> paramMap) {
		Map<String,Object> returnMap = new HashMap<String,Object>();
		String initStr = sapBapi.init(paramMap.get("WERKS").toString());
		if(!initStr.equals("")) {
	        returnMap.put("CODE", "-4");
            returnMap.put("MESSAGE", initStr);
            return returnMap;
		}
		JCoFunction function = null;  
		JCoFunction function2 = null;
	    JCoDestination destination = sapBapi.connect();

	    try {
	    	JCoContext.begin(destination); //开启SAP事务
		    /**
		     * 交货单修改、简配
		     */
	    	List<Map<String,String>> itemList = (List<Map<String, String>>) paramMap.get("ITEMLIST");
	    	
	    	function = destination.getRepository().getFunction("BAPI_OUTB_DELIVERY_CHANGE"); 
	    	function2 = destination.getRepository().getFunction("BAPI_TRANSACTION_COMMIT"); 
	    	
	    	JCoStructure import1 = function.getImportParameterList().getStructure("HEADER_DATA");
	    	import1.setValue("DELIV_NUMB", pixStrZero(itemList.get(0).get("VBELN_VL"),10));//交货单号
	    	
	    	function.getImportParameterList().setValue("DELIVERY", pixStrZero(itemList.get(0).get("DELIVERY"),10));
	    	
	    	JCoParameterList input = function.getTableParameterList(); 
	    	
	    	JCoTable ITEM_DATA = input.getTable("ITEM_DATA");
	    	JCoTable ITEM_CONTROL = input.getTable("ITEM_CONTROL");
	    	JCoTable ITEM_DATA_SPL = input.getTable("ITEM_DATA_SPL");
	    	ITEM_DATA.appendRows(itemList.size());
	    	ITEM_CONTROL.appendRows(itemList.size());
	    	ITEM_DATA_SPL.appendRows(itemList.size());
	    	for(int i=0;i<itemList.size();i++) {
	    		ITEM_DATA.setRow(i);
	    		ITEM_CONTROL.setRow(i);
	    		ITEM_DATA_SPL.setRow(i);
	    		Map<String,String> item = itemList.get(i);
	    		if(item.get("VBELN_VL") != null)ITEM_DATA.setValue("DELIV_NUMB", pixStrZero(item.get("VBELN_VL"),10));
	    		if(item.get("POSNR_VL") != null)ITEM_DATA.setValue("DELIV_ITEM", item.get("POSNR_VL"));
	    		if(item.get("LFIMG") != null)ITEM_DATA.setValue("DLV_QTY", item.get("LFIMG"));
	    		if(item.get("CHARG") != null && !StringUtils.isEmpty(item.get("CHARG").toString()) )ITEM_DATA.setValue("BATCH", item.get("CHARG"));
	    		if(item.get("BATCH") != null && !StringUtils.isEmpty(item.get("BATCH").toString()))ITEM_DATA.setValue("BATCH", item.get("BATCH"));
	    		
	    		if(item.get("UMREZ") != null)ITEM_DATA.setValue("FACT_UNIT_NOM", item.get("UMREZ"));
	    		if(item.get("UMREN") != null)ITEM_DATA.setValue("FACT_UNIT_DENOM", item.get("UMREN"));
	    		if(item.get("FACT_UNIT_NOM") != null)ITEM_DATA.setValue("FACT_UNIT_NOM", item.get("FACT_UNIT_NOM"));
	    		if(item.get("FACT_UNIT_DENOM") != null)ITEM_DATA.setValue("FACT_UNIT_DENOM", item.get("FACT_UNIT_DENOM"));
	    		
	    		
	    		if(item.get("VBELN_VL") != null)ITEM_CONTROL.setValue("DELIV_NUMB", pixStrZero(item.get("VBELN_VL"),10));
	    		if(item.get("POSNR_VL") != null)ITEM_CONTROL.setValue("DELIV_ITEM", item.get("POSNR_VL"));
	    		ITEM_CONTROL.setValue("CHG_DELQTY", "X");
	    		if(item.get("DELIV_ITEM") != null)ITEM_CONTROL.setValue("DELIV_ITEM", item.get("DELIV_ITEM"));
	    		if(item.get("LIPS_DEL") != null)ITEM_CONTROL.setValue("DELIV_ITEM", item.get("LIPS_DEL"));
	    		if(item.get("LFIMG") != null && Double.valueOf(item.get("LFIMG").toString())<=0) {
	    			ITEM_CONTROL.setValue("DEL_ITEM", "X");
	    		}
	    		if(item.get("VBELN_VL") != null)ITEM_DATA_SPL.setValue("DELIV_NUMB", pixStrZero(item.get("VBELN_VL"),10));
	    		if(item.get("POSNR_VL") != null)ITEM_DATA_SPL.setValue("DELIV_ITEM", item.get("POSNR_VL"));
	    		if(item.get("LGORT") != null)ITEM_DATA_SPL.setValue("STGE_LOC", item.get("LGORT"));
	    		if(item.get("STGE_LOC") != null)ITEM_DATA_SPL.setValue("STGE_LOC", item.get("STGE_LOC"));
	    	}
	    	
	    	function.execute(destination); 
	    	
	    	//检查是否返回错误信息
	    	JCoTable outputTable1 = function.getTableParameterList().getTable("RETURN");
	    	if(outputTable1.getNumRows() == 0) {
	    		function2.getImportParameterList().setValue("WAIT", "X");
		    	function2.execute(destination); 
		    	
	    		returnMap.put("CODE", "0");
	            returnMap.put("MESSAGE", "修改成功！");
	    	}else {
	    		//调用失败
	    		returnMap.put("CODE", outputTable1.getString("NUMBER"));
	            returnMap.put("MESSAGE", outputTable1.getString("MESSAGE"));
	    	}
	    	JCoContext.end(destination);	    	
	    }catch (Exception e) {  
            e.printStackTrace();  
            returnMap.put("CODE", "-1");
            returnMap.put("MESSAGE", e.getMessage());
        }		
		return returnMap;
	}
	
	/**
	 * 交货单过账
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> sapDeliveryUpdate(Map<String, Object> paramMap) {
		Map<String,Object> returnMap = new HashMap<String,Object>();
		//2019-10-17 判断待过账物料是否在收货工厂启用批次，如是修改交货单行项目关联的PO的批次为 WMS收料产生的批次（如配置为沿用源批次 可能为交货单简配批次）
		List<Map<String,String>> itemList = (List<Map<String, String>>) paramMap.get("ITEMLIST");
		String moveType = itemList.get(0).get("MOVE_TYPE") == null ? "" : itemList.get(0).get("MOVE_TYPE"); 
		List<Map<String,String>> poInfoList = new ArrayList<>();
		String PO_NUMBER = null;
		for (Map<String, String> map : itemList) {
			if(null != map.get("XCHPF") && "X".equals(map.get("XCHPF").toString())) {
				PO_NUMBER = map.get("VGBEL")==null?null:map.get("VGBEL").toString();
				map.put("PO_NUMBER", map.get("VGBEL")==null?null:map.get("VGBEL").toString());//交货单关联的采购订单号
				map.put("PO_ITEM", map.get("VGPOS")==null?null:map.get("VGPOS").toString());//交货单关联的采购订单行项目号
				//物料在收货工厂启用了批次
				poInfoList.add(map);
			}
		}
		if(!StringUtils.isEmpty(PO_NUMBER) && "645".equals(moveType)) {
			//修改PO
			returnMap = this.sapPoItemBatchChange(poInfoList,"S");
			if(!"0".equals(returnMap.get("CODE"))) {
				return returnMap;
			}
		}
		if (moveType.equals("601")) {
			List<Map<String, String>> mergeList = mergeList(itemList);
			paramMap.put("ITEMLIST", mergeList);
		}
		
		/**
		 * 先修改交货单
		 */
		returnMap = this.sapDeliveryChange(paramMap);
		if(!"0".equals(returnMap.get("CODE"))) {
			return returnMap;
		}
		
		String initStr = sapBapi.init(paramMap.get("WERKS").toString());
		if(!initStr.equals("")) {
	        returnMap.put("CODE", "-4");
            returnMap.put("MESSAGE", initStr);
            return returnMap;
		}
		JCoFunction function = null;  
	    JCoDestination destination = sapBapi.connect();

	    try {
		    /**
		     * 交货单过账
		     */
	    	
	    	function = destination.getRepository().getFunction("WS_DELIVERY_UPDATE"); 
	    	JCoStructure import1 = function.getImportParameterList().getStructure("VBKOK_WA");
	    	import1.setValue("VBELN_VL", pixStrZero(itemList.get(0).get("VBELN_VL"),10));
	    	
	    	import1.setValue("KOMUE", itemList.get(0).get("KOMUE")==null?"X":itemList.get(0).get("KOMUE"));
	    	import1.setValue("WABUC", itemList.get(0).get("WABUC")==null?"X":itemList.get(0).get("WABUC"));
	    	import1.setValue("WADAT_IST", paramMap.get("PSTNG_DATE"));
	    	import1.setValue("BLDAT", paramMap.get("DOC_DATE"));//凭证中的凭证日期
	    	
	    	function.getImportParameterList().setValue("SYNCHRON","X");
	    	function.getImportParameterList().setValue("COMMIT", "X");
	    	function.getImportParameterList().setValue("DELIVERY", pixStrZero(itemList.get(0).get("DELIVERY"),10));
	    	function.getImportParameterList().setValue("UPDATE_PICKING", "X");
	    	function.getImportParameterList().setValue("IF_ERROR_MESSAGES_SEND_0","X");
	    	function.getImportParameterList().setValue("IF_DATABASE_UPDATE","1");
	    	
/*	    	//2019-10-17 thw 注释原因，添加此段逻辑 导致601过账 交货单数量翻倍
 * 			if (moveType.equals("601")) {
		    	JCoParameterList input = function.getTableParameterList(); 
		    	JCoTable inputTable = input.getTable("VBPOK_TAB");
		    	//如果WMS有多行数据，需要根据交货单行项目汇总数量过账到SAP
		    	List<Map<String, String>> newItemList = (List<Map<String, String>>) paramMap.get("ITEMLIST");
		    	
		    	inputTable.appendRows(newItemList.size());
		    	for(int i=0;i<newItemList.size();i++) {
		    		inputTable.setRow(i);
		    		Map<String,String> item = newItemList.get(i);
		    		if(item.get("VBELN_VL") != null)inputTable.setValue("VBELN_VL", pixStrZero(item.get("VBELN_VL"),10));
		    		if(item.get("POSNR_VL") != null)inputTable.setValue("POSNR_VL", item.get("POSNR_VL"));
		    		if(item.get("VBELN") != null)inputTable.setValue("VBELN", pixStrZero(item.get("VBELN"),10));
		    		if(item.get("POSNN") != null)inputTable.setValue("POSNN", item.get("POSNN"));
		    		if(item.get("MATNR") != null)inputTable.setValue("MATNR", item.get("MATNR"));
		    		if(item.get("F_WERKS") != null)inputTable.setValue("WERKS", item.get("F_WERKS"));
		    		inputTable.setValue("LIANP", "X");
		    		if(item.get("LIPS_DEL") != null)inputTable.setValue("LIPS_DEL", item.get("LIPS_DEL"));
		    		if(item.get("LFIMG") != null)inputTable.setValue("LFIMG", item.get("LFIMG"));//
		    		if(item.get("LGORT") != null)inputTable.setValue("LGORT", item.get("LGORT"));
		    		if(item.get("CHARG") != null)inputTable.setValue("CHARG", item.get("CHARG"));
		    		if(item.get("ENTRY_UOM") != null)inputTable.setValue("MEINS", item.get("ENTRY_UOM"));
		    	}
	    	}*/
	    	
	    	function.execute(destination); 
	    	JCoParameterList output4 = function.getExportParameterList();
	    	//if(!StringUtils.isEmpty(output4.getString("MESSAGE_TEXT_OUTPUT"))){
	    	if("X".equals(output4.getString("EF_ERROR_ANY_0"))) {
	    		//调用失败
	    		returnMap.put("CODE", "-2");
	    		String MESSAGE = "未获取到交货单过账失败原因！";
	    		try {
					MESSAGE = output4.getString("MESSAGE_TEXT_OUTPUT");
				} catch (Exception e) {
				}
	            returnMap.put("MESSAGE", MESSAGE);
	    	}
	    	
	    }catch (Exception e) {  
            e.printStackTrace();  
            returnMap.put("CODE", "-1");
            returnMap.put("MESSAGE", e.getMessage());
        }
	    
	    //2019-10-17 判断待过账物料是否在收货工厂启用批次，如是修改交货单行项目关联的PO的批次为 WMS收料产生的批次（如配置为沿用源批次 可能为交货单简配批次）
	    //交货单过账成功后，将交货单行项目关联的PO的批次更新为空
		if(!StringUtils.isEmpty(PO_NUMBER) && "645".equals(moveType) && "0".equals(returnMap.get("CODE"))) {
			//修改PO
			this.sapPoItemBatchChange(poInfoList,"E");
		}
	    
		return returnMap;
	}
	
	/**
	 * 采购订单修改
	 */
	@SuppressWarnings("unchecked")
	protected Map<String, Object> sapPoItemBatchChange(List<Map<String,String>> itemList,String type) {
		Map<String,Object> returnMap = new HashMap<String,Object>();
		sapBapi.init();
		JCoFunction function = null;  
		JCoFunction function2 = null;
	    JCoDestination destination = sapBapi.connect();

	    try {
	    	String PO_NUMBER = pixStrZero(itemList.get(0).get("PO_NUMBER"),10);
	    	
	    	JCoContext.begin(destination); //开启SAP事务
	    	
	    	function = destination.getRepository().getFunction("BAPI_PO_CHANGE"); 
	    	function2 = destination.getRepository().getFunction("BAPI_TRANSACTION_COMMIT"); 
	    	function.getImportParameterList().setValue("PURCHASEORDER", PO_NUMBER);
	    	
	    	JCoParameterList input = function.getTableParameterList(); 
	    	
	    	JCoTable POITEM = input.getTable("POITEM");
	    	JCoTable POITEMX = input.getTable("POITEMX");
	    	POITEM.appendRows(itemList.size());
	    	POITEMX.appendRows(itemList.size());
	    	for(int i=0;i<itemList.size();i++) {
	    		POITEM.setRow(i);
	    		POITEMX.setRow(i);
	    		Map<String,String> item = itemList.get(i);
	    		POITEM.setValue("PO_ITEM", item.get("PO_ITEM"));
	    		if("S".equals(type)) {
	    			POITEM.setValue("BATCH", item.get("BATCH"));
	    		}else {
	    			POITEM.setValue("BATCH", "");
	    		}
	    		POITEMX.setValue("PO_ITEM", item.get("PO_ITEM"));
	    		POITEMX.setValue("BATCH", "X");
	    	}
	    	function.execute(destination); 
	    	
	    	//检查是否返回错误信息
	    	JCoTable outputTable1 = function.getTableParameterList().getTable("RETURN");
	    	int returnRows = outputTable1.getNumRows();
	    	boolean flag = false;
	    	if( returnRows > 0 ) {
	    		for(int i=0;i<returnRows;i++) {
	    			outputTable1.setRow(i);
	    			String TYPE = outputTable1.getString("TYPE");
	    			if("S".equals(TYPE)) {
	    				//修改成功
	    				flag = true;
	    	            break;
	    			}
	    		}
	    	}
	    	if(flag){
  				returnMap.put("CODE", "0");
	            returnMap.put("MESSAGE", "修改成功！");
	        	function2.getImportParameterList().setValue("WAIT", "X");
		    	function2.execute(destination);
	    	}else {
	    		//调用失败
	    		returnMap.put("CODE", outputTable1.getString("NUMBER"));
	            returnMap.put("MESSAGE", outputTable1.getString("MESSAGE"));
	    	}
	    	JCoContext.end(destination);	    	
	    }catch (Exception e) {  
            e.printStackTrace();  
            returnMap.put("CODE", "-1");
            returnMap.put("MESSAGE", e.getMessage());
        }		
		return returnMap;
	}
	
	protected String pixStrZero(String str,int length) {
		int pxnum = length-str.length();
		if(pxnum >0) str = String.format("%0"+(pxnum)+"d", 0) + str;    
		if(pxnum <0) str = str.substring(0, length);    
	    //System.out.println(str);
		return str;
	}
	
	protected static List<Map<String,String>> mergeList(List<Map<String,String>> lists) {
		List<Map<String, String>> countList = new ArrayList<Map<String, String>>();
		Map<String,Map<String,String>> matInfoMap = new HashMap<>();
		for (Map<String,String> list : lists) {  
			String vbeln = list.get("VBELN_VL");
            String posnr = list.get("POSNR_VL");
            String CHARG = list.get("CHARG");
            BigDecimal qty = list.get("LFIMG") == null ? BigDecimal.ZERO:new BigDecimal(list.get("LFIMG"));
            String str = vbeln+"*"+posnr+"*"+CHARG;
            if(matInfoMap.get(str)!=null) {
            	Map<String,String> matInfo = (Map<String,String>)matInfoMap.get(str);
                
            	BigDecimal qty1 = matInfo.get("LFIMG") == null ? BigDecimal.ZERO:new BigDecimal(matInfo.get("LFIMG"));
                BigDecimal lfimg = qty.add(qty1);
                matInfo.put("LFIMG", lfimg.toString());
            }else {
            	matInfoMap.put(str, list);
            }
		}
		for(Map<String,String> matInfo : matInfoMap.values()){
			countList.add(matInfo);
		}
		return countList;
	}
	
	protected static boolean isNumeric(String str) {
        String bigStr;
        try {
            bigStr = new BigDecimal(str).toString();
        } catch (Exception e) {
            return false;//异常 说明包含非数字。
        }
        return true;
    }

}

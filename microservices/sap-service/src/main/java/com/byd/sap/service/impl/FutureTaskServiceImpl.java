package com.byd.sap.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import com.byd.sap.rfc.SAPBapi;
import com.byd.sap.service.IfutureTaskService;
import com.sap.conn.jco.JCoContext;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoStructure;
import com.sap.conn.jco.JCoTable;

@Service("futureTaskService")
public class FutureTaskServiceImpl implements IfutureTaskService {
	private static final Logger log = LoggerFactory.getLogger("FutureTaskServiceImpl");
	@Autowired
	private SAPBapi sapBapi;
	
	/**
	 * SAP通用过账异步执行方法
	 * @param destination SAP连接对象
	 * @param function SAP函数对象
	 * @return 函数是否执行成功
	 * @throws JCoException
	 * @throws InterruptedException
	 */
    @Async("taskExecutor")
    @Override
	public Future<HashMap<String,Object>> sapGoodsmvtCreateExecute(Map<String, Object> paramMap) throws JCoException,InterruptedException{
    	log.info("开启线程异步执行过账："+ DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss") );
    	
    	HashMap<String,Object> returnMap = new HashMap<String,Object>();
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
		log.info("异步过账执行完成："+DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss") );
		return new AsyncResult<HashMap<String,Object>>(returnMap);
	}
	
	/**
	 * SAP交货单修改、过账异步执行方法
	 * @param destination SAP连接对象
	 * @param function SAP函数对象
	 * @return 函数是否执行成功
	 * @throws JCoException
	 * @throws InterruptedException
	 */
    @Async("taskExecutor")
    @Override
	public Future<HashMap<String,Object>> sapDeliveryChangeExecute(Map<String, Object> paramMap) throws JCoException,InterruptedException{
		
		
    	return new AsyncResult<HashMap<String,Object>>(new HashMap<String,Object>());
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
		HashMap<String, Map<String, String>> matInfoMap = new HashMap<String,Map<String,String>>();
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

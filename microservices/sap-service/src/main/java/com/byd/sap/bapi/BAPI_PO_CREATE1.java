package com.byd.sap.bapi;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.byd.sap.bapi.bean.BAPIMEPOHEADER;
import com.byd.sap.bapi.bean.BAPIMEPOITEM;
import com.byd.sap.bapi.bean.BapiPOCreate1Bean;
import com.byd.sap.bapi.bean.POSCHEDULE;
import com.byd.sap.rfc.SAPBapi;
import com.sap.conn.jco.JCoContext;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoStructure;
import com.sap.conn.jco.JCoTable;

/**
 * PO创建
 * @author ren.wei3
 *
 */
@Component
public class BAPI_PO_CREATE1 {

	@Autowired
	private SAPBapi sapBapi;
	
	public Map<String,Object> execute(String werks,BapiPOCreate1Bean beans){
		String sapInit = "";
		if (werks == null) {
			sapBapi.init();
		} else {
			sapInit = sapBapi.init(werks);
		}
		Map<String,Object> returnMap = new HashMap<String,Object>();
		
		if("".equals(sapInit)) {
			JCoFunction function = null;  
			JCoFunction function2 = null;  
			JCoFunction function3 = null;
		    JCoDestination destination = sapBapi.connect();
		    
		    try {
		    	
		    	JCoContext.begin(destination); 
		    	function = destination.getRepository().getFunction("BAPI_PO_CREATE1"); 
		    	function2 = destination.getRepository().getFunction("BAPI_TRANSACTION_COMMIT"); 
		    	function3 = destination.getRepository().getFunction("BAPI_TRANSACTION_ROLLBACK"); 
		    	
		    	JCoStructure poheader = function.getImportParameterList().getStructure("POHEADER");
		    	JCoStructure poheaderx = function.getImportParameterList().getStructure("POHEADERX");
		    	BAPIMEPOHEADER head = beans.getHeader();
		    	if (head == null) {
		    		returnMap.put("CODE", "-10");
		            returnMap.put("MESSAGE", "传入参数不完整");
		            return returnMap;
		    	}
		    	poheader.setValue("DOC_TYPE", head.getDocType());
		    	poheaderx.setValue("DOC_TYPE", "X");
		    	poheader.setValue("DOC_DATE", head.getDocDate());
		    	poheaderx.setValue("DOC_DATE", "X");
		    	poheader.setValue("VENDOR", head.getVendor());
		    	poheaderx.setValue("VENDOR", "X");
		    	poheader.setValue("PURCH_ORG", head.getPurchOrg());
		    	poheaderx.setValue("PURCH_ORG", "X");
		    	poheader.setValue("PUR_GROUP", head.getPurGroup());
		    	poheaderx.setValue("PUR_GROUP", "X");
		    	poheader.setValue("COMP_CODE", head.getCompCode());
		    	poheaderx.setValue("COMP_CODE", "X");
		    	
		    	JCoTable poItemTable = function.getTableParameterList().getTable("POITEM");
		    	JCoTable poItemTablex = function.getTableParameterList().getTable("POITEMX");
		    	List<BAPIMEPOITEM> itemList = beans.getItem();
		    	if (itemList == null || itemList.size() <= 0) {
		    		returnMap.put("CODE", "-10");
		            returnMap.put("MESSAGE", "传入参数不完整");
		            return returnMap;
		    	}
		    	poItemTable.appendRows(itemList.size());
		    	poItemTablex.appendRows(itemList.size());
		    	for(int i=0;i<itemList.size();i++) {
		    		poItemTable.setRow(i);
		    		poItemTablex.setRow(i);
		    		BAPIMEPOITEM item = itemList.get(i);
		    		poItemTable.setValue("PO_ITEM", item.getPoItem());
		    		poItemTablex.setValue("PO_ITEM", item.getPoItem());
		    		poItemTablex.setValue("PO_ITEMX", "X");
		    		poItemTable.setValue("MATERIAL", item.getMaterial());
		    		poItemTablex.setValue("MATERIAL", "X");
		    		poItemTable.setValue("QUANTITY", item.getQuantity());
		    		poItemTablex.setValue("QUANTITY", "X");
		    		poItemTable.setValue("PLANT", item.getPlant());
		    		poItemTablex.setValue("PLANT", "X");
		    		poItemTable.setValue("STGE_LOC", item.getStgeLoc());
		    		poItemTablex.setValue("STGE_LOC", "X");
		    		poItemTable.setValue("PO_UNIT", item.getPoUnit());
		    		poItemTablex.setValue("PO_UNIT", "X");
		    	}
		    	
		    	JCoTable poscheduleTable = function.getTableParameterList().getTable("POSCHEDULE");
		    	JCoTable poscheduleTablex = function.getTableParameterList().getTable("POSCHEDULEX");
		    	List<POSCHEDULE> poscheduleList = beans.getPoschedule();
		    	if (poscheduleList == null || poscheduleList.size() <= 0) {
		    		returnMap.put("CODE", "-10");
		            returnMap.put("MESSAGE", "传入参数不完整");
		            return returnMap;
		    	}
		    	poscheduleTable.appendRows(poscheduleList.size());
		    	poscheduleTablex.appendRows(poscheduleList.size());
		    	for(int i=0;i<poscheduleList.size();i++) {
		    		poscheduleTable.setRow(i);
		    		poscheduleTablex.setRow(i);
		    		POSCHEDULE item = poscheduleList.get(i);
		    		poscheduleTable.setValue("PO_ITEM", item.getPoItem());
		    		poscheduleTablex.setValue("PO_ITEM", item.getPoItem());
		    		poscheduleTablex.setValue("PO_ITEMX", "X");
		    		poscheduleTable.setValue("DELIVERY_DATE", item.getDeliveryDate());
		    		poscheduleTablex.setValue("DELIVERY_DATE", "X");
		    		poscheduleTable.setValue("QUANTITY", item.getQuantity());
		    		poscheduleTablex.setValue("QUANTITY", "X");
		    		poscheduleTable.setValue("SCHED_LINE", "0001");
		    		poscheduleTablex.setValue("SCHED_LINE", "0001");
		    		poscheduleTablex.setValue("SCHED_LINEX", "X");
		    		poscheduleTablex.setValue("PO_DATE", "X");
		    	}
		    	
		    	function.execute(destination); 
		    	//检查是否返回错误信息
		    	JCoTable outputTable = function.getTableParameterList().getTable("RETURN");
		    	String poNumber = function.getExportParameterList().getString("EXPPURCHASEORDER");
		    	boolean successflag = true;
		    	StringBuilder message = new StringBuilder();
		    	for(int i=0;i<outputTable.getNumRows();i++) {
		    		outputTable.setRow(i);
		    		String type = outputTable.getString("TYPE");
		    		if (type.equals("E") || type.equals("A")) {
		    			successflag = false;
		    		}
		    		if (type.equals("E")) {
			            message.append(type);
			            message.append(": ");
			            message.append(outputTable.getString("MESSAGE"));
			            message.append("; ");
		    		}
		    	}
		    	
		    	if (successflag) {
		    		function2.getImportParameterList().setValue("WAIT", "X");
			    	function2.execute(destination); 
			    	returnMap.put("PO_NUMBER", poNumber);
		    	} else {
		    		//调用失败
		    		returnMap.put("CODE", "-2");
		    		returnMap.put("MESSAGE", message);
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
}

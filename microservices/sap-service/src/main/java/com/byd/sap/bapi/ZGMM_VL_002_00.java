package com.byd.sap.bapi;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.byd.sap.rfc.SAPBapi;
import com.sap.conn.jco.JCoContext;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoTable;

/**
 * STO交货单过账
 * @author ren.wei3
 *
 */
@Component
public class ZGMM_VL_002_00 {

	@Autowired
	private SAPBapi sapBapi;
	
	public Map<String,Object> execute(String werks,String vbeln){
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
		    	function = destination.getRepository().getFunction("ZGMM_VL_002_00"); 
		    	function2 = destination.getRepository().getFunction("BAPI_TRANSACTION_COMMIT"); 
		    	function3 = destination.getRepository().getFunction("BAPI_TRANSACTION_ROLLBACK"); 
		    	
		    	JCoParameterList input = function.getImportParameterList();
		        input.setValue("DELIVERY", vbeln);
		        
		        function.execute(destination);
		    	
		    	//检查是否返回错误信息
		    	JCoTable outputTable = function.getTableParameterList().getTable("RETURN");
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
			    	returnMap.put("CODE", "0");
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

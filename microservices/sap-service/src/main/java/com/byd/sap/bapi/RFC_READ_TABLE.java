package com.byd.sap.bapi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.byd.sap.bapi.bean.RfcReadTableBean;
import com.byd.sap.rfc.SAPBapi;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoTable;

/**
 * @author ren.wei3
 *
 */
@Component
public class RFC_READ_TABLE {

	@Autowired
	private SAPBapi sapBapi;
	
	public Map<String,Object> execute(RfcReadTableBean beans){
		sapBapi.init();
		JCoFunction function = null;  
	    JCoDestination destination = sapBapi.connect();
	    Map<String,Object> returnMap = new HashMap<String,Object>();
	    List<String> strList = new ArrayList<String>();
	    
	    if(beans.getQueryTable() == null || beans.getOptions() == null || beans.getFields() == null) {
	    	returnMap.put("CODE", "-10");
            returnMap.put("MESSAGE", "传入参数不完整");
            return returnMap;
	    }
	    
	    try {
		    function = destination.getRepository().getFunction("RFC_READ_TABLE");  
	        JCoParameterList input = function.getImportParameterList();
	        input.setValue("QUERY_TABLE", beans.getQueryTable()); 
	        JCoTable inputOptionsTable = function.getTableParameterList().getTable("OPTIONS");
	        List<String> options = beans.getOptions();
	        inputOptionsTable.appendRows(options.size());
	        for(int i=0;i<options.size();i++) {
	        	inputOptionsTable.setRow(i);
	        	inputOptionsTable.setValue("TEXT", options.get(i));
	        }
	        
	        JCoTable inputFieldsTable = function.getTableParameterList().getTable("FIELDS");
	        List<String> fields = beans.getFields();
	        inputFieldsTable.appendRows(fields.size());
	    	for(int i=0;i<fields.size();i++) {
	    		inputFieldsTable.setRow(i);
	    		inputFieldsTable.setValue("FIELDNAME", fields.get(i));
	    	}
	    	function.execute(destination); 
	    	
	    	JCoTable outputTable=function.getTableParameterList().getTable("DATA");
	    	for(int i=0;i<outputTable.getNumRows();i++) {
	    		outputTable.setRow(i);
	    		String wa = outputTable.getString("WA");
	    		System.out.println("wa=>>>>>>>>>>>>>>>>>>>>" + wa);
	    		strList.add(wa);
	    	}
	    	returnMap.put("DATA", strList);
	    }catch (Exception e) {  
            e.printStackTrace(); 
            returnMap.put("CODE", "-2");
            returnMap.put("MESSAGE", e.getMessage());
        }
	    return returnMap;
	}
}

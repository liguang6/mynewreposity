package com.byd.sap.bapi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.byd.sap.bapi.bean.MaterialStockReadBean;
import com.byd.sap.rfc.SAPBapi;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoTable;

/** 
 * 读取ERP库存
 * @author 作者 : ren.wei3@byd.com 
 * @version 创建时间：2019年6月3日 上午11:13:11 
 * 
 */

@Component
public class MATERIAL_STOCK_READ {

	@Autowired
	private SAPBapi sapBapi;
	
	public Map<String,Object> execute(MaterialStockReadBean beans){
		String sapInit = "";
		if (beans.getPlant() == null) {
			sapBapi.init();
		} else {
			sapInit = sapBapi.init(beans.getPlant());
		}
		
		Map<String,Object> returnMap = new HashMap<String,Object>();
		if("".equals(sapInit)) {
			JCoFunction function = null;  
		    JCoDestination destination = sapBapi.connect();
		    List<Map<String,Object>> dataMapList = new ArrayList<Map<String,Object>>();
		    
		    if(beans.getPlant() == null || beans.getStorageLoc() == null ) {
		    	returnMap.put("CODE", "-10");
	            returnMap.put("MESSAGE", "传入参数不完整");
	            return returnMap;
		    }
		    
		    try {
			    function = destination.getRepository().getFunction("/SPE/MATERIAL_STOCK_READ");  
		        JCoParameterList input = function.getImportParameterList();
		        input.setValue("I_PLANT", beans.getPlant()); 
		        input.setValue("I_STORAGE_LOC", beans.getStorageLoc());
		        JCoTable matnrTable = function.getImportParameterList().getTable("IT_MATNR_RANGE");
		        List<String> matnrList = beans.getMatnrRange();
		        matnrTable.appendRows(matnrList.size());
		        for(int i=0;i<matnrList.size();i++) {
		        	matnrTable.setRow(i);
		        	matnrTable.setValue("LOW", matnrList.get(i)); 
		        	matnrTable.setValue("OPTION", "EQ"); 
		        	matnrTable.setValue("SIGN", "I"); 
		        }
		        
		    	function.execute(destination); 
		    	
		    	JCoTable outputTable=function.getExportParameterList().getTable("ET_MAT_STOCK");
		    	for(int i=0;i<outputTable.getNumRows();i++) {
		    		outputTable.setRow(i);
		    		String MATNR = outputTable.getString("MATNR");
		    		String CHARG = outputTable.getString("CHARG");
		    		String SOBKZ = outputTable.getString("SOBKZ");
		    		String LABST = outputTable.getString("LABST");
		    		String SPEME = outputTable.getString("SPEME");
		    		String LIFNR = outputTable.getString("LIFNR");
		    		String KLABS = outputTable.getString("KLABS");
		    		Map<String,Object> dateMap = new HashMap<String,Object>();
		    		dateMap.put("MATNR", MATNR);
		    		dateMap.put("CHARG", CHARG);
		    		dateMap.put("SOBKZ", SOBKZ);
		    		dateMap.put("LABST", LABST);
		    		dateMap.put("SPEME", SPEME);
		    		dateMap.put("LIFNR", LIFNR);
		    		dateMap.put("KLABS", KLABS);
		    		dataMapList.add(dateMap);
		    	}
		    	returnMap.put("DATA", JSONObject.toJSONString(dataMapList));
		    }catch (Exception e) {  
	            e.printStackTrace(); 
	            returnMap.put("CODE", "-2");
	            returnMap.put("MESSAGE", e.getMessage());
	        }
		}else {
			returnMap.put("CODE", "-4");
			returnMap.put("MESSAGE", sapInit);
		}
	    return returnMap;
	}
}

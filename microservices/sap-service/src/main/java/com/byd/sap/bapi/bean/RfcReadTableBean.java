package com.byd.sap.bapi.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 
 * @author 作者 : ren.wei3@byd.com 
 * @version 创建时间：2019年5月31日 上午9:31:40 
 *
 */
public class RfcReadTableBean implements Serializable{

	private String queryTable;
	
	private List<String> options;
	
	private List<String> fields;

	public String getQueryTable() {
		return queryTable;
	}

	public void setQueryTable(String queryTable) {
		this.queryTable = queryTable;
	}

	public List<String> getOptions() {
		return options;
	}

	public void setOptions(List<String> options) {
		this.options = options;
	}

	public List<String> getFields() {
		return fields;
	}

	public void setFields(List<String> fields) {
		this.fields = fields;
	}
	
}

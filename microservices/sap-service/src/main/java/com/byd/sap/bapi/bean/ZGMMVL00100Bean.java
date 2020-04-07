package com.byd.sap.bapi.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 
 * @author 作者 : ren.wei3@byd.com 
 * @version 创建时间：2019年5月31日 上午9:31:50 
 *
 */
public class ZGMMVL00100Bean implements Serializable{

	private String dueDate;
	
	private List<BAPIDLVREFTOSTO> item;

	public String getDueDate() {
		return dueDate;
	}

	public void setDueDate(String dueDate) {
		this.dueDate = dueDate;
	}

	public List<BAPIDLVREFTOSTO> getItem() {
		return item;
	}

	public void setItem(List<BAPIDLVREFTOSTO> item) {
		this.item = item;
	}
	
	
}

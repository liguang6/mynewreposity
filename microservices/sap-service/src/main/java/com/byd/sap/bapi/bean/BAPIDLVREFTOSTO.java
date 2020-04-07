package com.byd.sap.bapi.bean;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * STO交货单创建行明细
 * @author ren.wei3
 *
 */
public class BAPIDLVREFTOSTO implements Serializable{

	private String refDoc;
	
	private String refItem;
	
	private BigDecimal dlvQty;
	
	private String salesUnit;

	public String getRefDoc() {
		return refDoc;
	}

	public void setRefDoc(String refDoc) {
		this.refDoc = refDoc;
	}

	public String getRefItem() {
		return refItem;
	}

	public void setRefItem(String refItem) {
		this.refItem = refItem;
	}

	public BigDecimal getDlvQty() {
		return dlvQty;
	}

	public void setDlvQty(BigDecimal dlvQty) {
		this.dlvQty = dlvQty;
	}

	public String getSalesUnit() {
		return salesUnit;
	}

	public void setSalesUnit(String salesUnit) {
		this.salesUnit = salesUnit;
	}
	
	
}

package com.byd.sap.bapi.bean;

import java.io.Serializable;

/**
 * 
 * @author 作者 : ren.wei3@byd.com 
 * @version 创建时间：2019年5月31日 上午9:30:34 
 *
 */
public class BAPIMEPOHEADER implements Serializable{

	private String docType;
	
	private String docDate;
	
	private String vendor;
	
	private String purchOrg; //采购组织
	
	private String purGroup; //采购组
	
	private String compCode; //公司代码

	public String getDocType() {
		return docType;
	}

	public void setDocType(String docType) {
		this.docType = docType;
	}

	public String getDocDate() {
		return docDate;
	}

	public void setDocDate(String docDate) {
		this.docDate = docDate;
	}

	public String getVendor() {
		return vendor;
	}

	public void setVendor(String vendor) {
		this.vendor = vendor;
	}

	public String getPurchOrg() {
		return purchOrg;
	}

	public void setPurchOrg(String purchOrg) {
		this.purchOrg = purchOrg;
	}

	public String getPurGroup() {
		return purGroup;
	}

	public void setPurGroup(String purGroup) {
		this.purGroup = purGroup;
	}

	public String getCompCode() {
		return compCode;
	}

	public void setCompCode(String compCode) {
		this.compCode = compCode;
	}
	
	
}

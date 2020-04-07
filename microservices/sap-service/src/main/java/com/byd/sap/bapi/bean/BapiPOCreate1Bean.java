package com.byd.sap.bapi.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 
 * @author 作者 : ren.wei3@byd.com 
 * @version 创建时间：2019年5月31日 上午9:31:07 
 *
 */
public class BapiPOCreate1Bean implements Serializable{
	
	private BAPIMEPOHEADER header;

	private List<BAPIMEPOITEM> item;
	
	private List<POSCHEDULE> poschedule;
	
	public BAPIMEPOHEADER getHeader() {
		return header;
	}

	public void setHeader(BAPIMEPOHEADER header) {
		this.header = header;
	}

	public List<BAPIMEPOITEM> getItem() {
		return item;
	}

	public void setItem(List<BAPIMEPOITEM> item) {
		this.item = item;
	}

	public List<POSCHEDULE> getPoschedule() {
		return poschedule;
	}

	public void setPoschedule(List<POSCHEDULE> poschedule) {
		this.poschedule = poschedule;
	}

	
}

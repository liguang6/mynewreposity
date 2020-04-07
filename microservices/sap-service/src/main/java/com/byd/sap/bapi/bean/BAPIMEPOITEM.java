package com.byd.sap.bapi.bean;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 
 * @author 作者 : ren.wei3@byd.com 
 * @version 创建时间：2019年5月31日 上午9:30:45 
 *
 */
public class BAPIMEPOITEM implements Serializable{

	private String poItem;
	
	private String material;
	
	private BigDecimal quantity;
	
	private String plant;
	
	private String stgeLoc;
	
	private String poUnit;

	public String getPoItem() {
		return poItem;
	}

	public void setPoItem(String poItem) {
		this.poItem = poItem;
	}

	public String getMaterial() {
		return material;
	}

	public void setMaterial(String material) {
		this.material = material;
	}

	public BigDecimal getQuantity() {
		return quantity;
	}

	public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity;
	}

	public String getPlant() {
		return plant;
	}

	public void setPlant(String plant) {
		this.plant = plant;
	}

	public String getStgeLoc() {
		return stgeLoc;
	}

	public void setStgeLoc(String stgeLoc) {
		this.stgeLoc = stgeLoc;
	}

	public String getPoUnit() {
		return poUnit;
	}

	public void setPoUnit(String poUnit) {
		this.poUnit = poUnit;
	}
	
	
}

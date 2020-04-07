package com.byd.sap.bapi.bean;

import java.io.Serializable;
import java.util.List;

/** 
* @author 作者 : ren.wei3@byd.com 
* @version 创建时间：2019年6月3日 上午11:25:08 
* 
*/

public class MaterialStockReadBean implements Serializable{
	
	//工厂
	private String plant;
	//库位
	private String storageLoc;
	//物料范围
	private List<String> matnrRange;
	
	public String getPlant() {
		return plant;
	}
	public void setPlant(String plant) {
		this.plant = plant;
	}
	public String getStorageLoc() {
		return storageLoc;
	}
	public void setStorageLoc(String storageLoc) {
		this.storageLoc = storageLoc;
	}
	public List<String> getMatnrRange() {
		return matnrRange;
	}
	public void setMatnrRange(List<String> matnrRange) {
		this.matnrRange = matnrRange;
	}
	
	
}

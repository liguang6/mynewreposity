package com.byd.sap.bapi.bean;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 
 * @author 作者 : ren.wei3@byd.com 
 * @version 创建时间：2019年5月31日 上午9:31:16 
 *
 */
public class POSCHEDULE implements Serializable{

	private String poItem;
	
	private String deliveryDate;
	
	private BigDecimal quantity;

	public String getPoItem() {
		return poItem;
	}

	public void setPoItem(String poItem) {
		this.poItem = poItem;
	}

	public String getDeliveryDate() {
		return deliveryDate;
	}

	public void setDeliveryDate(String deliveryDate) {
		this.deliveryDate = deliveryDate;
	}

	public BigDecimal getQuantity() {
		return quantity;
	}

	public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity;
	}
	
	
}

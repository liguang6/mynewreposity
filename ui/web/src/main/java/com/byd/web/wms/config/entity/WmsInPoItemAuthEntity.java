package com.byd.web.wms.config.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotations.KeySequence;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

/** 
 * @author 作者 E-mail: peng.tao1@byd.com
 * @version 创建时间：2018年8月31日 上午10:24:03 
 * 类说明 
 */
@TableName("WMS_IN_PO_ITEM_AUTH")
@KeySequence("SEQ_WMS_IN_PO_ITEM_AUTH")//使用oracle 注解自增
public class WmsInPoItemAuthEntity implements Serializable {
	@TableId(value="ID",type=IdType.INPUT)
	private Long id;
	/*
	 * 采购工厂代码
	 */
	private String werks;
	/*
	 * 供应商代码
	 */
	private String lifnr;
	/**
	 * 采购订单号
	 */
	private String ebeln;
	/**
	 * 行项目号
	 */
	private String ebelp;
	/**
	 * 物料号
	 */
	private String matnr;
	/**
	 * 物料描述
	 */
	private String txz01;
	/**
	 * 最大可收货数量
	 */
	private BigDecimal maxMenge;
	
	/**
	 * 授权工厂代码 多个工厂使用,隔开
	 */
	private String authWerks;
	/*
	 * 删除标识 默认 0 否 X 是 
	 */
	private String del;
	/**
	 * 创建人
	 */
	private String creator;
	/*
	 * 创建时间
	 */
	private String creatDate;
	/*
	 * 编辑人
	 */
	private String editor;
	/**
	 * 编辑时间
	 */
	private String editDate;
	
	@TableField(exist=false)
	private String msg;
	@TableField(exist=false)
	private String rowNo;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getEbeln() {
		return ebeln;
	}
	public void setEbeln(String ebeln) {
		this.ebeln = ebeln;
	}
	public String getEbelp() {
		return ebelp;
	}
	public void setEbelp(String ebelp) {
		this.ebelp = ebelp;
	}
	public String getWerks() {
		return werks;
	}
	public void setWerks(String werks) {
		this.werks = werks;
	}
	public String getCreator() {
		return creator;
	}
	public void setCreator(String creator) {
		this.creator = creator;
	}
	public String getCreatDate() {
		return creatDate;
	}
	public void setCreatDate(String creatDate) {
		this.creatDate = creatDate;
	}
	public String getEditor() {
		return editor;
	}
	public void setEditor(String editor) {
		this.editor = editor;
	}
	public String getEditDate() {
		return editDate;
	}
	public void setEditDate(String editDate) {
		this.editDate = editDate;
	}
	public String getLifnr() {
		return lifnr;
	}
	public void setLifnr(String lifnr) {
		this.lifnr = lifnr;
	}
	public String getMatnr() {
		return matnr;
	}
	public void setMatnr(String matnr) {
		this.matnr = matnr;
	}
	public String getTxz01() {
		return txz01;
	}
	public void setTxz01(String txz01) {
		this.txz01 = txz01;
	}
	public BigDecimal getMaxMenge() {
		return maxMenge;
	}
	public void setMaxMenge(BigDecimal maxMenge) {
		this.maxMenge = maxMenge;
	}
	public String getAuthWerks() {
		return authWerks;
	}
	public void setAuthWerks(String authWerks) {
		this.authWerks = authWerks;
	}
	public String getDel() {
		return del;
	}
	public void setDel(String del) {
		this.del = del;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public String getRowNo() {
		return rowNo;
	}
	public void setRowNo(String rowNo) {
		this.rowNo = rowNo;
	}
	
}

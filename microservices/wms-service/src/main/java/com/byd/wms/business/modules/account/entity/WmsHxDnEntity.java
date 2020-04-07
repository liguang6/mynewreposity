package com.byd.wms.business.modules.account.entity;

import com.baomidou.mybatisplus.annotations.KeySequence;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

import java.io.Serializable;

/**
 * 核销业务：SAP交货单核销实体类
 * @author (changsha) byd_infomation_center
 * @date 2018-09-17
 */
@TableName("WMS_HX_DN")
@KeySequence("SEQ_WMS_HX_DN")
public class WmsHxDnEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * ID
	 */
	@TableId(value="ID",type=IdType.INPUT)
	private Long id;
	
	/**
	 * 发货工厂代码F_WERKS
	 */
	@TableField(value = "F_WERKS")
	private String fWerks;
	/**
	 * 收货工厂代码 WERKS
	 */
	@TableField(value = "WERKS")
	private String werks;
	/**
	 * 发货WMS仓库号 F_WH_NUMBER
	 */
	@TableField(value = "F_WH_NUMBER")
	private String fWhNumber;
	/**
	 * 收货WMS仓库号 WH_NUMBER
	 */
	@TableField(value = "WH_NUMBER")
	private String whNumber;
	/**
	 * 供应商代码 LIFNR
	 */
	private String lifnr;
	/**
	 * SAP交货单号 VBELN
	 */
	private String vbeln;
	/**
	 * SAP交货单行项目号 POSNR
	 */
	private String posnr;
	/**
	 * 参考采购订单号 EBELN
	 */
	private String ebeln;
	/**
	 * 参考采购订单行项目号 EBELP
	 */
	private String ebelp;

	/**
	 * 收货库位
	 
	private String lgort; */
	/**
	 * 物料号
	 */
	private String matnr;
	/**
	 * 物料描述
	 */
	private String maktx;
	/**
	 * 交货单位 UNIT
	 */
	private String unit;
	/**
	 * 交货数量 LFIMG
	 */
	private Double lfimg;
	/**
	 * 虚发311调拨数量 XF311T- 核销调拨发货过账，更新此字段
	 */
	@TableField(value = "XF311T")
	private Double xf311t;
	/**
	 * 实发311调拨数量 SF311T- 实物调拨发货过账，更新此字段
	 */
	@TableField(value = "SF311T")
	private Double sf311t;
	/**
	 * 实发312数量 SF312T-实物调拨发货冲销、取消，更新此字段
	 */
	@TableField(value = "SF312T")
	private Double sf312t;
	/**
	 * 虚发剩余核销数量 HX_QTY_XF
	 */
	@TableField(value = "HX_QTY_XF")
	private Double hxQtyXf;
	
	/**
	 * 虚收101数量 XS101T-核销STO交货单收货过账，更新此字段
	 */
	@TableField(value = "XS101T")
	private Double xs101t;
	/**
	 * 虚退161数量 XT161T-核销STO退货交货单收货过账，更新此字段
	 */
	@TableField(value = "XT161T")
	private Double xt161t;
	/**
	 * 实收103数量 SS103T-实物收料房收料，更新此字段
	 */
	@TableField(value = "SS103T")
	private Double ss103t;
	/**
	 * 实收124数量 SS124T-实物收料房退货，更新此字段
	 */
	@TableField(value = "SS124T")
	private Double ss124t;

	/**
	 * 虚收剩余核销数量 HX_QTY_XS
	 */
	@TableField(value = "HX_QTY_XS")
	private Double hxQtyXs;
	/**
	 * 取消标识 CANCEL_FLAG  默认0 否 X 时  WMS凭证取消时更新此字段
	 */
	private String cancelFlag;
	/**
	 * 记录创建人
	 */
	private String creator;
	/**
	 * 记录创建时间
	 */
	private String createDate;
	/**
	 * 记录修改人 EDITOR
	 */
	private String editor;
	/**
	 * 记录修改时间 EDIT_DATE
	 */
	private String editDate;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getfWerks() {
		return fWerks;
	}
	public void setfWerks(String fWerks) {
		this.fWerks = fWerks;
	}
	public String getWerks() {
		return werks;
	}
	public void setWerks(String werks) {
		this.werks = werks;
	}
	public String getfWhNumber() {
		return fWhNumber;
	}
	public void setfWhNumber(String fWhNumber) {
		this.fWhNumber = fWhNumber;
	}
	public String getWhNumber() {
		return whNumber;
	}
	public void setWhNumber(String whNumber) {
		this.whNumber = whNumber;
	}
	public String getLifnr() {
		return lifnr;
	}
	public void setLifnr(String lifnr) {
		this.lifnr = lifnr;
	}
	public String getVbeln() {
		return vbeln;
	}
	public void setVbeln(String vbeln) {
		this.vbeln = vbeln;
	}
	public String getPosnr() {
		return posnr;
	}
	public void setPosnr(String posnr) {
		this.posnr = posnr;
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
	public String getMatnr() {
		return matnr;
	}
	public void setMatnr(String matnr) {
		this.matnr = matnr;
	}
	public String getMaktx() {
		return maktx;
	}
	public void setMaktx(String maktx) {
		this.maktx = maktx;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public Double getLfimg() {
		return lfimg;
	}
	public void setLfimg(Double lfimg) {
		this.lfimg = lfimg;
	}
	public Double getXf311t() {
		return xf311t;
	}
	public void setXf311t(Double xf311t) {
		this.xf311t = xf311t;
	}
	public Double getSf311t() {
		return sf311t;
	}
	public void setSf311t(Double sf311t) {
		this.sf311t = sf311t;
	}
	public Double getSf312t() {
		return sf312t;
	}
	public void setSf312t(Double sf312t) {
		this.sf312t = sf312t;
	}
	public Double getHxQtyXf() {
		return hxQtyXf;
	}
	public void setHxQtyXf(Double hxQtyXf) {
		this.hxQtyXf = hxQtyXf;
	}
	public Double getXs101t() {
		return xs101t;
	}
	public void setXs101t(Double xs101t) {
		this.xs101t = xs101t;
	}
	public Double getXt161t() {
		return xt161t;
	}
	public void setXt161t(Double xt161t) {
		this.xt161t = xt161t;
	}
	public Double getSs103t() {
		return ss103t;
	}
	public void setSs103t(Double ss103t) {
		this.ss103t = ss103t;
	}
	public Double getSs124t() {
		return ss124t;
	}
	public void setSs124t(Double ss124t) {
		this.ss124t = ss124t;
	}
	public Double getHxQtyXs() {
		return hxQtyXs;
	}
	public void setHxQtyXs(Double hxQtyXs) {
		this.hxQtyXs = hxQtyXs;
	}
	public String getCancelFlag() {
		return cancelFlag;
	}
	public void setCancelFlag(String cancelFlag) {
		this.cancelFlag = cancelFlag;
	}
	public String getCreator() {
		return creator;
	}
	public void setCreator(String creator) {
		this.creator = creator;
	}
	public String getCreateDate() {
		return createDate;
	}
	public void setCreateDate(String createDate) {
		this.createDate = createDate;
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
}

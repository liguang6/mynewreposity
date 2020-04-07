package com.byd.wms.business.modules.account.entity;

import com.baomidou.mybatisplus.annotations.KeySequence;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

import java.io.Serializable;

/**
 * 核销业务：SAP303调拨单核销信息
 * @author (changsha) byd_infomation_center
 * @date 2018-10-10
 */
@TableName("WMS_HX_TO")
@KeySequence("SEQ_WMS_HX_TO")
public class WmsHxToEntity implements Serializable {
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
	 * SAP303凭证编号  SAP_MATDOC_NO
	 */
	@TableField(value = "SAP_MATDOC_NO")
	private String sapMatDocNo;
	/**
	 * SAP303凭证行项目编号 SAP_MATDOC_ITEM_NO
	 */
	@TableField(value = "SAP_MATDOC_ITEM_NO")
	private String sapMatDocItemNo;
	/**
	 * 供应商代码 LIFNR
	 */
	private String lifnr;

	/**
	 * 物料号
	 */
	private String matnr;
	/**
	 * 物料描述
	 */
	private String maktx;
	/**
	 * 调拨单位 ENTRY_UOM
	 */
	private String entryUom;
	/**
	 * 调拨数量 ENTRY_QNT
	 */
	private Double entryQnt;
	/**
	 * 303发货数量(虚发)- 303虚发更新此字段
	 */
	@TableField(value = "XF303")
	private Double xf303;
	/**
	 * 304发货数量(虚发)- 304虚发更新此字段，冲销 取消XF303凭证
	 */
	@TableField(value = "XF304")
	private Double xf304;
	/**
	 * 303虚发剩余核销数量-实物调拨发货冲销、取消，更新此字段
	 */
	@TableField(value = "HX_QTY_XF")
	private Double hxQtyXf;
	
	/**
	 * 303发货数量(实发)-303实发更新此字段
	 */
	@TableField(value = "SF303")
	private Double sf303;
	/**
	 * 304发货数量(实发)-304实发更新此字段，冲销 取消SF303凭证
	 */
	@TableField(value = "SF304")
	private Double sf304;
	/**
	 * 虚收305数量-305虚收更新此字段
	 */
	@TableField(value = "XS305")
	private Double xs305;
	/**
	 * 虚收306数量-306，虚收更新此字段，冲销 取消XS305凭证
	 */
	@TableField(value = "XS306")
	private Double xs306;
	
	/**
	 * 实收303DB数量-收料房实收更新此字段
	 */
	@TableField(value = "SS303DB")
	private Double ss303db;
	/**
	 * 实收304DB数量-收料房实收退货更新此字段
	 */
	@TableField(value = "SS304DB")
	private Double ss304db;

	/**
	 * 303虚收剩余核销数量
	 */
	@TableField(value = "HX_QTY_XS")
	private Double hxQtyXs;
	
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
	public String getEntryUom() {
		return entryUom;
	}
	public void setEntryUom(String entryUom) {
		this.entryUom = entryUom;
	}
	public Double getEntryQnt() {
		return entryQnt;
	}
	public void setEntryQnt(Double entryQnt) {
		this.entryQnt = entryQnt;
	}
	public Double getXf303() {
		return xf303;
	}
	public void setXf303(Double xf303) {
		this.xf303 = xf303;
	}
	public Double getXf304() {
		return xf304;
	}
	public void setXf304(Double xf304) {
		this.xf304 = xf304;
	}
	public Double getHxQtyXf() {
		return hxQtyXf;
	}
	public void setHxQtyXf(Double hxQtyXf) {
		this.hxQtyXf = hxQtyXf;
	}
	public Double getSf303() {
		return sf303;
	}
	public void setSf303(Double sf303) {
		this.sf303 = sf303;
	}
	public Double getSf304() {
		return sf304;
	}
	public void setSf304(Double sf304) {
		this.sf304 = sf304;
	}
	public Double getXs305() {
		return xs305;
	}
	public void setXs305(Double xs305) {
		this.xs305 = xs305;
	}
	public Double getXs306() {
		return xs306;
	}
	public void setXs306(Double xs306) {
		this.xs306 = xs306;
	}
	public Double getSs303db() {
		return ss303db;
	}
	public void setSs303db(Double ss303db) {
		this.ss303db = ss303db;
	}
	public Double getSs304db() {
		return ss304db;
	}
	public void setSs304db(Double ss304db) {
		this.ss304db = ss304db;
	}
	public Double getHxQtyXs() {
		return hxQtyXs;
	}
	public void setHxQtyXs(Double hxQtyXs) {
		this.hxQtyXs = hxQtyXs;
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
	public String getSapMatDocNo() {
		return sapMatDocNo;
	}
	public void setSapMatDocNo(String sapMatDocNo) {
		this.sapMatDocNo = sapMatDocNo;
	}
	public String getSapMatDocItemNo() {
		return sapMatDocItemNo;
	}
	public void setSapMatDocItemNo(String sapMatDocItemNo) {
		this.sapMatDocItemNo = sapMatDocItemNo;
	}
	
}

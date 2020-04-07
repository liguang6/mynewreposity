package com.byd.wms.webservice.ws.entity;

import com.baomidou.mybatisplus.annotations.KeySequence;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

import java.io.Serializable;


/**
 *  打印标签表
 */
@TableName("WMS_CORE_LABEL")
@KeySequence("SEQ_WMS_CORE_LABEL")//使用oracle 注解自增
public class WmsKnLabelRecordEntity implements Serializable {

	@TableField(value="ID")
	private Long id;

	@TableId(value="LABEL_NO",type= IdType.INPUT)
	private String label_no;

	@TableField(value="BOX_QTY")
	private String box_qty;

	@TableField(value="MATNR")
	private String matnr;

	@TableField(value="LIFNR")
	private String lifnr;

	@TableField(value="F_BATCH")
	private String f_batch;

	@TableField(value="UNIT")
	private String unit;

	@TableField(value="REMARK")
	private String remark;

	@TableField(value="WERKS")
	private String werks;

	@TableField(value="LIKTX")
	private String liktx;

	@TableField(value="PO_NO")
	private String po_no;

	@TableField(value="PO_ITEM_NO")
	private String po_item_no;

	@TableField(value="PRODUCT_DATE")
	private String product_date;

	@TableField(value="EFFECT_DATE")
	private String effect_date;

	@TableField(value="BATCH")
	private String batch;

	@TableField(value="F_LABEL_NO")
	private String f_label_no;

	@TableField(value="MAKTX")
	private String maktx;

	@TableField(value="SOBKZ")
	private String sobkz;

	@TableField(value="LGORT")
	private String lgort;

	@TableField(value="LABEL_STATUS")
	private String label_status;

	@TableField(value="DEl")
	private String del;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}


	public String getLabel_no() {
		return label_no;
	}

	public void setLabel_no(String label_no) {
		this.label_no = label_no;
	}

	public String getBox_qty() {
		return box_qty;
	}

	public void setBox_qty(String box_qty) {
		this.box_qty = box_qty;
	}

	public String getF_batch() {
		return f_batch;
	}

	public void setF_batch(String f_batch) {
		this.f_batch = f_batch;
	}

	public String getMatnr() {
		return matnr;
	}

	public void setMatnr(String matnr) {
		this.matnr = matnr;
	}

	public String getLifnr() {
		return lifnr;
	}

	public void setLifnr(String lifnr) {
		this.lifnr = lifnr;
	}


	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getWerks() {
		return werks;
	}

	public void setWerks(String werks) {
		this.werks = werks;
	}

	public String getLiktx() {
		return liktx;
	}

	public void setLiktx(String liktx) {
		this.liktx = liktx;
	}

	public String getPo_no() {
		return po_no;
	}

	public void setPo_no(String po_no) {
		this.po_no = po_no;
	}

	public String getPo_item_no() {
		return po_item_no;
	}

	public void setPo_item_no(String po_item_no) {
		this.po_item_no = po_item_no;
	}

	public String getProduct_date() {
		return product_date;
	}

	public void setProduct_date(String product_date) {
		this.product_date = product_date;
	}

	public String getEffect_date() {
		return effect_date;
	}

	public void setEffect_date(String effect_date) {
		this.effect_date = effect_date;
	}

	public String getBatch() {
		return batch;
	}

	public void setBatch(String batch) {
		this.batch = batch;
	}

	public String getF_label_no() {
		return f_label_no;
	}

	public void setF_label_no(String f_label_no) {
		this.f_label_no = f_label_no;
	}

	public String getMaktx() {
		return maktx;
	}

	public void setMaktx(String maktx) {
		this.maktx = maktx;
	}

	public String getSobkz() {
		return sobkz;
	}

	public void setSobkz(String sobkz) {
		this.sobkz = sobkz;
	}

	public String getLgort() {
		return lgort;
	}

	public void setLgort(String lgort) {
		this.lgort = lgort;
	}

	public String getLabel_status() {
		return label_status;
	}

	public void setLabel_status(String label_status) {
		this.label_status = label_status;
	}

	public String getDel() {
		return del;
	}

	public void setDel(String del) {
		this.del = del;
	}
}

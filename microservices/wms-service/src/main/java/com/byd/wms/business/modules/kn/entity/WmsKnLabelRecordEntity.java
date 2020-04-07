package com.byd.wms.business.modules.kn.entity;

import com.baomidou.mybatisplus.annotations.KeySequence;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

import java.io.Serializable;


/**
 * @ClassName WmsKnLabelRecordEntity
 * @Description
 * @Author qiu.jiaming1
 * @Date 2019/4/1
 */
@TableName("WMS_CORE_LABEL")
@KeySequence("SEQ_WMS_CORE_LABEL")//使用oracle 注解自增
public class WmsKnLabelRecordEntity implements Serializable {
	@TableId(value="ID",type= IdType.INPUT)
	private Long id;
	@TableId(value="LABEL_NO")
	private String LABELNO;
	@TableId(value="BOX_QTY")
	private String BOXQTY;
	@TableId(value="MATNR")
	private String MATNR;
	@TableId(value="LIFNR")
	private String LIFNR;
	@TableId(value="F_BATCH")
	private String FBATCH;
	//private String BATCH;
	@TableId(value="UNIT")
	private String UNIT;
	@TableId(value="REMARK")
	private String REMARK;
	private String editor;
	private String editDate;
	private String creator;
	private String createDate;
	private String del;

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

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}


	public String getMATNR() {
		return MATNR;
	}

	public void setMATNR(String MATNR) {
		this.MATNR = MATNR;
	}

	public String getLIFNR() {
		return LIFNR;
	}

	public void setLIFNR(String LIFNR) {
		this.LIFNR = LIFNR;
	}


	/*public String getBATCH() {
		return BATCH;
	}

	public void setBATCH(String BATCH) {
		this.BATCH = BATCH;
	}*/

	public String getUNIT() {
		return UNIT;
	}

	public void setUNIT(String UNIT) {
		this.UNIT = UNIT;
	}

	public String getLABELNO() {
		return LABELNO;
	}

	public void setLABELNO(String LABELNO) {
		this.LABELNO = LABELNO;
	}

	public String getBOXQTY() {
		return BOXQTY;
	}

	public void setBOXQTY(String BOXQTY) {
		this.BOXQTY = BOXQTY;
	}

	public String getFBATCH() {
		return FBATCH;
	}

	public void setFBATCH(String FBATCH) {
		this.FBATCH = FBATCH;
	}

	public String getREMARK() {
		return REMARK;
	}

	public void setREMARK(String REMARK) {
		this.REMARK = REMARK;
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

	public String getDel() {
		return del;
	}

	public void setDel(String del) {
		this.del = del;
	}

	@Override
	public String toString() {
		return "WmsKnLabelRecordEntity{" +
				"id=" + id +
				", LABELNO='" + LABELNO + '\'' +
				", BOXQTY='" + BOXQTY + '\'' +
				", MATNR='" + MATNR + '\'' +
				", LIFNR='" + LIFNR + '\'' +
				", FBATCH='" + FBATCH + '\'' +
				", UNIT='" + UNIT + '\'' +
				", REMARK='" + REMARK + '\'' +
				", editor='" + editor + '\'' +
				", editDate='" + editDate + '\'' +
				", creator='" + creator + '\'' +
				", createDate='" + createDate + '\'' +
				", del='" + del + '\'' +
				'}';
	}
}

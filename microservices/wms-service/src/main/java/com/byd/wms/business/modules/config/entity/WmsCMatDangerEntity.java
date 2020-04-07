package com.byd.wms.business.modules.config.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotations.KeySequence;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
/**
 * 危化品物料配置表
 * 
 * @author tangj
 * @email 
 * @date 2018年08月01日 
 */
@TableName("WMS_C_MAT_DANGER")
@KeySequence("SEQ_WMS_C_MAT_DANGER")
public class WmsCMatDangerEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@TableId(value = "ID",type=IdType.INPUT)
	private Long id;
	// 工厂代码
	private String werks;
	//供应商代码
	private String lifnr;
	// 物料号
	private String matnr;
	// 物料描述
	private String maktx;
	// 是否危化品物料
	private char dangerFlag;
	//保质期时长
	private double goodDates;
	private double minGoodDates;//MIN_GOOD_DATES;
	private char extendedEffectDate;//EXTENDED_EFFECT_DATE
	// 备注
	private String memo;
	// 软删标示
	private String del;
	// 创建者
	private String creator;
	// 创建日期
	private String createDate;
	// 编辑者
	private String editor;
    // 编辑日期
	private String editDate;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getWerks() {
		return werks;
	}
	public void setWerks(String werks) {
		this.werks = werks;
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
	public char getDangerFlag() {
		return dangerFlag;
	}
	public void setDangerFlag(char dangerFlag) {
		this.dangerFlag = dangerFlag;
	}
	public double getGoodDates() {
		return goodDates;
	}
	public void setGoodDates(double goodDates) {
		this.goodDates = goodDates;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	public String getDel() {
		return del;
	}
	public void setDel(String del) {
		this.del = del;
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
	public double getMinGoodDates() {
		return minGoodDates;
	}
	public void setMinGoodDates(double minGoodDates) {
		this.minGoodDates = minGoodDates;
	}
	public char getExtendedEffectDate() {
		return extendedEffectDate;
	}
	public void setExtendedEffectDate(char extendedEffectDate) {
		this.extendedEffectDate = extendedEffectDate;
	}
}

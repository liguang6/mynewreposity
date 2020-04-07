package com.byd.wms.business.modules.config.entity;

import java.io.Serializable;
import com.baomidou.mybatisplus.annotations.KeySequence;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

/**
 * 物料替代
 * 
 */
@TableName("WMS_C_MAT_REPLACE")
@KeySequence("SEQ_WMS_C_MAT_REPLACE")
public class WmsCMatReplaceEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * ID
	 */
	@TableId(value = "ID", type = IdType.INPUT)
	private Long id;
	/**
	 * 工厂
	 */
	@TableField(value="FACTORY_CODE")
	private String werks;
	/**
	 * 物料号
	 */
	 @TableField(value="MATERIAL_CODE")
	private String matnr;
	/**
	 * 物料描述
	 */
	 @TableField(value="MATERIEL_DESC")
	private String maktx;
	/**
	 * 客户物料号
	 */
	private String cMaterialCode;
	/**
	 * 客户物料描述
	 */
	private String cMaterielDesc;
	/**
	 * 内部替代料号
	 */
	private String innerlMaterialCode;
	/**
	 * 内部替代料描述
	 */
	private String innerlMaterialDesc;
	
	/**
	 * 删除标示 空,0 否 X是 默认空
	 */
	private String del;
	
	private String creator;
	
	private String createDate;
	
	private String editor;
	
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

	public String getcMaterialCode() {
		return cMaterialCode;
	}

	public void setcMaterialCode(String cMaterialCode) {
		this.cMaterialCode = cMaterialCode;
	}

	public String getcMaterielDesc() {
		return cMaterielDesc;
	}

	public void setcMaterielDesc(String cMaterielDesc) {
		this.cMaterielDesc = cMaterielDesc;
	}

	public String getInnerlMaterialCode() {
		return innerlMaterialCode;
	}

	public void setInnerlMaterialCode(String innerlMaterialCode) {
		this.innerlMaterialCode = innerlMaterialCode;
	}

	public String getInnerlMaterialDesc() {
		return innerlMaterialDesc;
	}

	public void setInnerlMaterialDesc(String innerlMaterialDesc) {
		this.innerlMaterialDesc = innerlMaterialDesc;
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

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getDel() {
		return del;
	}

	public void setDel(String del) {
		this.del = del;
	}
	
	
}

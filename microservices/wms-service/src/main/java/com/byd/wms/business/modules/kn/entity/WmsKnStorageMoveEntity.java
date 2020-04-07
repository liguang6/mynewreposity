package com.byd.wms.business.modules.kn.entity;

import com.baomidou.mybatisplus.annotations.KeySequence;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

import java.io.Serializable;
import java.util.Date;

/**
 * 移储记录表
 * 
 * @author cscc
 * @email 
 * @date 2018-11-07 15:54:40
 */
@TableName("WMS_KN_MOVE_STORAGE")
@KeySequence("SEQ_WMS_KN_MOVE_STORAGE")
public class WmsKnStorageMoveEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * $column.comments
	 */
	@TableId(value = "ID",type=IdType.INPUT)
	private Long id;
	/**
	 * 工厂代码
	 */
	private String werks;
	/**
	 * 仓库号
	 */
	private String whNumber;
	/**
	 * 物料号
	 */
	private String matnr;
	/**
	 * 物料描述
	 */
	private String maktx;
	/**
	 * 批次
	 */
	private String batch;
	/**
	 * 库位
	 */
	private String lgort;
	/**
	 * 源储位代码
	 */
	private String fBinCode;
	/**
	 * 源储位名称
	 */
	private String fBinName;
	/**
	 * 目标储位代码
	 */
	private String binCode;
	/**
	 * 目标储位名称
	 */
	private String binName;
	/**
	 * 供应商代码
	 */
	private String lifnr;
	/**
	 * 供应商名称
	 */
	private String liktx;
	/**
	 * 库存标示
	 */
	private String sobkz;
	/**
	 * 单位
	 */
	private String meins;
	/**
	 * 移动数量
	 */
	private Long moveQty;
	/**
	 * 编辑人
	 */
	private String editor;
	/**
	 * 编辑时间
	 */
	private String editDate;

	/**
	 * 设置：${column.comments}
	 */
	public void setId(Long id) {
		this.id = id;
	}
	/**
	 * 获取：${column.comments}
	 */
	public Long getId() {
		return id;
	}
	/**
	 * 设置：工厂代码
	 */
	public void setWerks(String werks) {
		this.werks = werks;
	}
	/**
	 * 获取：工厂代码
	 */
	public String getWerks() {
		return werks;
	}
	/**
	 * 设置：仓库号
	 */
	public void setWhNumber(String whNumber) {
		this.whNumber = whNumber;
	}
	/**
	 * 获取：仓库号
	 */
	public String getWhNumber() {
		return whNumber;
	}
	/**
	 * 设置：物料号
	 */
	public void setMatnr(String matnr) {
		this.matnr = matnr;
	}
	/**
	 * 获取：物料号
	 */
	public String getMatnr() {
		return matnr;
	}
	/**
	 * 设置：物料描述
	 */
	public void setMaktx(String maktx) {
		this.maktx = maktx;
	}
	/**
	 * 获取：物料描述
	 */
	public String getMaktx() {
		return maktx;
	}
	/**
	 * 设置：批次
	 */
	public void setBatch(String batch) {
		this.batch = batch;
	}
	/**
	 * 获取：批次
	 */
	public String getBatch() {
		return batch;
	}
	/**
	 * 设置：库位
	 */
	public void setLgort(String lgort) {
		this.lgort = lgort;
	}
	/**
	 * 获取：库位
	 */
	public String getLgort() {
		return lgort;
	}
	/**
	 * 设置：源储位代码
	 */
	public void setFBinCode(String fBinCode) {
		this.fBinCode = fBinCode;
	}
	/**
	 * 获取：源储位代码
	 */
	public String getFBinCode() {
		return fBinCode;
	}
	/**
	 * 设置：源储位名称
	 */
	public void setFBinName(String fBinName) {
		this.fBinName = fBinName;
	}
	/**
	 * 获取：源储位名称
	 */
	public String getFBinName() {
		return fBinName;
	}
	/**
	 * 设置：目标储位代码
	 */
	public void setBinCode(String binCode) {
		this.binCode = binCode;
	}
	/**
	 * 获取：目标储位代码
	 */
	public String getBinCode() {
		return binCode;
	}
	/**
	 * 设置：目标储位名称
	 */
	public void setBinName(String binName) {
		this.binName = binName;
	}
	/**
	 * 获取：目标储位名称
	 */
	public String getBinName() {
		return binName;
	}
	/**
	 * 设置：供应商代码
	 */
	public void setLifnr(String lifnr) {
		this.lifnr = lifnr;
	}
	/**
	 * 获取：供应商代码
	 */
	public String getLifnr() {
		return lifnr;
	}
	/**
	 * 设置：供应商名称
	 */
	public void setLiktx(String liktx) {
		this.liktx = liktx;
	}
	/**
	 * 获取：供应商名称
	 */
	public String getLiktx() {
		return liktx;
	}
	/**
	 * 设置：库存标示
	 */
	public void setSobkz(String sobkz) {
		this.sobkz = sobkz;
	}
	/**
	 * 获取：库存标示
	 */
	public String getSobkz() {
		return sobkz;
	}
	/**
	 * 设置：单位
	 */
	public void setMeins(String meins) {
		this.meins = meins;
	}
	/**
	 * 获取：单位
	 */
	public String getMeins() {
		return meins;
	}
	/**
	 * 设置：移动数量
	 */
	public void setMoveQty(Long moveQty) {
		this.moveQty = moveQty;
	}
	/**
	 * 获取：移动数量
	 */
	public Long getMoveQty() {
		return moveQty;
	}
	/**
	 * 设置：编辑人
	 */
	public void setEditor(String editor) {
		this.editor = editor;
	}
	/**
	 * 获取：编辑人
	 */
	public String getEditor() {
		return editor;
	}
	/**
	 * 设置：编辑时间
	 */
	public void setEditDate(String editDate) {
		this.editDate = editDate;
	}
	/**
	 * 获取：编辑时间
	 */
	public String getEditDate() {
		return editDate;
	}
}

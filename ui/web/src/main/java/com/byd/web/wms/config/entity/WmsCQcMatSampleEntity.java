package com.byd.web.wms.config.entity;

import java.io.Serializable;
import java.util.List;

import javax.validation.constraints.NotNull;

import com.baomidou.mybatisplus.annotations.KeySequence;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableLogic;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

/**
 * 物料质检抽样配置表
 * 
 * @author cscc
 * @email 
 * @date 2018-08-07 11:54:43
 */
@TableName("WMS_C_QC_MAT_SAMPLE")
@KeySequence("SEQ_WMS_C_QC_MAT_SAMPLE")
public class WmsCQcMatSampleEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * ID
	 */
	@TableId(value="ID",type=IdType.INPUT)
	private Long id;
	/**
	 * 工厂代码
	 */
	@NotNull
	private String werks;
	/**
	 * 物料号
	 */
	@NotNull
	private String matnr;
	/**
	 * 物料描述
	 */
	private String maktx;
	/**
	 * 供应商代码
	 */
	@NotNull
	private String lifnr;
	/**
	 * 供应商名称
	 */
	private String liktx;
	/**
	 * 抽样率(%)
	 */
	@NotNull
	private Long sampling;
	/**
	 * 最小抽样数量
	 */
	@NotNull
	private Long minSample;
	/**
	 * 最大抽样数量
	 */
	@NotNull
	private Long maxSample;
	/**
	 * 开箱率(%)
	 */
	@NotNull
	private Long unpacking;
	/**
	 * 最小开箱数
	 */
	@NotNull
	private Long minUnpacking;
	/**
	 * 最大开箱数
	 */
	@NotNull
	private Long maxUnpacking;
	/**
	 * 删除标示 空 否 X是 默认空
	 */
	@TableField(value = "del")
	@TableLogic
	private String del;
	/**
	 * 创建人
	 */
	private String creator;
	/**
	 * 创建时间
	 */
	private String createDate;
	/**
	 * 编辑人
	 */
	private String editor;
	/**
	 * 编辑时间
	 */
	private String editDate;
	/**
	 * 备注
	 */
	private String memo;
	
	/**
	 * 预览校验消息
	 */
	@TableField(exist = false)
	private List<String> msgs;
	

	public List<String> getMsgs() {
		return msgs;
	}
	public void setMsgs(List<String> msgs) {
		this.msgs = msgs;
	}
	/**
	 * 设置：ID
	 */
	public void setId(Long id) {
		this.id = id;
	}
	/**
	 * 获取：ID
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
	 * 设置：抽样率(%)
	 */
	public void setSampling(Long sampling) {
		this.sampling = sampling;
	}
	/**
	 * 获取：抽样率(%)
	 */
	public Long getSampling() {
		return sampling;
	}
	/**
	 * 设置：最小抽样数量
	 */
	public void setMinSample(Long minSample) {
		this.minSample = minSample;
	}
	/**
	 * 获取：最小抽样数量
	 */
	public Long getMinSample() {
		return minSample;
	}
	/**
	 * 设置：最大抽样数量
	 */
	public void setMaxSample(Long maxSample) {
		this.maxSample = maxSample;
	}
	/**
	 * 获取：最大抽样数量
	 */
	public Long getMaxSample() {
		return maxSample;
	}
	/**
	 * 设置：开箱率(%)
	 */
	public void setUnpacking(Long unpacking) {
		this.unpacking = unpacking;
	}
	/**
	 * 获取：开箱率(%)
	 */
	public Long getUnpacking() {
		return unpacking;
	}
	/**
	 * 设置：最小开箱数
	 */
	public void setMinUnpacking(Long minUnpacking) {
		this.minUnpacking = minUnpacking;
	}
	/**
	 * 获取：最小开箱数
	 */
	public Long getMinUnpacking() {
		return minUnpacking;
	}
	/**
	 * 设置：最大开箱数
	 */
	public void setMaxUnpacking(Long maxUnpacking) {
		this.maxUnpacking = maxUnpacking;
	}
	/**
	 * 获取：最大开箱数
	 */
	public Long getMaxUnpacking() {
		return maxUnpacking;
	}
	/**
	 * 设置：删除标示 空 否 X是 默认空
	 */
	public void setDel(String del) {
		this.del = del;
	}
	/**
	 * 获取：删除标示 空 否 X是 默认空
	 */
	public String getDel() {
		return del;
	}
	/**
	 * 设置：创建人
	 */
	public void setCreator(String creator) {
		this.creator = creator;
	}
	/**
	 * 获取：创建人
	 */
	public String getCreator() {
		return creator;
	}
	/**
	 * 设置：创建时间
	 */
	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}
	/**
	 * 获取：创建时间
	 */
	public String getCreateDate() {
		return createDate;
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
	/**
	 * 设置：备注
	 */
	public void setMemo(String memo) {
		this.memo = memo;
	}
	/**
	 * 获取：备注
	 */
	public String getMemo() {
		return memo;
	}
}

package com.byd.web.wms.config.entity;

import com.baomidou.mybatisplus.annotations.KeySequence;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

import java.io.Serializable;
import java.util.Date;

/**
 * 物料上线配置表
 * 
 * @author cscc
 * @email 
 * @date 2018-08-15 13:57:57
 */
@TableName("WMS_C_MAT_USING")
@KeySequence("SEQ_WMS_C_MAT_USING")
public class WmsCMatUsingEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * ID
	 */
	@TableId(value = "ID",type=IdType.INPUT)
	private Long id;
	/**
	 * 工厂代码
	 */
	private String werks;
	/**
	 * 物料号
	 */
	private String matnr;
	/**
	 * 物料描述
	 */
	private String maktx;
	/**
	 * 是否紧急物料 空,0 否 X是 默认空
	 */
	private String useFlag;
	/**
	 * 删除标示 空,0 否 X是 默认空
	 */
	private String del;
	/**
	 * 有效开始日期
	 */
	private String startDate;
	/**
	 * 有效截止日期
	 */
	private String endDate;
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
	 * 设置：是否上线 空,0 否 X是 默认空
	 */
	public void setUseFlag(String useFlag) {
		this.useFlag = useFlag;
	}
	/**
	 * 获取：是否上线 空,0 否 X是 默认空
	 */
	public String getUseFlag() {
		return useFlag;
	}
	/**
	 * 设置：删除标示 空,0 否 X是 默认空
	 */
	public void setDel(String del) {
		this.del = del;
	}
	/**
	 * 获取：删除标示 空,0 否 X是 默认空
	 */
	public String getDel() {
		return del;
	}
	/**
	 * 设置：有效开始日期
	 */
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	/**
	 * 获取：有效开始日期
	 */
	public String getStartDate() {
		return startDate;
	}
	/**
	 * 设置：有效截止日期
	 */
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	/**
	 * 获取：有效截止日期
	 */
	public String getEndDate() {
		return endDate;
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

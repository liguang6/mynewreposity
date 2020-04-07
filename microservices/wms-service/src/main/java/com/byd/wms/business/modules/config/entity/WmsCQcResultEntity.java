package com.byd.wms.business.modules.config.entity;

import java.io.Serializable;
import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.baomidou.mybatisplus.annotations.KeySequence;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableLogic;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

/**
 * 质检结果配置表
 * 
 * @author cscc
 * @email 
 * @date 2018-08-07 11:54:43
 */
@TableName("WMS_C_QC_RESULT")
@KeySequence("SEQ_WMS_C_QC_RESULT")
public class WmsCQcResultEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * ID
	 */
	@TableId(value="ID",type=IdType.INPUT)
	private Long id;
	/**
	 * 工厂代码
	 */
	private String werks;
	/**
	 * 质检结果类型代码 字典定义：（01让步接受，02合格，03不合格，04挑选使用，05紧急放行，06特采，07
	 */
	@NotEmpty
	private String qcResultCode;
	/**
	 * 质检结果类型名称
	 */
	private String qcResultName;
	/**
	 * 质检状态 字典定义：00未质检 01质检中 02已质检
	 */
	@NotBlank
	private String qcStatus;
	/**
	 * 可进仓标识 默认为空 不能进仓 X 为可进仓
	 */
	@NotBlank
	private String whFlag;
	/**
	 * 可退货标识 默认为空 不能退货  X 为可退货
	 */
	@NotBlank
	private String returnFlag;
	/**
	 * 评审标识 默认为空 不需评审 X 需要评审
	 */
	@NotBlank
	private String reviewFlag;
	/**
	 * 是否良品进仓标识 默认为空 良品 X 为不良品
	 */
	@NotBlank
	private String goodFlag;
	/**
	 * 删除标示 默认为空 X 为删除
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
	 * 导入预览提示消息
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
	 * 设置：质检结果类型代码 字典定义：（01让步接受，02合格，03不合格，04挑选使用，05紧急放行，06特采，07
	 */
	public void setQcResultCode(String qcResultCode) {
		this.qcResultCode = qcResultCode;
	}
	/**
	 * 获取：质检结果类型代码 字典定义：（01让步接受，02合格，03不合格，04挑选使用，05紧急放行，06特采，07
	 */
	public String getQcResultCode() {
		return qcResultCode;
	}
	/**
	 * 设置：质检结果类型名称
	 */
	public void setQcResultName(String qcResultName) {
		this.qcResultName = qcResultName;
	}
	/**
	 * 获取：质检结果类型名称
	 */
	public String getQcResultName() {
		return qcResultName;
	}
	/**
	 * 设置：质检状态 字典定义：00未质检 01质检中 02已质检
	 */
	public void setQcStatus(String qcStatus) {
		this.qcStatus = qcStatus;
	}
	/**
	 * 获取：质检状态 字典定义：00未质检 01质检中 02已质检
	 */
	public String getQcStatus() {
		return qcStatus;
	}
	/**
	 * 设置：可进仓标识 默认为空 不能进仓 X 为可进仓
	 */
	public void setWhFlag(String whFlag) {
		this.whFlag = whFlag;
	}
	/**
	 * 获取：可进仓标识 默认为空 不能进仓 X 为可进仓
	 */
	public String getWhFlag() {
		return whFlag;
	}
	/**
	 * 设置：可退货标识 默认为空 不能退货  X 为可退货
	 */
	public void setReturnFlag(String returnFlag) {
		this.returnFlag = returnFlag;
	}
	/**
	 * 获取：可退货标识 默认为空 不能退货  X 为可退货
	 */
	public String getReturnFlag() {
		return returnFlag;
	}
	/**
	 * 设置：评审标识 默认为空 不需评审 X 需要评审
	 */
	public void setReviewFlag(String reviewFlag) {
		this.reviewFlag = reviewFlag;
	}
	/**
	 * 获取：评审标识 默认为空 不需评审 X 需要评审
	 */
	public String getReviewFlag() {
		return reviewFlag;
	}
	/**
	 * 设置：是否良品进仓标识 默认为空 良品 X 为不良品
	 */
	public void setGoodFlag(String goodFlag) {
		this.goodFlag = goodFlag;
	}
	/**
	 * 获取：是否良品进仓标识 默认为空 良品 X 为不良品
	 */
	public String getGoodFlag() {
		return goodFlag;
	}
	/**
	 * 设置：删除标示 默认为空 X 为删除
	 */
	public void setDel(String del) {
		this.del = del;
	}
	/**
	 * 获取：删除标示 默认为空 X 为删除
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
}

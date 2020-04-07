package com.byd.web.wms.config.entity;

import com.baomidou.mybatisplus.annotations.KeySequence;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

import java.io.Serializable;

/**
 * 
 * 出库规则配置
 *
 */
@TableName("WMS_C_OUT_RULE_DETAIL")
@KeySequence("SEQ_WMS_S_OUT_RULE_DETAIL")
public class WmsCOutRuleDetailEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	@TableId(value = "ID", type = IdType.INPUT)
	private Long id;
	/**
	 * 仓库号
	 */
	@TableField(value="WH_NUMBER")
	private String warehouseCode;
	/**
	 * 工厂
	 */
	//private String factoryCode;
	/**
	 * 出库规则:批次先进先出;收料日期先进先出
	 */
	@TableField(value="out_Rule")
	private String outRule;
	//序号
	@TableField(value="seq_No")
	private String seqNo;
	@TableField(value="sort_Field")
	private String sortField;
	private String sortStrategy;
	/**
	 * 删除标示 空,0 否 X是 默认空
	 */
	private String del;
	private String editor;
	private String editDate;

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getWarehouseCode() {
		return warehouseCode;
	}

	public void setWarehouseCode(String warehouseCode) {
		this.warehouseCode = warehouseCode;
	}

	public String getOutRule() {
		return outRule;
	}

	public void setOutRule(String outRule) {
		this.outRule = outRule;
	}

	public String getSeqNo() {
		return seqNo;
	}

	public void setSeqNo(String seqNo) {
		this.seqNo = seqNo;
	}

	public String getSortField() {
		return sortField;
	}

	public void setSortField(String sortField) {
		this.sortField = sortField;
	}

	public String getSortStrategy() {
		return sortStrategy;
	}

	public void setSortStrategy(String sortStrategy) {
		this.sortStrategy = sortStrategy;
	}

	public String getDel() {
		return del;
	}

	public void setDel(String del) {
		this.del = del;
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

	@Override
	public String toString() {
		return "WmsCOutRuleDetailEntity{" +
				"id=" + id +
				", warehouseCode='" + warehouseCode + '\'' +
				", outRule='" + outRule + '\'' +
				", seqNo='" + seqNo + '\'' +
				", sortField='" + sortField + '\'' +
				", sortStrategy='" + sortStrategy + '\'' +
				", del='" + del + '\'' +
				", editor='" + editor + '\'' +
				", editDate='" + editDate + '\'' +
				'}';
	}
}

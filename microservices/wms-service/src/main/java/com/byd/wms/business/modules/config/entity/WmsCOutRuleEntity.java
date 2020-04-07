package com.byd.wms.business.modules.config.entity;

import java.io.Serializable;
import com.baomidou.mybatisplus.annotations.KeySequence;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

/**
 * 
 * 出库规则配置
 *
 */
@TableName("WMS_S_OUT_RULE")
@KeySequence("SEQ_WMS_S_OUT_RULE")
public class WmsCOutRuleEntity implements Serializable {
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
	private String outRule;
	/**
	 * 描述
	 */
	@TableField(value="TEXT")
	private String descs;
	/**
	 * 状态
	 */
	//private String status;
	//private String creator;
	//private String createDate;
	/**
	 * 删除标示 空,0 否 X是 默认空
	 */
	private String del;
	public String getDel() {
		return del;
	}
	public void setDel(String del) {
		this.del = del;
	}
	private String editor;
	private String editDate;
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
	
	public String getDescs() {
		return descs;
	}
	public void setDescs(String descs) {
		this.descs = descs;
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
}

package com.byd.web.wms.config.entity;

import java.io.Serializable;
import com.baomidou.mybatisplus.annotations.KeySequence;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
/**
 * 
 * 分配存储类型搜索顺序至控制标识
 *
 */
@TableName("WMS_S_RULE_SEARCH_SEQ")
@KeySequence("SEQ_WMS_S_RULE_SEARCH_SEQ")
public class WmsCControlSearchEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	@TableId(value = "ID",type=IdType.INPUT)
	private Long   id;
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
	 * 控制标识
	 */
	private String controlFlag;
	
	@TableField(value="FLAG_TYPE")
	private String controlFlagType;
	
	/**
	 * 仓库处理业务类型
	 */
	@TableField(value="BUSINESS_NAME")
	private String whBusinessType;
	/**
	 * 库位
	 */
	private String lgort;
	/**
	 * 库存类型
	 */
	 @TableField(value="SOBKZ")
	private String stockType;
	/**
	 * 存储类型搜索顺序
	 */
	 @TableField(value="SEARCH_SEQ")
	private String storageAreaSearch;
	/**
	 * 出库规则:批次先进先出;收料日期先进先出
	 */
	private String outRule;
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
	
	public String getControlFlag() {
		return controlFlag;
	}
	public void setControlFlag(String controlFlag) {
		this.controlFlag = controlFlag;
	}
	public String getWhBusinessType() {
		return whBusinessType;
	}
	public void setWhBusinessType(String whBusinessType) {
		this.whBusinessType = whBusinessType;
	}
	public String getLgort() {
		return lgort;
	}
	public void setLgort(String lgort) {
		this.lgort = lgort;
	}
	public String getStockType() {
		return stockType;
	}
	public void setStockType(String stockType) {
		this.stockType = stockType;
	}
	public String getStorageAreaSearch() {
		return storageAreaSearch;
	}
	public void setStorageAreaSearch(String storageAreaSearch) {
		this.storageAreaSearch = storageAreaSearch;
	}
	public String getOutRule() {
		return outRule;
	}
	public void setOutRule(String outRule) {
		this.outRule = outRule;
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
	public String getControlFlagType() {
		return controlFlagType;
	}
	public void setControlFlagType(String controlFlagType) {
		this.controlFlagType = controlFlagType;
	}

}

package com.byd.wms.business.modules.config.entity;

import java.io.Serializable;

/**
 * @author ren.wei3
 *
 */
import com.baomidou.mybatisplus.annotations.KeySequence;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

@TableName("WMS_S_BEST_RULE")
@KeySequence("SEQ_WMS_S_BEST_RULE")
public class WmsSBestRuleEntity implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@TableId(value = "ID",type=IdType.INPUT)
	private Long id;

	/**
	 * 仓库号
	 */
	private String whNumber;
	
	/**
	 * 规则类型1:入库 2:出库
	 */
	private String ruleType;
	
	/**
	 * 优先级
	 */
	private String seqno;
	
	/**
	 * 出入库控制标识 
	 */
	private String controlFlag;
	
	/**
	 * 仓库处理业务类型
	 */
	private String businessTypeFlag;
	
	/**
	 *库位 
	 */
	private String lgortFlag;
	
	/**
	 * 库存类型 
	 */
	private String stockTypeFlag;
	
	/**
	 * 软删除
	 */
	private String del;
	
	/**
	 * 编辑人
	 */
	private String editor;
	/**
	 * 编辑时间
	 */
	private String editDate;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getWhNumber() {
		return whNumber;
	}
	public void setWhNumber(String whNumber) {
		this.whNumber = whNumber;
	}
	public String getRuleType() {
		return ruleType;
	}
	public void setRuleType(String ruleType) {
		this.ruleType = ruleType;
	}
	
	public String getControlFlag() {
		return controlFlag;
	}
	public void setControlFlag(String controlFlag) {
		this.controlFlag = controlFlag;
	}
	public String getBusinessTypeFlag() {
		return businessTypeFlag;
	}
	public void setBusinessTypeFlag(String businessTypeFlag) {
		this.businessTypeFlag = businessTypeFlag;
	}
	public String getLgortFlag() {
		return lgortFlag;
	}
	public void setLgortFlag(String lgortFlag) {
		this.lgortFlag = lgortFlag;
	}
	public String getStockTypeFlag() {
		return stockTypeFlag;
	}
	public void setStockTypeFlag(String stockTypeFlag) {
		this.stockTypeFlag = stockTypeFlag;
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
	public String getSeqno() {
		return seqno;
	}
	public void setSeqno(String seqno) {
		this.seqno = seqno;
	}
	public String getDel() {
		return del;
	}
	public void setDel(String del) {
		this.del = del;
	}
	
}

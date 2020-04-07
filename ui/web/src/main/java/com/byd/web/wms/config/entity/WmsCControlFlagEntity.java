package com.byd.web.wms.config.entity;

import java.io.Serializable;
import com.baomidou.mybatisplus.annotations.KeySequence;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

/**
 * 
 * 控制标识配置
 *
 */
@TableName("WMS_S_CONTROL_FLAG")
@KeySequence("SEQ_WMS_S_CONTROL_FLAG")
public class WmsCControlFlagEntity implements Serializable {
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
	 * 控制标识
	 */
	private String controlFlag;
	/**
	 * 控制标识类型1：入库，2：出库
	 */
	private String controlFlagType;
	/**
	 * 描述
	 */
	@TableField(value="TEXT")
	private String des;
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
	public String getControlFlagType() {
		return controlFlagType;
	}
	public void setControlFlagType(String controlFlagType) {
		this.controlFlagType = controlFlagType;
	}
	public String getDes() {
		return des;
	}
	public void setDes(String des) {
		this.des = des;
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
	
}

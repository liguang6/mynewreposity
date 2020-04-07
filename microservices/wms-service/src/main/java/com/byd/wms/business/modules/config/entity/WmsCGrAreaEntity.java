package com.byd.wms.business.modules.config.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotations.KeySequence;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
/**
 * 收料房存放区配置
 * 
 * @author tangj
 * @email 
 * @date 2018年08月01日 
 */
@TableName("WMS_C_GR_AREA")
@KeySequence("SEQ_WMS_C_GR_AREA")
public class WmsCGrAreaEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@TableId(value = "ID",type=IdType.INPUT)
	private Long id;
	// 工厂代码
	private String werks;
	// 存放区代码
	private String areaCode;
	// 存放区名称
	private String areaName;
	// 软删标示
	private String del;
	// 创建者
	private String editor;
    // 创建日期
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
	public String getAreaCode() {
		return areaCode;
	}
	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
	}
	public String getAreaName() {
		return areaName;
	}
	public void setAreaName(String areaName) {
		this.areaName = areaName;
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
    
}

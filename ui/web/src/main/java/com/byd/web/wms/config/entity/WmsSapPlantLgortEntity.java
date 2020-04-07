package com.byd.web.wms.config.entity;

import com.baomidou.mybatisplus.annotations.KeySequence;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

/**
 * 工厂库存地点
 * @author develop07
 *
 */

@TableName("WMS_SAP_PLANT_LGORT")
@KeySequence("SEQ_WMS_SAP_PLANT_LGORT")
public class WmsSapPlantLgortEntity {
	
	@TableId(value="ID",type=IdType.INPUT)
	private Long id;
	
	/**
	 * 工厂代码
	 */
	private String werks;
	
	/**
	 * 库存地点代码
	 */
	private String lgort;
	
	/**
	 * 库存地点名称
	 */
	private String lgortName;
	
	/**
	 * 库存类型
	 */
	private String sobkz;
	
	/**
	 * 不良库空标识
	 */
	private String badFlag;
	
	
	/**
	 * 删除标识
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

	public String getWerks() {
		return werks;
	}

	public void setWerks(String werks) {
		this.werks = werks;
	}

	public String getLgort() {
		return lgort;
	}

	public void setLgort(String lgort) {
		this.lgort = lgort;
	}

	public String getLgortName() {
		return lgortName;
	}

	public void setLgortName(String lgortName) {
		this.lgortName = lgortName;
	}


	public String getSobkz() {
		return sobkz;
	}

	public void setSobkz(String sobkz) {
		this.sobkz = sobkz;
	}

	public String getBadFlag() {
		return badFlag;
	}

	public void setBadFlag(String badFlag) {
		this.badFlag = badFlag;
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

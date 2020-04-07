package com.byd.web.wms.config.entity;

import com.baomidou.mybatisplus.annotations.KeySequence;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

import java.io.Serializable;
import java.util.Date;

/**
 * 工厂关键零部件配置表
 * 
 * @author cscc tangj
 * @email 
 * @date 2018-12-25 11:31:32
 */
@TableName("WMS_C_KEY_PARTS")
@KeySequence("SEQ_WMS_C_KEY_PARTS")
public class WmsCKeyPartsEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * ID
	 */
	@TableId(value = "ID",type=IdType.INPUT)
	private Long id;
	/**
	 * 工厂
	 */
	private String werks;
	/**
	 * 零部件编号
	 */
	private String keyPartsNo;
	/**
	 * 零部件名称
	 */
	private String keyPartsName;
	/**
	 * SAP物料号
	 */
	private String matnr;
	/**
	 * 物料描述
	 */
	private String maktx;
	/**
	 * 删除 0 未删除 X 删除 默认0
	 */
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
	 * 修改人
	 */
	private String editor;
	/**
	 * 修改时间
	 */
	private String editDate;
	
	// 校验信息字段，不存入数据库
	@TableField(exist=false)
	private String msg;

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
	 * 设置：工厂
	 */
	public void setWerks(String werks) {
		this.werks = werks;
	}
	/**
	 * 获取：工厂
	 */
	public String getWerks() {
		return werks;
	}
	/**
	 * 设置：零部件编号
	 */
	public void setKeyPartsNo(String keyPartsNo) {
		this.keyPartsNo = keyPartsNo;
	}
	/**
	 * 获取：零部件编号
	 */
	public String getKeyPartsNo() {
		return keyPartsNo;
	}
	/**
	 * 设置：零部件名称
	 */
	public void setKeyPartsName(String keyPartsName) {
		this.keyPartsName = keyPartsName;
	}
	/**
	 * 获取：零部件名称
	 */
	public String getKeyPartsName() {
		return keyPartsName;
	}
	/**
	 * 设置：SAP物料号
	 */
	public void setMatnr(String matnr) {
		this.matnr = matnr;
	}
	/**
	 * 获取：SAP物料号
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
	 * 设置：删除 0 未删除 X 删除 默认0
	 */
	public void setDel(String del) {
		this.del = del;
	}
	/**
	 * 获取：删除 0 未删除 X 删除 默认0
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
	 * 设置：修改人
	 */
	public void setEditor(String editor) {
		this.editor = editor;
	}
	/**
	 * 获取：修改人
	 */
	public String getEditor() {
		return editor;
	}
	/**
	 * 设置：修改时间
	 */
	public void setEditDate(String editDate) {
		this.editDate = editDate;
	}
	/**
	 * 获取：修改时间
	 */
	public String getEditDate() {
		return editDate;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	
}

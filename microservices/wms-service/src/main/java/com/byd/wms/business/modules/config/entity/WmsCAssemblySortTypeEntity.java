package com.byd.wms.business.modules.config.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotations.KeySequence;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

/** 
 * @author 作者 E-mail: peng.tao1@byd.com
 * @version 创建时间：2019年8月2日 下午2:27:10 
 * 类说明 
 */
@TableName("WMS_C_ASSEMBLY_SORTTYPE")
@KeySequence("SEQ_WMS_C_ASSEMBLY_SORTTYPE")
public class WmsCAssemblySortTypeEntity implements Serializable{
	private static final long serialVersionUID = 1L;
	/**
	 * ID
	 */
	@TableId(value = "ID",type=IdType.INPUT)
	private Long id;
	
	/**
	 * 供应工厂代码
	 */
	private String fWerks;
	
	/**
	 * JIS排序类别
	 */
	private String jisSortType;
	/**
	 * 排序号
	 */
	private Integer sortNum;
	
	/**
	 * 
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
		
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	
	public String getfWerks() {
		return fWerks;
	}
	public void setfWerks(String fWerks) {
		this.fWerks = fWerks;
	}
	public String getJisSortType() {
		return jisSortType;
	}
	public void setJisSortType(String jisSortType) {
		this.jisSortType = jisSortType;
	}
	public Integer getSortNum() {
		return sortNum;
	}
	public void setSortNum(Integer sortNum) {
		this.sortNum = sortNum;
	}
	public String getDel() {
		return del;
	}
	public void setDel(String del) {
		this.del = del;
	}
	public String getCreator() {
		return creator;
	}
	public void setCreator(String creator) {
		this.creator = creator;
	}
	public String getCreateDate() {
		return createDate;
	}
	public void setCreateDate(String createDate) {
		this.createDate = createDate;
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
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	
}

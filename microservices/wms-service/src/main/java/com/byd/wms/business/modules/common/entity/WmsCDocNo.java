package com.byd.wms.business.modules.common.entity;

import com.baomidou.mybatisplus.annotations.KeySequence;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

@TableName("WMS_C_DOC_NO")
@KeySequence("SEQ_WMS_C_DOC_NO")//使用oracle 注解自增
public class WmsCDocNo {
	@TableId(value="ID",type=IdType.INPUT)
	private Long id;
	
	/**
	 * 工厂代码
	 */
	private String werks;
	/**
	 * 单据类型
	 */
	private String docType;
	/**
	 * 编号前缀
	 */
	private String preNo;
	/**
	 * 编号后缀
	 */
	private String backNo;
	/**
	 * 起始号
	 */
	private int startNo;
	/**
	 * 递增
	 */
	private int incrementNum;
	/**
	 * 编号长度
	 */
	private int noLength;
	/**
	 * 当前编号
	 */
	private int currentNo;
	
	private String del;
	
	private String creator;
	
	private String createDate;
	
	private String editor;
	
	private String editDate;
	
	private String memo;
	
	/**
	 * 单据类型名称
	 */
	@TableField(exist=false)
	private String docTypeName;
	
	@TableField(exist=false)
	private String sys;

	public String getSys() {
		return sys;
	}

	public void setSys(String sys) {
		this.sys = sys;
	}

	public String getDocTypeName() {
		return docTypeName;
	}

	public void setDocTypeName(String docTypeName) {
		this.docTypeName = docTypeName;
	}

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

	public String getDocType() {
		return docType;
	}

	public void setDocType(String docType) {
		this.docType = docType;
	}

	public String getPreNo() {
		return preNo;
	}

	public void setPreNo(String preNo) {
		this.preNo = preNo;
	}

	public String getBackNo() {
		return backNo;
	}

	public void setBackNo(String backNo) {
		this.backNo = backNo;
	}

	public int getStartNo() {
		return startNo;
	}

	public void setStartNo(int startNo) {
		this.startNo = startNo;
	}

	public int getIncrementNum() {
		return incrementNum;
	}

	public void setIncrementNum(int incrementNum) {
		this.incrementNum = incrementNum;
	}

	public int getNoLength() {
		return noLength;
	}

	public void setNoLength(int noLength) {
		this.noLength = noLength;
	}

	public int getCurrentNo() {
		return currentNo;
	}

	public void setCurrentNo(int currentNo) {
		this.currentNo = currentNo;
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

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}
	
	
	
	
	
}

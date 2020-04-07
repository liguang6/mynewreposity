package com.byd.wms.business.modules.config.entity;

import com.baomidou.mybatisplus.annotations.KeySequence;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import java.io.Serializable;

/**
 * 仓库储位排序
 * 
 * @author cscc TANGJ
 * @email 
 * @date 2019-03-21 15:36:51
 */
@TableName("WMS_CORE_WH_BIN_SEQ")
@KeySequence("SEQ_WMS_CORE_WH_BIN_SEQ")
public class WmsCoreWhBinSeqEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	
	/**
	 * ID
	 */
	@TableId(value = "ID",type=IdType.INPUT)
	private Long id;
	/**
	 * 仓库号
	 */
	private String whNumber;
	/**
	 * 排序类别 00 按照储位编号排序 01 按照坐标排序
	 */
	private String seqType;
	/**
	 * 储位代码
	 */
	private String binCode;
	/**
	 * 排序号
	 */
	private int seqno;
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
	public String getSeqType() {
		return seqType;
	}
	public void setSeqType(String seqType) {
		this.seqType = seqType;
	}
	public String getBinCode() {
		return binCode;
	}
	public void setBinCode(String binCode) {
		this.binCode = binCode;
	}
	public int getSeqno() {
		return seqno;
	}
	public void setSeqno(int seqno) {
		this.seqno = seqno;
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

package com.byd.web.sys.entity;

import java.io.Serializable;
import java.util.Date;

import javax.validation.constraints.NotBlank;

import com.baomidou.mybatisplus.annotations.KeySequence;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * @author ren.wei3
 *
 */
@TableName("sys_locale")
@KeySequence("SEQ_SYS_LOCALE")
public class SysLocaleEntity implements Serializable {

	@TableId(value = "ID", type = IdType.INPUT)
	private Long id;
	/**
	 * 国际化KEY
	 */
	@NotBlank(message="key值不能为空")
	private String pKey;
	
	/**
	 * 语言
	 */
	@NotBlank(message="语言不能为空")
	private String lKey;
	
	/**
	 * 中描述
	 */
	private String middleDesc;
	
	/**
	 * 短描述
	 */
	private String shortDesc;
	
	/**
	 * 长描述
	 */
	private String longDesc;
	
	/**
	 * 默认描述类型
	 */
	private String descDef;
	
	/**
	 * 修改时间
	 */
	private Date modifyDate;

	

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getpKey() {
		return pKey;
	}

	public void setpKey(String pKey) {
		this.pKey = pKey;
	}

	public String getlKey() {
		return lKey;
	}

	public void setlKey(String lKey) {
		this.lKey = lKey;
	}

	public String getMiddleDesc() {
		return middleDesc;
	}

	public void setMiddleDesc(String middleDesc) {
		this.middleDesc = middleDesc;
	}

	public String getShortDesc() {
		return shortDesc;
	}

	public void setShortDesc(String shortDesc) {
		this.shortDesc = shortDesc;
	}

	public String getLongDesc() {
		return longDesc;
	}

	public void setLongDesc(String longDesc) {
		this.longDesc = longDesc;
	}

	public String getDescDef() {
		return descDef;
	}

	public void setDescDef(String descDef) {
		this.descDef = descDef;
	}

	public Date getModifyDate() {
		return modifyDate;
	}

	public void setModifyDate(Date modifyDate) {
		this.modifyDate = modifyDate;
	}
	
}
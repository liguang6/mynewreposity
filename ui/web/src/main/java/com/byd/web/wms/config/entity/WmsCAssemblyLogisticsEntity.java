package com.byd.web.wms.config.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotations.KeySequence;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

/** 
 * @author 作者 E-mail: 
 * @version 创建时间：2019年7月26日 下午3:30:15 
 * 类说明 
 */
@TableName("WMS_C_ASSEMBLY_LOGISTICS")
@KeySequence("SEQ_WMS_C_ASSEMBLY_LOGISTICS")
public class WmsCAssemblyLogisticsEntity implements Serializable{
	private static final long serialVersionUID = 1L;
	/**
	 * ID
	 */
	@TableId(value = "ID",type=IdType.INPUT)
	private Long id;
	
	/**
	 * 总装工厂代码
	 */
	private String assemblyWerks;
	/**
	 * 供货工厂代码
	 */
	private String werksF;
	/**
	 * 供货工厂是否已上WMS 0 否 X 启用 默认0
	 */
	private String wmsFlagF;
	/**
	 * 供货工厂发货库位
	 */
	private String lgortF;
	/**
	 * 特殊库存类型
	 */
	private String sobkz;
	/**
	 * 供货工厂WMS过账移动类型
	 */
	private String wmsMoveType;
	/**
	 * 供货SAP过账标识 00 无需过账 01 实时过账 02 异步过账
	 */
	private String sapFlagF;
	/**
	 * 供货工厂SAP过账移动类型
	 */
	private String sapMoveType;
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
	
	public String getAssemblyWerks() {
		return assemblyWerks;
	}
	public void setAssemblyWerks(String assemblyWerks) {
		this.assemblyWerks = assemblyWerks;
	}
	public String getWerksF() {
		return werksF;
	}
	public void setWerksF(String werksF) {
		this.werksF = werksF;
	}
	public String getWmsFlagF() {
		return wmsFlagF;
	}
	public void setWmsFlagF(String wmsFlagF) {
		this.wmsFlagF = wmsFlagF;
	}
	public String getLgortF() {
		return lgortF;
	}
	public void setLgortF(String lgortF) {
		this.lgortF = lgortF;
	}
	public String getSobkz() {
		return sobkz;
	}
	public void setSobkz(String sobkz) {
		this.sobkz = sobkz;
	}
	public String getWmsMoveType() {
		return wmsMoveType;
	}
	public void setWmsMoveType(String wmsMoveType) {
		this.wmsMoveType = wmsMoveType;
	}
	public String getSapFlagF() {
		return sapFlagF;
	}
	public void setSapFlagF(String sapFlagF) {
		this.sapFlagF = sapFlagF;
	}
	public String getSapMoveType() {
		return sapMoveType;
	}
	public void setSapMoveType(String sapMoveType) {
		this.sapMoveType = sapMoveType;
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

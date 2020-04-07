package com.byd.wms.business.modules.config.entity;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;

import java.io.Serializable;
import java.util.Date;

/**
 * 物料信息表 SAP同步获取
 * 
 * @author cscc
 * @email 
 * @date 2018-08-14 08:45:52
 */
@TableName("WMS_SAP_MATERIAL")
public class WmsSapMaterialEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * ID
	 */
	@TableId
	private Long id;
	/**
	 * 物料号 MATNR
	 */
	private String matnr;
	/**
	 * 物料描述 MAKTX
	 */
	private String maktx;
	/**
	 * 英文描述
	 */
	private String maktxEn;
	/**
	 * 基本计量单位 MEINS
	 */
	private String meins;
	/**
	 * 工厂代码 WERKS
	 */
	private String werks;
	/**
	 * 采购单位 BSTME
	 */
	private String bstme;
	/**
	 * 发货单位 AUSME
	 */
	private String ausme;
	/**
	 * 工厂物料状态 MMSTA 空为正常 不为空表示物料在工厂维度状态为删除
	 */
	private String mmsta;
	/**
	 * 工厂删除标识符 LVORM X 标示删除
	 */
	private String lvorm;
	/**
	 * 采购类型 BESKZ
	 */
	private String beskz;
	/**
	 * 特殊采购类型 SOBSL
	 */
	private String sobsl;
	/**
	 * 价格控制指示符 VPRSV
	 */
	private String vprsv;
	/**
	 * 移动平均价 2 VERPR
	 */
	private Long verpr;
	/**
	 * 标准价格 STPRS
	 */
	private Long stprs;
	/**
	 * 价格单位 PEINH
	 */
	private String peinh;
	/**
	 * 数据导入日期
	 */
	private String importDate;
	/**
	 * 数据导入账号
	 */
	private String importor;
	/**
	 * 数据修改ID
	 */
	private String editorId;
	/**
	 * 数据修改姓名
	 */
	private String editor;
	/**
	 * 数据修改日期
	 */
	private String editDate;
	/**
	 * 价值属性 A B C D
	 */
	private String valueType;
	/**
	 * 物料删除标识 X 标示删除 空为正常
	 */
	private String matLvorm;
	/**
	 * 启用批次管理标示 默认为空 标示不启用 X 标示启用
	 */
	private String xchpf;
	/**
	 * 质检周期
	 */
	private Long prfrq;

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
	 * 设置：物料号 MATNR
	 */
	public void setMatnr(String matnr) {
		this.matnr = matnr;
	}
	/**
	 * 获取：物料号 MATNR
	 */
	public String getMatnr() {
		return matnr;
	}
	/**
	 * 设置：物料描述 MAKTX
	 */
	public void setMaktx(String maktx) {
		this.maktx = maktx;
	}
	/**
	 * 获取：物料描述 MAKTX
	 */
	public String getMaktx() {
		return maktx;
	}
	/**
	 * 设置：英文描述
	 */
	public void setMaktxEn(String maktxEn) {
		this.maktxEn = maktxEn;
	}
	/**
	 * 获取：英文描述
	 */
	public String getMaktxEn() {
		return maktxEn;
	}
	/**
	 * 设置：基本计量单位 MEINS
	 */
	public void setMeins(String meins) {
		this.meins = meins;
	}
	/**
	 * 获取：基本计量单位 MEINS
	 */
	public String getMeins() {
		return meins;
	}
	/**
	 * 设置：工厂代码 WERKS
	 */
	public void setWerks(String werks) {
		this.werks = werks;
	}
	/**
	 * 获取：工厂代码 WERKS
	 */
	public String getWerks() {
		return werks;
	}
	/**
	 * 设置：采购单位 BSTME
	 */
	public void setBstme(String bstme) {
		this.bstme = bstme;
	}
	/**
	 * 获取：采购单位 BSTME
	 */
	public String getBstme() {
		return bstme;
	}
	/**
	 * 设置：发货单位 AUSME
	 */
	public void setAusme(String ausme) {
		this.ausme = ausme;
	}
	/**
	 * 获取：发货单位 AUSME
	 */
	public String getAusme() {
		return ausme;
	}
	/**
	 * 设置：工厂物料状态 MMSTA 空为正常 不为空表示物料在工厂维度状态为删除
	 */
	public void setMmsta(String mmsta) {
		this.mmsta = mmsta;
	}
	/**
	 * 获取：工厂物料状态 MMSTA 空为正常 不为空表示物料在工厂维度状态为删除
	 */
	public String getMmsta() {
		return mmsta;
	}
	/**
	 * 设置：工厂删除标识符 LVORM X 标示删除
	 */
	public void setLvorm(String lvorm) {
		this.lvorm = lvorm;
	}
	/**
	 * 获取：工厂删除标识符 LVORM X 标示删除
	 */
	public String getLvorm() {
		return lvorm;
	}
	/**
	 * 设置：采购类型 BESKZ
	 */
	public void setBeskz(String beskz) {
		this.beskz = beskz;
	}
	/**
	 * 获取：采购类型 BESKZ
	 */
	public String getBeskz() {
		return beskz;
	}
	/**
	 * 设置：特殊采购类型 SOBSL
	 */
	public void setSobsl(String sobsl) {
		this.sobsl = sobsl;
	}
	/**
	 * 获取：特殊采购类型 SOBSL
	 */
	public String getSobsl() {
		return sobsl;
	}
	/**
	 * 设置：价格控制指示符 VPRSV
	 */
	public void setVprsv(String vprsv) {
		this.vprsv = vprsv;
	}
	/**
	 * 获取：价格控制指示符 VPRSV
	 */
	public String getVprsv() {
		return vprsv;
	}
	/**
	 * 设置：移动平均价 2 VERPR
	 */
	public void setVerpr(Long verpr) {
		this.verpr = verpr;
	}
	/**
	 * 获取：移动平均价 2 VERPR
	 */
	public Long getVerpr() {
		return verpr;
	}
	/**
	 * 设置：标准价格 STPRS
	 */
	public void setStprs(Long stprs) {
		this.stprs = stprs;
	}
	/**
	 * 获取：标准价格 STPRS
	 */
	public Long getStprs() {
		return stprs;
	}
	/**
	 * 设置：价格单位 PEINH
	 */
	public void setPeinh(String peinh) {
		this.peinh = peinh;
	}
	/**
	 * 获取：价格单位 PEINH
	 */
	public String getPeinh() {
		return peinh;
	}
	/**
	 * 设置：数据导入日期
	 */
	public void setImportDate(String importDate) {
		this.importDate = importDate;
	}
	/**
	 * 获取：数据导入日期
	 */
	public String getImportDate() {
		return importDate;
	}
	/**
	 * 设置：数据导入账号
	 */
	public void setImportor(String importor) {
		this.importor = importor;
	}
	/**
	 * 获取：数据导入账号
	 */
	public String getImportor() {
		return importor;
	}
	/**
	 * 设置：数据修改ID
	 */
	public void setEditorId(String editorId) {
		this.editorId = editorId;
	}
	/**
	 * 获取：数据修改ID
	 */
	public String getEditorId() {
		return editorId;
	}
	/**
	 * 设置：数据修改姓名
	 */
	public void setEditor(String editor) {
		this.editor = editor;
	}
	/**
	 * 获取：数据修改姓名
	 */
	public String getEditor() {
		return editor;
	}
	/**
	 * 设置：数据修改日期
	 */
	public void setEditDate(String editDate) {
		this.editDate = editDate;
	}
	/**
	 * 获取：数据修改日期
	 */
	public String getEditDate() {
		return editDate;
	}
	/**
	 * 设置：价值属性 A B C D
	 */
	public void setValueType(String valueType) {
		this.valueType = valueType;
	}
	/**
	 * 获取：价值属性 A B C D
	 */
	public String getValueType() {
		return valueType;
	}
	/**
	 * 设置：物料删除标识 X 标示删除 空为正常
	 */
	public void setMatLvorm(String matLvorm) {
		this.matLvorm = matLvorm;
	}
	/**
	 * 获取：物料删除标识 X 标示删除 空为正常
	 */
	public String getMatLvorm() {
		return matLvorm;
	}
	/**
	 * 设置：启用批次管理标示 默认为空 标示不启用 X 标示启用
	 */
	public void setXchpf(String xchpf) {
		this.xchpf = xchpf;
	}
	/**
	 * 获取：启用批次管理标示 默认为空 标示不启用 X 标示启用
	 */
	public String getXchpf() {
		return xchpf;
	}

	public Long getPrfrq() {
		return prfrq;
	}

	public void setPrfrq(Long prfrq) {
		this.prfrq = prfrq;
	}
}

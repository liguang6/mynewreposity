package com.byd.web.wms.config.entity;

import com.baomidou.mybatisplus.annotations.KeySequence;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

import java.io.Serializable;
import java.util.Date;

/**
 * 物料物流参数配置表 自制产品入库参数
 * 
 * @author cscc
 * @email 
 * @date 2018-09-28 10:30:07
 */
@TableName("WMS_C_MAT_LT_SAMPLE")
@KeySequence("SEQ_WMS_C_MAT_LT_SAMPLE")
public class WmsCMatLtSampleEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * ID
	 */
	@TableId(value = "ID",type=IdType.INPUT)
	private Long id;
	/**
	 * 工厂代码
	 */
	private String werks;
	/**
	 * 物料号
	 */
	private String matnr;
	/**
	 * 物料描述
	 */
	private String maktx;
	/**
	 * 满箱数量
	 */
	private Long fullBoxQty;
	/**
	 * 物流器具
	 */
	private String ltWare;
	/**
	 * 车型
	 */
	private String carType;
	/**
	 * 生产工位
	 */
	private String proStation;
	/**
	 * 配送工位
	 */
	private String disStation;
	/**
	 * 配送地址
	 */
	private String disAddrss;
	/**
	 * 删除标示 0 否 X是 默认0
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
	 * 编辑人
	 */
	private String editor;
	/**
	 * 编辑时间
	 */
	private String editDate;
	/**
	 * 模具编号
	 */
	private String mouldNo;

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
	 * 设置：工厂代码
	 */
	public void setWerks(String werks) {
		this.werks = werks;
	}
	/**
	 * 获取：工厂代码
	 */
	public String getWerks() {
		return werks;
	}
	/**
	 * 设置：物料号
	 */
	public void setMatnr(String matnr) {
		this.matnr = matnr;
	}
	/**
	 * 获取：物料号
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
	 * 设置：满箱数量
	 */
	public void setFullBoxQty(Long fullBoxQty) {
		this.fullBoxQty = fullBoxQty;
	}
	/**
	 * 获取：满箱数量
	 */
	public Long getFullBoxQty() {
		return fullBoxQty;
	}
	/**
	 * 设置：物流器具
	 */
	public void setLtWare(String ltWare) {
		this.ltWare = ltWare;
	}
	/**
	 * 获取：物流器具
	 */
	public String getLtWare() {
		return ltWare;
	}
	/**
	 * 设置：车型
	 */
	public void setCarType(String carType) {
		this.carType = carType;
	}
	/**
	 * 获取：车型
	 */
	public String getCarType() {
		return carType;
	}
	/**
	 * 设置：生产工位
	 */
	public void setProStation(String proStation) {
		this.proStation = proStation;
	}
	/**
	 * 获取：生产工位
	 */
	public String getProStation() {
		return proStation;
	}
	/**
	 * 设置：配送工位
	 */
	public void setDisStation(String disStation) {
		this.disStation = disStation;
	}
	/**
	 * 获取：配送工位
	 */
	public String getDisStation() {
		return disStation;
	}
	/**
	 * 设置：配送地址
	 */
	public void setDisAddrss(String disAddrss) {
		this.disAddrss = disAddrss;
	}
	/**
	 * 获取：配送地址
	 */
	public String getDisAddrss() {
		return disAddrss;
	}
	/**
	 * 设置：删除标示 0 否 X是 默认0
	 */
	public void setDel(String del) {
		this.del = del;
	}
	/**
	 * 获取：删除标示 0 否 X是 默认0
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
	 * 设置：编辑人
	 */
	public void setEditor(String editor) {
		this.editor = editor;
	}
	/**
	 * 获取：编辑人
	 */
	public String getEditor() {
		return editor;
	}
	/**
	 * 设置：编辑时间
	 */
	public void setEditDate(String editDate) {
		this.editDate = editDate;
	}
	/**
	 * 获取：编辑时间
	 */
	public String getEditDate() {
		return editDate;
	}
	
	public String getMouldNo() {
		return mouldNo;
	}
	
	public void setMouldNo(String mouldNo) {
		this.mouldNo = mouldNo;
	}
	
}

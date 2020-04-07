package com.byd.wms.business.modules.config.entity;

import com.baomidou.mybatisplus.annotations.KeySequence;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import java.io.Serializable;

/**
 * 仓库储位
 * 
 * @author cscc
 * @email 
 * @date 2018-08-07 15:36:51
 */
@TableName("WMS_CORE_WH_BIN")
@KeySequence("SEQ_WMS_CORE_WH_BIN")
public class WmsCoreWhBinEntity implements Serializable {
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
	
	private String storageAreaCode;
	/**
	 * 储位代码
	 */
	private String binCode;
	/**
	 * 储位名称
	 */
	private String binName;
	/**
	 * 储位类型 字典定义：00 进仓位 01 出仓位 02 拣配位
	 */
	private String binType;
	/**
	 * 储位状态 00 未启用 01 可用 02 不可用
	 */
	private String binStatus;
	/**
	 * 排
	 */
	private Long binRow;
	/**
	 * 列
	 */
	private Long binColumn;
	/**
	 * 层
	 */
	private Long binFloor;
	/**
	 * 容积
	 */
	private Long vl;
	/**
	 * 容积单位
	 */
	private String vlUnit;
	/**
	 * 承重
	 */
	private Long wt;
	/**
	 * 承重单位
	 */
	private String wtUnit;
	/**
	 * 容积使用
	 */
	private Long vlUse;
	/**
	 * 重量使用
	 */
	private Long wtUse;
	/**
	 * 周转率
	 */
	private String turnoverRate;
	/**
	 * 可容存储单元
	 */
	private String aStorageUnit;
	/**
	 * 占用存储单元
	 */
	private String uStorageUnit;
	/**
	 * x轴
	 */
	private Long x;
	/**
	 * y轴
	 */
	private Long y;
	/**
	 * z轴
	 */
	private Long z;
	
	/**
	 * 设置：${column.comments}
	 */
	private String del;
	/**
	 * 编辑人
	 */
	private String editor;
	/**
	 * 编辑时间
	 */
	private String editDate;

	
	public void setDel(String del) {
		this.del = del;
	}
	/**
	 * 获取：${column.comments}
	 */
	public String getDel() {
		return del;
	}
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
	 * 设置：仓库号
	 */
	public void setWhNumber(String whNumber) {
		this.whNumber = whNumber;
	}
	/**
	 * 获取：仓库号
	 */
	public String getWhNumber() {
		return whNumber;
	}
	
	/**
	 * 设置：排
	 */
	public void setBinRow(Long binRow) {
		this.binRow = binRow;
	}
	/**
	 * 获取：排
	 */
	public Long getBinRow() {
		return binRow;
	}
	/**
	 * 设置：列
	 */
	public void setBinColumn(Long binColumn) {
		this.binColumn = binColumn;
	}
	/**
	 * 获取：列
	 */
	public Long getBinColumn() {
		return binColumn;
	}
	/**
	 * 设置：层
	 */
	public void setBinFloor(Long binFloor) {
		this.binFloor = binFloor;
	}
	/**
	 * 获取：层
	 */
	public Long getBinFloor() {
		return binFloor;
	}
	/**
	 * 设置：储位代码
	 */
	public void setBinCode(String binCode) {
		this.binCode = binCode;
	}
	/**
	 * 获取：储位代码
	 */
	public String getBinCode() {
		return binCode;
	}
	/**
	 * 设置：储位名称
	 */
	public void setBinName(String binName) {
		this.binName = binName;
	}
	/**
	 * 获取：储位名称
	 */
	public String getBinName() {
		return binName;
	}
	/**
	 * 设置：储位类型 字典定义：00 进仓位 01 出仓位 02 拣配位
	 */
	public void setBinType(String binType) {
		this.binType = binType;
	}
	/**
	 * 获取：储位类型 字典定义：00 进仓位 01 出仓位 02 拣配位
	 */
	public String getBinType() {
		return binType;
	}
	/**
	 * 设置：储位状态 00 未启用 01 可用 02 不可用
	 */
	public void setBinStatus(String binStatus) {
		this.binStatus = binStatus;
	}
	/**
	 * 获取：储位状态 00 未启用 01 可用 02 不可用
	 */
	public String getBinStatus() {
		return binStatus;
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
	public String getStorageAreaCode() {
		return storageAreaCode;
	}
	public void setStorageAreaCode(String storageAreaCode) {
		this.storageAreaCode = storageAreaCode;
	}
	public Long getVl() {
		return vl;
	}
	public void setVl(Long vl) {
		this.vl = vl;
	}
	public String getVlUnit() {
		return vlUnit;
	}
	public void setVlUnit(String vlUnit) {
		this.vlUnit = vlUnit;
	}
	public Long getWt() {
		return wt;
	}
	public void setWt(Long wt) {
		this.wt = wt;
	}
	public String getWtUnit() {
		return wtUnit;
	}
	public void setWtUnit(String wtUnit) {
		this.wtUnit = wtUnit;
	}
	public Long getVlUse() {
		return vlUse;
	}
	public void setVlUse(Long vlUse) {
		this.vlUse = vlUse;
	}
	public Long getWtUse() {
		return wtUse;
	}
	public void setWtUse(Long wtUse) {
		this.wtUse = wtUse;
	}
	public String getTurnoverRate() {
		return turnoverRate;
	}
	public void setTurnoverRate(String turnoverRate) {
		this.turnoverRate = turnoverRate;
	}
	public Long getX() {
		return x;
	}
	public void setX(Long x) {
		this.x = x;
	}
	public Long getY() {
		return y;
	}
	public void setY(Long y) {
		this.y = y;
	}
	public Long getZ() {
		return z;
	}
	public void setZ(Long z) {
		this.z = z;
	}
	public String getaStorageUnit() {
		return aStorageUnit;
	}
	public void setaStorageUnit(String aStorageUnit) {
		this.aStorageUnit = aStorageUnit;
	}
	public String getuStorageUnit() {
		return uStorageUnit;
	}
	public void setuStorageUnit(String uStorageUnit) {
		this.uStorageUnit = uStorageUnit;
	}
	
}

package com.byd.wms.business.modules.qc.entity;

import com.baomidou.mybatisplus.annotations.KeySequence;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableLogic;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

import java.io.Serializable;

/**
 * 检验结果
 * 
 * @author (changsha) byd_infomation_center
 * @email 
 * @date 2018-08-13 15:12:12
 */
@TableName("WMS_QC_RESULT")
@KeySequence("SEQ_WMS_QC_RESULT")
public class WmsQcResultEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	
	/**
	 * ID
	 */
	@TableId(value="ID",type=IdType.INPUT)
	private Long id;
	
	/**
	 * 编辑人
	 */
	
	private String editor;
	/**
	 * 编辑时间
	 */
	private String editDate;
	
	/**
	 * 检验记录号
	 */
	private String qcResultNo;
	/**
	 * 检验记录行项目号
	 */
	private String qcResultItemNo;
	/**
	 * 送检单号 删除送检单时送检结果同步删除
	 */
	private String inspectionNo;
	/**
	 * 送检单行项目号
	 */
	private String inspectionItemNo;
	/**
	 * 质检记录类型 字典定义： 01初判 02重判
	 */
	private String qcRecordType;
	/**
	 * 库存来源 库存来源（01收料房，02库房）
	 */
	private String stockSource;
	/**
	 * 工厂代码
	 */
	private String werks;
	/**
	 * 仓库号
	 */
	private String whNumber;
	/**
	 * 库位
	 */
	private String lgort;
	private String lifnr;//LIFNR
	private String sobkz;
	/**
	 * 物料号
	 */
	private String matnr;
	/**
	 * 物料描述
	 */
	private String maktx;
	/**
	 * 单位
	 */
	private String unit;
	/**
	 * 数量
	 */
	private Double resultQty;
	/**
	 * WMS批次
	 */
	private String batch;
	/**
	 * 质检日期
	 */
	private String qcDate;
	/**
	 * 检验结果 字典定义：
	 */
	private String qcResultCode;
	/**
	 * 结果文本
	 */
	private String qcResultText;
	/**
	 * 结果描述(不合格原因)
	 */
	private String qcResult;
	private String returnReasonType;//不合格原因分类
	/**
	 * 破坏数量
	 */
	private Double destroyQty;
	/**
	 * IQC成本中心
	 */
	private String iqcCostCenter;
	/**
	 * 质检员
	 */
	private String qcPeople;
	/**
	 * 删除标示 0 否 X是 默认0
	 */
	@TableLogic
	@TableField("del")
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
	 * 标签
	 */
	private String labelNo;
	
	/**
	 * 是否已经进仓
	 */
	@TableField(exist = false)
	private boolean hasInInbound;
	/**
	 * 是否已经退货
	 */
	@TableField(exist = false)
	private boolean hasOutReturn;
	
	/**
	 * pda 的标签
	 */
	@TableField(exist = false)
	private String pdaLabelNo;
	
	
	
	public boolean isHasInInbound() {
		return hasInInbound;
	}
	public void setHasInInbound(boolean hasInInbound) {
		this.hasInInbound = hasInInbound;
	}
	public boolean isHasOutReturn() {
		return hasOutReturn;
	}
	public void setHasOutReturn(boolean hasOutReturn) {
		this.hasOutReturn = hasOutReturn;
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
	 * 设置：检验记录号
	 */
	public void setQcResultNo(String qcResultNo) {
		this.qcResultNo = qcResultNo;
	}
	/**
	 * 获取：检验记录号
	 */
	public String getQcResultNo() {
		return qcResultNo;
	}
	/**
	 * 设置：检验记录行项目号
	 */
	public void setQcResultItemNo(String qcResultItemNo) {
		this.qcResultItemNo = qcResultItemNo;
	}
	/**
	 * 获取：检验记录行项目号
	 */
	public String getQcResultItemNo() {
		return qcResultItemNo;
	}
	/**
	 * 设置：送检单号 删除送检单时送检结果同步删除
	 */
	public void setInspectionNo(String inspectionNo) {
		this.inspectionNo = inspectionNo;
	}
	/**
	 * 获取：送检单号 删除送检单时送检结果同步删除
	 */
	public String getInspectionNo() {
		return inspectionNo;
	}
	/**
	 * 设置：送检单行项目号
	 */
	public void setInspectionItemNo(String inspectionItemNo) {
		this.inspectionItemNo = inspectionItemNo;
	}
	/**
	 * 获取：送检单行项目号
	 */
	public String getInspectionItemNo() {
		return inspectionItemNo;
	}
	/**
	 * 设置：质检记录类型 字典定义： 01初判 02重判
	 */
	public void setQcRecordType(String qcRecordType) {
		this.qcRecordType = qcRecordType;
	}
	/**
	 * 获取：质检记录类型 字典定义： 01初判 02重判
	 */
	public String getQcRecordType() {
		return qcRecordType;
	}
	/**
	 * 设置：库存来源 库存来源（01收料房，02库房）
	 */
	public void setStockSource(String stockSource) {
		this.stockSource = stockSource;
	}
	/**
	 * 获取：库存来源 库存来源（01收料房，02库房）
	 */
	public String getStockSource() {
		return stockSource;
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
	 * 设置：库位
	 */
	public void setLgort(String lgort) {
		this.lgort = lgort;
	}
	/**
	 * 获取：库位
	 */
	public String getLgort() {
		return lgort;
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
	 * 设置：单位
	 */
	public void setUnit(String unit) {
		this.unit = unit;
	}
	/**
	 * 获取：单位
	 */
	public String getUnit() {
		return unit;
	}
	/**
	 * 设置：数量
	 */
	public void setResultQty(Double qty) {
		this.resultQty = qty;
	}
	/**
	 * 获取：数量
	 */
	public Double getResultQty() {
		return resultQty;
	}
	/**
	 * 设置：WMS批次
	 */
	public void setBatch(String batch) {
		this.batch = batch;
	}
	/**
	 * 获取：WMS批次
	 */
	public String getBatch() {
		return batch;
	}
	/**
	 * 设置：质检日期
	 */
	public void setQcDate(String qcDate) {
		this.qcDate = qcDate;
	}
	/**
	 * 获取：质检日期
	 */
	public String getQcDate() {
		return qcDate;
	}
	/**
	 * 设置：检验结果 字典定义：
	 */
	public void setQcResultCode(String qcResultCode) {
		this.qcResultCode = qcResultCode;
	}
	/**
	 * 获取：检验结果 字典定义：
	 */
	public String getQcResultCode() {
		return qcResultCode;
	}
	/**
	 * 设置：结果文本
	 */
	public void setQcResultText(String qcResultText) {
		this.qcResultText = qcResultText;
	}
	/**
	 * 获取：结果文本
	 */
	public String getQcResultText() {
		return qcResultText;
	}
	/**
	 * 设置：结果描述(不合格原因)
	 */
	public void setQcResult(String qcResult) {
		this.qcResult = qcResult;
	}
	/**
	 * 获取：结果描述(不合格原因)
	 */
	public String getQcResult() {
		return qcResult;
	}
	/**
	 * 设置：破坏数量
	 */
	public void setDestroyQty(Double destroyQty) {
		this.destroyQty = destroyQty;
	}
	/**
	 * 获取：破坏数量
	 */
	public Double getDestroyQty() {
		return destroyQty;
	}
	/**
	 * 设置：IQC成本中心
	 */
	public void setIqcCostCenter(String iqcCostCenter) {
		this.iqcCostCenter = iqcCostCenter;
	}
	/**
	 * 获取：IQC成本中心
	 */
	public String getIqcCostCenter() {
		return iqcCostCenter;
	}
	/**
	 * 设置：质检员
	 */
	public void setQcPeople(String qcPeople) {
		this.qcPeople = qcPeople;
	}
	/**
	 * 获取：质检员
	 */
	public String getQcPeople() {
		return qcPeople;
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
	public String getLabelNo() {
		return labelNo;
	}
	public void setLabelNo(String labelNo) {
		this.labelNo = labelNo;
	}
	public String getPdaLabelNo() {
		return pdaLabelNo;
	}
	public void setPdaLabelNo(String pdaLabelNo) {
		this.pdaLabelNo = pdaLabelNo;
	}
	public String getLifnr() {
		return lifnr;
	}
	public void setLifnr(String lifnr) {
		this.lifnr = lifnr;
	}
	public String getSobkz() {
		return sobkz;
	}
	public void setSobkz(String sobkz) {
		this.sobkz = sobkz;
	}
	public String getReturnReasonType() {
		return returnReasonType;
	}
	public void setReturnReasonType(String returnReasonType) {
		this.returnReasonType = returnReasonType;
	}
	
}

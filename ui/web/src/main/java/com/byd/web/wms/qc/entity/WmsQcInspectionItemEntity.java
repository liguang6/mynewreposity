package com.byd.web.wms.qc.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotations.KeySequence;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableLogic;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

/**
 * 送检单明细
 * 
 * @author (changsha) byd_infomation_center
 * @email 
 * @date 2018-08-13 15:12:12
 */
@TableName("WMS_QC_INSPECTION_ITEM")
@KeySequence("SEQ_WMS_QC_INSPECTION_ITEM")
public class WmsQcInspectionItemEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * ID
	 */
	@TableId(value="ID",type=IdType.INPUT)
	private Long id;
	/**
	 * 送检单号
	 */
	private String inspectionNo;
	/**
	 * 送检单行项目号
	 */
	private String inspectionItemNo;
	/**
	 * 行项目状态 字典定义：（00未质检，01完成，02关闭）
	 */
	private String inspectionItemStatus;
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
	/**
	 * 储位 如果库存来源是收料房，储位为 收料房存放区
	 */
	private String binCode;
	
	private String sobkz;//SOBKZ
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
	 * 送检数量
	 */
	private Double inspectionQty;
	
	/**
	 * 需要质检数量
	 */
	@TableField(exist = false)
	private Double requiredInspectionQtyCount;
	
	
	/**
	 * 箱数
	 */
	private Long boxCount;
	
	/**
	 * 需开箱数量
	 */
	@TableField(exist = false)
	private Long requiredBoxCount;
	/**
	 * WMS批次
	 */
	private String batch;
	/**
	 * 质检剩余天数 IQC质检预警
	 */
	private Long leftQcDays;
	/**
	 * 供应商代码
	 */
	private String lifnr;
	/**
	 * 供应商名称
	 */
	private String liktx;
	/**
	 * 试装数量
	 */
	private Double tryQty;
	/**
	 * 收料员**/
	private String receiver;
	/**
	 * 申请人
	 */
	private String afnam;
	/**
	 * 需求跟踪号
	 */
	private String bednr;
	/**
	 * 收货日期
	 */
	private String receiptDate;
	/**
	 * 收货单号
	 */
	private String receiptNo;
	/**
	 * 收货单行项目号
	 */
	private String receiptItemNo;
	/**
	 * 删除标示 0 否 X是 默认0
	 */
	@TableField(value = "del")
	@TableLogic
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
	 * 紧急物料
	 */
	@TableField(exist = false)
	private String urgentFlag;
	
	/**
	 * 不合格原因描述
	 */
	@TableField(exist = false)
	private String returnreason;
	
	/**
	 * 不合格原因代码
	 */
	@TableField(exist = false)
	private String returnreasoncode;
	/**
	 * 不合格原因分类
	 */
	@TableField(exist = false)
	private String returnreasontype;
	
	/**
	 * 检验结果代码
	 */
	@TableField(exist = false)
	private String qcResultCode;
	
	/**
	 * 检验结果文本
	 */
	@TableField(exist = false)
	private String qcResultText;
	
	/**
	 * 质检员
	 */
	@TableField(exist = false)
	private String qcPeople;
	
	
	/**
	 * 质检数量
	 */
	@TableField(exist = false)
	private Double checkedQty;
	
	/**
	 * 破坏数量
	 */
	@TableField(exist = false)
	private Double destoryQty;
	
	/**
	 * 成本中心
	 */
	@TableField(exist = false)
	private String costcenter;
	
	/**
	 * 标签号
	 */
	@TableField(exist = false)
	private String labelNo;
	
	/**
	 * 标签标识
	 */
	@TableField(exist = false)
	private String barcodeFlag;
	
	
	/**
	 * 过期日期
	 */
	@TableField(exist = false)
	private String effectDate;
	
	@TableField(exist = false)
	private String batchQcFlag;
	
	public String getQcPeople() {
		return qcPeople;
	}
	public void setQcPeople(String qcPeople) {
		this.qcPeople = qcPeople;
	}
	
	public Double getDestoryQty() {
		return destoryQty;
	}
	public void setDestoryQty(Double destoryQty) {
		this.destoryQty = destoryQty;
	}
	public Double getCheckedQty() {
		return checkedQty;
	}
	public void setCheckedQty(Double checkedQty) {
		this.checkedQty = checkedQty;
	}
	
	public String getQcResultCode() {
		return qcResultCode;
	}
	public void setQcResultCode(String qcResultCode) {
		this.qcResultCode = qcResultCode;
	}
	public String getQcResultText() {
		return qcResultText;
	}
	public void setQcResultText(String qcResultText) {
		this.qcResultText = qcResultText;
	}
	public String getReturnreason() {
		return returnreason;
	}
	public void setReturnreason(String returnreason) {
		this.returnreason = returnreason;
	}
	public String getReturnreasoncode() {
		return returnreasoncode;
	}
	public void setReturnreasoncode(String returnreasoncode) {
		this.returnreasoncode = returnreasoncode;
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
	 * 设置：送检单号
	 */
	public void setInspectionNo(String inspectionNo) {
		this.inspectionNo = inspectionNo;
	}
	/**
	 * 获取：送检单号
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
	 * 设置：行项目状态 字典定义：（00未质检，01完成，02关闭）
	 */
	public void setInspectionItemStatus(String inspectionItemStatus) {
		this.inspectionItemStatus = inspectionItemStatus;
	}
	/**
	 * 获取：行项目状态 字典定义：（00未质检，01完成，02关闭）
	 */
	public String getInspectionItemStatus() {
		return inspectionItemStatus;
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
	 * 设置：储位 如果库存来源是收料房，储位为 收料房存放区
	 */
	public void setBinCode(String binCode) {
		this.binCode = binCode;
	}
	/**
	 * 获取：储位 如果库存来源是收料房，储位为 收料房存放区
	 */
	public String getBinCode() {
		return binCode;
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
	 * 设置：送检数量
	 */
	public void setInspectionQty(Double inspectionQty) {
		this.inspectionQty = inspectionQty;
	}
	/**
	 * 获取：送检数量
	 */
	public Double getInspectionQty() {
		return inspectionQty;
	}
	/**
	 * 设置：箱数
	 */
	public void setBoxCount(Long boxCount) {
		this.boxCount = boxCount;
	}
	/**
	 * 获取：箱数
	 */
	public Long getBoxCount() {
		return boxCount;
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
	 * 设置：质检剩余天数 IQC质检预警
	 */
	public void setLeftQcDays(Long leftQcDays) {
		this.leftQcDays = leftQcDays;
	}
	/**
	 * 获取：质检剩余天数 IQC质检预警
	 */
	public Long getLeftQcDays() {
		return leftQcDays;
	}
	/**
	 * 设置：供应商代码
	 */
	public void setLifnr(String lifnr) {
		this.lifnr = lifnr;
	}
	/**
	 * 获取：供应商代码
	 */
	public String getLifnr() {
		return lifnr;
	}
	/**
	 * 设置：供应商名称
	 */
	public void setLiktx(String liktx) {
		this.liktx = liktx;
	}
	/**
	 * 获取：供应商名称
	 */
	public String getLiktx() {
		return liktx;
	}
	/**
	 * 设置：试装数量
	 */
	public void setTryQty(Double tryQty) {
		this.tryQty = tryQty;
	}
	/**
	 * 获取：试装数量
	 */
	public Double getTryQty() {
		return tryQty;
	}
	/**
	 * 设置：收料员
	 */
	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}
	/**
	 * 获取：收料员
	 */
	public String getReceiver() {
		return receiver;
	}
	/**
	 * 设置：申请人
	 */
	public void setAfnam(String applicant) {
		this.afnam = applicant;
	}
	/**
	 * 获取：申请人
	 */
	public String getAfnam() {
		return afnam;
	}
	/**
	 * 设置：需求跟踪号
	 */
	public void setBednr(String trackingNo) {
		this.bednr = trackingNo;
	}
	/**
	 * 获取：需求跟踪号
	 */
	public String getBednr() {
		return bednr;
	}
	/**
	 * 设置：收货日期
	 */
	public void setReceiptDate(String receiptDate) {
		this.receiptDate = receiptDate;
	}
	/**
	 * 获取：收货日期
	 */
	public String getReceiptDate() {
		return receiptDate;
	}
	/**
	 * 设置：收货单号
	 */
	public void setReceiptNo(String receiptNo) {
		this.receiptNo = receiptNo;
	}
	/**
	 * 获取：收货单号
	 */
	public String getReceiptNo() {
		return receiptNo;
	}
	/**
	 * 设置：收货单行项目号
	 */
	public void setReceiptItemNo(String receiptItemNo) {
		this.receiptItemNo = receiptItemNo;
	}
	/**
	 * 获取：收货单行项目号
	 */
	public String getReceiptItemNo() {
		return receiptItemNo;
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
	public Double getRequiredInspectionQtyCount() {
		return requiredInspectionQtyCount;
	}
	public void setRequiredInspectionQtyCount(Double requiredInspectionQtyCount) {
		this.requiredInspectionQtyCount = requiredInspectionQtyCount;
	}
	public Long getRequiredBoxCount() {
		return requiredBoxCount;
	}
	public void setRequiredBoxCount(Long requiredBoxCount) {
		this.requiredBoxCount = requiredBoxCount;
	}
	public String getUrgentFlag() {
		return urgentFlag;
	}
	public void setUrgentFlag(String urgentFlag) {
		this.urgentFlag = urgentFlag;
	}
	public String getCostcenter() {
		return costcenter;
	}
	public void setCostcenter(String costcenter) {
		this.costcenter = costcenter;
	}
	public String getLabelNo() {
		return labelNo;
	}
	public void setLabelNo(String labelNo) {
		this.labelNo = labelNo;
	}
	public String getEffectDate() {
		return effectDate;
	}
	public void setEffectDate(String effectDate) {
		this.effectDate = effectDate;
	}
	public String getBatchQcFlag() {
		return batchQcFlag;
	}
	public void setBatchQcFlag(String batchQcFlag) {
		this.batchQcFlag = batchQcFlag;
	}
	public String getBarcodeFlag() {
		return barcodeFlag;
	}
	public void setBarcodeFlag(String barcodeFlag) {
		this.barcodeFlag = barcodeFlag;
	}
	public String getSobkz() {
		return sobkz;
	}
	public void setSobkz(String sobkz) {
		this.sobkz = sobkz;
	}
	public String getReturnreasontype() {
		return returnreasontype;
	}
	public void setReturnreasontype(String returnreasontype) {
		this.returnreasontype = returnreasontype;
	}
}
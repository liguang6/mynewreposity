package com.byd.wms.business.modules.in.entity;


import com.baomidou.mybatisplus.annotations.KeySequence;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

import java.io.Serializable;

/**
 * webserviceLog实体
 * @author rain
 * @email 
 * @date 2019年11月27日11:26:50
 */
@TableName("wms_webservice_log")
@KeySequence("SEQ_WMS_WEBSERVICE_LOG_PKLOG")
public class WmsWebServiceLogEntity implements Serializable {
	private static final long serialVersionUID = 13333333L;
	
	/**
	 * 日志ID
	 */
	@TableId(value = "PK_LOG", type = IdType.INPUT)
	private Long pkLog;

	/**
	 * 业务主键
	 */
	private Long bizId;
	
	/**
	 * 接口编号
	 */
	private String flowNo;

	/**
	 * 接口源
	 */
	private String fromNo;

	/**
	 * 接口目标
	 */
	private String toNo;

	/**
	 * 接口请求数据
	 */
	private String fromJsonData;

	/**
	 * 接口返回数据
	 */
	private String toJsonData;

	/**
	 * 接口执行状态:[{"100","成功"},{"101","失败"}]
	 */
	private String vstatus;

	/**
	 * 创建人、创建时间、修改人、修改时间
	 */
	private String cturId;
	private String ctdt;
	private String upurId;
	private String updt;


	public Long getPkLog() {
		return pkLog;
	}

	public void setPkLog(Long pkLog) {
		this.pkLog = pkLog;
	}

	public Long getBizId() {
		return bizId;
	}

	public void setBizId(Long bizId) {
		this.bizId = bizId;
	}

	public String getFlowNo() {
		return flowNo;
	}

	public void setFlowNo(String flowNo) {
		this.flowNo = flowNo;
	}

	public String getFromNo() {
		return fromNo;
	}

	public void setFromNo(String fromNo) {
		this.fromNo = fromNo;
	}

	public String getToNo() {
		return toNo;
	}

	public void setToNo(String toNo) {
		this.toNo = toNo;
	}

	public String getFromJsonData() {
		return fromJsonData;
	}

	public void setFromJsonData(String fromJsonData) {
		this.fromJsonData = fromJsonData;
	}

	public String getToJsonData() {
		return toJsonData;
	}

	public void setToJsonData(String toJsonData) {
		this.toJsonData = toJsonData;
	}

	public String getVstatus() {
		return vstatus;
	}

	public void setVstatus(String vstatus) {
		this.vstatus = vstatus;
	}

	public String getCturId() {
		return cturId;
	}

	public void setCturId(String cturId) {
		this.cturId = cturId;
	}

	public String getCtdt() {
		return ctdt;
	}

	public void setCtdt(String ctdt) {
		this.ctdt = ctdt;
	}

	public String getUpurId() {
		return upurId;
	}

	public void setUpurId(String upurId) {
		this.upurId = upurId;
	}

	public String getUpdt() {
		return updt;
	}

	public void setUpdt(String updt) {
		this.updt = updt;
	}


}

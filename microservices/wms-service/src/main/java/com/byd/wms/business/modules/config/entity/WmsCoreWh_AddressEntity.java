package com.byd.wms.business.modules.config.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotations.KeySequence;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
/**
 * 仓库信息
 * 
 * @author tangj
 * @email 
 * @date 2018年07月31日 
 */

public class WmsCoreWh_AddressEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	private Long id;
	// 工厂代码
	private String werks;
	// 仓库号
	private String whNumber;
	
	// 软删标示
	private String del;
	// 创建者
	private String editor;
    // 创建日期
	private String editDate;
	//WMS_CORE_WH_ADDRESS内容
	//语言
	private String language;
		//仓库名称
	private String whName;
		//国家
	private String country;
		//省份
	private String province;
		//城市
	private String city;
		//区/县
	private String region;
		//街道
	private String street;
		//联系人
	private String contacts;
		//联系电话
	private String tel;
		//备注
	private String memo;
	//库位
	private String lgortNo;

	public String getLgortNo() {
		return lgortNo;
	}

	public void setLgortNo(String lgortNo) {
		this.lgortNo = lgortNo;
	}
	
	
	public String getWerks() {
		return werks;
	}
	public void setWerks(String werks) {
		this.werks = werks;
	}
	public String getWhNumber() {
		return whNumber;
	}
	public void setWhNumber(String whNumber) {
		this.whNumber = whNumber;
	}
	
	public String getDel() {
		return del;
	}
	public void setDel(String del) {
		this.del = del;
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
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public String getWhName() {
		return whName;
	}
	public void setWhName(String whName) {
		this.whName = whName;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getRegion() {
		return region;
	}
	public void setRegion(String region) {
		this.region = region;
	}
	public String getStreet() {
		return street;
	}
	public void setStreet(String street) {
		this.street = street;
	}
	public String getContacts() {
		return contacts;
	}
	public void setContacts(String contacts) {
		this.contacts = contacts;
	}
	public String getTel() {
		return tel;
	}
	public void setTel(String tel) {
		this.tel = tel;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "WmsCoreWh_AddressEntity{" +
				"id=" + id +
				", werks='" + werks + '\'' +
				", whNumber='" + whNumber + '\'' +
				", del='" + del + '\'' +
				", editor='" + editor + '\'' +
				", editDate='" + editDate + '\'' +
				", language='" + language + '\'' +
				", whName='" + whName + '\'' +
				", country='" + country + '\'' +
				", province='" + province + '\'' +
				", city='" + city + '\'' +
				", region='" + region + '\'' +
				", street='" + street + '\'' +
				", contacts='" + contacts + '\'' +
				", tel='" + tel + '\'' +
				", memo='" + memo + '\'' +
				", lgortNo='" + lgortNo + '\'' +
				'}';
	}
}

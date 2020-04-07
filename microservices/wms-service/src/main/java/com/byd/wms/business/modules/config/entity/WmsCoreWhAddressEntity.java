package com.byd.wms.business.modules.config.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotations.KeySequence;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

/** 
 * @author 作者 E-mail: peng.tao1@byd.com
 * @version 创建时间：2019年2月25日 下午3:57:17 
 * 类说明 
 */
@TableName("WMS_CORE_WH_ADDRESS")
@KeySequence("SEQ_WMS_CORE_WH_ADDRESS")
public class WmsCoreWhAddressEntity implements Serializable {
private static final long serialVersionUID = 1L;
	
	@TableId(value = "ID",type=IdType.INPUT)
	private Long id;
	// 工厂代码
	private String werks;
	// 仓库号
	private String whNumber;
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
	//
	private String del;
	//库位
	private String lgortNo;

	public String getLgortNo() {
		return lgortNo;
	}

	public void setLgortNo(String lgortNo) {
		this.lgortNo = lgortNo;
	}

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
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
	public String getDel() {
		return del;
	}
	public void setDel(String del) {
		this.del = del;
	}

	@Override
	public String toString() {
		return "WmsCoreWhAddressEntity{" +
				"id=" + id +
				", werks='" + werks + '\'' +
				", whNumber='" + whNumber + '\'' +
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
				", del='" + del + '\'' +
				", lgortNo='" + lgortNo + '\'' +
				'}';
	}
}

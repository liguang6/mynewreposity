package com.byd.wms.business.modules.timejob.entity;

import java.io.Serializable;
import java.util.Set;

/**
 * 送检单抬头
 * 
 * @author (changsha) byd_infomation_center
 * @email 
 * @date 2018-08-13 15:12:12
 */

public class WmsEmailAddresseeEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	private Long id;
	/**
	 *收件地址
	 */

	private String addressee;

	private String name;

	private String noticeType;

	private Set<WmsEmailMessageEntity> wmsEmailMessageEntity;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

    public String getAddressee() {
        return addressee;
    }

    public void setAddressee(String addressee) {
        this.addressee = addressee;
    }

    public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<WmsEmailMessageEntity> getWmsEmailMessageEntity() {
		return wmsEmailMessageEntity;
	}

	public void setWmsEmailMessageEntity(Set<WmsEmailMessageEntity> wmsEmailMessageEntity) {
		this.wmsEmailMessageEntity = wmsEmailMessageEntity;
	}

	public String getNoticeType() {
		return noticeType;
	}

}

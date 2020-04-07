package com.byd.wms.business.modules.timejob.dao;

import com.byd.wms.business.modules.timejob.entity.WmsEmailAddresseeEntity;

import java.util.List;

public interface EmailSendDao {

	List<WmsEmailAddresseeEntity> queryUnQualityMessage();

    List<WmsEmailAddresseeEntity> queryUnStorageMessage();

    List<WmsEmailAddresseeEntity> queryUnQualifiedMessage();

    List<WmsEmailAddresseeEntity> queryOverDueMessage();
}

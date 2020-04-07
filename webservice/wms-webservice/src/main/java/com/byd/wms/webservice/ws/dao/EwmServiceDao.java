package com.byd.wms.webservice.ws.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.byd.wms.webservice.ws.entity.WmsKnLabelRecordEntity;

import java.util.Map;

public interface EwmServiceDao extends BaseMapper<WmsKnLabelRecordEntity> {

	public void updateLabelByEwm(Map<String, Object> param);

	String getMaktx(String mantr);

	String getLiktx(String lifnr);
}

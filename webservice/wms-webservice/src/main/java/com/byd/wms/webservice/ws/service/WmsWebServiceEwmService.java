package com.byd.wms.webservice.ws.service;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

@WebService
public interface WmsWebServiceEwmService {

	/**
     * EWM-WMS条码共享
	 * 用于UB转储业务、一步联动STO业务、STO发货发货；
	 * 过账时触发接口；
	 * 从ewm发至wms系统中，工厂代码根据仓库号带出，wms收货时更新仓库号。
	 * 如传入的条码id有重复，系统自动覆盖。
     *
     * @param params
     * @return
     */
    @WebMethod
    String ewm2WmsByLabel(@WebParam(name = "label") String params);

}

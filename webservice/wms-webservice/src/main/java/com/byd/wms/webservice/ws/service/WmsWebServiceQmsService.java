package com.byd.wms.webservice.ws.service;

import java.util.Map;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
@WebService
public interface WmsWebServiceQmsService {
	
	/**
     * WMS获取QMS把需物料冻结的物料清单
     *
     * @param params
     * @return
     */
    @WebMethod
    String transFreezeMatInfo(@WebParam(name="map_info") String params);


	/**
	 * QMS把质检结果返回WMS
	 * @param result_info
	 * @return
	 */
	@WebMethod
	public String qualityResultService(@WebParam(name="result_info") String result_info);
	
	
	/**
	 * WMS获取收料房报表数据
	 * @param params
	 * @return
	 */
	@WebMethod
	public String getMaterialListService(@WebParam(name="params") String params);

}

package com.byd.wms.webservice.ws.service;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

/**
 * @auther: peng.yang8
 * @data: 2019/12/20
 * @description：
 */
@WebService
public interface WmsCallMaterialsService {
    @WebMethod
    String callMaterials(@WebParam(name = "params")String params);

}

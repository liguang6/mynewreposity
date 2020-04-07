package com.byd.wms.webservice.ws.service;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import java.util.List;
import java.util.Map;
@WebService
public interface WmsStockQueryService {
    /**
     * 调用库存查询接口
     * @param params
     * @return
     */
    @WebMethod
   String getQueryStock(@WebParam(name = "params") String params);
}

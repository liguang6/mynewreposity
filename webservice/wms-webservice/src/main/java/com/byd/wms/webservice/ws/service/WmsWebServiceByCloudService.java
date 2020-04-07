package com.byd.wms.webservice.ws.service;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import java.util.List;
import java.util.Map;

@WebService
public interface WmsWebServiceByCloudService {

    /**
     * 云平台获取仓库地址信息
     *
     * @param params
     * @return
     */
    @WebMethod
    String queryWhDetail(@WebParam(name = "params")String params);

}

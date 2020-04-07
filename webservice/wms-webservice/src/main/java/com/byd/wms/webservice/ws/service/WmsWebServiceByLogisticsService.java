package com.byd.wms.webservice.ws.service;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import java.util.Map;

/**
 * 
 * @author 作者 : ren.wei3@byd.com 
 * @version 创建时间：2019年5月31日 上午9:49:17 
 *
 */
@WebService
public interface WmsWebServiceByLogisticsService {


    
    /**
     * 接收第三方系统叫料需求
     * @return
     */
    @WebMethod
    Map<String,String> receiveOtherSystemREQ(@WebParam(name = "params") String params);



    /**
     * 接收物流信息 过账sap
     * @return
     */
    @WebMethod
    public String handoverOtherSystemREQ(@WebParam(name = "params") String params);

}

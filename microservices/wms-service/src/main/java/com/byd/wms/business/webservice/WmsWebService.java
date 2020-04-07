package com.byd.wms.business.webservice;

import java.util.List;
import java.util.Map;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

@WebService
public interface WmsWebService {
	 
    @WebMethod
    String hello(@WebParam(name = "name")String name);
    @WebMethod
    Map<String, Object> createNewDispatching(@WebParam(name = "params")String params);
    @WebMethod
    Map<String, Object> deleteDispatchingByArray(@WebParam(name = "params")String params);
    @WebMethod
    Map<String, Object> closeDispatchingByArray(@WebParam(name = "params")String params);
}

package com.byd.wms.webservice.labelmaster.service;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;


@WebService
public interface LabelWebService {
    @WebMethod
    public String getLabelMaster(@WebParam(name="mapInfo") String mapInfo);
}

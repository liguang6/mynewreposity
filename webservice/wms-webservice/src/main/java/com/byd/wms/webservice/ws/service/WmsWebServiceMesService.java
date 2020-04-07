package com.byd.wms.webservice.ws.service;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

@WebService
public interface WmsWebServiceMesService {
	@WebMethod
    public String transferMapInfo(@WebParam(name="map_info") String map_info);
}

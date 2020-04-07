
package com.byd.wms.webservice.ewm.sap.rfc.functions;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.Holder;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.9-b130926.1035
 * Generated source version: 2.2
 * 
 */
@WebService(name = "ZRFC_WMS_003", targetNamespace = "urn:sap-com:document:sap:rfc:functions")
@XmlSeeAlso({
    ObjectFactory.class
})
public interface ZRFCWMS003 {


    /**
     * 
     * @param itTM
     * @param eMESSAGE
     */
    @WebMethod(operationName = "ZRFC_WMS_003", action = "urn:sap-com:document:sap:rfc:functions:ZRFC_WMS_003:ZRFC_WMS_003Request")
    @RequestWrapper(localName = "ZRFC_WMS_003", targetNamespace = "urn:sap-com:document:sap:rfc:functions", className = "com.sap.document.sap.rfc.functions.ZRFCWMS003_Type")
    @ResponseWrapper(localName = "ZRFC_WMS_003Response", targetNamespace = "urn:sap-com:document:sap:rfc:functions", className = "com.sap.document.sap.rfc.functions.ZRFCWMS003Response")
    public void zrfcWMS003(
            @WebParam(name = "IT_TM", targetNamespace = "", mode = WebParam.Mode.INOUT)
                    Holder<ZTWMSTM> itTM,
            @WebParam(name = "E_MESSAGE", targetNamespace = "", mode = WebParam.Mode.OUT)
                    Holder<String> eMESSAGE);

}

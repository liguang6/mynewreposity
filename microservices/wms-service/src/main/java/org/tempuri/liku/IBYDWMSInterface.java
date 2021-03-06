
package org.tempuri.liku;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.8
 * Generated source version: 2.2
 * 
 */
@WebService(name = "IBYDWMSInterface", targetNamespace = "http://liku.tempuri.org/")
@XmlSeeAlso({
    ObjectFactory.class
})
public interface IBYDWMSInterface {


    /**
     * 
     * @param pMaterialXML
     * @return
     *     returns java.lang.String
     */
    @WebMethod(operationName = "SendMaterial", action = "http://liku.tempuri.org/IBYDWMSInterface/SendMaterial")
    @WebResult(name = "SendMaterialResult", targetNamespace = "http://liku.tempuri.org/")
    @RequestWrapper(localName = "SendMaterial", targetNamespace = "http://liku.tempuri.org/", className = "org.tempuri.liku.SendMaterial")
    @ResponseWrapper(localName = "SendMaterialResponse", targetNamespace = "http://liku.tempuri.org/", className = "org.tempuri.liku.SendMaterialResponse")
    public String sendMaterial(
        @WebParam(name = "pMaterialXML", targetNamespace = "http://liku.tempuri.org/")
        String pMaterialXML);

    /**
     * 
     * @param pInStockXML
     * @return
     *     returns java.lang.String
     */
    @WebMethod(operationName = "SendInStockOrder", action = "http://liku.tempuri.org/IBYDWMSInterface/SendInStockOrder")
    @WebResult(name = "SendInStockOrderResult", targetNamespace = "http://liku.tempuri.org/")
    @RequestWrapper(localName = "SendInStockOrder", targetNamespace = "http://liku.tempuri.org/", className = "org.tempuri.liku.SendInStockOrder")
    @ResponseWrapper(localName = "SendInStockOrderResponse", targetNamespace = "http://liku.tempuri.org/", className = "org.tempuri.liku.SendInStockOrderResponse")
    public String sendInStockOrder(
        @WebParam(name = "pInStockXML", targetNamespace = "http://liku.tempuri.org/")
        String pInStockXML);

    /**
     * 
     * @param pOutStockXML
     * @return
     *     returns java.lang.String
     */
    @WebMethod(operationName = "SendOutStock", action = "http://liku.tempuri.org/IBYDWMSInterface/SendOutStock")
    @WebResult(name = "SendOutStockResult", targetNamespace = "http://liku.tempuri.org/")
    @RequestWrapper(localName = "SendOutStock", targetNamespace = "http://liku.tempuri.org/", className = "org.tempuri.liku.SendOutStock")
    @ResponseWrapper(localName = "SendOutStockResponse", targetNamespace = "http://liku.tempuri.org/", className = "org.tempuri.liku.SendOutStockResponse")
    public String sendOutStock(
        @WebParam(name = "pOutStockXML", targetNamespace = "http://liku.tempuri.org/")
        String pOutStockXML);

    /**
     * 
     * @param pStorageXML
     * @return
     *     returns java.lang.Integer
     */
    @WebMethod(operationName = "GetStorage", action = "http://liku.tempuri.org/IBYDWMSInterface/GetStorage")
    @WebResult(name = "GetStorageResult", targetNamespace = "http://liku.tempuri.org/")
    @RequestWrapper(localName = "GetStorage", targetNamespace = "http://liku.tempuri.org/", className = "org.tempuri.liku.GetStorage")
    @ResponseWrapper(localName = "GetStorageResponse", targetNamespace = "http://liku.tempuri.org/", className = "org.tempuri.liku.GetStorageResponse")
    public Integer getStorage(
        @WebParam(name = "pStorageXML", targetNamespace = "http://liku.tempuri.org/")
        String pStorageXML);

}

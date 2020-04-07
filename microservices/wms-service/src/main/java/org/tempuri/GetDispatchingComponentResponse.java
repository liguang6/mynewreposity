
package org.tempuri;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>anonymous complex type的 Java 类。
 * 
 * <p>以下模式片段指定包含在此类中的预期内容。
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Get_Dispatching_ComponentResult" type="{http://tempuri.org/}WS_WMS_Dispatching" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "getDispatchingComponentResult"
})
@XmlRootElement(name = "Get_Dispatching_ComponentResponse")
public class GetDispatchingComponentResponse {

    @XmlElement(name = "Get_Dispatching_ComponentResult")
    protected WSWMSDispatching getDispatchingComponentResult;

    /**
     * 获取getDispatchingComponentResult属性的值。
     * 
     * @return
     *     possible object is
     *     {@link WSWMSDispatching }
     *     
     */
    public WSWMSDispatching getGetDispatchingComponentResult() {
        return getDispatchingComponentResult;
    }

    /**
     * 设置getDispatchingComponentResult属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link WSWMSDispatching }
     *     
     */
    public void setGetDispatchingComponentResult(WSWMSDispatching value) {
        this.getDispatchingComponentResult = value;
    }

}

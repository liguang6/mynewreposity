
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
 *         &lt;element name="Add_F_WMS_DispatchingDeliveryResult" type="{http://tempuri.org/}WMSDispatchingReturnResult" minOccurs="0"/>
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
    "addFWMSDispatchingDeliveryResult"
})
@XmlRootElement(name = "Add_F_WMS_DispatchingDeliveryResponse")
public class AddFWMSDispatchingDeliveryResponse {

    @XmlElement(name = "Add_F_WMS_DispatchingDeliveryResult")
    protected WMSDispatchingReturnResult addFWMSDispatchingDeliveryResult;

    /**
     * 获取addFWMSDispatchingDeliveryResult属性的值。
     * 
     * @return
     *     possible object is
     *     {@link WMSDispatchingReturnResult }
     *     
     */
    public WMSDispatchingReturnResult getAddFWMSDispatchingDeliveryResult() {
        return addFWMSDispatchingDeliveryResult;
    }

    /**
     * 设置addFWMSDispatchingDeliveryResult属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link WMSDispatchingReturnResult }
     *     
     */
    public void setAddFWMSDispatchingDeliveryResult(WMSDispatchingReturnResult value) {
        this.addFWMSDispatchingDeliveryResult = value;
    }

}


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
 *         &lt;element name="Add_DispatchingReceiveResult" type="{http://tempuri.org/}WS_WMS_DispatchingList" minOccurs="0"/>
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
    "addDispatchingReceiveResult"
})
@XmlRootElement(name = "Add_DispatchingReceiveResponse")
public class AddDispatchingReceiveResponse {

    @XmlElement(name = "Add_DispatchingReceiveResult")
    protected WSWMSDispatchingList addDispatchingReceiveResult;

    /**
     * 获取addDispatchingReceiveResult属性的值。
     * 
     * @return
     *     possible object is
     *     {@link WSWMSDispatchingList }
     *     
     */
    public WSWMSDispatchingList getAddDispatchingReceiveResult() {
        return addDispatchingReceiveResult;
    }

    /**
     * 设置addDispatchingReceiveResult属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link WSWMSDispatchingList }
     *     
     */
    public void setAddDispatchingReceiveResult(WSWMSDispatchingList value) {
        this.addDispatchingReceiveResult = value;
    }

}

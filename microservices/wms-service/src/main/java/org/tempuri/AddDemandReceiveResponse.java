
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
 *         &lt;element name="Add_DemandReceiveResult" type="{http://tempuri.org/}WS_WMS_List" minOccurs="0"/>
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
    "addDemandReceiveResult"
})
@XmlRootElement(name = "Add_DemandReceiveResponse")
public class AddDemandReceiveResponse {

    @XmlElement(name = "Add_DemandReceiveResult")
    protected WSWMSList addDemandReceiveResult;

    /**
     * 获取addDemandReceiveResult属性的值。
     * 
     * @return
     *     possible object is
     *     {@link WSWMSList }
     *     
     */
    public WSWMSList getAddDemandReceiveResult() {
        return addDemandReceiveResult;
    }

    /**
     * 设置addDemandReceiveResult属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link WSWMSList }
     *     
     */
    public void setAddDemandReceiveResult(WSWMSList value) {
        this.addDemandReceiveResult = value;
    }

}

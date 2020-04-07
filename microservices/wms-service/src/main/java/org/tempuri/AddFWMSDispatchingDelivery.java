
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
 *         &lt;element name="ParamList" type="{http://tempuri.org/}ArrayOfWMSDispatchingComponent" minOccurs="0"/>
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
    "paramList"
})
@XmlRootElement(name = "Add_F_WMS_DispatchingDelivery")
public class AddFWMSDispatchingDelivery {

    @XmlElement(name = "ParamList")
    protected ArrayOfWMSDispatchingComponent paramList;

    /**
     * 获取paramList属性的值。
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfWMSDispatchingComponent }
     *     
     */
    public ArrayOfWMSDispatchingComponent getParamList() {
        return paramList;
    }

    /**
     * 设置paramList属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfWMSDispatchingComponent }
     *     
     */
    public void setParamList(ArrayOfWMSDispatchingComponent value) {
        this.paramList = value;
    }

}

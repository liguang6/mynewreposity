
package org.tempuri;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
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
 *         &lt;element name="myHeader" type="{http://tempuri.org/}WS_WMS_Dispatching_Header" minOccurs="0"/>
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
    "myHeader"
})
@XmlRootElement(name = "Get_DispatchingByNo")
public class GetDispatchingByNo {

    protected WSWMSDispatchingHeader myHeader;

    /**
     * 获取myHeader属性的值。
     * 
     * @return
     *     possible object is
     *     {@link WSWMSDispatchingHeader }
     *     
     */
    public WSWMSDispatchingHeader getMyHeader() {
        return myHeader;
    }

    /**
     * 设置myHeader属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link WSWMSDispatchingHeader }
     *     
     */
    public void setMyHeader(WSWMSDispatchingHeader value) {
        this.myHeader = value;
    }

}

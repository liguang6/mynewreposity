
package org.tempuri;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>WMSDispatchingReturnResult complex type的 Java 类。
 * 
 * <p>以下模式片段指定包含在此类中的预期内容。
 * 
 * <pre>
 * &lt;complexType name="WMSDispatchingReturnResult">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Result" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="Message" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="WMSDispatchingComponentList" type="{http://tempuri.org/}ArrayOfWMSDispatchingComponent" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WMSDispatchingReturnResult", propOrder = {
    "result",
    "message",
    "wmsDispatchingComponentList"
})
public class WMSDispatchingReturnResult {

    @XmlElement(name = "Result")
    protected boolean result;
    @XmlElement(name = "Message")
    protected String message;
    @XmlElement(name = "WMSDispatchingComponentList")
    protected ArrayOfWMSDispatchingComponent wmsDispatchingComponentList;

    /**
     * 获取result属性的值。
     * 
     */
    public boolean isResult() {
        return result;
    }

    /**
     * 设置result属性的值。
     * 
     */
    public void setResult(boolean value) {
        this.result = value;
    }

    /**
     * 获取message属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMessage() {
        return message;
    }

    /**
     * 设置message属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMessage(String value) {
        this.message = value;
    }

    /**
     * 获取wmsDispatchingComponentList属性的值。
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfWMSDispatchingComponent }
     *     
     */
    public ArrayOfWMSDispatchingComponent getWMSDispatchingComponentList() {
        return wmsDispatchingComponentList;
    }

    /**
     * 设置wmsDispatchingComponentList属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfWMSDispatchingComponent }
     *     
     */
    public void setWMSDispatchingComponentList(ArrayOfWMSDispatchingComponent value) {
        this.wmsDispatchingComponentList = value;
    }

}

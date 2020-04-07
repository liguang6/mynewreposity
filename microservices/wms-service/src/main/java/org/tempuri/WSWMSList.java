
package org.tempuri;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>WS_WMS_List complex type的 Java 类。
 * 
 * <p>以下模式片段指定包含在此类中的预期内容。
 * 
 * <pre>
 * &lt;complexType name="WS_WMS_List">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="UserName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Password" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ListNo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="isDemandReceive" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="isLogisMvt" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="ListDemand" type="{http://tempuri.org/}ArrayOfWS_WMS_Demand" minOccurs="0"/>
 *         &lt;element name="ListLogisMvt" type="{http://tempuri.org/}ArrayOfWS_WMS_LogisMvt" minOccurs="0"/>
 *         &lt;element name="Status" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Message" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WS_WMS_List", propOrder = {
    "userName",
    "password",
    "listNo",
    "isDemandReceive",
    "isLogisMvt",
    "listDemand",
    "listLogisMvt",
    "status",
    "message"
})
public class WSWMSList {

    @XmlElement(name = "UserName")
    protected String userName;
    @XmlElement(name = "Password")
    protected String password;
    @XmlElement(name = "ListNo")
    protected String listNo;
    protected int isDemandReceive;
    protected int isLogisMvt;
    @XmlElement(name = "ListDemand")
    protected ArrayOfWSWMSDemand listDemand;
    @XmlElement(name = "ListLogisMvt")
    protected ArrayOfWSWMSLogisMvt listLogisMvt;
    @XmlElement(name = "Status")
    protected String status;
    @XmlElement(name = "Message")
    protected String message;

    /**
     * 获取userName属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUserName() {
        return userName;
    }

    /**
     * 设置userName属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUserName(String value) {
        this.userName = value;
    }

    /**
     * 获取password属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPassword() {
        return password;
    }

    /**
     * 设置password属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPassword(String value) {
        this.password = value;
    }

    /**
     * 获取listNo属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getListNo() {
        return listNo;
    }

    /**
     * 设置listNo属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setListNo(String value) {
        this.listNo = value;
    }

    /**
     * 获取isDemandReceive属性的值。
     * 
     */
    public int getIsDemandReceive() {
        return isDemandReceive;
    }

    /**
     * 设置isDemandReceive属性的值。
     * 
     */
    public void setIsDemandReceive(int value) {
        this.isDemandReceive = value;
    }

    /**
     * 获取isLogisMvt属性的值。
     * 
     */
    public int getIsLogisMvt() {
        return isLogisMvt;
    }

    /**
     * 设置isLogisMvt属性的值。
     * 
     */
    public void setIsLogisMvt(int value) {
        this.isLogisMvt = value;
    }

    /**
     * 获取listDemand属性的值。
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfWSWMSDemand }
     *     
     */
    public ArrayOfWSWMSDemand getListDemand() {
        return listDemand;
    }

    /**
     * 设置listDemand属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfWSWMSDemand }
     *     
     */
    public void setListDemand(ArrayOfWSWMSDemand value) {
        this.listDemand = value;
    }

    /**
     * 获取listLogisMvt属性的值。
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfWSWMSLogisMvt }
     *     
     */
    public ArrayOfWSWMSLogisMvt getListLogisMvt() {
        return listLogisMvt;
    }

    /**
     * 设置listLogisMvt属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfWSWMSLogisMvt }
     *     
     */
    public void setListLogisMvt(ArrayOfWSWMSLogisMvt value) {
        this.listLogisMvt = value;
    }

    /**
     * 获取status属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStatus() {
        return status;
    }

    /**
     * 设置status属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStatus(String value) {
        this.status = value;
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

}

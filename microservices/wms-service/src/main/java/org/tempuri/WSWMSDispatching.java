
package org.tempuri;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>WS_WMS_Dispatching complex type的 Java 类。
 * 
 * <p>以下模式片段指定包含在此类中的预期内容。
 * 
 * <pre>
 * &lt;complexType name="WS_WMS_Dispatching">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="UserName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Password" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ListHeader" type="{http://tempuri.org/}ArrayOfWS_WMS_Dispatching_Header" minOccurs="0"/>
 *         &lt;element name="ListItem" type="{http://tempuri.org/}ArrayOfWS_WMS_Dispatching_Item" minOccurs="0"/>
 *         &lt;element name="ListComponent" type="{http://tempuri.org/}ArrayOfWS_WMS_Dispatching_Component" minOccurs="0"/>
 *         &lt;element name="myPage" type="{http://tempuri.org/}WS_WMS_Page" minOccurs="0"/>
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
@XmlType(name = "WS_WMS_Dispatching", propOrder = {
    "userName",
    "password",
    "listHeader",
    "listItem",
    "listComponent",
    "myPage",
    "status",
    "message"
})
public class WSWMSDispatching {

    @XmlElement(name = "UserName")
    protected String userName;
    @XmlElement(name = "Password")
    protected String password;
    @XmlElement(name = "ListHeader")
    protected ArrayOfWSWMSDispatchingHeader listHeader;
    @XmlElement(name = "ListItem")
    protected ArrayOfWSWMSDispatchingItem listItem;
    @XmlElement(name = "ListComponent")
    protected ArrayOfWSWMSDispatchingComponent listComponent;
    protected WSWMSPage myPage;
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
     * 获取listHeader属性的值。
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfWSWMSDispatchingHeader }
     *     
     */
    public ArrayOfWSWMSDispatchingHeader getListHeader() {
        return listHeader;
    }

    /**
     * 设置listHeader属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfWSWMSDispatchingHeader }
     *     
     */
    public void setListHeader(ArrayOfWSWMSDispatchingHeader value) {
        this.listHeader = value;
    }

    /**
     * 获取listItem属性的值。
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfWSWMSDispatchingItem }
     *     
     */
    public ArrayOfWSWMSDispatchingItem getListItem() {
        return listItem;
    }

    /**
     * 设置listItem属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfWSWMSDispatchingItem }
     *     
     */
    public void setListItem(ArrayOfWSWMSDispatchingItem value) {
        this.listItem = value;
    }

    /**
     * 获取listComponent属性的值。
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfWSWMSDispatchingComponent }
     *     
     */
    public ArrayOfWSWMSDispatchingComponent getListComponent() {
        return listComponent;
    }

    /**
     * 设置listComponent属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfWSWMSDispatchingComponent }
     *     
     */
    public void setListComponent(ArrayOfWSWMSDispatchingComponent value) {
        this.listComponent = value;
    }

    /**
     * 获取myPage属性的值。
     * 
     * @return
     *     possible object is
     *     {@link WSWMSPage }
     *     
     */
    public WSWMSPage getMyPage() {
        return myPage;
    }

    /**
     * 设置myPage属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link WSWMSPage }
     *     
     */
    public void setMyPage(WSWMSPage value) {
        this.myPage = value;
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

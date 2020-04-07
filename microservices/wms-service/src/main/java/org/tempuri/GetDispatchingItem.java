
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
 *         &lt;element name="myItem" type="{http://tempuri.org/}WS_WMS_Dispatching_Item" minOccurs="0"/>
 *         &lt;element name="myPage" type="{http://tempuri.org/}WS_WMS_Page" minOccurs="0"/>
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
    "myItem",
    "myPage"
})
@XmlRootElement(name = "Get_Dispatching_Item")
public class GetDispatchingItem {

    protected WSWMSDispatchingItem myItem;
    protected WSWMSPage myPage;

    /**
     * 获取myItem属性的值。
     * 
     * @return
     *     possible object is
     *     {@link WSWMSDispatchingItem }
     *     
     */
    public WSWMSDispatchingItem getMyItem() {
        return myItem;
    }

    /**
     * 设置myItem属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link WSWMSDispatchingItem }
     *     
     */
    public void setMyItem(WSWMSDispatchingItem value) {
        this.myItem = value;
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

}

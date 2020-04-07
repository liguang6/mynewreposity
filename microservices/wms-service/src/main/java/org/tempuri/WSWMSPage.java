
package org.tempuri;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>WS_WMS_Page complex type的 Java 类。
 * 
 * <p>以下模式片段指定包含在此类中的预期内容。
 * 
 * <pre>
 * &lt;complexType name="WS_WMS_Page">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="iPageIndex" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="iPageSize" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="iPageCount" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="iRecordCount" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="SortField" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="IsDesc" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WS_WMS_Page", propOrder = {
    "iPageIndex",
    "iPageSize",
    "iPageCount",
    "iRecordCount",
    "sortField",
    "isDesc"
})
public class WSWMSPage {

    protected int iPageIndex;
    protected int iPageSize;
    protected int iPageCount;
    protected int iRecordCount;
    @XmlElement(name = "SortField")
    protected String sortField;
    @XmlElement(name = "IsDesc")
    protected int isDesc;

    /**
     * 获取iPageIndex属性的值。
     * 
     */
    public int getIPageIndex() {
        return iPageIndex;
    }

    /**
     * 设置iPageIndex属性的值。
     * 
     */
    public void setIPageIndex(int value) {
        this.iPageIndex = value;
    }

    /**
     * 获取iPageSize属性的值。
     * 
     */
    public int getIPageSize() {
        return iPageSize;
    }

    /**
     * 设置iPageSize属性的值。
     * 
     */
    public void setIPageSize(int value) {
        this.iPageSize = value;
    }

    /**
     * 获取iPageCount属性的值。
     * 
     */
    public int getIPageCount() {
        return iPageCount;
    }

    /**
     * 设置iPageCount属性的值。
     * 
     */
    public void setIPageCount(int value) {
        this.iPageCount = value;
    }

    /**
     * 获取iRecordCount属性的值。
     * 
     */
    public int getIRecordCount() {
        return iRecordCount;
    }

    /**
     * 设置iRecordCount属性的值。
     * 
     */
    public void setIRecordCount(int value) {
        this.iRecordCount = value;
    }

    /**
     * 获取sortField属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSortField() {
        return sortField;
    }

    /**
     * 设置sortField属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSortField(String value) {
        this.sortField = value;
    }

    /**
     * 获取isDesc属性的值。
     * 
     */
    public int getIsDesc() {
        return isDesc;
    }

    /**
     * 设置isDesc属性的值。
     * 
     */
    public void setIsDesc(int value) {
        this.isDesc = value;
    }

}

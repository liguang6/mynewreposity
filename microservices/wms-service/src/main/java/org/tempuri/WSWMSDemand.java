
package org.tempuri;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>WS_WMS_Demand complex type的 Java 类。
 * 
 * <p>以下模式片段指定包含在此类中的预期内容。
 * 
 * <pre>
 * &lt;complexType name="WS_WMS_Demand">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ListNo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ItemNo" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="PartLabelID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="PickListNo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="DemandNo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="DemandType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="AreaCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="PartSAP" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="PartName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="PartCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="SupplierNo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="SupplierName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Batch" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="PackNo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="PackCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="PerPack" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="Ctn" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="Qty" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="Unit" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Pou" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Station" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Section" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Line" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="HandOutEmp" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ReceiveEmp" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="MoveDirection" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="ReturnStatus" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ReturnMessage" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WS_WMS_Demand", propOrder = {
    "listNo",
    "itemNo",
    "partLabelID",
    "pickListNo",
    "demandNo",
    "demandType",
    "areaCode",
    "partSAP",
    "partName",
    "partCode",
    "supplierNo",
    "supplierName",
    "batch",
    "packNo",
    "packCode",
    "perPack",
    "ctn",
    "qty",
    "unit",
    "pou",
    "station",
    "section",
    "line",
    "handOutEmp",
    "receiveEmp",
    "moveDirection",
    "returnStatus",
    "returnMessage"
})
public class WSWMSDemand {

    @XmlElement(name = "ListNo")
    protected String listNo;
    @XmlElement(name = "ItemNo")
    protected int itemNo;
    @XmlElement(name = "PartLabelID")
    protected String partLabelID;
    @XmlElement(name = "PickListNo")
    protected String pickListNo;
    @XmlElement(name = "DemandNo")
    protected String demandNo;
    @XmlElement(name = "DemandType")
    protected String demandType;
    @XmlElement(name = "AreaCode")
    protected String areaCode;
    @XmlElement(name = "PartSAP")
    protected String partSAP;
    @XmlElement(name = "PartName")
    protected String partName;
    @XmlElement(name = "PartCode")
    protected String partCode;
    @XmlElement(name = "SupplierNo")
    protected String supplierNo;
    @XmlElement(name = "SupplierName")
    protected String supplierName;
    @XmlElement(name = "Batch")
    protected String batch;
    @XmlElement(name = "PackNo")
    protected String packNo;
    @XmlElement(name = "PackCode")
    protected String packCode;
    @XmlElement(name = "PerPack")
    protected double perPack;
    @XmlElement(name = "Ctn")
    protected int ctn;
    @XmlElement(name = "Qty")
    protected double qty;
    @XmlElement(name = "Unit")
    protected String unit;
    @XmlElement(name = "Pou")
    protected String pou;
    @XmlElement(name = "Station")
    protected String station;
    @XmlElement(name = "Section")
    protected String section;
    @XmlElement(name = "Line")
    protected String line;
    @XmlElement(name = "HandOutEmp")
    protected String handOutEmp;
    @XmlElement(name = "ReceiveEmp")
    protected String receiveEmp;
    @XmlElement(name = "MoveDirection")
    protected int moveDirection;
    @XmlElement(name = "ReturnStatus")
    protected String returnStatus;
    @XmlElement(name = "ReturnMessage")
    protected String returnMessage;

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
     * 获取itemNo属性的值。
     * 
     */
    public int getItemNo() {
        return itemNo;
    }

    /**
     * 设置itemNo属性的值。
     * 
     */
    public void setItemNo(int value) {
        this.itemNo = value;
    }

    /**
     * 获取partLabelID属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPartLabelID() {
        return partLabelID;
    }

    /**
     * 设置partLabelID属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPartLabelID(String value) {
        this.partLabelID = value;
    }

    /**
     * 获取pickListNo属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPickListNo() {
        return pickListNo;
    }

    /**
     * 设置pickListNo属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPickListNo(String value) {
        this.pickListNo = value;
    }

    /**
     * 获取demandNo属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDemandNo() {
        return demandNo;
    }

    /**
     * 设置demandNo属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDemandNo(String value) {
        this.demandNo = value;
    }

    /**
     * 获取demandType属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDemandType() {
        return demandType;
    }

    /**
     * 设置demandType属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDemandType(String value) {
        this.demandType = value;
    }

    /**
     * 获取areaCode属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAreaCode() {
        return areaCode;
    }

    /**
     * 设置areaCode属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAreaCode(String value) {
        this.areaCode = value;
    }

    /**
     * 获取partSAP属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPartSAP() {
        return partSAP;
    }

    /**
     * 设置partSAP属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPartSAP(String value) {
        this.partSAP = value;
    }

    /**
     * 获取partName属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPartName() {
        return partName;
    }

    /**
     * 设置partName属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPartName(String value) {
        this.partName = value;
    }

    /**
     * 获取partCode属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPartCode() {
        return partCode;
    }

    /**
     * 设置partCode属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPartCode(String value) {
        this.partCode = value;
    }

    /**
     * 获取supplierNo属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSupplierNo() {
        return supplierNo;
    }

    /**
     * 设置supplierNo属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSupplierNo(String value) {
        this.supplierNo = value;
    }

    /**
     * 获取supplierName属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSupplierName() {
        return supplierName;
    }

    /**
     * 设置supplierName属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSupplierName(String value) {
        this.supplierName = value;
    }

    /**
     * 获取batch属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBatch() {
        return batch;
    }

    /**
     * 设置batch属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBatch(String value) {
        this.batch = value;
    }

    /**
     * 获取packNo属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPackNo() {
        return packNo;
    }

    /**
     * 设置packNo属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPackNo(String value) {
        this.packNo = value;
    }

    /**
     * 获取packCode属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPackCode() {
        return packCode;
    }

    /**
     * 设置packCode属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPackCode(String value) {
        this.packCode = value;
    }

    /**
     * 获取perPack属性的值。
     * 
     */
    public double getPerPack() {
        return perPack;
    }

    /**
     * 设置perPack属性的值。
     * 
     */
    public void setPerPack(double value) {
        this.perPack = value;
    }

    /**
     * 获取ctn属性的值。
     * 
     */
    public int getCtn() {
        return ctn;
    }

    /**
     * 设置ctn属性的值。
     * 
     */
    public void setCtn(int value) {
        this.ctn = value;
    }

    /**
     * 获取qty属性的值。
     * 
     */
    public double getQty() {
        return qty;
    }

    /**
     * 设置qty属性的值。
     * 
     */
    public void setQty(double value) {
        this.qty = value;
    }

    /**
     * 获取unit属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUnit() {
        return unit;
    }

    /**
     * 设置unit属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUnit(String value) {
        this.unit = value;
    }

    /**
     * 获取pou属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPou() {
        return pou;
    }

    /**
     * 设置pou属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPou(String value) {
        this.pou = value;
    }

    /**
     * 获取station属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStation() {
        return station;
    }

    /**
     * 设置station属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStation(String value) {
        this.station = value;
    }

    /**
     * 获取section属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSection() {
        return section;
    }

    /**
     * 设置section属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSection(String value) {
        this.section = value;
    }

    /**
     * 获取line属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLine() {
        return line;
    }

    /**
     * 设置line属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLine(String value) {
        this.line = value;
    }

    /**
     * 获取handOutEmp属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHandOutEmp() {
        return handOutEmp;
    }

    /**
     * 设置handOutEmp属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHandOutEmp(String value) {
        this.handOutEmp = value;
    }

    /**
     * 获取receiveEmp属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReceiveEmp() {
        return receiveEmp;
    }

    /**
     * 设置receiveEmp属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReceiveEmp(String value) {
        this.receiveEmp = value;
    }

    /**
     * 获取moveDirection属性的值。
     * 
     */
    public int getMoveDirection() {
        return moveDirection;
    }

    /**
     * 设置moveDirection属性的值。
     * 
     */
    public void setMoveDirection(int value) {
        this.moveDirection = value;
    }

    /**
     * 获取returnStatus属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReturnStatus() {
        return returnStatus;
    }

    /**
     * 设置returnStatus属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReturnStatus(String value) {
        this.returnStatus = value;
    }

    /**
     * 获取returnMessage属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReturnMessage() {
        return returnMessage;
    }

    /**
     * 设置returnMessage属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReturnMessage(String value) {
        this.returnMessage = value;
    }

}


package org.tempuri;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>WMSDispatchingComponent complex type的 Java 类。
 * 
 * <p>以下模式片段指定包含在此类中的预期内容。
 * 
 * <pre>
 * &lt;complexType name="WMSDispatchingComponent">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="WMS_DELIVERY_NO" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="DELIVERY_NO" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="DELIVERY_ITEM_NO" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="REQUIREMENT_NO" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="WMS_REQUIREMENT_NO" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="WMS_REQUIREMENT_ITEM_NO" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="WAREHOUSE_CODE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="PLCD" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="LOCD" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="MATNR" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="MATEDS" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="QUANTY" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="UNIT" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="STATION" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="POU" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="PRODUCTION_LINE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="BATCH" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="DeliveryType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="DeliveryRoute" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="DeliveryTime" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="LineFeedingRoute" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="VENDOR_CODE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="VENDOR" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="STTPNM" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Stock" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="BinCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ReceivePLCD" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ReceiveLOCD" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ReceiveLOCDType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="MOTYPE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="RFID_No" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Product_No" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Trailer_ID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="REMARK" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="PackCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="PerPack" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="PackType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="iState" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="DemandStatus" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="STATUS" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="pzh" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="MESSAGE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Direction" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WMSDispatchingComponent", propOrder = {
    "wmsdeliveryno",
    "deliveryno",
    "deliveryitemno",
    "requirementno",
    "wmsrequirementno",
    "wmsrequirementitemno",
    "warehousecode",
    "plcd",
    "locd",
    "matnr",
    "mateds",
    "quanty",
    "unit",
    "station",
    "pou",
    "productionline",
    "batch",
    "deliveryType",
    "deliveryRoute",
    "deliveryTime",
    "lineFeedingRoute",
    "vendorcode",
    "vendor",
    "sttpnm",
    "stock",
    "binCode",
    "receivePLCD",
    "receiveLOCD",
    "receiveLOCDType",
    "motype",
    "rfidNo",
    "productNo",
    "trailerID",
    "remark",
    "packCode",
    "perPack",
    "packType",
    "iState",
    "demandStatus",
    "status",
    "pzh",
    "message",
    "direction"
})
public class WMSDispatchingComponent {

    @XmlElement(name = "WMS_DELIVERY_NO")
    protected String wmsdeliveryno;
    @XmlElement(name = "DELIVERY_NO")
    protected String deliveryno;
    @XmlElement(name = "DELIVERY_ITEM_NO")
    protected String deliveryitemno;
    @XmlElement(name = "REQUIREMENT_NO")
    protected String requirementno;
    @XmlElement(name = "WMS_REQUIREMENT_NO")
    protected String wmsrequirementno;
    @XmlElement(name = "WMS_REQUIREMENT_ITEM_NO")
    protected String wmsrequirementitemno;
    @XmlElement(name = "WAREHOUSE_CODE")
    protected String warehousecode;
    @XmlElement(name = "PLCD")
    protected String plcd;
    @XmlElement(name = "LOCD")
    protected String locd;
    @XmlElement(name = "MATNR")
    protected String matnr;
    @XmlElement(name = "MATEDS")
    protected String mateds;
    @XmlElement(name = "QUANTY")
    protected double quanty;
    @XmlElement(name = "UNIT")
    protected String unit;
    @XmlElement(name = "STATION")
    protected String station;
    @XmlElement(name = "POU")
    protected String pou;
    @XmlElement(name = "PRODUCTION_LINE")
    protected String productionline;
    @XmlElement(name = "BATCH")
    protected String batch;
    @XmlElement(name = "DeliveryType")
    protected String deliveryType;
    @XmlElement(name = "DeliveryRoute")
    protected String deliveryRoute;
    @XmlElement(name = "DeliveryTime")
    protected String deliveryTime;
    @XmlElement(name = "LineFeedingRoute")
    protected String lineFeedingRoute;
    @XmlElement(name = "VENDOR_CODE")
    protected String vendorcode;
    @XmlElement(name = "VENDOR")
    protected String vendor;
    @XmlElement(name = "STTPNM")
    protected String sttpnm;
    @XmlElement(name = "Stock")
    protected String stock;
    @XmlElement(name = "BinCode")
    protected String binCode;
    @XmlElement(name = "ReceivePLCD")
    protected String receivePLCD;
    @XmlElement(name = "ReceiveLOCD")
    protected String receiveLOCD;
    @XmlElement(name = "ReceiveLOCDType")
    protected String receiveLOCDType;
    @XmlElement(name = "MOTYPE")
    protected String motype;
    @XmlElement(name = "RFID_No")
    protected String rfidNo;
    @XmlElement(name = "Product_No")
    protected String productNo;
    @XmlElement(name = "Trailer_ID")
    protected String trailerID;
    @XmlElement(name = "REMARK")
    protected String remark;
    @XmlElement(name = "PackCode")
    protected String packCode;
    @XmlElement(name = "PerPack")
    protected double perPack;
    @XmlElement(name = "PackType")
    protected String packType;
    protected int iState;
    @XmlElement(name = "DemandStatus")
    protected String demandStatus;
    @XmlElement(name = "STATUS")
    protected String status;
    protected String pzh;
    @XmlElement(name = "MESSAGE")
    protected String message;
    @XmlElement(name = "Direction")
    protected int direction;

    /**
     * ��ȡwmsdeliveryno���Ե�ֵ��
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getWMSDELIVERYNO() {
        return wmsdeliveryno;
    }

    /**
     * ����wmsdeliveryno���Ե�ֵ��
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWMSDELIVERYNO(String value) {
        this.wmsdeliveryno = value;
    }

    /**
     * 获取deliveryno属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDELIVERYNO() {
        return deliveryno;
    }

    /**
     * 设置deliveryno属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDELIVERYNO(String value) {
        this.deliveryno = value;
    }

    /**
     * 获取deliveryitemno属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDELIVERYITEMNO() {
        return deliveryitemno;
    }

    /**
     * 设置deliveryitemno属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDELIVERYITEMNO(String value) {
        this.deliveryitemno = value;
    }

    /**
     * 获取requirementno属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getREQUIREMENTNO() {
        return requirementno;
    }

    /**
     *  设置requirementno属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setREQUIREMENTNO(String value) {
        this.requirementno = value;
    }

    /**
     * 获取wmsrequirementno属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getWMSREQUIREMENTNO() {
        return wmsrequirementno;
    }

    /**
     * 设置wmsrequirementno属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWMSREQUIREMENTNO(String value) {
        this.wmsrequirementno = value;
    }

    /**
     * 获取wmsrequirementitemno属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getWMSREQUIREMENTITEMNO() {
        return wmsrequirementitemno;
    }

    /**
     * 设置wmsrequirementitemno属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWMSREQUIREMENTITEMNO(String value) {
        this.wmsrequirementitemno = value;
    }

    /**
     * 获取warehousecode属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getWAREHOUSECODE() {
        return warehousecode;
    }

    /**
     * 设置warehousecode属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWAREHOUSECODE(String value) {
        this.warehousecode = value;
    }

    /**
     * 获取plcd属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPLCD() {
        return plcd;
    }

    /**
     * 设置plcd属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPLCD(String value) {
        this.plcd = value;
    }

    /**
     * 获取locd属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLOCD() {
        return locd;
    }

    /**
     * ����locd���Ե�ֵ��
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLOCD(String value) {
        this.locd = value;
    }

    /**
     * ��ȡmatnr���Ե�ֵ��
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMATNR() {
        return matnr;
    }

    /**
     * ����matnr���Ե�ֵ��
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMATNR(String value) {
        this.matnr = value;
    }

    /**
     * ��ȡmateds���Ե�ֵ��
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMATEDS() {
        return mateds;
    }

    /**
     * ����mateds���Ե�ֵ��
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMATEDS(String value) {
        this.mateds = value;
    }

    /**
     * ��ȡquanty���Ե�ֵ��
     * 
     */
    public double getQUANTY() {
        return quanty;
    }

    /**
     * ����quanty���Ե�ֵ��
     * 
     */
    public void setQUANTY(double value) {
        this.quanty = value;
    }

    /**
     * ��ȡunit���Ե�ֵ��
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUNIT() {
        return unit;
    }

    /**
     * ����unit���Ե�ֵ��
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUNIT(String value) {
        this.unit = value;
    }

    /**
     * ��ȡstation���Ե�ֵ��
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSTATION() {
        return station;
    }

    /**
     * ����station���Ե�ֵ��
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSTATION(String value) {
        this.station = value;
    }

    /**
     * ��ȡpou���Ե�ֵ��
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPOU() {
        return pou;
    }

    /**
     * ����pou���Ե�ֵ��
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPOU(String value) {
        this.pou = value;
    }

    /**
     * ��ȡproductionline���Ե�ֵ��
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPRODUCTIONLINE() {
        return productionline;
    }

    /**
     * ����productionline���Ե�ֵ��
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPRODUCTIONLINE(String value) {
        this.productionline = value;
    }

    /**
     * ��ȡbatch���Ե�ֵ��
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBATCH() {
        return batch;
    }

    /**
     * ����batch���Ե�ֵ��
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBATCH(String value) {
        this.batch = value;
    }

    /**
     * ��ȡdeliveryType���Ե�ֵ��
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDeliveryType() {
        return deliveryType;
    }

    /**
     * ����deliveryType���Ե�ֵ��
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDeliveryType(String value) {
        this.deliveryType = value;
    }

    /**
     * ��ȡdeliveryRoute���Ե�ֵ��
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDeliveryRoute() {
        return deliveryRoute;
    }

    /**
     * ����deliveryRoute���Ե�ֵ��
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDeliveryRoute(String value) {
        this.deliveryRoute = value;
    }

    /**
     * ��ȡdeliveryTime���Ե�ֵ��
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDeliveryTime() {
        return deliveryTime;
    }

    /**
     * ����deliveryTime���Ե�ֵ��
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDeliveryTime(String value) {
        this.deliveryTime = value;
    }

    /**
     * ��ȡlineFeedingRoute���Ե�ֵ��
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLineFeedingRoute() {
        return lineFeedingRoute;
    }

    /**
     * ����lineFeedingRoute���Ե�ֵ��
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLineFeedingRoute(String value) {
        this.lineFeedingRoute = value;
    }

    /**
     * ��ȡvendorcode���Ե�ֵ��
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVENDORCODE() {
        return vendorcode;
    }

    /**
     * ����vendorcode���Ե�ֵ��
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVENDORCODE(String value) {
        this.vendorcode = value;
    }

    /**
     * ��ȡvendor���Ե�ֵ��
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVENDOR() {
        return vendor;
    }

    /**
     * ����vendor���Ե�ֵ��
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVENDOR(String value) {
        this.vendor = value;
    }

    /**
     * ��ȡsttpnm���Ե�ֵ��
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSTTPNM() {
        return sttpnm;
    }

    /**
     * ����sttpnm���Ե�ֵ��
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSTTPNM(String value) {
        this.sttpnm = value;
    }

    /**
     * ��ȡstock���Ե�ֵ��
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStock() {
        return stock;
    }

    /**
     * ����stock���Ե�ֵ��
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStock(String value) {
        this.stock = value;
    }

    /**
     * ��ȡbinCode���Ե�ֵ��
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBinCode() {
        return binCode;
    }

    /**
     * ����binCode���Ե�ֵ��
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBinCode(String value) {
        this.binCode = value;
    }

    /**
     * ��ȡreceivePLCD���Ե�ֵ��
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReceivePLCD() {
        return receivePLCD;
    }

    /**
     * ����receivePLCD���Ե�ֵ��
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReceivePLCD(String value) {
        this.receivePLCD = value;
    }

    /**
     * ��ȡreceiveLOCD���Ե�ֵ��
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReceiveLOCD() {
        return receiveLOCD;
    }

    /**
     * ����receiveLOCD���Ե�ֵ��
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReceiveLOCD(String value) {
        this.receiveLOCD = value;
    }

    /**
     * ��ȡreceiveLOCDType���Ե�ֵ��
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReceiveLOCDType() {
        return receiveLOCDType;
    }

    /**
     * ����receiveLOCDType���Ե�ֵ��
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReceiveLOCDType(String value) {
        this.receiveLOCDType = value;
    }

    /**
     * ��ȡmotype���Ե�ֵ��
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMOTYPE() {
        return motype;
    }

    /**
     * ����motype���Ե�ֵ��
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMOTYPE(String value) {
        this.motype = value;
    }

    /**
     * ��ȡrfidNo���Ե�ֵ��
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRFIDNo() {
        return rfidNo;
    }

    /**
     * ����rfidNo���Ե�ֵ��
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRFIDNo(String value) {
        this.rfidNo = value;
    }

    /**
     * ��ȡproductNo���Ե�ֵ��
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProductNo() {
        return productNo;
    }

    /**
     * ����productNo���Ե�ֵ��
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProductNo(String value) {
        this.productNo = value;
    }

    /**
     * ��ȡtrailerID���Ե�ֵ��
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTrailerID() {
        return trailerID;
    }

    /**
     * ����trailerID���Ե�ֵ��
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTrailerID(String value) {
        this.trailerID = value;
    }

    /**
     * ��ȡremark���Ե�ֵ��
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getREMARK() {
        return remark;
    }

    /**
     * ����remark���Ե�ֵ��
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setREMARK(String value) {
        this.remark = value;
    }

    /**
     * ��ȡpackCode���Ե�ֵ��
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
     * ����packCode���Ե�ֵ��
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
     * ��ȡperPack���Ե�ֵ��
     * 
     */
    public double getPerPack() {
        return perPack;
    }

    /**
     * ����perPack���Ե�ֵ��
     * 
     */
    public void setPerPack(double value) {
        this.perPack = value;
    }

    /**
     * ��ȡpackType���Ե�ֵ��
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPackType() {
        return packType;
    }

    /**
     * ����packType���Ե�ֵ��
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPackType(String value) {
        this.packType = value;
    }

    /**
     * ��ȡiState���Ե�ֵ��
     * 
     */
    public int getIState() {
        return iState;
    }

    /**
     * ����iState���Ե�ֵ��
     * 
     */
    public void setIState(int value) {
        this.iState = value;
    }

    /**
     * ��ȡdemandStatus���Ե�ֵ��
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDemandStatus() {
        return demandStatus;
    }

    /**
     * ����demandStatus���Ե�ֵ��
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDemandStatus(String value) {
        this.demandStatus = value;
    }

    /**
     * ��ȡstatus���Ե�ֵ��
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSTATUS() {
        return status;
    }

    /**
     * ����status���Ե�ֵ��
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSTATUS(String value) {
        this.status = value;
    }

    /**
     * ��ȡpzh���Ե�ֵ��
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPzh() {
        return pzh;
    }

    /**
     * ����pzh���Ե�ֵ��
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPzh(String value) {
        this.pzh = value;
    }

    /**
     * ��ȡmessage���Ե�ֵ��
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMESSAGE() {
        return message;
    }

    /**
     * ����message���Ե�ֵ��
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMESSAGE(String value) {
        this.message = value;
    }

    /**
     * ��ȡdirection���Ե�ֵ��
     * 
     */
    public int getDirection() {
        return direction;
    }

    /**
     * ����direction���Ե�ֵ��
     * 
     */
    public void setDirection(int value) {
        this.direction = value;
    }

}

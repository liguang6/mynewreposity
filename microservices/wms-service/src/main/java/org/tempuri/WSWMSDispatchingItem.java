
package org.tempuri;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>WS_WMS_Dispatching_Item complex type的 Java 类。
 * 
 * <p>以下模式片段指定包含在此类中的预期内容。
 * 
 * <pre>
 * &lt;complexType name="WS_WMS_Dispatching_Item">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="PLANT_CODE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="FROM_PLANT_CODE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="FROM_WAREHOUSING_CODE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="STATUS" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="TYPE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="FeedType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="DISPATCHING_NO" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ITEM_NO" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="PICKING_ADDRESS" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="UNIQUE_IDENTIFY_CODE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="LINE_GATEGORY" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Line" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="MATERIAL_CODE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="MATERIAL_DESC" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="CAR_MODEL" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="CAR_COLOR" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="UNIT" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="QUANTITY" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="REQUIREMENT_NO" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="HANDOVER_QUANTITY" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="PRINT_DATE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="HANDOVER_DATE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="REQUIRING_DATE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="LINE_REQUIREMENT_DATE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="TRIAL_FLAG" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="DEL" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="DISPATCHING_ADDRESS" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="WORKING_LOCATION" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="DOLLYPOS" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="CAR_SERIES" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="JIS_SEQUENCE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="RELATED_GROUP" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="REMARK" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ACTUAL_PRINT_DATE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ACTUAL_HANDOVER_DATE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="PRE_PRINT_DATE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="CREATE_DATE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ListComponent" type="{http://tempuri.org/}ArrayOfWS_WMS_Dispatching_Component" minOccurs="0"/>
 *         &lt;element name="isExeOperate" type="{http://www.w3.org/2001/XMLSchema}int"/>
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
@XmlType(name = "WS_WMS_Dispatching_Item", propOrder = {
    "plantcode",
    "fromplantcode",
    "fromwarehousingcode",
    "status",
    "type",
    "feedType",
    "dispatchingno",
    "itemno",
    "pickingaddress",
    "uniqueidentifycode",
    "linegategory",
    "line",
    "materialcode",
    "materialdesc",
    "carmodel",
    "carcolor",
    "unit",
    "quantity",
    "requirementno",
    "handoverquantity",
    "printdate",
    "handoverdate",
    "requiringdate",
    "linerequirementdate",
    "trialflag",
    "del",
    "dispatchingaddress",
    "workinglocation",
    "dollypos",
    "carseries",
    "jissequence",
    "relatedgroup",
    "remark",
    "actualprintdate",
    "actualhandoverdate",
    "preprintdate",
    "createdate",
    "listComponent",
    "isExeOperate",
    "returnStatus",
    "returnMessage"
})
public class WSWMSDispatchingItem {

    @XmlElement(name = "PLANT_CODE")
    protected String plantcode;
    @XmlElement(name = "FROM_PLANT_CODE")
    protected String fromplantcode;
    @XmlElement(name = "FROM_WAREHOUSING_CODE")
    protected String fromwarehousingcode;
    @XmlElement(name = "STATUS")
    protected String status;
    @XmlElement(name = "TYPE")
    protected String type;
    @XmlElement(name = "FeedType")
    protected String feedType;
    @XmlElement(name = "DISPATCHING_NO")
    protected String dispatchingno;
    @XmlElement(name = "ITEM_NO")
    protected String itemno;
    @XmlElement(name = "PICKING_ADDRESS")
    protected String pickingaddress;
    @XmlElement(name = "UNIQUE_IDENTIFY_CODE")
    protected String uniqueidentifycode;
    @XmlElement(name = "LINE_GATEGORY")
    protected String linegategory;
    @XmlElement(name = "Line")
    protected String line;
    @XmlElement(name = "MATERIAL_CODE")
    protected String materialcode;
    @XmlElement(name = "MATERIAL_DESC")
    protected String materialdesc;
    @XmlElement(name = "CAR_MODEL")
    protected String carmodel;
    @XmlElement(name = "CAR_COLOR")
    protected String carcolor;
    @XmlElement(name = "UNIT")
    protected String unit;
    @XmlElement(name = "QUANTITY")
    protected double quantity;
    @XmlElement(name = "REQUIREMENT_NO")
    protected String requirementno;
    @XmlElement(name = "HANDOVER_QUANTITY")
    protected double handoverquantity;
    @XmlElement(name = "PRINT_DATE")
    protected String printdate;
    @XmlElement(name = "HANDOVER_DATE")
    protected String handoverdate;
    @XmlElement(name = "REQUIRING_DATE")
    protected String requiringdate;
    @XmlElement(name = "LINE_REQUIREMENT_DATE")
    protected String linerequirementdate;
    @XmlElement(name = "TRIAL_FLAG")
    protected String trialflag;
    @XmlElement(name = "DEL")
    protected String del;
    @XmlElement(name = "DISPATCHING_ADDRESS")
    protected String dispatchingaddress;
    @XmlElement(name = "WORKING_LOCATION")
    protected String workinglocation;
    @XmlElement(name = "DOLLYPOS")
    protected String dollypos;
    @XmlElement(name = "CAR_SERIES")
    protected String carseries;
    @XmlElement(name = "JIS_SEQUENCE")
    protected String jissequence;
    @XmlElement(name = "RELATED_GROUP")
    protected String relatedgroup;
    @XmlElement(name = "REMARK")
    protected String remark;
    @XmlElement(name = "ACTUAL_PRINT_DATE")
    protected String actualprintdate;
    @XmlElement(name = "ACTUAL_HANDOVER_DATE")
    protected String actualhandoverdate;
    @XmlElement(name = "PRE_PRINT_DATE")
    protected String preprintdate;
    @XmlElement(name = "CREATE_DATE")
    protected String createdate;
    @XmlElement(name = "ListComponent")
    protected ArrayOfWSWMSDispatchingComponent listComponent;
    protected int isExeOperate;
    @XmlElement(name = "ReturnStatus")
    protected String returnStatus;
    @XmlElement(name = "ReturnMessage")
    protected String returnMessage;

    /**
     * 获取plantcode属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPLANTCODE() {
        return plantcode;
    }

    /**
     * 设置plantcode属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPLANTCODE(String value) {
        this.plantcode = value;
    }

    /**
     * 获取fromplantcode属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFROMPLANTCODE() {
        return fromplantcode;
    }

    /**
     * 设置fromplantcode属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFROMPLANTCODE(String value) {
        this.fromplantcode = value;
    }

    /**
     * 获取fromwarehousingcode属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFROMWAREHOUSINGCODE() {
        return fromwarehousingcode;
    }

    /**
     * 设置fromwarehousingcode属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFROMWAREHOUSINGCODE(String value) {
        this.fromwarehousingcode = value;
    }

    /**
     * 获取status属性的值。
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
     * 设置status属性的值。
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
     * 获取type属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTYPE() {
        return type;
    }

    /**
     * 设置type属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTYPE(String value) {
        this.type = value;
    }

    /**
     * 获取feedType属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFeedType() {
        return feedType;
    }

    /**
     * 设置feedType属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFeedType(String value) {
        this.feedType = value;
    }

    /**
     * 获取dispatchingno属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDISPATCHINGNO() {
        return dispatchingno;
    }

    /**
     * 设置dispatchingno属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDISPATCHINGNO(String value) {
        this.dispatchingno = value;
    }

    /**
     * 获取itemno属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getITEMNO() {
        return itemno;
    }

    /**
     * 设置itemno属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setITEMNO(String value) {
        this.itemno = value;
    }

    /**
     * 获取pickingaddress属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPICKINGADDRESS() {
        return pickingaddress;
    }

    /**
     * 设置pickingaddress属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPICKINGADDRESS(String value) {
        this.pickingaddress = value;
    }

    /**
     * 获取uniqueidentifycode属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUNIQUEIDENTIFYCODE() {
        return uniqueidentifycode;
    }

    /**
     * 设置uniqueidentifycode属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUNIQUEIDENTIFYCODE(String value) {
        this.uniqueidentifycode = value;
    }

    /**
     * 获取linegategory属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLINEGATEGORY() {
        return linegategory;
    }

    /**
     * 设置linegategory属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLINEGATEGORY(String value) {
        this.linegategory = value;
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
     * 获取materialcode属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMATERIALCODE() {
        return materialcode;
    }

    /**
     * 设置materialcode属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMATERIALCODE(String value) {
        this.materialcode = value;
    }

    /**
     * 获取materialdesc属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMATERIALDESC() {
        return materialdesc;
    }

    /**
     * 设置materialdesc属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMATERIALDESC(String value) {
        this.materialdesc = value;
    }

    /**
     * 获取carmodel属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCARMODEL() {
        return carmodel;
    }

    /**
     * 设置carmodel属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCARMODEL(String value) {
        this.carmodel = value;
    }

    /**
     * 获取carcolor属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCARCOLOR() {
        return carcolor;
    }

    /**
     * 设置carcolor属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCARCOLOR(String value) {
        this.carcolor = value;
    }

    /**
     * 获取unit属性的值。
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
     * 设置unit属性的值。
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
     * 获取quantity属性的值。
     * 
     */
    public double getQUANTITY() {
        return quantity;
    }

    /**
     * 设置quantity属性的值。
     * 
     */
    public void setQUANTITY(double value) {
        this.quantity = value;
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
     * 设置requirementno属性的值。
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
     * 获取handoverquantity属性的值。
     * 
     */
    public double getHANDOVERQUANTITY() {
        return handoverquantity;
    }

    /**
     * 设置handoverquantity属性的值。
     * 
     */
    public void setHANDOVERQUANTITY(double value) {
        this.handoverquantity = value;
    }

    /**
     * 获取printdate属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPRINTDATE() {
        return printdate;
    }

    /**
     * 设置printdate属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPRINTDATE(String value) {
        this.printdate = value;
    }

    /**
     * 获取handoverdate属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHANDOVERDATE() {
        return handoverdate;
    }

    /**
     * 设置handoverdate属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHANDOVERDATE(String value) {
        this.handoverdate = value;
    }

    /**
     * 获取requiringdate属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getREQUIRINGDATE() {
        return requiringdate;
    }

    /**
     * 设置requiringdate属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setREQUIRINGDATE(String value) {
        this.requiringdate = value;
    }

    /**
     * 获取linerequirementdate属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLINEREQUIREMENTDATE() {
        return linerequirementdate;
    }

    /**
     * 设置linerequirementdate属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLINEREQUIREMENTDATE(String value) {
        this.linerequirementdate = value;
    }

    /**
     * 获取trialflag属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTRIALFLAG() {
        return trialflag;
    }

    /**
     * 设置trialflag属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTRIALFLAG(String value) {
        this.trialflag = value;
    }

    /**
     * 获取del属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDEL() {
        return del;
    }

    /**
     * 设置del属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDEL(String value) {
        this.del = value;
    }

    /**
     * 获取dispatchingaddress属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDISPATCHINGADDRESS() {
        return dispatchingaddress;
    }

    /**
     * 设置dispatchingaddress属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDISPATCHINGADDRESS(String value) {
        this.dispatchingaddress = value;
    }

    /**
     * 获取workinglocation属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getWORKINGLOCATION() {
        return workinglocation;
    }

    /**
     * 设置workinglocation属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWORKINGLOCATION(String value) {
        this.workinglocation = value;
    }

    /**
     * 获取dollypos属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDOLLYPOS() {
        return dollypos;
    }

    /**
     * 设置dollypos属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDOLLYPOS(String value) {
        this.dollypos = value;
    }

    /**
     * 获取carseries属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCARSERIES() {
        return carseries;
    }

    /**
     * 设置carseries属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCARSERIES(String value) {
        this.carseries = value;
    }

    /**
     * 获取jissequence属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getJISSEQUENCE() {
        return jissequence;
    }

    /**
     * 设置jissequence属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setJISSEQUENCE(String value) {
        this.jissequence = value;
    }

    /**
     * 获取relatedgroup属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRELATEDGROUP() {
        return relatedgroup;
    }

    /**
     * 设置relatedgroup属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRELATEDGROUP(String value) {
        this.relatedgroup = value;
    }

    /**
     * 获取remark属性的值。
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
     * 设置remark属性的值。
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
     * 获取actualprintdate属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getACTUALPRINTDATE() {
        return actualprintdate;
    }

    /**
     * 设置actualprintdate属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setACTUALPRINTDATE(String value) {
        this.actualprintdate = value;
    }

    /**
     * 获取actualhandoverdate属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getACTUALHANDOVERDATE() {
        return actualhandoverdate;
    }

    /**
     * 设置actualhandoverdate属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setACTUALHANDOVERDATE(String value) {
        this.actualhandoverdate = value;
    }

    /**
     * 获取preprintdate属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPREPRINTDATE() {
        return preprintdate;
    }

    /**
     * 设置preprintdate属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPREPRINTDATE(String value) {
        this.preprintdate = value;
    }

    /**
     * 获取createdate属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCREATEDATE() {
        return createdate;
    }

    /**
     * 设置createdate属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCREATEDATE(String value) {
        this.createdate = value;
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
     * 获取isExeOperate属性的值。
     * 
     */
    public int getIsExeOperate() {
        return isExeOperate;
    }

    /**
     * 设置isExeOperate属性的值。
     * 
     */
    public void setIsExeOperate(int value) {
        this.isExeOperate = value;
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

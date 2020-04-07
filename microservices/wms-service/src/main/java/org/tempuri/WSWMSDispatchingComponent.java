
package org.tempuri;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>WS_WMS_Dispatching_Component complex type的 Java 类。
 * 
 * <p>以下模式片段指定包含在此类中的预期内容。
 * 
 * <pre>
 * &lt;complexType name="WS_WMS_Dispatching_Component">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="PLANT_CODE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="FROM_PLANT_CODE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="FROM_WAREHOUSING_CODE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="BARCODE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="DISPATCHING_NO" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ITEM_NO" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="COMPONENT_NO" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="STATUS" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="FeedType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="VENDOR_CODE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="VENDOR_NAME" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="PICKING_ADDRESS" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="MATERIAL_CODE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="MATERIAL_DESC" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="UNIT" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="QUANTITY" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="PACKAGE_MODEL" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="PACKAGE_TYPE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="DEL" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="PICKING_USER_ID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="PICKING_USER_NAME" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="BATCH" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="AUTO_FLAG" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="PACKAGE_QTY" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="ACTUAL_PRINT_DATE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ACTUAL_HANDOVER_DATE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="DELETE_REMARK" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="HANDOVER_USER_ID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="REQUIREMENT_TYPE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="COMFIRM_DATE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="DOLLYPOS" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="CREATE_DATE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
@XmlType(name = "WS_WMS_Dispatching_Component", propOrder = {
    "plantcode",
    "fromplantcode",
    "fromwarehousingcode",
    "barcode",
    "dispatchingno",
    "itemno",
    "componentno",
    "status",
    "feedType",
    "vendorcode",
    "vendorname",
    "pickingaddress",
    "materialcode",
    "materialdesc",
    "unit",
    "quantity",
    "packagemodel",
    "packagetype",
    "del",
    "pickinguserid",
    "pickingusername",
    "batch",
    "autoflag",
    "packageqty",
    "actualprintdate",
    "actualhandoverdate",
    "deleteremark",
    "handoveruserid",
    "requirementtype",
    "comfirmdate",
    "dollypos",
    "createdate",
    "isExeOperate",
    "returnStatus",
    "returnMessage"
})
public class WSWMSDispatchingComponent {

    @XmlElement(name = "PLANT_CODE")
    protected String plantcode;
    @XmlElement(name = "FROM_PLANT_CODE")
    protected String fromplantcode;
    @XmlElement(name = "FROM_WAREHOUSING_CODE")
    protected String fromwarehousingcode;
    @XmlElement(name = "BARCODE")
    protected String barcode;
    @XmlElement(name = "DISPATCHING_NO")
    protected String dispatchingno;
    @XmlElement(name = "ITEM_NO")
    protected String itemno;
    @XmlElement(name = "COMPONENT_NO")
    protected String componentno;
    @XmlElement(name = "STATUS")
    protected String status;
    @XmlElement(name = "FeedType")
    protected String feedType;
    @XmlElement(name = "VENDOR_CODE")
    protected String vendorcode;
    @XmlElement(name = "VENDOR_NAME")
    protected String vendorname;
    @XmlElement(name = "PICKING_ADDRESS")
    protected String pickingaddress;
    @XmlElement(name = "MATERIAL_CODE")
    protected String materialcode;
    @XmlElement(name = "MATERIAL_DESC")
    protected String materialdesc;
    @XmlElement(name = "UNIT")
    protected String unit;
    @XmlElement(name = "QUANTITY")
    protected double quantity;
    @XmlElement(name = "PACKAGE_MODEL")
    protected String packagemodel;
    @XmlElement(name = "PACKAGE_TYPE")
    protected String packagetype;
    @XmlElement(name = "DEL")
    protected String del;
    @XmlElement(name = "PICKING_USER_ID")
    protected String pickinguserid;
    @XmlElement(name = "PICKING_USER_NAME")
    protected String pickingusername;
    @XmlElement(name = "BATCH")
    protected String batch;
    @XmlElement(name = "AUTO_FLAG")
    protected String autoflag;
    @XmlElement(name = "PACKAGE_QTY")
    protected double packageqty;
    @XmlElement(name = "ACTUAL_PRINT_DATE")
    protected String actualprintdate;
    @XmlElement(name = "ACTUAL_HANDOVER_DATE")
    protected String actualhandoverdate;
    @XmlElement(name = "DELETE_REMARK")
    protected String deleteremark;
    @XmlElement(name = "HANDOVER_USER_ID")
    protected String handoveruserid;
    @XmlElement(name = "REQUIREMENT_TYPE")
    protected String requirementtype;
    @XmlElement(name = "COMFIRM_DATE")
    protected String comfirmdate;
    @XmlElement(name = "DOLLYPOS")
    protected String dollypos;
    @XmlElement(name = "CREATE_DATE")
    protected String createdate;
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
     * 获取barcode属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBARCODE() {
        return barcode;
    }

    /**
     * 设置barcode属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBARCODE(String value) {
        this.barcode = value;
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
     * 获取componentno属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCOMPONENTNO() {
        return componentno;
    }

    /**
     * 设置componentno属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCOMPONENTNO(String value) {
        this.componentno = value;
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
     * 获取vendorcode属性的值。
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
     * 设置vendorcode属性的值。
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
     * 获取vendorname属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVENDORNAME() {
        return vendorname;
    }

    /**
     * 设置vendorname属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVENDORNAME(String value) {
        this.vendorname = value;
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
     * 获取packagemodel属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPACKAGEMODEL() {
        return packagemodel;
    }

    /**
     * 设置packagemodel属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPACKAGEMODEL(String value) {
        this.packagemodel = value;
    }

    /**
     * 获取packagetype属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPACKAGETYPE() {
        return packagetype;
    }

    /**
     * 设置packagetype属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPACKAGETYPE(String value) {
        this.packagetype = value;
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
     * 获取pickinguserid属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPICKINGUSERID() {
        return pickinguserid;
    }

    /**
     * 设置pickinguserid属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPICKINGUSERID(String value) {
        this.pickinguserid = value;
    }

    /**
     * 获取pickingusername属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPICKINGUSERNAME() {
        return pickingusername;
    }

    /**
     * 设置pickingusername属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPICKINGUSERNAME(String value) {
        this.pickingusername = value;
    }

    /**
     * 获取batch属性的值。
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
     * 设置batch属性的值。
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
     * 获取autoflag属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAUTOFLAG() {
        return autoflag;
    }

    /**
     * 设置autoflag属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAUTOFLAG(String value) {
        this.autoflag = value;
    }

    /**
     * 获取packageqty属性的值。
     * 
     */
    public double getPACKAGEQTY() {
        return packageqty;
    }

    /**
     * 设置packageqty属性的值。
     * 
     */
    public void setPACKAGEQTY(double value) {
        this.packageqty = value;
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
     * 获取deleteremark属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDELETEREMARK() {
        return deleteremark;
    }

    /**
     * 设置deleteremark属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDELETEREMARK(String value) {
        this.deleteremark = value;
    }

    /**
     * 获取handoveruserid属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHANDOVERUSERID() {
        return handoveruserid;
    }

    /**
     * 设置handoveruserid属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHANDOVERUSERID(String value) {
        this.handoveruserid = value;
    }

    /**
     * 获取requirementtype属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getREQUIREMENTTYPE() {
        return requirementtype;
    }

    /**
     * 设置requirementtype属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setREQUIREMENTTYPE(String value) {
        this.requirementtype = value;
    }

    /**
     * 获取comfirmdate属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCOMFIRMDATE() {
        return comfirmdate;
    }

    /**
     * 设置comfirmdate属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCOMFIRMDATE(String value) {
        this.comfirmdate = value;
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

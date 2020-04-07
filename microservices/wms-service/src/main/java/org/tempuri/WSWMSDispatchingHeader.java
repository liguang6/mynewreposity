
package org.tempuri;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>WS_WMS_Dispatching_Header complex type的 Java 类。
 * 
 * <p>以下模式片段指定包含在此类中的预期内容。
 * 
 * <pre>
 * &lt;complexType name="WS_WMS_Dispatching_Header">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="PickListNo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="PLANT_CODE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="FROM_PLANT_CODE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="DISPATCHING_NO" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="STATUS" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="TYPE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="FeedType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="SORT_TYPE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="S0RT_GROUP" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="SORT_MODEL" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="LAST_LINE_NUMBER" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="JIS_SERIAL_NUMBER" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="WAITING_LOCATION" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="LINE_GATEGORY" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Line" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ELEVATOR_CODE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="WORKING_LOCATION" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="PRINT_DATE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="HANDOVER_DATE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="RECEIVING_DATE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="HEDAN_QUANTITY" type="{http://www.w3.org/2001/XMLSchema}float"/>
 *         &lt;element name="DEL" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="VERIFY" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="CREATE_DATE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="CREATE_USER_ID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="CREATE_USER_NAME" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="UPDATE_DATE" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="UPDATE_USER_ID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ListItem" type="{http://tempuri.org/}ArrayOfWS_WMS_Dispatching_Item" minOccurs="0"/>
 *         &lt;element name="OperateType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
@XmlType(name = "WS_WMS_Dispatching_Header", propOrder = {
    "pickListNo",
    "plantcode",
    "fromplantcode",
    "dispatchingno",
    "status",
    "type",
    "feedType",
    "sorttype",
    "s0RTGROUP",
    "sortmodel",
    "lastlinenumber",
    "jisserialnumber",
    "waitinglocation",
    "linegategory",
    "line",
    "elevatorcode",
    "workinglocation",
    "printdate",
    "handoverdate",
    "receivingdate",
    "hedanquantity",
    "del",
    "verify",
    "createdate",
    "createuserid",
    "createusername",
    "updatedate",
    "updateuserid",
    "listItem",
    "operateType",
    "isExeOperate",
    "returnStatus",
    "returnMessage"
})
public class WSWMSDispatchingHeader {

    @XmlElement(name = "PickListNo")
    protected String pickListNo;
    @XmlElement(name = "PLANT_CODE")
    protected String plantcode;
    @XmlElement(name = "FROM_PLANT_CODE")
    protected String fromplantcode;
    @XmlElement(name = "DISPATCHING_NO")
    protected String dispatchingno;
    @XmlElement(name = "STATUS")
    protected String status;
    @XmlElement(name = "TYPE")
    protected String type;
    @XmlElement(name = "FeedType")
    protected String feedType;
    @XmlElement(name = "SORT_TYPE")
    protected String sorttype;
    @XmlElement(name = "S0RT_GROUP")
    protected String s0RTGROUP;
    @XmlElement(name = "SORT_MODEL")
    protected String sortmodel;
    @XmlElement(name = "LAST_LINE_NUMBER")
    protected String lastlinenumber;
    @XmlElement(name = "JIS_SERIAL_NUMBER")
    protected String jisserialnumber;
    @XmlElement(name = "WAITING_LOCATION")
    protected String waitinglocation;
    @XmlElement(name = "LINE_GATEGORY")
    protected String linegategory;
    @XmlElement(name = "Line")
    protected String line;
    @XmlElement(name = "ELEVATOR_CODE")
    protected String elevatorcode;
    @XmlElement(name = "WORKING_LOCATION")
    protected String workinglocation;
    @XmlElement(name = "PRINT_DATE")
    protected String printdate;
    @XmlElement(name = "HANDOVER_DATE")
    protected String handoverdate;
    @XmlElement(name = "RECEIVING_DATE")
    protected String receivingdate;
    @XmlElement(name = "HEDAN_QUANTITY")
    protected float hedanquantity;
    @XmlElement(name = "DEL")
    protected String del;
    @XmlElement(name = "VERIFY")
    protected int verify;
    @XmlElement(name = "CREATE_DATE")
    protected String createdate;
    @XmlElement(name = "CREATE_USER_ID")
    protected String createuserid;
    @XmlElement(name = "CREATE_USER_NAME")
    protected String createusername;
    @XmlElement(name = "UPDATE_DATE")
    protected String updatedate;
    @XmlElement(name = "UPDATE_USER_ID")
    protected String updateuserid;
    @XmlElement(name = "ListItem")
    protected ArrayOfWSWMSDispatchingItem listItem;
    @XmlElement(name = "OperateType")
    protected String operateType;
    protected int isExeOperate;
    @XmlElement(name = "ReturnStatus")
    protected String returnStatus;
    @XmlElement(name = "ReturnMessage")
    protected String returnMessage;

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
     * 获取sorttype属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSORTTYPE() {
        return sorttype;
    }

    /**
     * 设置sorttype属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSORTTYPE(String value) {
        this.sorttype = value;
    }

    /**
     * 获取s0RTGROUP属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getS0RTGROUP() {
        return s0RTGROUP;
    }

    /**
     * 设置s0RTGROUP属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setS0RTGROUP(String value) {
        this.s0RTGROUP = value;
    }

    /**
     * 获取sortmodel属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSORTMODEL() {
        return sortmodel;
    }

    /**
     * 设置sortmodel属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSORTMODEL(String value) {
        this.sortmodel = value;
    }

    /**
     * 获取lastlinenumber属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLASTLINENUMBER() {
        return lastlinenumber;
    }

    /**
     * 设置lastlinenumber属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLASTLINENUMBER(String value) {
        this.lastlinenumber = value;
    }

    /**
     * 获取jisserialnumber属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getJISSERIALNUMBER() {
        return jisserialnumber;
    }

    /**
     * 设置jisserialnumber属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setJISSERIALNUMBER(String value) {
        this.jisserialnumber = value;
    }

    /**
     * 获取waitinglocation属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getWAITINGLOCATION() {
        return waitinglocation;
    }

    /**
     * 设置waitinglocation属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWAITINGLOCATION(String value) {
        this.waitinglocation = value;
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
     * 获取elevatorcode属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getELEVATORCODE() {
        return elevatorcode;
    }

    /**
     * 设置elevatorcode属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setELEVATORCODE(String value) {
        this.elevatorcode = value;
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
     * 获取receivingdate属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRECEIVINGDATE() {
        return receivingdate;
    }

    /**
     * 设置receivingdate属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRECEIVINGDATE(String value) {
        this.receivingdate = value;
    }

    /**
     * 获取hedanquantity属性的值。
     * 
     */
    public float getHEDANQUANTITY() {
        return hedanquantity;
    }

    /**
     * 设置hedanquantity属性的值。
     * 
     */
    public void setHEDANQUANTITY(float value) {
        this.hedanquantity = value;
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
     * 获取verify属性的值。
     * 
     */
    public int getVERIFY() {
        return verify;
    }

    /**
     * 设置verify属性的值。
     * 
     */
    public void setVERIFY(int value) {
        this.verify = value;
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
     * 获取createuserid属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCREATEUSERID() {
        return createuserid;
    }

    /**
     * 设置createuserid属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCREATEUSERID(String value) {
        this.createuserid = value;
    }

    /**
     * 获取createusername属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCREATEUSERNAME() {
        return createusername;
    }

    /**
     * 设置createusername属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCREATEUSERNAME(String value) {
        this.createusername = value;
    }

    /**
     * 获取updatedate属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUPDATEDATE() {
        return updatedate;
    }

    /**
     * 设置updatedate属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUPDATEDATE(String value) {
        this.updatedate = value;
    }

    /**
     * 获取updateuserid属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUPDATEUSERID() {
        return updateuserid;
    }

    /**
     * 设置updateuserid属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUPDATEUSERID(String value) {
        this.updateuserid = value;
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
     * 获取operateType属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOperateType() {
        return operateType;
    }

    /**
     * 设置operateType属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOperateType(String value) {
        this.operateType = value;
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


package org.tempuri;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>WS_WMS_LogisMvt complex type的 Java 类。
 * 
 * <p>以下模式片段指定包含在此类中的预期内容。
 * 
 * <pre>
 * &lt;complexType name="WS_WMS_LogisMvt">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ListNo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ItemNo" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="Plant" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Stge_Loc" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Stck_Type" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Material" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="MaterialDesc" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Batch" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Vendor" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="VendorName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Entry_Qnt" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="Entry_Uom" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="isSAPAccount" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="Move_Type" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="MoveDirection" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="TmpMoveType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="TmpSAPLoct" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="HandOutEmp" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ReceiveEmp" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Move_Plant" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Move_Stloc" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="OrderID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="WBS_Elem" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Line" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Remark" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Doc_Date" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Pstng_Date" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Header_Txt" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Item_Text" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
@XmlType(name = "WS_WMS_LogisMvt", propOrder = {
    "listNo",
    "itemNo",
    "plant",
    "stgeLoc",
    "stckType",
    "material",
    "materialDesc",
    "batch",
    "vendor",
    "vendorName",
    "entryQnt",
    "entryUom",
    "isSAPAccount",
    "moveType",
    "moveDirection",
    "tmpMoveType",
    "tmpSAPLoct",
    "handOutEmp",
    "receiveEmp",
    "movePlant",
    "moveStloc",
    "orderID",
    "wbsElem",
    "line",
    "remark",
    "docDate",
    "pstngDate",
    "headerTxt",
    "itemText",
    "returnStatus",
    "returnMessage"
})
public class WSWMSLogisMvt {

    @XmlElement(name = "ListNo")
    protected String listNo;
    @XmlElement(name = "ItemNo")
    protected int itemNo;
    @XmlElement(name = "Plant")
    protected String plant;
    @XmlElement(name = "Stge_Loc")
    protected String stgeLoc;
    @XmlElement(name = "Stck_Type")
    protected String stckType;
    @XmlElement(name = "Material")
    protected String material;
    @XmlElement(name = "MaterialDesc")
    protected String materialDesc;
    @XmlElement(name = "Batch")
    protected String batch;
    @XmlElement(name = "Vendor")
    protected String vendor;
    @XmlElement(name = "VendorName")
    protected String vendorName;
    @XmlElement(name = "Entry_Qnt")
    protected double entryQnt;
    @XmlElement(name = "Entry_Uom")
    protected String entryUom;
    protected int isSAPAccount;
    @XmlElement(name = "Move_Type")
    protected String moveType;
    @XmlElement(name = "MoveDirection")
    protected int moveDirection;
    @XmlElement(name = "TmpMoveType")
    protected String tmpMoveType;
    @XmlElement(name = "TmpSAPLoct")
    protected String tmpSAPLoct;
    @XmlElement(name = "HandOutEmp")
    protected String handOutEmp;
    @XmlElement(name = "ReceiveEmp")
    protected String receiveEmp;
    @XmlElement(name = "Move_Plant")
    protected String movePlant;
    @XmlElement(name = "Move_Stloc")
    protected String moveStloc;
    @XmlElement(name = "OrderID")
    protected String orderID;
    @XmlElement(name = "WBS_Elem")
    protected String wbsElem;
    @XmlElement(name = "Line")
    protected String line;
    @XmlElement(name = "Remark")
    protected String remark;
    @XmlElement(name = "Doc_Date")
    protected String docDate;
    @XmlElement(name = "Pstng_Date")
    protected String pstngDate;
    @XmlElement(name = "Header_Txt")
    protected String headerTxt;
    @XmlElement(name = "Item_Text")
    protected String itemText;
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
     * 获取plant属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPlant() {
        return plant;
    }

    /**
     * 设置plant属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPlant(String value) {
        this.plant = value;
    }

    /**
     * 获取stgeLoc属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStgeLoc() {
        return stgeLoc;
    }

    /**
     * 设置stgeLoc属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStgeLoc(String value) {
        this.stgeLoc = value;
    }

    /**
     * 获取stckType属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStckType() {
        return stckType;
    }

    /**
     * 设置stckType属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStckType(String value) {
        this.stckType = value;
    }

    /**
     * 获取material属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMaterial() {
        return material;
    }

    /**
     * 设置material属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMaterial(String value) {
        this.material = value;
    }

    /**
     * 获取materialDesc属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMaterialDesc() {
        return materialDesc;
    }

    /**
     * 设置materialDesc属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMaterialDesc(String value) {
        this.materialDesc = value;
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
     * 获取vendor属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVendor() {
        return vendor;
    }

    /**
     * 设置vendor属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVendor(String value) {
        this.vendor = value;
    }

    /**
     * 获取vendorName属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVendorName() {
        return vendorName;
    }

    /**
     * 设置vendorName属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVendorName(String value) {
        this.vendorName = value;
    }

    /**
     * 获取entryQnt属性的值。
     * 
     */
    public double getEntryQnt() {
        return entryQnt;
    }

    /**
     * 设置entryQnt属性的值。
     * 
     */
    public void setEntryQnt(double value) {
        this.entryQnt = value;
    }

    /**
     * 获取entryUom属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEntryUom() {
        return entryUom;
    }

    /**
     * 设置entryUom属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEntryUom(String value) {
        this.entryUom = value;
    }

    /**
     * 获取isSAPAccount属性的值。
     * 
     */
    public int getIsSAPAccount() {
        return isSAPAccount;
    }

    /**
     * 设置isSAPAccount属性的值。
     * 
     */
    public void setIsSAPAccount(int value) {
        this.isSAPAccount = value;
    }

    /**
     * 获取moveType属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMoveType() {
        return moveType;
    }

    /**
     * 设置moveType属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMoveType(String value) {
        this.moveType = value;
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
     * 获取tmpMoveType属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTmpMoveType() {
        return tmpMoveType;
    }

    /**
     * 设置tmpMoveType属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTmpMoveType(String value) {
        this.tmpMoveType = value;
    }

    /**
     * 获取tmpSAPLoct属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTmpSAPLoct() {
        return tmpSAPLoct;
    }

    /**
     * 设置tmpSAPLoct属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTmpSAPLoct(String value) {
        this.tmpSAPLoct = value;
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
     * 获取movePlant属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMovePlant() {
        return movePlant;
    }

    /**
     * 设置movePlant属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMovePlant(String value) {
        this.movePlant = value;
    }

    /**
     * 获取moveStloc属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMoveStloc() {
        return moveStloc;
    }

    /**
     * 设置moveStloc属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMoveStloc(String value) {
        this.moveStloc = value;
    }

    /**
     * 获取orderID属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOrderID() {
        return orderID;
    }

    /**
     * 设置orderID属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOrderID(String value) {
        this.orderID = value;
    }

    /**
     * 获取wbsElem属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getWBSElem() {
        return wbsElem;
    }

    /**
     * 设置wbsElem属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWBSElem(String value) {
        this.wbsElem = value;
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
     * 获取remark属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRemark() {
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
    public void setRemark(String value) {
        this.remark = value;
    }

    /**
     * 获取docDate属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDocDate() {
        return docDate;
    }

    /**
     * 设置docDate属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDocDate(String value) {
        this.docDate = value;
    }

    /**
     * 获取pstngDate属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPstngDate() {
        return pstngDate;
    }

    /**
     * 设置pstngDate属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPstngDate(String value) {
        this.pstngDate = value;
    }

    /**
     * 获取headerTxt属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHeaderTxt() {
        return headerTxt;
    }

    /**
     * 设置headerTxt属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHeaderTxt(String value) {
        this.headerTxt = value;
    }

    /**
     * 获取itemText属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getItemText() {
        return itemText;
    }

    /**
     * 设置itemText属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setItemText(String value) {
        this.itemText = value;
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

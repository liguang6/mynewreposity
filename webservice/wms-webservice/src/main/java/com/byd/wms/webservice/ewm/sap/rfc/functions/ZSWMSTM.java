
package com.byd.wms.webservice.ewm.sap.rfc.functions;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>ZSWMS_TM complex type�� Java �ࡣ
 * 
 * <p>����ģʽƬ��ָ�������ڴ����е�Ԥ�����ݡ�
 * 
 * <pre>
 * &lt;complexType name="ZSWMS_TM">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="WHMNO" type="{urn:sap-com:document:sap:rfc:functions}char8"/>
 *         &lt;element name="TMIDN" type="{urn:sap-com:document:sap:rfc:functions}char32"/>
 *         &lt;element name="TMIDN_F" type="{urn:sap-com:document:sap:rfc:functions}char200"/>
 *         &lt;element name="MATNR" type="{urn:sap-com:document:sap:rfc:functions}char18"/>
 *         &lt;element name="LIFNR" type="{urn:sap-com:document:sap:rfc:functions}char18"/>
 *         &lt;element name="PONUM" type="{urn:sap-com:document:sap:rfc:functions}char20"/>
 *         &lt;element name="POITM" type="{urn:sap-com:document:sap:rfc:functions}char20"/>
 *         &lt;element name="TMQTY" type="{urn:sap-com:document:sap:rfc:functions}quantum18.3"/>
 *         &lt;element name="MEINS" type="{urn:sap-com:document:sap:rfc:functions}char3"/>
 *         &lt;element name="SKUWEI" type="{urn:sap-com:document:sap:rfc:functions}char8"/>
 *         &lt;element name="PDATE" type="{urn:sap-com:document:sap:rfc:functions}date10"/>
 *         &lt;element name="VALTO" type="{urn:sap-com:document:sap:rfc:functions}date10"/>
 *         &lt;element name="VBTCH" type="{urn:sap-com:document:sap:rfc:functions}char32"/>
 *         &lt;element name="BYDBTH" type="{urn:sap-com:document:sap:rfc:functions}char32"/>
 *         &lt;element name="RMARK" type="{urn:sap-com:document:sap:rfc:functions}char200"/>
 *         &lt;element name="ASNQTY" type="{urn:sap-com:document:sap:rfc:functions}quantum18.3"/>
 *         &lt;element name="STATS" type="{urn:sap-com:document:sap:rfc:functions}char1"/>
 *         &lt;element name="YLZD1" type="{urn:sap-com:document:sap:rfc:functions}char32"/>
 *         &lt;element name="YLZD2" type="{urn:sap-com:document:sap:rfc:functions}char32"/>
 *         &lt;element name="YLZD3" type="{urn:sap-com:document:sap:rfc:functions}char32"/>
 *         &lt;element name="YLZD4" type="{urn:sap-com:document:sap:rfc:functions}char32"/>
 *         &lt;element name="YLZD5" type="{urn:sap-com:document:sap:rfc:functions}char32"/>
 *         &lt;element name="YLZD6" type="{urn:sap-com:document:sap:rfc:functions}char32"/>
 *         &lt;element name="YLZD7" type="{urn:sap-com:document:sap:rfc:functions}char32"/>
 *         &lt;element name="YLZD8" type="{urn:sap-com:document:sap:rfc:functions}char32"/>
 *         &lt;element name="YLZD9" type="{urn:sap-com:document:sap:rfc:functions}char32"/>
 *         &lt;element name="YLZD0" type="{urn:sap-com:document:sap:rfc:functions}char32"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ZSWMS_TM", propOrder = {
    "whmno",
    "tmidn",
    "tmidnf",
    "matnr",
    "lifnr",
    "ponum",
    "poitm",
    "tmqty",
    "meins",
    "skuwei",
    "pdate",
    "valto",
    "vbtch",
    "bydbth",
    "rmark",
    "asnqty",
    "stats",
    "ylzd1",
    "ylzd2",
    "ylzd3",
    "ylzd4",
    "ylzd5",
    "ylzd6",
    "ylzd7",
    "ylzd8",
    "ylzd9",
    "ylzd0"
})
public class ZSWMSTM {

    @XmlElement(name = "WHMNO", required = true)
    protected String whmno;
    @XmlElement(name = "TMIDN", required = true)
    protected String tmidn;
    @XmlElement(name = "TMIDN_F", required = true)
    protected String tmidnf;
    @XmlElement(name = "MATNR", required = true)
    protected String matnr;
    @XmlElement(name = "LIFNR", required = true)
    protected String lifnr;
    @XmlElement(name = "PONUM", required = true)
    protected String ponum;
    @XmlElement(name = "POITM", required = true)
    protected String poitm;
    @XmlElement(name = "TMQTY", required = true)
    protected BigDecimal tmqty;
    @XmlElement(name = "MEINS", required = true)
    protected String meins;
    @XmlElement(name = "SKUWEI", required = true)
    protected String skuwei;
    @XmlElement(name = "PDATE", required = true)
    protected String pdate;
    @XmlElement(name = "VALTO", required = true)
    protected String valto;
    @XmlElement(name = "VBTCH", required = true)
    protected String vbtch;
    @XmlElement(name = "BYDBTH", required = true)
    protected String bydbth;
    @XmlElement(name = "RMARK", required = true)
    protected String rmark;
    @XmlElement(name = "ASNQTY", required = true)
    protected BigDecimal asnqty;
    @XmlElement(name = "STATS", required = true)
    protected String stats;
    @XmlElement(name = "YLZD1", required = true)
    protected String ylzd1;
    @XmlElement(name = "YLZD2", required = true)
    protected String ylzd2;
    @XmlElement(name = "YLZD3", required = true)
    protected String ylzd3;
    @XmlElement(name = "YLZD4", required = true)
    protected String ylzd4;
    @XmlElement(name = "YLZD5", required = true)
    protected String ylzd5;
    @XmlElement(name = "YLZD6", required = true)
    protected String ylzd6;
    @XmlElement(name = "YLZD7", required = true)
    protected String ylzd7;
    @XmlElement(name = "YLZD8", required = true)
    protected String ylzd8;
    @XmlElement(name = "YLZD9", required = true)
    protected String ylzd9;
    @XmlElement(name = "YLZD0", required = true)
    protected String ylzd0;

    /**
     * ��ȡwhmno���Ե�ֵ��
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getWHMNO() {
        return whmno;
    }

    /**
     * ����whmno���Ե�ֵ��
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWHMNO(String value) {
        this.whmno = value;
    }

    /**
     * ��ȡtmidn���Ե�ֵ��
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTMIDN() {
        return tmidn;
    }

    /**
     * ����tmidn���Ե�ֵ��
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTMIDN(String value) {
        this.tmidn = value;
    }

    /**
     * ��ȡtmidnf���Ե�ֵ��
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTMIDNF() {
        return tmidnf;
    }

    /**
     * ����tmidnf���Ե�ֵ��
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTMIDNF(String value) {
        this.tmidnf = value;
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
     * ��ȡlifnr���Ե�ֵ��
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLIFNR() {
        return lifnr;
    }

    /**
     * ����lifnr���Ե�ֵ��
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLIFNR(String value) {
        this.lifnr = value;
    }

    /**
     * ��ȡponum���Ե�ֵ��
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPONUM() {
        return ponum;
    }

    /**
     * ����ponum���Ե�ֵ��
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPONUM(String value) {
        this.ponum = value;
    }

    /**
     * ��ȡpoitm���Ե�ֵ��
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPOITM() {
        return poitm;
    }

    /**
     * ����poitm���Ե�ֵ��
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPOITM(String value) {
        this.poitm = value;
    }

    /**
     * ��ȡtmqty���Ե�ֵ��
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getTMQTY() {
        return tmqty;
    }

    /**
     * ����tmqty���Ե�ֵ��
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setTMQTY(BigDecimal value) {
        this.tmqty = value;
    }

    /**
     * ��ȡmeins���Ե�ֵ��
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMEINS() {
        return meins;
    }

    /**
     * ����meins���Ե�ֵ��
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMEINS(String value) {
        this.meins = value;
    }

    /**
     * ��ȡskuwei���Ե�ֵ��
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSKUWEI() {
        return skuwei;
    }

    /**
     * ����skuwei���Ե�ֵ��
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSKUWEI(String value) {
        this.skuwei = value;
    }

    /**
     * ��ȡpdate���Ե�ֵ��
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPDATE() {
        return pdate;
    }

    /**
     * ����pdate���Ե�ֵ��
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPDATE(String value) {
        this.pdate = value;
    }

    /**
     * ��ȡvalto���Ե�ֵ��
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVALTO() {
        return valto;
    }

    /**
     * ����valto���Ե�ֵ��
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVALTO(String value) {
        this.valto = value;
    }

    /**
     * ��ȡvbtch���Ե�ֵ��
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVBTCH() {
        return vbtch;
    }

    /**
     * ����vbtch���Ե�ֵ��
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVBTCH(String value) {
        this.vbtch = value;
    }

    /**
     * ��ȡbydbth���Ե�ֵ��
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBYDBTH() {
        return bydbth;
    }

    /**
     * ����bydbth���Ե�ֵ��
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBYDBTH(String value) {
        this.bydbth = value;
    }

    /**
     * ��ȡrmark���Ե�ֵ��
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRMARK() {
        return rmark;
    }

    /**
     * ����rmark���Ե�ֵ��
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRMARK(String value) {
        this.rmark = value;
    }

    /**
     * ��ȡasnqty���Ե�ֵ��
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getASNQTY() {
        return asnqty;
    }

    /**
     * ����asnqty���Ե�ֵ��
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setASNQTY(BigDecimal value) {
        this.asnqty = value;
    }

    /**
     * ��ȡstats���Ե�ֵ��
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSTATS() {
        return stats;
    }

    /**
     * ����stats���Ե�ֵ��
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSTATS(String value) {
        this.stats = value;
    }

    /**
     * ��ȡylzd1���Ե�ֵ��
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getYLZD1() {
        return ylzd1;
    }

    /**
     * ����ylzd1���Ե�ֵ��
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setYLZD1(String value) {
        this.ylzd1 = value;
    }

    /**
     * ��ȡylzd2���Ե�ֵ��
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getYLZD2() {
        return ylzd2;
    }

    /**
     * ����ylzd2���Ե�ֵ��
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setYLZD2(String value) {
        this.ylzd2 = value;
    }

    /**
     * ��ȡylzd3���Ե�ֵ��
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getYLZD3() {
        return ylzd3;
    }

    /**
     * ����ylzd3���Ե�ֵ��
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setYLZD3(String value) {
        this.ylzd3 = value;
    }

    /**
     * ��ȡylzd4���Ե�ֵ��
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getYLZD4() {
        return ylzd4;
    }

    /**
     * ����ylzd4���Ե�ֵ��
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setYLZD4(String value) {
        this.ylzd4 = value;
    }

    /**
     * ��ȡylzd5���Ե�ֵ��
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getYLZD5() {
        return ylzd5;
    }

    /**
     * ����ylzd5���Ե�ֵ��
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setYLZD5(String value) {
        this.ylzd5 = value;
    }

    /**
     * ��ȡylzd6���Ե�ֵ��
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getYLZD6() {
        return ylzd6;
    }

    /**
     * ����ylzd6���Ե�ֵ��
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setYLZD6(String value) {
        this.ylzd6 = value;
    }

    /**
     * ��ȡylzd7���Ե�ֵ��
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getYLZD7() {
        return ylzd7;
    }

    /**
     * ����ylzd7���Ե�ֵ��
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setYLZD7(String value) {
        this.ylzd7 = value;
    }

    /**
     * ��ȡylzd8���Ե�ֵ��
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getYLZD8() {
        return ylzd8;
    }

    /**
     * ����ylzd8���Ե�ֵ��
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setYLZD8(String value) {
        this.ylzd8 = value;
    }

    /**
     * ��ȡylzd9���Ե�ֵ��
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getYLZD9() {
        return ylzd9;
    }

    /**
     * ����ylzd9���Ե�ֵ��
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setYLZD9(String value) {
        this.ylzd9 = value;
    }

    /**
     * ��ȡylzd0���Ե�ֵ��
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getYLZD0() {
        return ylzd0;
    }

    /**
     * ����ylzd0���Ե�ֵ��
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setYLZD0(String value) {
        this.ylzd0 = value;
    }

}

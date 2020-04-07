
package com.byd.wms.webservice.ewm.sap.rfc.functions;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>anonymous complex type�� Java �ࡣ
 * 
 * <p>����ģʽƬ��ָ�������ڴ����е�Ԥ�����ݡ�
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="E_MESSAGE" type="{urn:sap-com:document:sap:rfc:functions}char80"/>
 *         &lt;element name="IT_TM" type="{urn:sap-com:document:sap:rfc:functions}ZTWMS_TM"/>
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
    "emessage",
    "ittm"
})
@XmlRootElement(name = "ZRFC_WMS_003Response")
public class ZRFCWMS003Response {

    @XmlElement(name = "E_MESSAGE", required = true)
    protected String emessage;
    @XmlElement(name = "IT_TM", required = true)
    protected ZTWMSTM ittm;

    /**
     * ��ȡemessage���Ե�ֵ��
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEMESSAGE() {
        return emessage;
    }

    /**
     * ����emessage���Ե�ֵ��
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEMESSAGE(String value) {
        this.emessage = value;
    }

    /**
     * ��ȡittm���Ե�ֵ��
     * 
     * @return
     *     possible object is
     *     {@link ZTWMSTM }
     *     
     */
    public ZTWMSTM getITTM() {
        return ittm;
    }

    /**
     * ����ittm���Ե�ֵ��
     * 
     * @param value
     *     allowed object is
     *     {@link ZTWMSTM }
     *     
     */
    public void setITTM(ZTWMSTM value) {
        this.ittm = value;
    }

}

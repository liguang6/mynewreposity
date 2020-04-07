
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
    "ittm"
})
@XmlRootElement(name = "ZRFC_WMS_003")
public class ZRFCWMS003_Type {

    @XmlElement(name = "IT_TM", required = true)
    protected ZTWMSTM ittm;

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

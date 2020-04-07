
package org.tempuri;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>ArrayOfWS_WMS_LogisMvt complex type的 Java 类。
 * 
 * <p>以下模式片段指定包含在此类中的预期内容。
 * 
 * <pre>
 * &lt;complexType name="ArrayOfWS_WMS_LogisMvt">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="WS_WMS_LogisMvt" type="{http://tempuri.org/}WS_WMS_LogisMvt" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfWS_WMS_LogisMvt", propOrder = {
    "wswmsLogisMvt"
})
public class ArrayOfWSWMSLogisMvt {

    @XmlElement(name = "WS_WMS_LogisMvt", nillable = true)
    protected List<WSWMSLogisMvt> wswmsLogisMvt;

    /**
     * Gets the value of the wswmsLogisMvt property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the wswmsLogisMvt property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getWSWMSLogisMvt().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link WSWMSLogisMvt }
     * 
     * 
     */
    public List<WSWMSLogisMvt> getWSWMSLogisMvt() {
        if (wswmsLogisMvt == null) {
            wswmsLogisMvt = new ArrayList<WSWMSLogisMvt>();
        }
        return this.wswmsLogisMvt;
    }

}

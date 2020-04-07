
package org.tempuri;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>ArrayOfWS_WMS_Dispatching_Header complex type的 Java 类。
 * 
 * <p>以下模式片段指定包含在此类中的预期内容。
 * 
 * <pre>
 * &lt;complexType name="ArrayOfWS_WMS_Dispatching_Header">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="WS_WMS_Dispatching_Header" type="{http://tempuri.org/}WS_WMS_Dispatching_Header" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfWS_WMS_Dispatching_Header", propOrder = {
    "wswmsDispatchingHeader"
})
public class ArrayOfWSWMSDispatchingHeader {

    @XmlElement(name = "WS_WMS_Dispatching_Header", nillable = true)
    protected List<WSWMSDispatchingHeader> wswmsDispatchingHeader;

    /**
     * Gets the value of the wswmsDispatchingHeader property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the wswmsDispatchingHeader property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getWSWMSDispatchingHeader().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link WSWMSDispatchingHeader }
     * 
     * 
     */
    public List<WSWMSDispatchingHeader> getWSWMSDispatchingHeader() {
        if (wswmsDispatchingHeader == null) {
            wswmsDispatchingHeader = new ArrayList<WSWMSDispatchingHeader>();
        }
        return this.wswmsDispatchingHeader;
    }

}

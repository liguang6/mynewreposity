
package org.tempuri;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>ArrayOfWS_WMS_Dispatching_Component complex type的 Java 类。
 * 
 * <p>以下模式片段指定包含在此类中的预期内容。
 * 
 * <pre>
 * &lt;complexType name="ArrayOfWS_WMS_Dispatching_Component">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="WS_WMS_Dispatching_Component" type="{http://tempuri.org/}WS_WMS_Dispatching_Component" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfWS_WMS_Dispatching_Component", propOrder = {
    "wswmsDispatchingComponent"
})
public class ArrayOfWSWMSDispatchingComponent {

    @XmlElement(name = "WS_WMS_Dispatching_Component", nillable = true)
    protected List<WSWMSDispatchingComponent> wswmsDispatchingComponent;

    /**
     * Gets the value of the wswmsDispatchingComponent property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the wswmsDispatchingComponent property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getWSWMSDispatchingComponent().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link WSWMSDispatchingComponent }
     * 
     * 
     */
    public List<WSWMSDispatchingComponent> getWSWMSDispatchingComponent() {
        if (wswmsDispatchingComponent == null) {
            wswmsDispatchingComponent = new ArrayList<WSWMSDispatchingComponent>();
        }
        return this.wswmsDispatchingComponent;
    }

}


package org.tempuri;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>ArrayOfWMSDispatchingComponent complex type的 Java 类。
 * 
 * <p>以下模式片段指定包含在此类中的预期内容。
 * 
 * <pre>
 * &lt;complexType name="ArrayOfWMSDispatchingComponent">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="WMSDispatchingComponent" type="{http://tempuri.org/}WMSDispatchingComponent" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfWMSDispatchingComponent", propOrder = {
    "wmsDispatchingComponent"
})
public class ArrayOfWMSDispatchingComponent {

    @XmlElement(name = "WMSDispatchingComponent", nillable = true)
    protected List<WMSDispatchingComponent> wmsDispatchingComponent;

    /**
     * Gets the value of the wmsDispatchingComponent property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the wmsDispatchingComponent property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getWMSDispatchingComponent().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link WMSDispatchingComponent }
     * 
     * 
     */
    public List<WMSDispatchingComponent> getWMSDispatchingComponent() {
        if (wmsDispatchingComponent == null) {
            wmsDispatchingComponent = new ArrayList<WMSDispatchingComponent>();
        }
        return this.wmsDispatchingComponent;
    }

}

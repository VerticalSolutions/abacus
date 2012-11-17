/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package count.web.managed;

import count.domain.Contact;
import count.service.ContactFacade;
import count.web.utils.JSFUtils;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import org.primefaces.context.RequestContext;

/**
 *
 * @author Rommel
 */
public class ContactEditBean {
    private Contact contact;
    @EJB
    private ContactFacade service;

    public ContactEditBean() {
    }

    public Contact getContact() {
        if (contact == null){
            Integer contactId = (Integer)JSFUtils.getFlash().get("contactId");
            contact = service.find(contactId);
        }
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }
    
    public String save() {
        service.edit(contact);
        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Contact has been saved.");
        FacesContext.getCurrentInstance().addMessage(null, msg);
        return "list";
    }
}

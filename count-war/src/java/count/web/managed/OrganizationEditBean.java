/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package count.web.managed;

import count.domain.Organization;
import count.service.OrganizationFacade;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

/**
 *
 * @author Rommel
 */
public class OrganizationEditBean {
    private Organization organization;
    @EJB
    private OrganizationFacade service;
    /**
     * Creates a new instance of OrganizationEditBean
     */
    public OrganizationEditBean() {
    }

    public Organization getOrganization() {
        if(organization == null){
            organization = service.find(1);
        }
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }
    
    public String save(){
        service.edit(organization);
        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Organization has been updated.");
        FacesContext.getCurrentInstance().addMessage(null, msg);
        return null;
    }
    
    
}

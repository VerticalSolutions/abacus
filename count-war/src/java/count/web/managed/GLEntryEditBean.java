/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package count.web.managed;

import count.domain.GLEntry;
import count.service.GLEntryFacade;
import count.web.utils.JSFUtils;
import javax.ejb.EJB;
import javax.faces.event.ActionEvent;
import org.primefaces.context.RequestContext;

/**
 *
 * @author Rommel
 */
public class GLEntryEditBean {
    @EJB
    private GLEntryFacade service;
    private GLEntry entry;
    /**
     * Creates a new instance of GLEntryEditBean
     */
    public GLEntryEditBean() {
    }

    public GLEntry getEntry() {
        if (entry == null){
            Integer glEntryId = (Integer)JSFUtils.getFlash().get("glEntryId");
            entry = service.find(glEntryId);
        }
        return entry;
    }

    public void setEntry(GLEntry entry) {
        this.entry = entry;
    }
    
    public void save(ActionEvent e){
        service.edit(entry);
    }
    
    public void updateSummary(){
        RequestContext context = RequestContext.getCurrentInstance();
        context.update("form:t1:sumCredit");
        context.update("form:t1:sumDebit");
    }
    
    
}

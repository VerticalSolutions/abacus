/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package count.service;

import count.domain.Contact;
import count.domain.ContactGroup;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Rommel
 */
@Stateless
public class ContactFacade extends AbstractFacade<Contact> {
    @PersistenceContext(unitName = "count_countapp-ejb_ejb_1.0-SNAPSHOTPU")
    private EntityManager em;
    @EJB
    private ContactGroupFacade contactGroupFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ContactFacade() {
        super(Contact.class);
    }
    
}

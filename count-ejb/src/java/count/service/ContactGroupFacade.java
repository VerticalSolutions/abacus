/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package count.service;

import count.domain.ContactGroup;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author Rommel
 */
@Stateless
public class ContactGroupFacade extends AbstractFacade<ContactGroup> {
    @PersistenceContext(unitName = "count_countapp-ejb_ejb_1.0-SNAPSHOTPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ContactGroupFacade() {
        super(ContactGroup.class);
    }

    @Override
    public void remove(ContactGroup group) {
        super.remove(group);
        String qryStr = "update contacts set custom_groups = replace(custom_groups,concat('[', ? ,'];'),'')";
        Query query = em.createNativeQuery(qryStr);
        query.setParameter(1, group.getName());
        query.executeUpdate();
    }
    
    
    
}

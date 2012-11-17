/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package count.service;

import count.domain.Organization;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Rommel
 */
@Stateless
public class OrganizationFacade extends AbstractFacade<Organization> {
    @PersistenceContext(unitName = "count_countapp-ejb_ejb_1.0-SNAPSHOTPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public OrganizationFacade() {
        super(Organization.class);
    }
    
}

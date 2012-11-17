/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package count.service;

import count.domain.GLAccount;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author Rommel
 */
@Stateless
public class GLAccountFacade extends AbstractFacade<GLAccount> {
    @PersistenceContext(unitName = "count_countapp-ejb_ejb_1.0-SNAPSHOTPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public GLAccountFacade() {
        super(GLAccount.class);
    }
    
    public List<GLAccount> findByType(String type){
        Query query = em.createNamedQuery("GLAccount.findByType");
        query.setParameter("type", type);
        return query.getResultList();
    }
}

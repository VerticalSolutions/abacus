/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package count.service;

import count.domain.GLEntry;
import java.util.Date;
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
public class GLEntryFacade extends AbstractFacade<GLEntry> {
    @PersistenceContext(unitName = "count_countapp-ejb_ejb_1.0-SNAPSHOTPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public GLEntryFacade() {
        super(GLEntry.class);
    }

    @Override
    public void edit(GLEntry entity) {
        entity.sync();
        super.edit(entity);
    }

    public List getDailyAccountBalanceByDateRange(int accountId, Date startDate, Date endDate) {
        StringBuilder builder = new StringBuilder();
        builder.append("SELECT datefield, IFNULL(AMOUNT,0) AS AMOUNT, @RUN := @RUN + IFNULL(AMOUNT,0) AS RUNNING_TOTAL");
        builder.append(" FROM (SELECT @RUN := 0) vars, calendar c LEFT JOIN (");
        builder.append(" SELECT ? as TRANS_DATE, sum(AMOUNT) AS AMOUNT");//1
        builder.append(" FROM gl_trans_detail d, gl_trans_header h");
        builder.append(" WHERE d.gl_trans_header_id = h.gl_trans_header_id");
        builder.append(" AND d.gl_account_id = ?");//2
        builder.append(" AND TRANS_DATE <= ?");//3
        builder.append(" UNION ALL");
        builder.append(" SELECT TRANS_DATE, SUM(AMOUNT) AS AMOUNT");
        builder.append(" FROM gl_trans_detail d, gl_trans_header h");
        builder.append(" WHERE d.gl_trans_header_id = h.gl_trans_header_id");
        builder.append(" AND d.gl_account_id = ?");//4
        builder.append(" AND TRANS_DATE > ?");//5
        builder.append(" AND TRANS_DATE <= ?");//6
        builder.append(" GROUP BY TRANS_DATE) t ON (t.trans_date= c.datefield)");
        builder.append(" WHERE c.datefield between ? AND ?");//7//8
        builder.append(" ORDER BY c.datefield");
        Query query = em.createNativeQuery(builder.toString());
        java.sql.Date startDateParam = new java.sql.Date(startDate.getTime());
        java.sql.Date endDateParam = new java.sql.Date(endDate.getTime());
        query.setParameter(1, startDateParam);
        query.setParameter(2, accountId);
        query.setParameter(3, startDateParam);
        query.setParameter(4, accountId);
        query.setParameter(5, startDateParam);
        query.setParameter(6, endDateParam);
        query.setParameter(7, startDateParam);
        query.setParameter(8, endDateParam);
        List result = query.getResultList();
        System.out.println("result " + result);
        return result;
    }
    
    
    
}

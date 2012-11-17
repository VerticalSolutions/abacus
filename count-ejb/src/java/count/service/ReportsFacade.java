/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package count.service;

import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author Rommel
 */
@Stateless
@LocalBean
public class ReportsFacade {

    @PersistenceContext(unitName = "count_countapp-ejb_ejb_1.0-SNAPSHOTPU")
    private EntityManager em;

    public List<Object[]> getTrialBalanceData(Date startDate, Date endDate) {
        StringBuilder b = new StringBuilder();
        b.append("select acct_code, IF(acct_code IS NULL, 'TOTAL', acct_name) as acct_name, sum(BeginningBalance) as BeginningBalance,");
        b.append(" sum(DEBIT) as DEBIT, sum(CREDIT) as CREDIT, sum(BeginningBalance)+ sum(DEBIT) - sum(CREDIT) as EndingBalance");
        b.append(" from(select acct_code, acct_name, acct_type, sum(amount) as BeginningBalance, 0 as DEBIT, 0 as CREDIT");
        b.append(" from gl_trans_detail d left join gl_accounts a on (d.gl_account_id=a.gl_acct_id)");
        b.append(" inner join gl_trans_header h on (d.gl_trans_header_id=h.gl_trans_header_id)");
        b.append(" where reporting_date < ? ");//1 startDate
        b.append(" group by acct_code, acct_name, acct_type");
        b.append(" union all");
        b.append(" select acct_code, acct_name, acct_type, 0 as BeginningBalance,");
        b.append(" sum(if(amount>0,amount,0)) as DEBIT,");
        b.append(" sum(if(amount<0,abs(amount),0)) as CREDIT");
        b.append(" from gl_trans_detail d left join gl_accounts a on (d.gl_account_id=a.gl_acct_id)");
        b.append(" inner join gl_trans_header h on (d.gl_trans_header_id=h.gl_trans_header_id)");
        b.append(" where reporting_date between ? and ?");//2 3
        b.append(" group by acct_code, acct_name, acct_type) tmp");
        b.append(" GROUP BY acct_code WITH ROLLUP");
        Query query = em.createNativeQuery(b.toString());
        query.setParameter(1, startDate);
        query.setParameter(2, startDate);
        query.setParameter(3, endDate);
        return query.getResultList();
    }
}

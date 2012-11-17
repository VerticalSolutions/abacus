/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package count.web.managed;

import count.service.GLEntryFacade;
import count.web.utils.JSFUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import org.primefaces.event.TabChangeEvent;

/**
 *
 * @author Rommel
 */
public class GeneralLedgerTabsBean {

    private List<String[]> tabs;
    private Map<String, GLEntriesSearchBean> map;
    @EJB
    private GLEntryFacade service;
        
    public List<String[]> getTabs() {
        if (tabs == null) {
            tabs = new ArrayList<String[]>();
            tabs.add(new String[]{"All", "All"});
            tabs.add(new String[]{"Invoices", "Sales"});
            tabs.add(new String[]{"Cash Receipts/Disbursements", "Cash"});
            tabs.add(new String[]{"Manual Journal", "Manual"});
            tabs.add(new String[]{"Others", "Others"});
            
            map = new HashMap<String, GLEntriesSearchBean>();
            map.put("All", new GLEntriesSearchBean(service, null));
            map.put("Invoices", new GLEntriesSearchBean(service, new Object[]{"equals", "transType", "INV"}));
            map.put("Cash Receipts/Disbursements", new GLEntriesSearchBean(service, new Object[]{"equals", "transType", "PAY"}));
            map.put("Manual Journal", new GLEntriesSearchBean(service, new Object[]{"equals", "transType", "MJ"}));
            map.put("Others", new GLEntriesSearchBean(service, new Object[]{"not in", "transType", "INV","PAY","MJ"}));
            
        }
        return tabs;
    }

    public Map<String, GLEntriesSearchBean> getMap() {
        return map;
    }
    
    public void onTabChange(TabChangeEvent event) {
        String[] activeTab = (String[]) event.getData();
        setActiveIndex(getIndex(activeTab[0]));
    }
    
    private int getIndex(String name) {
        for (int i = 0; i < tabs.size(); i++) {
            if (name.equals(tabs.get(i)[0])) {
                return i;
            }
        }
        return 0;
    }
    
    public int getActiveIndex() {
        Integer index = (Integer) JSFUtils.getFromSession("entriesTabIndex");
        if (index == null) {
            index = 0;
        }
        return index;
    }

    public void setActiveIndex(int activeIndex) {
        JSFUtils.storeOnSession("entriesTabIndex", activeIndex);
    }

    
}

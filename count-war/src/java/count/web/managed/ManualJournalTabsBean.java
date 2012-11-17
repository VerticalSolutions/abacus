/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package count.web.managed;

import count.service.GLEntryFacade;
import count.web.utils.JSFUtils;
import java.util.*;
import javax.ejb.EJB;
import org.primefaces.event.TabChangeEvent;

/**
 *
 * @author Rommel
 */
public class ManualJournalTabsBean {

    private List<String[]> tabs;
    private Map<String, GLEntriesSearchBean> map;
    @EJB
    private GLEntryFacade service;
    
    
    
    public ManualJournalTabsBean() {
    }
    
    public List<String[]> getTabs() {
        if (tabs == null) {
            tabs = new ArrayList<String[]>();
            tabs.add(new String[]{"All", "All"});
            tabs.add(new String[]{"Drafts", "Drafts"});
            tabs.add(new String[]{"Posted", "Posted"});
            tabs.add(new String[]{"Voided", "Voided"});
            
            map = new HashMap<String, GLEntriesSearchBean>();
            map.put("All", new GLEntriesSearchBean(service, null));
            map.put("Drafts", new GLEntriesSearchBean(service, new Object[]{"equals", "status", "D"}));
            map.put("Posted", new GLEntriesSearchBean(service, new Object[]{"equals", "status", "P"}));
            map.put("Voided", new GLEntriesSearchBean(service, new Object[]{"equals", "status", "V"}));
            
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

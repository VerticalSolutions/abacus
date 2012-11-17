/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package count.web.managed;

import count.domain.Contact;
import count.domain.ContactGroup;
import count.service.ContactFacade;
import count.service.ContactGroupFacade;
import count.web.utils.JSFUtils;
import java.io.Serializable;
import java.util.*;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.event.ActionEvent;
import org.primefaces.event.TabChangeEvent;

/**
 *
 * @author Rommel
 */
public class ContactsTabBean implements Serializable {

    private List<String[]> tabs;
    private Map<String, ContactsSearchBean> map;
    @EJB
    private ContactFacade service;
    @EJB
    private ContactGroupFacade contactGroupFacade;
    private ContactGroup selectedGroup;
    private String newGroup;
    private ContactGroupConverter contactGroupConverter;

    /**
     * Creates a new instance of ContactsTabBean
     */
    public ContactsTabBean() {
    }

    public List<String[]> getTabs() {
        if (tabs == null) {
            tabs = new ArrayList<String[]>();
            tabs.add(new String[]{"All", "All"});

            tabs.add(new String[]{"Customers", "Customers"});
            tabs.add(new String[]{"Suppliers", "Suppliers"});
            tabs.add(new String[]{"Employees", "Employees"});
            List<ContactGroup> groups = getContactGroups();
            map = new LinkedHashMap<String, ContactsSearchBean>();
            map.put("All", new ContactsSearchBean(service, null));
            map.put("Customers", new ContactsSearchBean(service, new Object[]{"equals", "customer", true}));
            map.put("Suppliers", new ContactsSearchBean(service, new Object[]{"equals", "supplier", true}));
            map.put("Employees", new ContactsSearchBean(service, new Object[]{"equals", "employee", true}));
            for (ContactGroup group : groups) {
                String name = group.getName();
                String groupKey = "[" + name + "]";
                String tabId = replaceWhiteSpace(name);
                System.out.println(tabId);
                tabs.add(new String[]{name, tabId});
                map.put(name, new ContactsSearchBean(service, new Object[]{"contains", "customGroups", groupKey}));
            } 
        }
        return tabs;
    }

    public Map<String, ContactsSearchBean> getContactSearchBean() {

        return map;
    }

    public void onTabChange(TabChangeEvent event) {
        String[] activeTab = (String[]) event.getData();
        setActiveIndex(getIndex(activeTab[0]));
    }

    public List<ContactGroup> getContactGroups() {
        return contactGroupFacade.findAll();
    }
    
    public void deleteActiveGroup(ActionEvent e){
        String[] activeTab = tabs.get(getActiveIndex());
        ContactGroup target = new ContactGroup(activeTab[0]);
        contactGroupFacade.remove(target);
        int index = getIndex(target.getName());
        tabs.remove(index);
        setActiveIndex(0);       
    }

    public void showAddToGroupDialog(ActionEvent e) {
        String[] activeTab = tabs.get(getActiveIndex());
        Contact[] selection = map.get(activeTab[0]).getSelection();
        JSFUtils.addCallbackParam("showAddToGroupDialog", selection.length > 0);
        if (selection.length == 0) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "No Contacts Selected", "Please select contacts.");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }

    }

    private String replaceWhiteSpace(String input) {
        return input.replaceAll("\\s", "_");
    }

    public void addToGroup(ActionEvent e) {
        ContactGroup target = getTargetGroup();
        if (target == null) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Must specify a target group.");
            FacesContext.getCurrentInstance().addMessage("dialog1", msg);
            JSFUtils.addCallbackParam("success", false);
            return;
        }
        String[] activeTab = tabs.get(getActiveIndex());
        Contact[] selection = map.get(activeTab[0]).getSelection();
        for (Contact contact : selection) {
            contact.addToGroup(target);
            service.edit(contact);
        }
        int index = getIndex(target.getName());
        setActiveIndex(index);
        setNewGroup("");
        JSFUtils.addCallbackParam("success", true);

    }

    private int getIndex(String name) {
        for (int i = 0; i < tabs.size(); i++) {
            if (name.equals(tabs.get(i)[0])) {
                return i;
            }
        }
        return 0;
    }

    public ContactGroup getTargetGroup() {
        if (newGroup != null && !"".equals(newGroup)) {
            ContactGroup target = new ContactGroup(newGroup);
            contactGroupFacade.create(target);
            String tabId = replaceWhiteSpace(newGroup);
            tabs.add(new String[]{newGroup, tabId});
            String groupKey = "[" + newGroup + "]";
            map.put(newGroup, new ContactsSearchBean(service, new Object[]{"contains", "customGroups", groupKey}));
            return target;
            
        } else if (selectedGroup != null) {
            return selectedGroup;
        } else {
            return null;
        }
    }

    public void moveToGroup(ActionEvent e) {
        ContactGroup target = getTargetGroup();
        if (target == null) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Must specify a target group.");
            FacesContext.getCurrentInstance().addMessage("dialog2", msg);
            JSFUtils.addCallbackParam("success", false);
            return;
        }
        String[] activeTab = tabs.get(getActiveIndex());
        ContactGroup fromGroup = new ContactGroup(activeTab[0]);
        Contact[] selection = map.get(activeTab[0]).getSelection();
        for (Contact contact : selection) {
            contact.move(fromGroup, target);
            service.edit(contact);
        }
        int index = getIndex(target.getName());
        setActiveIndex(index);
        setNewGroup("");
        JSFUtils.addCallbackParam("success", true);

    }

    public int getActiveIndex() {
        Integer index = (Integer) JSFUtils.getFromSession("contactsTabIndex");
        if (index == null) {
            index = 0;
        }
        return index;
    }

    public void setActiveIndex(int activeIndex) {
        JSFUtils.storeOnSession("contactsTabIndex", activeIndex);
    }

    public String getNewGroup() {
        return newGroup;
    }

    public void setNewGroup(String newGroup) {
        this.newGroup = newGroup;
    }

    public ContactGroup getSelectedGroup() {
        return selectedGroup;
    }

    public void setSelectedGroup(ContactGroup selectedGroup) {
        this.selectedGroup = selectedGroup;
    }

    public ContactGroupConverter getContactGroupConverter() {
        if (contactGroupConverter == null) {
            contactGroupConverter = new ContactGroupConverter();
        }
        return contactGroupConverter;
    }

    public class ContactGroupConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext context, UIComponent component, String value) {
            if (value != null && !"".equals(value)) {
                return contactGroupFacade.find(value);
            }
            return value;
        }

        @Override
        public String getAsString(FacesContext context, UIComponent component, Object value) {
            if (value instanceof ContactGroup) {
                return ((ContactGroup) value).getName();
            }
            return value.toString();
        }
    }
}

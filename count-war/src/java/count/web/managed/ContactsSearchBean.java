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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import org.eclipse.persistence.config.QueryHints;
import org.eclipse.persistence.queries.FetchGroup;
import org.primefaces.context.RequestContext;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

/**
 *
 * @author Rommel
 */
public class ContactsSearchBean {

    @EJB
    private ContactFacade service;
    private Map<String, String> columnCriteria;
    private Object[] additionalFilter;
    private LazyDataModel<Contact> lazyDataModel;
    private Contact[] selection;

    /**
     * Creates a new instance of ContactsSearchBean
     */
    public ContactsSearchBean() {
        this(null, null);
    }

    public ContactsSearchBean(ContactFacade service, Object[] filter) {
        this.additionalFilter = filter;
        this.service = service;
        columnCriteria = new HashMap<String, String>();
        columnCriteria.put("name", "contains");
        columnCriteria.put("email", "contains");
        columnCriteria.put("telephone", "contains");
        lazyDataModel = new ContactLazyDataModel();
    }

    public LazyDataModel<Contact> getLazyDataModel() {
        return lazyDataModel;
    }

    public Contact[] getSelection() {
        return selection;
    }

    public void setSelection(Contact[] selection) {
        this.selection = selection;
    }

    public class ContactLazyDataModel extends LazyDataModel<Contact> {

        @Override
        public List<Contact> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters) {
            Object[] rootPredicate = JSFUtils.buildPredicate(columnCriteria, filters);
            if (additionalFilter != null) {
                rootPredicate = new Object[]{"AND", rootPredicate, additionalFilter};
            }
            List<Object[]> hints = new ArrayList<Object[]>();
            FetchGroup fetchGroup = new FetchGroup();
            for (Map.Entry<String, String> entry : columnCriteria.entrySet()) {
                fetchGroup.addAttribute(entry.getKey());
            }
            hints.add(new Object[]{QueryHints.FETCH_GROUP, fetchGroup});

            List<Contact> result = service.find(first, pageSize, sortField, sortOrder.toString(), rootPredicate, hints);
            int projection = service.count(rootPredicate);
            this.setRowCount(projection);
            return result;
        }

        @Override
        public Contact getRowData(String rowKey) {
            return service.find(Integer.valueOf(rowKey));
        }

        @Override
        public Object getRowKey(Contact contact) {
            return contact.getContactId();
        }

        @Override
        public void setRowIndex(final int rowIndex) {
            if (rowIndex == -1 || getPageSize() == 0) {
                super.setRowIndex(-1);
            } else {
                super.setRowIndex(rowIndex % getPageSize());
            }
        }
    }
}

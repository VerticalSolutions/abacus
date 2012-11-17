/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package count.web.managed;

import count.domain.Contact;
import count.domain.GLEntry;
import count.service.GLEntryFacade;
import count.web.utils.JSFUtils;
import java.util.*;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import org.eclipse.persistence.config.QueryHints;
import org.eclipse.persistence.queries.FetchGroup;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

/**
 *
 * @author Rommel
 */
public class GLEntriesSearchBean {

    @EJB
    private GLEntryFacade service;
    private Map<String, String> columnCriteria;
    private Object[] additionalFilter;
    private LazyDataModel<GLEntry> lazyDataModel;
    private GLEntry[] selection;
    private String description;
    private Date startDate;
    private Date endDate;

    public GLEntriesSearchBean() {
    }

    public GLEntriesSearchBean(GLEntryFacade service, Object[] additionalFilter) {
        this.service = service;
        this.additionalFilter = additionalFilter;
        columnCriteria = new HashMap<String, String>();
        columnCriteria.put("status", null);
        columnCriteria.put("transType", null);
        columnCriteria.put("description", "contains");
        columnCriteria.put("myReference", "contains");
        columnCriteria.put("transDate", null);
        columnCriteria.put("totalDebit", null);
        columnCriteria.put("totalCredit", null);
    }

    public GLEntry[] getSelection() {
        return selection;
    }

    public void setSelection(GLEntry[] selection) {
        this.selection = selection;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public LazyDataModel<GLEntry> getLazyDataModel() {
        if (lazyDataModel == null) {
            lazyDataModel = new GLEntryLazyDataModel();
        }
        return lazyDataModel;
    }
    
    public Object[] getSearchFilter(){
        System.out.println(FacesContext.getCurrentInstance().getResponseComplete());
        Object[] searchFilter = new Object[4];
        searchFilter[0] = "AND";
        int index = 1;
        if(description != null && !"".equals(description)){
            searchFilter[index++]= new Object[]{"contains","description", description};
        }
        if(startDate != null){
            searchFilter[index++] = new Object[]{"gte","transDate",startDate};
        }
        if(endDate != null){
            searchFilter[index++] = new Object[]{"lte", "transDate", endDate};
        }
        return searchFilter;
    } 

    public class GLEntryLazyDataModel extends LazyDataModel<GLEntry> {

        @Override
        public List<GLEntry> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters) {
            Object[] rootPredicate = getSearchFilter();
            if (additionalFilter != null) {
                rootPredicate = new Object[]{"AND", rootPredicate, additionalFilter};
            }
            List<Object[]> hints = JSFUtils.buildQueryHints(columnCriteria);

            List<GLEntry> result = service.find(first, pageSize, sortField, sortOrder.toString(), rootPredicate, hints);
            int projection = service.count(rootPredicate);
            this.setRowCount(projection);
            return result;
        }

        @Override
        public GLEntry getRowData(String rowKey) {
            return service.find(Integer.valueOf(rowKey));
        }

        @Override
        public Object getRowKey(GLEntry entry) {
            return entry.getId();
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

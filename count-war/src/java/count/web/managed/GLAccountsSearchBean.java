/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package count.web.managed;

import count.domain.GLAccount;
import count.service.GLAccountFacade;
import count.web.utils.JSFUtils;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.event.ActionEvent;
import org.primefaces.context.RequestContext;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

/**
 *
 * @author Rommel
 */
public class GLAccountsSearchBean implements Serializable {

    @EJB
    private transient GLAccountFacade service;
    Map<String, String> columnCriteria;
    private LazyDataModel<GLAccount> accountLazyDataModel;
    private GLAccount[] selectedAccounts;
    private GLAccount account;
    private Converter converter;

    public GLAccountsSearchBean() {
        columnCriteria = new HashMap<String, String>();
        columnCriteria.put("code", "contains");
        columnCriteria.put("name", "contains");
        columnCriteria.put("type", "contains");
    }

    public LazyDataModel<GLAccount> getAccountLazyDataModel() {
        if (accountLazyDataModel == null) {
            accountLazyDataModel = new GLAccountLazyDataModel();
        }
        return accountLazyDataModel;
    }

    public Converter getConverter() {
        if (converter == null) {
            converter = new GLAccountConverter();
        }
        return converter;
    }

    public List<GLAccount> complete(String query) {
        Map<String, String> filters = new HashMap<String, String>();
        filters.put("globalFilter", query);
        Object[] rootPredicate = JSFUtils.buildPredicate(columnCriteria, filters);
        List<GLAccount> result = service.find(rootPredicate);
        return result;
    }

    public GLAccount[] getSelectedAccounts() {
        return selectedAccounts;
    }

    public void setSelectedAccounts(GLAccount[] selectedAccounts) {
        this.selectedAccounts = selectedAccounts;
    }

    public List<GLAccount> getAccounts() {
        return service.findAll();
    }

    public GLAccount getAccount() {
        return account;
    }

    public void setAccount(GLAccount account) {
        this.account = account;
    }

    public void dummy(ActionEvent e) {
    }

    public void saveAccount(ActionEvent e) {
        System.out.println("inside SaveAccount");
        RequestContext context = RequestContext.getCurrentInstance();
        context.addCallbackParam("loggedIn", true);
        FacesMessage msg = null;
        msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Login Error", "Invalid credentials");
        FacesContext.getCurrentInstance().addMessage(null, msg);
        save();

    }

    public void save() {
        service.edit(account);
    }

    public class GLAccountLazyDataModel extends LazyDataModel<GLAccount> {

        public GLAccountLazyDataModel() {
        }

        @Override
        public GLAccount getRowData(String rowKey) {
            return service.find(rowKey);
        }

        @Override
        public Object getRowKey(GLAccount account) {
            return account.getAccountId();
        }

        @Override
        public List<GLAccount> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters) {
            Object[] rootPredicate = JSFUtils.buildPredicate(columnCriteria, filters);
            List<GLAccount> result = service.find(first, pageSize, sortField, sortOrder.toString(), rootPredicate);
            int projection = service.count(rootPredicate);
            this.setRowCount(projection);
            return result;
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

    public class GLAccountConverter implements Converter, Serializable {

        @Override
        public Object getAsObject(FacesContext context, UIComponent component, String value) {
            return service.find(Integer.valueOf(value)) ;
        }

        @Override
        public String getAsString(FacesContext context, UIComponent component, Object value) {
            return ((GLAccount) value).getAccountId().toString();
        }
    }
}

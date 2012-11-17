/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package count.web.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.faces.context.FacesContext;
import javax.faces.context.Flash;
import org.eclipse.persistence.config.QueryHints;
import org.eclipse.persistence.queries.FetchGroup;
import org.primefaces.context.RequestContext;

/**
 *
 * @author Rommel
 */
public class JSFUtils {

    public static Object[] buildPredicate(Map<String, String> columnCriteria, Map<String, String> filters) {
        Object[] rootPredicate = null;
        Object[] columnFilters = null;
        if (filters != null && !filters.isEmpty()) {
            columnFilters = new Object[filters.size() + 1];
            columnFilters[0] = "AND";
            int index = 1;
            for (Map.Entry<String, String> entry : filters.entrySet()) {
                String filterProperty = entry.getKey();
                String filterValue = entry.getValue();
                if (!"globalFilter".equalsIgnoreCase(filterProperty)) {
                    String filterMode = columnCriteria.get(filterProperty);
                    Object[] predicate = new Object[3];
                    predicate[0] = filterMode;
                    predicate[1] = filterProperty;
                    predicate[2] = filterValue;
                    columnFilters[index++] = predicate;
                }
            }
        }
        String globalFilterValue = filters.get("globalFilter");
        Object[] globalPredicates = null;
        if (globalFilterValue != null && !"".equals(globalFilterValue)) {
            //+1 for the operator like "OR"
            globalPredicates = new Object[columnCriteria.size() + 1];
            globalPredicates[0] = "OR";
            int i = 1;
            for (Map.Entry<String, String> entry : columnCriteria.entrySet()) {
                String filterProperty = entry.getKey();
                String filterMode = entry.getValue();
                if (filterMode!=null && !"".equals(filterMode)) {
                    Object[] predicate = new Object[3];
                    predicate[0] = filterMode;
                    predicate[1] = filterProperty;
                    predicate[2] = globalFilterValue;
                    globalPredicates[i++] = predicate;
                }
            }
        
        }
        if (columnFilters != null && globalPredicates != null) {
            rootPredicate = new Object[]{"AND", columnFilters, globalPredicates};
        } else if (columnFilters != null && columnFilters.length >= 2) {
            rootPredicate = columnFilters;
        } else if (globalPredicates != null && globalPredicates.length >= 2) {
            rootPredicate = globalPredicates;
        } else {
        }
        return rootPredicate;
    }
    
    public static List<Object[]> buildQueryHints(Map<String, String> map) {
        List<Object[]> hints = new ArrayList<Object[]>();
        FetchGroup fetchGroup = new FetchGroup();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            fetchGroup.addAttribute(entry.getKey());
        }
        hints.add(new Object[]{QueryHints.FETCH_GROUP, fetchGroup});
        return hints;
    }

    public static Flash getFlash() {
        return (FacesContext.getCurrentInstance().getExternalContext().getFlash());
    }

    /**
     * Convenience method for setting Session variables.
     *
     * @param key object key
     * @param object value to store
     */
    public static void storeOnSession(String key, Object object) {
        FacesContext ctx = FacesContext.getCurrentInstance();
        Map sessionState = ctx.getExternalContext().getSessionMap();
        sessionState.put(key, object);
    }

    /**
     * Convenience method for getting Session variables.
     *
     * @param key object key
     * @return session object for key
     */
    public static Object getFromSession(String key) {
        FacesContext ctx = FacesContext.getCurrentInstance();
        Map sessionState = ctx.getExternalContext().getSessionMap();
        return sessionState.get(key);
    }

    public static void addCallbackParam(String key, Object value) {
        RequestContext context = RequestContext.getCurrentInstance();
        context.addCallbackParam(key, value);
    }
}

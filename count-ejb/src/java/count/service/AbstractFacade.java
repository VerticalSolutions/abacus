/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package count.service;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.*;

/**
 *
 * @author Rommel
 */
public abstract class AbstractFacade<T> {

    private Class<T> entityClass;

    public AbstractFacade(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    protected void applyQueryHints(Query query, List<Object[]> hints) {
        if(hints != null && !hints.isEmpty()){
            for(Object[] hint : hints){
                query.setHint((String)hint[0], hint[1]);
            }
        }
    }

    protected Predicate buildInPredicate(Object[] filter, Path path) {
        List values = new ArrayList();
        for(int i = 2; i< filter.length; i++){
            Object value = filter[i];
            if(value != null && !"".equals(value)){
                values.add(value);
            }
        }
        return path.in(values);
    }

    protected abstract EntityManager getEntityManager();

    public void create(T entity) {
        getEntityManager().persist(entity);
    }

    public void edit(T entity) {
        getEntityManager().merge(entity);
    }

    public void remove(T entity) {
        getEntityManager().remove(getEntityManager().merge(entity));
    }

    public T find(Object id) {
        return getEntityManager().find(entityClass, id);
    }

    public List<T> findAll() {
        CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        cq.select(cq.from(entityClass));
        return getEntityManager().createQuery(cq).getResultList();
    }

    public List<T> findRange(int[] range) {
        CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        cq.select(cq.from(entityClass));
        javax.persistence.Query q = getEntityManager().createQuery(cq);
        q.setMaxResults(range[1] - range[0]);
        q.setFirstResult(range[0]);
        return q.getResultList();
    }

    public int count() {
        CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        Root<T> rt = cq.from(entityClass);
        cq.select(getEntityManager().getCriteriaBuilder().count(rt));
        javax.persistence.Query q = getEntityManager().createQuery(cq);
        return ((Long) q.getSingleResult()).intValue();
    }

    public int count(Object[] filters) {
        System.out.println("inside count");
        EntityManager em = getEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery cq = cb.createQuery();
        Root<T> root = cq.from(entityClass);
        cq.select(cb.count(root));
        if (filters != null && filters.length != 0) {
            cq.where(buildPredicate(root, cb, filters));
        }
        Query query = em.createQuery(cq);
        return ((Long) query.getSingleResult()).intValue();
    }

    protected void applyFirstAndMaxResults(Query query, int first, int maxResults) {
        if (first > 0) {
            query.setFirstResult(first);
        }
        if (maxResults > 0) {
            query.setMaxResults(maxResults);
        }
    }
    

    private Predicate buildPredicate(Root<T> root, CriteriaBuilder cb, Object[] filter) {
        if (filter == null || filter.length < 2) {
            throw new IllegalArgumentException("Invalid filter");
        }
        String filterMode = (String) filter[0];
        if ("AND".equalsIgnoreCase(filterMode)) {
            List<Predicate> predicates = new ArrayList<Predicate>();
            for (int i = 1; i < filter.length; i++) {
                Object[] obj = (Object[]) filter[i];
                if (obj != null) {
                    predicates.add(buildPredicate(root, cb, obj));
                }
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        }
        if ("OR".equalsIgnoreCase(filterMode)) {
            List<Predicate> predicates = new ArrayList<Predicate>();
            for (int i = 1; i < filter.length; i++) {
                Object[] obj = (Object[]) filter[i];
                if (obj != null) {
                    predicates.add(buildPredicate(root, cb, obj));
                }
            }
            return cb.or(predicates.toArray(new Predicate[0]));
        }
        String filterProperty = (String) filter[1];
        Path path = root.get(filterProperty);
        if ("contains".equalsIgnoreCase(filterMode)) {
            return cb.like(path, "%" + filter[2] + "%");
        }
        if ("startsWith".equalsIgnoreCase(filterMode)) {
            return cb.like(path, filter[2] + "%");
        }
        if ("endsWith".equalsIgnoreCase(filterMode)) {
            return cb.like(path, "%" + filter[2]);
        }
        if ("exact".equalsIgnoreCase(filterMode)||"equals".equalsIgnoreCase(filterMode)) {
            return cb.equal(path, filter[2]);
        }
        if ("between".equalsIgnoreCase(filterMode)) {
            return cb.between(path, (Comparable)filter[2], (Comparable)filter[3]);
        }
        if ("gte".equalsIgnoreCase(filterMode)) {
            return cb.greaterThanOrEqualTo(path, (Comparable)filter[2]);
        }
        if ("lte".equalsIgnoreCase(filterMode)) {
            return cb.lessThanOrEqualTo(path, (Comparable) filter[2]);
        }
        if ("in".equalsIgnoreCase(filterMode)) {
            return buildInPredicate(filter, path);
        }
        if ("not in".equalsIgnoreCase(filterMode)) {
            return buildInPredicate(filter, path).not();
        }
        throw new IllegalArgumentException("Filter mode: " + filterMode + " is not supported.");

    }

    public List<T> find(int first, int pageSize, String sortField, String sortOrder, Object[] filters) {
        return find(first, pageSize, sortField, sortOrder, filters, null);
    }

    public List<T> find(int first, int pageSize, String sortField, String sortOrder, Object[] filters, List<Object[]> hints) {
        System.out.println("inside find");
        EntityManager em = getEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(entityClass);
        Root<T> root = cq.from(entityClass);
        if (filters != null && filters.length != 0) {
            cq.where(buildPredicate(root, cb, filters));
        }
        if (sortField != null) {
            Order order = null;
            if ("ASCENDING".equalsIgnoreCase(sortOrder)) {
                order = cb.asc(root.get(sortField));
            }
            if ("DESCENDING".equalsIgnoreCase(sortOrder)) {
                order = cb.desc(root.get(sortField));
            }
            if (order != null) {
                cq.orderBy(order);
            }
        }
        Query query = em.createQuery(cq);
        applyFirstAndMaxResults(query, first, pageSize);
        applyQueryHints(query, hints);
        System.out.println("about to return result");
        return query.getResultList();
    }
    
    public List<T> find(Object[] filters) {
        return find(0, 0, null, null, filters);
    }
}

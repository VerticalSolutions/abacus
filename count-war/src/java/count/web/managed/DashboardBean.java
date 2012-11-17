/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package count.web.managed;

import count.domain.GLAccount;
import count.service.GLAccountFacade;
import count.service.GLEntryFacade;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import org.primefaces.event.DashboardReorderEvent;
import org.primefaces.model.DashboardColumn;
import org.primefaces.model.DashboardModel;
import org.primefaces.model.DefaultDashboardColumn;
import org.primefaces.model.DefaultDashboardModel;
import org.primefaces.model.chart.CartesianChartModel;
import org.primefaces.model.chart.LineChartSeries;

/**
 *
 * @author Rommel
 */
@ManagedBean
@RequestScoped
public class DashboardBean implements Serializable {

    private DashboardModel model;
    private List<GLAccount> banks;
    @EJB
    private GLAccountFacade service;
    @EJB
    private GLEntryFacade glEntryFacade;
    private Map<Integer, CartesianChartModel> bankChartModels;
    private double maxY = 0;

    public DashboardBean() {
        
    }
    @PostConstruct
    public void init(){
        model = new DefaultDashboardModel();
        DashboardColumn column1 = new DefaultDashboardColumn();
        DashboardColumn column2 = new DefaultDashboardColumn();
        column1.addWidget("banks");
        column2.addWidget("moneyin");
        column2.addWidget("moneyout");
        column2.addWidget("expenseclaims");
        model.addColumn(column1);
        model.addColumn(column2);
    }
    
    public List<GLAccount> getBanks() {
        if (banks == null) {
            banks = service.findByType("Bank");
        }
        return banks;
    }

    public Map<Integer, CartesianChartModel> getBankChartModels() {
        if (bankChartModels == null) {
            bankChartModels = new HashMap<Integer, CartesianChartModel>();
            for (GLAccount bank : getBanks()) {
                Integer id = bank.getAccountId();
                bankChartModels.put(id,createLinearModel(id));
            }
        }
        return bankChartModels;
    }

    private CartesianChartModel createLinearModel(Integer accountId) {
        CartesianChartModel linearModel = new CartesianChartModel();
        Calendar cal = Calendar.getInstance();
        Date endDate = cal.getTime();
        cal.add(Calendar.DATE, -10);
        Date startDate = cal.getTime();
        LineChartSeries series1 = new LineChartSeries();
        List<Object[]> accountBalances = glEntryFacade.getDailyAccountBalanceByDateRange(accountId, startDate, endDate);
        double max = maxY;
        for (Object[] row : accountBalances) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd");
            double value = ((Number) row[2]).doubleValue();
            series1.set(sdf.format(row[0]), value);
            if (max < value) {
                max = value;
            }
        }
        this.maxY = roundUp(max, 3000);
        linearModel.addSeries(series1);
        return linearModel;
    }

    public static int roundUp(double num, int multipleOf) {
        return (int) (Math.ceil(num / (double) multipleOf) * multipleOf);
    }
    
    public double getMaxY() {
        return maxY;
    }

    public void handleReorder(DashboardReorderEvent event) {
        FacesMessage message = new FacesMessage();
        message.setSeverity(FacesMessage.SEVERITY_INFO);
        message.setSummary("Reordered: " + event.getWidgetId());
        message.setDetail("Item index: " + event.getItemIndex() + ", Column index: " + event.getColumnIndex() + ", Sender index: " + event.getSenderColumnIndex());

        addMessage(message);
    }

    private void addMessage(FacesMessage message) {
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public DashboardModel getModel() {
        return model;
    }
}

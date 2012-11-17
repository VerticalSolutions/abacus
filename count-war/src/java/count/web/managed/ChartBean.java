/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package count.web.managed;

import count.service.GLEntryFacade;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.chart.CartesianChartModel;
import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.chart.LineChartSeries;

@ManagedBean
@ViewScoped
public class ChartBean implements Serializable {

    @EJB
    private GLEntryFacade service;
    private CartesianChartModel linearModel;
    private double maxY;
    private CartesianChartModel categoryModel;
    
    public ChartBean() {
//        createLinearModel();
        createCategoryModel();
    }

    @PostConstruct
    public void init() {
        createLinearModel();
    }

    

    
    
    public CartesianChartModel getLinearModel() {
        return linearModel;
    }

    public CartesianChartModel getCategoryModel() {
        return categoryModel;
    }

    public double getMaxY() {
        return maxY;
    }

    private void createLinearModel() {
        linearModel = new CartesianChartModel();
        Calendar cal = Calendar.getInstance();
        Date endDate = cal.getTime();
        cal.add(Calendar.DATE, -14);
        Date startDate = cal.getTime();
        LineChartSeries series1 = new LineChartSeries();
        List<Object[]> accountBalances = service.getDailyAccountBalanceByDateRange(3, startDate, endDate);
        double max = 0;
        for (Object[] row : accountBalances) {
            SimpleDateFormat sdf = new SimpleDateFormat("MMM d");
            double value = ((Number) row[2]).doubleValue();
            series1.set(sdf.format(row[0]), value);
            if (max < value) {
                max = value;
            }
        }
        this.maxY = roundUp(max, 3000);
        linearModel.addSeries(series1);
    }

    public static int roundUp(double num, int multipleOf) {
        return (int) (Math.ceil(num / (double) multipleOf) * multipleOf);
    }

    private void createCategoryModel() {
        categoryModel = new CartesianChartModel();

        ChartSeries boys = new ChartSeries();
        boys.setLabel("Awaiting Payment");

        boys.set("2004", 120);
        boys.set("2005", 100);
        boys.set("2006", 44);
        boys.set("2007", 150);
        boys.set("2008", 25);

        ChartSeries girls = new ChartSeries();
        girls.setLabel("Paid");

        girls.set("2004", 52);
        girls.set("2005", 60);
        girls.set("2006", 110);
        girls.set("2007", 135);
        girls.set("2008", 120);

        categoryModel.addSeries(boys);
        categoryModel.addSeries(girls);
    }
}

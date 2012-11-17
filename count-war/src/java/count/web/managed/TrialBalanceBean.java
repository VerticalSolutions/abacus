/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package count.web.managed;

import count.service.ReportsFacade;
import count.utils.AppUtils;
import count.web.servlets.ReportEngine;
import count.web.utils.JSFUtils;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

/**
 *
 * @author Rommel
 */
@ManagedBean
@RequestScoped
public class TrialBalanceBean {

    private Date startDate;
    private Date endDate;
    private String output;
    private String reportURL;
    private List<Object[]> data;
    @EJB
    private ReportsFacade service;

    /**
     * Creates a new instance of TrialBalanceBean
     */
    public TrialBalanceBean() {
        reportURL=FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath() + "/ReportEngine?";
        Calendar cal = Calendar.getInstance();
        AppUtils.removeTimeElements(cal);
        cal.set(Calendar.DAY_OF_MONTH,1);
        AppUtils.removeTimeElements(cal);
        startDate = cal.getTime();
        AppUtils.setToEndOfMonth(cal);
        endDate = cal.getTime();
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

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }
    
    Map<String, String> getSecureParams(){
        DateFormat df = new SimpleDateFormat(ReportEngine.DATE_PATTERN);
        Map<String, String> strParams = new HashMap<String, String>();
        strParams.put("rpt","trialbalance");
        strParams.put("start_date", df.format(startDate));
        strParams.put("end_date",df.format(endDate));
        return strParams;
    }

    public List<Object[]> getData() {
        return data;
    }
    
    

    public void runReport(ActionEvent e) {
//        Map<String, String> secureParams = getSecureParams();
//        StringBuilder builder = new StringBuilder();
//        builder.append(reportURL);
//        for(Map.Entry<String, String> entry: secureParams.entrySet()){
//            builder.append("&amp;");
//            builder.append(entry.getKey());
//            builder.append("=");
//            builder.append(entry.getValue());
//        }
//        builder.append("&amp;output=");
//        builder.append(output);
//        builder.append("&amp;key=");
//        builder.append(ReportEngine.getMD5(secureParams));
//        System.out.println(builder.toString());
//        JSFUtils.addCallbackParam("url", builder.toString());
        data = service.getTrialBalanceData(startDate, endDate);
    }

}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package count.utils;

import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author Rommel
 */
public class AppUtils {
    public static void removeTimeElements(Calendar cal){
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);  
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
    }
    public static Date removeTimeElements(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(date.getTime());
        removeTimeElements(cal);
        return cal.getTime();
    }
    
    public static void setToEndOfMonth(Calendar cal){
        int maxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        cal.set(Calendar.DAY_OF_MONTH, maxDay);
        setToEndOfDay(cal);
    }
    
    public static void setToEndOfDay(Calendar cal) {
      cal.set(Calendar.HOUR_OF_DAY, cal.getMaximum(Calendar.HOUR_OF_DAY));
      cal.set(Calendar.MINUTE,      cal.getMaximum(Calendar.MINUTE));
      cal.set(Calendar.SECOND,      cal.getMaximum(Calendar.SECOND));
      cal.set(Calendar.MILLISECOND, cal.getMaximum(Calendar.MILLISECOND));
    }
    
    public static Date setToEndOfMonth(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(date.getTime());
        setToEndOfMonth(cal);
        return cal.getTime();
    }
}

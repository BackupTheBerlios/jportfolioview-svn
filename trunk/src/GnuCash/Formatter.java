/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package GnuCash;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.Date;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;

/**
 *
 * @author are
 */
public class Formatter {
    private static DecimalFormat priceFormat;
    private static DecimalFormat shareFormat;
    private static DateFormat dateFormat;
    
    static DecimalFormat getPriceFormat() {
        if (priceFormat==null) {
            priceFormat=(DecimalFormat) DecimalFormat.getNumberInstance();
            priceFormat.setGroupingUsed(true);
            priceFormat.setMaximumFractionDigits(2);
            priceFormat.setMinimumFractionDigits(2);
            priceFormat.setMinimumIntegerDigits(1);
            
        }
        return priceFormat;
    }
    
    static String formatPrice(double price) {
        return getPriceFormat().format(price);
    }
    
    static DecimalFormat getShareFormat() {
        if (shareFormat==null) {
            shareFormat=(DecimalFormat) DecimalFormat.getNumberInstance();
            shareFormat.setGroupingUsed(true);
            shareFormat.setMaximumFractionDigits(3);
            shareFormat.setMinimumFractionDigits(0);
            shareFormat.setMinimumIntegerDigits(1);
            
        }
        return shareFormat;
    }
    
    static String formatShare(double shares) {
        return getShareFormat().format(shares);
    }
    
    static DateFormat getDateFormat() {
        if (dateFormat==null) {
            dateFormat= DateFormat.getDateInstance();
        }
        return dateFormat;
    }
    
    static String formatDate(Date d) {
        return getDateFormat().format(d);
    }
    
    static String formatDate(DateTime dt) {
        return getDateFormat().format(dt.toDate());
    }
}

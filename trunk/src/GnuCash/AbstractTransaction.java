/*
* Copyright (C) 2008 Andreas Reichel
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 2
* of the License, or any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
*/

package GnuCash;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.ISODateTimeFormat;

/**
* @author Andreas Reichel
* @version $Id$
*/
public abstract class AbstractTransaction {
    static String datemask="";
    static String doublemask="";
    
    double value;
    Date date;
    String description;
    String currency_id;
    String account_id;
    double quantity;
    
    public double getValue() {
        return value;
    }
    
    public String getValueStr() {
        return DecimalFormat.getNumberInstance().format(value);
    }
    
    public Date getDate() {
        return date;
    }
    
    public String getDateStr() {
        return SimpleDateFormat.getDateTimeInstance().format(date);
    }
    
    static double getDoubleValue(String s) {
        String[] r = s.split("/");
        Double x = new Double(0d);

        try {
            x = Double.valueOf(r[0]) / Double.valueOf(r[1]);
        } catch (Exception e) {
            //System.out.println(e.getMessage());
        }

        return x.doubleValue();
    }

    static Date getDateValue(String s) {
        Date d = new Date();
        try {
            d=  GnuCashUtil.getGnuCashDate(s);
        } catch (Exception e) {
            System.out.println(s);
            System.out.println(e.getMessage());
        }
        return d;
    }

    static String formatDate(Date d) {
        return SimpleDateFormat.getDateInstance().format(d);
    }
}
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

package datasource;

import java.text.DecimalFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 *
 * @author are
 */
public class PageParameterSet {
    public final static boolean MODE_ISIN=true;
    public final static boolean MODE_SYMBOL=false;
    
    boolean mode=MODE_ISIN;
    String url;
    String priceXPath;
    String pricePattern;
    DecimalFormat decimalFormat;
    
    String currencyXPath;
    String alternateCurrencyXPath;
    String currencyPattern;
    
    String valutaXPath;
    String valutaPattern;
    DateTimeFormatter dateTimeFormatter;
}

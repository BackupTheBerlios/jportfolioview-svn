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

import GnuCash.Commodity;
import GnuCash.GnuCashDocument;
import GnuCash.Price;
import java.text.ParseException;


import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.dom4j.*;
import org.dom4j.io.*;

import java.util.regex.*;

public class PriceDataSource extends AbstractDataSource {

    PageParameterSet pageParameterSet;
    Commodity commodity;
    HashMap<String, Price> priceHashMap;

    public PriceDataSource(HashMap<String, Price> priceHashMap, PageParameterSet pageParameterSet, Commodity commodity) {
        setPriority(MIN_PRIORITY);
        this.priceHashMap = priceHashMap;
        this.pageParameterSet = pageParameterSet;
        this.commodity = commodity;
        
        if (pageParameterSet.mode == PageParameterSet.MODE_SYMBOL) {
                uriStr = pageParameterSet.url.concat(commodity.getID());
            } else {
                uriStr = pageParameterSet.url.concat(commodity.getXCode());
            }
    }


    void readData() {
        try {
            //@todo: this is broken, but i don't know why
            //@todo: sometimes it returns price too?
            //@todo: how to avoid prices in foreign currencies?
            String currencyID = getNodeValueStr(doc, pageParameterSet.currencyXPath, pageParameterSet.currencyPattern);

            String priceStr = getNodeValueStr(doc, pageParameterSet.priceXPath, pageParameterSet.pricePattern);
            double value = pageParameterSet.decimalFormat.parse(priceStr).doubleValue();

            String valutaStr = getNodeValueStr(doc, pageParameterSet.valutaXPath, pageParameterSet.valutaPattern);
            Date date = pageParameterSet.dateTimeFormatter.parseDateTime(valutaStr).toDate();

            Price price = new Price();
            price.setCommodityID(commodity.getID());
            price.setCommoditySpace(commodity.getSpace());
            price.setCurrencyID(currencyID);
            price.setDate(date);
            price.setSource(GnuCashDocument.PRC_SOURCE);
            price.setType("last");
            price.setValue(value);


            System.out.println(commodity.getID() + " " + date + " " + value + " " + currencyID);
            
            //@todo: remove ugly hack
            //@todo: the length check is needed to work around broken currencies (string contains price and currency)
            //@todo: handle foreign currencies correctly
            if (currencyID.equals("EUR")) priceHashMap.put(commodity.getID(), price);
            
        } catch (ParseException ex) {
            //Logger.getLogger(PriceDataSource.class.getName()).log(Level.INFO, null, ex);
        }
    }
}

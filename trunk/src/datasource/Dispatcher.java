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
import GnuCash.Price;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.dom4j.Document;
import org.dom4j.Node;
import org.joda.time.format.DateTimeFormat;
import org.xml.sax.SAXException;

/**
 *
 * @author are
 */
public class Dispatcher extends Thread {
    Iterator<Commodity> commodityIterator;
    HashMap<String, Price> priceHashMap;
    Vector<Thread> threadVector=new Vector();
    Document config;

    public Dispatcher(Collection<Commodity> commodityCollection) throws SAXException, Exception {
        //@TODO: read from ressource inside the jar
        config = Settings.getInstance().getDatasourceDocument();
        priceHashMap = new HashMap();
        commodityIterator = commodityCollection.iterator();
    }
    
    public HashMap<String, Price> getPriceHashMap() throws InterruptedException {
        Iterator<Thread> threadIterator=threadVector.iterator();
        while (threadIterator.hasNext()) {
            threadIterator.next().join();
        }
        return priceHashMap;
    }
    
    public Price[] getPriceArray() throws InterruptedException {
        Iterator<Thread> threadIterator=threadVector.iterator();
        while (threadIterator.hasNext()) {
            threadIterator.next().join();
        }
        return priceHashMap.values().toArray(new Price[0]);
    }
    
    public void run() {
        Iterator<Node> nodeIterator = config.selectNodes("/pages/page[@mode='SYMBOL']").listIterator();
        while (nodeIterator.hasNext()) {
            Node n=nodeIterator.next();
            
            PageParameterSet pageParameterSet = new PageParameterSet();
            
            String pattern=n.selectSingleNode("@datetimepattern").getText();
            pageParameterSet.dateTimeFormatter = DateTimeFormat.forPattern(pattern);
            
            pattern=n.selectSingleNode("@decimalpattern").getText();
            pageParameterSet.decimalFormat = (DecimalFormat) DecimalFormat.getInstance();
            pageParameterSet.decimalFormat.applyPattern(pattern);
            
            if (n.selectSingleNode("@mode").getText().equals("SYMBOL")) {
                pageParameterSet.mode = PageParameterSet.MODE_SYMBOL;
                System.out.println("set mode to symbol");
            } else {
                pageParameterSet.mode = PageParameterSet.MODE_ISIN;
                System.out.println("set mode to isin");
            }
            pageParameterSet.url = n.selectSingleNode("@url").getText();
            
            pageParameterSet.priceXPath = n.selectSingleNode("trigger[@name='price']/@xpath").getText();
            pageParameterSet.pricePattern = n.selectSingleNode("trigger[@name='price']/@regex").getText();

            pageParameterSet.valutaXPath = n.selectSingleNode("trigger[@name='valuta']/@xpath").getText();
            pageParameterSet.valutaPattern = n.selectSingleNode("trigger[@name='valuta']/@regex").getText();
            
            pageParameterSet.currencyXPath = n.selectSingleNode("trigger[@name='currency']/@xpath").getText();
            pageParameterSet.alternateCurrencyXPath = n.selectSingleNode("trigger[@name='currencyA']/@xpath").getText();
            pageParameterSet.currencyPattern = n.selectSingleNode("trigger[@name='currency']/@regex").getText();

            while (commodityIterator.hasNext()) {
                PriceDataSource pds=new PriceDataSource(priceHashMap, pageParameterSet, commodityIterator.next());
                //threadVector.add(pds);
                pds.run();
            }
        }
    }
}

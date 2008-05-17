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

import java.util.Date;
import org.dom4j.Node;

/**
 *
 * @author are
 */
public class Price extends AbstractTransaction {

    String commodity_id;
    String commodity_space;
    String source;
    String type;
    
    public Price() {}
    
    public Price(Node n) {
        commodity_id = n.selectSingleNode("price:commodity/cmdty:id").getText();
        currency_id = n.selectSingleNode("price:currency/cmdty:id").getText();
        date = getDateValue(n.selectSingleNode("price:time/ts:date").getText());
        source = n.selectSingleNode("price:source").getText();
        type = n.selectSingleNode("price:type").getText();
        value = getDoubleValue(n.selectSingleNode("price:value").getText());

    //System.out.println(commodity_id + ":" + formatDate(date) + ":" + value);
    }
    
    public void setCommodityID(String commodityID) {
        this.commodity_id=commodityID;
    }
    
    public String getCommodityID() {
        return commodity_id;
    }
    
    public void setCurrencyID(String currencyID) {
        this.currency_id=currencyID;
    }
    
    public void setDate(Date date) {
        this.date=date;
    }
    
    public void setSource(String source) {
        this.source=source;
    }
    
    public void setType(String type) {
        this.type=type;
    }
    
    public void setValue(double value) {
        this.value=value;
    }
    
    public void setCommoditySpace(String space) {
        this.commodity_space=space;
    }
}

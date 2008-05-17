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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import org.dom4j.Node;

/**
 *
 * @author are
 */
public class Transaction extends AbstractTransaction {
       
    Transaction() {
        
    }
    
    public Transaction(Node n, String account_id) {
        description = n.selectSingleNode("../../trn:description").getText();
        currency_id = n.selectSingleNode("../../trn:currency/cmdty:id").getText();
        date = getDateValue(n.selectSingleNode("../../trn:date-posted/ts:date").getText());
        value = getDoubleValue(n.selectSingleNode("split:value").getText());
        //quantity = getDoubleValue(n.selectSingleNode("split:quantity").getText());
        
        if (n.selectSingleNode("split:account[@type='guid']").getText().equals(account_id) ) {
                quantity = getDoubleValue(n.selectSingleNode("split:quantity").getText());
            }
    }
    
    static void addTransactions(Vector<Transaction> transactions, Node n, String account_id) {
        List<Node> NL=n.selectNodes("trn:splits[trn:split[split:account='" + account_id + "']]/trn:split");
        
        for (int i=0; i<NL.size();i++) {
            
            Transaction t=new Transaction();
            t.description = n.selectSingleNode("trn:description").getText();
            t.currency_id = n.selectSingleNode("trn:currency/cmdty:id").getText();
            t.date = getDateValue(n.selectSingleNode("trn:date-posted/ts:date").getText());
            
            t.value = getDoubleValue(NL.get(i).selectSingleNode("split:value").getText());
            
            if (NL.get(i).selectSingleNode("split:account[@type='guid']").getText().equals(account_id) ) {
                t.quantity = getDoubleValue(NL.get(i).selectSingleNode("split:quantity").getText());
            }
            
            transactions.add(t);
        }
    }
    
    
    public String getDescription() {
        return description;
    }
    
    public String getQuantity() {
        return DecimalFormat.getNumberInstance().format(quantity);
    }
    
    public Transaction clone() {
        Transaction t=new Transaction();
        t.value=this.value;
        t.date=this.date;
        t.description=this.description;
        t.currency_id=this.currency_id;
        t.account_id=this.account_id;
        t.quantity=this.quantity;
        
        return t;
    }
}

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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
import org.dom4j.*;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.XMLReaderAdapter;

public class GnuCashUtil extends XMLReaderAdapter {

    final static int ADD_NOTHING = 0;
    final static int ADD_COMMODITY = 10;
    final static int ADD_ACCOUNT = 20;
    final static int ADD_STOCK = 21;
    final static int ADD_PRICE = 30;
    final static int ADD_PRICE_TIME = 31;
    final static int ADD_PRICE_COMMODITY = 32;
    final static int ADD_PRICE_CURRENCY = 33;
    final static int ADD_TRANSACTION = 40;
    final static int ADD_SPLIT = 41;
    final static int ADD_TRANSACTION_DATE_POSTED = 42;
    final static String GnuCashDateTimePattern = "yyyy-MM-dd HH:mm:ss Z";
    final static DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(GnuCashDateTimePattern);
    private int mode;
    private String text;
    Document d;
    List<Node> l;
    String r;
    HashMap<String, StockAccount> accountHashMapByID;
    HashMap<String, StockAccount> accountHashMapBySymbol;
    HashMap<String, GnuCashAccount> gnucashAccountHashMap;
    HashMap<String, Commodity> commodityHashMap;
    HashMap<String, Vector> priceHashMap;
    Vector<Price> priceVector;
    StockAccount stockAccount;
    GnuCashAccount gnucashAccount;
    Transaction transaction;
    Transaction split;
    Price price;
    Commodity commodity;
    Vector<Transaction> splitVector;
    Vector<Vector> transactionSplits;
    private static String filename = "";

    public GnuCashUtil(String newFilename) throws SAXException {
        filename = newFilename;
        parse();
    }

    public void parse(String newFileName) {
        filename = newFileName;
        parse();
    }

    public void parse() {
        InputSource inputSource;

        mode = 0;
        text = "";
        accountHashMapByID = new HashMap();
        accountHashMapBySymbol = new HashMap();
        gnucashAccountHashMap = new HashMap();
        commodityHashMap = new HashMap();
        priceHashMap = new HashMap();
        priceVector = new Vector();
        transactionSplits = new Vector();

        try {
            try {
                inputSource = new InputSource(new GZIPInputStream(new FileInputStream(filename)));
            } catch (IOException ex) {
                inputSource = new InputSource(new FileInputStream(filename));
            }

            parse(inputSource);

        } catch (IOException ex) {
            Logger.getLogger(GnuCashUtil.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(GnuCashUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        if (qName.equals("gnc:account")) {
            stockAccount = new StockAccount();
            gnucashAccount =
              new GnuCashAccount();
            mode = ADD_ACCOUNT;

        } else if (qName.equals("gnc:transaction")) {
            transaction = new Transaction();
            splitVector = new Vector();
            mode = ADD_TRANSACTION;



        } else if (qName.equals("trn:split") && mode == ADD_TRANSACTION) {
            split = new Transaction();
            mode = ADD_SPLIT;

        } else if (qName.equals("trn:date-posted") && mode == ADD_TRANSACTION) {
            split = new Transaction();
            mode = ADD_TRANSACTION_DATE_POSTED;


        } else if (qName.equals("price")) {
            price = new Price();
            mode = ADD_PRICE;

        } else if (qName.equals("price:time") && mode == ADD_PRICE) {
            mode = ADD_PRICE_TIME;

        } else if (qName.equals("price:commodity") && mode == ADD_PRICE) {
            mode = ADD_PRICE_COMMODITY;

        } else if (qName.equals("price:currency") && mode == ADD_PRICE) {
            mode = ADD_PRICE_CURRENCY;
        } else if (qName.equals("gnc:commodity")) {
            commodity = new Commodity();
            mode = ADD_COMMODITY;
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
        if (qName.equals("gnc:account") && mode == ADD_ACCOUNT) {
            gnucashAccountHashMap.put(gnucashAccount.id, gnucashAccount);
            mode = ADD_NOTHING;

        } else if (qName.equals("gnc:account") && mode == ADD_STOCK) {
            gnucashAccountHashMap.put(gnucashAccount.id, gnucashAccount);
            accountHashMapByID.put(stockAccount.id, stockAccount);
            mode = ADD_NOTHING;




        } else if (qName.equals("act:type") && (mode == ADD_ACCOUNT)) {
            gnucashAccount.type = text;

            if (text.equals("STOCK") || text.equals("MUTUAL")) {
                mode = ADD_STOCK;
            }

        } else if (qName.equals("act:name") && (mode == ADD_ACCOUNT || mode == ADD_STOCK)) {
            gnucashAccount.name = text;
            stockAccount.name = text;

        } else if (qName.equals("act:id") &&
          (mode == ADD_ACCOUNT || mode == ADD_STOCK)) {
            gnucashAccount.id = text;
            stockAccount.id = text;

        } else if (qName.equals("act:parent") && (mode == ADD_ACCOUNT || mode == ADD_STOCK)) {
            gnucashAccount.parentID = text;

        } else if (qName.equals("cmdty:id") && mode == ADD_STOCK) {
            gnucashAccount.commodityID = text;
            stockAccount.symbol = text;


        } else if (qName.equals("gnc:transaction") && mode == ADD_TRANSACTION) {
            mode = ADD_NOTHING;

        } else if (qName.equals("trn:description") && mode == ADD_TRANSACTION) {
            transaction.description = text;
            transactionSplits.add(splitVector);

        } else if (qName.equals("ts:date") && mode == ADD_TRANSACTION_DATE_POSTED) {
            transaction.date = AbstractTransaction.getDateValue(text);
            mode = ADD_TRANSACTION;

        } else if (qName.equals("ts:date") && mode == ADD_PRICE_TIME) {
            price.date = AbstractTransaction.getDateValue(text);
            mode = ADD_PRICE;

        } else if (qName.equals("cmdty:id") && mode == ADD_PRICE_COMMODITY) {
            price.commodity_id = text;
            mode = ADD_PRICE;

        } else if (qName.equals("cmdty:id") && mode == ADD_PRICE_CURRENCY) {
            price.currency_id = text;
            mode = ADD_PRICE;

        } else if (qName.equals("price:value") && mode == ADD_PRICE) {
            price.value = AbstractTransaction.getDoubleValue(text);

        } else if (qName.equals("price") && mode == ADD_PRICE) {
            priceVector.add(price);
            mode = ADD_NOTHING;

        } else if (qName.equals("trn:split") && mode == ADD_SPLIT) {
            split.date = transaction.date;
            split.currency_id = transaction.currency_id;
            splitVector.add(split);
            mode = ADD_TRANSACTION;

        } else if (qName.equals("split:value") && mode == ADD_SPLIT) {
            split.value = Split.getDoubleValue(text);

        } else if (qName.equals("split:quantity") && mode == ADD_SPLIT) {
            split.quantity = Split.getDoubleValue(text);

        } else if (qName.equals("split:account") && mode == ADD_SPLIT) {
            split.account_id = text;

        } else if (qName.equals(
          "split:action") && mode == ADD_SPLIT) {
            split.description = transaction.description + ", " + text;

        } else if (qName.equals("cmdty:space") && mode == ADD_COMMODITY) {
            commodity.space = text;

        } else if (qName.equals("cmdty:id") && mode == ADD_COMMODITY) {
            commodity.id = text;

        } else if (qName.equals("cmdty:name") && mode == ADD_COMMODITY) {
            commodity.name = text;

        } else if (qName.equals("cmdty:xcode") && mode == ADD_COMMODITY) {
            commodity.xcode = text;

        } else if (qName.equals("cmdty:fraction") && mode == ADD_COMMODITY) {
            commodity.fraction = text;

        } else if (qName.equals("gnc:commodity") && mode == ADD_COMMODITY) {
            commodityHashMap.put(commodity.getSpaceID(), commodity);
            mode = ADD_NOTHING;
        }
    }

    public void characters(char[] ch, int start, int length) {
        if (mode != ADD_NOTHING) {
            text = String.copyValueOf(ch, start, length);
        }
    }

    public void endDocument() {
        // transform splits into transactions of an accounts


        for (int i = 0; i < transactionSplits.size(); i++) {

            for (int k = 0; k < transactionSplits.get(i).size(); k++) {
                Transaction s = (Transaction) transactionSplits.get(i).
                  get(k);

                if (accountHashMapByID.containsKey(s.account_id) && s.quantity != 0) {

                    for (int j = 0; j < transactionSplits.get(i).size(); j++) {
                        Transaction s1 = (Transaction) transactionSplits.get(i).get(j);

                        String accountType = gnucashAccountHashMap.get(s1.account_id).type;

                        if (accountType.equals("STOCK") || accountType.equals("MUTUAL")) {
                            accountHashMapByID.get(s.account_id).transactions.add(s1);
                        } else if (accountType.equals("EXPENSE") || accountType.equals("INCOME")) {
                            s1.quantity = 0d;
                            accountHashMapByID.get(s.account_id).transactions.add(s1);
                        }
                    }
                }
            }



        }

        // sort the transactions of an account by date ascending
        //@todo: replace this with a treemap, that sorts automatically
        // transform to symbol based hashmap, delete accounts without transactions
        Iterator<StockAccount> accIterator = accountHashMapByID.values().iterator();
        while (accIterator.hasNext()) {
            StockAccount account = accIterator.next();
            if (account.getTransactionVector().size() > 0) {
                accountHashMapBySymbol.put(account.symbol, account);
            }
        }

        // add the prices to the accounts
        for (int i = 0; i < priceVector.size(); i++) {
            Price price = priceVector.get(i);

            if (accountHashMapBySymbol.containsKey(price.commodity_id)) {



                accountHashMapBySymbol.get(
                  price.commodity_id).prices.add(price);
            }
        }
        //sort the transactions and the prices of each account
        //@todo: replace this with a treemap, that sorts automatically
        accIterator = accountHashMapBySymbol.values().iterator();
        while (accIterator.hasNext()) {
            StockAccount account = accIterator.next();
            account.sortTransactions();
            account.sortPrices();


            account.calculate();
        }
    }

    public Collection<StockAccount> getAccountVector() {
        return accountHashMapBySymbol.values();
    }

    public StockAccount[] getStockAccountArr() {
        return accountHashMapBySymbol.values().toArray(new StockAccount[0]);
    }

    public Collection<Commodity> getCommodities() {
        return commodityHashMap.values();
    }

    // @todo: implement this properly
    public String[] getCurrencies() {
        String[] currencies = {"EUR", "USD", "JPY", "CHF", "GBP", "AUD"};
        return currencies;
    }

    public boolean hasCommodity(String space, String id, String isin) {
        String s = space.concat("::").concat(id).concat("::").concat(isin);
        return commodityHashMap.containsKey(s);
    }

    public StockAccount getAccount(String symbol) {
        return accountHashMapBySymbol.get(symbol);

    }

    //@todo: filter account because comboboxes can't hold too many values
    //@todo: remove this, when tree-widget is used instead of comboboxes
    public GnuCashAccount[] getGnuCashAccountArr() {
        Vector<GnuCashAccount> relevantAccounts = new Vector();
         Iterator<GnuCashAccount> accIterator = gnucashAccountHashMap.values().iterator();
        while (accIterator.hasNext()) {
            GnuCashAccount acc = accIterator.next();
            if (acc.type.equals("BANK") || acc.type.equals("ASSET") || acc.type.equals("INCOME")) {
                relevantAccounts.add(acc);
            }
        }

        GnuCashAccount[] acc = relevantAccounts.toArray(new GnuCashAccount[0]);
        Arrays.sort(acc, new GnuCashAccountComparator());
        return acc;
    }

    public Collection<GnuCashAccount> getGnuCashAccounts() {
        return gnucashAccountHashMap.values();
    }

    public GnuCashAccount getGnuCashAccount(String id) {
        GnuCashAccount gca = null;
        if (gnucashAccountHashMap.containsKey(id)) {
            gca = gnucashAccountHashMap.get(id);
        }
        return gca;
    }

    public static DateTimeFormatter getGnuCashDateTimeFormatter() {
        return dateTimeFormatter;
    }

    public static DateTime getGnuCashDateTime(String s) {
        return dateTimeFormatter.parseDateTime(s);
    }

    public static Date getGnuCashDate(String s) {
        return dateTimeFormatter.parseDateTime(s).toDate();
    }

    private class GnuCashAccountComparator implements Comparator<GnuCashAccount> {

        public int compare(GnuCashAccount acc1, GnuCashAccount acc2) {
            return acc1.name.compareToIgnoreCase(acc2.name);
        }
    }
}







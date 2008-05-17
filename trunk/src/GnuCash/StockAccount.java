/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package GnuCash;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import org.dom4j.Node;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Period;

/**
 *
 * @author are
 */
public class StockAccount {

    String id;
    String name;
    String type;
    String symbol;
    String code;
    String commodity_id;
    String parent_id;
    Vector<Price> prices = new Vector();
    Vector<Transaction> transactions;
    double value = 0.00;
    double stockBalance = 0d;
    double cashBalance = 0d;
    double income = 0d;
    double yield = 0d;

    public StockAccount() {
        transactions = new Vector();
    }

    public StockAccount(Node n) {
        String XPathStr;
        List<Node> NL;

        name = n.selectSingleNode("act:name").getText();
        id = n.selectSingleNode("act:id").getText();
        type = n.selectSingleNode("act:type").getText();
        commodity_id = n.selectSingleNode("act:commodity/cmdty:id").getText();
        parent_id = n.selectSingleNode("act:parent").getText();
        code = stripSymbol(commodity_id);

        // get prices of account
        XPathStr = "/gnc-v2/gnc:book/gnc:pricedb/price[price:commodity[cmdty:id='" + commodity_id + "']]";

        NL = n.getDocument().selectNodes(XPathStr);

        //@todo: use TreeMap as it sorts automatically
        Price[] p = new Price[NL.size()];
        for (int i = 0; i < NL.size(); i++) {
            p[i] = new Price(NL.get(i));
        }
        Arrays.sort(p, new PriceComparator());
        prices = new Vector(Arrays.asList(p));


        // get transactions of account
        XPathStr = "/gnc-v2/gnc:book/gnc:transaction[trn:splits[trn:split[split:account='" + id + "']]]/trn:splits/trn:split";

        NL = n.getDocument().selectNodes(XPathStr);

        Transaction[] t = new Transaction[NL.size()];
        for (int i = 0; i < NL.size(); i++) {
            t[i] = new Transaction(NL.get(i), id);
            value += t[i].quantity;
        }
        Arrays.sort(t, new TransactionComparator());
        transactions = new Vector(Arrays.asList(t));
    }

    public String getName() {
        return name;
    }

    public String getQuantityStr() {
        return DecimalFormat.getNumberInstance().format(value);
    }

    //----------- Transactions
    public Vector<Transaction> getTransactionVector() {
        return transactions;
    }

    public Transaction[] getTransactionArr() {
        return transactions.toArray(new Transaction[0]);
    }

    public Transaction getFirstTransaction() {
        return transactions.firstElement();
    }

    public String getFirstTransactionDateStr() {
        return SimpleDateFormat.getDateInstance().format(transactions.firstElement().getDate());
    }

    public String getLastTransactionDateStr() {
        return SimpleDateFormat.getDateInstance().format(transactions.lastElement().getDate());
    }

    public String getFirstTransactionValueStr() {
        return DecimalFormat.getNumberInstance().format(transactions.firstElement().getValue());
    }

    public String getLastTransactionValueStr() {
        return DecimalFormat.getNumberInstance().format(transactions.lastElement().getValue());
    }

    public Transaction getLastTransaction() {
        return transactions.lastElement();
    }

    // -----------------
    public Price getFirstPrice() {
        return prices.firstElement();
    }

    public String getFirstPriceDateStr() {
        return SimpleDateFormat.getDateInstance().format(prices.firstElement().getDate());
    }

    public String getLastPriceDateStr() {
        return SimpleDateFormat.getDateInstance().format(prices.lastElement().getDate());
    }

    public String getFirstPriceValueStr() {
        String s = "";
        if (prices.size() > 0) {
            s = DecimalFormat.getNumberInstance().format(prices.firstElement().getValue());
        }
        return s;
    }

    public String getLastPriceValueStr() {
        String s = "";
        if (prices.size() > 0) {
            s = DecimalFormat.getNumberInstance().format(prices.lastElement().getValue());
        }
        return s;
    }

    public Price getLastPrice() {
        return prices.lastElement();
    }

    public Price getPriceByDate(Date d) {
        Price p = prices.lastElement();

        for (int i = 0; i < prices.size(); i++) {
            Price p1 = prices.get(i);
            if (p1.date.compareTo(d) < 0) {
                p = p1;
            }
        }
        return p;
    }

    public void calculate() {
        stockBalance = 0d;
        for (int i = 0; i < transactions.size(); i++) {
            stockBalance += transactions.get(i).quantity;
        }

        if (!prices.isEmpty()) {
            cashBalance = -stockBalance * getLastPrice().getValue();
        }

        income = 0d;
        for (int i = 0; i < transactions.size(); i++) {
            income += transactions.get(i).getValue();
        }
        income = 0 - income - cashBalance;

        yield = findRoot(0);
    }

    //todo: implement this beast
    public double getAveragePrice(double shares) {
        double result = 0.00d;

        Vector<Transaction> trsCopy = new Vector(transactions.size());

        double stockBalance = 0d;
        double sellBalance = 0d;

        // build a copy vector, because we don't like to modify the original
        // calculate, how many share are sold already
        // adjust the price correctly
        for (int i = 0; i < transactions.size(); i++) {
            Transaction trs = (Transaction) transactions.get(i).clone();
            trsCopy.add(trs);


            if (trs.quantity > 0) {
                trs.value /= trs.quantity;
            }

            if (trs.quantity < 0) {
                sellBalance += trs.quantity;
            }
        }

        // reduce every transaction by the sold transactions
        // then we have a vector with still holded shares and there prices
        for (int i = 0; i < trsCopy.size() && sellBalance < 0; i++) {
            Transaction trs = trsCopy.get(i);

            if (trs.quantity < 0) {
                trs.quantity = 0;
            } else {
                double d = trs.quantity + sellBalance;
                if (d > 0) {
                    trs.quantity = d;
                    sellBalance = 0d;
                } else {
                    trs.quantity = 0d;
                    sellBalance = d;
                }
            }
        }

        // now we should have only still existing shares and their prices
        // we can calculate the average price now, but only up to the requested shares!
        double x = 0d;
        double y = 0d;
        for (int i = 0; i < trsCopy.size() && stockBalance < shares; i++) {
            Transaction trs = trsCopy.get(i);
            if (trs.quantity > 0) {
                x += trs.value * trs.quantity;
                y += trs.quantity;

                System.out.println(trs.quantity + " " + trs.value);
                stockBalance += trs.quantity;
            }
        }

        // if y<=0 then something went really wrong here!
        if (y > 0) {
            result = x / y; // average price
        } else {
            result = -1;
        }
        System.out.println(result);
        System.out.println("-------------------");
        return result;
    }
    
    public String getID() {
        return id;
    }
    
    public String getSymbol() {
        return symbol;
    }
    
    //@todo: fix this please
    public String getISIN() {
        return "unkown";
    }
    
    
    public double getStockBalance() {
        return stockBalance;
    }

    public String getStockBalanceStr() {
        return DecimalFormat.getNumberInstance().format(stockBalance);
    }

    public String getCashBalanceStr() {
        return DecimalFormat.getNumberInstance().format(cashBalance);
    }

    public String getIncomeStr() {
        return DecimalFormat.getNumberInstance().format(income);
    }

    public double getIncome() {
        return income;
    }

    private double getResult(double x) {
        double Result = 0;
        int n = 0;
        DateTime d0 = new DateTime(transactions.firstElement().date);

        Iterator<Transaction> trsIterator = transactions.iterator();

        while (trsIterator.hasNext()) {
            Transaction trs = trsIterator.next();
            n = Days.daysBetween(new DateTime(trs.date), d0).getDays();

            Result += trs.value * Math.pow(1d + x, -n / 365d);
        }

        n = Days.daysBetween(new DateTime(), d0).getDays();
        Result += cashBalance * Math.pow(1d + x, -n / 365d);

        return Result;
    }

    private double getPrimeResult(double x) {
        double Result = 0;
        int n = 0;
        DateTime d0 = new DateTime(transactions.firstElement().date);

        Iterator<Transaction> trsIterator = transactions.iterator();

        while (trsIterator.hasNext()) {
            Transaction trs = trsIterator.next();
            n = Days.daysBetween(new DateTime(trs.date), d0).getDays();
            Result += (-n / 365d) * trs.value * Math.pow(1d + x, (-n / 365d) - 1d);
        }

        n = Days.daysBetween(new DateTime(), d0).getDays();
        Result += (-n / 365d) * cashBalance * Math.pow(1d + x, (-n / 365d) - 1d);
        return Result;
    }

    private double findRoot(double x) {
        double tolerance = .001; // Our approximation of zero
        double y = 1;
        long maxIterations = 1000;
        long i = 0;

        while (i < maxIterations && Math.abs(y) > tolerance) {
            y = getResult(x);
            x = x - y / getPrimeResult(x);
            i++;
        }
        return -x;
    }

    public String getYieldStr() {
        return DecimalFormat.getPercentInstance().format(yield);
    }

    public String stripSymbol(String code) {
        String s = "";
        int i = code.indexOf(".");
        if (i > 0) {
            s = code.substring(0, i);
        } else {
            s = code;
        }
        return s;
    }


    public void sortTransactions() {
        Transaction[] t = transactions.toArray(new Transaction[0]);

        Arrays.sort(t, new TransactionComparator());
        transactions = new Vector(Arrays.asList(t));
    }

    public void sortPrices() {
        Price[] p = prices.toArray(new Price[0]);

        Arrays.sort(p, new PriceComparator());
        prices = new Vector(Arrays.asList(p));
    }

    class PriceComparator implements Comparator<Price> {

        public int compare(Price o1, Price o2) {
            return o1.getDate().compareTo(o2.getDate());
        }
    }

    class TransactionComparator implements Comparator<Transaction> {

        public int compare(Transaction o1, Transaction o2) {
            return o1.getDate().compareTo(o2.getDate());
        }
    }
}


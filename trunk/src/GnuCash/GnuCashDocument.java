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

import datasource.Settings;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.dom.DOMElement;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.xml.sax.InputSource;

/**
 *
 * @author are
 */
public class GnuCashDocument {

    public static String CURR_SPACE = "ISO4217";
    public static String CMDTY_SPACE = "XETRA";
    public static String CMDTY_QUOTE_SOURCE = "yahoo_europe";
    public static String CMDTY_VERSION = "2.0.0";
    public static String CMDTY_FRACTION = "1";
    public static String ACC_VERSION = "2.0.0";
    public static String TRN_VERSION = "2.0.0";
    public static String PRC_SOURCE = "Finance::Quote";
    private Element book = null;
    private Element pricedb = null;
    private DecimalFormat decimalFormat;
    private DateTimeFormatter dateTimeFormat;
    private static String filename = "";
    private boolean modified = false;

    public GnuCashDocument(String newFileName) throws DocumentException, FileNotFoundException {
        decimalFormat = (DecimalFormat) DecimalFormat.getIntegerInstance();
        decimalFormat.setGroupingUsed(false);

        //dateTimeFormat=DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss Z");
        dateTimeFormat = GnuCashUtil.getGnuCashDateTimeFormatter();

        filename = newFileName;
    }

    public void setFileName(String newFileName) {
        filename = newFileName;
        reset();
    }

    public void reset() {
        book = null;
        pricedb = null;
    }

    public boolean isModified() {
        return modified;
    }

    public void setBook() {
        Document d;
        InputSource inputSource;

        if (book == null) {
            try {
                try {
                    inputSource = new InputSource(new GZIPInputStream(new FileInputStream(filename)));
                } catch (IOException ex) {
                    inputSource = new InputSource(new FileInputStream(filename));
                }

                SAXReader reader = new SAXReader(false);
                d = reader.read(inputSource);

                book = (Element) d.selectSingleNode("/gnc-v2/gnc:book");
                pricedb = (Element) book.selectSingleNode("gnc:pricedb");

            } catch (FileNotFoundException ex1) {
                Logger.getLogger(GnuCashDocument.class.getName()).log(Level.SEVERE, null, ex1);
            } catch (DocumentException ex) {
                Logger.getLogger(GnuCashDocument.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        modified = false;
    }

    public void writeToXML(String filename) throws FileNotFoundException, UnsupportedEncodingException, IOException, DocumentException {
        String suffix = "";
        OutputStream out = null;

        OutputFormat outformat = OutputFormat.createPrettyPrint();
        outformat.setEncoding("UTF-8");

        setBook();

        //should we save as copy?
        //expect explicit TRUE for safety
        if (!Settings.getInstance().get("jPortfolioView", "file", "saveAsCopy").equals("false")) {
            suffix = ".copy";
        }

        out = new FileOutputStream(filename.concat(suffix));

        //should we compress the file?
        if (Settings.getInstance().get("jPortfolioView", "file", "compressFile").equals("true")) {
            out = new GZIPOutputStream(out);
        }

        XMLWriter writer = new XMLWriter(out, outformat);
        writer.write(book.getDocument());
        writer.flush();

        if (out instanceof GZIPOutputStream) {
            ((GZIPOutputStream) out).finish();
        }
        modified = false;
    }

    // commodity has to be prior pricedb
    // we need to append the commodity into the list of commodities therefore
    public void addCommodity(String isin, String symbol, String name, String space) {
        setBook();

        //Element e=book.addElement("gnc:commodity");
        DOMElement e = new DOMElement("gnc:commodity");
        e.addAttribute("version", CMDTY_VERSION);
        e.addNamespace("cmdty", "http://www.gnucash.org/XML/cmdty");
        e.addElement("cmdty:space").setText(space);
        e.addElement("cmdty:id").setText(symbol);
        e.addElement("cmdty:xcode").setText(isin);
        e.addElement("cmdty:name").setText(name);
        e.addElement("cmdty:fraction").setText(CMDTY_FRACTION);
        e.addElement("cmdty:get_quotes");
        e.addElement("cmdty:quote_source").setText(CMDTY_QUOTE_SOURCE);
        e.addElement("cmdty:quote_tz");

        book.elements("commodity").add(1, e);

        modified = true;
    /*
     *  <gnc:commodity version="2.0.0">
    <cmdty:space>XETRA</cmdty:space>
    <cmdty:id>586590</cmdty:id>
    <cmdty:name>Grenkeleasing AG</cmdty:name>
    <cmdty:fraction>10000</cmdty:fraction>
    <cmdty:get_quotes/>
    <cmdty:quote_source>vwd</cmdty:quote_source>
    <cmdty:quote_tz/>
    </gnc:commodity>
     */
    }

    public String addAccount(String parentID, String isin, String name, String commodityID, String space) {
        String accountID = UUID.randomUUID().toString().replace("-", "");

        setBook();

        Element e = book.addElement("gnc:account");
        e.addAttribute("version", ACC_VERSION);

        e.addElement("act:name").setText(name);

        Element e1 = e.addElement("act:id");
        e1.setText(accountID);
        e1.addAttribute("type", "guid");

        e.addElement("act:type").setText("STOCK");
        e1 = e.addElement("act:commodity");
        e1.addElement("cmdty:space").setText(space);
        e1.addElement("cmdty:id").setText(commodityID);

        e.addElement("act:commodity-scu").setText(CMDTY_FRACTION);
        e.addElement("act:code").setText(isin);
        e.addElement("act:description").setText("Stock inserted by jPortfolioView");

        e1 = e.addElement("act:slots").addElement("slot");
        e1.addElement("slot:key").setText("tax-related");
        e1 = e1.addElement("slot:value");
        e1.addAttribute("type", "integer");
        e1.setText("1");

        e1 = e.addElement("act:parent");
        e1.addAttribute("type", "guid");
        e1.setText(parentID);

        modified = true;
        /*
         * <gnc:account version="2.0.0">
        <act:name>Grenkeleasing AG</act:name>
        <act:id type="guid">29240bd513568d9300ae0adf3d0581eb</act:id>
        <act:type>STOCK</act:type>
        <act:commodity>
        <cmdty:space>XETRA</cmdty:space>
        <cmdty:id>586590</cmdty:id>
        </act:commodity>
        <act:commodity-scu>10000</act:commodity-scu>
        <act:slots>
        <slot>
        <slot:key>tax-related</slot:key>
        <slot:value type="integer">1</slot:value>
        </slot>
        </act:slots>
        <act:parent type="guid">6f81df5326b38f3e2b12bb0bc2b78fbf</act:parent>
        </gnc:account>
         */
        return accountID;
    }

    // @todo: split this into splits
    public String addTransaction(String description, String currency, DateTime valuta, String accountID, String bankAccountID, String incomeAccountID, String feeAccountID, Number shares, Number value, Number fee, Number courtage, Number amount) {
        String transactionID = getUUID();

        setBook();

        String dateposted = dateTimeFormat.print(valuta);
        String dateentered = dateTimeFormat.print(new DateTime());

        Element e = book.addElement("gnc:transaction");
        e.addAttribute("version", TRN_VERSION);

        Element e1 = e.addElement("trn:id");
        e1.addAttribute("type", "guid");
        e1.setText(transactionID);

        e1 = e.addElement("trn:currency");
        e1.addElement("cmdty:space").setText(CURR_SPACE);
        e1.addElement("cmdty:id").setText(currency);


        e.addElement("trn:date-posted").addElement("ts:date").setText(dateposted);
        e.addElement("trn:date-entered").addElement("ts:date").setText(dateentered);
        e.addElement("trn:description").setText(description);

        e1 = e.addElement("trn:splits");

        Element e2 = e1.addElement("trn:split");

        Element e3 = e2.addElement("split:id");
        e3.addAttribute("type", "guid");
        e3.setText(getUUID());

        e2.addElement("split:action").setText("Buy");
        e2.addElement("split:reconciled-state").setText("n");
        e2.addElement("split:value").setText(getNumberStr(currency, CURR_SPACE, value));
        e2.addElement("split:quantity").setText(getNumberStr(shares));
        e3 = e2.addElement("split:account");
        e3.addAttribute("type", "guid");
        e3.setText(accountID);

        e2 = e1.addElement("trn:split");

        e3 = e2.addElement("split:id");
        e3.addAttribute("type", "guid");
        e3.setText(getUUID());
        e2.addElement("split:reconciled-state").setText("n");
        e2.addElement("split:value").setText(getNumberStr(currency, CURR_SPACE, -1 * amount.floatValue()));
        e2.addElement("split:quantity").setText(getNumberStr(currency, CURR_SPACE, -1 * amount.floatValue()));
        e3 = e2.addElement("split:account");
        e3.addAttribute("type", "guid");
        e3.setText(bankAccountID);
        
        if (fee.floatValue()!=0f) {
        e2 = e1.addElement("trn:split");

        e3 = e2.addElement("split:id");
        e3.addAttribute("type", "guid");
        e3.setText(getUUID());
        e2.addElement("split:action").setText("Fee");
        e2.addElement("split:reconciled-state").setText("n");
        e2.addElement("split:value").setText(getNumberStr(currency, CURR_SPACE, fee.floatValue()));
        e2.addElement("split:quantity").setText(getNumberStr(currency, CURR_SPACE, fee.floatValue()));
        e3 = e2.addElement("split:account");
        e3.addAttribute("type", "guid");
        e3.setText(feeAccountID);
        }
        
        if (courtage.floatValue()!=0f) {
        e2 = e1.addElement("trn:split");

        e3 = e2.addElement("split:id");
        e3.addAttribute("type", "guid");
        e3.setText(getUUID());
        e2.addElement("split:action").setText("Courtage");
        e2.addElement("split:reconciled-state").setText("n");
        e2.addElement("split:value").setText(getNumberStr(currency, CURR_SPACE, courtage.floatValue()));
        e2.addElement("split:quantity").setText(getNumberStr(currency, CURR_SPACE, courtage.floatValue()));
        e3 = e2.addElement("split:account");
        e3.addAttribute("type", "guid");
        e3.setText(feeAccountID);
        }
        
        modified = true;

        return transactionID;
    }

    // @todo: split this into splits
    public String sellTransaction(String description, String currency, DateTime valuta, String accountID, String bankAccountID, String incomeAccountID, String feeAccountID, String taxAccountID, Number shares, Number value, Number fee, Number courtage, Number tax, Number income, Number amount) {
        String transactionID = getUUID();

        setBook();

        String dateposted = dateTimeFormat.print(valuta);
        String dateentered = dateTimeFormat.print(new DateTime());

        Element e = book.addElement("gnc:transaction");
        e.addAttribute("version", TRN_VERSION);

        Element e1 = e.addElement("trn:id");
        e1.addAttribute("type", "guid");
        e1.setText(transactionID);

        e1 = e.addElement("trn:currency");
        e1.addElement("cmdty:space").setText(CURR_SPACE);
        e1.addElement("cmdty:id").setText(currency);


        e.addElement("trn:date-posted").addElement("ts:date").setText(dateposted);
        e.addElement("trn:date-entered").addElement("ts:date").setText(dateentered);
        e.addElement("trn:description").setText(description);

        e1 = e.addElement("trn:splits");

        //-----
        Element e2 = e1.addElement("trn:split");

        Element e3 = e2.addElement("split:id");
        e3.addAttribute("type", "guid");
        e3.setText(getUUID());

        e2.addElement("split:action").setText("Sell");
        e2.addElement("split:reconciled-state").setText("n");
        e2.addElement("split:value").setText(getNumberStr(currency, CURR_SPACE, -1 * value.doubleValue()));
        e2.addElement("split:quantity").setText(getNumberStr(-1 * shares.floatValue()));
        e3 = e2.addElement("split:account");
        e3.addAttribute("type", "guid");
        e3.setText(accountID);

        //-----
        e2 = e1.addElement("trn:split");

        e3 = e2.addElement("split:id");
        e3.addAttribute("type", "guid");
        e3.setText(getUUID());

        e2.addElement("split:action").setText("Income");
        e2.addElement("split:reconciled-state").setText("n");
        e2.addElement("split:value").setText(getNumberStr(currency, CURR_SPACE, income));
        e2.addElement("split:quantity").setText(getNumberStr(currency, CURR_SPACE, 0d));
        e3 = e2.addElement("split:account");
        e3.addAttribute("type", "guid");
        e3.setText(accountID);

        //------
        e2 = e1.addElement("trn:split");

        e3 = e2.addElement("split:id");
        e3.addAttribute("type", "guid");
        e3.setText(getUUID());
        e2.addElement("split:reconciled-state").setText("n");
        e2.addElement("split:value").setText(getNumberStr(currency, CURR_SPACE, amount.floatValue()));
        e2.addElement("split:quantity").setText(getNumberStr(currency, CURR_SPACE, amount.floatValue()));
        e3 = e2.addElement("split:account");
        e3.addAttribute("type", "guid");
        e3.setText(bankAccountID);


        //------
        if (fee.floatValue()!=0f) {
        e2 = e1.addElement("trn:split");

        e3 = e2.addElement("split:id");
        e3.addAttribute("type", "guid");
        e3.setText(getUUID());
        e2.addElement("split:action").setText("Fee");
        e2.addElement("split:reconciled-state").setText("n");
        e2.addElement("split:value").setText(getNumberStr(currency, CURR_SPACE, fee.floatValue()));
        e2.addElement("split:quantity").setText(getNumberStr(currency, CURR_SPACE, fee.floatValue()));
        e3 = e2.addElement("split:account");
        e3.addAttribute("type", "guid");
        e3.setText(feeAccountID);
        }
        //------
        
        if (courtage.floatValue()!=0f) {
        e2 = e1.addElement("trn:split");

        e3 = e2.addElement("split:id");
        e3.addAttribute("type", "guid");
        e3.setText(getUUID());
        e2.addElement("split:action").setText("Courtage");
        e2.addElement("split:reconciled-state").setText("n");
        e2.addElement("split:value").setText(getNumberStr(currency, CURR_SPACE, courtage.floatValue()));
        e2.addElement("split:quantity").setText(getNumberStr(currency, CURR_SPACE, courtage.floatValue()));
        e3 = e2.addElement("split:account");
        e3.addAttribute("type", "guid");
        e3.setText(feeAccountID);
        }
        
        //------
        if (tax.floatValue()!=0f) {
        e2 = e1.addElement("trn:split");

        e3 = e2.addElement("split:id");
        e3.addAttribute("type", "guid");
        e3.setText(getUUID());
        e2.addElement("split:action").setText("Tax");
        e2.addElement("split:reconciled-state").setText("n");
        e2.addElement("split:value").setText(getNumberStr(currency, CURR_SPACE, tax));
        e2.addElement("split:quantity").setText(getNumberStr(currency, CURR_SPACE, tax));
        e3 = e2.addElement("split:account");
        e3.addAttribute("type", "guid");
        e3.setText(taxAccountID);
        }
        
        //------
        e2 = e1.addElement("trn:split");

        e3 = e2.addElement("split:id");
        e3.addAttribute("type", "guid");
        e3.setText(getUUID());
        e2.addElement("split:action").setText("Income");
        e2.addElement("split:reconciled-state").setText("n");
        e2.addElement("split:value").setText(getNumberStr(currency, CURR_SPACE, -1 * income.doubleValue()));
        e2.addElement("split:quantity").setText(getNumberStr(currency, CURR_SPACE, -1 * income.doubleValue()));
        e3 = e2.addElement("split:account");
        e3.addAttribute("type", "guid");
        e3.setText(incomeAccountID);

        modified = true;

        return transactionID;
    }

    // @todo: split this into splits
    public String dividendTransaction(String description, String currency, DateTime valuta, String accountID, String bankAccountID, String incomeAccountID, String feeAccountID, String taxAccountID, Number value, Number fee, Number courtage, Number tax, Number income, Number amount) {
        String transactionID = getUUID();

        setBook();

        String dateposted = dateTimeFormat.print(valuta);
        String dateentered = dateTimeFormat.print(new DateTime());

        //buy of the share
        Element e = book.addElement("gnc:transaction");
        e.addAttribute("version", TRN_VERSION);

        Element e1 = e.addElement("trn:id");
        e1.addAttribute("type", "guid");
        e1.setText(transactionID);

        e1 = e.addElement("trn:currency");
        e1.addElement("cmdty:space").setText(CURR_SPACE);
        e1.addElement("cmdty:id").setText(currency);


        e.addElement("trn:date-posted").addElement("ts:date").setText(dateposted);
        e.addElement("trn:date-entered").addElement("ts:date").setText(dateentered);
        e.addElement("trn:description").setText(description);

        /*
         * <gnc:transaction version="2.0.0">
  <trn:id type="guid">5db7b9fdc1769befe14b2bcea0329bc4</trn:id>
  <trn:currency>
    <cmdty:space>ISO4217</cmdty:space>
    <cmdty:id>EUR</cmdty:id>
  </trn:currency>
  <trn:date-posted>
    <ts:date>2006-05-15 00:00:00 +0200</ts:date>
  </trn:date-posted>
  <trn:date-entered>
    <ts:date>2006-07-07 18:02:46 +0200</ts:date>
  </trn:date-entered>
  <trn:description>Dividende Alliant Energy</trn:description>
  <trn:splits>
    <trn:split>
      <split:id type="guid">1808a5aa727d6ba81fe58540e8e3f63b</split:id>
      <split:action>Kauf</split:action>
      <split:reconciled-state>n</split:reconciled-state>
      <split:value>0/100</split:value>
      <split:quantity>10000/10000</split:quantity>
      <split:account type="guid">a60e298bbea14755e21d732362104b69</split:account>
    </trn:split>
  </trn:splits>
</gnc:transaction>
         */ 
        
        
        e1 = e.addElement("trn:splits");

        Element e2 = e1.addElement("trn:split");

        Element e3 = e2.addElement("split:id");
        e3.addAttribute("type", "guid");
        e3.setText(getUUID());

        e2.addElement("split:action").setText("Buy");
        e2.addElement("split:reconciled-state").setText("n");
        e2.addElement("split:value").setText(getNumberStr(currency, CURR_SPACE, 0d));
        e2.addElement("split:quantity").setText(getNumberStr(1));
        e3 = e2.addElement("split:account");
        e3.addAttribute("type", "guid");
        e3.setText(accountID);
        
        /* obsolete
        e2 = e1.addElement("trn:split");

        e3 = e2.addElement("split:id");
        e3.addAttribute("type", "guid");
        e3.setText(getUUID());
        e2.addElement("split:reconciled-state").setText("n");
        e2.addElement("split:value").setText(getNumberStr(currency, CURR_SPACE, 0d));
        e2.addElement("split:quantity").setText(getNumberStr(currency, CURR_SPACE, -1 * 0d));
        e3 = e2.addElement("split:account");
        e3.addAttribute("type", "guid");
        e3.setText(bankAccountID);
        */


        // Sale of the share
        
        /*
         * <gnc:transaction version="2.0.0">
  <trn:id type="guid">f8b256f02ff8a92237fe09552e3b56dc</trn:id>
  <trn:currency>
    <cmdty:space>ISO4217</cmdty:space>
    <cmdty:id>EUR</cmdty:id>
  </trn:currency>
  <trn:date-posted>
    <ts:date>2006-05-15 00:00:00 +0200</ts:date>
  </trn:date-posted>
  <trn:date-entered>
    <ts:date>2006-07-07 18:09:52 +0200</ts:date>
  </trn:date-entered>
  <trn:description>Dividende Alliant Energy</trn:description>
  <trn:splits>
    <trn:split>
      <split:id type="guid">913e28309a0d50439afe78fbbac97d3d</split:id>
      <split:action>Verkauf</split:action>
      <split:reconciled-state>n</split:reconciled-state>
      <split:value>0/100</split:value>
      <split:quantity>-10000/10000</split:quantity>
      <split:account type="guid">a60e298bbea14755e21d732362104b69</split:account>
    </trn:split>
    <trn:split>
      <split:id type="guid">c060929f5707741c7836ec180b3ea972</split:id>
      <split:action>Dividende</split:action>
      <split:reconciled-state>n</split:reconciled-state>
      <split:value>611/100</split:value>
      <split:quantity>611/100</split:quantity>
      <split:account type="guid">06c3725806c918fac4a2e9ff468af42b</split:account>
    </trn:split>
    <trn:split>
      <split:id type="guid">78ac9704d09834cd81e353796d0979e7</split:id>
      <split:action>Dividende</split:action>
      <split:reconciled-state>n</split:reconciled-state>
      <split:value>-611/100</split:value>
      <split:quantity>-611/100</split:quantity>
      <split:account type="guid">b47af96d62b6e6f45f8e30a051b8cc21</split:account>
    </trn:split>
  </trn:splits>
</gnc:transaction>
*/
        
        
        e = book.addElement("gnc:transaction");
        e.addAttribute("version", TRN_VERSION);

        e1 = e.addElement("trn:id");
        e1.addAttribute("type", "guid");
        e1.setText(transactionID);

        e1 = e.addElement("trn:currency");
        e1.addElement("cmdty:space").setText(CURR_SPACE);
        e1.addElement("cmdty:id").setText(currency);


        e.addElement("trn:date-posted").addElement("ts:date").setText(dateposted);
        e.addElement("trn:date-entered").addElement("ts:date").setText(dateentered);
        e.addElement("trn:description").setText(description);

        e1 = e.addElement("trn:splits");

        //-----
        e2 = e1.addElement("trn:split");

        e3 = e2.addElement("split:id");
        e3.addAttribute("type", "guid");
        e3.setText(getUUID());

        e2.addElement("split:action").setText("Sale");
        e2.addElement("split:reconciled-state").setText("n");
        e2.addElement("split:value").setText(getNumberStr(currency, CURR_SPACE, 0 ));
        e2.addElement("split:quantity").setText(getNumberStr(-1d));
        e3 = e2.addElement("split:account");
        e3.addAttribute("type", "guid");
        e3.setText(accountID);

        //-----
        e2 = e1.addElement("trn:split");

        e3 = e2.addElement("split:id");
        e3.addAttribute("type", "guid");
        e3.setText(getUUID());

        e2.addElement("split:action").setText("Income");
        e2.addElement("split:reconciled-state").setText("n");
        e2.addElement("split:value").setText(getNumberStr(currency, CURR_SPACE, amount));
        e2.addElement("split:quantity").setText(getNumberStr(currency, CURR_SPACE, amount));
        e3 = e2.addElement("split:account");
        e3.addAttribute("type", "guid");
        e3.setText(bankAccountID);

        //------
        if (fee.floatValue() != 0f) {
            e2 = e1.addElement("trn:split");

            e3 = e2.addElement("split:id");
            e3.addAttribute("type", "guid");
            e3.setText(getUUID());
            e2.addElement("split:action").setText("Fee");
            e2.addElement("split:reconciled-state").setText("n");
            e2.addElement("split:value").setText(getNumberStr(currency, CURR_SPACE, fee.floatValue()));
            e2.addElement("split:quantity").setText(getNumberStr(currency, CURR_SPACE, fee.floatValue()));
            e3 = e2.addElement("split:account");
            e3.addAttribute("type", "guid");
            e3.setText(feeAccountID);
        }

        //------
        if (courtage.floatValue() != 0f) {
            e2 = e1.addElement("trn:split");

            e3 = e2.addElement("split:id");
            e3.addAttribute("type", "guid");
            e3.setText(getUUID());
            e2.addElement("split:action").setText("Courtage");
            e2.addElement("split:reconciled-state").setText("n");
            e2.addElement("split:value").setText(getNumberStr(currency, CURR_SPACE, courtage.floatValue()));
            e2.addElement("split:quantity").setText(getNumberStr(currency, CURR_SPACE, courtage.floatValue()));
            e3 = e2.addElement("split:account");
            e3.addAttribute("type", "guid");
            e3.setText(feeAccountID);
        }

        //------
        if (tax.floatValue() != 0f) {
            e2 = e1.addElement("trn:split");

            e3 = e2.addElement("split:id");
            e3.addAttribute("type", "guid");
            e3.setText(getUUID());
            e2.addElement("split:action").setText("Tax");
            e2.addElement("split:reconciled-state").setText("n");
            e2.addElement("split:value").setText(getNumberStr(currency, CURR_SPACE, tax));
            e2.addElement("split:quantity").setText(getNumberStr(currency, CURR_SPACE, tax));
            e3 = e2.addElement("split:account");
            e3.addAttribute("type", "guid");
            e3.setText(taxAccountID);
        }

        //------
        e2 = e1.addElement("trn:split");

        e3 = e2.addElement("split:id");
        e3.addAttribute("type", "guid");
        e3.setText(getUUID());
        e2.addElement("split:action").setText("Income");
        e2.addElement("split:reconciled-state").setText("n");
        e2.addElement("split:value").setText(getNumberStr(currency, CURR_SPACE, -1 * income.doubleValue()));
        e2.addElement("split:quantity").setText(getNumberStr(currency, CURR_SPACE, -1 * income.doubleValue()));
        e3 = e2.addElement("split:account");
        e3.addAttribute("type", "guid");
        e3.setText(incomeAccountID);

        modified = true;

        /*
        <gnc:transaction version="2.0.0"> 
        <trn:id type="guid">68c75feaa06e478a540f4b9d3e01ca40</trn:id>  
        <trn:currency> 
        <cmdty:space>ISO4217</cmdty:space>  
        <cmdty:id>EUR</cmdty:id> 
        </trn:currency>  
        <trn:date-posted> 
        <ts:date>2005-11-21 00:00:00 +0100</ts:date> 
        </trn:date-posted>  
        <trn:date-entered> 
        <ts:date>2005-11-21 18:46:31 +0100</ts:date> 
        </trn:date-entered>  
        <trn:description>Zinsen DEKA Convergence Renten</trn:description>  
        <trn:splits> 
        <trn:split> 
        <split:id type="guid">2b30b8851a1321058ae6346a0f56fd92</split:id>  
        <split:reconciled-state>n</split:reconciled-state>  
        <split:value>22320/100</split:value>  
        <split:quantity>22320/100</split:quantity>  
        <split:account type="guid">06c3725806c918fac4a2e9ff468af42b</split:account> 
        </trn:split>  
        <trn:split> 
        <split:id type="guid">958cf0eb3b6b66502b398dc272c7870e</split:id>  
        <split:action>Kauf</split:action>  
        <split:reconciled-state>n</split:reconciled-state>  
        <split:value>-22320/100</split:value>  
        <split:quantity>-10000/10000</split:quantity>  
        <split:account type="guid">d4db45a31352d597564635dafc11bb2b</split:account> 
        </trn:split> 
        </trn:splits> 
        </gnc:transaction>
         * 
         *  
         */

        /*
        <gnc:transaction version="2.0.0"> 
        <trn:id type="guid">f240993e6d6a6bf968b1ae72608e0090</trn:id>  
        <trn:currency> 
        <cmdty:space>ISO4217</cmdty:space>  
        <cmdty:id>EUR</cmdty:id> 
        </trn:currency>  
        <trn:date-posted> 
        <ts:date>2005-11-25 00:00:00 +0100</ts:date> 
        </trn:date-posted>  
        <trn:date-entered> 
        <ts:date>2005-11-26 09:37:23 +0100</ts:date> 
        </trn:date-entered>  
        <trn:description>Zinsen</trn:description>  
        <trn:splits> 
        <trn:split> 
        <split:id type="guid">9ca1a16bc4af1f68b2a9caf918a289ee</split:id>  
        <split:reconciled-state>n</split:reconciled-state>  
        <split:value>5550/100</split:value>  
        <split:quantity>5550/100</split:quantity>  
        <split:account type="guid">06c3725806c918fac4a2e9ff468af42b</split:account> 
        </trn:split>  
        <trn:split> 
        <split:id type="guid">ef710a698ea7fed24fc6387190619d12</split:id>  
        <split:action>Verkauf</split:action>  
        <split:reconciled-state>n</split:reconciled-state>  
        <split:value>-5550/100</split:value>  
        <split:quantity>-10000/10000</split:quantity>  
        <split:account type="guid">92fc13d90ee305e5fc2ad3bd913b7069</split:account> 
        </trn:split> 
        </trn:splits> 
        </gnc:transaction>
         */
        return transactionID;
    }

    public String addPrice(String currencyID, String commodityID, float price, DateTime valuta) {
        String priceID = getUUID();

        setBook();

        Element e = pricedb.addElement("price");

        Element e1 = e.addElement("price:id");
        e1.addAttribute("type", "guid");
        e1.setText(priceID);

        e1 = e.addElement("price:commodity");
        e1.addElement("cmdty:space").setText(CMDTY_SPACE);
        e1.addElement("cmdty:id").setText(commodityID);

        e1 = e.addElement("price:currency");
        e1.addElement("cmdty:space").setText(CURR_SPACE);
        e1.addElement("cmdty:id").setText(currencyID);

        e1 = e.addElement("price:time").addElement("ts:date");
        e1.setText(dateTimeFormat.print(valuta));

        e.addElement("price:source").setText(PRC_SOURCE);
        e.addElement("price:type").setText("last");
        e.addElement("price:value").setText(getNumberStr(price, 5));

        /*
         * <price>
        <price:id type="guid">9de63d78106776fb9035d71aacc34a7e</price:id>
        <price:commodity>
        <cmdty:space>ISO4217</cmdty:space>
        <cmdty:id>THB</cmdty:id>
        </price:commodity>
        <price:currency>
        <cmdty:space>ISO4217</cmdty:space>
        <cmdty:id>EUR</cmdty:id>
        </price:currency>
        <price:time>
        <ts:date>2007-12-11 09:22:48 +0100</ts:date>
        </price:time>
        <price:source>Finance::Quote</price:source>
        <price:type>last</price:type>
        <price:value>223900000/10000000000</price:value>
        </price>
         */
        modified = true;

        return priceID;
    }

    public String addPrice(Price p) {
        String priceID = getUUID();

        setBook();

        Element e = pricedb.addElement("price");

        Element e1 = e.addElement("price:id");
        e1.addAttribute("type", "guid");
        e1.setText(priceID);

        e1 = e.addElement("price:commodity");
        e1.addElement("cmdty:space").setText(p.commodity_space);
        e1.addElement("cmdty:id").setText(p.commodity_id);

        e1 = e.addElement("price:currency");
        e1.addElement("cmdty:space").setText(CURR_SPACE);
        e1.addElement("cmdty:id").setText(p.currency_id);

        e1 = e.addElement("price:time").addElement("ts:date");
        e1.setText(dateTimeFormat.print(new DateTime(p.date)));

        e.addElement("price:source").setText(PRC_SOURCE);
        e.addElement("price:type").setText("last");
        e.addElement("price:value").setText(getNumberStr(p.value, 5));

        modified = true;

        return priceID;
    }

    private String getUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    //@TODO: implement these functions properly
    private String getNumberStr(String commodityID, String space, Number value) {
        String s = decimalFormat.format(value.floatValue() * 100f).concat("/100");
        return s;
    }

    private String getNumberStr(Number value) {
        String s = decimalFormat.format(value).concat("/").concat(CMDTY_FRACTION);
        return s;
    }

    private String getNumberStr(Number value, int digits) {
        String s = decimalFormat.format(value.floatValue() * 100000).concat("/100000");
        return s;
    }
}

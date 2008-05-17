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

package PortfolioView;

/**
* @author are
*/

//@todo: fix the currencies
//@todo: implement the tree selectors

import javafx.ui.*;
import GnuCash.*;
import datasource.*;
import java.lang.Object;
import java.lang.System;
import java.text.DecimalFormat;
import java.util.HashMap;
import org.joda.time.DateTime;
import org.joda.time.<<format>>.DateTimeFormat;
import org.joda.time.<<format>>.DateTimeFormatter;

class AssetFrame extends Frame {
    attribute rows:Row[];
    attribute columns:Column[];
    
    attribute gnucashUtil:GnuCashUtil;
    attribute gnucashDocument:GnuCashDocument;
    
    // needed for dividend or sale
    attribute account:StockAccount on replace {
        symbol=account.getSymbol();
        name=account.getName();
        isin=account.getISIN();
    };
    
    attribute filename:String;
    
    // 0 - addMode
    // 1 - dividendMode
    // 2 - sellMode
    // 3 - buyMode
    attribute modeSelection:Integer=1;
    
    attribute symbol:String;
    attribute isin:String;
    attribute name:String;
    attribute valuta:String;
    attribute shares:String on replace { calculateAmount(); };
    attribute price:String on replace { calculateAmount(); };
    
    attribute currencies:String[]=["EUR", "USD", "GBP", "JPY", "CHF"];
    attribute currencySelection:Integer=0;
    
    attribute courtage:String on replace { calculateAmount(); };
    attribute fee:String on replace { calculateAmount(); };
    attribute tax:String on replace { calculateAmount(); };
    attribute amount:String;
    attribute value:String;
    attribute bruttoIncome:String;
    attribute nettoIncome:String;
    
    attribute parentAccountID:String;
    attribute bankAccountID:String;
    attribute incomeAccountID:String;
    attribute feeAccountID:String;
    attribute taxAccountID:String;
    
    postinit {
        width=360;
        height=640;
        
        rows=[
        Row {alignment: Alignment.BASELINE }
        , Row {alignment: Alignment.BASELINE }
        , Row {alignment: Alignment.BASELINE }
        , Row {alignment: Alignment.BASELINE }
        , Row {alignment: Alignment.BASELINE }
        , Row {alignment: Alignment.BASELINE }
        , Row {alignment: Alignment.BASELINE }
        , Row {alignment: Alignment.BASELINE }
        , Row {alignment: Alignment.BASELINE }
        , Row {alignment: Alignment.BASELINE }
        , Row {alignment: Alignment.BASELINE }
        , Row {alignment: Alignment.BASELINE }
        , Row {alignment: Alignment.BASELINE }
        , Row {alignment: Alignment.BASELINE }
        , Row {alignment: Alignment.BASELINE }
        , Row {alignment: Alignment.BASELINE }
        , Row {alignment: Alignment.BASELINE }
        , Row {alignment: Alignment.BASELINE }
        , Row {alignment: Alignment.BASELINE }
        ];
        
        columns=[
        Column { alignment: Alignment.TRAILING }
        , Column {
            alignment: Alignment.LEADING
            resizable: true
        }
        , Column {
            alignment: Alignment.LEADING
            resizable: false
        }
        ];
        
        content=BorderPanel {
            
            
            center: GroupPanel {
                rows: rows
                
                columns: columns
                
                content: [
                Label {
                    row: rows[0]
                    column: columns[0]
                    text: "Mode"
                    
                }
                , ComboBox {
                    row: rows[0]
                    column: columns[1]
                    
                    cells: [
                    ComboBoxCell {
                        text: "Add a new asset"
                        value: "Add a new asset"
                    }
                    , ComboBoxCell {
                        text: "Collect dividend"
                        value: "Collect dividend"
                    }
                    , ComboBoxCell {
                        text: "Sell an asset"
                        value: "Sell an asset"
                    }
                    , ComboBoxCell {
                        text: "Buy more shares"
                        value: "Buy more shares"
                    }
                    ]
                    selection: bind modeSelection with inverse
                }
                
                , Label {
                    row: rows[1]
                    column: columns[0]
                    text: "ISIN"
                }
                , TextField {
                    row: rows[1]
                    column: columns[1]
                    value: bind isin with inverse
                    
                }
                , Button {
                    row: rows[1]
                    column: columns[2]
                    text: "..."
                    toolTipText: "look up for isin"
                    action: function():Void {
                        var hashMap=new HashMap();
                        var dataSource=new InfoDataSource(hashMap, isin);
                        dataSource.run();
                        dataSource.join();
                    
                        symbol=hashMap.get("symbol").toString();
                        name=hashMap.get("name").toString();
                    }
                }
                
                , Label {
                    row: rows[2]
                    column: columns[0]
                    text: "Symbol"
                }
                , TextField {
                    row: rows[2]
                    column: columns[1]
                    value: bind symbol with inverse
                    
                }
                , Label {
                    row: rows[3]
                    column: columns[0]
                    text: "Name"
                }
                
                , TextField {
                    row: rows[3]
                    column: columns[1]
                    value: bind name with inverse
                }
                
                , Label {
                    row: rows[4]
                    column: columns[0]
                    text: "Valuta"
                }
                , TextField {
                    row: rows[4]
                    column: columns[1]
                    text: bind valuta with inverse
                }
                
                
                , Label {
                    row: rows[5]
                    column: columns[0]
                    text: "Shares"
                }
                , TextField {
                    row: rows[5]
                    column: columns[1]
                    text: bind shares with inverse
                }
                , Label {
                    row: rows[6]
                    column: columns[0]
                    text: "Price"
                    
                }
                , TextField {
                    row: rows[6]
                    column: columns[1]
                    value: bind price with inverse
                }
                //@TODO: get currencies from gnucash
                , ComboBox {
                    row: rows[6]
                    column: columns[2]
                    cells: bind for (c in currencies ) [
                        ComboBoxCell {text: c}
                        ]
                        selection: bind currencySelection with inverse
                }
                
                , Label {
                    row: rows[7]
                    column: columns[0]
                    text: "Courtage"
                    
                }
                , TextField {
                    row: rows[7]
                    column: columns[1]
                    value: bind courtage with inverse
                }
                
                , Label {
                    row: rows[8]
                    column: columns[0]
                    text: "Fee"
                    
                }
                , TextField {
                    row: rows[8]
                    column: columns[1]
                    value: bind fee with inverse
                }
                
                , Label {
                    row: rows[9]
                    column: columns[0]
                    text: "Tax"
                    
                }
                , TextField {
                    row: rows[9]
                    column: columns[1]
                    toolTipText: "Tax that was payed already (Quellensteuer)"
                    //visible: bind (modeSelection==2)
                    value: bind tax with inverse
                } 
                
                , Label {
                    row: rows[10]
                    column: columns[0]
                    text: "Amount"
                    
                }
                , TextField {
                    row: rows[10]
                    column: columns[1]
                    editable: false
                    value: bind amount with inverse
                }
                
                , Label {
                    row: rows[11]
                    column: columns[0]
                    text: "Income brutto"
                    
                }
                , TextField {
                    row: rows[11]
                    column: columns[1]
                    editable: false
                    toolTipText: "Income before costs and tax"
                    value: bind bruttoIncome with inverse
                }
                
                , Label {
                    row: rows[12]
                    column: columns[0]
                    text: "Income netto"
                    
                }
                , TextField {
                    row: rows[12]
                    column: columns[1]
                    editable: false
                    toolTipText: "Income after costs and tax"
                    value: bind nettoIncome with inverse
                }
                , Label {
                    row: rows[13]
                    column: columns[0]
                    text: "Parent Account"
                    
                }
                , TreeSelector {
                    row: rows[13]
                    column: columns[1]
                    gnucashUtil: bind gnucashUtil
                    value: bind parentAccountID with inverse
                }
                
                , Label {
                    row: rows[14]
                    column: columns[0]
                    text: "Depot Account"
                
                }
                
                , TreeSelector {
                    row: rows[14]
                    column: columns[1]
                    gnucashUtil: bind gnucashUtil
                    value: bind bankAccountID with inverse
                }
                
                , Label {
                    row: rows[15]
                    column: columns[0]
                    text: "Income Account"
                
                }
                
                , TreeSelector {
                    row: rows[15]
                    column: columns[1]
                    gnucashUtil: bind gnucashUtil
                    value: bind incomeAccountID with inverse
                }
                
                , Label {
                    row: rows[16]
                    column: columns[0]
                    text: "Fee Account"
                
                }
                
                , TreeSelector {
                    row: rows[16]
                    column: columns[1]
                    gnucashUtil: bind gnucashUtil
                    value: bind feeAccountID with inverse
                }
                
                , Label {
                    row: rows[17]
                    column: columns[0]
                    text: "Tax Account"
                
                }
                
                , TreeSelector {
                    row: rows[17]
                    column: columns[1]
                    gnucashUtil: bind gnucashUtil
                    value: bind taxAccountID with inverse
                }
                ]
            }
            
            
            
            bottom: FlowPanel {
                alignment: Alignment.TRAILING
                content: [
                Button {
                    text: "cancel"
                    action: function() {
                        this.close();
                    }
                }
                
                , Button {
                    text: "add"
                    action: function() {
                        if ( verifyData() ) {
                            System.out.println("looks good, start to write the data");

                            if (modeSelection==0) {
                                addAsset();
                            } else if (modeSelection==1) {
                                collectDividend();
                            } else if (modeSelection==2) {
                                sellAsset();
                            } else if (modeSelection==3) {
                                buyAsset();
                            }

                            this.close();
                        }
                    }
                }
                ]
            }
    };
}

function calculateAmount():Void {
    var s=0.0;
    var p=0.0;
    var f=0.0;
    var c=0.0;
    var t=0.0;
    
    try {
        s=DecimalFormat.getInstance().parse(shares).floatValue();
        } catch (any) {
        //shares=DecimalFormat.getInstance().<<format>>(0);
    }
    
    try {
        p=DecimalFormat.getInstance().parse(price).floatValue();
        } catch (any1) {
        //price=DecimalFormat.getInstance().<<format>>(0.00);
    }
    
    try {
        f=DecimalFormat.getInstance().parse(fee).floatValue();
        } catch (any2) {
        //fee=DecimalFormat.getInstance().<<format>>(0.00);
    }
    
    try {
        c=DecimalFormat.getInstance().parse(courtage).floatValue();
        } catch (any3) {
        //courtage=DecimalFormat.getInstance().<<format>>(0.00);
    }
    
    try {
        t=DecimalFormat.getInstance().parse(tax).floatValue();
        } catch (any4) {
        //tax=DecimalFormat.getInstance().<<format>>(0.00);
    }
    
    if (modeSelection==0 or modeSelection==3) {
        value=DecimalFormat.getInstance().<<format>>(s*p);
        amount=DecimalFormat.getInstance().<<format>>(s*p + f + c +t);
        bruttoIncome=DecimalFormat.getInstance().<<format>>(0.00);
        nettoIncome=DecimalFormat.getInstance().<<format>>(-f-c-t);
        
        } else if (modeSelection==2) {
        value=DecimalFormat.getInstance().<<format>>(s*p);
        amount=DecimalFormat.getInstance().<<format>>(s*p - f - c - t);
        
        var i=s*(p-account.getAveragePrice(s));
        
        bruttoIncome=DecimalFormat.getInstance().<<format>>(i);
        nettoIncome=DecimalFormat.getInstance().<<format>>(i-f-c-t);
        
        } else if (modeSelection==1) {
        value=DecimalFormat.getInstance().<<format>>(s*p);
        amount=DecimalFormat.getInstance().<<format>>(s*p - f - c - t);
        bruttoIncome=DecimalFormat.getInstance().<<format>>(s*p);
        nettoIncome=DecimalFormat.getInstance().<<format>>((s*p)-f-c-t);
    }
}

function getDateTimeValue(s:String) {
    //return DateTimeFormat.forPattern("yyyy-MM-dd").parseDateTime(s);
    return DateTimeFormat.shortDate().parseDateTime(s);
}

function getFloatValue(s:String) {
    return DecimalFormat.getInstance().parse(s).floatValue();
}

function sellAsset() {
    var currency:String=currencies[currencySelection];
    
    // test, if currency exists already
    // else add it to the document
    // @TODO: implement this properly
    if (not gnucashUtil.hasCommodity(GnuCashDocument.CURR_SPACE, currency, "")) {
        System.out.println("missing currency: {currency}, please add it manually");
    }
    
    gnucashDocument.sellTransaction("Verkauf {isin} {name}"
        , currency
        , getDateTimeValue(valuta)
        , account.getID()
        , bankAccountID
        , incomeAccountID
        , feeAccountID
        , taxAccountID
        , getFloatValue(shares)
        , getFloatValue(value)
        , getFloatValue(fee)
        , getFloatValue(courtage)
        , getFloatValue(tax)
        , getFloatValue(bruttoIncome)
        , getFloatValue(amount)
    );
    
    // take care: commodity has to be in place before price is added
    // @TODO: improve this
    gnucashDocument.addPrice(currency, symbol, getFloatValue(price), getDateTimeValue(valuta));
}

function verifyData() {
    var result:Boolean=true;
    
    if (isin.length()==0) {
        System.out.println("Error: please insert an isin!");
        result=false;
        } else if (symbol.length()==0) {
        System.out.println("Error: please insert an symbol!");
        result=false;
        } else if (name.length()==0) {
        System.out.println("Warning: you shoul insert a name");
        result=false;
        } else if (valuta.length()==0) {
        System.out.println("Error: please insert an date value!");
        result=false;
        } else if (shares.length()==0) {
        System.out.println("Error: please insert the number of bought shares!");
        result=false;
        } else if (price.length()==0) {
        System.out.println("Warning: you should insert the price!");
        result=false;
    }
    return result;
}

function readSettings() {
    var settings=Settings.getInstance();
    
    parentAccountID=settings.get("jPortfolioView","Accounts","Parent");
    bankAccountID=settings.get("jPortfolioView","Accounts","Bank");
    incomeAccountID=settings.get("jPortfolioView","Accounts","Income");
    feeAccountID=settings.get("jPortfolioView","Accounts","Fee");
    taxAccountID=settings.get("jPortfolioView","Accounts","Tax");
}

function addAsset() {
    var currency:String=currencies[currencySelection];
    
    // test, if commodity exists already
    // else add it to the document
    if (not gnucashUtil.hasCommodity(GnuCashDocument.CMDTY_SPACE, symbol, isin)) {
        gnucashDocument.addCommodity(
            isin
            , symbol
            , name
            , GnuCashDocument.CMDTY_SPACE
        );
    }

    // test, if currency exists already
    // else add it to the document
    // @TODO: implement this properly
    if (not gnucashUtil.hasCommodity(GnuCashDocument.CURR_SPACE, currency, "")) {
        System.out.println("missing currency: {currency}, please add it manually");
    }

    var accountID=gnucashDocument.addAccount(parentAccountID, isin, name, symbol, GnuCashDocument.CMDTY_SPACE);
    gnucashDocument.addTransaction("Kauf {isin} {name}"
        , currency
        , getDateTimeValue(valuta)
        , accountID
        , bankAccountID
        , incomeAccountID
        , feeAccountID
        , getFloatValue(shares)
        , getFloatValue(value)
        , getFloatValue(fee)
        , getFloatValue(courtage)
        , getFloatValue(amount)
    );

    // take care: commodity has to be in place before price is added
    // @TODO: improve this
    gnucashDocument.addPrice(currency, symbol, getFloatValue(price), getDateTimeValue(valuta));
}

function buyAsset() {
    var currency:String=currencies[currencySelection];
    
    // test, if currency exists already
    // else add it to the document
    // @TODO: implement this properly
    if (not gnucashUtil.hasCommodity(GnuCashDocument.CURR_SPACE, currency, "")) {
        System.out.println("missing currency: {currency}, please add it manually");
    }
    
    gnucashDocument.addTransaction("Kauf {isin} {name}"
    , currency
    , getDateTimeValue(valuta)
    , account.getID()
    , bankAccountID
    , incomeAccountID
    , feeAccountID
    , getFloatValue(shares)
    , getFloatValue(value)
    , getFloatValue(fee)
    , getFloatValue(courtage)
    , getFloatValue(amount)
    );
    
    // take care: commodity has to be in place before price is added
    // @TODO: improve this
    gnucashDocument.addPrice(currency, symbol, getFloatValue(price), getDateTimeValue(valuta));
}

function collectDividend() {
    var currency:String=currencies[currencySelection];
    
    // test, if currency exists already
    // else add it to the document
    // @TODO: implement this properly
    if (not gnucashUtil.hasCommodity(GnuCashDocument.CURR_SPACE, currency, "")) {
        System.out.println("missing currency: {currency}, please add it manually");
    }
    gnucashDocument.dividendTransaction("Dividende {isin} {name}"
    , currency
    , getDateTimeValue(valuta)
    , account.getID()
    , bankAccountID
    , incomeAccountID
    , feeAccountID
    , taxAccountID
    , getFloatValue(value)
    , getFloatValue(fee)
    , getFloatValue(courtage)
    , getFloatValue(tax)
    , getFloatValue(bruttoIncome)
    , getFloatValue(amount)
    );
}

}





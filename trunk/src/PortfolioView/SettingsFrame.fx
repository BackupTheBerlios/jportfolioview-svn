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

import javafx.ui.*;
import GnuCash.*;
import datasource.*;
import java.lang.Object;
import java.lang.System;
import java.text.DecimalFormat;
import org.joda.time.DateTime;
import org.joda.time.<<format>>.DateTimeFormat;
import org.joda.time.<<format>>.DateTimeFormatter;
import java.io.File;



class SettingsFrame extends Dialog {
    attribute rows:Row[];
    attribute columns:Column[];
    attribute gnucashUtil:GnuCashUtil;
    attribute filename:String="";
    attribute compressFile:Boolean=true;
    attribute saveAsCopy:Boolean=false;
    
    attribute currency:String="EUR";
    attribute courtage:String;
    attribute fee:String;
    attribute parentAccountID:String="";
    attribute bankAccountID:String="";
    attribute incomeAccountID="";
    attribute feeAccountID="";
    attribute taxAccountID="";
    attribute dataSourceSelection:Integer=0;
    
    
    attribute datasource1:String="";
    attribute proxyIP:String="";
    attribute proxyPort:String="";
    
    override attribute visible=false;
    override attribute modal=true;
    
    postinit {
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
        ];
        /*
        attribute SettingsFrame.rows= foreach (i in [0..10]) [
        Row {alignment: BASELINE }
        ];
        */
        columns= [
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
        
        width=360;
        height=320;
        
        content=GroupPanel {
            rows: rows
            
            columns: columns
            
            content: [
            Label {
                row: rows[0]
                column: columns[0]
                text: "Filename"
                
            }
            , TextField {
                row: rows[0]
                column: columns[1]
                toolTipText: "Define the default Gnucash-file."
                value: bind filename with inverse
            }
            , Button {
                row: rows[0]
                column: columns[2]
                toolTipText: "loop up for the Gnucash-file."
                text: "..."
                action: function() {
                    var fileChooser=FileChooser {
                        title: "Select Gnucash file"
                        files: true
                        directories: false
                        action: function(selectedFile:File) {
                            filename=selectedFile.getAbsolutePath();
                        }
                };
                fileChooser.showOpenDialog(this);
            }
    }
    , CheckBox {
        row: rows[1]
        column: columns[1]
        text: "compress Gnucash-File"
        toolTipText: "Define if the Gnucash-File is compressed."
        selected: bind compressFile with inverse
    }
    
    , CheckBox {
        row: rows[2]
        column: columns[1]
        text: "save as copy"
        toolTipText: "Define if the data is saved into a seperate file."
        selected: bind saveAsCopy with inverse
    }
    
    , Label {
        row: rows[3]
        column: columns[0]
        text: "Parent Account"
        
    }
    
    , TreeSelector {
        row: rows[3]
        column: columns[1]
        gnucashUtil: bind gnucashUtil
        value: bind parentAccountID with inverse
    }
    
    , Label {
        row: rows[4]
        column: columns[0]
        text: "Depot Account"
        
    }
    
    
    , TreeSelector {
        row: rows[4]
        column: columns[1]
        gnucashUtil: bind gnucashUtil
        value: bind bankAccountID with inverse
    }
    
    , Label {
        row: rows[5]
        column: columns[0]
        text: "Income Account"
        
    }
    
    , TreeSelector {
        row: rows[5]
        column: columns[1]
        gnucashUtil: bind gnucashUtil
        value: bind incomeAccountID with inverse
    }
    
    , Label {
        row: rows[6]
        column: columns[0]
        text: "Fee Account"
        
    }
    
    , TreeSelector {
        row: rows[6]
        column: columns[1]
        gnucashUtil: bind gnucashUtil
        value: bind feeAccountID with inverse
    }
    
    , Label {
        row: rows[7]
        column: columns[0]
        text: "Tax Account"
        
    }
    
    , TreeSelector {
        row: rows[7]
        column: columns[1]
        gnucashUtil: bind gnucashUtil
        value: bind taxAccountID with inverse
    }
    
    , Label {
        row: rows[8]
        column: columns[0]
        text: "Datasource"
        
    }
    , TextField {
        row: rows[8]
        column: columns[1]
        toolTipText: "Define the datasource file."
        value: bind datasource1 with inverse
    }
    , Button {
        row: rows[8]
        column: columns[2]
        toolTipText: "look up for the datsource file."
        text: "..."
        action: function() {
            var fileChooser=FileChooser {
                title: "Select datasource file"
                files: true
                directories: false
                action: function(selectedFile:File) {
                    datasource1=selectedFile.getAbsolutePath();
                }
            };
            fileChooser.showOpenDialog(this);
        }
    }
    
    , Label {
        row: rows[9]
        column: columns[0]
        text: "Proxy"
        
    }
    , TextField {
        row: rows[9]
        column: columns[1]
        toolTipText: "Define the proxy ip or name here."
        value: bind proxyIP with inverse
    }
    , TextField {
        row: rows[9]
        column: columns[2]
        toolTipText: "Define the proxy port here."
        value: bind proxyPort with inverse
    }

]
};



buttons= [
Button {
    row: rows[10]
    column: columns[0]
    text: "cancel"
    action: function() {
        this.hide();
    }
}

, Button {
    row: rows[10]
    column: columns[1]
    text: "save"
    action: function() {
        var settings=Settings.getInstance();
        Settings.getInstance().set("jPortfolioView","file","filename", filename);
        Settings.getInstance().set("jPortfolioView","file","compressFile", compressFile);
        Settings.getInstance().set("jPortfolioView","file","saveAsCopy", saveAsCopy);
        Settings.getInstance().set("jPortfolioView","file","datasource", datasource1);
        
        Settings.getInstance().set("jPortfolioView","Accounts","Parent", parentAccountID);
        Settings.getInstance().set("jPortfolioView","Accounts","Bank", bankAccountID);
        Settings.getInstance().set("jPortfolioView","Accounts","Income", incomeAccountID);
        Settings.getInstance().set("jPortfolioView","Accounts","Fee", feeAccountID);
        Settings.getInstance().set("jPortfolioView","Accounts","Tax", taxAccountID);
        
        Settings.getInstance().set("jPortfolioView","network","proxyIP", proxyIP);
        Settings.getInstance().set("jPortfolioView","network","proxyPort", proxyPort);
        
        Settings.getInstance().writeToFile();
        this.hide();
    }
}
];


var settings=Settings.getInstance();
filename=Settings.getInstance().get("jPortfolioView","file","filename");
compressFile= ( Settings.getInstance().get("jPortfolioView","file","compressFile").equals("true") );
saveAsCopy= ( Settings.getInstance().get("jPortfolioView","file","saveAsCopy").equals("true") );
datasource1=Settings.getInstance().get("jPortfolioView","file","datasource");

parentAccountID=Settings.getInstance().get("jPortfolioView","Accounts","Parent");
bankAccountID=Settings.getInstance().get("jPortfolioView","Accounts","Bank");
incomeAccountID=Settings.getInstance().get("jPortfolioView","Accounts","Income");
feeAccountID=Settings.getInstance().get("jPortfolioView","Accounts","Fee");
taxAccountID=Settings.getInstance().get("jPortfolioView","Accounts","Tax");

proxyIP=Settings.getInstance().get("jPortfolioView","network","proxyIP");
proxyPort=Settings.getInstance().get("jPortfolioView","network","proxyPort");

}

}

//attribute SettingsFrame.lookAndFeel:







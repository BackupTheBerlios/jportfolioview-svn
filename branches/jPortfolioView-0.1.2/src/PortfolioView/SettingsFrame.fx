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



class SettingsFrame extends Frame {
    attribute rows:Row[];
    attribute columns:Column[];
    attribute gnucashUtil:GnuCashUtil;
    //attribute gnucashAccounts:GnuCashAccount[]=bind gnucashUtil.getGnuCashAccountArr();
    attribute gnucashAccounts:GnuCashAccount[];
    attribute filename:String;
    attribute compressFile:Boolean;
    attribute saveAsCopy:Boolean;
    
    attribute currency:String;
    attribute courtage:String;
    attribute fee:String;
    attribute parentAccountSelection:Integer;
    attribute bankAccountSelection:Integer;
    attribute incomeAccountSelection:Integer;
    attribute feeAccountSelection:Integer;
    attribute taxAccountSelection:Integer;
    attribute dataSourceSelection:Integer;
    
    attribute datasource:String;
    
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
    visible=true;

    content=BorderPanel {
        
        center: GroupPanel {
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
            
            , ComboBox {
                row: rows[3]
                column: columns[1]
                cells: bind for (acc in gnucashAccounts ) [
                ComboBoxCell {
                    text: acc.getDescription()
                    value: acc.getID()
                }
                ]
                selection: bind parentAccountSelection with inverse
            }
            
            , Label {
                row: rows[4]
                column: columns[0]
                text: "Depot Account"
                
            }
            
            , ComboBox {
                row: rows[4]
                column: columns[1]
                cells: bind for (acc in gnucashAccounts ) [
                ComboBoxCell {
                    text: acc.getDescription()
                    value: acc.getID()
                }
                ]
                selection: bind bankAccountSelection with inverse
            }
            
            , Label {
                row: rows[5]
                column: columns[0]
                text: "Income Account"
                
            }
            
            , ComboBox {
                row: rows[5]
                column: columns[1]
                cells: bind for (acc in gnucashAccounts ) [
                ComboBoxCell {
                    text: acc.getDescription()
                    value: acc.getID()
                }
                ]
                selection: bind incomeAccountSelection with inverse
            }
            
            , Label {
                row: rows[6]
                column: columns[0]
                text: "Fee Account"
                
            }
            
            , ComboBox {
                row: rows[6]
                column: columns[1]
                cells: bind for (acc in gnucashAccounts ) [
                ComboBoxCell {
                    text: acc.getDescription()
                    value: acc.getID()
                }
                ]
                selection: bind feeAccountSelection with inverse
            }
            
            , Label {
                row: rows[7]
                column: columns[0]
                text: "Tax Account"
                
            }
            
            , ComboBox {
                row: rows[7]
                column: columns[1]
                cells: bind for (acc in gnucashAccounts ) [
                ComboBoxCell {
                    text: acc.getDescription()
                    value: acc.getID()
                }
                ]
                selection: bind taxAccountSelection with inverse
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
                value: bind datasource with inverse
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
                            datasource=selectedFile.getAbsolutePath();
                        }
                    };
                    fileChooser.showOpenDialog(this);
                }
            }
            
            
            ]
        }
        bottom: FlowPanel {
            alignment: Alignment.TRAILING
            content: [
            Button {
                row: rows[10]
                column: columns[0]
                text: "cancel"
                action: function() {
                    this.close();
                }
            }
            
            , Button {
                row: rows[10]
                column: columns[1]
                text: "save"
                action: function() {
                    var parentAccount:GnuCashAccount=gnucashAccounts[parentAccountSelection];
                    var bankAccount:GnuCashAccount=gnucashAccounts[bankAccountSelection];
                    var incomeAccount:GnuCashAccount=gnucashAccounts[incomeAccountSelection];
                    var feeAccount:GnuCashAccount=gnucashAccounts[feeAccountSelection];
                    var taxAccount:GnuCashAccount=gnucashAccounts[taxAccountSelection];
                    
                    //var settings=Settings.getInstance();
                    /*
                    Settings.getInstance().set("jPortfolioView","file","filename", filename);
                    Settings.getInstance().set("jPortfolioView","file","compressFile", compressFile);
                    Settings.getInstance().set("jPortfolioView","file","saveAsCopy", saveAsCopy);
                    Settings.getInstance().set("jPortfolioView","file","datasource", datasource);
                    
                    Settings.getInstance().set("jPortfolioView","Accounts","Parent", parentAccount.getID());
                    Settings.getInstance().set("jPortfolioView","Accounts","Bank", bankAccount.getID());
                    Settings.getInstance().set("jPortfolioView","Accounts","Income", incomeAccount.getID());
                    Settings.getInstance().set("jPortfolioView","Accounts","Fee", feeAccount.getID());
                    Settings.getInstance().set("jPortfolioView","Accounts","Tax", taxAccount.getID());
                    Settings.getInstance().writeToFile();
                    */
                    this.close();
                }
            }
            ]
        }
    };
    
    
    }
    
    
    function readSettings():Void {
    //var settings=Settings.getInstance();
    /*
    filename=Settings.getInstance().get("jPortfolioView","file","filename");
    compressFile= ( Settings.getInstance().get("jPortfolioView","file","compressFile").equals("true") );
    saveAsCopy= ( Settings.getInstance().get("jPortfolioView","file","saveAsCopy").equals("true") );
    datasource=Settings.getInstance().get("jPortfolioView","file","datasource");
    
    var idParentAccount=Settings.getInstance().get("jPortfolioView","Accounts","Parent");
    var idBankAccount=Settings.getInstance().get("jPortfolioView","Accounts","Bank");
    var idIncomeAccount=Settings.getInstance().get("jPortfolioView","Accounts","Income");
    var idFeeAccount=Settings.getInstance().get("jPortfolioView","Accounts","Fee");
    var idTaxAccount=Settings.getInstance().get("jPortfolioView","Accounts","Tax");
    var f1=true;
    var f2=true;
    var f3=true;
    var f4=true;
    var f5=true;
    
    //@todo: implement this with a nice bitmask instead of f1..f5
    for (acc in gnucashAccounts) {
        if (f1 and acc.getID().equals(idParentAccount)) {
            parentAccountSelection=indexof acc;
            f1=false;
        }
        
        if (f2 and acc.getID().equals(idBankAccount)) {
            bankAccountSelection=indexof acc;
            f2=false;
        }
        
        if (f3 and acc.getID()==idIncomeAccount) {
            incomeAccountSelection=indexof acc;
            f3=false;
        }
        
        if (f4 and acc.getID()==idFeeAccount) {
            feeAccountSelection=indexof acc;
            f4=false;
        }
        
        if (f5 and acc.getID()==idTaxAccount) {
            taxAccountSelection=indexof acc;
            f5=false;
        }
    }
        */
}

}

//attribute SettingsFrame.lookAndFeel:







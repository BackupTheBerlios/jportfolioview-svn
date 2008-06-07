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
import java.lang.Object;
import java.lang.System;

class TransactionTableFrameClass extends Dialog {
    attribute SelectModel:TableSelectModelClass;
    attribute selectedsymbol:String
        = bind SelectModel.SelectedTableCell.text
        on replace {
        
        var transactions=account.getTransactionArr();
        var bgColor1=Color { red: 0.95, green: 0.95, blue: 0.95 };
        var bgColor2=Color { red: 1, green: 1, blue: 1 };
        var fgColor2=Color { red: 0, green: 0, blue: 0 };
        var fgColor1=Color { red: 1, green: 0, blue: 0 };
            
        cells=for (t in transactions ) [
                    TableCell {
                        text: t.getDateStr()
                        background: if (indexof t % 2 == 1) then bgColor1 else bgColor2
                        //foreground: if (a.getIncome()<0) then fgColor1 else fgColor2
                    }
                    , TableCell {
                        text: t.getQuantity()
                        background: if (indexof t % 2 == 1) then bgColor1 else bgColor2
                        //foreground: if (a.getIncome()<0) then fgColor1 else fgColor2
                    }
                    , TableCell {
                        text: t.getValueStr()
                        background: if (indexof t % 2 == 1) then bgColor1 else bgColor2
                        //foreground: if (a.getIncome()<0) then fgColor1 else fgColor2
                    }
                    , TableCell {
                        text: t.getDescription();
                        background: if (indexof t % 2 == 1) then bgColor1 else bgColor2
                        //foreground: if (a.getIncome()<0) then fgColor1 else fgColor2
                    }
                    ];
                    show();
    }    
    
    override attribute title= bind SelectModel.SelectedTableCell.text;
    attribute gnucashutil:GnuCashUtil;
    attribute gnucashDocument:GnuCashDocument;
    attribute account:StockAccount=bind gnucashutil.getAccount(selectedsymbol);
    attribute cells:TableCell[];
    
    override attribute visible=false;
    //override attribute modal=true;
    
    postinit {
        
        width=480;
        height=320;
        
        content=BorderPanel{
            top: ToolBar {
                buttons:
                [Button{
                    text: "close Frame"
                    action: function() {
                        this.hide();
                    }
                }
                , Button{
                    text: "buy more Shares"
                    action: function() {
                        var assetFrame=AssetFrame {
                            title: "buy more Shares"
                            modeSelection: 3
                            gnucashUtil: bind gnucashutil
                            gnucashDocument: bind gnucashDocument
                            account: bind account
                        };
                        assetFrame.showDialog(this);
                        assetFrame=null;
                    }
                }
                , Button{
                    text: "collect Dividend"
                    action: function() {
                        var assetFrame=AssetFrame {
                            title: "collect Dividend"
                            modeSelection: 1
                            gnucashUtil: bind gnucashutil
                            gnucashDocument: bind gnucashDocument
                            account: bind account
                        };
                        assetFrame.showDialog(this);
                        assetFrame=null;
                    }
                }
                
                , Button {
                    text: "sell Shares"
                    action: function() {
                        var assetFrame=AssetFrame {
                            title: "sell Shares"
                            modeSelection: 2
                            gnucashUtil: bind gnucashutil
                            gnucashDocument: bind gnucashDocument
                            account: bind account
                        };
                        assetFrame.showDialog(this);
                        assetFrame=null;
                    }
                }
                ]
            }
            
            center: Table {
                columns: [
                TableColumn {
                    text: "Valuta"
                    alignment: HorizontalAlignment.TRAILING
                    width: 60
                }
                , TableColumn {
                    text: "Shares"
                    alignment: HorizontalAlignment.TRAILING
                    width: 60
                }
                , TableColumn {
                    text: "Amount"
                    alignment: HorizontalAlignment.TRAILING
                    width: 60
                }
                , TableColumn {
                    text: "Description"
                    alignment: HorizontalAlignment.LEADING
                    width: 240
                }
                ]
                cells: bind cells
                }
            };
        }
}



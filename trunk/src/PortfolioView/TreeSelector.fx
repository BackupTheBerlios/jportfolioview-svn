/*
* TreeSelector.fx
*
* Created on 08.05.2008, 18:07:31
*/

package PortfolioView;

/**
* @author are
*/
import javafx.ui.*;
import java.util.HashMap;
import java.lang.System;
import GnuCash.*;

class TreeSelector extends GroupPanel {
    attribute text:String;
    attribute value:String on replace {
        text=gnucashUtil.getGnuCashAccount(value).getDescription();
    };
    attribute gnucashUtil:GnuCashUtil;
    
    
    postinit {
        sizeToFitColumn=true;
        sizeToFitRow=true;
        autoCreateContainerGaps=false;
        autoCreateGaps=false;
        
        rows=[
        Row {alignment: Alignment.BASELINE }
        ];
        
        columns= [
        Column {
            alignment: Alignment.LEADING
            resizable: true
        }
        , Column {
            alignment: Alignment.TRAILING
            resizable: false
        }
        ];
        
        content=[
        TextField {
            row: rows[0]
            column: columns[0]
            columns: 15
            value: bind text
            editable: false
            selectOnFocus: true
            sizeToFitRow: true
            sizeToFitColumn: true
            doubleBuffered: true
        }
        
        //@todo: implement this properly
        , Button {
            row: rows[0]
            column: columns[1]
            text: ""
            toolTipText: "show tree of possible values"
            action: function():Void {
                var treeSelectorDialog=TreeSelectorDialog {
                    gnucashUtil:gnucashUtil
                    selectedAccountID: bind value with inverse
                };
                treeSelectorDialog.showDialog(this);
                //value=treeSelectorFrame.getSelectedAccountID();
                //treeSelectorFrame.dispose();
            }
        }
        ];
        sizeToFitColumn=true;
    }


}

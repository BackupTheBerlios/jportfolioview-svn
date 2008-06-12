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
import javafx.ui.*;
import org.dom4j.*;
import org.dom4j.io.*;
import org.dom4j.xpath.*;

import org.jaxen.dom4j.*;

import java.util.Vector;
import java.lang.System;
import java.io.File;

import GnuCash.*;
import datasource.*;
import charting.*;
import java.util.Date;

//@todo: implement this correctly as option
//import javax.swing.UIManager;
//import com.sun.java.swing.plaf.gtk.GTKLookAndFeel;

//UIManager.setLookAndFeel(new GTKLookAndFeel());


//try to read set filename
var filename:String=Settings.getInstance().get("jPortfolioView","file","filename");


//if there is none, select a file
if (not(new File(filename)).exists() ) {
    var selectedFile:File;
    
    var fileChooser=FileChooser {
        title: "select the Gnucash file"
        files: true
        directories: false
        action: function(selectedFile:File):Void {
            filename=selectedFile.getAbsolutePath();
        }
};
fileChooser.showOpenDialog(null);

}


var gcu:GnuCashUtil=new GnuCashUtil(filename);
var gnucashDocument=new GnuCashDocument(filename);
var selectedSymbol:String="";

System.out.println("looks good!");




var tableselectmodel=new TableSelectModelClass();

var transactiontableframe=TransactionTableFrameClass {
    SelectModel:tableselectmodel
    gnucashutil: bind gcu
    gnucashDocument: bind gnucashDocument
    visible:false;
};
transactiontableframe.hide();

var statusbar=GridPanel{
    rows: 1
    columns: 3
    cells: [
    TextField { value: "state"}
    , TextField { value: "message"}
    , TextField { value: "something else"}
    ]
};

var toolbar=ToolBar {
    buttons: [
    Button {
        text: "quit Program"
        toolTipText: "Quit the Program."
        action: function() {
            gcu=null;
            gnucashDocument=null;
            transactiontableframe=null;
            System.exit(0);
        }
        icon: Image { url: XMLTools.getResourceUrlStr("/icons/exit.png") }
        
    }
    , Button {
        text: "save Changes"
        toolTipText: "Save any changes into GnuCash file."
        action: function() {
            System.out.println("write to file");
            gnucashDocument.writeToXML(filename);
            System.out.println("well done?");
            
            gcu=new GnuCashUtil(filename);
        }
        icon: Image { url: XMLTools.getResourceUrlStr("/icons/save.png") }
    }
    
    , Button {
        text: "view Chart"
        toolTipText: "View the Chart from online datasource."
        action: function() {
            //var chartFrame=ChartFrame {
            //    symbol: tableselectmodel.SelectedTableCell.text
            //    visible: true
            //};
            Settings.setProxy();
            //var chartFrame=new charting.ChartFrame(tableselectmodel.SelectedTableCell.text);
            ChartFrame.showIt();
        }
        icon: Image { url: XMLTools.getResourceUrlStr("/icons/go-next.png") }
    }
, Button {
    text: "add Asset"
    toolTipText: "add an Asset into the Gnucash file."
    action: function() {
        var assetFrame=AssetFrame {
            title: "add an Asset"
            gnucashUtil: gcu
            gnucashDocument: gnucashDocument
        };
        assetFrame.show();
        assetFrame=null;
        
    }
    icon: Image { url: XMLTools.getResourceUrlStr("/icons/add.png") }
}
, Button {
    text: "update Prices"
    toolTipText: "update the prices into the Gnucash file."
    action: function() {
        try {
            var commodities=gcu.getCommodities();
            var dispatcher=new Dispatcher( commodities );
            dispatcher.run();
            
            var prices=dispatcher.getPriceArray();
            for (p in prices) {
                //println("add price for {p.getCommodityID()}: {p.valueStr} {p.dateStr}");
                gnucashDocument.addPrice(p);
            }
            } catch (any) {
            System.out.println(any);
        }
    }
    icon: Image { url: XMLTools.getResourceUrlStr("/icons/system-software-update.png") }
}

, Button {
    text: "analyze Datasource"
    toolTipText: "show and research the tree of a datasource."
    action: function() {
        var configFrame=ConfigFrame {};
        configFrame.show();
        configFrame=null;
        
    }
    icon: Image { url: XMLTools.getResourceUrlStr("/icons/edit-find-replace.png") }
}

, Button {
    text: "configure"
    toolTipText: "adjust some preferences."
    action: function() {
        var settingsFrame=SettingsFrame {
            title: "configure Settings"
            gnucashUtil: gcu
        };
        settingsFrame.show();
        settingsFrame=null;
    }
    icon: Image { url: XMLTools.getResourceUrlStr("/icons/preferences-system.png") }
}

]
};


var rows=[
Row {alignment: Alignment.BASELINE }
];
var columns=[
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

var apppanel=SplitPane {
    orientation: Orientation.VERTICAL
    content: [
    
    SplitView {
        content:
        GroupPanel {
            rows: rows
            
            columns: columns
            
            content: [
            Label { 
                text: "Filename" 
                row: rows[0]
                column: columns[0]
                
            }
            , TextField { 
                row: rows[0]
                column: columns[1]
                value: bind filename
                toolTipText: "Name of the loaded file."
                editable: false
            }
            /*
            * @todo: implement the online file loader
            , Button {
            row: rows[0]
            column: columns[2]
            text: "..."
            action: operation() {
            println("comming soon");
        }
    }
        */ 
        ]
    }
}
, SplitView {
    content: StockReportTable {
        tableSelectModel: bind tableselectmodel
        gnucashUtil: bind gcu
        selectedSymbol: bind selectedSymbol with inverse
    }
}
]
};

var myframe=Frame {
    width: 800
    height: 600
    title: "portfolio view"
    content: BorderPanel {
        top: toolbar
        bottom: statusbar
        center: apppanel
    }
    visible: true
};

myframe.pack();



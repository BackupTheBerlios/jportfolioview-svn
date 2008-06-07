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
import java.awt.Dimension;
import org.dom4j.*;
import org.dom4j.io.*;
import org.dom4j.util.NodeComparator;
import java.lang.System;

import datasource.*;


class ConfigFrame extends Dialog {
    attribute selectedNode:Node;
    attribute treeSelectedValue:java.lang.Object on replace{
        if(treeSelectedValue <>  null){
            var treeCell=treeSelectedValue as TreeCell;
            var uniqueXPath:String=treeCell.value as String;
            selectedNode=document.selectSingleNode(uniqueXPath);
            
            xml=selectedNode.asXML();
            xpath=selectedNode.getPath();
            text=selectedNode.getText();
            System.out.println(text);
            System.out.println(xpath);
        }
};

attribute rootTreeCell= TreeCell {
    text: "nothing";
};

attribute document:Document on replace {
    if (document<>null) {
        parseNode(rootTreeCell, document.getRootElement() );
    }
};

attribute url:String;
attribute xml:String;
attribute text:String;
attribute xpath:String;
attribute regex:String;
attribute result:String;

// todo: replace with proper initiation (perhaps "trigger on new" ?)
// --
function parseNode(p:TreeCell, n: Node):Void {
    p.value=n.getUniquePath();
    p.text=n.getName();
    
    var i= n.selectNodes("*").listIterator();
    while ( i.hasNext() )  {
        var cn=i.next() as Node;
        
        var c=TreeCell {};
        insert c into p.cells;
        
        parseNode(c, cn);
    }
}

postinit {
    width=480;
    height=360;
    visible=true;
    
    var rows:Row[]=[
    Row { alignment: Alignment.BASELINE }
    , Row { alignment: Alignment.BASELINE }
    , Row { alignment: Alignment.BASELINE }
    , Row { alignment: Alignment.BASELINE }
    , Row { alignment: Alignment.BASELINE }
    ];
    
    var columns:Column[]=[
    Column {alignment: Alignment.TRAILING }
    , Column {
        alignment: Alignment.LEADING
        resizable: true 
    }
    , Column {alignment: Alignment.LEADING }
    ];
    
    content=SplitPane {
        orientation: Orientation.HORIZONTAL
        content: [
        SplitView {
            content: Tree {
                //preferredSize: new Dimension(120,240)
                root: bind rootTreeCell as TreeCell
                selectedValue: bind treeSelectedValue with inverse
                
                doubleBuffered: true
                rootVisible: true
                showRootHandles: true
            }
            weight: 0.25
        }
        , SplitView {
            content: SplitPane {
                orientation: Orientation.VERTICAL
                content: [
                SplitView {
                    content: GroupPanel {
                        rows: rows
                        columns: columns
                        
                        content: [
                        //----------------------------
                        Label { 
                            row: rows[0]
                            column: columns[0]
                            text: "url" 
                        }
                        , TextField {
                            row: rows[0]
                            column: columns[1]
                            toolTipText: "Please insert url"
                            value: bind url with inverse
                        }
                        , Button {
                            row: rows[0]
                            column: columns[2]
                            text: "..."
                            toolTipText: "update url"
                            action: function() {
                                document=XMLTools.parseHtml(url);
                                XMLTools.writeToXML(document, "/home/are/test.xml");
                            }
                        }
                        //----------------------------
                        , Label { 
                            row: rows[1]
                            column: columns[0]
                            text: "xpath" 
                        }
                        , TextField {
                            row: rows[1]
                            column: columns[1]
                            toolTipText: "please insert the xpath"
                            value: bind xpath with inverse
                        }
                        , Button {
                            row: rows[1]
                            column: columns[2]
                            text: "..."
                            toolTipText: "select node from XPath"
                            action: function() {
                                selectedNode=document.selectSingleNode(xpath);
                                result=AbstractDataSource.getNodeValueStr(document, xpath, regex);
                            }
                        }
                        //----------------------------
                        , Label { 
                            row: rows[2]
                            column: columns[0]
                            text: "regex" 
                        }
                        , TextField {
                            row: rows[2]
                            column: columns[1]
                            toolTipText: "Please insert the regular expression"
                            value: bind regex with inverse
                        }
                        
                        //----------------------------
                        , Label { 
                            row: rows[3]
                            column: columns[0]
                            text: "result" 
                        }
                        , TextField {
                            row: rows[3]
                            column: columns[1]
                            value: bind result
                        }
                    ]
                }
                weight: 0.10
            }
            , SplitView {
                content: TextArea {
                    //preferredSize: new Dimension(240,120)
                    text: bind text
                    lineWrap: true
                    wrapStyleWord: true
                    doubleBuffered: true
                    editable: false
                }
                weight: 0.15
            }
            , SplitView {
                content: TextArea {
                    //preferredSize: new Dimension(240,120)
                    text: bind xml
                    lineWrap: true
                    wrapStyleWord: true
                    doubleBuffered: true
                    editable: false
                    
                }
                weight: 0.75
            }
            ]
        }
        weight: 0.75
    }
    ]
};
}
}


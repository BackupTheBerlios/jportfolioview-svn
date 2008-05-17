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

class SelectModel {
    attribute selectedTreeCell:MyTreeCell;
}

class MyTreeCell extends TreeCell {
    attribute selectModel:SelectModel;
    attribute node:Node on replace {
        text=node.getName();    
    };
    
    override attribute selected on replace {
        if (selected==true) {
            selectModel.selectedTreeCell=this;
        }
    };
}


class XMLPanel extends GroupPanel {
    attribute url:String;
    attribute xpath:String;
    attribute regex:String;
    attribute value:String;
    attribute document:Document;
    attribute rootTreeCell:MyTreeCell;
    attribute findtext:String;
    attribute comparator:NodeComparator=new NodeComparator();;
    
    function findTreeCell(n:Node, c:MyTreeCell):Void {
        
        if ( comparator.compare(n, c.node ) == 0 ) {
            c.selected=true;
            } else {
            for (c1 in c.cells) {
                findTreeCell(n, c1 as MyTreeCell);
            }
    }
}

postinit {
    rows= [
    Row {alignment: Alignment.BASELINE }
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
    
    content=
    [Label {
        text: "url"
        row: rows[0]
        column: columns[0]
    }
    , TextField {
        value: bind url
        row: rows[0]
        column: columns[1]
        selectOnFocus: true
    }
    , Button {
        text: "..."
        row: rows[0]
        column: columns[2]
        action: function() {
            document=XMLTools.parseHtml(url);
        }
    }
    , Label {
        text: "xpath"
        row: rows[1]
        column: columns[0]
    }
    , TextField {
        value: bind xpath
        row: rows[1]
        column: columns[1]
    }
    
    , Button {
        text: "..."
        toolTipText: "select Node from xpath"
        row: rows[1]
        column: columns[2]
        
        //@todo: implement this properly
        action: function() {
            var n:Node=document.selectSingleNode(xpath);
            if (n==null) {
                System.out.println("nothing found");
            } else {
                findTreeCell(n, rootTreeCell);
            }
        }
    }
    
    , Label {
        text: "regex"
        row: rows[2]
        column: columns[0]
    }
    , TextField {
        value: bind regex
        row: rows[2]
        column: columns[1]
    }
    
    , Label {
        text: "result"
        row: rows[3]
        column: columns[0]
    }
    , TextField {
        value: bind value
        row: rows[3]
        column: columns[1]
    }
    , Button {
        text: "..."
        row: rows[3]
        column: columns[2]
        action: function() {
            value=PriceDataSource.getNodeValueStr(document, xpath, regex);
        }
    }
    
    , Label {
        text: "find"
        row: rows[4]
        column: columns[0]
    }
    , TextField {
        value: bind findtext
        row: rows[4]
        column: columns[1]
    }
    , Button {
        text: "..."
        row: rows[4]
        column: columns[2]
        
        //@todo: implement this properly
        action: function() {
            System.out.println("missing feature");
        }
    }
    ];
    
}
}




class ConfigFrame extends Frame {
    attribute selectModel:SelectModel;
    attribute rootTreeCell:MyTreeCell;
    attribute document:Document on replace {
        parseNode(rootTreeCell, document.getRootElement() );
        pack();
        };
    attribute selectedNode:Node=bind selectModel.selectedTreeCell.node on replace {
        xml=selectedNode.asXML();
        text=selectedNode.getText();    
    };
    attribute selectedTreeCell:MyTreeCell;
    attribute url:String;
    attribute xpath:String=bind selectModel.selectedTreeCell.node.getUniquePath();
    attribute xml:String;
    attribute text:String;
    
    // todo: replace with proper initiation (perhaps "trigger on new" ?)
    // --
    function parseNode(p:MyTreeCell, n: Node):Void {
        p.node=n;
        
        var i= n.selectNodes("*").listIterator();
        while ( i.hasNext() )  {
            var cn=i.next() as Node;
            
            var c=MyTreeCell {
                selectModel: bind p.selectModel
            };
            insert c into p.cells;
            
            parseNode(c, cn);
        }
    }

postinit {
    width=480;
    height=360;
    visible=true;
    
    selectModel=new SelectModel();
    /*
    rootTreeCell=MyTreeCell {
        selectModel: selectModel
    };
        */
        
    
    content=SplitPane {
        orientation: Orientation.HORIZONTAL
        content: [
        SplitView {
            content: Tree {
                //preferredSize: new Dimension(120,240)
                root: TreeCell {
                        text: "test"
                        }
                
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
                    content: XMLPanel {
                        document: bind document
                        rootTreeCell: bind rootTreeCell
                        url: bind url
                        xpath: bind xpath
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


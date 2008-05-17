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

class AccountTreeFrame extends Frame {
    attribute gnucashUtil:GnuCashUtil;
    //attribute title="Select an account";
    //attribute visible=true;
    /*
    attribute content=Tree {
        root: TreeCell {
            text: "test"
        }
    }
        */
    
    public static function getInstance():AccountTreeFrame {
        return AccountTreeFrame {
            title: "Selelct an account"
            visible: true
            content: Tree {
                root: TreeCell {
                    text: "test"
                }
            }
        };
    }
}

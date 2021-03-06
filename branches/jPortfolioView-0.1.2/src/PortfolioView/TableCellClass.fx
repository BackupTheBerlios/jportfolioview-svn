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
import org.dom4j.Node;
import java.lang.System;

class TableCellClass extends TableCell {
    attribute SelectModel:TableSelectModelClass;
    override attribute selected on replace {
        if (selected==true) {
        SelectModel.SelectedTableCell=this;
        System.out.println(SelectModel.SelectedTableCell.text);
        }
    };
}

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

package GnuCash;

/**
 *
 * @author are
 */
public class GnuCashAccount {
    String id;
    String parentID="";
    String name;
    String type;
    
    String commodityID;
    
    public String getDescription() {
        return name + ":" + type;
    }
    
    public String toString() {
        return name + ":" + type;
    }
    
    public String getID() {
        return id;
    }
    
    public String getType() {
      return type;
    }
    
    public String getParentID() {
        return parentID;
    }
    
    public String getCommodityID() {
        return commodityID;
    }
}

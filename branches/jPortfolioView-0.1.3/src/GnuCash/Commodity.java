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
public class Commodity {
    String space;
    String id;
    String name;
    String xcode="";
    String fraction;
    
    public String getSpaceID() {
        return space.concat("::").concat(id).concat("::").concat(xcode);
    }
    
    public String getSpace() {
        return space;
    }
    
    public boolean isEqual(Commodity c1) {
        String idStr1=space.concat("::").concat(id).concat("::").concat(xcode);
        String idStr2=c1.space.concat("::").concat(c1.id).concat("::").concat(c1.xcode);
        return idStr1.equalsIgnoreCase(idStr2);
    }
    
    public boolean equals(Commodity c) {
        return space.equalsIgnoreCase(c.space) && id.equalsIgnoreCase(c.id);
    }
    
    public String getID() {
        return id;
    }
    
    public void setID(String id) {
        this.id=id;
    }
    
    public String getXCode() {
        return xcode;
    }
    
    public void setXCode(String xcode) {
        this.xcode=xcode;
    }
    
}


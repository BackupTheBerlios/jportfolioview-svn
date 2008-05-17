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

package datasource;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.dom4j.*;
import org.dom4j.io.*;

import java.util.regex.*;

public class InfoDataSource extends AbstractDataSource {

    
    HashMap<String, String> hashMap;
    String symbol;
    Node configNode;
    
    public InfoDataSource(HashMap<String, String> hashMap, String symbol) throws Exception {
        setPriority(MIN_PRIORITY);
        this.hashMap = hashMap;
        
        Document configDocument= Settings.getInstance().getDatasourceDocument();
        configNode = configDocument.selectSingleNode("/pages/page");
        uriStr = configNode.selectSingleNode("@url").getText().concat(symbol);
    }

    private void putNodeValueIntoHashMap(String key) {
        String xpath;
        String pattern;
        String result;
        
        xpath = configNode.selectSingleNode("trigger[@name='" + key + "']/@xpath").getText();
        pattern = configNode.selectSingleNode("trigger[@name='" + key + "']/@regex").getText();
        result = getDocumentValueStr(xpath, pattern);
        hashMap.put(key, result);
    }
    
    void readData() {
        try {
            putNodeValueIntoHashMap("name");
            putNodeValueIntoHashMap("symbol");
            putNodeValueIntoHashMap("isin");
        } catch (Exception ex) {
            Logger.getLogger(InfoDataSource.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

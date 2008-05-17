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

import org.dom4j.io.SAXReader;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 *
 * @author are
 */
public class TagSoupReader extends SAXReader {
    private final static String ParserClassname = "org.ccil.cowan.tagsoup.Parser";
    private final static String NamespaceStr = "http://www.w3.org/1999/xhtml";
    
    private static TagSoupReader instance;
    
    private TagSoupReader() throws SAXException {
        super(XMLReaderFactory.createXMLReader(ParserClassname), false);
    }
    
    public static TagSoupReader getInstance() throws SAXException {
	if (instance == null) {
	    instance = new TagSoupReader();
	}
	return instance;
    }
}

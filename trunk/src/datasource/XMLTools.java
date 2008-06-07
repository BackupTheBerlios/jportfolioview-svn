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


import java.io.FileOutputStream;

import org.dom4j.*;
import org.dom4j.io.*;

import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;


public class XMLTools {
    public static long writeToXML(Document doc, String Filename) {
	long errorcode=0;
	
	try {
	    OutputFormat outformat = OutputFormat.createPrettyPrint();
	    outformat.setEncoding("UTF-8");
	    FileOutputStream out = new FileOutputStream(Filename);
	    XMLWriter writer = new XMLWriter(out, outformat);
	    writer.write(doc);
	    writer.flush();
	} catch (Exception x) {
	    System.out.println(x.getMessage());
	    errorcode=x.hashCode();
	}
	return errorcode;
    }
    
    public static Document readXML(String Filename) throws Exception {
	SAXReader reader = new SAXReader(false);
	return reader.read(Filename);
    }
    
    
    public static Document readXMLResource(String ResourceName) throws DocumentException {
        Document doc;
        
            SAXReader reader = new SAXReader(false);
            doc=reader.read(System.out.getClass().getResourceAsStream(ResourceName) );
        return doc;
    }
    
    public  static Document parseHtml(String UrlStr) throws Exception {
	String ParserClassname="org.ccil.cowan.tagsoup.Parser";
	String NamespaceStr="http://www.w3.org/1999/xhtml";
	
	// use tagsoup-parser
	XMLReader parser =XMLReaderFactory.createXMLReader(ParserClassname);
	SAXReader reader = new SAXReader(parser, false);
	
	return reader.read(UrlStr);
    }
    
    public static void transformToHtml(Document doc, String xslfilename, String htmlfilename) {
	try {
	    // load the transformer using JAXP
	    TransformerFactory factory = TransformerFactory.newInstance();
	    Transformer transformer = factory.newTransformer( new StreamSource( System.out.getClass().getResource(xslfilename).getFile()  ) );
	    
	    // now lets style the given document
	    DocumentResult result = new DocumentResult();
	    transformer.transform( new DocumentSource( doc ), result );
	    
	    // write to file
	    writeToXML(result.getDocument(), htmlfilename);
	    
	    
	} catch (Exception x) {
	    System.out.println(x.getMessage());
	}
    }
    
    public static String getHomeDir() {
	String p=System.getProperty("user.home");
	String s=System.getProperty("file.separator");
	if (p.lastIndexOf(s) < p.length()) p+=s;
	return p;
    }
    
    public static String getResourceUrlStr(String resourceName) {
        String urlStr=System.out.getClass().getResource(resourceName).toExternalForm();
        return urlStr;
    }
}

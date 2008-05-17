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

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.List;

import java.net.URI;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.dom4j.*;
import org.dom4j.io.*;

import org.jaxen.dom4j.Dom4jXPath;
import java.util.regex.*;
import org.xml.sax.SAXException;

public abstract class AbstractDataSource extends Thread {

    private final static String NamespaceStr = "http://www.w3.org/1999/xhtml";
    Document doc;
    boolean IsFinished = false;
    String uriStr;


    @Override
    public void run() {
        try {
            URI uri = new URI(uriStr);
            doc = TagSoupReader.getInstance().read(uri.toURL());

            readData();

        } catch (SAXException ex) {
            Logger.getLogger(AbstractDataSource.class.getName()).log(Level.SEVERE, null, ex);
        } catch (DocumentException ex) {
            Logger.getLogger(AbstractDataSource.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MalformedURLException ex) {
            Logger.getLogger(AbstractDataSource.class.getName()).log(Level.SEVERE, null, ex);
        } catch (URISyntaxException ex) {
            Logger.getLogger(AbstractDataSource.class.getName()).log(Level.SEVERE, null, ex);
        }
        doc=null;
    }

    abstract void readData();

    public String getValueStr(String s, String pattern) {
        String r = "";

        if (s.length() > 0 && pattern.length() > 0) {
            Pattern p = Pattern.compile(pattern, Pattern.MULTILINE | Pattern.DOTALL | Pattern.UNICODE_CASE);
            Matcher m = p.matcher(s);
            if (m.find()) {
                r = m.group(1);
            }
        } else {
            r = s;
        }

        return r;
    }

    public String getDocumentValueStr(String xpath, String pattern) {
        String s = "";

        try {
            Dom4jXPath p = new Dom4jXPath(xpath);
            p.addNamespace("h", NamespaceStr);
            s = p.stringValueOf(doc);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        if (s.length() > 0 && pattern.length() > 0) {
            Pattern p = Pattern.compile(pattern, Pattern.MULTILINE | Pattern.DOTALL | Pattern.UNICODE_CASE);
            Matcher m = p.matcher(s);
            if (m.find()) {
                s = m.group(1);
            }
        }

        return s.replaceAll("^[ \\s]+|\\s{2,}+|[ \\s]+$", "");
    }

    public static String getNodeValueStr(Node n, String xpath, String pattern) {
        String s = "";

        try {
            Dom4jXPath p = new Dom4jXPath(xpath);
            p.addNamespace("h", NamespaceStr);
            s = p.stringValueOf(n);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        if (s.length() > 0 && pattern.length() > 0) {
            Pattern p = Pattern.compile(pattern, Pattern.MULTILINE | Pattern.DOTALL | Pattern.UNICODE_CASE);
            Matcher m = p.matcher(s);
            if (m.find()) {
                s = m.group(1);
            }
        }

        return s.replaceAll("^[ \\s]+|\\s{2,}+|[ \\s]+$", "");
    }

    public Node selectSingleNode(Node n, String XPathStr) throws Exception {
        Dom4jXPath p = new Dom4jXPath(XPathStr);
        p.addNamespace("h", NamespaceStr);
        return (Node) p.selectSingleNode(n);
    }

    public List selectNodes(Node n, String XPathStr) throws Exception {
        Dom4jXPath p = new Dom4jXPath(XPathStr);
        p.addNamespace("h", NamespaceStr);
        return p.selectNodes(n);
    }
}

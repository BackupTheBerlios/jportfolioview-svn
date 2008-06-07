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

import java.util.logging.Level;
import java.util.logging.Logger;
import org.dom4j.*;
import java.io.File;
import java.util.Properties;

public class Settings {

    static Settings instance;
    static String SettingsFile = "settings.xml";
    boolean shallSave = false;
    Document doc;

    public Settings() {
        try {
            doc = XMLTools.readXML(getSettingsFileName());
        } catch (Exception x) {
            doc = DocumentFactory.getInstance().createDocument();
            Element e = doc.addElement("programs");
            e = e.addElement("program");
            e.addAttribute("name", System.getProperty("ant.project.name", "none"));
            shallSave = true;
        }
    }

    public static Settings getInstance() {
        if (instance == null) {
            instance = new Settings();
        }
        return instance;
    }

    public void writeToFile() {
        if (shallSave) {
            XMLTools.writeToXML(doc, getSettingsFileName());
        }
    }

    public String get(String Program, String Module, String Option) {
        String resultStr = "";
        String XPath = "//program[@name='" + Program + "']/module[@name='" + Module + "']/option[@name='" + Option + "']";

        Node n = doc.selectSingleNode(XPath);
        if (n != null) {
            resultStr = n.getText();
        }
        return resultStr;
    }

    public void set(String Program, String Module, String Setting, String Value) {
        Node n = getSetting(Program, Module, Setting);
        if (!Value.equals(n.getText())) {
            shallSave = true;
            n.setText(Value);
        }
    }

    //@todo: is there a smarter solution for the string-bool-transfomer?
    public void set(String Program, String Module, String Setting, boolean Value) {
        Node n = getSetting(Program, Module, Setting);
        boolean previousValue = (n.getText().equals("true"));

        if (Value != previousValue) {
            shallSave = true;

            if (Value) {
                n.setText("true");
            } else {
                n.setText("false");
            }
        }
    }

    private Node getRoot() {
        String XPath = "/programs";

        Node n = doc.selectSingleNode(XPath);
        if (n == null) {
            Element e = doc.addElement("programs");

            n = (Node) e;
            shallSave = true;
        }
        return n;
    }

    private Node getProgram(String Program) {
        String XPath = "//program[@name='" + Program + "']";

        Node n = doc.selectSingleNode(XPath);
        if (n == null) {
            Element e = ((Element) getRoot()).addElement("program").addAttribute("name", Program);

            n = (Node) e;
            shallSave = true;
        }
        return n;
    }

    private Node getModule(String Program, String Module) {
        String XPath = "//program[@name='" + Program + "']/module[@name='" + Module + "']";

        Node n = doc.selectSingleNode(XPath);
        if (n == null) {
            Element e = ((Element) getProgram(Program)).addElement("module").addAttribute("name", Module);

            n = (Node) e;
            shallSave = true;
        }
        return n;
    }

    private Node getSetting(String Program, String Module, String Option) {
        String XPath = "//program[@name='" + Program + "']/module[@name='" + Module + "']/option[@name='" + Option + "']";

        Node n = doc.selectSingleNode(XPath);
        if (n == null) {
            Element e = ((Element) getModule(Program, Module)).addElement("option").addAttribute("name", Option);

            n = (Node) e;
            shallSave = true;
        }
        return n;
    }

    public String getSettingsFileName() {
        String HomeDir = System.getProperty("user.home");

        if (!HomeDir.endsWith(File.separator)) {
            HomeDir += File.separator;
        }

        String FileName = HomeDir + SettingsFile;
        File f = new File(FileName);

        if ((!shallSave) && (!f.canRead())) {
            FileName = getClass().getResource("/etc/" + SettingsFile).getFile();
        }

        System.out.println("Filename: " + FileName);
        return FileName;
    }

    public String getProjectName() {
        String ProjectName = "";
        try {
            ProjectName = System.getProperty("ant.project.name", "none");
        } catch (Exception x) {
            System.out.println(x.getMessage());
        }
        return ProjectName;
    }

    public Document getDatasourceDocument() throws Exception {
        Document doc = null;
        String filename = "";

        filename = get("jPortfolioView", "file", "datasource");
        if (filename.length() > 0 && (new File(filename).exists() )) {
            doc = XMLTools.readXML(filename);
            
        } else {
            doc = XMLTools.readXMLResource("/datasources.xml");
        }
        return doc;
    }
    
    public static void setProxy() {
        //set proxy if available
        String proxyIP=getInstance().get("jPortfolioView","network","proxyIP");
        String proxyPort=getInstance().get("jPortfolioView","network","proxyPort");
        
        if (proxyIP.length()>0) {
            // Modify system properties
            Properties sysProperties = System.getProperties();

            // Specify proxy settings
            sysProperties.put("proxyHost", proxyIP);
            sysProperties.put("proxyPort", proxyPort);
            sysProperties.put("proxySet",  "true");
        } else {
            System.getProperties().put("proxySet",  "false");
        }
    }
}

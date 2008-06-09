package charting;

import datasource.Settings;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Iterator;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.dom4j.Document;
import org.dom4j.Node;

class ChartControlPane extends JToolBar implements ActionListener, ListSelectionListener {

    private ChartCanvas chartcanvas;
    private HashMap<String, String> chartCommand;

    ChartControlPane(ChartCanvas chartcanvas) throws Exception {
        JToggleButton b;
        
        setOrientation(JToolBar.VERTICAL);
        this.chartcanvas = chartcanvas;
        //this.setLayout(new FlowLayout(FlowLayout.RIGHT));

        chartCommand = new HashMap();
        Document config = Settings.getInstance().getDatasourceDocument();
        Iterator<Node> nodeIterator = config.selectNodes("/pages/page[@mode='SYMBOL']/charts/chart").listIterator();
        while (nodeIterator.hasNext()) {
            Node node = nodeIterator.next();

            String name = node.selectSingleNode("@name").getText();
            String url = node.selectSingleNode("@url").getText();
            String description = node.selectSingleNode("@description").getText();

            chartCommand.put(name, url);
            b = new JToggleButton(name);
            b.setToolTipText(description);
            b.setActionCommand(name);
            b.addActionListener(this);

            add(b);
            
            chartcanvas.setChart(name);
        }

        addSeparator();

        b = new JToggleButton("arrow");
        b.setActionCommand("arrow");
        b.addActionListener(this);
        add(b);

        b = new JToggleButton("delete");
        b.setActionCommand("DELETE_FIGURE");
        b.addActionListener(this);
        add(b);

        b = new JToggleButton("snap");
        b.setActionCommand("snap");
        b.addActionListener(this);
        add(b);
        
        b = new JToggleButton("line");
        b.setActionCommand("drawLine");
        b.addActionListener(this);
        add(b);

        b = new JToggleButton("Trend");
        b.setActionCommand("drawTrend");
        b.addActionListener(this);
        add(b);

        b = new JToggleButton("Flag");
        b.setActionCommand("drawFlag");
        b.addActionListener(this);
        add(b);
        
        b = new JToggleButton("Fibonacci");
        b.setActionCommand("drawFibonacci");
        b.addActionListener(this);
        add(b);
    }

    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        
        if (command.equals("DELETE_FIGURE")) {
            chartcanvas.delFigure();
            chartcanvas.setDrawingMode(ChartCanvas.DRAW_FIGURE);


        } else if (command.equals("drawLine")) {
            chartcanvas.toggleDrawingMode(ChartCanvas.DRAW_LINE_START_MODE);

        } else if (command.equals("drawTrend")) {
            chartcanvas.setDrawingMode(ChartCanvas.DRAW_FIGURE);

            Point2D.Double p = new Point2D.Double(chartcanvas.getBounds().getCenterX(), chartcanvas.getBounds().getCenterX());
            chartcanvas.addFigure(new TrendFigure(p));

        } else if (command.equals("drawFibonacci")) {
            chartcanvas.setDrawingMode(ChartCanvas.DRAW_FIGURE);

            Point2D.Double p = new Point2D.Double(chartcanvas.getBounds().getCenterX(), chartcanvas.getBounds().getCenterX());
            chartcanvas.addFigure(new FibonacciFigure(p));

        }else if (command.equals("snap")) {
            chartcanvas.toggleDrawingMode(ChartCanvas.SNAP_MODE);
        } else {
           chartcanvas.setChart(chartCommand.get(command)); 
        }
        
    }

    public void valueChanged(ListSelectionEvent e) {
        JList l = (JList) e.getSource();
        String symbol = l.getSelectedValue().toString();

        chartcanvas.setSymbol(symbol);
    }

    public void setChartCanvas(ChartCanvas chartcanvas) {
        this.chartcanvas = chartcanvas;
    }
}

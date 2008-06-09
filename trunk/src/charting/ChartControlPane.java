package charting;

import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;


class ChartControlPane extends JPanel implements ActionListener, ListSelectionListener {
	private ChartCanvas chartcanvas;
	
	private final String chartcommand1="http://isht.comdirect.de/charts/large.chart?hist=1d&type=candle&asc=lin&dsc=abs&avg1=38&avg2=90&avg3=200&avgtype=simple&ind0=MACD&ind1=SRS&ind2=AROON&currency=&lSyms=";
	
	private final String chartcommand2="http://isht.comdirect.de/charts/large.chart?hist=1d&type=CONNECTLINE&asc=lin&dsc=abs&avg1=38&avg2=90&avg3=200&avgtype=simple&ind0=MACD&ind1=SRS&ind2=AROON&currency=&lSyms=";
	
	private final String chartcommand3="http://isht.comdirect.de/charts/large.chart?hist=3m&type=CONNECTLINE&asc=lin&dsc=abs&avg1=38&avg2=90&avg3=200&avgtype=simple&ind=ZIGZAG&ind0=MACD&ind1=SRS&ind2=AROSC&ind3=CMF&currency=&lSyms=";
	
	private final String chartcommand4="http://isht.comdirect.de/charts/large.chart?hist=1y&type=candle&asc=lin&dsc=abs&avg1=38&avg2=90&avg3=200&avgtype=simple&ind=ZIGZAG&ind0=MACD&ind1=SRS&ind2=AROSC&ind3=CMF&currency=&lSyms=";
	
	ChartControlPane(ChartCanvas chartcanvas) {
		this.chartcanvas=chartcanvas;
		
                setLayout(new GridLayout(5,1));
                
		JButton b1=new JButton("Snap");
		b1.setActionCommand("snap");
		b1.addActionListener(this);
				
		add(b1);
		//add(new JButton("Test2"),"nl");
		
		JButton chartbutton1=new JButton("Intraday");
		chartbutton1.setActionCommand("chart1");
		chartbutton1.addActionListener(this);
		add(chartbutton1);
		
		JButton chartbutton2=new JButton("Intraday");
		chartbutton2.setActionCommand("chart2");
		chartbutton2.addActionListener(this);
		add(chartbutton2);
		
		JButton chartbutton3=new JButton("3 Monate");
		chartbutton3.setActionCommand("chart3");
		chartbutton3.addActionListener(this);
		add(chartbutton3);
		
		JButton chartbutton4=new JButton("1 Jahr");
		chartbutton4.setActionCommand("chart4");
		chartbutton4.addActionListener(this);
		add(chartbutton4);
	}
	
	public void actionPerformed(ActionEvent e) {
		String command=e.getActionCommand();
		
		if (command.equals("chart1")) {
			chartcanvas.setChart(chartcommand1);
		} else if (command.equals("chart2")) {
			chartcanvas.setChart(chartcommand2);
		} else if (command.equals("chart3")) {
			chartcanvas.setChart(chartcommand3);
		} else if (command.equals("chart4")) {
			chartcanvas.setChart(chartcommand4);
		}
	}
	
	public void valueChanged(ListSelectionEvent e) {
		JList l=(JList) e.getSource();
		String symbol=l.getSelectedValue().toString();
		
		chartcanvas.setSymbol(symbol);
	}
	
	public void setChartCanvas(ChartCanvas chartcanvas) {
		this.chartcanvas=chartcanvas;
	}
}

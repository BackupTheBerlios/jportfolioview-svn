package charting;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JScrollPane;

public class ChartFrame extends JFrame  {
    ChartCanvas canvas;
    ChartControlPane controlpane;
    
    public ChartFrame(String symbol) {
	canvas=new ChartCanvas(symbol);
        controlpane=new ChartControlPane(canvas);
        
        JScrollPane jsp=new JScrollPane(canvas);
        setLayout(new BorderLayout());
        add(jsp, BorderLayout.CENTER);
        add(controlpane, BorderLayout.EAST);
        setVisible(true);
    }
}

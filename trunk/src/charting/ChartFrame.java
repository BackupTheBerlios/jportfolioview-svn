package charting;

import java.awt.BorderLayout;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import charting.*;

public class ChartFrame extends JFrame  {
    private ChartCanvas canvas;
    private ChartControlPane controlpane;
    private static ChartFrame instance; 
    
    private ChartFrame(String symbol) {
        try {
            canvas = new ChartCanvas(symbol);
            controlpane = new ChartControlPane(canvas);

            JScrollPane jsp = new JScrollPane(canvas);
            setLayout(new BorderLayout());
            add(jsp, BorderLayout.CENTER);
            add(controlpane, BorderLayout.EAST);
            setVisible(true);
        } catch (Exception ex) {
            Logger.getLogger(ChartFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void showIt() {
        if (instance==null) {
            instance.pack();
            instance.setVisible(true);
            instance.setAlwaysOnTop(true);
        } 
        
    }
    
    public static void set(String symbol) {
        if (instance==null) {
            instance=new ChartFrame(symbol); 
        } else {
            instance.canvas.setSymbol(symbol);
        }
        instance.pack();
    }
    
    public static void off() {
        if (instance!=null) instance.setVisible(false);
    }
    
}

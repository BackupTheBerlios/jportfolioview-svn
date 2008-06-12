/*
 * TrendFigure.java
 *
 * Created on 7. September 2006, 11:05
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package charting;

import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.text.DecimalFormat;

/**
 *
 * @author are
 */
public class FibonacciFigure extends AbstractFigure {
    
    /** Creates a new instance of TrendFigure */
    public FibonacciFigure(Point2D.Float p) {
	points.add( new Point2D.Float(p.x +25f, p.y -25f));
	points.add( new Point2D.Float(p.x -25f, p.y +25f));
    }
    
    public void paintFigure(Graphics2D g2) {
	Line2D.Float l1 =new Line2D.Float(points.get(0),points.get(1));
	
	g2.draw(l1);
        
        drawFibonacciLine(g2,1.0000f);
        drawFibonacciLine(g2,0.6180f);
        drawFibonacciLine(g2,0.5000f);
        drawFibonacciLine(g2,0.3820f);
        drawFibonacciLine(g2,0.0000f);
    }
    
    private void drawFibonacciLine(Graphics2D g2, float r) {
        float y=0;
        if (points.get(1).y > points.get(0).y) {
            y=points.get(0).y + ((points.get(1).y-points.get(0).y) * r);
        } else {
            y=points.get(1).y + (points.get(0).y-points.get(1).y) * r;
        }
        
        Point2D.Float p1=new Point2D.Float(points.get(0).x, y );
        Point2D.Float p2=new Point2D.Float(points.get(1).x, y );
        
        Line2D.Float l=new Line2D.Float(p1,p2);
        g2.draw(l);
        
        String s=DecimalFormat.getPercentInstance().format(r);
        g2.drawString(s, p1.x, p1.y );
    }
}

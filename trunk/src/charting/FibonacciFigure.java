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

/**
 *
 * @author are
 */
public class FibonacciFigure extends AbstractFigure {
    
    /** Creates a new instance of TrendFigure */
    public FibonacciFigure(Point2D p) {
	points.add( new Point2D.Double(p.getX()+25,p.getY()-25));
	points.add( new Point2D.Double(p.getX()-25,p.getY()+25));
    }
    
    public void paintFigure(Graphics2D g2) {
	Line2D.Double l1 =new Line2D.Double(points.get(0),points.get(1));
	
	g2.draw(l1);
        g2.draw(getLine100());
        g2.draw(getLine618());
        g2.draw(getLine500());
        g2.draw(getLine382());
        g2.draw(getLine000());
    }
    
    
    private Line2D.Double getLine100() {
        Point2D.Double p1=new Point2D.Double(points.get(0).x, points.get(0).y );
        Point2D.Double p2=new Point2D.Double(points.get(1).x, points.get(0).y );
        return new Line2D.Double(p1,p2);
    }
    
    private Line2D.Double getLine618() {
        double y=0;
        if (points.get(1).y > points.get(0).y) {
            y=points.get(0).y + ((points.get(1).y-points.get(0).y) * 0.618d);
        } else {
            y=points.get(1).y + (points.get(0).y-points.get(1).y) * 0.618d;
        }
        
        Point2D.Double p1=new Point2D.Double(points.get(0).x, y );
        Point2D.Double p2=new Point2D.Double(points.get(1).x, y );
        return new Line2D.Double(p1,p2);
    }
    
    private Line2D.Double getLine500() {
        double y=0;
        if (points.get(1).y > points.get(0).y) {
            y=points.get(0).y + ((points.get(1).y-points.get(0).y) * 0.500d);
        } else {
            y=points.get(1).y + (points.get(0).y-points.get(1).y) * 0.500d;
        }
        
        Point2D.Double p1=new Point2D.Double(points.get(0).x, y );
        Point2D.Double p2=new Point2D.Double(points.get(1).x, y );
        return new Line2D.Double(p1,p2);
    }
    
    private Line2D.Double getLine382() {
        double y=0;
        if (points.get(1).y > points.get(0).y) {
            y=points.get(0).y + ((points.get(1).y-points.get(0).y) * 0.382d);
        } else {
            y=points.get(1).y + (points.get(0).y-points.get(1).y) * 0.382d;
        }
        
        Point2D.Double p1=new Point2D.Double(points.get(0).x, y );
        Point2D.Double p2=new Point2D.Double(points.get(1).x, y );
        return new Line2D.Double(p1,p2);
    }
    
    private Line2D.Double getLine000() {
        Point2D.Double p1=new Point2D.Double(points.get(0).x, points.get(1).y );
        Point2D.Double p2=new Point2D.Double(points.get(1).x, points.get(1).y );
        return new Line2D.Double(p1,p2);
    }
    
}

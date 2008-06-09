/*
 * Figure.java
 *
 * Created on 7. September 2006, 10:47
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package charting;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Vector;

/**
 *
 * @author are
 */
public abstract class AbstractFigure {
    Vector<Point2D.Double> points=new Vector();
    Color color=Color.RED;
    Color selectcolor=Color.GREEN;
    
    static final int SNAP_DISTANCE=7;
    static final double ELLIPSE_RADIUS=3d;
    
    final public void paint(Graphics2D g2, boolean selected) {
	if (selected) g2.setColor( selectcolor );
	else g2.setColor( color );
	
	for (int i=0; i<points.size(); i++) {
	    Point2D.Double p=points.get(i);
	    Rectangle2D.Double e=new Rectangle2D.Double(p.getX()-ELLIPSE_RADIUS, p.getY()-ELLIPSE_RADIUS,2*ELLIPSE_RADIUS,2*ELLIPSE_RADIUS);
	    g2.draw(e);
	}
	paintFigure( g2 );
    };
    
    final public void move(double x, double y) {
	for (int i=0; i<points.size(); i++) {
	    Point2D.Double p=points.get(i);
	    p.x+=x;
	    p.y+=y;
	}
    };
    
    abstract public void paintFigure(Graphics2D g2);
    
    public Vector<Point2D.Double> getPoints() {
	return points;
    }
    
    
}

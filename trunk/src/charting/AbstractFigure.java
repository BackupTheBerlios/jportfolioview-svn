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
    Vector<Point2D.Float> points=new Vector();
    Color color=Color.RED;
    Color selectcolor=Color.GREEN;
    
    static final int SNAP_DISTANCE=7;
    static final float ELLIPSE_RADIUS=3f;
    
    final public void paint(Graphics2D g2, boolean selected) {
	if (selected) g2.setColor( selectcolor );
	else g2.setColor( color );
	
	for (int i=0; i<points.size(); i++) {
	    Point2D.Float p=points.get(i);
	    Rectangle2D.Float e=new Rectangle2D.Float(p.x -ELLIPSE_RADIUS, p.y -ELLIPSE_RADIUS,2*ELLIPSE_RADIUS,2*ELLIPSE_RADIUS);
	    g2.draw(e);
	}
	paintFigure( g2 );
    };
    
    final public void move(float x, float y) {
	for (int i=0; i<points.size(); i++) {
	    Point2D.Float p=points.get(i);
	    p.x+=x;
	    p.y+=y;
	}
    };
    
    abstract public void paintFigure(Graphics2D g2);
    
    public Vector<Point2D.Float> getPoints() {
	return points;
    }
    
    
}

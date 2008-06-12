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
public class TrendFigure extends AbstractFigure {
    
    /** Creates a new instance of TrendFigure */
    public TrendFigure(Point2D.Float p) {
	points.add( new Point2D.Float(p.x,p.y-25f));
	points.add( new Point2D.Float(p.x-25f,p.y));
	points.add( new Point2D.Float(p.x+25f,p.y+25f));
    }
    
    public void paintFigure(Graphics2D g2) {
	Line2D.Float l1 =new Line2D.Float(points.get(0),points.get(1));
	//Line2D.Float l2 =new Line2D.Float(points.get(1),points.get(2));
	Line2D.Float l3 =new Line2D.Float(getX4(), getX5());
	
	g2.draw(l1);
	//g2.draw(l2);
	g2.draw(l3);
    }
    
    public Point2D.Float getX4() {
	float x1=points.get(0).x;
	float y1=points.get(0).y;
	
	float x2=points.get(1).x;
	float y2=points.get(1).y;
	
	float x3=points.get(2).x;
	float y3=points.get(2).y;
	
	float x4=(float) ((((((x2 - x1)*(y1 - y3)) + (x3*(y2 - y1)))*(y2 - y1)) + (x1*Math.pow(x2 - x1,2))) /
		(Math.pow(y2 - y1,2) + Math.pow(x2 - x1,2)));
	
	
	
	
	float y4=((y2-y1)*(x4-x3)/(x2-x1)+y3);
	
	return new Point2D.Float(x4,y4);
    }
    
    public Point2D.Float getX5() {
	float x1=points.get(1).x;
	float y1=points.get(1).y;
	
	float x2=points.get(0).x;
	float y2=points.get(0).y;
	
	float x3=points.get(2).x;
	float y3=points.get(2).y;
	
	float x4=(float) ((((((x2 - x1)*(y1 - y3)) + (x3*(y2 - y1)))*(y2 - y1)) + (x1*Math.pow(x2 - x1,2))) /
		(Math.pow(y2 - y1,2) + Math.pow(x2 - x1,2)));
	
	float y4=((y2-y1)*(x4-x3)/(x2-x1)+y3);
	
	return new Point2D.Float(x4,y4);
    }
    
}

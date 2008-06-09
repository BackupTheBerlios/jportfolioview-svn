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
    public TrendFigure(Point2D p) {
	points.add( new Point2D.Double(p.getX(),p.getY()-5));
	points.add( new Point2D.Double(p.getX()-5,p.getY()));
	points.add( new Point2D.Double(p.getX()+5,p.getY()+5));
    }
    
    public void paintFigure(Graphics2D g2) {
	Line2D.Double l1 =new Line2D.Double(points.get(0),points.get(1));
	//Line2D.Double l2 =new Line2D.Double(points.get(1),points.get(2));
	Line2D.Double l3 =new Line2D.Double(getX4(), getX5());
	
	g2.draw(l1);
	//g2.draw(l2);
	g2.draw(l3);
    }
    
    public Point2D.Double getX4() {
	double x1=points.get(0).getX();
	double y1=points.get(0).getY();
	
	double x2=points.get(1).getX();
	double y2=points.get(1).getY();
	
	double x3=points.get(2).getX();
	double y3=points.get(2).getY();
	
	double x4=(((((x2 - x1)*(y1 - y3)) + (x3*(y2 - y1)))*(y2 - y1)) + (x1*Math.pow(x2 - x1,2))) /
		(Math.pow(y2 - y1,2) + Math.pow(x2 - x1,2));
	
	
	
	
	double y4=((y2-y1)*(x4-x3)/(x2-x1)+y3);
	
	return new Point2D.Double(x4,y4);
    }
    
    public Point2D.Double getX5() {
	double x1=points.get(1).getX();
	double y1=points.get(1).getY();
	
	double x2=points.get(0).getX();
	double y2=points.get(0).getY();
	
	double x3=points.get(2).getX();
	double y3=points.get(2).getY();
	
	double x4=(((((x2 - x1)*(y1 - y3)) + (x3*(y2 - y1)))*(y2 - y1)) + (x1*Math.pow(x2 - x1,2))) /
		(Math.pow(y2 - y1,2) + Math.pow(x2 - x1,2));
	
	double y4=((y2-y1)*(x4-x3)/(x2-x1)+y3);
	
	return new Point2D.Double(x4,y4);
    }
    
}

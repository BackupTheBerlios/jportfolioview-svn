package charting;

import java.awt.Dimension;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;


import java.net.URL;
import java.util.Vector;
import javax.swing.JPanel;
import javax.imageio.ImageIO;

/**
 *  Description of the Class
 *
 * @author    root
 */
public class ChartCanvas extends JPanel implements ActionListener, MouseListener, MouseMotionListener {

    private BufferedImage bimg = null;
    //Vector<Shape> shapelist = new Vector();
    private Vector<AbstractFigure> figurelist = new Vector();
    private AbstractFigure selectedfigure;
    private Point2D.Float selectedpoint;
    
    
    private Shape selectedshape = null;
    private int crossx = 0;
    private int crossy = 0;
    private int backgroundcolor = -1;
    private int snapcolor = -1;
    private int y1 = 0;
    private int y2 = 0;
    
    public final static int ARROW_MODE = 0;
    public final static int SNAP_MODE = 1;
    public final static int CROSSHAIR_MODE = 8;
    public final static int DRAW_LINE_END_MODE=2;
    public final static int DRAW_LINE_START_MODE=4;
    public final static int DRAW_FIGURE = 32;
    
    private int mode = ARROW_MODE;
    
    private String urlstr;
    private String symbol = "DAX.ETR";

    /**  Constructor for the ChartCanvas object */
    ChartCanvas() {
        addMouseListener(this);
        addMouseMotionListener(this);
        loadChart();
    }

    ChartCanvas(String symbol) {
        addMouseListener(this);
        addMouseMotionListener(this);

        this.symbol = symbol;

        loadChart();
    }

    private void loadChart() {
        try {
            URL url = new URL(urlstr + symbol);
            bimg = ImageIO.read(url);

            this.setPreferredSize(new Dimension(bimg.getWidth(), bimg.getHeight()));
            this.validate();
        } catch (Exception x) {
            System.out.println(x.getMessage());
        }
        repaint();
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
        loadChart();
    }

    public void setChart(String urlstr) {
        this.urlstr = urlstr;
        loadChart();
    }

    /**
     *  Description of the Method
     *
     * @param  graphics  Description of Parameter
     */
    public void paint(Graphics graphics) {
        Graphics2D g2 = (Graphics2D) graphics;
        //g2.scale(zoomfactor, zoomfactor);

        if (bimg != null) {
            g2.drawImage(bimg, 0, 0, this);        //g2.setRenderingHint(RenderingHints.)
        //g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP,0.5f));
        }
        
        if (isModeSet(CROSSHAIR_MODE)) paintCrossHair(g2);

        for (int i = 0; i < figurelist.size(); i++) {
            AbstractFigure f = figurelist.get(i);
            f.paint(g2, f.equals(selectedfigure));
        }
    }

    /**
     *  Description of the Method
     *
     * @param  graphics  Description of Parameter
     */
    public void update(Graphics graphics) {
        paint(graphics);
    }
    //----------------------------------------------------------------
    // ActionEventListener
    public void actionPerformed(ActionEvent e) {
        loadChart();
    }
    //------- MouseMotionListener
    /**
     *  Description of the Method
     *
     * @param  e  Description of Parameter
     */
    public void mouseDragged(MouseEvent e) {
        if (isModeSet(DRAW_FIGURE)) {
            if (selectedpoint != null) {
                selectedpoint.setLocation(e.getX(), e.getY());
            } else {
                selectedfigure.move(e.getX() - crossx, e.getY() - crossy);
            }
            repaint();
        }

        crossx = e.getX();
        crossy = e.getY();
    }

    /**
     *  Description of the Method
     *
     * @param  e  Description of Parameter
     */
    public void mouseMoved(MouseEvent e) {
        crossx = e.getX();

        if (snapcolor != backgroundcolor) {
            setOnSnapColor(snapcolor);
        } else {
            crossy = e.getY();
        }
        repaint();
    }
    //----------------------------------------------------------------
    // MouseListener
    public void mouseClicked(MouseEvent e) {
        if (e.getButton() == e.BUTTON1) {
            if (isModeSet(SNAP_MODE)  ) {
                snapcolor = findSnapColor();
                mode = 0;
            }
        } else {
            if (y1 == 0) {
                y1 = e.getY();
            } else if (y2 == 0) {
                y2 = e.getY();
            }
        }
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
        if (isModeSet(DRAW_FIGURE)) {
            getSnapPoint();
            repaint();
        }
    }

    public void mouseReleased(MouseEvent e) {
    }

    /**
     *  Description of the Method
     *
     * @param  g2  Description of Parameter
     */
    private void paintCrossHair(Graphics2D g2) {
        g2.drawLine(Math.min(crossx, bimg.getWidth()), 0, Math.min(crossx, bimg.getWidth()), this.getHeight());
        g2.drawLine(0, Math.min(crossy, bimg.getHeight()), bimg.getWidth(), Math.min(crossy, bimg.getHeight()));
    }

    private int findSnapColor() {
        int result = 0;
        int color = backgroundcolor;
        float saturation = 0;
        float value = 1;

        Color tcolor;
        float[] hsv;

        // look inside a 5 pixel square
        // for the strongest color with the highest saturation
        // and the lowest value

        int maxh = Math.min(crossy + 5, bimg.getHeight());
        int maxw = Math.min(crossx + 5, bimg.getWidth());

        for (int r = Math.max(crossy - 5, 0); r < maxh; r++) {
            for (int c = Math.max(crossx - 5, 0); c < maxw; c++) {
                color = bimg.getRGB(c, r);

                tcolor = new Color(color);
                hsv = Color.RGBtoHSB(tcolor.getRed(), tcolor.getGreen(), tcolor.getBlue(), null);

                if ((hsv[1] > saturation || hsv[2] < value) && color != backgroundcolor) {
                    saturation = hsv[1];
                    value = hsv[2];
                    result = color;
                }
            }
        }
        return result;
    }

    private void setOnSnapColor(int snapcolor) {
        boolean b = false;

        int maxh = Math.min(bimg.getHeight(), this.getHeight());
        int maxw = Math.min(bimg.getWidth(), this.getWidth());

        if (crossx < maxw) {
            for (int r = 0; (crossy - r > 0 || crossy + r < maxh) & !b; r++) {
                if (crossy - r > 0) {
                    if (isEqualColor(bimg.getRGB(crossx, crossy - r), snapcolor)) {
                        crossy -= r;
                        b = true;
                    }
                }

                if (crossy + r < maxh & !b) {
                    if (isEqualColor(bimg.getRGB(crossx, crossy + r), snapcolor)) {
                        crossy += r;
                        b = true;
                    }
                }
            }
        }
    }

    private boolean isEqualColor(int color1, int color2) {
        Color c1 = new Color(color1);
        Color c2 = new Color(color2);

        float[] values1 = Color.RGBtoHSB(c1.getRed(), c1.getGreen(), c1.getBlue(), null);
        float[] values2 = Color.RGBtoHSB(c2.getRed(), c2.getGreen(), c2.getBlue(), null);

        return Math.abs((values1[0] - values2[0])) < 0.02 && Math.abs((values1[2] - values2[2])) < 0.02;


    }
/*
    private void drawShapes(Graphics2D g2) {
        for (int i = 0; i < shapelist.size(); i++) {
            g2.draw(shapelist.get(i));
        }

    }
*/
    boolean isModeSet(long mode) {
        return ((this.mode & mode) == mode);
    }

    void toggleDrawingMode(long mode) {
        this.mode ^= mode;
        
        /*
        if ((mode & DRAW_LINE_START_MODE) == DRAW_LINE_START_MODE) {
            selectedshape = new Line2D.Float();
            shapelist.add(selectedshape);
        }
         */ 
    }

    public void setDrawingMode(long mode) {
        this.mode |= mode;
    }

    public void removeDrawingMode(long mode) {
        this.mode &= ~mode;
    }

    public void clearDrawingMode() {
        this.mode = ARROW_MODE;
    }

    public void addFigure(AbstractFigure fig) {
        figurelist.add(fig);
        selectedfigure = fig;

        repaint();
    }

    public void delFigure() {
        figurelist.remove(selectedfigure);
        selectedfigure = null;

        repaint();
    }

    public void getSnapPoint() {
        float d = 0f;

        for (int i = 0; i < figurelist.size(); i++) {
            AbstractFigure f = figurelist.get(i);
            Vector<Point2D.Float> points = f.getPoints();

            for (int k = 0; k < points.size(); k++) {
                float d1 = (float) points.get(k).distance(getMousePosition());
                if ((i == 0 && k == 0) || d1 < d) {
                    d = d1;
                    selectedfigure = f;
                    selectedpoint = points.get(k);
                }
            }
        }

        if (d > 7) {
            selectedpoint = null;
        }
    }
}

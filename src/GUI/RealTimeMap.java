package GUI;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JPanel;

public class RealTimeMap extends JComponent {
	private static final String defaultImagePath = "defaultImage.png";
	private Image image;
	private Canvas canvas;
	
	private ArrayList<DrawingShape> waypoints;
	private DrawingRectangle rover;
	private static final Color WAYPOINT = new Color(29,29,236);
	private static final Color ROVER = new Color(255,0,0);
	
	
	private int width, height;
	public RealTimeMap(String imPath){
		try {
			canvas = new Canvas();
			this.add(canvas);
			//canvas.addKeyListener(keyboard);	// add listener to take keyboard input
	    	Dimension size = new Dimension(200, 300);
	    	//^^^^^^ replace with pre-defined values
	    	canvas.setPreferredSize(size);
	    	canvas.setBackground(new Color(5,255,255));
			
			canvas.setMinimumSize(new Dimension(size));
			image = ImageIO.read(new File(imPath));
			rover = new DrawingRectangle(new Rectangle2D.Double(0, 0, 5, 5),ROVER);
			waypoints = new ArrayList<DrawingShape>();
		} catch (IOException e) {
			System.out.println("D:");
			e.printStackTrace();
		}
	}
	
	public RealTimeMap(){
		this(defaultImagePath);
	}
	
	@Override
	public void addKeyListener(KeyListener key)
	{
		canvas.addKeyListener(key);
		super.addKeyListener(key);
	}
	

/*
	ImageComponent mapImage;
	private ArrayList<DrawingRectangle> waypoints;
	private DrawingRectangle rover;
	private static final Color WAYPOINT = new Color(29,29,236);
	private static final Color ROVER = new Color(255,0,0);
	private static final int WIDTH = 800;
	private static final int HEIGHT = 400;
	
	public RealtimeMap(){
		waypoints = new ArrayList<DrawingRectangle>();
		
		
		//this.setBackground(new Color(178, 223, 210));
		//this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
		//this.setMinimumSize(new Dimension(WIDTH, HEIGHT));
		//this.setMaximumSize(new Dimension(WIDTH, HEIGHT));
		
		addWaypoint(20,200);
		addWaypoint(123,200);
		addWaypoint(145,70);
		//clearWaypoints();
	}
*/	
	@Override
	protected void paintComponent(Graphics g) {

		super.paintComponent(g);

		Graphics2D g2 = (Graphics2D)g;
		drawBackground(g2);
		//g.drawImage(image,0,0,null);
		//drawShapes(g2);
	}
	
	private void drawBackground(Graphics2D g2) {
		g2.setColor(getBackground());
		g2.fillRect(0,  0, getWidth(), getHeight());
	}

	/*private void drawShapes(Graphics2D g2) {
		for (DrawingShape shape : waypoints) {
			shape.draw(g2);
		}
		rover.draw(g2);
	}*/
	
	public void moveRover(int x, int y)
	{
		rover = new DrawingRectangle(new Rectangle2D.Double(x, y, 5, 5),ROVER);
		this.repaint();
	}
	
	public void addWaypoint(int x, int y)
	{
		if(x <= WIDTH && y <= HEIGHT)
		{
			waypoints.add((DrawingShape) new DrawingRectangle(new Rectangle2D.Double(x, y, 5, 5),WAYPOINT));
		}
		else
		{
			System.err.println("Incorrect input to RealtimeMap.setWaypoint");
			System.err.println("Input X: " + x + " Max Width: " + WIDTH);
			System.err.println("Input Y: " + y + " Max Height: " + HEIGHT);
		}	
	}
	
	public void clearWaypoints()
	{
		waypoints.clear();
	}
	
	/////////////////
	// Drawing Shape
	/////////////////
	
	
	interface DrawingShape {
		boolean contains(Graphics2D g2, double x, double y);
		void draw(Graphics2D g2);
		Rectangle2D getBounds(Graphics2D g2);
	}


	class DrawingRectangle {

		private Rectangle2D rect;
		private Color color;
		
		public DrawingRectangle(Rectangle2D rect, Color color) {
			this.rect = rect;
			this.color = color;
		}
		
		public Color getColor()
		{
			return color;
		}

		public void draw(Graphics2D g2) {
			g2.setColor(color);
			g2.fill(rect);
		}
		
		public Rectangle2D getBounds(Graphics2D g2) {
			return rect.getBounds2D();
		}
	}
	
}

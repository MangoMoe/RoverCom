package GUI;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import javax.swing.JPanel;

public class RealtimeMap extends JPanel {
	ImageComponent mapImage;
	private ArrayList<DrawingRectangle> waypoints;
	private DrawingRectangle rover;
	private static final Color WAYPOINT = new Color(29,29,236);
	private static final Color ROVER = new Color(255,0,0);
	private static final int WIDTH = 800;
	private static final int HEIGHT = 400;
	
	public RealtimeMap(){
		waypoints = new ArrayList<DrawingRectangle>();
		rover = new DrawingRectangle(new Rectangle2D.Double(0, 0, 5, 5),ROVER);
		
		this.setBackground(new Color(178, 223, 210));
		this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
		this.setMinimumSize(new Dimension(WIDTH, HEIGHT));
		this.setMaximumSize(new Dimension(WIDTH, HEIGHT));
		
		addWaypoint(20,200);
		addWaypoint(123,200);
		addWaypoint(145,70);
		//clearWaypoints();
	}
	
	@Override
	protected void paintComponent(Graphics g) {

		super.paintComponent(g);

		Graphics2D g2 = (Graphics2D)g;
		drawBackground(g2);

		drawShapes(g2);
	}
	
	private void drawBackground(Graphics2D g2) {
		g2.setColor(getBackground());
		g2.fillRect(0,  0, getWidth(), getHeight());
	}

	private void drawShapes(Graphics2D g2) {
		for (DrawingRectangle shape : waypoints) {
			shape.draw(g2);
		}
		rover.draw(g2);
	}
	
	public void moveRover(int x, int y)
	{
		rover = new DrawingRectangle(new Rectangle2D.Double(x, y, 5, 5),ROVER);
	}
	
	public void addWaypoint(int x, int y)
	{
		if(x <= WIDTH && y <= HEIGHT)
		{
			waypoints.add(new DrawingRectangle(new Rectangle2D.Double(x, y, 5, 5),WAYPOINT));
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
	
	
	/*interface DrawingShape {
		boolean contains(Graphics2D g2, double x, double y);
		void draw(Graphics2D g2);
		Rectangle2D getBounds(Graphics2D g2);
	}*/


	class DrawingRectangle {

		private Rectangle2D rect;
		private Color color;
		
		public DrawingRectangle(Rectangle2D rect, Color color) {
			this.rect = rect;
			this.color = color;
		}

		/*@Override
		public boolean contains(Graphics2D g2, double x, double y) {
			return rect.contains(x, y);
		}*/
		
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

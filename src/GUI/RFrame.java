package GUI;

import java.awt.BorderLayout;

import javax.swing.JFrame;

public class RFrame extends JFrame {
	private static final int DEFAULT_WIDTH = 1200;
	private static final int DEFAULT_HEIGHT = 600; 
	TopBar topBanana;
	WestPanel westBanana;
	EastPanel eastBanana;
	SouthPanel southBanana;
	RealtimeMap iceCreamScoop;
	public RFrame(){
		super();
		this.setTitle("Wall-E");
		this.setLayout(new BorderLayout());
		
		topBanana = new TopBar();
		westBanana = new WestPanel();
		eastBanana = new EastPanel();
		southBanana = new SouthPanel();
		iceCreamScoop = new RealtimeMap();
		
		this.getContentPane().add(topBanana,BorderLayout.NORTH);
		this.getContentPane().add(westBanana, BorderLayout.WEST);
		this.getContentPane().add(eastBanana, BorderLayout.EAST);
		this.getContentPane().add(southBanana, BorderLayout.SOUTH);
		this.getContentPane().add(iceCreamScoop, BorderLayout.CENTER);
		
		this.setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//makes sure program shuts down when window closed
		this.setResizable(false);
		this.pack();
		
		//System.out.println(iceCreamScoop.getWidth() + "     " + iceCreamScoop.getHeight());
	}
	
	/*public static void main(String[] args){
		JFrame frame = new RFrame();
		frame.setVisible(true);
	}*/
}




package GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.KeyListener;

import javax.swing.JFrame;

import code.HeaderType;

public class RFrame extends JFrame {
	private static final int DEFAULT_WIDTH = 200;
	private static final int DEFAULT_HEIGHT = 800; 
	TopBar topBanana;
	WestPanel westBanana;
	EastPanel eastBanana;
	SouthPanel southBanana;
	RealTimeMap iceCreamScoop;
	public RFrame(){
		super();
		this.setTitle("Wall-E");
		this.setLayout(new BorderLayout());
		
		topBanana = new TopBar();
		westBanana = new WestPanel();
		eastBanana = new EastPanel();
		southBanana = new SouthPanel();
		iceCreamScoop = new RealTimeMap();
		
		this.getContentPane().add(topBanana,BorderLayout.NORTH);
		this.getContentPane().add(westBanana, BorderLayout.WEST);
		this.getContentPane().add(eastBanana, BorderLayout.EAST);
		this.getContentPane().add(southBanana, BorderLayout.SOUTH);
		this.getContentPane().add(iceCreamScoop, BorderLayout.CENTER);
		
		this.setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//makes sure program shuts down when window closed
		//this.setResizable(false);
		this.pack();
		
		//System.out.println(iceCreamScoop.getWidth() + "     " + iceCreamScoop.getHeight());
	}
	
	@Override
	public void addKeyListener(KeyListener key)
	{
		topBanana.addKeyListener(key);
		eastBanana.addKeyListener(key);
		southBanana.addKeyListener(key);
		westBanana.addKeyListener(key);
		iceCreamScoop.addKeyListener(key);
		iceCreamScoop.requestFocusInWindow();
		
		super.addKeyListener(key);
	}
	
	public void updateDisplay()
	{
		for(HeaderType header : HeaderType.values())
		{
			switch(header.getByte() & (byte)0xF0)
			{
				case (byte)0x50:	// battery packet
					if ((header.getByte() & (byte)0x0F) > 0)	// not boost packet (invalid index to pass in
						topBanana.voltagePanel.setVoltageMeterText((int)(header.getByte() & (byte)0x0F) - 1, header.getCurrentValue());
					else	// must be a boost packet
						eastBanana.setValue(header);
					break;
				case (byte)0x20:	// arm packet
					westBanana.setValue(header);
					break;
				default:	// other header
					eastBanana.setValue(header);
					break;
			}
		}
	}
	/*public static void main(String[] args){
		JFrame frame = new RFrame();
		frame.setVisible(true);
	}*/
}




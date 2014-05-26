package GUI;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.JTextField;

public class TopBar extends JPanel {
	VoltagePanel voltagePanel;
	GPSTextpanel gpsTextPanel;
	public TopBar(){
		this.setLayout(new GridLayout(1,2));
		
		voltagePanel = new VoltagePanel();
		this.add(voltagePanel);
		
		gpsTextPanel = new GPSTextpanel();
		this.add(gpsTextPanel);
				
		//this.setBackground(new Color(0,61,245));
	}
	
	@Override
	public void addKeyListener(KeyListener key)
	{
		voltagePanel.addKeyListener(key);
		super.addKeyListener(key);
	}
}

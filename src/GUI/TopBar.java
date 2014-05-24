package GUI;
import java.awt.GridLayout;
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
				
	}
}

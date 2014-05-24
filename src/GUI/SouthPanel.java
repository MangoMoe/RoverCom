package GUI;

import javax.swing.JPanel;
import javax.swing.JTextArea;

public class SouthPanel extends JPanel {
	JTextArea console;
	public SouthPanel(){
		console = new JTextArea("THIS IS A TEST",5,50);
		console.setVisible(true);
		
		this.add(console);
	}
}

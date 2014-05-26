package GUI;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;


public class GPSTextpanel extends JPanel {
	JLabelAndJTextField currentPositionTextBox;
	JLabelAndJTextField inputPositionTextBox;
	JButton    clearButton;
	JButton    addButton;
	public GPSTextpanel(){
		
		this.setLayout(new GridLayout(2,2));
		
		currentPositionTextBox = new JLabelAndJTextField("Current Position:", "");
		currentPositionTextBox.setTextFieldEditable(false);
		inputPositionTextBox   = new JLabelAndJTextField("Input Waypoint: ", "");
		
		clearButton = new JButton();
		clearButton.setText("CLEAR ALL WAYPOINTS");
		
		addButton = new JButton();
		addButton.setText("ADD WAYPOINT");
		
		this.add(currentPositionTextBox);
		/*this.add(clearButton);
		this.add(inputPositionTextBox);
		this.add(addButton);*/
	}
	
	public void setCurrentPositionText(String position){
		currentPositionTextBox.setTextField(position);
	}
}

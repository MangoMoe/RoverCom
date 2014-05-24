package GUI;
import java.awt.GridLayout;
import java.awt.Panel;
import java.util.ArrayList;

import javax.swing.JTextField;


public class VoltagePanel extends Panel {
	private static final int NUM_METERS = 6;
	ArrayList<JLabelAndJTextField> voltageMeters;
	public VoltagePanel(){
		this.setLayout(new GridLayout(3,2));
		voltageMeters = new ArrayList<JLabelAndJTextField>();
		for(int i=0; i<NUM_METERS; i++){
			voltageMeters.add(new JLabelAndJTextField());
			voltageMeters.get(i).setLabelString("Voltage Meter " + (i+1));
			voltageMeters.get(i).setTextFieldEditable(false);
			this.add(voltageMeters.get(i));
		}
	}
	
	public void setVoltageMeterText(int index, int voltageLevel){
		voltageMeters.get(index).setTextField(Integer.toString(voltageLevel));
	}
}

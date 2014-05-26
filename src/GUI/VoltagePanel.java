package GUI;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import javax.swing.JTextField;


public class VoltagePanel extends Panel {
	private static final int NUM_METERS = 8;
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
		//this.setBackground(new Color(0,61,245));
	}
	
	public void setVoltageMeterText(int index, int voltageLevel){
		voltageMeters.get(index).setTextField(Integer.toString(voltageLevel));
	}
	
	@Override
	public void addKeyListener(KeyListener key)
	{
		for(int i = 0; i < NUM_METERS; i++)
		{
			voltageMeters.get(i).addKeyListener(key);
		}
		super.addKeyListener(key);
	}
}

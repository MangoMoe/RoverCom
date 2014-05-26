package GUI;

import java.awt.Color;
import java.awt.event.KeyListener;

import javax.swing.JPanel;
import javax.swing.JTextArea;

public class SouthPanel extends JPanel {
	JTextArea console;
	public SouthPanel(){
		console = new JTextArea("THIS IS A TEST",5,50);
		console.setVisible(true);
		console.setEditable(false);
		
		this.add(console);
		
		//this.setBackground(new Color(0,61,245));
	}
	
	@Override
	public void addKeyListener(KeyListener key)
	{
		console.addKeyListener(key);
		super.addKeyListener(key);
	}
}
package GUI;
import java.awt.GridLayout;
import java.awt.event.KeyListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;


public class JLabelAndJTextField extends JPanel {
	JLabel label;
	JTextField textField;
	public JLabelAndJTextField(){
		this(" DEFAULT_LABEL", " DEFAULT_FIELDTEXT");
	}
	
	public JLabelAndJTextField(String labelText, String fieldText){
		this.setLayout(new GridLayout(1,2));
		label = new JLabel(labelText + "->");
		textField = new JTextField(fieldText);
		this.add(label);
		this.add(textField);
	}
	
	public void setLabelString(String string){
		label.setText(string);
	}
	
	public void setTextField(String string){
		textField.setText(string);
	}
	
	public void setTextFieldEditable(boolean b){
		textField.setEditable(b);
	}
	
	@Override
	public void addKeyListener(KeyListener key)
	{
		label.addKeyListener(key);
		textField.addKeyListener(key);
		super.addKeyListener(key);
	}
}

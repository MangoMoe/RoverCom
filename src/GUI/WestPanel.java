package GUI;

import java.awt.Color;
import java.awt.event.KeyListener;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import code.HeaderType;

public class WestPanel extends JPanel {
	JTable armTable;
	TableModel armDataModel;
	
	public WestPanel(){
		
		armDataModel = new ArmTableModel();
		armTable = new JTable((TableModel) armDataModel);
		armTable.getColumnModel().getColumn(0).setMinWidth(100);
		armTable.getColumnModel().getColumn(1).setMinWidth(50);
		//armTable.getColumnModel().getColumn(2).setMinWidth(50);
		
		this.add(armTable);
		
		//this.setBackground(new Color(0,61,245));
	}
	
	@Override
	public void addKeyListener(KeyListener key)
	{
		armTable.addKeyListener(key);
		super.addKeyListener(key);
	}
	

	public void setValue(HeaderType header)
	{
		switch(header.getByte())
		{
		case (byte)0x20:	// turret command
			armDataModel.setValueAt(Integer.toString(header.getCurrentValue()), 1, 1);
			break;
		case (byte)0x21:	// shoulder command
			armDataModel.setValueAt(Integer.toString(header.getCurrentValue()), 2, 1);
			break;
		case (byte)0x22:	// elbow command
			armDataModel.setValueAt(Integer.toString(header.getCurrentValue()), 3, 1);
			break;
		case (byte)0x23:	// wrist flap command
			armDataModel.setValueAt(Integer.toString(header.getCurrentValue()), 4, 1);
			break;
		case (byte)0x24:	// wrist rotate command
			armDataModel.setValueAt(Integer.toString(header.getCurrentValue()), 5, 1);
			break;
		case (byte)0x25:	// gripper command
			int i = header.getCurrentValue();
			String str1 = "Neutral";
			if(i == 1)
				str1 = "Closing";
			else if (i == 2)
				str1 = "Opening";
			armDataModel.setValueAt(str1, 6, 1);
			break;
		case (byte)0x26:	// rotator/auger command	
			int j = header.getCurrentValue();
			String str2 = "Neutral";
			if(j == 1)
				str2 = "Reverse";
			else if (j == 2)
				str2 = "Forward";
			armDataModel.setValueAt(str2, 7, 1);
			break;
		// no arm feedback :(
		/*case (byte)0x27:	// shoulder feedback
			armDataModel.setValueAt(Integer.toString((int)((double)header.getCurrentValue() * (65535.0/4095.0))), 2, 2);
			break;
		case (byte)0x28:	// shoulder current draw
			armDataModel.setValueAt(Integer.toString(header.getCurrentValue()), 8, 2);
			break;
		case (byte)0x29:	// elbow feedback
			armDataModel.setValueAt(Integer.toString((int)((double)header.getCurrentValue() * (65535.0/4095.0))), 2, 2);
			break;
		case (byte)0x2A:	// elbow current draw
			armDataModel.setValueAt(Integer.toString(header.getCurrentValue()), 9, 2);
			break;*/
		default:	// don't recognize this packet, ignore
			break;
		}
		((AbstractTableModel) armDataModel).fireTableRowsInserted(0, 7);	// update table to reflect new values
	}
}

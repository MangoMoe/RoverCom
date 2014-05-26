package GUI;

import java.awt.Color;
import java.awt.event.KeyListener;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import code.HeaderType;

public class EastPanel extends JPanel {
	JTable miscTable;
	TableModel miscDataModel;
	
	public EastPanel(){
		
		miscDataModel = new MiscTableModel();
		miscTable = new JTable((TableModel) miscDataModel);
		miscTable.getColumnModel().getColumn(0).setMinWidth(100);
		miscTable.getColumnModel().getColumn(1).setMinWidth(50);
		this.add(miscTable);
		
		//this.setBackground(new Color(0,61,245));
	}
	@Override
	public void addKeyListener(KeyListener key)
	{
		miscTable.addKeyListener(key);
		super.addKeyListener(key);
	}
	
	public void setValue(HeaderType header)
	{
		if(header == HeaderType.driveAll)
			miscDataModel.setValueAt(Integer.toString(header.getCurrentValue()), 1, 1);
		else if(header == HeaderType.driveLeft)
			miscDataModel.setValueAt(Integer.toString(header.getCurrentValue()), 2, 1);
		else if(header == HeaderType.driveRight)
			miscDataModel.setValueAt(Integer.toString(header.getCurrentValue()), 3, 1);
		else if(header == HeaderType.gimbalYaw)
			miscDataModel.setValueAt(Integer.toString(header.getCurrentValue()), 4, 1);
		else if(header == HeaderType.gimbalPitch)
			miscDataModel.setValueAt(Integer.toString(header.getCurrentValue()), 5, 1);
		else if(header == HeaderType.camera)
			miscDataModel.setValueAt(Integer.toString(header.getCurrentValue()), 6, 1);
		else if(header == HeaderType.boost)
			miscDataModel.setValueAt(Integer.toString(header.getCurrentValue()), 7, 1);
		((AbstractTableModel) miscDataModel).fireTableRowsInserted(0, 7);	// update table to reflect new values
	}
}

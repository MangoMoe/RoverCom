package GUI;

import java.awt.event.KeyListener;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

public class WestPanel extends JPanel {
	JTable armTable;
	TableModel armDataModel;
	
	public WestPanel(){
		
		armDataModel = new ArmTableModel();
		armTable = new JTable((TableModel) armDataModel);
		//armTable.getColumnModel().getColumn(0).setWidth(300);
		this.add(armTable);
	}
	
	@Override
	public void addKeyListener(KeyListener key)
	{
		armTable.addKeyListener(key);
		super.addKeyListener(key);
	}
	
}

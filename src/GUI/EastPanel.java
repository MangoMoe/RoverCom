package GUI;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableModel;

public class EastPanel extends JPanel {
	JTable miscTable;
	TableModel miscDataModel;
	
	public EastPanel(){
		
		miscDataModel = new MiscTableModel();
		miscTable = new JTable((TableModel) miscDataModel);
		this.add(miscTable);
	}
}

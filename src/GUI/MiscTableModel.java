package GUI;

import javax.swing.table.AbstractTableModel;

public class MiscTableModel extends AbstractTableModel {

	private static final int NUM_COLUMNS =  2;
	private static final int NUM_ROWS    =  8;
	private String[][] cellData;
	
	public MiscTableModel(){
		cellData = new String[NUM_ROWS][NUM_COLUMNS];
		
		cellData[0][0] = "Thing";
		cellData[0][1] = "Value";
		
		cellData[1][0] = "All Wheels";
		cellData[2][0] = "Left Wheels";
		cellData[3][0] = "Right Wheels";
		cellData[4][0] = "Gimbal Yaw";
		cellData[5][0] = "Gimbal Pitch";
		cellData[6][0] = "Current Camera";
		cellData[7][0] = "BOOOOST!";
	}
	
	@Override
	public int getColumnCount() {
		return NUM_COLUMNS;
	}

	@Override
	public int getRowCount() {
		return NUM_ROWS;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return cellData[rowIndex][columnIndex];
	}
	
	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex){
		if(!(aValue instanceof String)){
			System.err.printf("ArmTableModel Error: Invalid call of setValueAt using a %s object at (%d,%d)", 
					aValue.getClass().toString(), rowIndex, columnIndex);
			return;
		}
		
		cellData[rowIndex][columnIndex] = (String)aValue;	
	}
}

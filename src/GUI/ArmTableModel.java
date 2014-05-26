package GUI;

import javax.swing.table.AbstractTableModel;

import code.HeaderType;

public class ArmTableModel extends AbstractTableModel {
	private static final int NUM_COLUMNS =  2;
	private static final int NUM_ROWS    =  8;
	private String[][] cellData;
	
	public ArmTableModel(){
		cellData = new String[NUM_ROWS][NUM_COLUMNS];
		
		cellData[0][0] = "OCTAN";
		cellData[0][1] = "SENDING";
		//cellData[0][2] = "RECEIVING";
		
		cellData[1][0] = "Turret Rotation";
		cellData[2][0] = "Shoulder";
		cellData[3][0] = "Elbow Position";
		cellData[4][0] = "Wrist Flap";
		cellData[5][0] = "Wrist Rotation";
		cellData[6][0] = "Claw Value";
		cellData[7][0] = "Rotator/auger";
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

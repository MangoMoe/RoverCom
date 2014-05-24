package GUI;

import javax.swing.table.AbstractTableModel;

public class ArmTableModel extends AbstractTableModel {
	private static final int NUM_COLUMNS =  3;
	private static final int NUM_ROWS    =  8;
	private String[][] cellData;
	
	public ArmTableModel(){
		cellData = new String[NUM_ROWS][NUM_COLUMNS];
		
		cellData[0][0] = "OCTAN";
		cellData[0][1] = "SENDING";
		cellData[0][2] = "RECEIVING";
		
		cellData[1][0] = "Base Rot";
		cellData[2][0] = "Shoulder";
		cellData[3][0] = "ElbowPos";
		cellData[4][0] = "WristRot";
		cellData[5][0] = "WristFlap";
		cellData[6][0] = "Claw Pos";
		cellData[7][0] = "ClawWheels";
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

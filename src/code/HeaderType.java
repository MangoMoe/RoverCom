package code;

public enum HeaderType {	// enumeration of udp packet header types for easy access and error prevention
// add data type/size required to here???
	
	// system header types
	systemConfirmation(0,0,0,(byte)0x00),
	
	// drive header types
	driveAll(1000,2000,2,(byte)0x10),  driveLeft(1000,2000,2,(byte)0x11), driveRight(1000,2000,2,(byte)0x12), drive1(1000,2000,2,(byte)0x13),
	drive2(1000,2000,2,(byte)0x14), drive3(1000,2000,2,(byte)0x15), drive4(1000,2000,2,(byte)0x16), drive5(1000,2000,2,(byte)0x17),
	drive6(1000,2000,2,(byte)0x18),
	
	// arm header types
	armTurretCommand(0,65535,2,(byte)0x20), armShoulderCommand(0,65535,2,(byte)0x21), armElbowCommand(0,65535,2,(byte)0x22), 
	armWristFlapCommand(0,65535,2,(byte)0x23), armWristRotateCommand(0,65535,2,(byte)0x24), armGripperCommand(-128,127,1,(byte)0x25),
	armRotatorCommand(-128,127,1,(byte)0x26), armShoulderFeedback(0,4095,2,(byte)0x27), armShoulderCurrent(0,255,1,(byte)0x28),
	armElbowFeedback(0,4095,2,(byte)0x29), armElbowCurrent(0,255,1,(byte)0x2A), armWristFlapFeedback(0,65535,2,(byte)0x2B),
	armWristRotateFeedback(0,65535,2,(byte)0x2C), armGripperCurrent(0,255,1,(byte)0x2D),
	
	// gimbal header types
	gimbal(0,0,0,(byte)0x30),
	
	// camera header types
	camera(0,0,0,(byte)0x30),
	
	// battery header types
	battery(0,0,0,(byte)0x30),
	
	// imu header types
	imu(0,0,0,(byte)0x30),
	
	// misc header types
	misc(0,0,0,(byte)0x30);
	
	private final int MIN_VALUE;
	private final int MAX_VALUE;
	private final int DATA_SIZE;	// in bytes
	private final byte HEADER_BYTE;
	private static HeaderType[] headerFromByte;
	
	HeaderType(int min, int max, int dataSize, byte headerByte)
	{
		HeaderType.checkIfArrayInitialized();
		MIN_VALUE = min;
		MAX_VALUE = max;
		DATA_SIZE = dataSize;
		HEADER_BYTE = headerByte;
		HeaderType.addHeaderByte(headerByte, this);
	}
	
	public static HeaderType getHeader(byte headerByte)	// return the header from byte input
	{
		return headerFromByte[(char)headerByte];
	}
	
	public int getMin()
	{
		return this.MIN_VALUE;
	}
	
	public int getMax()
	{
		return this.MAX_VALUE;
	}
	
	public int getDataSize()
	{
		return this.DATA_SIZE;
	}
	public byte getByte()
	{
		return this.HEADER_BYTE;
	}
	
	private static void addHeaderByte(byte headerByte, HeaderType header)
	{
		headerFromByte[headerByte] = header;
	}
	
	private static void checkIfArrayInitialized()
	{
		if(headerFromByte == null)
			headerFromByte = new HeaderType[0x100];
	}
}

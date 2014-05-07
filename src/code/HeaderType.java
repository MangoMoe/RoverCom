package code;

public enum HeaderType {	// enumeration of udp packet header types for easy access and error prevention
// add data type/size required to here???
	
	// system header types
	systemConfirmation(0,0,0,(byte)0x00,false,null),
	
	// drive header types
	driveAll(1000,2000,2,(byte)0x10,false,null),  driveLeft(1000,2000,2,(byte)0x11,false,null), driveRight(1000,2000,2,(byte)0x12,false,null), drive1(1000,2000,2,(byte)0x13,false,null),
	drive2(1000,2000,2,(byte)0x14,false,null), drive3(1000,2000,2,(byte)0x15,false,null), drive4(1000,2000,2,(byte)0x16,false,null), drive5(1000,2000,2,(byte)0x17,false,null),
	drive6(1000,2000,2,(byte)0x18,false,null),
	
	// arm header types
	armTurretCommand(0,65535,2,(byte)0x20,true,null), armShoulderCommand(0,65535,2,(byte)0x21,true,null), armElbowCommand(0,65535,2,(byte)0x22,true,null), 
	armWristFlapCommand(0,65535,2,(byte)0x23,true,null), armWristRotateCommand(0,65535,2,(byte)0x24,true,null), armGripperCommand(-128,127,1,(byte)0x25,false,null),
	armRotatorCommand(-128,127,1,(byte)0x26,false,null), armShoulderFeedback(0,4095,2,(byte)0x27,false,HeaderType.armShoulderCommand), armShoulderCurrent(0,255,1,(byte)0x28,false,null),
	armElbowFeedback(0,4095,2,(byte)0x29,false,HeaderType.armElbowCommand), armElbowCurrent(0,255,1,(byte)0x2A,false,null), armWristFlapFeedback(0,65535,2,(byte)0x2B,false,HeaderType.armWristFlapCommand),
	armWristRotateFeedback(0,65535,2,(byte)0x2C,false,HeaderType.armWristRotateCommand), armGripperCurrent(0,255,1,(byte)0x2D,false,HeaderType.armGripperCommand),
	
	// gimbal header types
	gimbal((byte)0x30),
	
	// camera header types
	camera((byte)0x40),
	
	// battery header types
	battery((byte)0x50),
	
	// imu header types
	imu((byte)0x60),
	
	// misc header types
	misc((byte)0x70);
	
	private final int MIN_VALUE;
	private final int MAX_VALUE;
	private final int DATA_SIZE;	// in bytes
	private final byte HEADER_BYTE;
	private final boolean ADDITIVE;	// is the value requested by keyboard or other input added to current value (i.e. an arm angle or something)
									// or set as the current value (i.e. a wheel speed or something)
	private final HeaderType LOGICAL_PAIR;	// the headertype that this headertype is an input for (i.e. armShoulderCommand is the logical pair for armShoulderFeedback
											// while armShoulderCommand's logical pair is null because it is an broadcast value) the header type referenced probably has to be initialized first in the list before this one
	private static HeaderType[] headerFromByte;
	
	private int currentValue;
	
	HeaderType(int min, int max, int dataSize, byte headerByte, boolean additive, HeaderType pairing)
	{
		HeaderType.checkIfArrayInitialized();
		MIN_VALUE = min;
		MAX_VALUE = max;
		DATA_SIZE = dataSize;
		HEADER_BYTE = headerByte;
		ADDITIVE = additive;
		if(pairing == null)
			LOGICAL_PAIR = null;
		else LOGICAL_PAIR = pairing;
		HeaderType.addHeaderByte(headerByte, this);
	}
	
	HeaderType()	// constructor for headers we haven't quite figured out yet, change header above to use other constructor before use
	{
		//HeaderType.checkIfArrayInitialized();
		// don't add this to the headerFromByte array or see if the array is initialized
		MIN_VALUE = 0;
		MAX_VALUE = 0;
		DATA_SIZE = 0;
		HEADER_BYTE = (byte) 0xFF;
		ADDITIVE = false;
		LOGICAL_PAIR = null;
		//HeaderType.addHeaderByte(headerByte, this);
	}
	
	HeaderType(byte headerByte)	// constructor for headers we haven't quite figured out yet, change header above to use other constructor before use
	{
		//HeaderType.checkIfArrayInitialized();
		// don't add this to the headerFromByte array or see if the array is initialized
		MIN_VALUE = 0;
		MAX_VALUE = 0;
		DATA_SIZE = 0;
		HEADER_BYTE = headerByte;
		ADDITIVE = false;
		LOGICAL_PAIR = null;
		//HeaderType.addHeaderByte(headerByte, this);
	}
	
	public static HeaderType getHeader(byte headerByte)	// return the header from byte input
	{
		return headerFromByte[(char)headerByte];	// chars are unsinged
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
	public boolean isAdditive()
	{
		return this.ADDITIVE;
	}
	public HeaderType getPairing()
	{
		if (this.LOGICAL_PAIR == null)
			return null;
		else return this.LOGICAL_PAIR;
	}
	public int getCurrentValue()
	{
		return currentValue;
	}
	public void setCurrentValue(int newValue)	// simple validation here, add more?
	{
		if(this.isAdditive())
		{
			if(newValue + currentValue > MAX_VALUE)
				currentValue = MAX_VALUE;
			else if(newValue + currentValue < MIN_VALUE)
				currentValue = MIN_VALUE;
			else currentValue += newValue;
		}
		else
		{
			if(newValue > MAX_VALUE)
				currentValue = MAX_VALUE;
			else if(newValue < MIN_VALUE)
				currentValue = MIN_VALUE;
			else currentValue = newValue;
		}
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

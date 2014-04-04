package code;

import java.io.*;
import java.util.Scanner;
import java.nio.ByteBuffer;
import javax.swing.JFrame;
// Highest possible value for byte:	0x7F --> 127
// Lowest possible value for byte:	-0x80 --> -128
// To use higher values, cast to byte -->  array[0] = (byte) 0xFF


import utils.TwoWayMap;

public class ServerMain 
{
	private static CommonData com;	// must be static for some reason
	private static Rover hal;
	private static final String ADDRESS = "Medallion";
	private static TwoWayMap<HeaderType, Byte> HeaderMap = new TwoWayMap<HeaderType, Byte>();	// map byte values to strings for easy encoding and decoding
	private static Keyboard keyboard = new Keyboard();
	
	
    public static void main(String[] args) throws IOException 
    {
    	// setup
    	hal = new Rover();
    	initializeHeaderMap();	// initialize map of header byte values
    	com = new CommonData();	// initialize common data
    	
    	addKeyListener(keyboard);	// need to figure this out!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    	
    	Scanner reader = new Scanner(System.in);
    	
    	// start threads
    	InputThread input = new InputThread(com);
    	input.start();
    	BroadcastThread output = new BroadcastThread(com, ADDRESS);
    	output.start();
    	
    	// logic stuff
    	boolean running = true;
    	long lastTime = System.nanoTime(); //uses nanoseconds, more precise than current time in milliseconds
        final double ns = 1000000000.0 / 60.0; //use this variable to ensure 60 times a second
        double delta = 0.0;
        while (running) //loops while game going. two parts: graphical and logical
        { 
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;
            while(delta >= 1)
            {
                update();
                delta--;
            } //limited to updating 60 times per second ---> THIS WILL MAKE SENDING CONTINUOUS STUFF EASIER
        }
    	
    	reader.nextLine();	// wait for some input on console
    	
    	/*byte[] test = com.popRecievedPacket();	// get first recieved packet (or null)
    	while(test != null)						// loop through other packets in recieved packets queue
    	{
    		test[0] = (byte) 0xFF;
    		com.addPacketToSend(test);
    		test = com.popRecievedPacket();
    	}*/
    	createPacket("arm turret command", (short)19);
    	createPacket("drive 1", (short)226);
    	try {
			Thread.sleep(2000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
    	
    	// Shutdown stuff
    	
    	input.interrupt();	// interrupt the inputThread to shut it down
    	output.interrupt();
    	
    	try {
			input.join();	// wait for other threads to close
			output.join();
		} catch (InterruptedException e) {
			System.out.println("ServerMain interrupted while attempting to join threads");
			e.printStackTrace();
		}
    	
    	reader.close();
    	System.out.println("ServerMain shutting down, goodbye.");
    }
    
    private static void initializeHeaderMap()	// set up map of strings to byte values
    {
    	
    	//enum?
    	// System: 0x00 - 0x0F
    	HeaderMap.put(HeaderType.systemConfirmation, new Byte((byte) 0x00));
    	
    	// Drive: 0x10 - 0x1F
    	HeaderMap.put(HeaderType.driveAll, new Byte((byte) 0x10));
    	HeaderMap.put(HeaderType.driveRight, new Byte((byte) 0x11));
    	HeaderMap.put(HeaderType.driveLeft, new Byte((byte) 0x12));
    	HeaderMap.put(HeaderType.drive1, new Byte((byte) 0x13));
    	HeaderMap.put(HeaderType.drive2, new Byte((byte) 0x14));
    	HeaderMap.put(HeaderType.drive3, new Byte((byte) 0x15));
    	HeaderMap.put(HeaderType.drive4, new Byte((byte) 0x16));
    	HeaderMap.put(HeaderType.drive5, new Byte((byte) 0x17));
    	HeaderMap.put(HeaderType.drive6, new Byte((byte) 0x18));
    	
    	// Arm: 0x20 - 0x2F
    	HeaderMap.put(HeaderType.armTurretCommand, new Byte((byte) 0x20));
    	HeaderMap.put(HeaderType.armShoulderCommand, new Byte((byte) 0x21));
    	HeaderMap.put(HeaderType.armElbowCommand, new Byte((byte) 0x22));
    	HeaderMap.put(HeaderType.armWristFlapCommand, new Byte((byte) 0x23));
    	HeaderMap.put(HeaderType.armWristRotateCommand, new Byte((byte) 0x24));
    	HeaderMap.put(HeaderType.armGripperCommand, new Byte((byte) 0x25));
    	HeaderMap.put(HeaderType.armRotatorCommand, new Byte((byte) 0x26));
    	HeaderMap.put(HeaderType.armShoulderFeedback, new Byte((byte) 0x27));
    	HeaderMap.put(HeaderType.armShoulderCurrent, new Byte((byte) 0x28));
    	HeaderMap.put(HeaderType.armElbowFeedback , new Byte((byte) 0x29));
    	HeaderMap.put(HeaderType.armElbowCurrent, new Byte((byte) 0x2A));
    	HeaderMap.put(HeaderType.armWristFlapFeedback, new Byte((byte) 0x2B));
    	HeaderMap.put(HeaderType.armWristRotateFeedback, new Byte((byte) 0x2C));
    	HeaderMap.put(HeaderType.armGripperCurrent, new Byte((byte) 0x2B));
    	
    	// Gimbal: 0x30-0x3F
    	HeaderMap.put(HeaderType.gimbal, new Byte((byte) 0x30));
    	
    	// Camera: 0x40 - 0x4F
    	HeaderMap.put(HeaderType.camera, new Byte((byte) 0x40));
    	
    	// Battery: 0x50 - 0x5F
    	HeaderMap.put(HeaderType.battery, new Byte((byte) 0x50));
    	
    	// IMU: 0x60 - 0x6F
    	HeaderMap.put(HeaderType.imu, new Byte((byte) 0x60));
    	
    	// Misc/laser?: 0x70 - 0x7F
    	HeaderMap.put(HeaderType.misc, new Byte((byte) 0x70));
    	
    	
    }
    
    //private static byte[] createPacket(String header, byte data){return new byte[0];}	// not needed?
    private static void createPacket(HeaderType header, short data)	// create packet creation functions // USE BIG ENDIAN FOR NOW (default)
    {
    	ByteBuffer buffer = ByteBuffer.allocate(3);	// three bytes
    	buffer.put(HeaderMap.getByte(header));
    	buffer.putShort(data);
    	buffer.flip();
    	
    	com.addPacketToSend(buffer.array());
    }
    private static void createPacket(String header, int data){}
    
    // input stuffs
    private static void update()
    {
    	keyboard.update();
    	if(keyboard.up)
    	{
    		createPacket(HeaderType.armTurretCommand, (short) 23 );
    		System.out.println("creating packet");
    	}
    }
    
}
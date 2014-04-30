package code;

import java.awt.Canvas;
import java.awt.Dimension;
import java.io.*;
import java.util.Scanner;
import java.nio.ByteBuffer;

import javax.swing.JFrame;

import utils.TwoWayMap;

//Highest possible value for byte:	0x7F --> 127
//Lowest possible value for byte:	-0x80 --> -128
//To use higher values, cast to byte -->  array[0] = (byte) 0xFF

public class ServerMain implements Runnable
{
	private static String address = "Medallion";	// default address
	
	// Threads
	InputThread input;
	BroadcastThread output;
	
	// Logic
	private static CommonData com;	// must be static for some reason
	private Rover hal;
	private static TwoWayMap<HeaderType, Byte> HeaderMap = new TwoWayMap<HeaderType, Byte>();	// map byte values to strings for easy encoding and decoding
	
	// Structure/required to run
	private Keyboard keyboard;
	private Canvas canvas;
	private JFrame frame;
	private Thread thread;
	private boolean running = false;
	
	public ServerMain()
	{
		// setup
    	hal = new Rover();
    	initializeHeaderMap();	// initialize map of header byte values
    	com = new CommonData();	// initialize common data
    	keyboard = new Keyboard();
    	canvas = new Canvas();
    	frame = new JFrame();
    	
    	canvas.addKeyListener(keyboard);	// add listener to take keyboard input
    	Dimension size = new Dimension(600, 300);
    	//^^^^^^ replace with pre-defined values
    	canvas.setPreferredSize(size);
    	
    	Scanner reader = new Scanner(System.in);	// initialize some stuff
    	
    	System.out.println("Enter name (ex HAL1) or ip address of host to broadcast to: ");
		address = reader.nextLine();
		
	}
	
	public synchronized void start()
	{
		try
		{
			// Start Threads
			input = new InputThread(com);
			input.start();
			output = new BroadcastThread(com, address);
			output.start();
			
			// Graphics type threads
			running = true;
			thread = new Thread(this, "Display"); //new thread to hold gui
	        thread.start();
		} catch(IOException e)
		{
			System.out.println("ServerMain had an IO exception while trying to start threads");
			e.printStackTrace();
		}
	}
	
	public synchronized void stop()
	{
		running = false;
		
		input.interrupt();	// interrupt the inputThread to shut it down
    	output.interrupt();
    	
    	try {
			input.join();	// wait for other threads to close
			output.join();
			thread.join();
		} catch (InterruptedException e) {
			System.out.println("ServerMain interrupted while attempting to join threads");
			e.printStackTrace();
		}
    	System.out.println("ServerMain shutting down, goodbye.");
	}
	
    public void run() //WizardBall implements runnable, so when thread started, run method runs
    { 
        long lastTime = System.nanoTime(); //uses nanoseconds, more precise than current time in milliseconds
        long timer = System.currentTimeMillis(); //timer variable for fps counter
        final double ns = 1000000000.0 / 60.0; //use this variable to ensure 60 times a second
        double delta = 0.0;
        int frames = 0, updates = 0; //first is how many frames per second, second should be 60 updates per second
        while (running) //loops while game going. two parts: graphical and logical
        { 
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;
            while(delta >= 1)
            {
                update();
                updates++;
                delta--;
            } //limited to updating 60 times per second
            //render(); //unlimited updating, called as many times as possible
//unlimited rendering???
            frames++; //increment number of frames per second
            
            
            if(System.currentTimeMillis() - timer > 1000) //if 1 second has passed
            {
                timer += 1000;//reset timer
                //System.out.println(updates + " ups, " + frames + " fps");//display fps and updates per second
                frame.setTitle("Rover Time!" + "    |   " + updates + " ups, " + frames + " fps");//display title of program and fps counter
                frames = 0;
                updates = 0;
            }
        }
        stop();
    }
	
    public static void main(String[] args)
    {
    	// instantiate new instance of this class
    	ServerMain Interface = new ServerMain();	// come up with better name?
    	
    	Interface.frame.setResizable(false);//resizing can cause graphics errors, make sure to do first
        Interface.frame.setTitle("Rover Base Station");
        Interface.frame.add(Interface.canvas);//adds instance of game to the window (can add because subclass of canvas)
        Interface.frame.pack();//set size of Interface.frame based on component (canvas size)
        Interface.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//makes sure program shuts down when window closed
        Interface.frame.setLocationRelativeTo(null);//centers window in middle of screen
        Interface.frame.setVisible(true);//makes sure window can be seen
    	
    	Interface.start();
    }
    
    //private static byte[] createPacket(String header, byte data){return new byte[0];}	// not needed?
    public static void createPacket(HeaderType header, short data)	// create packet creation functions // USE BIG ENDIAN FOR NOW (default)
    {
    	ByteBuffer buffer = ByteBuffer.allocate(3);	// three bytes
    	buffer.put(HeaderMap.getByte(header));
    	buffer.putShort(data);
    	buffer.flip();
    	
    	com.addPacketToSend(buffer.array());
    }
    public static void createPacket(HeaderType header, int data)
    {
    	ByteBuffer buffer = ByteBuffer.allocate(5);	// 5 bytes
    	buffer.put(HeaderMap.getByte(header));
    	buffer.putInt(data);
    	buffer.flip();
    	
    	com.addPacketToSend(buffer.array());
    }
    
    // input stuffs
    private void update()
    {
    	keyboard.update();
    	if(keyboard.up)
    	{
    		createPacket(HeaderType.armTurretCommand, (short) 23 );
    		System.out.println("creating packet");
    	}
    	if(keyboard.down)
    	{
    		createPacket(HeaderType.driveAll, (short) 56 );
    		System.out.println("creating packet");
    	}
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
    
}
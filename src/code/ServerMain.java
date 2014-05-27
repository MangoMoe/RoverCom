package code;

import java.awt.*;

import javax.swing.*;

import java.awt.event.*;
import java.awt.Canvas;
import java.awt.Container;
import java.awt.Dimension;
import java.io.IOException;
import java.net.DatagramPacket;
import java.nio.ByteBuffer;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities.*;

import GUI.RFrame;
import ch.aplu.xboxcontroller.XboxController;


//Highest possible value for byte:	0x7F --> 127
//Lowest possible value for byte:	-0x80 --> -128
//To use higher values, cast to byte -->  array[0] = (byte) 0xFF

public class ServerMain implements Runnable
{
	private static String address = "Medallion";	// default address
	
	// Threads
	InputThread input;
	BroadcastThread output;
	//SerialThread serialT;
	
	// Logic
	private static CommonData com;	// must be static for some reason
	private XboxController xc;
	private XboxControllerHandler handler;
	private SerialTest serial;
	
	private static boolean brake = false;	// figure out how to make this non-static
	String comPort;
	
	// Structure/required to run
	private Keyboard keyboard;
	private Thread thread;
	private boolean running = false;
	private static HeaderTypeAccessor accessor = new HeaderTypeAccessor();
	
	// GUI stuff
	private RFrame GUIFrame;
	
	
	public static boolean ControllerConnected = false;	// make not static?
	
	public ServerMain()
	{
		// setup
    	com = new CommonData();	// initialize common data
    	
    	Scanner reader = new Scanner(System.in);	// initialize some stuff
    	
    	System.out.println("Enter name (ex HAL1) or ip address of host to broadcast to: ");
		address = reader.nextLine();
		System.out.println("Enter Com Port Number (should probably be 5): ");
		comPort = reader.nextLine();
		
		reader.close();
		
    	xc = new XboxController();
    	if (!xc.isConnected())	// if xbox controller not connected tell error
        {
          JOptionPane.showMessageDialog(null, 
            "Xbox controller not connected. Press F2 at any time to update keyboard mappings.",
            "Startup Information", 
            JOptionPane.WARNING_MESSAGE);
          xc.release();
          ControllerConnected = false;
        }
    	else
    	{
    		JOptionPane.showMessageDialog(null,
    				"Xbox controller successfully connected. Press the escape key at any time to switch to keyboard input or press F1 to switch back to controller. Press F2 at any time to update input mappings.", 
    				"Startup Information",
    				JOptionPane.WARNING_MESSAGE);
    		ControllerConnected = true;
    		xc.addXboxControllerListener(XboxControllerHandler.initializeAdapter(xc));	// initialize input
    	}
    	keyboard = new Keyboard(ControllerConnected);
    	handler = new XboxControllerHandler(this);
    	
    	accessor = new HeaderTypeAccessor();
    	SwingUtilities.invokeLater(new Runnable(){
    		@Override
    		public void run(){
    			GUIFrame = new RFrame(accessor);
    			
    			GUIFrame.addKeyListener(keyboard);
    		}
    	});
    	//GUIFrame.addKeyListener(keyboard);	// add listener to take keyboard input (this overridden method adds the listener to all components of the GUIFrame)
		
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
			//serialT = new SerialThread(com);
			//serialT.start();
			
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
	
	public void startSerial()
	{

    	serial = new SerialTest(comPort);
	}
	
	public void stopSerial()
	{
		serial.close();
		serial = null;
		System.out.println("Serial Comms Stopped");
	}
	
	public synchronized void stop()
	{
		running = false;
		
		xc.release();	// release xbox controller (part of cleanup)
		
		if(serial != null)
			serial.close();	// close serial input
		
		input.interrupt();	// interrupt the inputThread to shut it down
    	output.interrupt();
    	//serialT.interrupt();
    	
    	try {
			input.join();	// wait for other threads to close
			output.join();
			//serialT.join();
			thread.join();
		} catch (InterruptedException e) {
			System.out.println("ServerMain interrupted while attempting to join threads");
			e.printStackTrace();
		}
    	System.out.println("ServerMain shutting down, goodbye.");
	}
	
    public void run() //Application implements runnable, so when thread started, run method runs
    { 
        long lastTime = System.nanoTime(); //uses nanoseconds, more precise than current time in milliseconds
        long timer = System.currentTimeMillis(); //timer variable for fps counter
        final double ns = 1000000000.0 / 10.0; //use this variable to ensure 60 times a second
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
            
            brake = false;	// reset brake every second
            
           // render(); //unlimited updating, called as many times as possible
//unlimited rendering???
            frames++; //increment number of frames per second
            
            
            if(System.currentTimeMillis() - timer > 1000) //if 1 second has passed
            {
                timer += 1000;//reset timer
                //System.out.println(updates + " ups, " + frames + " fps");//display fps and updates per second
                //GUIFrame.setTitle("Rover Time!" + "    |   " + updates + " ups, " + frames + " fps");//display title of program and fps counter
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
    	
    	
    	Interface.start();
    }
    
    public static synchronized void requestPacket(HeaderType header, int data, boolean overrideAdditivity)	//place to put packet validation logic
    {
    	if(header != null)
    	{
    		accessor.setData(header,data,overrideAdditivity);
	    	//header.setCurrentValue(data,overrideAdditivity);	// set new value
	    	data = header.getCurrentValue();	// setting value has validation, so get validated value
	    	
	    	if(header.equals(HeaderType.driveAll) && data == 1500)	// we are sending a stop packet, turn on brake
	    		brake = true;
	    	
	    	if(!(brake && (header.getByte() & (byte)0xF0) == (byte)0x10 && data != 1500))	// if we're braking don't send packets to move wheels
	    	{
		    	createPacket(header, data);
	    	}
    	}
    }
    
    
    private static void createPacket(HeaderType header, int data)
    {
    	ByteBuffer buffer = ByteBuffer.allocate(3);	// 3 bytes
    	buffer.put(header.getByte());
    	buffer.putShort((short)data);
    	buffer.flip();
    	
    	com.addPacketToSend(buffer.array());
    }
    
    //private static byte[] createPacket(String header, byte data){return new byte[0];}	// not needed?
    /*public static void createPacket(HeaderType header, short data)	// create packet creation functions // USE BIG ENDIAN FOR NOW (default)
    {
    	ByteBuffer buffer = ByteBuffer.allocate(3);	// three bytes
    	buffer.put(header.getByte());
    	buffer.putShort(data);
    	buffer.flip();
    	
    	com.addPacketToSend(buffer.array());
    }*/
    
    int getValue(byte[] a)	// get int value from byte buffer
    {
	    ByteBuffer buffer = ByteBuffer.allocate(2);	// 2 bytes
    	buffer.put(a, 1, 2);	// read last two bytes from input buffer
    	buffer.flip();
    	return (int) buffer.getShort();
    }
    
    // input stuffs
    private void update()
    {
    	keyboard.update();
    	handler.update(keyboard.getDisableController(),keyboard.getUpdateStatus());
    	//serialT.update();
    	// serial update
    	
    	if(keyboard.getSerialStop() && !keyboard.getSerialStart() && serial != null)
    	{
    		stopSerial();
    	}
    	else if(keyboard.getSerialStart() && !keyboard.getSerialStop() && serial == null)
    	{
    		startSerial();
    	}
    	
    	if(serial != null && serial.recieving)
    	{
    		ServerMain.requestPacket(HeaderType.armTurretCommand, (int)((double)(serial.turret - 75) * (65536.0 / (950.0 - 75.0))) ,true);
    		ServerMain.requestPacket(HeaderType.armShoulderCommand, (int)((double)(serial.shoulder - 465) * (65536.0 / (830.0 - 465.0))),true);
    		ServerMain.requestPacket(HeaderType.armElbowCommand, (int)((double)(serial.elbow - 160) * (65536.0 / (860.0 - 160.0))),true);
    		ServerMain.requestPacket(HeaderType.armWristRotateCommand, (int)((double)(serial.wristRotate - 200) * (65536.0 / (820.0 - 200.0))),true);
    		ServerMain.requestPacket(HeaderType.armWristFlapCommand, (int)((double)(serial.wristPitch - 200) * (65536.0 / (820.0 - 200.0))),true);
    		if(serial.gripper < 100)	// close gripper
    			ServerMain.requestPacket(HeaderType.armGripperCommand, 1, true);
    		else if (serial.gripper > 700)	// open gripper
    			ServerMain.requestPacket(HeaderType.armGripperCommand, 2, true);
    		else	// gripper neutral
    			ServerMain.requestPacket(HeaderType.armGripperCommand, 0,true);
    		// don't use gripper input yet
    	}
    	
    	// interperet all recieved packets so far
    	byte[] input = com.popRecievedPacket();
    	HeaderType header;
    	while(input != null)
    	{
	        header = HeaderType.getHeader(input[0]);
	        header.setCurrentValue(getValue(input),true);
	        /*if(header.getPairing() != null)	// set value of logical pairing
	        {
	        	header.getPairing().setCurrentValue((int)((double)header.getCurrentValue() * ((double) header.getPairing().getMax() / (double) header.getMax())));
	        }*/
	        input = com.popRecievedPacket();
    	}
    	
    }
    private void render()
    {
    	/*SwingUtilities.invokeLater(new Runnable(){
    		@Override
    		public void run(){
    			GUIFrame.updateDisplay();
    		}
    	});*/
    }
    
}
package code;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.Scanner;

import ch.aplu.xboxcontroller.XboxController;
import ch.aplu.xboxcontroller.XboxControllerAdapter;

public class Keyboard implements KeyListener
{
	// Keyboard things
    private static final int NUMKEYS = 1000;
    private static final int NUMHEADERS = 5;	// maximum number of headers per key
    private boolean[] keys = new boolean[NUMKEYS];//stores states of keys on keyboard *keyCodes (see below) might go out of this range*
    // initialize array?
    private HeaderType[][] headers = new HeaderType[NUMKEYS][NUMHEADERS];
    private int[][] values = new int[NUMKEYS][NUMHEADERS];
    //private boolean[] isShort = new boolean[NUMKEYS];
    //^^^^^^^^	maybe add this later, just keep as ints for now
    private boolean anyKeyPressed;
    
    // XboxController things
    static double leftMagnitude = 0, rightMagnitude = 0;
    static double leftDirection = 0, rightDirection = 0;
    static boolean brake = false, paused = false;
    private boolean disableController = false;
    
    final int NUMCONTROLLER = 18;
    
    static final int dpadNIndex = 0;
    static final int dpadNEIndex = 1;
    static final int dpadEIndex = 2;
    static final int dpadSEIndex = 3;
    static final int dpadSIndex = 4;
    static final int dpadSWIndex = 5;
    static final int dpadWIndex = 6;
    static final int dpadNWIndex = 7;
    static final int buttonAIndex = 8;
    static final int buttonBIndex = 9;
    static final int buttonXIndex = 10;
    static final int buttonYIndex= 11;
    static final int leftShoulderIndex = 12;
    static final int rightShoulderIndex = 13;
    static final int leftTriggerIndex = 14;
    static final int rightTriggerIndex = 15;
    static final int rightThumbButtonIndex = 16;
    static final int leftThumbButtonIndex = 17;
    
    HeaderType[][] controllerHeaders = new HeaderType[NUMCONTROLLER][NUMHEADERS];
    int[][] controllerValues = new int[NUMCONTROLLER][NUMHEADERS];
    
    HeaderType[][] leftThumbHeaders = new HeaderType[360][NUMHEADERS];
    HeaderType[][] rightThumbHeaders = new HeaderType[360][NUMHEADERS];
    
    int[][] leftThumbValues = new int[360][NUMHEADERS];
    int[][] rightThumbValues = new int[360][NUMHEADERS];
    
    
    
    public boolean up, down, left, right;
    public Keyboard(boolean controllerConnected)	// keyboard object kind of houses XboxController stuff too
    {
    	disableController = !controllerConnected;	// change default input based on controller connection
    	
    	updateKeyMappings();
    	updateControllerMappings();
    }
    private void updateKeyMappings()
    {
    	headers = new HeaderType[NUMKEYS][NUMHEADERS];	// discard old values
    	values = new int[NUMKEYS][NUMHEADERS];
    	    
    	File file = new File("keyMappings.txt");
    	Scanner reader;
    	int keycode, value;
    	HeaderType header;
    	String str;
		try {
			reader = new Scanner(file);
			
			//HeaderType test = HeaderType.valueOf(reader.next());
			while(reader.hasNextLine())	// loops through keymapping file inputting values into two arrays using keycode that can be indexed later
			{
// add case for having an X or something which means that there is no values for this keycode or something
				if(reader.hasNextInt())	// if there is an int nearby (not a comment)
				{
					keycode = reader.nextInt();	// get keycode
					for(int i = 0; i < NUMHEADERS; i++)
					{
						if(reader.hasNext() && !reader.hasNextInt())	// keep checking for expected input
						{
							try {
								str = reader.next();
								header = HeaderType.valueOf(str);
								if(reader.hasNextInt())
								{
									value = reader.nextInt();
									
									headers[keycode][i] = header;
									values[keycode][i] = value;
								}
							} catch (IllegalArgumentException e) {	// string input was not the name of a header
								reader.nextLine();	// go to next key binding
								break;	// break out of for loop
							}
						}
					}
				}
				else{reader.nextLine();}	// go to next line if no ints, i.e. this is a comment
			}
			reader.close();
		} catch (Exception e) {
			System.err.println("Problem opening keyMappings file");
			e.printStackTrace();
		}
    }
    
    private void updateControllerMappings()
    {
    	// drop old values and start new
    	controllerHeaders = new HeaderType[NUMCONTROLLER][NUMHEADERS];
        controllerValues = new int[NUMCONTROLLER][NUMHEADERS];
        
        leftThumbHeaders = new HeaderType[360][NUMHEADERS];
        rightThumbHeaders = new HeaderType[360][NUMHEADERS];
        
        leftThumbValues = new int[360][NUMHEADERS];
        rightThumbValues = new int[360][NUMHEADERS];
        
    	File file = new File("controllerMappings.txt");
    	Scanner reader;
    	int value;
    	HeaderType header;
    	String str;
		try {
			reader = new Scanner(file);
			reader.nextLine();	// skip first line
			
			for(int x = 0; x < NUMCONTROLLER; x++)	// fill in all non-joystick values
			{
				reader.next();	// skip string used to say what each value is
				if(!reader.hasNextInt())	// if there is Not an integer next
				{
					for(int i = 0; i < NUMHEADERS; i++)
					{
						if(reader.hasNext() && !reader.hasNextInt())	// keep checking for expected input
						{
							try {
								str = reader.next();
								if(str.contains(";"))	// put semicolon by it self at end of line to tell it to stop looking for new HeaderTypes
									break;	//go to next line
								header = HeaderType.valueOf(str);
								if(reader.hasNextInt())
								{
									value = reader.nextInt();
									
									controllerHeaders[x][i] = header;
									controllerValues[x][i] = value;
								}
							} catch (IllegalArgumentException e) {	// string input was not the name of a header
								reader.nextLine();	// go to next line binding
								break;	// break out of for loop
							}
						}
					}
				}
			}
			
			// Right Joystick
			reader.nextLine();	// skip line explaining that joystick values start now
			reader.nextLine();
			int start = 0, stop = 0;
			while(reader.hasNextInt())	// loop through listed numbers, taking in degree values for joystick input
			{
				start = reader.nextInt();
				if(start > 359)	// bounds checking
					start = 359;
				if(start < 0)
					start = 0;
				if(reader.hasNextInt())
				{
					stop = reader.nextInt();
					if(stop > 359)	// bounds checking
						stop = 359;
					if(stop < 0)
						stop = 0;
					for(int i = 0; i < NUMHEADERS; i++)
					{
						if(reader.hasNext() && !reader.hasNextInt())	// keep checking for expected input
						{
							try {
								str = reader.next();
								if(str.contains(";"))	// put semicolon by it self at end of line to tell it to stop looking for new HeaderTypes
									break;	//go to next line
								header = HeaderType.valueOf(str);
								if(reader.hasNextInt())
								{
									value = reader.nextInt();
									
									for(int j = start; j <= stop; j++)	// go to each index in array between two angle values and add those header types
									{
										rightThumbHeaders[j][i] = header;
										rightThumbValues[j][i] = value;
									}
								}
							} catch (IllegalArgumentException e) {	// string input was not the name of a header
								reader.nextLine();	// go to next line binding
								break;	// break out of for loop
							}
						}
					}
				}
			}
			// Left joystick
			reader.nextLine();	// skip line explaining that joystick values start now
			while(reader.hasNextInt())	// loop through listed numbers, taking in degree values for left joystick input
			{
				start = reader.nextInt();
				if(start > 359)	// bounds checking
					start = 359;
				if(start < 0)
					start = 0;
				if(reader.hasNextInt())
				{
					stop = reader.nextInt();
					if(stop > 359)	// bounds checking
						stop = 359;
					if(stop < 0)
						stop = 0;
					for(int i = 0; i < NUMHEADERS; i++)
					{
						if(reader.hasNext() && !reader.hasNextInt())	// keep checking for expected input
						{
							try {
								str = reader.next();
								if(str.contains(";"))	// put semicolon by it self at end of line to tell it to stop looking for new HeaderTypes
									break;	//go to next line
								header = HeaderType.valueOf(str);
								if(reader.hasNextInt())
								{
									value = reader.nextInt();
									
									for(int j = start; j <= stop; j++)	// go to each index in array between two angle values and add those header types
									{
										leftThumbHeaders[j][i] = header;
										leftThumbValues[j][i] = value;
									}
								}
							} catch (IllegalArgumentException e) {	// string input was not the name of a header
								reader.nextLine();	// go to next line binding
								break;	// break out of for loop
							}
						}
					}
				}
			}
			reader.close();
		} catch (Exception e) {
			System.err.println("Problem opening controllerMappings file");
			e.printStackTrace();
		}
    }
    
    public void update() //checks every cycle if key is pressed or released
    {
    	if(keys[27])
    	{
			disableController = true;
			System.out.println("Keyboard active.");
    	}
    	else if(keys[112])
    	{
			disableController = false;
			System.out.println("Controller active");
    	}
    	else if(keys[113])
    	{
			updateKeyMappings();
			updateControllerMappings();
			System.out.println("updating input mappings");
    	}
    	
    	if(disableController)	// only do key stuff if not using controller
    	{
	        up = keys[KeyEvent.VK_UP] || keys[KeyEvent.VK_W];
	        down = keys[KeyEvent.VK_DOWN] || keys[KeyEvent.VK_S];
	        left = keys[KeyEvent.VK_LEFT] || keys[KeyEvent.VK_A];
	        right = keys[KeyEvent.VK_RIGHT] || keys[KeyEvent.VK_D];
	        
	        for(int i = 0; i < NUMKEYS; i++)
	        {
	        	if(keys[i])
	        	{
	        		for(int j = 0; j < NUMHEADERS; j++)
	        		{
		        		if(headers[i][j] != null)	// only create packets for defined header values
		        		{
		        			ServerMain.requestPacket(headers[i][j], values[i][j]);
		        			//System.out.println("Packet: " + headers[i] + " value: " + values[i]);
		        			//System.out.println("Creating packet for " + KeyEvent.getKeyText(i));
		        		}
		        		anyKeyPressed = true;
	        		}
	        	}
	        }
	        if(!anyKeyPressed)
	        {
	        	ServerMain.requestPacket(HeaderType.driveAll, 1500);	// send brake packet if no keys pressed
	        }
    	}
        if(!disableController && ServerMain.ControllerConnected)
        {
	        if(!paused)
	        {
		        if((leftDirection > 270.0 || leftDirection < 90.0) && !brake)	// Left Thumb forward
		        	ServerMain.requestPacket(HeaderType.driveLeft, (int)(1500 + (250 * leftMagnitude)));
		        else if (!brake)	// Left Thumb backward
		        	ServerMain.requestPacket(HeaderType.driveLeft, (int)(1500 - (250 * leftMagnitude)));
		        else
		        	ServerMain.requestPacket(HeaderType.driveAll, 1500); // stop!!!!
		        if((rightDirection > 270.0 || rightDirection < 90.0) && !brake)	// Right Thumb Forward
		        	ServerMain.requestPacket(HeaderType.driveRight, (int)(1500 + (250 * rightMagnitude)));
		        else if (!brake)	// Right Thumb Backward
		        	ServerMain.requestPacket(HeaderType.driveRight, (int)(1500 - (250 * rightMagnitude)));
		        else
		        	ServerMain.requestPacket(HeaderType.driveAll, 1500);
	        }
	        else
	        	ServerMain.requestPacket(HeaderType.driveAll, 1500);
        }
        anyKeyPressed = false;
    }
    
    public void keyPressed(KeyEvent e)
    {
    	//System.out.println("key # " + e.getKeyCode() + " pressed: " + KeyEvent.getKeyText(e.getKeyCode()));
        keys[e.getKeyCode()] = true;//if a key is pressed, the corresponding spot in the array is set to true
    }
    
    public void keyReleased(KeyEvent e)
    {
    	//System.out.println("key # " + e.getKeyCode() + " released: " + KeyEvent.getKeyText(e.getKeyCode()));
        keys[e.getKeyCode()] = false;//if a key is released, the corresponding spot in the array is set to false
    }
    
    public void keyTyped(KeyEvent e)
    {
        
    }
    
    public static XboxControllerAdapter initializeAdapter(final XboxController xc)
    {	//http://www.aplu.ch/classdoc/xbox/ch/aplu/xboxcontroller/XboxControllerAdapter.html
    	return new XboxControllerAdapter()
    	{
    		public void leftThumbMagnitude(double magnitude)
    		{
    			leftMagnitude = magnitude;
    		}
    		public void leftThumbDirection(double direction)
    		{
    			leftDirection = direction;
    		}
    		public void rightThumbMagnitude(double magnitude)
    		{
    			rightMagnitude = magnitude;
    		}
    		public void rightThumbDirection(double direction)
    		{
    			rightDirection = direction;
    		}
    		
    		public void leftShoulder(boolean pressed)
    		{
    			brake = pressed;
    		}
    		
    		public void rightShoulder(boolean pressed)
    		{
    			brake = pressed;
    		}
    		
    		public void leftTrigger(double value)
    		{
    			if(value > 0.1)
    				brake = true;
    			else
    				brake = false;
    		}
    		
    		public void rightTrigger(double value)
    		{
    			if(value > 0.1)
    				brake = true;
    			else
    				brake = false;
    		}
    		
    		public void start(boolean pressed)
    		{
    			if(pressed)	// pressing start tells rover to hold position
    			{
    				if(paused == true)
    					paused = false;
    				else
    					paused = true;
    			}
    		}
    		
    		public void buttonA(boolean pressed)
    		{
    			ServerMain.requestPacket(HeaderType.armWristFlapCommand, 100);
    			xc.vibrate(0, 65000);
    		}
    		
    		public void buttonB(boolean pressed)
    		{
    			ServerMain.requestPacket(HeaderType.armWristFlapCommand, -100);
    			xc.vibrate(0, 0);
    		}
    	};
    }
}
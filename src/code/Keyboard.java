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
    static double leftThumbMagnitude = 0.0, rightThumbMagnitude = 0.0;
    static double leftThumbDirection = 0.0, rightThumbDirection = 0.0;
    static double leftTriggerMagnitude = 0.0, rightTriggerMagnitude = 0.0;
    static int dpadDirection = 0;	// values between 0 and 7
    static boolean paused = false,
    		dpadPressed = false,
    		buttonAPressed = false,
			buttonBPressed = false,
			buttonXPressed = false,
			buttonYPressed = false,
			leftShoulderPressed = false,
    		rightShoulderPressed = false,
    		leftThumbPressed = false,
    		rightThumbPressed = false;

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
			
			// Left joystick
			reader.nextLine();	// skip line explaining that joystick values start now
			reader.nextLine();
			int start = 0, stop = 0;
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
			// Right Joystick
			reader.nextLine();	// skip line explaining that joystick values start now
			reader.nextLine();
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
		        			ServerMain.requestPacket(headers[i][j], values[i][j],false);
		        			//System.out.println("Packet: " + headers[i] + " value: " + values[i]);
		        			//System.out.println("Creating packet for " + KeyEvent.getKeyText(i));
		        		}
		        		anyKeyPressed = true;
	        		}
	        	}
	        }
	        if(!anyKeyPressed)
	        {
	        	ServerMain.requestPacket(HeaderType.driveAll, 1500,false);	// send brake packet if no keys pressed
	        }
    	}
        if(!disableController && ServerMain.ControllerConnected)
        {
	        if(!paused)
	        {
		        if(dpadPressed)
		        {
		        	for(int i = 0; i < NUMHEADERS; i++)
		        		ServerMain.requestPacket(controllerHeaders[dpadDirection][i], controllerValues[dpadDirection][i],false);
		        }
		        if(buttonAPressed)
		        {
		        	for(int i = 0; i < NUMHEADERS; i++)
		        		ServerMain.requestPacket(controllerHeaders[buttonAIndex][i], controllerValues[buttonAIndex][i],false);
		        }
		        if(buttonBPressed)
		        {
		        	for(int i = 0; i < NUMHEADERS; i++)
		        		ServerMain.requestPacket(controllerHeaders[buttonBIndex][i], controllerValues[buttonBIndex][i],false);
		        }
		        if(buttonXPressed)
		        {
		        	for(int i = 0; i < NUMHEADERS; i++)
		        		ServerMain.requestPacket(controllerHeaders[buttonXIndex][i], controllerValues[buttonXIndex][i],false);
		        }
		        if(buttonYPressed)
		        {
		        	for(int i = 0; i < NUMHEADERS; i++)
		        		ServerMain.requestPacket(controllerHeaders[buttonYIndex][i], controllerValues[buttonYIndex][i],false);
		        }
		        if(leftShoulderPressed)
		        {
		        	for(int i = 0; i < NUMHEADERS; i++)
		        		ServerMain.requestPacket(controllerHeaders[leftShoulderIndex][i], controllerValues[leftShoulderIndex][i],false);
		        }
		        if(rightShoulderPressed)
		        {
		        	for(int i = 0; i < NUMHEADERS; i++)
		        		ServerMain.requestPacket(controllerHeaders[rightShoulderIndex][i], controllerValues[rightShoulderIndex][i],false);
		        }
		        if(leftTriggerMagnitude > 0.0)
		        {
		        	for(int i = 0; i < NUMHEADERS; i++)
		        	{
		        		if(controllerHeaders[leftTriggerIndex][i] != null)
		        		{
			        		if((controllerHeaders[leftTriggerIndex][i].getByte() & (byte)0xF0) == (byte)0x10)	// drive packet
			        			ServerMain.requestPacket(controllerHeaders[leftTriggerIndex][i], (int)(1500 + ((double)controllerValues[leftTriggerIndex][i] * leftTriggerMagnitude)),false);
			        		else
			        			ServerMain.requestPacket(controllerHeaders[leftTriggerIndex][i], (int)((double)controllerValues[leftTriggerIndex][i] * leftTriggerMagnitude),false);
		        		}
		        	}
		        }
		        if(rightTriggerMagnitude > 0.0)
		        {
		        	for(int i = 0; i < NUMHEADERS; i++)
		        	{
		        		if(controllerHeaders[rightTriggerIndex][i] != null)
		        		{
			        		if((controllerHeaders[rightTriggerIndex][i].getByte() & (byte)0xF0) == (byte)0x10)	// drive packet
			        			ServerMain.requestPacket(controllerHeaders[rightTriggerIndex][i], (int)(1500 + ((double)controllerValues[rightTriggerIndex][i] * rightTriggerMagnitude)),false);
			        		else
			        			ServerMain.requestPacket(controllerHeaders[rightTriggerIndex][i], (int)((double)controllerValues[rightTriggerIndex][i] * rightTriggerMagnitude),false);
		        		}
		        	}
		        }
		        if(leftThumbPressed)
		        {
		        	for(int i = 0; i < NUMHEADERS; i++)
		        		ServerMain.requestPacket(controllerHeaders[leftThumbButtonIndex][i], controllerValues[leftThumbButtonIndex][i],false);
		        }
		        if(rightThumbPressed)
		        {
		        	for(int i = 0; i < NUMHEADERS; i++)
		        		ServerMain.requestPacket(controllerHeaders[rightThumbButtonIndex][i], controllerValues[rightThumbButtonIndex][i],false);
		        }
		        if(leftThumbMagnitude > 0.0)
		        {
		        	for(int i = 0; i < NUMHEADERS; i++)
		        	{
		        		if(leftThumbHeaders[(int)leftThumbDirection][i] != null)
		        		{
			        		if((leftThumbHeaders[(int)leftThumbDirection][i].getByte() & (byte)0xF0) == (byte)0x10)	// drive packet
			        			ServerMain.requestPacket(leftThumbHeaders[(int)leftThumbDirection][i], (int)(1500 + ((double)leftThumbValues[(int)leftThumbDirection][i] * leftThumbMagnitude)),false);
			        		else
			        			ServerMain.requestPacket(leftThumbHeaders[(int)leftThumbDirection][i], (int)((double)leftThumbValues[(int)leftThumbDirection][i] * leftThumbMagnitude),false);
		        		}
		        	}
		        }
		        if(rightThumbMagnitude > 0.0)
		        {
		        	for(int i = 0; i < NUMHEADERS; i++)
		        	{
		        		if(rightThumbHeaders[(int)rightThumbDirection][i] != null)
		        		{
			        		if((rightThumbHeaders[(int)rightThumbDirection][i].getByte() & (byte)0xF0) == (byte)0x10)	// drive packet
			        			ServerMain.requestPacket(rightThumbHeaders[(int)rightThumbDirection][i], (int)(1500 + ((double)rightThumbValues[(int)rightThumbDirection][i] * rightThumbMagnitude)),false);
			        		else
			        			ServerMain.requestPacket(rightThumbHeaders[(int)rightThumbDirection][i], (int)((double)rightThumbValues[(int)rightThumbDirection][i] * rightThumbMagnitude),false);
		        		}
		        	}
		        }
	        }
	        else
	        	ServerMain.requestPacket(HeaderType.driveAll, 1500,false);
        }
        anyKeyPressed = false;
    }
    
    @Override
    public void keyPressed(KeyEvent e)
    {
    	//System.out.println("key # " + e.getKeyCode() + " pressed: " + KeyEvent.getKeyText(e.getKeyCode()));
        keys[e.getKeyCode()] = true;//if a key is pressed, the corresponding spot in the array is set to true
    }
    
    @Override
    public void keyReleased(KeyEvent e)
    {
    	//System.out.println("key # " + e.getKeyCode() + " released: " + KeyEvent.getKeyText(e.getKeyCode()));
        keys[e.getKeyCode()] = false;//if a key is released, the corresponding spot in the array is set to false
    }
    
    @Override
    public void keyTyped(KeyEvent e)
    {
        //System.out.println("Creeper gonna getcha!");
    }
    
    public static XboxControllerAdapter initializeAdapter(final XboxController xc)
    {	//http://www.aplu.ch/classdoc/xbox/ch/aplu/xboxcontroller/XboxControllerAdapter.html
    	return new XboxControllerAdapter()
    	{
    		public void dpad(int direction, boolean pressed)
    		{
    			dpadDirection = direction;
    			dpadPressed = pressed;
    		}
    		public void buttonA(boolean pressed)
    		{
    			buttonAPressed = pressed;
    		}
    		public void buttonB(boolean pressed)
    		{
    			buttonBPressed = pressed;
    		}
    		public void buttonX(boolean pressed)
    		{
    			buttonXPressed = pressed;
    		}
    		public void buttonY(boolean pressed)
    		{
    			buttonYPressed = pressed;
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
    		
    		public void leftShoulder(boolean pressed)
    		{
    			leftShoulderPressed = pressed;
    		}
    		public void rightShoulder(boolean pressed)
    		{
    			rightShoulderPressed = pressed;
    		}
    		public void leftTrigger(double value)
    		{
    			leftTriggerMagnitude = value;
    		}
    		public void rightTrigger(double value)
    		{
    			rightTriggerMagnitude = value;
    		}
    		public void leftThumb(boolean pressed)
    		{
    			leftThumbPressed = pressed;
    		}
    		public void rightThumb(boolean pressed)
    		{
    			rightThumbPressed = pressed;
    		}
    		public void leftThumbMagnitude(double magnitude)
    		{
    			leftThumbMagnitude = magnitude;
    		}
    		public void leftThumbDirection(double direction)
    		{
    			leftThumbDirection = direction;
    		}
    		public void rightThumbMagnitude(double magnitude)
    		{
    			rightThumbMagnitude = magnitude;
    		}
    		public void rightThumbDirection(double direction)
    		{
    			rightThumbDirection = direction;
    		}
    	};
    }
}
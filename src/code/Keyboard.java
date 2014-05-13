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
    private boolean[] keys = new boolean[NUMKEYS];//stores states of keys on keyboard *keyCodes (see below) might go out of this range*
    // initialize array?
    private HeaderType[] headers = new HeaderType[NUMKEYS];
    private int[] values = new int[NUMKEYS];
    //private boolean[] isShort = new boolean[NUMKEYS];
    //^^^^^^^^	maybe add this later, just keep as ints for now
    private boolean anyKeyPressed;
    
    // XboxController things
    static double leftMagnitude = 0, rightMagnitude = 0;
    static double leftDirection = 0, rightDirection = 0;
    static boolean brake = false, paused = false;
    
    private boolean disableController = false;
    public boolean up, down, left, right;
    public Keyboard()
    {
    	updateKeyMappings();
    	
    }
    private void updateKeyMappings()
    {
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
					keycode = reader.nextInt();
					if(reader.hasNext())	// keep checking for expected input
					{
						str = reader.next();
						header = HeaderType.valueOf(str);
						if(reader.hasNextInt())
						{
							value = reader.nextInt();
							
							headers[keycode] = header;
							values[keycode] = value;
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
    public void update() //checks every cycle if key is pressed or released
    {
    	if(keys[27])
    	{
			disableController = true;
    	}
    	else if(keys[112])
    	{
			disableController = false;
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
	        		if(headers[i] != null)	// only create packets for defined header values
	        		{
	        			ServerMain.requestPacket(headers[i], values[i]);
	        			//System.out.println("Packet: " + headers[i] + " value: " + values[i]);
	        			//System.out.println("Creating packet for " + KeyEvent.getKeyText(i));
	        		}
	        		anyKeyPressed = true;
	        	}
	        }
	        if(!anyKeyPressed)
	        {
	        	ServerMain.requestPacket(HeaderType.driveAll, 1500);	// send stop packet if no keys pressed
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
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
    
    private boolean disableController = false;
    private boolean updateMappings = false;
    
    public boolean up, down, left, right;
    public Keyboard(boolean controllerConnected)	// keyboard object kind of houses XboxController stuff too
    {
    	disableController = !controllerConnected;	// change default input based on controller connection
    	
    	updateKeyMappings();
    	//updateControllerMappings();
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
			updateMappings = true;
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
    
    public boolean getDisableController()
    {
    	return disableController;
    }
    
    public boolean getUpdateStatus()
    {
    	if(updateMappings)
    	{
    		updateMappings = false;
    		return true;
    	}
    	else
    		return false;
    	
    }
}
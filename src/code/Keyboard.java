package code;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.Scanner;





import javax.swing.JOptionPane;

import ch.aplu.xboxcontroller.XboxController;
import ch.aplu.xboxcontroller.XboxControllerAdapter;

public class Keyboard implements KeyListener
{
	private XboxController controller;
	  private int leftVibrate = 0; 
	  private int rightVibrate = 0;
	
    private static final int NUMKEYS = 1000;
    private boolean[] keys = new boolean[NUMKEYS];//stores states of keys on keyboard *keyCodes (see below) might go out of this range*
    // initialize array?
    private HeaderType[] headers = new HeaderType[NUMKEYS];
    private int[] values = new int[NUMKEYS];
    //private boolean[] isShort = new boolean[NUMKEYS];
    //^^^^^^^^	maybe add this later, just keep as ints for now
    
    public boolean up, down, left, right;
    public Keyboard()
    {
    	updateKeyMappings();
    	controller = new XboxController();
    	if (!controller.isConnected())
        {
          JOptionPane.showMessageDialog(null, 
            "Xbox controller not connected.",
            "Fatal error", 
            JOptionPane.ERROR_MESSAGE);
          controller.release();
          return;
        }
        
        controller.addXboxControllerListener(new XboxControllerAdapter()
        {
          public void leftTrigger(double value)
          {
            leftVibrate = (int)(65535 * value * value);
            controller.vibrate(leftVibrate, rightVibrate);
          }
          public void rightTrigger(double value)
          {
            rightVibrate = (int)(65535 * value * value);
            controller.vibrate(leftVibrate, rightVibrate);
          }
        });
        
        JOptionPane.showMessageDialog(null, 
          "Xbox controller connected.\n" + 
          "Press left or right trigger, Ok to quit.",
            "RumbleDemo V1.0 (www.aplu.ch)", 
            JOptionPane.PLAIN_MESSAGE);
        
        controller.release();
        System.exit(0);
      }
    //}
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
        			//System.out.println("Creating packet for " + KeyEvent.getKeyText(i));
        		}
        	}
        }
        
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
}
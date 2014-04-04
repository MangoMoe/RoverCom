package code;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Keyboard implements KeyListener
{
    
    private boolean[] keys = new boolean[120];//stores states of keys on keyboard *keyCodes (see below) might go out of this range*
    // initialize array?
    public boolean up, down, left, right;
    public Keyboard(){
        
    }
    public void update() //checks every cycle if key is pressed or released
    {
        up = keys[KeyEvent.VK_UP] || keys[KeyEvent.VK_W];
        down = keys[KeyEvent.VK_DOWN] || keys[KeyEvent.VK_S];
        left = keys[KeyEvent.VK_LEFT] || keys[KeyEvent.VK_A];
        right = keys[KeyEvent.VK_RIGHT] || keys[KeyEvent.VK_D];
        //System.out.println(up);//tests if above method is working
        
        
        /*
         * for (int i = 0; i < keys.length; i++){//used to find index of a key (prints a number to console)
        	if(keys[i]) 
        	{
        		System.out.println("Key = " + i);
        	}
        }
        */
    }
    
    public void keyPressed(KeyEvent e)
    {
    	System.out.println("key pressed");
        keys[e.getKeyCode()] = true;//if a key is pressed, the corresponding spot in the array is set to true
    }
    
    public void keyReleased(KeyEvent e)
    {
    	System.out.println("key released");
        keys[e.getKeyCode()] = false;//if a key is released, the corresponding spot in the array is set to false
    }
    
    public void keyTyped(KeyEvent e)
    {
        
    }
}
package code;

import java.awt.Canvas;
import java.awt.Dimension;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Scanner;

import javax.swing.JFrame;

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
		reader.close();
		
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
    
    public static void requestPacket(HeaderType header, int data)	//place to put packet validation logic
    {
    	header.setCurrentValue(data);	// set new value
    	data = header.getCurrentValue();
    	
    	createPacket(header, data);
    }
    
    
    private static void createPacket(HeaderType header, int data)
    {
    	ByteBuffer buffer = ByteBuffer.allocate(5);	// 5 bytes
    	buffer.put(header.getByte());
    	buffer.putInt(data);
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
    
    // input stuffs
    private void update()
    {
    	keyboard.update();
    	
    }
    private void render()
    {
    	
    }
    
}
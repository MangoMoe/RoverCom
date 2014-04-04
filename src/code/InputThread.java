package code;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class InputThread extends Thread 
{
	protected final int PORT = 4445;

    protected DatagramSocket socket = null;
    protected CommonData com = null;

    public InputThread(CommonData common) throws IOException 
    {
    	this("Rover Input Thread", common);
    }

    public InputThread(String name, CommonData common) throws IOException 
    {
        super(name);
        com = common;
        socket = new DatagramSocket(PORT);

    }
    
    public void run()
    {
    	try
    	{
	        byte[] buf = new byte[256];
	        DatagramPacket packet = new DatagramPacket(buf, buf.length);
	        
    		while(!Thread.interrupted())	// while we haven't been told to stop
	        {
	        	socket.receive(packet);
	        	
	        	com.addRecievedPacket(packet.getData());	// add recieved packet to common data buffer
	        	System.out.println("Input Thread has recieved packet");
	        }
    	}
    // more catch statements?
    	catch (IOException e)
    	{
    		if (!(e.getMessage().equals("socket closed")))	// we want to ignore socket closed exceptions, we cause this on purpose when the thread is interrupted to force it to stop waiting to receive a packet
    		{
        		System.err.println("Some sort of IO exception in InputThread, Uh oh.");
        		e.printStackTrace();
    		}
        }
    }
    
    @Override
    public void interrupt(){
      super.interrupt();  
      this.socket.close();	// if inerrupted we need to close the socket so it doesn't just wait for udp thread
    }
}
package code;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class BroadcastThread extends Thread 
{

	protected final int PORT = 4444;
    protected DatagramSocket socket = null;
    protected CommonData com = null;
    protected InetAddress address = null;

    public BroadcastThread(CommonData common, String host ) throws IOException {
    	this("QuoteServerThread", common, host);
    }

    public BroadcastThread(String name, CommonData common, String host) throws IOException {
        super(name);
        com = common;
        address = InetAddress.getByName(host);
        socket = new DatagramSocket();	// use constructor with port???
    }
    	
    public void run() 
    {
        try 
        {
        	// initialize data
	        byte[] buf;
	        
	        while(!Thread.interrupted())	// Send all packets until queue runs out, sleep, check for more, repeat
	        {
	        	buf = com.popPacketToSend();
	        	while(buf != null)
	        	{
			        DatagramPacket packet = new DatagramPacket(buf, buf.length ,address, PORT); // FFFIIIIXXX THIS!
// use a fixed port?
			        socket.send(packet);
			        buf = com.popPacketToSend();
	        	}
	        	Thread.sleep(20);
	        }
        } 
        catch (IOException e) 
        {
        	System.err.println("Some sort of IO exception in BroadcastThread, Uh oh.");
            e.printStackTrace();
        } catch (Exception e) {	// we got interrupted while sleeping DO WE NEED BOTH THIS AND THE CONDITION IN THE WHILE LOOP ABOVE?
			// do nothing, just keep going
		}
        
        socket.close();	// shut things down
    }
}

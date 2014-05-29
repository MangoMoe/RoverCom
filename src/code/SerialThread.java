package code;

import java.io.IOException;

/*import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import gnu.io.CommPortIdentifier; 
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent; 
import gnu.io.SerialPortEventListener; 

import java.util.Enumeration;*/


public class SerialThread extends Thread 
{
	BroadcastSerial broadcast;
    protected CommonData com = null;	// concurrency object for data transfer between threads
    private String comPort;
    public static final int PACKET_SIZE = 12;
    
    private static final byte START1 = (byte)'$',START2 = (byte)'H',START3 = (byte)'A',START4 = (byte)'L',START5 = (byte)'R',START6 = (byte)'V',START7 = (byte)'R',
    		STOP = (byte)0xFE,ESCAPE = (byte)0xEE;

    public SerialThread(CommonData common, String comPort) throws IOException {
    	this("Puppet Serial Input Thread", common,comPort);
    }
    
    public SerialThread(String name, CommonData common, String comPort) throws IOException {
        super(name);
        com = common;
        this.comPort = comPort;
    }
    
    public synchronized void run() 
    {
        try 
        {
        	short[] currentPacket;
	        while(!Thread.interrupted())	// Send all packets until queue runs out, sleep, check for more, repeat
	        {
	        	if(com.getStartSerial())	// check for external commands to start or stop serial communications
	        		startSerial();
	        	if(com.getStopSerial())
	        		stopSerial();
	        	if(broadcast != null)	// only send stuff if serial is on
	        	{
		        	currentPacket = com.popSerialPacketToSend();
		        	while(currentPacket != null)
		        	{
		        		sendPacket(currentPacket);
		        		currentPacket = com.popSerialPacketToSend();
		        	}
		        	sleep(20);	// take a break once in a while to free up processing
	        	}
	        }
        } 
        catch (Exception e) 
        {
        	System.err.println("Some sort of exception in SerialThread, Horror of Horrors!");
            e.printStackTrace();
        }
        broadcast.close();
    }
    
    private void sendPacket(short[] data)
    {
    	byte curByte;
    	if(data.length == PACKET_SIZE && broadcast != null)	// only use on valid length data
    	{
    		broadcast.send(START1);
    		broadcast.send(START2);
    		broadcast.send(START3);
    		broadcast.send(START4);
    		broadcast.send(START5);
    		broadcast.send(START6);
    		broadcast.send(START7);
    		System.out.println("Sent start byte sequence" /*+ String.format("%02x", START1 & 0xff)*/);
    		for(int i = 0; i < PACKET_SIZE; i++)
    		{
    			curByte = (byte) (data[i] >> 8);
    			/*if(curByte == START || curByte == STOP || curByte == ESCAPE)	// send escape byte if necessary (bytestuffed protocol)
    			{
    				broadcast.send(ESCAPE);	// send escape byte
    				System.out.print(String.format("%02x", ESCAPE & 0xff) + " ");
    			}*/
    			broadcast.send(curByte);
    			System.out.print(String.format("%02x", curByte & 0xff) + " ");
    			
    			curByte = (byte) (data[i]);
    			/*if(curByte == START || curByte == STOP || curByte == ESCAPE)
    			{
    				broadcast.send(ESCAPE);	// send escape byte
    				System.out.print(String.format("%02x", ESCAPE & 0xff) + " ");
    			}*/
    			broadcast.send(curByte);
    			System.out.print(String.format("%02x", curByte & 0xff) + " ");
    			
    			System.out.println("  Sent " + (i/* + 1*/) + "th chunk of data");
    		}
    		/*broadcast.send(STOP);
    		System.out.println("Sent stop byte: " + String.format("%02x", STOP & 0xff));*/
    	}
    }
    
    public synchronized void startSerial()
    {
    	broadcast = new BroadcastSerial(comPort);
    }
    
    public synchronized void stopSerial()
    {
	    broadcast.close();
		broadcast = null;
    }
    /*public synchronized void update()
    {
    	if(serial.recieving)
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
    }*/

}

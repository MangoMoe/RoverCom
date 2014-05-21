package code;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import gnu.io.CommPortIdentifier; 
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent; 
import gnu.io.SerialPortEventListener; 

import java.util.Enumeration;


public class SerialThread extends Thread 
{
	SerialTest serial = new SerialTest("3");
    protected CommonData com = null;	// concurrency object for data transfer between threads

    public SerialThread(CommonData common) throws IOException {
    	this("Puppet Serial Input Thread", common);
    }
    
    public SerialThread(String name, CommonData common) throws IOException {
        super(name);
        com = common;
    }
    
    public void run() 
    {
        try 
        {
	        while(!Thread.interrupted())	// Send all packets until queue runs out, sleep, check for more, repeat
	        {
	        	sleep(20);
	        }
        } 
        catch (Exception e) 
        {
        	System.err.println("Some sort of exception in SerialThread, Horror of Horrors!");
            e.printStackTrace();
        }
        serial.close();
    }

}

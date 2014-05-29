package code;

import java.util.concurrent.ConcurrentLinkedQueue;

public class CommonData	// this class will hold all of the data passed between threads
{
	private ConcurrentLinkedQueue<byte[]> packetsRecieved;
	private ConcurrentLinkedQueue<byte[]> packetsToSend;
	private ConcurrentLinkedQueue<short[]> serialPacketsToSend;
	
	private boolean startSerial,stopSerial;
	
	CommonData()	// initialize all class variables above!
	{
		packetsRecieved = new ConcurrentLinkedQueue<byte[]>();
		packetsToSend = new ConcurrentLinkedQueue<byte[]>();
		serialPacketsToSend = new ConcurrentLinkedQueue<short[]>();
	}
	
	public synchronized void startSerial()
	{
		startSerial = true;
		serialPacketsToSend = new ConcurrentLinkedQueue<short[]>();	// clear this queue so we don't have buildup while serial is off
	}
	
	public synchronized void stopSerial()
	{
		stopSerial = true;
	}
	
	public synchronized boolean getStartSerial()
	{
		if(startSerial)
		{
			startSerial = false;	// clear flag
			return true;
		}
		else
			return false;
	}
	
	public synchronized boolean getStopSerial()
	{
		if(stopSerial)
		{
			stopSerial = false;	// clear flag
			return true;
		}
		else
			return false;
	}
	
	public synchronized void addRecievedPacket(byte[] data)
	{
		packetsRecieved.add(data.clone());	// must copy data into new byte array because data is really a pointer to a buffer
											// so if we use data itself (a pointer), all entries will be whatever that buffer currently is
	}
	
	public synchronized byte[] popRecievedPacket()	// use a get packet instead or in addition to
	{
		return packetsRecieved.poll();
	}
	
	public synchronized void addPacketToSend(byte[] data)
	{
		packetsToSend.add(data.clone());	// must copy data into new byte array because data is really a pointer to a buffer
											// so if we use data itself (a pointer), all entries will be whatever that buffer currently is
	}
	
	public synchronized byte[] popPacketToSend()	// use a get packet instead or in addition to
	{
		return packetsToSend.poll();
	}
	
	public synchronized void addSerialPacketToSend(short[] data)
	{
		serialPacketsToSend.add(data.clone());	// must copy data into new byte array because data is really a pointer to a buffer
											// so if we use data itself (a pointer), all entries will be whatever that buffer currently is
	}
	
	public synchronized short[] popSerialPacketToSend()	// use a get packet instead or in addition to
	{
		return serialPacketsToSend.poll();
	}
}

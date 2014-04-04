package code;

import java.util.concurrent.ConcurrentLinkedQueue;

public class CommonData	// this class will hold all of the data passed between threads
{
	private
	
	ConcurrentLinkedQueue<byte[]> packetsRecieved;
	ConcurrentLinkedQueue<byte[]> packetsToSend;
	
	
	
	CommonData()	// initialize all class variables above!
	{
		packetsRecieved = new ConcurrentLinkedQueue<byte[]>();
		packetsToSend = new ConcurrentLinkedQueue<byte[]>();
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
}

package code;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

import java.util.Enumeration;

public class BroadcastSerial implements SerialPortEventListener
{
	    SerialPort serialPort;
	    int turret = 0,
	    		shoulder = 0,
	    		elbow = 0,
	    		wristRotate = 0,
	    		wristPitch = 0,
	    		gripper = 0,
	    		toggleSwitch = 0,
	    		checksum = 0;
	    public boolean recieving = false;
		

	    private static final String PORT_NAMES[] = {
	            "/dev/tty.usbserial-A9007UX1", // Mac OS X
	            "/dev/ttyUSB0", // Linux
	            "COM5", // Windows
	    };

	    private BufferedReader input;
	    private static OutputStream output;
	    private static final int TIME_OUT = 2000;
	    private static final int DATA_RATE = 115200;

	    public void initialize() {
	        CommPortIdentifier portId = null;
	        Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();

	        while (portEnum.hasMoreElements()) {
	            CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();
	            for (String portName : PORT_NAMES) {
	                if (currPortId.getName().equals(portName)) {
	                    portId = currPortId;
	                    break;
	                }
	            }
	        }
	        if (portId == null) {
	            System.out.println("Could not find broadcast COM port.");
	            return;
	        }

	        try {
	            serialPort = (SerialPort) portId.open(this.getClass().getName(),TIME_OUT);

	            serialPort.setSerialPortParams(DATA_RATE, SerialPort.DATABITS_8, SerialPort.STOPBITS_1,    SerialPort.PARITY_NONE);

	            input = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
	            output = serialPort.getOutputStream();

	            serialPort.addEventListener(this);
	            serialPort.notifyOnDataAvailable(true);
	        }
	        catch (Exception e) {
	            System.err.println(e.toString());
	        }
	    }
	    
	    
	    public synchronized void serialEvent(SerialPortEvent oEvent) {
	    	// ignore serial port events?
	        /*if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
	            try {
	            	System.out.println("DATA_AVAILABLE");
	                String inputLine=null;
	                if (input.ready()) {
	                    inputLine = input.readLine();
	                    System.err.println(inputLine);
	                    //read(inputLine);
	                }

	            } catch (Exception e) {
	                System.err.println(e.toString());
	            }
	        }*/
	    }

	    public synchronized void close() {
	        if (serialPort != null) {
	            serialPort.removeEventListener();
	            serialPort.close();
	        }
	    }

	    public BroadcastSerial(String ncom)
	    {
	        if(Integer.parseInt(ncom)>=3 && Integer.parseInt(ncom)<=9)
	            PORT_NAMES[2] = "COM" + ncom;
	        initialize();
	       
	        System.out.println("Broadcast Serial Comms Started");
	    }
	    
	    /*public void startMessage()
	    {}
	    
	    public void stopMessage()
	    {}*/
	    
	    public synchronized void send(byte b)
	    {
	        try{
	            output.write(b);	// send as a  bit number
	        }
	        catch (Exception e) {
	            //e.printStackTrace();
	        }
	    }

}

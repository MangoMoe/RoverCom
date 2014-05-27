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

//DISCLAMER: I pretty much just copied this straight from the web and made a few tweaks for what i want
public class SerialTest implements SerialPortEventListener {
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
            System.out.println("Could not find COM port.");
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

        if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
            try {

                String inputLine=null;
                if (input.ready()) {
                    inputLine = input.readLine();
                    read(inputLine);
                }

            } catch (Exception e) {
                System.err.println(e.toString());
            }
        }       
    }

    public synchronized void close() {
        if (serialPort != null) {
            serialPort.removeEventListener();
            serialPort.close();
        }
    }

    public SerialTest(String ncom){
        if(Integer.parseInt(ncom)>=3 && Integer.parseInt(ncom)<=9)
            PORT_NAMES[2] = "COM" + ncom;
        initialize();
       
        System.out.println("Serial Comms Started");
    }

    /*public synchronized void send(int b){
        try{
            output.write(b);
        }
        catch (Exception e) {
            System.err.println(e.toString());
        }
    }*/

    public synchronized void read(String data)
    {
        try
        {
        	/*System.out.println(data);*/
        	String[] datums = data.split(",");
        	
        	if(datums[0].equals("$PUPRPT"))
        	{
	        	turret = Integer.parseInt(datums[1]);
	        	shoulder = Integer.parseInt(datums[2]);
	        	elbow = Integer.parseInt(datums[3]);
	        	wristRotate = Integer.parseInt(datums[4]);
	        	wristPitch = Integer.parseInt(datums[5]);
	        	gripper = Integer.parseInt(datums[6]);
	        	toggleSwitch = Integer.parseInt(datums[7]);
	        	checksum = Integer.parseInt(datums[8].replace("*", ""));	// get checksum, don't include star at end
	        	
	        	//System.out.println((turret ^ shoulder ^ elbow ^ wristRotate ^ wristPitch ^ gripper) + "  :  " + checksum);
	        	//System.out.println((turret ^ shoulder ^ elbow ^ wristRotate ^ wristPitch ^ gripper) == checksum);
	        	if((turret ^ shoulder ^ elbow ^ wristRotate ^ wristPitch ^ gripper /*^ toggleSwitch*/) == checksum)
	        		recieving = true;	// lastly, say that we are recieving
	        	else
	        		recieving = false;
        	}
        	else
        		recieving = false;
        	
        }
        catch (Exception e) {
            e.printStackTrace();
            recieving = false;
        }
    }
}
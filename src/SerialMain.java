import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;


public class SerialMain implements SerialPortEventListener
{
    SerialPort serialPort;

    private InputStreamReader input;

    private OutputStream output;

    private static final int CONNECTION_TIME_OUT = 2000;

    private CommPortIdentifier portId = null;

    public SerialMain(String port)
    {
        try
        {
            portId = CommPortIdentifier.getPortIdentifier(port);
        } catch(NoSuchPortException e) {
            e.printStackTrace();
        }
    }

    public boolean init(int baud)
    {
        if (portId == null)
        {
            System.out.println("Could not find COM port.");
            return false;
        }

        try
        {
            // open serial port, and use class name for the appName.
            serialPort = (SerialPort) portId.open("Serial test", CONNECTION_TIME_OUT);

            // set port parameters
            serialPort.setSerialPortParams(baud, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

            // open the streams
            input = new InputStreamReader(serialPort.getInputStream(), StandardCharsets.US_ASCII);
            output = serialPort.getOutputStream();

            // add event listeners
            serialPort.addEventListener(this);
            serialPort.notifyOnDataAvailable(true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    /**
     * This should be called when you stop using the port.
     * This will prevent port locking on platforms like Linux.
     */
    public synchronized void close()
    {
        if (serialPort != null)
        {
            serialPort.removeEventListener();
            serialPort.close();
        }
    }

    /**
     * Handle an event on the serial port. Read the data and print it.
     */
    public synchronized void serialEvent(SerialPortEvent oEvent)
    {
        if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE)
        {
            try
            {
                int r;

                while((r = input.read()) != -1)
                {
                    char c = (char)r;
                    SerialViewer.instance.write(c + "");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // Ignore all the other eventTypes, but you should consider the other ones.
    }

    public void write(String data)
    {
        try
        {
            output.write(data.getBytes(StandardCharsets.US_ASCII));
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public void write(byte data)
    {
        try
        {
            output.write(data);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}

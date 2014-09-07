package moppydesk.outputs;

import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Sammy1Am
 */
public interface MoppyBridge{


    /**
     * Convenience method that splits the periodData int into two bytes for
     * sending over serial.
     *
     * @param pin Controller pin to handle ntoe
     * @param periodData length of period in microSeconds
     */
    public void sendEvent(byte pin, int periodData);

    /**
     * Sends an event to the Arduino.
     *
     * @param pin Controller pin
     * @param b1
     * @param b2
     */
    public void sendEvent(byte pin, byte b1, byte b2);

    /**
     * Sends a special code (first byte=100) to reset the drives
     */
    public void resetDrives();

    public void close();

}

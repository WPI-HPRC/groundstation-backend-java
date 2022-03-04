package com.hprc.serialPort;

import javax.comm.CommPortIdentifier;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import com.hprc.Main;

public class SerialManager {
    private static Enumeration comPorts;

    private static int timeout;

    /**
     *
     * @param timeout Timeout on the serial port
     * @param startBytes List of decimal bytes to act as a start of data
     * @param endBytes List of decimal bytes to act as an end of data
     */
    public SerialManager(int timeout, int[] startBytes, int[] endBytes) {
        this.timeout = timeout;
    }

    public void findSerialPort() {
        comPorts = CommPortIdentifier.getPortIdentifiers();

    }
}

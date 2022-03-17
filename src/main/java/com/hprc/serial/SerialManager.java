package com.hprc.serial;

import com.fazecast.jSerialComm.SerialPort;
import com.hprc.serial.listeners.OnData;
import com.hprc.serial.listeners.OnDisconnect;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Daniel Pearson
 * @version 3/17/2021
 */
public class SerialManager {
    private Logger logger = LoggerFactory.getLogger("SerialManager");

    private SerialPort comPort;
    private int baudRate;

    private OnData onSerialReceived;
    private OnDisconnect onSerialDisconnect;

    public static List<Identifier> identifiers;
    private static HashMap<String, Object> telemetry = new HashMap<>();

    public SerialManager() {
        onSerialReceived = new OnData(comPort, logger); //Event listener for data receiving
        onSerialDisconnect = new OnDisconnect(comPort, logger); //Event listener for com disconnects

        identifiers = new ArrayList<>();
    }

    /**
     * Configuration method to set baud rate speed
     * @param baudRate Specified baud of serial stream
     */
    public void setBaudRate(int baudRate) {
        this.baudRate = baudRate;
    }

    /**
     * Hashmap of telemetry data
     * Dependent on identifiers, if no identifiers are added, this WILL NOT RETURN ANYTHING
     * @return returns data which has been retrieved from the serial stream
     */
    public HashMap<String, Object> getTelemetry() {
        return telemetry;
    }

    /**
     * Configuration method to add identifiers in the serial stream
     * Can technically be any size but 3 bytes is recommended
     * MUST be in Decimal Byte format
     * Ex: serialManager.addIdentifier(new ArrayList<>(Arrays.asList(4,3,2,1)), "Velocity", DataTypes.FLOAT);
     * @param identifierBytes Arraylist of identifier bytes
     * @param name Name of the data you are looking to find
     * @param datatype Bit extraction method expected [int, float, string, etc.]
     */
    public void addIdentifier(ArrayList<Integer> identifierBytes, String name, DataTypes datatype) {
        Identifier identifier = new Identifier(identifierBytes,name,datatype);
        identifiers.add(identifier);
        telemetry.put(name, 0);
    }

    /**
     * Testing method to log to the console each identifier which has been added to the SerialManager
     */
    public void logInfo() {
        for(Identifier ident : identifiers) {
            logger.info(String.format("%s - %s", ident.name, ident.identifierBytes));
        }
        telemetry.forEach((key, value) -> {
            logger.info(String.format("%s - %s", key, value));
        });
    }

    /**
     * Synchronous method to begin streaming data from the selected com port.
     * Will prompt the user to select a com port
     * @throws IOException Throws an exception and terminates program when no ports are available
     */
    public synchronized void startStream() throws IOException {
        SerialPort[] ports = getSerialPorts();

        System.out.print("Select Port [0,1,2,?]: ");
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(System.in)
        );

        //Read the port selection from the user and parse
        int portSelection = Integer.parseInt(reader.readLine());
        if(portSelection > ports.length) {
            logger.error("Not a valid port...");
            System.exit(0);
        }
        comPort = ports[portSelection];

        //Com port configuration
        comPort.setBaudRate(baudRate);

        //Open the com port and add the data listeners
        comPort.openPort();
        if(!comPort.isOpen()) {
            logger.error("Port not able to open...");
        }

        comPort.addDataListener(onSerialReceived);
        comPort.addDataListener(onSerialDisconnect);
    }

    /**
     * Method to obtain an array of serial ports available to the system.
     * Note: Auto-removes com ports which are locked/in-use
     * @return ports : array of serial port objects
     */
    public SerialPort[] getSerialPorts() {
        SerialPort[] ports = SerialPort.getCommPorts();

        for(int i=0; i < ports.length; i++) {
            if(ports[i].isOpen()) {
                ArrayUtils.remove(ports, i);
                continue;
            }
        }

        if(ports.length == 0) {
            logger.error("No Com Ports Found!");
            System.exit(0);
        }

        logger.info("Serial Ports: ");
        for(int i=0; i < ports.length; i++) {
            logger.info(ports[i].getSystemPortName() + String.format(" [%s]", i));
        }

        return ports;
    }
}
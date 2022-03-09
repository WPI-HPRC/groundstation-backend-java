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
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class SerialManager {
    private Logger logger = LoggerFactory.getLogger("SerialManager");
    private int timeout;
    private static SerialPort comPort;
    private OnData onSerialReceived;
    private OnDisconnect onSerialDisconnect;
    private List<Object> identifiers;

    public SerialManager(int timeout, int byteLength) {
        this.timeout = timeout;
        onSerialReceived = new OnData(comPort, logger);
        onSerialDisconnect = new OnDisconnect(comPort, logger);
        identifiers = new ArrayList<>();
    }

    public void addIdentifier(int[] identifierBytes, String name, DataTypes datatype) {
        //identifiers.add(new Object[]{identifierBytes, name, datatype});
        Identifier identifier = new Identifier(identifierBytes,name,datatype);
        identifiers.add(identifier);
    }

    public void getInfo() {
        System.out.println(identifiers);
    }

    public synchronized void startStream() throws IOException {
        SerialPort[] ports = getSerialPorts();
        System.out.print("Select Port [0,1,2,?]: ");
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(System.in)
        );
        int portSelection = Integer.parseInt(reader.readLine());
        if(portSelection > ports.length) {
            logger.error("Not a valid port...");
            System.exit(0);
        }
        comPort = ports[portSelection];

        comPort.setBaudRate(9600);
        comPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0,0);
        comPort.openPort();
        if(!comPort.isOpen()) {
            logger.error("Port not able to open...");
        }

        comPort.addDataListener(onSerialReceived);
        comPort.addDataListener(onSerialDisconnect);
    }

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
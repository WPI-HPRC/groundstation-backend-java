package com.hprc;

import com.hprc.serial.DataTypes;
import com.hprc.serial.SerialManager;
import gnu.io.PortInUseException;
import gnu.io.UnsupportedCommOperationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.TooManyListenersException;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger("Groundstation");
    private static final SerialManager serial = new SerialManager();

    public static void main(String[] args) throws IOException {
        logger.info("Starting Backend...");

        //Config
        serial.setBaudRate(9600);

        serial.addIdentifier(new ArrayList<>(Arrays.asList(1,2,3,4)), "Altitude", DataTypes.FLOAT);
        serial.addIdentifier(new ArrayList<>(Arrays.asList(4,3,2,1)), "Velocity", DataTypes.FLOAT);
        serial.addIdentifier(new ArrayList<>(Arrays.asList(69,78,68,66)), "EndB", DataTypes.END_BYTES);
        serial.addIdentifier(new ArrayList<>(Arrays.asList(65,67,88)), "AccelX", DataTypes.SIGNED_INT);
        serial.addIdentifier(new ArrayList<>(Arrays.asList(65,67,89)), "AccelY", DataTypes.SIGNED_INT);
        serial.addIdentifier(new ArrayList<>(Arrays.asList(65,67,90)), "AccelZ", DataTypes.SIGNED_INT);
        serial.addIdentifier(new ArrayList<>(Arrays.asList(71,89,88)), "GyroX", DataTypes.SIGNED_INT);
        serial.addIdentifier(new ArrayList<>(Arrays.asList(71,89,89)), "GyroY", DataTypes.SIGNED_INT);
        serial.addIdentifier(new ArrayList<>(Arrays.asList(71,89,90)), "GyroZ", DataTypes.SIGNED_INT);
        serial.addIdentifier(new ArrayList<>(Arrays.asList(65,76,84)), "Altitude", DataTypes.SIGNED_INT);
        serial.addIdentifier(new ArrayList<>(Arrays.asList(83,84,84)), "State", DataTypes.SIGNED_INT);
        serial.addIdentifier(new ArrayList<>(Arrays.asList(84,83,80)), "Timestamp", DataTypes.SIGNED_INT);
        serial.addIdentifier(new ArrayList<>(Arrays.asList(84,77,80)), "Temperature", DataTypes.FLOAT);

        //serial.logInfo();
        serial.startStream();
        Thread t = new Thread() {
            public void run() {
                try { Thread.sleep(1000000);} catch (InterruptedException ie) {}
            }
        };
        t.start();
        System.out.println("Started!");

    }
}

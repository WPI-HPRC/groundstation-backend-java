package com.hprc;

import com.hprc.serial.DataTypes;
import com.hprc.serial.SerialManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger("Groundstation");

    private static SerialManager serialManager = new SerialManager();

    public static void main(String[] args) throws IOException {
        logger.info("Starting Backend...");

        //Serial Manager Configuration
        serialManager.setBaudRate(9600);
        serialManager.setPacketSize(22);

        serialManager.addIdentifier(new ArrayList<>(Arrays.asList(1,2,3,4)), "Altitude", DataTypes.FLOAT);
        serialManager.addIdentifier(new ArrayList<>(Arrays.asList(4,3,2,1)), "Velocity", DataTypes.FLOAT);
        serialManager.addIdentifier(new ArrayList<>(Arrays.asList(69,78,68,66)), "EndB", DataTypes.END_BYTES);
        serialManager.addIdentifier(new ArrayList<>(Arrays.asList(65,67,88)), "AccelX", DataTypes.SIGNED_INT);
        serialManager.addIdentifier(new ArrayList<>(Arrays.asList(65,67,89)), "AccelY", DataTypes.SIGNED_INT);
        serialManager.addIdentifier(new ArrayList<>(Arrays.asList(65,67,90)), "AccelZ", DataTypes.SIGNED_INT);
        serialManager.addIdentifier(new ArrayList<>(Arrays.asList(71,89,88)), "GyroX", DataTypes.SIGNED_INT);
        serialManager.addIdentifier(new ArrayList<>(Arrays.asList(71,89,89)), "GyroY", DataTypes.SIGNED_INT);
        serialManager.addIdentifier(new ArrayList<>(Arrays.asList(71,89,90)), "GyroZ", DataTypes.SIGNED_INT);
        serialManager.addIdentifier(new ArrayList<>(Arrays.asList(65,76,84)), "Altitude", DataTypes.SIGNED_INT);
        serialManager.addIdentifier(new ArrayList<>(Arrays.asList(83,84,84)), "State", DataTypes.SIGNED_INT);
        serialManager.addIdentifier(new ArrayList<>(Arrays.asList(84,83,80)), "Timestamp", DataTypes.SIGNED_INT);
        serialManager.addIdentifier(new ArrayList<>(Arrays.asList(84,77,80)), "Temperature", DataTypes.FLOAT);
        //Open com port and start serial stream
        serialManager.startStream();
        serialManager.logInfo();

    }
}

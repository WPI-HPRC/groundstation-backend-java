package com.hprc;

import com.hprc.serial.DataTypes;
import com.hprc.serial.SerialManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger("Groundstation");
    private static SerialManager serial = null;

    static {
        try {
            serial = new SerialManager();
        } catch (IOException e) {
            logger.error(e.toString());
        }
    }

    public static void main(String[] args) throws IOException {
        logger.info("Starting Backend...");

        //Config
        serial.setBaudRate(115200);
        serial.enableLogging();

        serial.addIdentifier(new ArrayList<>(Arrays.asList(65,67,88)), "AccelX", DataTypes.SIGNED_INT);
        serial.addIdentifier(new ArrayList<>(Arrays.asList(65,67,89)), "AccelY", DataTypes.SIGNED_INT);
        serial.addIdentifier(new ArrayList<>(Arrays.asList(65,67,90)), "AccelZ", DataTypes.SIGNED_INT);
        serial.addIdentifier(new ArrayList<>(Arrays.asList(71,89,88)), "GyroX", DataTypes.SIGNED_INT);
        serial.addIdentifier(new ArrayList<>(Arrays.asList(71,89,89)), "GyroY", DataTypes.SIGNED_INT);
        serial.addIdentifier(new ArrayList<>(Arrays.asList(71,89,90)), "GyroZ", DataTypes.SIGNED_INT);
        serial.addIdentifier(new ArrayList<>(Arrays.asList(65,76,84)), "Altitude", DataTypes.FLOAT);
        serial.addIdentifier(new ArrayList<>(Arrays.asList(83,84,84)), "State", DataTypes.SIGNED_INT);
        serial.addIdentifier(new ArrayList<>(Arrays.asList(84,83,80)), "Timestamp", DataTypes.SIGNED_INT);
        serial.addIdentifier(new ArrayList<>(Arrays.asList(84,77,80)), "Temperature", DataTypes.FLOAT);
        serial.addIdentifier(new ArrayList<>(Arrays.asList(69,78,68,66)), "EndByte", DataTypes.END_BYTES);

        serial.startStream();

        Thread t = new Thread(() -> {
            try { Thread.sleep(10);} catch (InterruptedException e) { logger.error(e.toString());}
        });
        t.start();
    }
}

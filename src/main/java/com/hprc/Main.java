package com.hprc;

import com.hprc.serial.DataTypes;
import com.hprc.serial.SerialManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

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

        serial.addIdentifier("ACX", "AccelX", DataTypes.SIGNED_INT);
        serial.addIdentifier("ACY", "AccelY", DataTypes.SIGNED_INT);
        serial.addIdentifier("ACZ", "AccelZ", DataTypes.SIGNED_INT);
        serial.addIdentifier("GYX", "GyroX", DataTypes.SIGNED_INT);
        serial.addIdentifier("GYY", "GyroY", DataTypes.SIGNED_INT);
        serial.addIdentifier("GYZ", "GyroZ", DataTypes.SIGNED_INT);
        serial.addIdentifier("ALT", "Altitude", DataTypes.FLOAT);
        serial.addIdentifier("STT", "State", DataTypes.SIGNED_INT);
        serial.addIdentifier("TSP", "Timestamp", DataTypes.SIGNED_INT);
        serial.addIdentifier("TMP", "Temperature", DataTypes.FLOAT);
        serial.addIdentifier("VOL", "Voltage", DataTypes.FLOAT);
        serial.addIdentifier("ENDB", "EndByte", DataTypes.END_BYTES);

        serial.startStream();

        Thread t = new Thread(() -> {
            try { Thread.sleep(10);} catch (InterruptedException e) { logger.error(e.toString());}
        });
        t.start();
    }
}

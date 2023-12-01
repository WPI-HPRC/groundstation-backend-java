package com.hprc;

import com.hprc.serial.DataTypes;
import com.hprc.serial.SerialManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

public class Main {

    private static ArrayList<Integer> toAsciiList(String str) {
        ArrayList<Integer> list = new ArrayList<>();

        for (char character : str.toCharArray()) {
            list.add((int)character);
        }
        return list;
    }

    private static final Logger logger = LoggerFactory.getLogger("Groundstation");
    private static SerialManager serial = null;
    private static int disconnectedCount = 0;

    static {
        try {
            serial = new SerialManager();
        } catch (IOException | URISyntaxException e) {
            logger.error(e.toString());
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        logger.info("Starting Backend...");
        
        serial.setBaudRate(115200); //Set baudrate for serial communications
        serial.enableLogging(); //Start logging at baudrate set earlier

        serial.addIdentifier(toAsciiList("ACX"), "AccelX", DataTypes.FLOAT);
        serial.addIdentifier(toAsciiList("ACY"), "AccelY", DataTypes.FLOAT);
        serial.addIdentifier(toAsciiList("ACZ"), "AccelZ", DataTypes.FLOAT);
        serial.addIdentifier(toAsciiList("GYX"), "GyroX", DataTypes.FLOAT);
        serial.addIdentifier(toAsciiList("GYY"), "GyroY", DataTypes.FLOAT);
        serial.addIdentifier(toAsciiList("GYZ"), "GyroZ", DataTypes.FLOAT);
        serial.addIdentifier(toAsciiList("ALT"), "Altitude", DataTypes.FLOAT);
        serial.addIdentifier(toAsciiList("STT"), "State", DataTypes.UNSIGNED_INT);
        serial.addIdentifier(toAsciiList("TSP"), "Timestamp", DataTypes.UNSIGNED_INT);
        serial.addIdentifier(toAsciiList("TMP"), "Temperature", DataTypes.SIGNED_INT);
        serial.addIdentifier(toAsciiList("VLT"), "Voltage", DataTypes.FLOAT);
        serial.addIdentifier(toAsciiList("VEL"), "Velocity", DataTypes.SIGNED_INT);
        serial.addIdentifier(toAsciiList("ARB"), "AirbrakesDeploy", DataTypes.UNSIGNED_INT);
        serial.addIdentifier(toAsciiList("ENDB"), "EndByte", DataTypes.IGNORE);

        serial.startStream(); // Start serial stream for receiver

        Thread t = new Thread(() -> {
            while(true) {
                try {
                    if(serial.comPort.getInputStream().read() == -1) {
                        disconnectedCount++;
                    }

                    if(disconnectedCount > 2) {
                        serial.telemetry.put("RocketConnected", false);
                        serial.manualPushTelemetry();
                    }

                    if (serial.comPort.getInputStream().read() != -1) {
                        disconnectedCount = 0;
                    }

                    Thread.sleep(1000);
                } catch(InterruptedException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        t.start();

    }
}

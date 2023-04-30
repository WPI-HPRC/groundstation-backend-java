package com.hprc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hprc.serial.DataTypes;
import com.hprc.serial.SerialManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.Console;
import java.io.FileReader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger("Groundstation");
    private static SerialManager serial = null;
    private static int disconnectedCount = 0;

    static {
        try {
            serial = new SerialManager();
        } catch (IOException e) {
            logger.error(e.toString());
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        logger.info("Starting Backend...");
        
        serial.setBaudRate(115200); //Set baudrate for serial communications
        serial.enableLogging(); //Start logging at baudrate set earlier

        serial.addIdentifier(new ArrayList<>(Arrays.asList(65,67,88)), "AccelX", DataTypes.FLOAT);
        serial.addIdentifier(new ArrayList<>(Arrays.asList(65,67,89)), "AccelY", DataTypes.FLOAT);
        serial.addIdentifier(new ArrayList<>(Arrays.asList(65,67,90)), "AccelZ", DataTypes.FLOAT);
        serial.addIdentifier(new ArrayList<>(Arrays.asList(71,89,88)), "GyroX", DataTypes.FLOAT);
        serial.addIdentifier(new ArrayList<>(Arrays.asList(71,89,89)), "GyroY", DataTypes.FLOAT);
        serial.addIdentifier(new ArrayList<>(Arrays.asList(71,89,90)), "GyroZ", DataTypes.FLOAT);
        serial.addIdentifier(new ArrayList<>(Arrays.asList(65,76,84)), "Altitude", DataTypes.FLOAT);
        serial.addIdentifier(new ArrayList<>(Arrays.asList(83,84,84)), "State", DataTypes.UNSIGNED_INT);
        serial.addIdentifier(new ArrayList<>(Arrays.asList(84,83,80)), "Timestamp", DataTypes.UNSIGNED_INT);
        serial.addIdentifier(new ArrayList<>(Arrays.asList(84,77,80)), "Temperature", DataTypes.SIGNED_INT);
        serial.addIdentifier(new ArrayList<>(Arrays.asList(86, 76, 84)), "Voltage", DataTypes.FLOAT);
        serial.addIdentifier(new ArrayList<>(Arrays.asList(86, 69, 76)), "Velocity", DataTypes.SIGNED_INT);
        serial.addIdentifier(new ArrayList<>(Arrays.asList(65, 82, 66)), "AirbrakesDeploy", DataTypes.UNSIGNED_INT);
        serial.addIdentifier(new ArrayList<>(Arrays.asList(69,78,68,66)), "EndByte", DataTypes.IGNORE);

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

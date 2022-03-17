package com.hprc;

import com.hprc.serial.DataTypes;
import com.hprc.serial.SerialManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger("Groundstation");

    private static SerialManager serialManager = new SerialManager();



    public static void main(String[] args) throws IOException {
        logger.info("Starting Backend...");

        //Serial Manager Configuration
        serialManager.setBaudRate(9600);
        serialManager.addIdentifier(new ArrayList<>(Arrays.asList(1,2,3,4)), "Altitude", DataTypes.FLOAT);
        serialManager.addIdentifier(new ArrayList<>(Arrays.asList(4,3,2,1)), "Velocity", DataTypes.FLOAT);

        //Open com port and start serial stream
        serialManager.startStream();
        serialManager.logInfo();

    }
}

package com.hprc;

import com.hprc.serial.DataTypes;
import com.hprc.serial.SerialManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class Main {
    private static Logger logger = LoggerFactory.getLogger("Groundstation");
    private static SerialManager serialManager = new SerialManager(2000, 128);

    public Main() {

    }

    public static void main(String[] args) throws IOException {
        logger.info("Starting Backend...");
        //serialManager.startStream();
        //serialManager.startStream();
        serialManager.addIdentifier(new int[]{4,3,2,1} , "Test", DataTypes.FLOAT);
        serialManager.addIdentifier(new int[]{4,3,2,1}, "Test2", DataTypes.FLOAT);
        serialManager.startStream();
        serialManager.getInfo();
    }
}

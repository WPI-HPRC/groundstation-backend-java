package com.hprc;

import com.hprc.serial.SerialManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger("Groundstation");
    private static final SerialManager serial = new SerialManager();

    public static void main(String[] args) throws IOException {
        logger.info("Starting Backend...");
        System.out.println(serial.getAvailableSerialPorts());
    }
}

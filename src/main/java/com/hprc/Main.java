package com.hprc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hprc.serialPort.SerialManager;

public class Main {
    public static Logger logger = LoggerFactory.getLogger("Ground-Station");
    public static int[] startBytes = {};
    public static int[] endBytes = {};
    public static SerialManager serialManager = new SerialManager(1000, startBytes, endBytes);

    public Main() {
    }

    public static void main(String[] args) {
        logger.info("Starting Backend...");

        serialManager.findSerialPort();
    }
}

package com.hprc.serial.listeners;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import org.slf4j.Logger;

public class OnDisconnect implements SerialPortDataListener {

    private SerialPort serialPort;
    private Logger logger;

    public OnDisconnect(SerialPort sp, Logger logger) {
        this.serialPort = sp;
        this.logger = logger;
    }
    @Override
    public int getListeningEvents() {
        return SerialPort.LISTENING_EVENT_PORT_DISCONNECTED;
    }

    @Override
    public void serialEvent(SerialPortEvent serialPortEvent) {
        logger.error("Serial Disconnected!");
    }
}

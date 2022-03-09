package com.hprc.serial.listeners;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import org.slf4j.Logger;

import java.util.ArrayList;

public class OnData implements SerialPortDataListener {
    private SerialPort sP;
    private Logger logger;
    private ArrayList<Integer> fillBuffer = new ArrayList<>();

    public OnData(SerialPort serialPort, Logger logger) {
        this.sP = serialPort;
        this.logger = logger;
    }

    @Override
    public int getListeningEvents() {
        return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
    }

    @Override
    public void serialEvent(SerialPortEvent serialPortEvent) {
        if (serialPortEvent.getEventType() != SerialPort.LISTENING_EVENT_DATA_AVAILABLE)
        return;
        byte[] newData = new byte[serialPortEvent.getSerialPort().bytesAvailable()];
        int numRead = serialPortEvent.getSerialPort().readBytes(newData, newData.length);

        ArrayList<Integer> decimalBytes = new ArrayList<>();

        for(byte data : newData) {
            decimalBytes.add((int) data);
        }
    }
}

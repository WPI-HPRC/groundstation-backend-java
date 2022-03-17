package com.hprc.serial.listeners;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.hprc.serial.SerialManager;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OnData implements SerialPortDataListener  {
    private SerialPort sP;
    private Logger logger;
    private int[] startBytes = {66,69,71,66};
    private int[] endBytes = {69,78,68,66};

    public OnData(SerialPort serialPort, Logger logger) {
        this.sP = serialPort;
        this.logger = logger;
    }

    @Override
    public int getListeningEvents() {
        return SerialPort.LISTENING_EVENT_DATA_RECEIVED;
    }

    @Override
    public void serialEvent(SerialPortEvent serialPortEvent) {
        int bytesAvailable = serialPortEvent.getSerialPort().bytesAvailable();

        byte[] buffer = new byte[bytesAvailable];
        List<Byte> dataList = new ArrayList<>();

        if(bytesAvailable > 1) {
            serialPortEvent.getSerialPort().readBytes(buffer, bytesAvailable);
            dataList = Arrays.asList(ArrayUtils.toObject(buffer));
            System.out.println(dataList);
        }
        
        for(int i=0; i < dataList.size(); i++) {

        }



        /*for(int i=0; i < newData.length; i++) {
            //decimalBytes.add((int) data);
            if(newData[i] == startBytes[2] && newData[i-1] == startBytes[1] && newData[i-2] == startBytes[0]) {
                feedBytes = true;
            } else {
                continue;
            }

            if(feedBytes) {
                decimalBytes.add((int) newData[i]);
            } else {
                continue;
            }
        }*/
    }
}

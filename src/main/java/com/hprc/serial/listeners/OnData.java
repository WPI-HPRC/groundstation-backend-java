package com.hprc.serial.listeners;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.hprc.serial.Identifier;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.hprc.serial.SerialManager.identifiers;

public class OnData implements SerialPortDataListener  {
    private SerialPort sP;
    private Logger logger;

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
        /*int bytesAvailable = serialPortEvent.getSerialPort().bytesAvailable();

        byte[] buffer = new byte[bytesAvailable];
        List<Byte> dataList = new ArrayList<>();

        if(bytesAvailable > 1) {
            serialPortEvent.getSerialPort().readBytes(buffer, bytesAvailable);
            dataList = Arrays.asList(ArrayUtils.toObject(buffer));
            System.out.println(dataList);

            for(Identifier identifier : identifiers) {
                int identIdx = findIdentIdx(dataList, identifier);

                if(identIdx != -1) {
                    int startIdx = identIdx + identifier.identifierBytes.size();
                    int endIdx = getNextIdentIdx(dataList, startIdx);
                    List<Byte> valList = dataList.subList(startIdx,endIdx);
                }
            }
        }*/
    }

    private int findIdentIdx(List<Byte> packet, Identifier identifier) {
        List<Integer> bytes = identifier.identifierBytes;

        for(int i=0; i < packet.size(); i++) {
            if(packet.get(i).intValue() == bytes.get(2) &&
            packet.get(i-1).intValue() == bytes.get(1) &&
            packet.get(i-2).intValue() == bytes.get(0)) {
                return i-2;
            }
        }

        return -1;
    }

    private int getNextIdentIdx(List<Byte> packet, int identIdx) {

        for(int i = identIdx; i < packet.size(); i++) {
            if(hasIdent(new ArrayList<>(
                    Arrays.asList(
                            packet.get(i-2).intValue(),
                            packet.get(i-1).intValue(),
                            packet.get(i).intValue())))) {
                return i-2;
            }
        }

        return packet.size();
    }

    private boolean hasIdent(ArrayList<Integer> packetData) {

        for(int i=0; i < identifiers.size(); i++) {
            if(identifiers.get(i).identifierBytes.equals(packetData)) {
                return true;
            }
        }

        return false;
    }
}

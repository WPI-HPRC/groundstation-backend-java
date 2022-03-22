package com.hprc.serial.listeners;

import com.fazecast.jSerialComm.SerialPortEvent;
import com.fazecast.jSerialComm.SerialPortPacketListener;
import com.fazecast.jSerialComm.SerialPort;
import com.hprc.serial.Identifier;
import com.hprc.serial.SerialManager;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.hprc.serial.SerialManager.identifiers;

public class OnPacket implements SerialPortPacketListener {
    private SerialPort sP;
    private Logger logger;

    public OnPacket(SerialPort serialPort, Logger logger) {
        this.sP = serialPort;
        this.logger = logger;
    }

    @Override
    public int getPacketSize() {
        return SerialManager.packetSize;
    }

    @Override
    public int getListeningEvents() {
        return SerialPort.LISTENING_EVENT_DATA_RECEIVED;
    }

    @Override
    public void serialEvent(SerialPortEvent event) {
        byte[] newData = event.getReceivedData();
        List<Byte> dataList = Arrays.asList(ArrayUtils.toObject(newData));

        System.out.println(dataList);

        /*for(Identifier identifier : identifiers) {
            int identIdx = findIdentIdx(dataList,identifier);

            if(identIdx != -1) {
                int startIdx = identIdx + identifier.identifierBytes.size();
                int endIdx = getNextIdentIdx(dataList, identIdx);
                List<Byte> valList = dataList.subList(startIdx, endIdx);
                System.out.println(valList);
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

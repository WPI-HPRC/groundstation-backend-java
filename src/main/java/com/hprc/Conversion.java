package com.hprc;

import java.util.List;

public class Conversion {

    public static void twosCompliment(List<Byte> data) {
        String binaryData = "";
        for (Byte datum : data) {
            String datumBinary = String.format("%8s", Integer.toBinaryString(datum & 0xFF)).replace(' ', '0');
            binaryData = binaryData + datumBinary;
        }

        System.out.println(binaryData);
    }
}

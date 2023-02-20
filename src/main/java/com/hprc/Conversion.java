package com.hprc;

import org.apache.commons.lang3.ArrayUtils;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;


public class Conversion {

    /**
     * Converts any decimal byte array to binary
     * @param data List of bytes to convert to binary
     * @return Binary string of input concatenated
     */
    private synchronized static String toBinary(List<Byte> data) {
        StringBuilder binaryData = new StringBuilder();
        for (Byte datum : data) {
            String datumBinary = String.format("%8s", Integer.toBinaryString(datum & 0xFF)).replace(' ', '0');
            binaryData.append(datumBinary);

        }
        return binaryData.toString();
    }

    /**
     *
     * @param data List of bytes to convert to a double
     * @return double value of a decimal byte array converted using the IEE754 standard
     */
    public synchronized static float toFloatIEEE754(List<Byte> data) {
        String binaryStr = toBinary(data);
        Long l = Long.parseLong(binaryStr, 2);

        return Float.intBitsToFloat(l.intValue());
    }

    /**
     *
     * @param data List of bytes to convert to an integer
     * @return integer value of a decimal byte array using the twos complement standard
     */
    public synchronized static short toSignedInt16(List<Byte> data) {
        String binary = toBinary(data);
        short conversion = (short) Integer.parseInt(binary,2);

        return conversion;
    }

    public synchronized static int toUnsignedInt(List<Byte> data) {
        String binary = toBinary(data);
        int conversion = Integer.parseInt(binary, 2);

        return conversion;
    }
}
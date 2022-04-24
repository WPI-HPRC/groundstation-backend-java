package com.hprc;

import java.util.List;


public class Conversion {

    /**
     * Converts any decimal byte array to binary
     * @param data List of bytes to convert to binary
     * @return Binary string of input concatenated
     */
    private static String toBinary(List<Byte> data) {
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
    public static int toFloatIEEE754(List<Byte> data) {
        String binaryStr = toBinary(data);

        //Rounds decimal to the thousands place
        //double scale = Math.pow(10,3);
        //return Integer.parseInt(binaryStr, 2);
        //return Float.intBitsToFloat(intBits);
        Long l = Long.parseLong(binaryStr, 2);
        return Math.round(Float.intBitsToFloat(l.intValue()));
        //return Math.round((Float.intBitsToFloat(Integer.parseInt(binaryStr, 2))) * scale) / scale;
    }

    /**
     *
     * @param data List of bytes to convert to an integer
     * @return integer value of a decimal byte array using the twos complement standard
     */
    public static int toSignedInt16(List<Byte> data) {

        String binary = toBinary(data);

        long l = Long.parseLong(binary,2);
        return (int) l;
    }
}
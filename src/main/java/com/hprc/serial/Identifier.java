package com.hprc.serial;

import java.util.ArrayList;

public class Identifier {

    public ArrayList<Integer> identifierBytes;
    public String name;
    public DataTypes dataType;

    Identifier(ArrayList<Integer> identifierBytes, String name, DataTypes dataType) {
        this.identifierBytes = identifierBytes;
        this.name = name;
        this.dataType = dataType;
    }
}
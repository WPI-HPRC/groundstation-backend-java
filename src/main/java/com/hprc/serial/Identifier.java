package com.hprc.serial;

public class Identifier {
        public int[] identifierBytes;
        public String name;
        public DataTypes dataType;

        Identifier(int[] identifierBytes, String name, DataTypes dataType) {
            this.identifierBytes = identifierBytes;
            this.name = name;
            this.dataType = dataType;
        }

        public String getIdentifier() {
            return identifierBytes.toString();
        }
    }
package com.hprc.serial;

import gnu.io.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hprc.Conversion;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings({"unused", "rawtypes", "SuspiciousMethodCalls"})
public class SerialManager implements SerialPortEventListener {
    private final Logger logger = LoggerFactory.getLogger("Serial Manager");
    private SerialPort comPort;
    private int baudRate = 0;

    private InputStream input;
    OutputStream output;

    public static List<Identifier> identifiers;
    public static HashMap<String, Object> telemetry = new HashMap<>();

    public SerialManager() {

        identifiers =  new ArrayList<>();

    }

    /**
     * Configuration method to set baud rate speed
     * @param baudRate Specified baud of serial stream
     */
    public void setBaudRate(int baudRate) {
        this.baudRate = baudRate;
    }

    /**
     * Configuration method to add identifiers in the serial stream
     * Can technically be any size but 3 bytes is recommended
     * MUST be in Decimal Byte format
     * Ex: serialManager.addIdentifier(new ArrayList<>(Arrays.asList(4,3,2,1)), "Velocity", DataTypes.FLOAT);
     * @param identifierBytes Arraylist of identifier bytes
     * @param name Name of the data you are looking to find
     * @param datatype Bit extraction method expected [int, float, string, etc.]
     */
    public void addIdentifier(ArrayList<Integer> identifierBytes, String name, DataTypes datatype) {
        Identifier identifier = new Identifier(identifierBytes,name,datatype);
        identifiers.add(identifier);
        telemetry.put(name, 0);
    }

    /**
     * Testing method to log to the console each identifier which has been added to the SerialManager
     */
    public void getConfig() {
        logger.info("Identifiers");
        for(Identifier ident : identifiers) {
            logger.info(String.format("%s - %s - %s", ident.name, ident.identifierBytes, ident.dataType));
        }
        logger.info(String.format("Baud Rate: %s", baudRate));
    }

    /**
     * Starts serial stream and updates telemetry hashmap
     * @throws IOException Throws an error if comport cannot carry out request
     */
    public void startStream() throws IOException {
        HashSet<CommPortIdentifier> h = getAvailableSerialPorts();

        int comPortCount = 0;

        for(CommPortIdentifier identifier : h) {
            comPortCount++;
            logger.info(identifier.getName() + String.format(" [%s]", comPortCount));
        }
        //Prompted port selection
        System.out.print("Select Port [1,2,?]: ");
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(System.in)
        );

        int portSelection = Integer.parseInt(reader.readLine());
        if(portSelection > h.size()) {
            logger.error("Not a valid port!");
            System.exit(0);
        }

        CommPortIdentifier selectedIdentifier = null;
        comPortCount = 0;
        for(CommPortIdentifier identifier : h) {
            comPortCount++;
            if(comPortCount == portSelection) {
                selectedIdentifier = identifier;
            }
        }

        if(baudRate == 0) {
            logger.error("Please set a correct baud rate!");
            System.exit(0);
        }

        try {
            assert selectedIdentifier != null;
            comPort = selectedIdentifier.open(this.getClass().getName(), 2000);
            comPort.setSerialPortParams(
                    baudRate,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);

            input = comPort.getInputStream();
            output = comPort.getOutputStream();

            comPort.addEventListener(this);
            comPort.notifyOnDataAvailable(true);

        } catch(Exception e) {
            logger.error(e.toString());
        }

    }

    /**
     * Method to return a HashSet of all serial ports that are available on the system, not ones that are closed!
     * @return all available serial ports
     */
    public HashSet<CommPortIdentifier> getAvailableSerialPorts() {
        HashSet<CommPortIdentifier> h = new HashSet<>();
        Enumeration thePorts = CommPortIdentifier.getPortIdentifiers();
        while (thePorts.hasMoreElements()) {
            CommPortIdentifier com = (CommPortIdentifier) thePorts.nextElement();

            if(com.getPortType() == CommPortIdentifier.PORT_SERIAL) {
                try {
                    CommPort thePort = com.open("CommUtil", 50);
                    thePort.close();
                    h.add(com);
                } catch (PortInUseException e) {
                    System.out.println("Port, "  + com.getName() + ", is in use.");
                } catch (Exception e) {
                    System.err.println("Failed to open port " +  com.getName());
                    e.printStackTrace();
                }
            }
        }
        return h;
    }

    /**
     * Closes the serial port, necessary to call at end of program on linux or the com port will be locked!
     */
    public synchronized void close() {
        if(comPort != null) {
            comPort.removeEventListener();
            comPort.close();
        }
    }

    /**
     * Overridden method from the serial port listener, called when the serial port receives a packet of data
     * @param serialPortEvent Object holding information relating to the incoming data
     * @implNote SerialPortEventListener
     */
    @Override
    public synchronized void serialEvent(SerialPortEvent serialPortEvent) {
        if (serialPortEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
			try {
                byte[] data = new byte[input.available()]; //Creates a byte array with size of available bytes
                int dataRead = input.read(data);

                List<Byte> bytes = new ArrayList<>();
                for (byte datum : data) {
                    bytes.add(datum);
                }

                Map<String, Integer> identifierLocations = findIdentifiers(bytes); //Locates all identifiers in array
                Object[] keys = identifierLocations.keySet().toArray();

                for(int i=0; i < identifierLocations.size(); i++) {
                    int startLocation = identifierLocations.get(keys[i]);
                    int endLocation = startLocation + 4;
                    List<Byte> telemetryData = bytes.subList(startLocation,endLocation);

                    /*
                      Loop identifiers and look for matches in the array
                      Coordinate data with identifiers
                     */
                    for(Identifier ident : identifiers) {
                        if(ident.name == keys[i]) {
                            if(ident.dataType == DataTypes.FLOAT) {
                                telemetry.put(ident.name, Conversion.toFloatIEEE754(telemetryData));
                            } else if(ident.dataType == DataTypes.SIGNED_INT) {
                                telemetry.put(ident.name, Conversion.toSignedInt16(telemetryData));
                            }
                        }
                    }
                }

                System.out.println(telemetry);

			} catch (Exception e) {
				logger.error(e.toString());
			}
		}
    }

    /**
     * Find identifiers in list of bytes
     * @param data List of bytes of data coming in from the serial port
     * @return A map of identifiers and its correlated start position in the array, sorted from least to highest in array
     */
    public Map<String, Integer> findIdentifiers(List<Byte> data) {
        HashMap<String, Integer> map = new HashMap<>();
        for(Identifier identifier: identifiers) {
            ArrayList<Integer> idBytes = identifier.identifierBytes;
            for(int i=0; i < data.size(); i++) {
                if(idBytes.get(2) == data.get(i).intValue() &&
                        idBytes.get(1) == data.get(i-1).intValue() &&
                        idBytes.get(0) == data.get(i-2).intValue()
                ) {
                    int startId = i + 1;
                    map.put(identifier.name, startId);
                }
            }
        }

        //Sorting Algorithm to sort from lowest to highest value
        return map.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,
                        LinkedHashMap::new));
    }
}

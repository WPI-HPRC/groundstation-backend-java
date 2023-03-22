package com.hprc.serial;

import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hprc.TelemetryServer;
import com.opencsv.CSVWriter;
import gnu.io.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hprc.Conversion;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings({"unused", "rawtypes", "SuspiciousMethodCalls"})
public class SerialManager implements SerialPortEventListener {
    private static final Logger logger = LoggerFactory.getLogger("Serial Manager");
    public SerialPort comPort;
    private int baudRate = 0;

    private InputStream input;
    OutputStream outputStream;

    private boolean loggingEnabled;

    public List<Identifier> identifiers;
    public Map<String, Object> telemetry;
    private int dataLogged = 0;

    private final String fileName;

    CSVWriter csvWriter;
    private final ObjectMapper mapper;

    private final TelemetryServer wss;

    public SerialManager() throws IOException {

        identifiers =  new ArrayList<>();
        telemetry = new HashMap<>();
        loggingEnabled = false;

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");
        int hours = LocalDateTime.now().getHour();
        int minutes = LocalDateTime.now().getMinute();
        int seconds = LocalDateTime.now().getSecond();

        String path = new File(SerialManager.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getParentFile().getPath();
        logger.info("Log File Path: " + path);
        fileName = String.format("%s/%s",path, String.format("(%s-%s-%s)-telemetry.csv",hours,minutes,seconds));

        mapper = new ObjectMapper();

        wss = new TelemetryServer(3005);

        Identifier connectedIdentifier = new Identifier(new ArrayList<>(Arrays.asList(82, 79, 67, 75)), "RocketConnected", DataTypes.IGNORE);
        identifiers.add(connectedIdentifier);
        telemetry.put("RocketConnected", 0);
    }

    /**
     * Configuration method to set baud rate speed
     * @param baudRate Specified baud of serial stream
     */
    public void setBaudRate(int baudRate) {
        this.baudRate = baudRate;
    }

    /**
     *
     * @param file
     */
    public void fileRead(File file) throws FileNotFoundException {
        Scanner fileScan = new Scanner(file);
        while(fileScan.hasNextLine()) {
            String data = fileScan.nextLine();
        }
        fileScan.close();
    }

    /**
     * Configuration method to enable data logging
     */
    public void enableLogging() {
        this.loggingEnabled = true;
    }

    /**
     * Configuration method to add identifiers in the serial stream
     * Can technically be any size but 3 bytes is recommended
     * MUST be in Decimal Byte format
     * Ex: serialManager.addIdentifier(new ArrayList<>(Arrays.asList(4,3,2,1)), "Velocity", DataTypes.FLOAT);
     * @param identifierBytes Arraylist of identifier bytes
     * @param name Name of the data you are looking to find
     * @param datatype Bit extraction method expected [int, float, string, etc.]2
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
    public synchronized void startStream() throws IOException, InterruptedException {
        HashSet<CommPortIdentifier> h = getAvailableSerialPorts();

        int comPortCount = 0;

        for(CommPortIdentifier identifier : h) {
            comPortCount++;
            logger.info(identifier.getName() + String.format(" [%s]", comPortCount));
        }

        //If no serial ports are found, prompt to use a predetermined file (SIMULATOR mode)
        if(comPortCount == 0) {
            logger.info("No serial ports found...");
            logger.info("Continue in SIMULATOR mode? Y/N");
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(System.in)
            );
            String line = reader.readLine();
            if(line.equalsIgnoreCase("Y")) {
                logger.info("Running SIMULATOR mode.  Please enter file path:");
                /* simulator mode, reading from a pre-existing .csv file */
                /* input the filepath */
                BufferedReader breader = new BufferedReader(
                    new InputStreamReader(System.in)
                );
                String fileNameLine = breader.readLine();


                File simFile = new File(fileNameLine);
                if(simFile.exists()) {
                    Scanner simScanner = new Scanner(simFile);
                    String IDs = simScanner.nextLine();

                    logger.info(IDs);
                    String[] idArray = IDs.split(",");

                    int index = 0;

                    wss.start();                
                    while(simScanner.hasNextLine()) {

                        String nextL = simScanner.nextLine();
                        telemetry.clear();
                        String[] lineArray = nextL.split(",");
                    
                        try {
                            for(int colIndex = 0; colIndex < idArray.length; colIndex++) {
                                switch(idArray[colIndex]) {
                                    case "AccelX":
                                        int accel_X = Integer.parseInt(lineArray[colIndex]);
                                        telemetry.put("AccelX", accel_X);
                                        break;
                                    case "AccelY":
                                        int accel_Y = Integer.parseInt(lineArray[colIndex]);
                                        telemetry.put("AccelZ", accel_Y);
                                        break;
                                    case "AccelZ":
                                        int accel_Z = Integer.parseInt(lineArray[colIndex]);
                                        telemetry.put("AccelY", accel_Z);
                                        break;
                                    case "GyroX":
                                        int gyro_X = Integer.parseInt(lineArray[colIndex]);
                                        telemetry.put("GyroX", gyro_X); // these are flipped for testing
                                        break;    
                                    case "GyroY":
                                        int gyro_Y = Integer.parseInt(lineArray[colIndex]);
                                        telemetry.put("GyroY", gyro_Y); // 
                                        break;
                                    case "GyroZ":
                                        int gyro_Z = Integer.parseInt(lineArray[colIndex]);
                                        telemetry.put("GyroZ", gyro_Z); // its on purpose, trust me!
                                        break;    
                                    case "Altitude":
                                        float altitude = Float.parseFloat(lineArray[colIndex]);
                                        telemetry.put("Altitude", altitude);
                                        break;    
                                    case "Temperature":
                                        float temperature = Float.parseFloat(lineArray[colIndex]);
                                        telemetry.put("Temperature", temperature);
                                        break;
                                    case "State":
                                        int state = Integer.parseInt(lineArray[colIndex]);
                                        telemetry.put("State", state);
                                        break;
                                    case "Timestamp":
                                        int time = Integer.parseUnsignedInt(lineArray[colIndex]);
                                        telemetry.put("Timestamp", time);
                                        break;
                                    default:
                                        float out = Float.parseFloat(lineArray[colIndex]);
                                        telemetry.put(idArray[colIndex], out);
                                }
                            }

    
    
    
    
    
    
                            String telemetryJson = mapper.writeValueAsString(telemetry);
    
                            wss.broadcast(telemetryJson);
                        } catch(NumberFormatException e) {

                        }
                    
                    Thread.sleep(10);

                }
                }
                

                Thread.sleep(1000);
                close();
                System.exit(0);


            } else {
                Thread.sleep(1000);
                close();
                System.exit(0);
            }
            
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
            outputStream = comPort.getOutputStream();

            comPort.addEventListener(this);
            comPort.notifyOnDataAvailable(true);

        } catch(Exception e) {
            logger.error(e.toString());
        }

        wss.start();
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

            if (com.getPortType() == CommPortIdentifier.PORT_SERIAL) {
                try {
                    CommPort thePort = com.open("CommUtil", 50);
                    thePort.close();
                    h.add(com);
                } catch (PortInUseException e) {
                    logger.error(com.getName() + " is in use!");
                } catch (Exception e) {
                    logger.error("Failed to open port " + com.getName());
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

    public synchronized void writeTelemetry() throws IOException {
        csvWriter = new CSVWriter(new FileWriter(fileName, true));

        List<String> keys = new ArrayList<>();
        for(Identifier ident : identifiers) {
            if(ident.dataType != DataTypes.IGNORE) {
                keys.add(ident.name);
            }
            //keys.add(ident.name);
        }
        Collections.sort(keys);

        if(dataLogged == 0) {
            String[] headerArr = keys.toArray(new String[keys.size()]);
            csvWriter.writeNext(headerArr);
        }

        List<String> data = new ArrayList<>();
        int telemSize = keys.size();
        for(int i=0; i < telemSize; i++) {
            data.add(telemetry.get(keys.get(i)).toString());
        }
        String[] dataArr = data.toArray(new String[data.size()]);

        if(!(csvWriter == null)) {
            csvWriter.writeNext(dataArr);
        }

        dataLogged++;

        if(csvWriter != null) {
            csvWriter.close();
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

                //System.out.println(bytes);

                Map<String, Integer> identifierLocations = findIdentifiers(bytes); //Locates all identifiers in array
                Object[] keys = identifierLocations.keySet().toArray();
                int locationsArrSize = identifierLocations.size() - 1;
                for(int i=0; i < locationsArrSize; i++) {
                    int startLocation = identifierLocations.get(keys[i]);
                    int endLocation = identifierLocations.get(keys[i+1]) - 3;
                    List<Byte> telemetryData = bytes.subList(startLocation,endLocation);

                    /*
                      Loop identifiers and look for matches in the array
                      Coordinate data with identifiers
                    */
                    for(Identifier ident : identifiers) {

                        if(ident.name == keys[i]) {

//                            System.out.println(ident.name + ": " + telemetryData);

                            if(ident.dataType == DataTypes.FLOAT) {
                                telemetry.put(ident.name, Conversion.toFloatIEEE754(telemetryData));
                            } else if(ident.dataType == DataTypes.SIGNED_INT) {
                                telemetry.put(ident.name, Conversion.toSignedInt16(telemetryData));
                            } else if(ident.dataType == DataTypes.UNSIGNED_INT) {
                                telemetry.put(ident.name, Conversion.toUnsignedInt(telemetryData));
                            } else if(ident.dataType == DataTypes.IGNORE) {
                                continue;
                            }
                        }
                    }
                }

                //System.out.println(telemetry);

                String telemetryJson = mapper.writeValueAsString(telemetry);

                wss.broadcast(telemetryJson);

                if(loggingEnabled) {
                    writeTelemetry();
                }

                if(telemetry.containsKey("Timestamp")) {
                    telemetry.put("RocketConnected", true);
                }

			} catch (Exception e) {
				logger.error(e.toString());
			}
		} else if(serialPortEvent.getEventType() == SerialPortEvent.OUTPUT_BUFFER_EMPTY) {
            System.out.println("SAFE");
        }

        else {
            telemetry.put("RocketConnected", false);
            try {
                manualPushTelemetry();
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void manualPushTelemetry() throws JsonProcessingException {
        String telemetryJson = mapper.writeValueAsString(telemetry);
        wss.broadcast(telemetryJson);
    }

    /**
     * Find identifiers in list of bytes
     * @param data List of bytes of data coming in from the serial port
     * @return A map of identifiers and its correlated start position in the array, sorted from least to highest in array
     */
    public synchronized Map<String, Integer> findIdentifiers(List<Byte> data) {
        HashMap<String, Integer> map = new HashMap<>();
        for(Identifier identifier: identifiers) {
            ArrayList<Integer> idBytes = identifier.identifierBytes;
            int dataSize = data.size();
            for(int i=1; i < dataSize; i++) {
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
package genericDashButton;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
//import java.net.URL;
//import java.net.URLConnection;
import java.net.*;
import java.security.MessageDigest;
import java.util.Scanner;

import javax.xml.bind.DatatypeConverter;

// import fasterJackson-packages
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * pi4j-packages
 *
 */
import com.pi4j.io.gpio.*;

public class App 
{
	public static final String RASPID = "0000000001";
	public static final String USER = "JAVA";
	public static final String PASS = "1ndonesia";
	
	static boolean isStarted = false; //indicator if process already started
	static String functionID; //determined functionID
	static String specialID; //special parameter for additional function
	static int color; //color for LED
	static String info;
	
	//fix problems with system inconsistencies
	static {
	System.setProperty("pi4j.linking","dynamic");
	}
	
    public static void main(String[] args) throws IOException {
    	boolean newProcess = false;
  
	    System.out.println("START");
	    
	    /*
	     * GPIO-Controller with GPIO-Port IO27/wiringPi02
	     * button.isHigh signalizes pressed button
	     * startes the respective process
	     */
	    //
	    final GpioController gpio = GpioFactory.getInstance();
	    final GpioPinDigitalInput button = gpio.provisionDigitalInputPin(RaspiPin.GPIO_02, PinPullResistance.PULL_DOWN);
	    
	    /*
	     * RGB-LED with GPIO-Port IO18/wiringPi01
	     * RGB-LED will be turned on, if SAP-Status is deliviered 
	     * e.g. yellow for running process, green for success
	     */
	    final GpioPinDigitalOutput pin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_01, "MyLED", PinState.HIGH);

	    /*
	     * TODO: Set RGB-LED to blue: ready for process
	     * color = xy pin.
	     */
	    pin.setShutdownOptions(true, PinState.LOW);
	    pin.low();
	    
	    /*
	     * Loop for running program to frequently check button status
	     */
	    while(true){
	    	
	    	/*
	    	 * if button is pressed the first time start process
	    	 */
	        if (button.isHigh() == true) {
	        	if (isStarted == false){	
	        		/*
	        		 * gather information about the hardware e.g. function
	        		 * returned String is JSON-Format
	        		 * Information can be converted to attributes
	        		 */
	        		pin.high();
	        	info = getGeneralInfo();
	        		InfoJson jsonInfo = convertFromJson(info);
	        		functionID = jsonInfo.getD().getFuncid(); 
	        		specialID = jsonInfo.getD().getFunspc();
	        		isStarted = executeFunction(jsonInfo);
	        	}
	        	
	        	/*
	        	 * execute function with numerous steps
	        	 */
	        	else {
	        		
	        		//isStarted = executeFunction(Info);
				}
	        }
	        //TODO set pin color
	    
    	
//    	//Standard Objekt:
//	    InfoJsonFormat InfoJsonFormat = new InfoJsonFormat("0000000001",
//    			"SAP_ERIDEA", "0000000001", "DCP-9022CD", 
//    			"RASPBERRY PI 0002 MASTERARBEIT",
//    			"HARDWAREKOMPONENTE ZUR VALIDIERUNG DER MASTERARBEIT");
//    	
//    	//Bei Standard Objekt die FunktionsId verändern:
//    	changeFunctionID(InfoJsonFormat, "0000000002");
//    	
//    	
//    	//Test Post:
//    	InfoJsonFormat jsonFormat2 = new InfoJsonFormat("0000000005",
//    			"SAP_ERIDEA", "0000000001", "DCP-9022CD", 
//    			"RASPBERRY PI 0002 MASTERARBEIT",
//    			"HARDWAREKOMPONENTE ZUR VALIDIERUNG DER MASTERARBEIT");
//    	
//    	sendRaspiInfo(convertToJson(jsonFormat2));  	
//    	
//        System.out.println(info);
	    } 
    }
    
    /*
     * decision maker depending on functionID
     * calling function
     */
    public static boolean executeFunction(InfoJson info) throws IOException {
    	
    	switch (functionID) {
		case "0000000001": //order for printing paper
			sendOrder(info);
			break;
			
		case "0000000002": //visitor-manager
			executeVisit(info);
			break;
			
		case "0000000003": //meeting-manager
			executeAgendaItem(info);
			break;

		default:
			System.out.println("TODO: print error on display, that functionID is not set");
			break;
		}
    	
    	return isStarted; 
    }
    
    /**
     * executes sales order
     * 
     * @param json String
     * @throws IOException
     */
    public static final void sendOrder(InfoJson info) throws IOException{
    	/*
    	 * URL for Odata-Service, Json-Body will be added to send information e.g. special parameter
    	 * possibility to change json with specific attributes
    	 */
    	String sUrl = "http://eridea.privatedns.org:8002/sap/opu/odata/sap/ZRASP_GENERIC_SRV/RASINFOSet()?$format=json";
    	
    	URL serverURL = new URL(sUrl);
    	
    	HttpURLConnection urlConnection = (HttpURLConnection)serverURL.openConnection();
    	
    	// Indicate that we want to write to the HTTP request body
    	urlConnection.setDoOutput(true);
    	urlConnection.setRequestMethod("POST");
    	
    	// Writing the post data to the HTTP request body
    	BufferedWriter httpRequestBodyWriter = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream()));
    	String json = convertToJson(info);
    	httpRequestBodyWriter.write(json);
    	httpRequestBodyWriter.close();
    	
    	// Reading from the HTTP response body
        Scanner httpResponseScanner = new Scanner(urlConnection.getInputStream());
        while(httpResponseScanner.hasNextLine()) {
            System.out.println(httpResponseScanner.nextLine());
        }
        httpResponseScanner.close();	
        
        //start process or close process and TODO set LED green
        isStarted = false;
    }
    
    /**
     * executes visit
     * 
     * @param json String
     * @throws IOException
     */
    public static final void executeVisit(InfoJson info) throws IOException{
    	/*
    	 * URL for Odata-Service, Json-Body will be added to send information e.g. special parameter
    	 * possibility to change json with specific attributes
    	 * Timestamp of visit, return: feedback for visitor
    	 */
    	String sUrl = "http://eridea.privatedns.org:8002/sap/opu/odata/sap/ZRASP_GENERIC_SRV/RASINFOSet()?$format=json";
    	
    	URL serverURL = new URL(sUrl);
    	
    	HttpURLConnection urlConnection = (HttpURLConnection)serverURL.openConnection();
    	
    	// Indicate that we want to write to the HTTP request body
    	urlConnection.setDoOutput(true);
    	urlConnection.setRequestMethod("POST");
    	
    	// Writing the post data to the HTTP request body
    	BufferedWriter httpRequestBodyWriter = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream()));
    	String json = convertToJson(info);
    	httpRequestBodyWriter.write(json);
    	httpRequestBodyWriter.close();
    	
    	// Reading from the HTTP response body
        Scanner httpResponseScanner = new Scanner(urlConnection.getInputStream());
        while(httpResponseScanner.hasNextLine()) {
            System.out.println(httpResponseScanner.nextLine());
        }
        httpResponseScanner.close();	
        
        //start process or close process and TODO set LED green
        isStarted = false;
    }
    
    /**
     * executes agenda item
     * 
     * @param json String
     * @throws IOException
     */
    public static final void executeAgendaItem(InfoJson info) throws IOException{
    	//create TimeStamp to send beginning or end date of meeting
    	
    	/*
    	 * URL for Odata-Service, Json-Body will be added to send information e.g. special parameter
    	 * possibility to change json with specific attributes
    	 */
    	String sUrl = "http://eridea.privatedns.org:8002/sap/opu/odata/sap/ZRASP_GENERIC_SRV/RASINFOSet()?$format=json";
    	
    	URL serverURL = new URL(sUrl);
    	
    	HttpURLConnection urlConnection = (HttpURLConnection)serverURL.openConnection();
    	
    	// Indicate that we want to write to the HTTP request body
    	urlConnection.setDoOutput(true);
    	urlConnection.setRequestMethod("POST");
    	
    	// Writing the post data to the HTTP request body
    	BufferedWriter httpRequestBodyWriter = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream()));
    	String json = convertToJson(info);
    	httpRequestBodyWriter.write(json);
    	httpRequestBodyWriter.close();
    	
    	// Reading from the HTTP response body
        Scanner httpResponseScanner = new Scanner(urlConnection.getInputStream());
        while(httpResponseScanner.hasNextLine()) {
            System.out.println(httpResponseScanner.nextLine());
        }
        httpResponseScanner.close();	
        
        //close process and TODO set LED green
        if (isStarted == false){
        	isStarted = true;
        }
        else{
        	isStarted = false;
        }
    }
    
    public static String getGeneralInfo() throws IOException {
    	String body = null;

        // Create URL with fix constant Component-ID
        String sUrl = "http://eridea.privatedns.org:8002/sap/opu/odata/sap/ZRASP_GENERIC_SRV/RASINFOSet('" + RASPID + "')?$format=json";

        body = buildString(sUrl);
    	
    	return body; 
    }
    
    //benötigt: Methode ConvertToJSON: wandelt den mitgelieferten Body in eine JSON-Datei um
    /*
     * Dateistruktur der JSON-Datei 
     * RASPID TYPE CHAR10 KEY
     * CUSTID TYPE CHAR10
     * FUNCID TYPE CHAR10
     * FUNSPC TYPE CHAR10
     * RASNAM  TYPE CHAR30
     * RASDES TYPE CHAR100
     * 
     */
    
    //Wenn du es in einer Datei haben willst und nicht in einer Klasse musst du das noch implementieren.
    //Ich nehme aber mal stark an, dass du es als Java Klasse haben möchtest.
    
    /**
     * Wandelt ein JsonFormatObjekt in einen Json String um.
     * 
     * @param obj JsonObjekt (bzw JsonEintrag)
     * @return JsonString
     * @throws JsonGenerationException
     * @throws JsonMappingException
     * @throws IOException
     */
    public static String convertToJson(InfoJson obj) throws JsonGenerationException, JsonMappingException, IOException{
    	ObjectMapper objectMapper = new ObjectMapper();
    	
    	return objectMapper.writeValueAsString(obj);
    }
    
    
    /**
     * Wandelt einen String mit Json in ein JsonFormat Objekt um.
     * 
     * @param json JsonString
     * @return JsonEintrag in Form eines JavaObjekts
     * @throws JsonParseException
     * @throws JsonMappingException
     * @throws IOException
     */
    public static InfoJson convertFromJson(String json) throws JsonParseException, JsonMappingException, IOException{
    	ObjectMapper objectMapper = new ObjectMapper();
    	
    	return objectMapper.readValue(json, InfoJson.class);
    }
    

    //benötigt: Methode ChangeFuncid: ändert den JSON-Eintrag der Function-ID(FUNCID)
    
    /**
     * Erwartet das JSON Objekt (bzw Eintrag) und die neue FunktionsID.
     * 
     * @param jsonFormat JsonEintrag
     * @param newFunctionId NewFunktionId
     */
    public static final void changeFunctionID(InfoJson jsonFormat, String newFunctionId){
    	jsonFormat.getD().setFuncid(newFunctionId);
    }
    
    
    //benötigt: Methode SendService: hängt den abgeänderten JSON-Body an den Service an und
    //"versendet" ihn, d.h. durch den aufgerufenen Service mit Body wird nun POST ausgeführt
    
    //Zum nachlesen: https://www.techcoil.com/blog/how-to-send-http-post-requests-with-java-without-using-any-external-libraries/
    
    /**
     * Die Funktion nimmt einen beliebigen String entgegen und sendet einen POST Request an die URL.
     * 
     * @param json String
     * @throws IOException
     */
    public static final void sendRaspiInfo(String json) throws IOException{
    	
    	//TODO Diese URL ist nicht die richtige für Post!!!!!!!
    	String sUrl = "http://eridea.privatedns.org:8002/sap/opu/odata/sap/ZRASP_GENERIC_SRV/RASINFOSet('" + RASPID + "')?$format=json";
    	
    	URL serverURL = new URL(sUrl);
    	
    	HttpURLConnection urlConnection = (HttpURLConnection)serverURL.openConnection();
    	
    	// Indicate that we want to write to the HTTP request body
    	urlConnection.setDoOutput(true);
    	urlConnection.setRequestMethod("POST");
    	
    	// Writing the post data to the HTTP request body
    	BufferedWriter httpRequestBodyWriter = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream()));
    	httpRequestBodyWriter.write(json);
    	httpRequestBodyWriter.close();
    	
    	// Reading from the HTTP response body
        Scanner httpResponseScanner = new Scanner(urlConnection.getInputStream());
        while(httpResponseScanner.hasNextLine()) {
            System.out.println(httpResponseScanner.nextLine());
        }
        httpResponseScanner.close();	
    }
    
    
    public static InputStream getStream(String odataurl) throws IOException{
	        String name = USER;
	        String password = PASS;
	
	        String authString = name + ":" + password;   
	        String authStringEnc = DatatypeConverter.printBase64Binary(authString.getBytes("UTF-8"));
	        
	        URL url = new URL(odataurl);
	        URLConnection urlConnection = url.openConnection();
	        urlConnection.setRequestProperty("Authorization", "Basic " + authStringEnc);
	        InputStream inputStream = urlConnection.getInputStream();
	        
	        return inputStream;
    }
    
    public static String buildString(String odataurl) throws IOException {
		InputStreamReader isr = new InputStreamReader(getStream(odataurl));
		
		int numCharsRead;
		char[] charArray = new char[1024];
		StringBuffer sb = new StringBuffer();
		
		
		while ((numCharsRead = isr.read(charArray)) > 0) {
		    sb.append(charArray, 0, numCharsRead);
		}
		return sb.toString();
    }
    

    public static String sha256(String base) {
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(base.getBytes("UTF-8"));
            StringBuffer hexString = new StringBuffer();

            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if(hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            System.out.println(hexString.toString());
            return hexString.toString();
        } catch(Exception ex){
            throw new RuntimeException(ex);
        }
    }
}

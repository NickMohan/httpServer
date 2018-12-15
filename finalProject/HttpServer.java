import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.*;
import javax.net.ssl.*;



public class HttpServer implements Runnable{
	//Static variables
	static final String DEFAULT_FILE = "index.html";
	static final String FILE_404 = "404.html";
	static final File ROOT = new File("root/");

	//More global variables
	private Socket client;
	private static Logger actLog;
	private static Logger errLog;
	private static boolean connection = false;

	//Constructor for the http server class
	public HttpServer(Socket cl, Logger act, Logger err){
		client = cl;
		actLog = act;
		errLog = err;
	}

	//Main method for the http server
	public static void httpserver(Socket connect){
		actLog.finer("Connected to Client: "+connect.toString());

		//Initialize streams outside of try for catching errors
		BufferedReader in = null;
		PrintWriter out = null;
		BufferedOutputStream fileOut = null;

		try{

			//set up all IO connections for the server
			in = new BufferedReader(new InputStreamReader(connect.getInputStream()));
			out = new PrintWriter(connect.getOutputStream());
			fileOut = new BufferedOutputStream(connect.getOutputStream());

			//Initialize some variables for later use
			String fileRequested = null;
			String httpRequestType = null;
			int contentLength = -1;
			String contentType = "";
			String connectKeepAlive = "";

			actLog.finer("Finish I/O Connections");

			//Request Line Parsing
			String requestLine = in.readLine();
			System.out.println("Request Line: "+ requestLine);
			StringTokenizer requestLineTokenizer = new StringTokenizer(requestLine);
			httpRequestType = requestLineTokenizer.nextToken().toUpperCase();
			fileRequested = requestLineTokenizer.nextToken().toLowerCase();
			actLog.finer("Request Parsed");

			//This parses for the keep alive header and ends if needs to
			String temp = ".";

			//Loops through all the headers extracting key information we are looking for 
			while(!(temp.equals(""))){
				temp = in.readLine();
				StringTokenizer headParse;
				if(temp.startsWith("Connection:")){
 					headParse = new StringTokenizer(temp);
 					headParse.nextToken();
					connectKeepAlive = headParse.nextToken();
				}
				if(temp.startsWith("Content-Type:")){
 					headParse = new StringTokenizer(temp);
 					headParse.nextToken();
					contentType = headParse.nextToken();
				}
				if(temp.startsWith("Content-Length:")){
					headParse = new StringTokenizer(temp);
 					headParse.nextToken();
					contentLength = Integer.parseInt(headParse.nextToken());
				}
			}

			//Set the connection varibale for keep alive or not
			connection = (connectKeepAlive.equalsIgnoreCase("Keep-Alive")) ? true : false;

			//For the GET and the HEAD method
			if(httpRequestType.equalsIgnoreCase("GET") || httpRequestType.equalsIgnoreCase("HEAD")){

				//Adding index.html for / requests
				if(fileRequested.endsWith("/")){
					fileRequested += DEFAULT_FILE;
				}

				actLog.finer("File Requested Path:" + fileRequested);

				//Turn the file into a byte string for sending back
				File file = new File(ROOT, fileRequested);
				int fileLength = (int) file.length();
				String fileType = getContentType(fileRequested);

				System.out.println(fileLength+"\t"+fileType);

				byte[] fileData = fileDataToBytes(file,fileLength);

				//Respond with these headers
				out.println("HTTP/1.1 200 OK");
				out.println("Server: TEST");
				out.println("Date: "+new Date());
				out.println("Content-type:" + fileType);
				out.println("Content-length: "+fileLength);
				out.print("\r\n\r\n");
				//out.println();
				out.flush();

				//If a get method return with the file and if head not
				if(httpRequestType.equals("GET")){
					fileOut.write(fileData,0,fileLength);
					fileOut.flush();
					actLog.finer("GET Request Returned");
				}
				else{ actLog.finer("HEAD Request Returned"); }
			}

			//For a POST method request
			else if(httpRequestType.equalsIgnoreCase("POST")){
				StringTokenizer postParser;
				String scriptPath = fileRequested;

				//Gets the request body into a string
				StringBuilder body = new StringBuilder(contentLength);
				for(int i = 0; i< contentLength; i++){
					char c = (char) in.read();
					body.append((char) c);
				}
				
					
				System.out.println("Content Type:  "+contentType);
				System.out.println("Content Length:  "+contentLength);
				System.out.println("Body:  "+body.toString());

				//We only except multipart form data and send bad request to not that
				if(!contentType.equalsIgnoreCase("multipart/form-data;") && !contentType.equalsIgnoreCase("application/x-www-form-urlencoded")){
					fileNotFound(out,fileOut);	
				}
				else{

				//This is the headers sent back from a POST request
				out.println("HTTP/1.1 200 OK");
				out.println("Server: TEST");
				out.println("Date: "+new Date());
				out.println("Content-length: 0");
				out.print("\r\n\r\n");
				//out.println();
				out.flush();

				}
				actLog.finer("POST Request Returned");

			}

			//Options request to send back the avaliable HTTP methods
			else if(httpRequestType.equalsIgnoreCase("OPTIONS")){
				actLog.finer("OPTIONS Request recieved");
				
				out.println("HTTP/1.1 200 OK");
				out.println("Allow: GET, HEAD, OPTIONS, POST");
				out.println("Server: TEST");
				out.println("Date: "+new Date());
				out.println("Content-length: 0");
				out.print("\r\n\r\n");
				out.flush();
				actLog.finer("OPTIONS Request Returned");
			}

		}	
		//Catching exceptions down here 
		catch(FileNotFoundException z){
			errLog.finer("File Not Found Exception: "+z); 
			try{fileNotFound(out,fileOut);}
			catch(IOException a){}
		}
		//More exception catching 
		catch(IOException x){errLog.finer("IOException: " + x);}
		catch(NullPointerException y){errLog.finer("NullPointerException: "+y);}
		catch(Exception e){errLog.finer("Exception: "+ e);}
		finally{
			try {
				//This closes connections of the header is not keep alive and keeps alive with a timeout if not
				if(!connection) {
					in.close();
					out.close();
					fileOut.close();
					connect.close();
					System.out.println("Closed connection"); 
				}
				else if(connection) {
					connect.setKeepAlive(true);
					System.out.println("Client keep alive: " + connect.getKeepAlive());
					connect.setSoTimeout(5000);
					System.out.println("So_TimeOut set: " + connect.getSoTimeout());
				}
			}
			catch(IOException x){errLog.finer("IOException: "+x);}
		}
	}

	//This takes a requested file and parses it to get the associated MIME type
	private static String getContentType(String fileRequested){
		if(fileRequested.endsWith(".html") || fileRequested.endsWith(".htm")){
			return "text/html";
		}
		else if(fileRequested.endsWith(".ico")){
			return "image/x-icon";
		}
		else if(fileRequested.endsWith(".js")){
			return "application/javascript";
		}
		else if(fileRequested.endsWith(".css")){
			return "text/css";
		}
		else if(fileRequested.endsWith(".php")){
			return "application/x-php";
		}
		else{
			return "text/plain"; 
		}
	}

	//Turns a file into bytes array
	private static byte[] fileDataToBytes(File file, int length) throws IOException{
		FileInputStream fileBytes = null;
		byte[] data = new byte[length];

		try{
			fileBytes = new FileInputStream(file);
			fileBytes.read(data);
		} finally {
			if(fileBytes != null){
				fileBytes.close();
			}
		}
		return data;
	}

	//Sends back a 404 if a file not found exception is thrown
	private static void fileNotFound(PrintWriter out, BufferedOutputStream fileOut)throws IOException{
		File file = new File(ROOT,FILE_404);
		int fileLength = (int) file.length();
		String fileType = getContentType(FILE_404);

		System.out.println(fileLength+"\t"+fileType);

		byte[] fileData = fileDataToBytes(file,fileLength);

		//This sends back the 404 page if the requested file is not found 
		out.println("HTTP/1.1 404 File Not Found");
		out.println("Server: TEST");
		out.println("Date: "+new Date());
		out.println("Content-type:" + fileType);
		out.println("Content-length: "+fileLength);
		out.print("\r\n\r\n");
		//out.println();
		out.flush();

		//Sends backs the file data 
		fileOut.write(fileData,0,fileLength);
		fileOut.flush();
		actLog.finer("404 Returned");
	}

	//Overloaded run method from runnable class for the server multithreading
	public void run(){
		httpserver(client);
	}
}

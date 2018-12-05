import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.*;

//---------------------------
//			TODO
//---------------------------
//Logging 				[X]
//POST					[]
//HEAD 					[X]							
//Better MIME Types 			[]
//Keep-Alive Header    		        []
//Add Timeout for Files that dont show  []
//
//Read more into header(keep alive, cookies?)
//Compression?(GZIP)
//SSL?
//Authentication?(User and Password)?
//Other Things?


public class HttpServer implements Runnable{
	static final String DEFAULT_FILE = "index.html";
	static final String FILE_404 = "404.html";
	static final File ROOT = new File("root/pq-doc/");

	private Socket client;
	private static Logger actLog;
	private static Logger errLog;
	private static boolean connection = false;

	public HttpServer(Socket cl, Logger act, Logger err){
		client = cl;
		actLog = act;
		errLog = err;
	}


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

			//System.out.println(connectKeepAlive+"  "+contentType+"  "+contentLength);

			connection = (connectKeepAlive.equalsIgnoreCase("Keep-Alive")) ? true : false;

			if(httpRequestType.equalsIgnoreCase("GET") || httpRequestType.equalsIgnoreCase("HEAD")){

				if(fileRequested.endsWith("/")){
					fileRequested += DEFAULT_FILE;
				}

				actLog.finer("File Requested Path:" + fileRequested);

				File file = new File(ROOT, fileRequested);
				int fileLength = (int) file.length();
				String fileType = getContentType(fileRequested);

				System.out.println(fileLength+"\t"+fileType);

				byte[] fileData = fileDataToBytes(file,fileLength);

				out.println("HTTP/1.1 200 OK");
				out.println("Server: TEST");
				out.println("Date: "+new Date());
				out.println("Content-type:" + fileType);
				out.println("Content-length: "+fileLength);
				out.print("\r\n\r\n");
				//out.println();
				out.flush();

				if(httpRequestType.equals("GET")){
					fileOut.write(fileData,0,fileLength);
					fileOut.flush();
					actLog.finer("GET Request Returned");
				}
				else{ actLog.finer("HEAD Request Returned"); }
			}

			else if(httpRequestType.equalsIgnoreCase("POST")){
				StringTokenizer postParser;
				String scriptPath = fileRequested;

				//Gets the request body into a string
				StringBuilder body = new StringBuilder(contentLength);
				for(int i = 0; i< contentLength; i++){
					char c = (char) in.read();
					body.append((char) c);
				}

				System.out.println("Body:  "+body.toString());

				//We only except multipart form data and send bad request to not that
				if(!contentType.equalsIgnoreCase("multipart/form-data;")){
					fileNotFound(out,fileOut);	
				}
				else{

					//Script should run here and response here should be formulated
					//All done right here

				}

				//Old stuff commented out is at the bottom of the page

				actLog.finer("POST Request Returned");

			}

			else if(httpRequestType.equalsIgnoreCase("OPTIONS")){
				actLog.finer("OPTIONS Request recieved");
				
				out.println("HTTP/1.1 200 OK");
				out.println("Allow: GET, HEAD, OPTIONS");
				out.println("Server: TEST");
				out.println("Date: "+new Date());
				out.println("Content-length: 0");
				out.print("\r\n\r\n");
				//out.println();
				out.flush();
				actLog.finer("OPTIONS Request Returned");
			}

		}	
		catch(FileNotFoundException z){
			errLog.finer("File Not Found Exception: "+z); 
			try{fileNotFound(out,fileOut);}
			catch(IOException a){}
		}
		catch(IOException x){errLog.finer("IOException: " + x);}
		catch(NullPointerException y){errLog.finer("NullPointerException: "+y);}
		catch(Exception e){errLog.finer("Exception: "+ e);}
		finally{
			try{
			//	if(!connection){
					in.close();
					out.close();
					fileOut.close();
					connect.close();
					System.out.println("Closed Connections");
			//	}
			//	else{
			//		System.out.println("Connections left open");
			//	}
			}
			catch(IOException x){errLog.finer("IOException: "+x);}
		}
	}

	//This should support more MIME types in the future
	private static String getContentType(String fileRequested){
		//Need to change this to split at the . and then get everything afterwards
		//then need to call the function that returns mime types from a scanner text file

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
	//String fileType = fileRequested.split("\\.",0);
	//return "text/"+fileType

	//I dont really know how the file input stream works but this works
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


		out.println("HTTP/1.1 404 File Not Found");
		out.println("Server: TEST");
		out.println("Date: "+new Date());
		out.println("Content-type:" + fileType);
		out.println("Content-length: "+fileLength);
		out.print("\r\n\r\n");
		//out.println();
		out.flush();

		fileOut.write(fileData,0,fileLength);
		fileOut.flush();
		actLog.finer("404 Returned");
	}

	public void run(){
		httpserver(client);
	}


}




				//This should send the request body to the script for processing and the return the response
				//to the client

				/*
				out.println("HTTP/1.1 200 OK");
				out.println("Server: TEST");
				out.println("Date: "+new Date());
				out.println("Content-length: "); 		//FILL OUT
				out.println("Content-Type: "); 			//FILL OUT
				out.print("\r\n\r\n");
				//out.println();
				out.flush();

				//This should return the response in the body for the POST request
				
				//fileOut.write(fileData,0,fileLength);
				//fileOut.flush();
				//out.flush();
				*/

//				if(fileRequested.endsWith("/")){
//					fileRequested += DEFAULT_FILE;
//				}	
				
//				File file = new File(ROOT, fileRequested);
//				int fileLength = (int) file.length();
//				String fileType = getContentType(fileRequested);

//				System.out.println(fileLength+"\t"+fileType);

//				byte[] fileData = fileDataToBytes(file,fileLength);

				//for(byte x : fileData){System.out.print(x+" ");}

				//out.println("HTTP/1.1 200 OK");
				//out.println("Server: TEST");
				//out.println("Date: "+new Date());
//				out.println("Content-type:" + fileType);
//				out.println("Content-length: "+fileLength);
				//out.print("\r\n\r\n");
				//out.println();
				//out.flush();

				//fileOut.write(fileData,0,fileLength);
				//fileOut.flush();



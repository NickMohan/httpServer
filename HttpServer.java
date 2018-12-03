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
//Better MIME Types 	[]
//Keep-Alive Header     []
//
//Read more into header(keep alive, cookies?)
//Compression?(GZIP)
//SSL?
//Authentication?(User and Password)?
//Other Things?


public class HttpServer implements Runnable{
	static final String DEFAULT_FILE = "index.html";
	static final String FILE_404 = "404.html";
	static final File ROOT = new File("root/");

	private Socket client;
	private static Logger actLog;
	private static Logger errLog;

	public HttpServer(Socket cl, Logger act, Logger err){
		client = cl;
		actLog = act;
		errLog = err;
	}


	public static void httpserver(Socket connect){
		actLog.finer("Connected to Client: "+connect.toString());
		BufferedReader in = null;
		PrintWriter out = null;
		BufferedOutputStream fileOut = null;

		try{
			in = new BufferedReader(new InputStreamReader(connect.getInputStream()));
			out = new PrintWriter(connect.getOutputStream());
			fileOut = new BufferedOutputStream(connect.getOutputStream());
			String fileRequested = null;
			String httpRequestType = null;
			actLog.finer("Finish I/O Connections");

			//Was hanging here idk if still does havent really done anything with it

			//Adding more header parsing up here for cookies and other things like 
			//keep alive and such idk that just seems like a lot of work at the moment



			String requestLine = in.readLine();
			System.out.println("Request Line: "+ requestLine);
			StringTokenizer requestLineTokenizer = new StringTokenizer(requestLine);

			httpRequestType = requestLineTokenizer.nextToken().toUpperCase();
			fileRequested = requestLineTokenizer.nextToken().toLowerCase();
			actLog.finer("Request Parsed");

			//Also need to add HEAD and POST and any other methods we want
			//Also methods not found would work too(I think this is trace)
			//Also need to add file not found so if index.html isnt there or something else 
			//and a 404 page or something or error page idk needs something 

			if(httpRequestType.equals("GET") || httpRequestType.equals("HEAD")){

				if(fileRequested.endsWith("/")){
					fileRequested += DEFAULT_FILE;
				}

				actLog.finer("File Requested Path:" + fileRequested);

				File file = new File(ROOT, fileRequested);
				int fileLength = (int) file.length();
				String fileType = getContentType(fileRequested);

				System.out.println(fileLength+"\t"+fileType);

				byte[] fileData = fileDataToBytes(file,fileLength);

				//for(byte x : fileData){System.out.print(x+" ");}

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

			//Have to extract data from the body of the request then send it to the script then return contents
			// script = fileRequested
			// content type is important and needs to be parsed out to read what is in the body
			//
			else if(httpRequestType.equals("POST")){
				StringTokenizer postParser;
				String scriptPath = fileRequested;
				String contentType = "";
				String requestBodyData;
				String temp = "";
				int contentLength = -1;
				

				while(contentType == "" || contentLength == -1){
					temp = in.readLine();
					if(temp.startsWith("Content-Type:")){
 						postParser = new StringTokenizer(temp);
 						postParser.nextToken();
						contentType = postParser.nextToken();
					}
					if(temp.startsWith("Content-Length:")){
						postParser = new StringTokenizer(temp);
 						postParser.nextToken();
						contentLength = Integer.parseInt(postParser.nextToken());
					}
					//gotta split here when blank line before request body
					//Have to parse out the request body here and I do not know how to do that
				}
				//This should send the request body to the script for processing and the return the response
				//to the client


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

				actLog.finer("POST Request Returned");


			}

			else if(httpRequestType.equals("OPTIONS")){
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
				in.close();
				out.close();
				fileOut.close();
				connect.close();
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
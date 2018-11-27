import java.io.*;
import java.net.*;
import java.util.*;

//---------------------------
//			TODO
//---------------------------
//Logging
//POST
//HEAD
//Better MIME Types
//Read more into header(keep alive, cookies?)
//Compression?(GZIP)
//SSL?
//Authentication?(User and Password)?
//Other Things?


public class HttpServer implements Runnable{
	static final String DEFAULT_FILE = "index.html";
	static final String FILE_404 = "404.html";
	static final File ROOT = new File(".");

	private Socket client;

	public HttpServer(Socket cl){
		client = cl;
	}


	public static void httpserver(Socket connect){
		System.out.println("Connected to Client: "+connect.toString());
		BufferedReader in = null;
		PrintWriter out = null;
		BufferedOutputStream fileOut = null;

		try{
			in = new BufferedReader(new InputStreamReader(connect.getInputStream()));
			out = new PrintWriter(connect.getOutputStream());
			fileOut = new BufferedOutputStream(connect.getOutputStream());
			String fileRequested = null;
			String httpRequestType = null;
			System.out.println("Finish I/O Connections");

			//THIS IS IMPORTANT BECAUSE IT BREAKS THE CLIENt CONNECTION SOMETIMES
			//Seems like client connects goes through then goes through again and hangs here
			//idk why need to figure that out 
			//I think it is something with the favicon request but I am not too sure because it does not
			//hang when making requests with curl

			String requestLine = in.readLine();
			System.out.println("Request Line: "+ requestLine);
			StringTokenizer requestLineTokenizer = new StringTokenizer(requestLine);

			httpRequestType = requestLineTokenizer.nextToken().toUpperCase();
			fileRequested = requestLineTokenizer.nextToken().toLowerCase();
			System.out.println("Request Parsed");

			//Also need to add HEAD and POST and any other methods we want
			//Also methods not found would work too(I think this is trace)
			//Also need to add file not found so if index.html isnt there or something else 
			//and a 404 page or something or error page idk needs something 

			if(httpRequestType.equals("GET") || httpRequestType.equals("HEAD")){

				if(fileRequested.endsWith("/")){
					fileRequested += DEFAULT_FILE;
				}

				System.out.println("File Requested Path:" + fileRequested);

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
					System.out.println("GET Request Returned");
				}
				else{ System.out.println("HEAD Request Returned"); }
			}

			else if(httpRequestType.equals("POST")){

			}

			else if(httpRequestType.equals("OPTIONS")){
				System.out.println("OPTIONS Request recieved");
				out.println("HTTP/1.1 200 OK");
				out.println("Allow: GET, HEAD, OPTIONS");
				out.println("Server: TEST");
				out.println("Date: "+new Date());
				out.println("Content-length: 0");
				out.print("\r\n\r\n");
				//out.println();
				out.flush();
				System.out.println("OPTIONS Request Returned");
			}

		}	
		catch(FileNotFoundException z){
			System.out.println("File Not Found Exception: "+z); 
			try{fileNotFound(out,fileOut);}
			catch(IOException a){}
		}
		catch(IOException x){System.out.println("IOException: " + x);}
		catch(NullPointerException y){System.out.println("NullPointerException: "+y);}
		catch(Exception e){System.out.println("Exception: "+ e);}
		finally{
			try{
				in.close();
				out.close();
				fileOut.close();
				connect.close();
			}
			catch(IOException x){System.out.println("IOException: "+x);}
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
		System.out.println("404 Returned");
	}

	public void run(){
		httpserver(client);
	}


}
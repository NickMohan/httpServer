import java.io.*;
import java.net.*;
import java.util.*;

public class Server{
	static final File ROOT = new File(".");
	static final int PORT = 8080; 
	static final String DEFAULT_FILE = "index.html";

	/*
	Another error is that client goes throught and reconnects or something and the 
	program hangs trying to request a favicon or something after the I/O connections 
	I honestly have no clue why need to figure it out. Put a note in the code where 
	this hang occurs 
	
	
	Need to add multithreading up here because multithreading is cool
	Also should add a logger
	*/

	public static void main(String[] args) throws IOException{
		ServerSocket serverSock = new ServerSocket(PORT);
		System.out.println("Server running on IP and Port: " + serverSock.toString());

		while(true){
			httpserver(serverSock.accept());	
		}
		//serverSock.close();
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

			//Seems like client connects goes through then goes through again and hangs here
			//idk why need to figure that out 

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

			if(httpRequestType.equals("GET")){

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
				out.println("Content-length"+fileLength);
				out.println();
				out.flush();

				fileOut.write(fileData,0,fileLength);
				fileOut.flush();
				System.out.println("GET Request Returned");
			}
		}	
		//catch(IOException x){System.err.println("IOException: " + x);}
		//catch(NullPointerException y){System.out.println("NullPointerException: "+y);}
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

/*	private static String inputStreamToString(InputStream in){
		Scanner scan = new Scanner(in).useDelimiter("\\A");
		return scan.hasNext() ? scan.next() : "";
	}*/

	//This should support more MIME types in the future
	private static String getContentType(String fileRequested){
		if(fileRequested.endsWith(".html") || fileRequested.endsWith(".htm")){
			return "text/html";
		}
		else{
			return "text/plain"; 
		}
	}
	//String fileType = fileRequested.split("\\.",0);
	//return "text/"+fileType

	//I dont really know how the file input stream works but this 
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

}


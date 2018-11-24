import java.io.*;
import java.net.*;
import java.util.*;

public class Server{
	static final File ROOT = new File(".");
	static final int PORT = 8080; 
	static final String DEFAULT_FILE = "index.html";

	public static void main(String[] args) throws IOException{
		ServerSocket serverSock = new ServerSocket(PORT);
		System.out.println("Server running on IP and Port: " + serverSock.toString());

		while(true){
			httpserver(serverSock.accept());	
			serverSock.close();
		}
	}

	public static void httpserver(Socket connect) throws IOException{
		System.out.println("Connected to Client: "+connect.toString());
		InputStream in = connect.getInputStream();
		PrintWriter out = new PrintWriter(connect.getOutputStream());
		BufferedOutputStream fileOut = new BufferedOutputStream(connect.getOutputStream());
		String fileRequested = null;
		String httpRequestType = null;
		System.out.println("Finish I/O Connections");

		//Program gets caught right here for some reason most of the time need to fix this

		String requestLine = inputStreamToString(in);
		StringTokenizer requestLineTokenizer = new StringTokenizer(requestLine);

		httpRequestType = requestLineTokenizer.nextToken().toUpperCase();
		fileRequested = requestLineTokenizer.nextToken().toLowerCase();
		System.out.println("Request Parsed");

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

			for(byte x : fileData){System.out.print(x+" ");}

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
		in.close();
		out.close();
		fileOut.close();
		connect.close();
	}

	private static String inputStreamToString(InputStream in){
		Scanner scan = new Scanner(in).useDelimiter("\\A");
		return scan.hasNext() ? scan.next() : "";
	}

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


import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Server{
	//static final File ROOT = new File(".");
	static final int PORT = 8080; 
	//static final String DEFAULT_FILE = "index.html";
	//static final String FILE_404 = "404.html";

	/*
	Another error is that client goes throught and reconnects or something and the 
	program hangs trying to request a favicon or something after the I/O connections 
	I honestly have no clue why need to figure it out. Put a note in the code where 
	this hang occurs 
	
	Also should add a logger
	*/

	public static void main(String[] args) throws IOException{
		ServerSocket serverSock = new ServerSocket(PORT);
		System.out.println("Server running on IP and Port: " + serverSock.toString());

		Executor service = Executors.newCachedThreadPool();

		while(true){
			try{

				Socket client = serverSock.accept();
				service.execute(new HttpServer(client));
			}
			catch(SocketTimeoutException x){
				System.out.println("Socket timed out: "+x);
			}	
		}
	}
}


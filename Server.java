import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.*;

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
		//Set up the loggers
		Logger actLog = Logger.getLogger("activity");
		Logger errLog = Logger.getLogger("errors");
		actLog.setLevel(Level.FINER);
		errLog.setLevel(Level.FINER);
		
		//Handler files for log messages
		Handler act = new FileHandler("activity.log");
		Handler err = new FileHandler("error.log");
		act.setLevel(Level.FINER);
		err.setLevel(Level.FINER);

		actLog.addHandler(act);
		errLog.addHandler(err);


		ServerSocket serverSock = new ServerSocket(PORT);
		actLog.finer("Server running on IP and Port: " + serverSock.toString());

		Executor service = Executors.newCachedThreadPool();

		while(true){
			try{
				Socket client = serverSock.accept();
				HttpServer temp = new HttpServer(client,actLog,errLog);
				service.execute(temp);
			}
			catch(SocketTimeoutException x){
				errLog.finer("Socket timed out: "+x);
			}	
		}
	}
}

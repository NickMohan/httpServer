import java.io.*;
import java.net.*;
import java.util.*;
import java.util.Date;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.*;
import javax.net.*;


public class Server{
	private static ArrayList<InetAddress> blacklist = new ArrayList<InetAddress>();
	private static long timeLast;
	private static InetAddress prev;
	static final int PORT = 8080; 

	public static boolean isBlackListed(InetAddress check){
		if(!blacklist.isEmpty()){
			if (blacklist.contains(check)){
				return true;
			}	
		}
     		return false;
   	}

	public static void main(String[] args) throws IOException{
		blacklist.add(InetAddress.getByName("www.myspace.com"));
		Date time = new Date();
 		time.setTime(0);

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

		//Join loggers and handler
		actLog.addHandler(act);
		errLog.addHandler(err);

		//Create a new server socket
		ServerSocket serverSock = new ServerSocket(PORT);


		actLog.finer("Server running on IP and Port: " + serverSock.toString());

		System.out.println("Server running on IP and Port: " + serverSock.toString());
		Executor service = Executors.newCachedThreadPool();

		while(true){
			try{
				Socket client = serverSock.accept();
				if (isBlackListed(client.getLocalAddress())){
    					client.close();
    				}
  				long past = timeLast;
    				timeLast = time.getTime();
    				long currentTime = time.getTime();
    				if ((currentTime < (past+10) ) && ((client.getLocalAddress()) == prev)) {
					blacklist.add(client.getLocalAddress());
					prev = client.getLocalAddress();
    					client.close();
    				}
				else{
    					prev = client.getLocalAddress();
				}	
			
					client.setSoTimeout(10000);
					HttpServer temp = new HttpServer(client,actLog,errLog);
					service.execute(temp);
				
			
			}
			catch(SocketTimeoutException x){
				errLog.finer("Socket timed out: "+x);
			}
			catch(NullPointerException a){
				errLog.finer("NullPointerException: "+a);
			}
		}
	}
	
}

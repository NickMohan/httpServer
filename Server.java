import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.*;
import javax.net.ssl.*;
import javax.net.*;
import java.security.cert.*;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.KeyManagementException;

public class Server{
	static final int PORT = 8080; 

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

		//Join loggers and handler
		actLog.addHandler(act);
		errLog.addHandler(err);

		//Create a new server socket

		//TrustManager[] tm = {new MyX509TrustManager()};

		//SSLContext sslContext = SSLContext.getInstance("SSL");
/*		
TrustManager[] trustAllCerts = new TrustManager[] { 
    new X509TrustManager() {     
        public java.security.cert.X509Certificate[] getAcceptedIssuers() { 
            return new X509Certificate[0];
        } 
        public void checkClientTrusted( 
            java.security.cert.X509Certificate[] certs, String authType) {
            } 
        public void checkServerTrusted( 
            java.security.cert.X509Certificate[] certs, String authType) {
        }
    } 
}; 

// Install the all-trusting trust manager
try {
    SSLContext sc = SSLContext.getInstance("SSL"); 
    sc.init(null, trustAllCerts, new java.security.SecureRandom()); 
    HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
}catch(Exception mid){} 
*/




SSLContext sc=null;//=SSLContext.getInstance("TLS");

try{

sc = SSLContext.getDefault();       //Instance("TLS");

String keyStoreFilename = "mykey.keystore";
char[] storepass = "mypassword".toCharArray();
char[] keypass = "mypassword".toCharArray();
String alias = "alias";
FileInputStream fIn = new FileInputStream(keyStoreFilename);
KeyStore keystore = KeyStore.getInstance("JKS");
keystore.load(fIn, storepass);

Certificate cert = keystore.getCertificate(alias);
System.out.println(cert);

KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
kmf.init(keystore,keypass);

TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
tmf.init(keystore);


//sc = SSLContext.getInstance("SSL"); 
	sc.init(kmf.getKeyManagers(),tmf.getTrustManagers(),null); 
}
catch(NoSuchAlgorithmException e){}
catch(KeyStoreException v){}
catch(CertificateException i){}
catch(IOException p){}
catch(KeyManagementException u){}
catch(UnrecoverableKeyException o){}




















		//sslContext.init(null,tm,null);

		SSLServerSocketFactory factory = (SSLServerSocketFactory) sc.getServerSocketFactory();





		//sslContext.getServerSocketFactoy();

		SSLServerSocket serverSock = (SSLServerSocket) factory.createServerSocket(PORT);

//		System.setProperty("javax.net.ssl.keyStore","za.store");
		//System.setProperty("javax.net.ssl.keyStorePassword","password");


//ServerSocket serverSock = new ServerSocket(PORT);
//		SSLServerSocketFactory factory =(SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
		//SSLServerSocketFactory.getDefault();

		//String[] temp2 = factory.getSupportedCipherSuites();

		//String[] temp3 = factory.getDefaultCipherSuites();

//		SSLServerSocket serverSock =(SSLServerSocket) factory.createServerSocket(PORT);

		//serverSock.setEnabledCipherSuites(temp2);

		//String[] temp3 = serverSock.getSupportedProtocols();

		//serverSock.setEnabledProtocols(temp3);


serverSock.setEnabledCipherSuites(factory.getSupportedCipherSuites());




		//ServerSocket serverSock = new ServerSocket(PORT);
		actLog.finer("Server running on IP and Port: " + serverSock.toString());

		System.out.println("Server running on IP and Port: " + serverSock.toString());
		Executor service = Executors.newCachedThreadPool();

		while(true){
			try{
				SSLSocket client = (SSLSocket) serverSock.accept();
				client.setSoTimeout(10000);
				client.setEnabledCipherSuites(factory.getSupportedCipherSuites());
				HttpServer temp = new HttpServer(client,actLog,errLog);
				service.execute(temp);
			}
			catch(SocketTimeoutException x){
				errLog.finer("Socket timed out: "+x);
			}
			catch(SSLHandshakeException z){
				System.out.println("SSL Handshake: "+z);
			}	
		}
	}
}

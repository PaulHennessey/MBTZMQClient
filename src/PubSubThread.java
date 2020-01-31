
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import com.thetransactioncompany.jsonrpc2.JSONRPC2ParseException;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Notification;

public class PubSubThread implements Runnable
{
    private int localPortNumber;        

    public PubSubThread(int port)
    {
    	this.localPortNumber = port;
    }
    
	@Override
	public void run()
	{
		while(true)
		{
			
	        try 
	        (
        		ZContext context = new ZContext()
	        ) 
	        {
	            ZMQ.Socket subscriber = context.createSocket(SocketType.SUB);
	            subscriber.connect("tcp://localhost:5556");
	            subscriber.subscribe("B".getBytes(ZMQ.CHARSET));

	            while (!Thread.currentThread().isInterrupted()) 
	            {
	                // Read envelope with address
	                String address = subscriber.recvStr();
	                // Read message contents
	                String contents = subscriber.recvStr();
	                System.out.println(address + " : " + contents);
	            }
	        }
/*
	        try 
	        (            
	            ServerSocket serverSocket = new ServerSocket(localPortNumber);
	            Socket pubSubSocket = serverSocket.accept();     
	            BufferedReader in = new BufferedReader(new InputStreamReader(pubSubSocket.getInputStream()));
	        ) 
	        {        	            
	            String input;
	                		
	    		// Read request    		
	            while ((input = in.readLine()) != null) 					
	            {            	            	            	
	            	try 
	            	{            		            		
	            		JSONRPC2Notification notification = JSONRPC2Notification.parse(input);     
	            		System.out.println("Notification: " + notification.toString());
	    			} 
	            	catch (JSONRPC2ParseException e) 
	            	{
	    				System.out.println(e.getMessage());
	    				return;
	    			}            	            
	            }
	        } 
	        catch (IOException e) 
	        {
	            System.out.println(e.getMessage());
	        }*/
		}
	}
}

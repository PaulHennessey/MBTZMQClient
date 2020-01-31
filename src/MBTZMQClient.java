//  Hello World client in Java
//  Connects REQ socket to tcp://localhost:5555
//  Sends "Hello" to server, expects "World" back

import java.io.IOException;

public class MBTZMQClient
{
    public static void main(String[] args) throws IOException 
    {        
        if (args.length != 4) 
        {        	        
            System.err.println("Usage: java MBTJSONRPCClient <local host name>  <local port number> <remote host name> <remote port number>");
            System.exit(1);
        }

        String localHostName = args[0];
        int localPortNumber = Integer.parseInt(args[1]);
        String remoteHostName = args[2];
        int remotePortNumber = Integer.parseInt(args[3]);        

        Thread reqRespThread = new Thread(new ReqRespThread(remoteHostName, remotePortNumber, localHostName, localPortNumber),"ReqRespThread");
        Thread pubSubThread = new Thread(new PubSubThread(localPortNumber),"PubSubThread");
        
        reqRespThread.start();
        pubSubThread.start();       
    }           
}
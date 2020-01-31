import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import com.thetransactioncompany.jsonrpc2.JSONRPC2Request;


public class ReqRespThread implements Runnable
{
    private String remoteHostName;
    private int remotePortNumber;        
    private String localHostName;
    private int localPortNumber;        

    public ReqRespThread(String remoteHost, int remotePort, String localHost, int localPort)
    {
    	this.remoteHostName = remoteHost;
    	this.remotePortNumber = remotePort;
    	this.localHostName = localHost;
    	this.localPortNumber = localPort;
    }
        
	@Override
	public void run()
	{		
        try 
        (
    		ZContext context = new ZContext();
    		BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in))
		) 
        {      	
            ZMQ.Socket socket = context.createSocket(SocketType.REQ);
            socket.connect("tcp://localhost:5555");

        	int reqId = 1;																// The request ID
            String input;
            
 			while ((input = stdIn.readLine()) != null) 									// Keep going until user types ctrl-c
            {            	     
 				String methodName = GetMethodName(input); 				
 				Map<String,Object> params = GetParams(input); 				 			
 				JSONRPC2Request request = new JSONRPC2Request(methodName, params, reqId);            	
 				
        		String requestJSON = request.toString();								// Serialise request to JSON-encoded string
        		System.out.println("Request sent: " + requestJSON);
        		        		
        		long start = System.currentTimeMillis();        		        		
        		socket.send(requestJSON);												// Send to server
 				
        		String reply = socket.recvStr();
        		String result = new JSONObject(reply).getString("result");
        		
      		  	if(methodName.equals("select"))
                {                                               
      		  		System.out.println("Response received: " + reply);
      		  		JSONObject object = new JSONObject(result);
	                JSONArray elements = object.getJSONArray("elements");
	                                
	                for (int i = 0; i < elements.length(); ++i) 
	                {
	                  String element = elements.getString(i);
	                  System.out.println(element);
	                }
	              }
	              else
	              {
	            	  System.out.println("Response received: " + reply);
	              }
                                    
          		long end = System.currentTimeMillis();          		          	
          		System.out.println("Round trip response time = " + (end-start) + " millis");
            } 			
        }
        catch (IOException e) 
        {
            System.err.println(e.getMessage());
            System.exit(1);
        }        
	}
	
	private String GetMethodName(String input)	
	{
    	String[] inputArray = input.trim().split("\\s+");					// Trim and split string on spaces
    	return inputArray[0];    			
	}
	
	private Map<String,Object> GetParams(String input)	
	{
		Map<String,Object> params = new HashMap<String,Object>();
		
		String[] inputArray = input.trim().split("\\s+");					// Trim and split string on spaces
		
	    if (inputArray[0].equals("model.create_element") || inputArray[0].equals("create")) 
	    {		               	
	    	params.put("uri", inputArray[1]); 	 
	    	params.put("content", inputArray[2]);	    	
        }
	    else if (inputArray[0].equals("model.subscribe_to_element") || inputArray[0].equals("subscribe")) 
	    {		               	
	    	params.put("uri", inputArray[1]);
	    	params.put("host", localHostName);
	    	params.put("port", localPortNumber);
        }
	    else if (inputArray[0].equals("model.unsubscribe_from_element") || inputArray[0].equals("unsubscribe")) 
	    {		               	
	    	params.put("uri", inputArray[1]);
	    	params.put("host", localHostName);
	    	params.put("port", localPortNumber);
        }	    
	    else if (inputArray[0].equals("model.update_element") || inputArray[0].equals("update")) 
	    {		               	
	    	params.put("uri", inputArray[1]);
	    	params.put("content", inputArray[2]); 	   
        }
	    else if (inputArray[0].equals("model.move_element") || inputArray[0].equals("move")) 
	    {		               	
	    	params.put("olduri", inputArray[1]);
	    	params.put("newuri", inputArray[2]); 	   
        }
	    else if (inputArray[0].equals("model.select_elements") || inputArray[0].equals("select")) 
	    {		               	
	    	params.put("pattern", inputArray[1]); 	   
        }
	    
	    return params;    			
	}	
	
}

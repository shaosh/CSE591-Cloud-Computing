package VmServerController;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DecimalFormat;
import java.util.Date;

import ServerModel.UploadedFile;
import ServerModel.UploadedFileManager;
import ServerModel.UserInfo;
import ServerModel.UserInfoManager;

public class VmServer {
	public static final int CLIENT_SERVER_PORT = 12346;//Listening device port number  
//	public static final int LISTEN_VM_PORT = 12347;//Listening VM port number  
	public static final int BUFFERSIZE = 8192;
	public static Socket client;
	
    public static void main(String[] args) {  
        System.out.println("Starting vm server...\n");  
        VmServer server = new VmServer();  
        server.init();  
    }  
  
    public void init() {  
        try {  
            ServerSocket serverSocket = new ServerSocket(CLIENT_SERVER_PORT);  
            while (true) {  
                //Client and server are connected.
//                Socket client = serverSocket.accept();  
            	client = serverSocket.accept();
                // Process this connection
                new HandlerThread(client);  
            }  
        } catch (Exception e) {  
            System.out.println("Server exception: " + e.getMessage());  
        }  
    }  
  
    private class HandlerThread implements Runnable {  
        private Socket socket;  
        public HandlerThread(Socket client) {  
            socket = client;  
            new Thread(this).start();  
        }  
  
        public void run() {  
            try {     
                // Read data from client
            	DataInputStream input = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            	DataOutputStream out = new DataOutputStream(socket.getOutputStream());  
            	String clientInputStr = input.readUTF();//Corresponds to the write method in client side, otherwise it will EOFException
                // Process data from client  
                System.out.println("Content sent by client:" + clientInputStr);  
                String[] parsedCommand = parse(clientInputStr);
                String type = parsedCommand[0];
                String s=null;
                System.out.println(type);
                if(type.equals(Command.DOWNLOAD)){
                	String fileName = parsedCommand[1];
                	String path = "/home/ubuntu/VmServer/" + fileName;
            		File file = new File(path); 
                	if(file != null){
                		long length = file.length();
                		out.writeLong(length);
                		out.flush();
                		DataInputStream fis = new DataInputStream(new BufferedInputStream(new FileInputStream(path)));
                		boolean sendSuccess = sendFile(length, fis, out);
                		fis.close();
                		if(sendSuccess){                			
            	            s = Command.DOWNLOAD_SUCCESSFUL;
                		}
                		else{
                			s = Command.DOWNLOAD_FAILED;
                		}
                	}
                }
                else if(type.equals(Command.UPLOAD)){
                	String fileName = parsedCommand[1];     
                	receiveFile(fileName, input);
                	s = Command.UPLOAD_SUCCESSFUL;                	
                } 
                else if(type.equals(Command.COPY)){
                	String sourceIp = parsedCommand[1];
                	String deviceIp = parsedCommand[2];
                	String fileName = parsedCommand[3];
                	out.writeUTF(Command.COPYING);
//                	out.close();
//                	input.close();
//                	socket.close();
                	//Now the server execute a client's function to request the file from the source VM
                	socket = new Socket(sourceIp, CLIENT_SERVER_PORT);
                	input = new DataInputStream(socket.getInputStream());  
                	out = new DataOutputStream(socket.getOutputStream());  
                	out.writeUTF(Command.DOWNLOAD + Command.DELIMITER + fileName);
    	            out.flush();
    	            boolean receiveTag = receiveFile(fileName, input);     
    	            System.out.println("receiveTag: " + receiveTag);
//    	            String ret = input.readUTF();
//    	            System.out.println("ret: " + ret);
    	            if(receiveTag == true){ //&& ret.equals(Command.DOWNLOAD_SUCCESSFUL)){
                    	out.close();
                    	input.close();
                    	socket.close();
                    	//Now the server execute a client's function to send the file to device
//                    	socket = new Socket(deviceIp, CLIENT_SERVER_PORT);
                    	socket = new Socket(deviceIp, 12347);
                    	input = new DataInputStream(socket.getInputStream());  
                    	out = new DataOutputStream(socket.getOutputStream());  
                    	
                    	String path = "/home/ubuntu/VmServer/" + fileName;
                		File file = new File(path); 
                    	if(file.exists()){
                    		out.writeUTF(Command.UPLOAD + Command.DELIMITER + fileName);
            	            out.flush();
                    		long length = file.length();
                    		out.writeLong(length);
                    		out.flush();
                    		DataInputStream fis = new DataInputStream(new BufferedInputStream(new FileInputStream(path)));
                    		boolean sendSuccess = sendFile(length, fis, out);
                    		fis.close();
                    		if(sendSuccess){ 
//                	            s = Command.DOWNLOAD_SUCCESSFUL;
                    			s = "";
                    		}
                    		else{
                    			s = Command.DOWNLOAD_FAILED;
                    		}
                    	}
                    	else{
                    		s = Command.DOWNLOAD_FAILED;
                    	}
                    	
//                    	out.close();
//                    	input.close();
//                    	socket.close();
//                    	socket = client;
//                    	input = new DataInputStream(client.getInputStream());  
//                    	out = new DataOutputStream(client.getOutputStream());  
    	            }
    	            else{
    	            	s = Command.DOWNLOAD_FAILED;
    	            }
//                	Socket nestedSocket = null;
//                	try {
                		//Create a socket and connect to the specific port number in the address
//                		nestedSocket = new Socket(ip, CLIENT_SERVER_PORT); 
                		
//                	}
                }
                else{
                	s = "something";
                }
                
                // Respond to client
                System.out.println("Server: " + s);
                out.writeUTF(s);  
                out.close();  
                input.close(); 
                socket.close();
            } catch (Exception e) {  
                System.out.println("Server run exception: " + e.getMessage());  
            } finally {  
                if (socket != null) {  
                    try { 
                    } 
                    catch (Exception e) {  
                        socket = null;  
                        System.out.println("Server finally exception: " + e.getMessage());  
                    }  
                }  
            } 
        } 
        
        private boolean sendFile(long length, DataInputStream fis, DataOutputStream out) throws IOException{
        	int bufferSize = BUFFERSIZE;
            byte[] buf = new byte[bufferSize];
            long count = 0;
            while(count < length){
            	int read = 0;
            	if(fis != null){
            		read = fis.read(buf);
            	}
            	out.write(buf, 0 , read);
            	count += (long)read;
            }
            System.out.println("Send file length: " + count);
            return true;
        }
        
        private boolean receiveFile(String fileName, DataInputStream input) throws IOException{
        	int bufferSize = BUFFERSIZE;
        	byte[] buf = new byte[bufferSize];
        	int passedlen = 0;
        	long len = 0;
        	len = input.readLong();
        	String savePath = "/home/ubuntu/VmServer/" + fileName; //Path needs to be changed
        	DataOutputStream fileOut = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(savePath)));
        	System.out.println("Receive file length " + len);
            System.out.println("Start to receive file!" + "\n");
            long count = 0;
            while(count < len){
            	int read = 0;
            	if(input != null){
            		read = input.read(buf);
            	}
            	passedlen += read;
            	if(passedlen * 100 / len > ((passedlen - read) * 100 / len))
            		System.out.println("File received " +  (passedlen * 100/ len) + "%");
            	fileOut.write(buf, 0, read);
            	count += (long)read;
            }
        	fileOut.flush();
            fileOut.close(); 	
            return true;
        }
        
        private String[] parse(String command){
        	String[] dataArray = command.split("\\${5}"); 
        	return dataArray;
        }
    }  
}  
package ServerController;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ServerModel.Relation;
import ServerModel.RelationManager;
import ServerModel.UploadedFile;
import ServerModel.UploadedFileManager;
import ServerModel.UserInfo;
import ServerModel.UserInfoManager;

public class Server {
	public static final int PORT = 12344;//Listening port number   
	
    public static void main(String[] args) {  
        System.out.println("Starting server...\n");  
        Server server = new Server();  
        server.init();  
    }  
  
    public void init() {  
        try {  
            ServerSocket serverSocket = new ServerSocket(PORT);  
            while (true) {  
                //Client and server are connected.
                Socket client = serverSocket.accept();  
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
                if(type.equals(Command.LOGIN)){
                	boolean b = UserInfoManager.validateUser(parsedCommand[1], parsedCommand[2]);
                	if(b==true){
                		out.writeUTF(Command.LOGIN_SUCCESSFUL);
                		out.flush();
                		s = Command.LOGIN_SUCCESSFUL;
                	}
                	else {
                		out.writeUTF(Command.LOGIN_FAIL);
                		out.flush();
                		s = Command.LOGIN_FAIL;
                	} 
                }                
                else if(type.equals(Command.UPDATE_USERINFO)){
                	boolean b = UserInfoManager.updateUserInfo(parsedCommand[1], parsedCommand[4], Double.valueOf(parsedCommand[2]), Double.valueOf(parsedCommand[3]));
                	if(b){
                		out.writeUTF(Command.UPDATE_USERINFO_SUCCESSFUL);
                		out.flush();
                		s = Command.UPDATE_USERINFO_SUCCESSFUL;
                	}
                	else{
                		out.writeUTF(Command.UPDATE_USERINFO_FAIL);
                		out.flush();
                		s = Command.UPDATE_USERINFO_FAIL;
                	}                
                }
//                else if(type.equals(Command.GET_SOURCE_INFO)){
//                	UserInfo user = UserInfoManager.queryUser(parsedCommand[1]);
//                	if(user != null){
//	                	double lati = user.getDeviceGPSLati();
//	                	double longi = user.getDeviceGPSLongi();
//	                	String cloudIp = user.getCloudIp();
//	                	String deviceIp = user.getDeviceIp();
//	                	out.writeUTF("" + lati + Command.DELIMITER + longi + Command.DELIMITER + cloudIp + Command.DELIMITER + deviceIp);
//	    	            out.flush();
//	    	            s = Command.GET_SOURCE_INFO_SUCCESSFUL;
//                	}
//                	else{
//                		out.writeUTF(Command.GET_SOURCE_INFO_FAILED);
//                		out.flush();
//                		s = Command.GET_SOURCE_INFO_FAILED;
//                	}
//                }
                else if(type.equals(Command.DOWNLOAD)){
//                	String fileName = parsedCommand[1];
//                	String ownerId = parsedCommand[2];
                	String ownerId = parsedCommand[1];
            		UserInfo user = UserInfoManager.queryUser(ownerId);
                	double lati = Double.valueOf(user.getDeviceGPSLati());
                	double longi = Double.valueOf(user.getDeviceGPSLongi());
                	String cloudIp = user.getCloudIp();
                	String deviceIp = user.getDeviceIp();
                	s = "" + lati + Command.DELIMITER + longi + Command.DELIMITER + cloudIp + Command.DELIMITER + deviceIp;
                }
                else if(type.equals(Command.UPLOAD)){
                	String fileName = parsedCommand[1]; 
                	long len = 0;
                	len = input.readLong();
                	long fileId = UploadedFileManager.addFile(fileName, getFileSize(len));	
                	RelationManager.addRelation(parsedCommand[2], fileId, 1, parsedCommand[2]);
	                RelationManager.addRelation(parsedCommand[3], fileId, 0, parsedCommand[2]);
                	s = Command.DB_UPDATE_SUCCESSFUL + Command.DELIMITER + "The file ID is " + fileId;
                } 
                else if(type.equals(Command.QUERY_ALL_USERID)){
                	String userIdList = UserInfoManager.queryUserList(parsedCommand[1]);
                	s = userIdList;
                }
                else if(type.equals(Command.GET_SHARED_FILELIST)){
                	List <Relation>list = RelationManager.queryFileIdList(parsedCommand[1], 0);
                	s = UploadedFileManager.fetchFileList(list);                	
                }
                else{
                	s = "something";
                }
                
                // Respond to client
                System.out.println("Server: " + s);
                out.writeUTF(s);  
                out.close();  
                input.close();  
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
        
        private String[] parse(String command){
        	String[] dataArray = command.split("\\${5}"); 
        	return dataArray;
        }
        
        private String getFileName(String data){
        	int length = data.length();
        	for(int i = length - 1; i > -1; i--){        		
        		if(data.charAt(i) == '/' || data.charAt(i) == '\\'){
        			return data.substring(i + 1, length);
        		}
        	}
        	return null;
        }
        
//        private String getFileSize(File file){
        private String getFileSize(long length){
    		DecimalFormat df = new DecimalFormat("#.00");
//    		if(file.exists()){
//			double bytes = file.length();
    		double bytes = length;
			double kilobytes = (bytes / 1024);
			double megabytes = (kilobytes / 1024);
			double gigabytes = (megabytes / 1024);
			if(gigabytes >= 1){
				return (df.format(gigabytes) + " " + "GB");
			}
			else if(megabytes >= 1){
				return (df.format(megabytes) + " " + "MB");
			}
			else if(kilobytes >= 1){
				return (df.format(kilobytes) + " " + "KB");
			}
			else{
				return (df.format(bytes) + " " + "Bytes");
			}
//    		}
//    		else{
//    			return "File not exist";
//    		}		
    	}
        
        
    }  
}  
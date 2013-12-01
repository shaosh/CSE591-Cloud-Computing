package com.cc.fileSys.client.function;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import android.os.Environment;
import com.cc.fileSys.client.parameter.Variable;

public class DeviceClient {
	public static final int BUFFERSIZE = 8192;

	public static void main(String[] args) {
		// uploadFile("C:/profile.jpg", "shihuan", "qingyun", "1.1.1.1");
		// uploadFile("C:/A3_ShihuanShao.zip", "sshao1", "2.2.2.2");
		// uploadFile("D:/Program Files/python.msi", "sshao1", "3.3.3.3");
		// uploadFile("c:/FoodItemData.xml", "sshao1", "3.3.3.3");
		// downloadFile("python.msi", "sshao1");
		downloadFile("profile.jpg", "sshao", 31.192609, 121.431577,
				Variable.DBServerIP);
		downloadFile("profile1.jpg", "sshao", 31.192609, 121.431577,
				Variable.DBServerIP);
	}

	public static boolean uploadFile(String path, String ownerId,
			String recipientId, String cloudIp) {
		boolean tag = true;

		System.out.println("Starting Client...");
		System.out
				.println("When receiving \"upload successful\" from server, client will be terminated\n");
		Socket socket = null;
		try {
			// Create a socket and connect to the specific
			// Variable.DBServerPORT number in
			// the address
			socket = new Socket(cloudIp, Variable.vmPort);

			// Read data from the server
			DataInputStream input = new DataInputStream(socket.getInputStream());
			// Send data to server
			DataOutputStream out = new DataOutputStream(
					socket.getOutputStream());
			System.out.println("Uploading file to user VM... \t");

			DataInputStream fis = new DataInputStream(new BufferedInputStream(
					new FileInputStream(path)));
			File file = new File(path);
			long length = file.length();
			// Upload file to user VM
			out.writeUTF(Command.UPLOAD + Command.DELIMITER + file.getName());
			out.flush();
			out.writeLong(length);
			out.flush();

			boolean sendSuccess = sendFile(length, fis, out);
			if (sendSuccess) {
				String ret = input.readUTF();
				fis.close();
				// After upload file to vm, update the DB in server
				if (Command.UPLOAD_SUCCESSFUL.equals(ret)) {
					System.out.println(Command.UPLOAD_SUCCESSFUL);
					System.out.println("Client will be closed");
					Thread.sleep(500);
					out.close();
					input.close();
					socket.close();
					// Update DB in server
					socket = new Socket(Variable.DBServerIP,
							Variable.DBServerPORT);// Variable.DBServerIP =
													// serverIp
					input = new DataInputStream(socket.getInputStream());
					out = new DataOutputStream(socket.getOutputStream());
					System.out.println("Updating DB... \t");
					out.writeUTF(Command.UPLOAD + Command.DELIMITER
							+ file.getName() + Command.DELIMITER + ownerId
							+ Command.DELIMITER + recipientId);
					out.flush();
					out.writeLong(length);
					out.flush();
					ret = input.readUTF();
					String[] parsedRet = parse(ret);
					if (Command.DB_UPDATE_SUCCESSFUL.equals(parsedRet[0])) {
						System.out.println(Command.DB_UPDATE_SUCCESSFUL);
						System.out.println(parsedRet[1]);
						tag = true;
					}
					else {
						tag = false;
						System.out.println(Command.DB_UPDATE_FAILED);
					}
					System.out.println("Client will be closed");
					Thread.sleep(500);
					out.close();
					input.close();
				}
				else {
					tag = false;
					System.out.println(Command.UPLOAD_FAILED);
					System.out.println("Client will be closed");
					Thread.sleep(500);
					out.close();
					input.close();
				}
			}
			else {
				tag = false;
				System.out.println(Command.UPLOAD_FAILED);
				System.out.println("Client will be closed");
				Thread.sleep(500);
				out.close();
				input.close();
				fis.close();
			}
		}
		catch (Exception e) {
			System.out.println("Client exception: " + e.getMessage());
		}
		finally {
			if (socket != null) {
				try {
					socket.close();
				}
				catch (IOException e) {
					socket = null;
					System.out.println("Client finally exception: "
							+ e.getMessage());
					tag = false;
				}
			}
		}
		return tag;
	}

	public static boolean downloadFile(String fileName, String ownerId,
			double selfLati, double selfLongi, String selfCloudIp) {
		boolean tag = false;
		System.out.println("Starting Client...");
		System.out
				.println("When receiving \"download successful\" from server, client will be terminated\n");
		Socket socket = null;
		try {
			// Create a socket and connect to the specific
			// Variable.DBServerPORT number in
			// the address
			socket = new Socket(Variable.DBServerIP, Variable.DBServerPORT);

			// Read data from the server
			DataInputStream input = new DataInputStream(
					new BufferedInputStream(socket.getInputStream()));
			// Send data to server
			DataOutputStream out = new DataOutputStream(
					socket.getOutputStream());
			System.out.println("Downloading file... \t");

			// out.writeUTF(Command.DOWNLOAD + Command.DELIMITER + fileName
			// + Command.DELIMITER + ownerId);
			out.writeUTF(Command.DOWNLOAD + Command.DELIMITER + ownerId);
			out.flush();
			String ret = input.readUTF();
			String[] ips = parse(ret);
			String cloudIp = ips[2];
			String deviceIp = ips[3];
			String ip = selectSource(selfLati, selfLongi, ret);
			System.out.println(ip);
			// *****close the connection to server, open a connection to
			// client server
			out.close();
			input.close();
			socket.close();
			if (ip.equals(deviceIp) && ping(deviceIp)) {
				socket = new Socket(ip, Variable.devicePort);// Variable.DBServerIP
				// =
				// deviceIp
				input = new DataInputStream(socket.getInputStream());
				out = new DataOutputStream(socket.getOutputStream());
				out.writeUTF(Command.DOWNLOAD + Command.DELIMITER + fileName);
				out.flush();
				boolean flag = receiveFile(fileName, input);
				// If receive "download successful" from server, disconnect
				if (flag == true) {
					tag = true;
					System.out.println(Command.DOWNLOAD_SUCCESSFUL);
					System.out.println("Client will be closed");
					Thread.sleep(500);
					out.close();
					input.close();
				}
				else {
					tag = false;
					System.out.println(Command.DOWNLOAD_FAILED);
					System.out.println("Client will be closed");
					Thread.sleep(500);
					out.close();
					input.close();
				}
			}
			else {
				socket = new Socket(Variable.vmIP, Variable.vmPort);
				input = new DataInputStream(socket.getInputStream());
				out = new DataOutputStream(socket.getOutputStream());
				String selfIp = getExterIP.getLocalIP();// ("10.144.21.216");
				out.writeUTF(Command.COPY + Command.DELIMITER + ip
						+ Command.DELIMITER + selfIp + Command.DELIMITER
						+ fileName);
				ret = input.readUTF();
				if (Command.COPYING.equals(ret)) {
					tag = true;
					System.out.println(Command.DOWNLOADING);
					System.out.println("Client will be closed");
					Thread.sleep(500);
					out.close();
					input.close();
				}
				else {
					tag = false;
					System.out.println(Command.DOWNLOAD_FAILED);
					System.out.println("Client will be closed");
					Thread.sleep(500);
					out.close();
					input.close();
				}
			}

			// *****
			// out.writeUTF(Command.DOWNLOAD + Command.DELIMITER +
			// fileName);
			// out.flush();
			// receiveFile(fileName, input);

			// ret = input.readUTF();

			// // If receive "download successful" from server, disconnect
			// if (Command.DOWNLOAD_SUCCESSFUL.equals(ret)) {
			// System.out.println(Command.DOWNLOAD_SUCCESSFUL);
			// System.out.println("Client will be closed");
			// Thread.sleep(500);
			// out.close();
			// input.close();
			// break;
			// }
			// else{
			// tag = false;
			// System.out.println(Command.DOWNLOAD_FAILED);
			// System.out.println("Client will be closed");
			// Thread.sleep(500);
			// out.close();
			// input.close();
			// break;
			// }
		}
		catch (Exception e) {
			System.out.println("Client exception: " + e.getMessage());
		}
		finally {
			if (socket != null) {
				try {
					socket.close();
					// DeviceServer server = new DeviceServer();
					// server.init();
					// System.out.println("Server Started");
				}
				catch (IOException e) {
					socket = null;
					System.out.println("Client finally exception: "
							+ e.getMessage());
					tag = false;
				}
			}
		}
		return tag;
	}

	public static String[] queryFiles(String username) {
		String[] ips = null;
		System.out.println("Starting Client...");
		System.out
				.println("When receiving \"GET_SHARED_FILELIST\" from server, client will be terminated\n");
		Socket socket = null;
		try {
			// Create a socket and connect to the specific
			// Variable.DBServerPORT number in
			// the address
			socket = new Socket(Variable.DBServerIP, Variable.DBServerPORT);

			// Read data from the server
			DataInputStream input = new DataInputStream(socket.getInputStream());
			// Send data to server
			DataOutputStream out = new DataOutputStream(
					socket.getOutputStream());
			System.out.println("get file list.. \t");

			// Upload file to user VM
			out.writeUTF(Command.GET_SHARED_FILELIST + Command.DELIMITER
					+ username);
			out.flush();

			String ret = input.readUTF();
			ips = parse(ret);
		}
		catch (Exception e) {
			System.out.println("Client exception: " + e.getMessage());
		}
		finally {
			if (socket != null) {
				try {
					socket.close();
				}
				catch (IOException e) {
					socket = null;
					System.out.println("Client finally exception: "
							+ e.getMessage());
				}
			}
		}
		return ips;
	}

	public static String[] queryUsers(String username) {
		String[] ips = null;
		System.out.println("Starting Client...");
		System.out
				.println("When receiving \"query all userid\" from server, client will be terminated\n");
		Socket socket = null;
		try {
			// Create a socket and connect to the specific
			// Variable.DBServerPORT number in
			// the address
			socket = new Socket(Variable.DBServerIP, Variable.DBServerPORT);

			// Read data from the server
			DataInputStream input = new DataInputStream(socket.getInputStream());
			// Send data to server
			DataOutputStream out = new DataOutputStream(
					socket.getOutputStream());
			System.out.println("Uploading file to user VM... \t");

			// Upload file to user VM
			out.writeUTF(Command.QUERY_ALL_USERID + Command.DELIMITER
					+ username);
			out.flush();

			String ret = input.readUTF();
			ips = parse(ret);
		}
		catch (Exception e) {
			System.out.println("Client exception: " + e.getMessage());
		}
		finally {
			if (socket != null) {
				try {
					socket.close();
				}
				catch (IOException e) {
					socket = null;
					System.out.println("Client finally exception: "
							+ e.getMessage());
				}
			}
		}
		return ips;
	}

	private static boolean sendFile(long length, DataInputStream fis,
			DataOutputStream out) throws IOException {
		int bufferSize = BUFFERSIZE;
		byte[] buf = new byte[bufferSize];
		long count = 0;
		while (count < length) {
			int read = 0;
			if (fis != null) {
				read = fis.read(buf);
			}
			out.write(buf, 0, read);
			count += (long) read;
		}
		System.out.println("Send file length: " + count);
		return true;
	}

	private static boolean receiveFile(String fileName, DataInputStream input)
			throws IOException {
		int bufferSize = BUFFERSIZE;
		byte[] buf = new byte[bufferSize];
		int passedlen = 0;
		long len = 0;
		len = input.readLong();
		String savePath = Environment.getExternalStorageDirectory() + "/worm/"
				+ fileName; // Path needs to be changed
		DataOutputStream fileOut = new DataOutputStream(
				new BufferedOutputStream(new FileOutputStream(savePath)));
		System.out.println("Receive file length " + len);
		System.out.println("Start to receive file!" + "\n");
		long count = 0;
		while (count < len) {
			int read = 0;
			if (input != null) {
				read = input.read(buf);
			}
			passedlen += read;
			if (passedlen * 100 / len > ((passedlen - read) * 100 / len))
				System.out.println("DisplayFile received "
						+ (passedlen * 100 / len) + "%");
			fileOut.write(buf, 0, read);
			count += (long) read;
		}
		fileOut.flush();
		fileOut.close();
		return true;
	}

	public static boolean login(String userId, String password) {
		boolean tag = false;
		System.out.println("Starting Client...");
		System.out
				.println("When receiving \"login successful\" from server, client will be terminated\n");
		Socket socket = null;
		try {
			// Create a socket and connect to the specific
			// Variable.DBServerPORT number in
			// the address
			socket = new Socket(Variable.DBServerIP, Variable.DBServerPORT);

			// Read data from the server
			DataInputStream input = new DataInputStream(socket.getInputStream());
			// Send data to server
			DataOutputStream out = new DataOutputStream(
					socket.getOutputStream());
			System.out.println("Validating credential... \t");

			out.writeUTF(Command.LOGIN + Command.DELIMITER + userId
					+ Command.DELIMITER + password);
			out.flush();

			String ret = input.readUTF();

			// If receive "login successful" from server, disconnect
			if (Command.LOGIN_SUCCESSFUL.equals(ret)) {
				tag = true;
				System.out.println(Command.LOGIN_SUCCESSFUL);
				System.out.println("Client will be closed");
				Thread.sleep(500);
				out.close();
				input.close();
			}
			else {
				tag = false;
				System.out.println(Command.LOGIN_FAIL);
				System.out.println("Client will be closed");
				Thread.sleep(500);
				out.close();
				input.close();
			}

			// out.close();
			// input.close();
		}
		catch (Exception e) {
			System.out.println("Client exception: " + e.getMessage());
		}
		finally {
			if (socket != null) {
				try {
					socket.close();
				}
				catch (IOException e) {
					socket = null;
					System.out.println("Client finally exception: "
							+ e.getMessage());
					tag = false;
				}
			}
		}
		return tag;
	}

	public static boolean updateInfo(String userId, String deviceGPSLati,
			String deviceGPSLongi, String deviceIp) {
		boolean tag = true;
		System.out.println("Starting Client...");
		System.out
				.println("When receiving \"update userinfo successful\" from server, client will be terminated\n");
		Socket socket = null;
		try {
			// Create a socket and connect to the specific
			// Variable.DBServerPORT number in
			// the address
			socket = new Socket(Variable.DBServerIP, Variable.DBServerPORT);

			// Read data from the server
			DataInputStream input = new DataInputStream(socket.getInputStream());
			// Send data to server
			DataOutputStream out = new DataOutputStream(
					socket.getOutputStream());
			System.out.println("Updating userinfo... \t");

			out.writeUTF(Command.UPDATE_USERINFO + Command.DELIMITER + userId
					+ Command.DELIMITER + deviceGPSLati + Command.DELIMITER
					+ deviceGPSLongi + Command.DELIMITER + deviceIp);
			out.flush();

			String ret = input.readUTF();

			// If receive "login successful" from server, disconnect
			if (Command.UPDATE_USERINFO_SUCCESSFUL.equals(ret)) {
				System.out.println(Command.UPDATE_USERINFO_SUCCESSFUL);
				System.out.println("Client will be closed");
				Thread.sleep(500);
				out.close();
				input.close();
			}
			else {
				tag = false;
				System.out.println(Command.UPDATE_USERINFO_FAIL);
				System.out.println("Client will be closed");
				Thread.sleep(500);
				out.close();
				input.close();
			}
			// out.close();
			// input.close();
		}
		catch (Exception e) {
			System.out.println("Client exception: " + e.getMessage());
		}
		finally {
			if (socket != null) {
				try {
					socket.close();
					return tag;
				}
				catch (IOException e) {
					socket = null;
					System.out.println("Client finally exception: "
							+ e.getMessage());
					return false;
				}
			}
		}
		return false;
	}

	// Fetch owner device location, device ip and cloud ip
	/*
	 * private static String getSourceInfo(String ownerId){ String ret = null;
	 * System.out.println("Starting Client..."); System.out.println(
	 * "When receiving \"get source info successful\" from server, client will be terminated\n"
	 * ); while (true) { Socket socket = null; try { //Create a socket and
	 * connect to the specific Variable.DBServerPORT number in the address
	 * socket = new Socket(Variable.DBServerIP, Variable.DBServerPORT);
	 * 
	 * //Read data from the server DataInputStream input = new
	 * DataInputStream(socket.getInputStream()); //Send data to server
	 * DataOutputStream out = new DataOutputStream(socket.getOutputStream());
	 * System.out.println("Get file owner source info... \t");
	 * 
	 * out.writeUTF(Command.GET_SOURCE_INFO + Command.DELIMITER + ownerId);
	 * out.flush();
	 * 
	 * ret = input.readUTF();
	 * 
	 * // If receive "get source info failed" from server, disconnect if
	 * (Command.GET_SOURCE_INFO_FAILED.equals(ret)) {
	 * System.out.println(Command.GET_SOURCE_INFO_FAILED);
	 * System.out.println("Client will be closed"); Thread.sleep(500);
	 * out.close(); input.close(); break; } else{
	 * System.out.println(Command.GET_SOURCE_INFO_SUCCESSFUL);
	 * System.out.println("Client will be closed"); Thread.sleep(500);
	 * out.close(); input.close(); break; } } catch (Exception e) {
	 * System.out.println("Client exception: " + e.getMessage()); } finally { if
	 * (socket != null) { try { socket.close(); return ret; } catch (IOException
	 * e) { socket = null; System.out.println("Client finally exception: " +
	 * e.getMessage()); return ret; } } } } return ret; }
	 */
	private static String selectSource(double selfLati, double selfLongi,
			String sourceInfo) {
		System.out.println("sourceInfo: " + sourceInfo);
		String[] parsedSourceInfo = parse(sourceInfo);
		double sourceLati = Double.valueOf(parsedSourceInfo[0]);
		double sourceLongi = Double.valueOf(parsedSourceInfo[1]);
		String sourceCloudIp = parsedSourceInfo[2];
		String sourceDeviceIp = parsedSourceInfo[3];
		double distance = CalculateDistance.D_jw(selfLati, selfLongi,
				sourceLati, sourceLongi);
		System.out.println("distance: " + distance);
		if (distance > Command.CRITICAL_DISTANCE) {
			return sourceCloudIp;
		}
		else {
			return sourceDeviceIp;
		}
	}

	private static String[] parse(String command) {
		String[] dataArray = command.split("\\${5}");
		return dataArray;
	}

	private static boolean ping(String host) throws UnknownHostException,
			IOException {
		int timeOut = 3000;
		boolean status = InetAddress.getByName(host).isReachable(timeOut);
		System.out.println(status);
		return status;
	}
}
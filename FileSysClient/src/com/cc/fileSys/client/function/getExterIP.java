package com.cc.fileSys.client.function;


import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;


public class getExterIP {
//	public static void main(String[] args){
//		String ip = null;
//		ip = getLocalIP ();
//		System.out.println(ip);
//	}
	public static String getLocalIP () {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()&&!(inetAddress.getHostAddress().indexOf(":") > -1)) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {
        	System.out.println(ex.toString());
        	return ex.toString();
        }
        return null;
	}
   
}

package com.cc.fileSys.client.UI;


import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

public class getExterIP {
	public static void main(String[] args){
		String ip = null;
		ip = getAndroidLocalIP ();
		System.out.println(ip);
		getCurrentExternalIP();
	}
	public static String getAndroidLocalIP () {
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
    public static String getCurrentExternalIP () {
        String ip = null;  
        try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpGet httpget = new HttpGet("http://ip2country.sourceforge.net/ip2c.php?format=JSON");
                // HttpGet httpget = new HttpGet("http://whatismyip.everdot.org/ip");
                // HttpGet httpget = new HttpGet("http://whatismyip.com.au/");
                // HttpGet httpget = new HttpGet("http://www.whatismyip.org/");
                HttpResponse response;
                
                response = httpclient.execute(httpget);
                
                //Log.i("externalip",response.getStatusLine().toString());
                
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                        long len = entity.getContentLength();
                        if (len != -1 && len < 1024) {
                                String str=EntityUtils.toString(entity);
                                //Log.i("externalip",str);
                                ip=str;
                        } else {
                                ip ="Response too long or error." ;
                                //debug
                                //ip.setText("Response too long or error: "+EntityUtils.toString(entity));
                                //Log.i("externalip",EntityUtils.toString(entity));
                        }            
                } else {
                        ip="Null:"+response.getStatusLine().toString();
                }
            
        }
        catch (Exception e)
        {
        	ip = e.toString();
        }
        
        System.out.println(ip);
        return ip;

    }
    
}

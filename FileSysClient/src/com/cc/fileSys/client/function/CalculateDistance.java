package com.cc.fileSys.client.function;

public class CalculateDistance { 
	private static final double EARTH_RADIUS = 6378.137;  
	private static double rad(double d)  
	{  
		return d * Math.PI / 180.0;  
	}  
	/** 
	* @param lat1 
	* @param lng1 
	* @param lat2 
	* @param lng2 
	* @return 
	*/  
    public static double GetDistance(double lat1, double lng1, double lat2, double lng2)  
    {  
    	double radLat1 = rad(lat1);  
    	double radLat2 = rad(lat2);  
    	double a = radLat1 - radLat2;  
    	double b = rad(lng1) - rad(lng2);  
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a/2),2) +   
        		   Math.cos(radLat1)*Math.cos(radLat2)*Math.pow(Math.sin(b/2),2)));  
        s = s * EARTH_RADIUS;  
        s = Math.round(s * 10000) / 10000;  
        return s;  
    }  
    public static double D_jw(double wd1,double jd1,double wd2,double jd2)  
    {  
        double x,y,out;  
        double PI=3.14159265;  
        double R=6.371229*1e6;  
        x=(jd2-jd1)*PI*R*Math.cos( ((wd1+wd2)/2) *PI/180)/180;  
        y=(wd2-wd1)*PI*R/180;  
        out=Math.hypot(x,y);  
        return out; //return meter, not km
//        return out/1000;  
    }  
  
//    public static void main(String args[]){  
//	     //31.1795100,121.3944810  
//    	 System.out.println(GoogleMap.D_jw(31.192609,121.431577,31.1822952,121.4314814));  
//    }  
}

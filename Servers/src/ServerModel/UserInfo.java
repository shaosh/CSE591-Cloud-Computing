package ServerModel;

public class UserInfo {
	private String userId;
	private String deviceIp;
	private String cloudIp;
	private String deviceGPSLati;
	private String deviceGPSLongi;	
	private String password;
	
	public UserInfo(){}
	
	public UserInfo(String userId, String password){
		this.userId = new String(userId);
		this.password = new String(password);
		this.deviceIp = "";
		this.cloudIp = "";
		this.deviceGPSLati = "";
		this.deviceGPSLongi = "";
	}
	
	public UserInfo(String userId, String deviceIp, String cloudIp, String deviceGPSLati, String deviceGPSLongi, String password){
		this.userId = new String(userId);
		this.password = new String(password);
		this.deviceIp = new String(deviceIp);
		this.cloudIp = new String(cloudIp);
		this.deviceGPSLati = deviceGPSLati;
		this.deviceGPSLongi = deviceGPSLongi;
	}
	
	public void setUserId(String userId){
		this.userId = new String(userId);
	}
	
	public void setPassword(String password){
		this.password = new String(password);
	}
	
	public void setDeviceIp(String deviceIp){
		this.deviceIp = new String(deviceIp);
	}
	
	public void setDeviceGPSLati(String deviceGPSLati){
		this.deviceGPSLati = deviceGPSLati;
	}
	
	public void setDeviceGPSLongi(String deviceGPSLongi){
		this.deviceGPSLongi = deviceGPSLongi;
	}
	
	public String getUserId(){
		return userId;
	}
	
	public String getPassword(){
		return password;
	}
	
	public String getDeviceIp(){
		return deviceIp;
	}
	
	public String getDeviceGPSLati(){
		return deviceGPSLati;
	}
	
	public String getDeviceGPSLongi(){
		return deviceGPSLongi;
	}

	public String getCloudIp() {
		return cloudIp;
	}

	public void setCloudIp(String cloudIp) {
		this.cloudIp = cloudIp;
	}
}

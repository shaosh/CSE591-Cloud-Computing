package ServerModel;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import ServerController.Command;

public class UserInfoManager {
	private static Session session;
	
	private static void createSession(){
		session = SessionFactoryUtil.getSessionFactory().openSession();
        session.beginTransaction();
	}
	
	public static void addUser(String userId, String deviceIp, String cloudIp, double deviceGPSLati, double deviceGPSLongi, String password){
		UserInfo userInfo = new UserInfo();
		userInfo.setUserId(userId);
		userInfo.setPassword(password);
		userInfo.setDeviceGPSLati(String.valueOf(deviceGPSLati));
		userInfo.setDeviceGPSLongi(String.valueOf(deviceGPSLongi));
		userInfo.setCloudIp(cloudIp);
		userInfo.setDeviceIp(deviceIp);
		createSession();
		session.save(userInfo);
		session.getTransaction().commit();
		session.close();
		return;
	}
	
	public static UserInfo queryUser(String userId){
		createSession();
		String hql = "from UserInfo as u where u.userId=:userId";
		Query query = session.createQuery(hql);
		query.setString("userId", userId);
		List <UserInfo>list = query.list();
		UserInfo userInfo = null;
		java.util.Iterator<UserInfo> iter = list.iterator();
		while (iter.hasNext()) {
			userInfo = iter.next();
		}					
		session.getTransaction().commit();
		session.close();
		return userInfo;	
	}
	
	public static boolean checkUniqueID(String userId){
		if(queryUser(userId) == null){
			return true;
		}
		else{
			return false;
		}
	}
	
	public static boolean validateUser(String userId, String password){
		createSession();
		String hql = "from UserInfo as u where u.userId=:userId and u.password=:password";
		Query query = session.createQuery(hql);
		query.setString("userId", userId);
		query.setString("password", password);
		List <UserInfo>list = query.list();
		UserInfo userInfo = null;
		java.util.Iterator<UserInfo> iter = list.iterator();
		while (iter.hasNext()) {
			userInfo = iter.next();
		}					
		session.getTransaction().commit();
		session.close();
		if(userInfo == null){
			return false;
		}
		else{
			return true;
		}
	}
	
	
	public static boolean updateUserInfo(String userId, String deviceIp, double deviceGPSLati, double deviceGPSLongi)
	{
		boolean b = true;
		if(userId.isEmpty() || userId ==null){
			
			b=false;
		}
		if(deviceIp.isEmpty()||deviceIp==null){
			b=false;
			
		}
		createSession();
		UserInfo user = (UserInfo) session.get(UserInfo.class, userId); 
		user.setDeviceIp(deviceIp);
		user.setDeviceGPSLati(String.valueOf(deviceGPSLati));
		user.setDeviceGPSLongi(String.valueOf(deviceGPSLongi));
		session.getTransaction().commit();
		session.close();
		return b;
		
	}
	
	public static String queryUserList(String userId){
		createSession();
		String hql = "from UserInfo";
		Query query = session.createQuery(hql);
		List <UserInfo>list = query.list();
		session.getTransaction().commit();
		session.close();
		String str = "";
		ArrayList<String> userIdList = new ArrayList<String>();
		java.util.Iterator<UserInfo> iter = list.iterator();
		while(iter.hasNext()){
			String recipientId = iter.next().getUserId();
//			userIdList.add(iter.next().getUserId());
			if(userId.equals(recipientId) != true){
				if(str.equals("")){
					str = recipientId;
				}
				else{
					str = str.concat(Command.DELIMITER + recipientId);
				}
			}				
		}
		return str;
//		return userIdList;		
	}

}

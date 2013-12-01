package ServerModel;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

public class RelationManager {
	private static Session session;
	
	private static void createSession(){
		session = SessionFactoryUtil.getSessionFactory().openSession();
        session.beginTransaction();
	}
	
	public static void addRelation(String target, long fileId, int isOwner, String source){
		Relation relation = new Relation();
		relation.setFileId(fileId);
		relation.setTarget(target);
		relation.setIsOwner(isOwner);
		relation.setSource(source);
		createSession();
		session.save(relation);
		session.getTransaction().commit();
		session.close();
		return;
	}
	
//	private static String[] queryFileIdList(String recipientId){
	public static List queryFileIdList(String target, int isOwner){
		createSession();
		String hql = "from Relation as r where r.target=:target and r.isOwner=:isOwner";
		Query query = session.createQuery(hql);
		query.setString("target", target);
		query.setInteger("isOwner", isOwner);
		List <Relation>list = query.list();
//		Relation relation = null;
//		java.util.Iterator<Relation> iter = list.iterator();
//		while (iter.hasNext()) {
//			relation = iter.next();
//		}					
		session.getTransaction().commit();
		session.close();
		return list;
//		return file;	
	}
	
	public static void updateOwnership(String target, long fileId, boolean isOwner){
		createSession();
		String hql = "update Relation as r set r.isOwner=:isOwner where target=:target and fileId=:fileId";
		Query query = session.createQuery(hql);
		query.setBoolean("isOwner", isOwner);
		query.setString("target", target);
		query.setLong("fileId", fileId); 
		query.executeUpdate(); 		
		session.getTransaction().commit();
		session.close();
	}
}

